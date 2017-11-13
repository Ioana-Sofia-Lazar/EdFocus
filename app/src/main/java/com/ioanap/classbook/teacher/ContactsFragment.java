package com.ioanap.classbook.teacher;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Contact;
import com.ioanap.classbook.model.UserAccountSettings;
import com.ioanap.classbook.utils.ContactsListAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ContactsFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // widgets
    private RecyclerView mContactsRecyclerView;
    private FloatingActionButton mFabAddContact;
    private EditText mSearchEditText;

    // variables
    private ArrayList<Contact> mContacts;
    private OnFragmentInteractionListener mListener;
    private ContactsListAdapter mContactsListAdapter;

    // db reference
    private DatabaseReference mContactsRef, mSettingsRef;

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mContactsRef = FirebaseDatabase.getInstance().getReference().child("contacts");
        mSettingsRef = FirebaseDatabase.getInstance().getReference().child("user_account_settings");
    }

    /**
     * Display contacts for the current user
     */
    private void displayContacts() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        ((BaseActivity) getActivity()).showProgressDialog("");
        mContactsRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
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
        });

        ((BaseActivity) getActivity()).hideProgressDialog();
    }

    /**
     * Get info to display in the contacts list for the contact with given id.
     *
     * @param id
     */
    private void showContactData(final String id) {
        mSettingsRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserAccountSettings settings = dataSnapshot.getValue(UserAccountSettings.class);
                Log.d(TAG, "getcontactdata : " + settings.toString());

                Contact contact = new Contact();
                contact.setId(id);
                contact.setName(settings.getDisplayName());
                contact.setEmail(settings.getEmail());
                contact.setProfilePhoto(settings.getProfilePhoto());
                contact.setUserType(settings.getUserType());

                mContacts.add(contact); Log.d(TAG, "contact : " + contact.toString());
                mContactsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContacts = new ArrayList<>();

        mFabAddContact = (FloatingActionButton) view.findViewById(R.id.fab_add_contact);
        mContactsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_contacts);
        mSearchEditText = (EditText) view.findViewById(R.id.edit_text_search_contact);

        mContactsListAdapter = new ContactsListAdapter(getContext(), mContacts);
        mContactsRecyclerView.setAdapter(mContactsListAdapter);
        mContactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mFabAddContact.setOnClickListener(this);

        displayContacts();

        // filter contacts according to text that user enters
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // filter list according to user input
                filterContactsList(s.toString());
            }
        });
    }

    void filterContactsList(String text){
        ArrayList<Contact> temp = new ArrayList();
        text = text.toLowerCase();

        for(Contact contact: mContacts){
            if(contact.getName().toLowerCase().contains(text) || contact.getEmail().contains(text)){
                temp.add(contact);
            }
        }

        //update recycler view
        mContactsListAdapter.updateList(temp);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (view == mFabAddContact) {
            // jump to search activity
            startActivity(new Intent(getActivity(), SearchActivity.class));
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
