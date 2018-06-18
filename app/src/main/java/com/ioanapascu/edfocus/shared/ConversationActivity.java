package com.ioanapascu.edfocus.shared;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ioanapascu.edfocus.BaseActivity;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.Message;
import com.ioanapascu.edfocus.model.UserAccountSettings;
import com.ioanapascu.edfocus.utils.MessagesListAdapter;
import com.ioanapascu.edfocus.utils.UniversalImageLoader;
import com.ioanapascu.edfocus.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationActivity extends BaseActivity {

    // pagination
    private static final int ITEMS_TO_LOAD = 8;
    // variables
    private static String FIRST_ROW = "Pull to load more messages...";
    private String userId;
    private List<Message> mMessages;
    private List<String> mFirstRow;
    private MessagesListAdapter mAdapter;
    private int mItemPosition = 0;
    private String mLastKey = "";
    private String mPreviousKey = "";

    // widgets
    private TextView mNameTxt, mLastSeenTxt;
    private CircleImageView mProfilePhoto;
    private ImageView mAddBtn, mSendBtn;
    private EditText mMessageText;
    private RecyclerView mMessagesRecycler;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(this, false);
        setContentView(R.layout.activity_conversation);

        userId = getIntent().getStringExtra("userId");
        mMessages = new ArrayList<>();
        mFirstRow = Arrays.asList(FIRST_ROW);

        mNameTxt = findViewById(R.id.txt_name);
        mLastSeenTxt = findViewById(R.id.txt_last_seen);
        mProfilePhoto = findViewById(R.id.profile_photo);
        mSendBtn = findViewById(R.id.btn_send);
        mMessageText = findViewById(R.id.txt_message);
        mMessagesRecycler = findViewById(R.id.recycler_messages);
        mSwipeRefreshLayout = findViewById(R.id.layout_swipe);

        mAdapter = new MessagesListAdapter(this, mFirstRow,
                mMessages, CURRENT_USER_ID);
        mMessagesRecycler.setAdapter(mAdapter);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mMessagesRecycler.setLayoutManager(mLinearLayoutManager);

        showUserInfo();
        showMessages();

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mItemPosition = 0;
                showMoreMessages();
            }
        });
    }

    private void showMoreMessages() {
        Query query = mMessagesRef.child(CURRENT_USER_ID).child(userId).orderByKey().endAt(mLastKey).limitToLast(ITEMS_TO_LOAD + 1);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                String messageKey = dataSnapshot.getKey();

                if (!mPreviousKey.equals(messageKey)) { // do not repeat the last message from page on the next page
                    // place the older message at the beginning of the list
                    mMessages.add(mItemPosition++, message);
                    mFirstRow.set(0, FIRST_ROW);
                    mAdapter.notifyDataSetChanged();
                } else {
                    mPreviousKey = mLastKey;
                }

                if (mItemPosition == 0 && messageKey.equals(mPreviousKey)) {
                    mFirstRow.set(0, "");
                    mAdapter.notifyDataSetChanged();
                }

                if (mItemPosition == 1) {
                    mLastKey = messageKey;
                }

                mSwipeRefreshLayout.setRefreshing(false);

                //mLinearLayoutManager.scrollToPositionWithOffset(ITEMS_TO_LOAD, 0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showMessages() {
        mMessages.clear();
        Query query = mMessagesRef.child(CURRENT_USER_ID).child(userId).limitToLast(ITEMS_TO_LOAD);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);

                mItemPosition++;

                if (mItemPosition == 1) {
                    String messageKey = dataSnapshot.getKey();
                    mLastKey = messageKey;
                    mPreviousKey = messageKey;
                }

                mMessages.add(message);
                mAdapter.notifyDataSetChanged();

                mMessagesRecycler.scrollToPosition(mMessages.size() - 1);

                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String messageText = mMessageText.getText().toString();

        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        Message message = new Message(messageText, false, "text", System.currentTimeMillis(),
                CURRENT_USER_ID);

        mMessagesRef.child(CURRENT_USER_ID).child(userId).push().setValue(message);
        mMessagesRef.child(userId).child(CURRENT_USER_ID).push().setValue(message);

        // set as last message of the conversations (used for for conversations list)
        mConversationsRef.child(CURRENT_USER_ID).child(userId).setValue(message);
        mConversationsRef.child(userId).child(CURRENT_USER_ID).setValue(message);

        mMessageText.setText("");

    }

    private void showUserInfo() {
        mUserAccountSettingsRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserAccountSettings settings = dataSnapshot.getValue(UserAccountSettings.class);

                if (settings != null) {
                    mNameTxt.setText(settings.getFirstName() + " " + settings.getLastName());
                    UniversalImageLoader.setImage(settings.getProfilePhoto(), mProfilePhoto, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mOnlineUsersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mLastSeenTxt.setText("Online");
                } else {
                    // get last seen
                    mLastSeenRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                long dateInMillis = (long) dataSnapshot.getValue();
                                mLastSeenTxt.setText(String.format("Last seen:%s", Utils.formatLastSeenDate(dateInMillis)));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
