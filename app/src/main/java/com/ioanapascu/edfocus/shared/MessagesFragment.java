package com.ioanapascu.edfocus.shared;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.firebase.FirebaseUtils;
import com.ioanapascu.edfocus.model.Contact;
import com.ioanapascu.edfocus.model.Conversation;
import com.ioanapascu.edfocus.model.Message;
import com.ioanapascu.edfocus.model.UserAccountSettings;
import com.ioanapascu.edfocus.utils.ConversationsListAdapter;
import com.ioanapascu.edfocus.utils.MessageContactsListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ioana on 2/23/2018.
 */

public class MessagesFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MessagesFragment";

    // widgets
    RecyclerView mConversationsRecycler;
    FloatingActionButton mNewMessageFab;

    // variables
    ConversationsListAdapter mAdapter;
    List<Conversation> mConversations;
    List<Contact> mContacts;
    FirebaseUtils firebase;
    MessageContactsListAdapter mContactsAdapter;
    Dialog newMessageDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebase = new FirebaseUtils();

        mConversationsRecycler = view.findViewById(R.id.recycler_conversations);
        mNewMessageFab = view.findViewById(R.id.fab_new_message);

        mConversations = new ArrayList<>();
        mContacts = new ArrayList<>();

        // conversations
        mAdapter = new ConversationsListAdapter(getContext(), mConversations, firebase.getCurrentUserId());
        mConversationsRecycler.setAdapter(mAdapter);
        mConversationsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mConversationsRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        // contacts
        mContactsAdapter = new MessageContactsListAdapter(getContext(), R.layout.row_message_contact, mContacts);

        mNewMessageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewMessageDialog();
            }
        });

        displayConversations();
        retrieveContacts();
    }

    private void retrieveContacts() {
        firebase.mContactsRef.child(firebase.getCurrentUserId()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mContacts.clear();

                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String contactId = data.getValue(String.class);

                            // for this contact (user) id get info to display in the contacts list
                            showContactData(contactId);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    private void showContactData(final String id) {
        firebase.mUserAccountSettingsRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserAccountSettings settings = dataSnapshot.getValue(UserAccountSettings.class);

                Contact contact = new Contact();
                contact.setId(id);
                contact.setName(settings.getFirstName() + " " + settings.getLastName());
                contact.setEmail(settings.getEmail());
                contact.setProfilePhoto(settings.getProfilePhoto());
                contact.setUserType(settings.getUserType());

                mContacts.add(contact);
                mContactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showNewMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose a contact");

        ListView listview = new ListView(getContext());
        listview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        listview.setAdapter(mContactsAdapter);
        builder.setView(listview);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = mContacts.get(position);

                Intent intent = new Intent(getContext(), ConversationActivity.class);
                intent.putExtra("userId", contact.getId());
                startActivity(intent);

                newMessageDialog.dismiss();
            }
        });

        newMessageDialog = builder.create();
        newMessageDialog.show();
    }

    private void displayConversations() {
        firebase.mConversationsRef.child(firebase.getCurrentUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mConversations.clear();
                mAdapter.notifyDataSetChanged();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    final Message message = data.getValue(Message.class);
                    String userId = data.getKey();

                    firebase.mUserAccountSettingsRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserAccountSettings settings = dataSnapshot.getValue(UserAccountSettings.class);
                            Conversation conversation = new Conversation();

                            conversation.setLastMessage(message.getMessage());
                            conversation.setFrom(message.getFrom());
                            conversation.setUserId(settings.getId());
                            conversation.setUserName(settings.getFirstName() + " " + settings.getLastName());
                            conversation.setUserPhoto(settings.getProfilePhoto());
                            conversation.setSeen(message.isSeen());
                            conversation.setLastMessageDate(message.getTime());

                            mConversations.add(conversation);
                            mAdapter.notifyDataSetChanged();
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

    @Override
    public void onClick(View view) {
    }
}
