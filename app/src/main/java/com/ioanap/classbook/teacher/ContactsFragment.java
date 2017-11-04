package com.ioanap.classbook.teacher;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Contact;
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
public class ContactsFragment extends Fragment {

    private static final String TAG = "ContactsFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mContactsRecyclerView;

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
    }

    private void testList() {
        //Create the Person objects
        Contact john = new Contact("John","12-20-1998","http://recruitstaffonline.com/wp-content/uploads/2013/05/small-business.jpg");
        Contact steve = new Contact("Steve","08-03-1987","http://recruitstaffonline.com/wp-content/uploads/2013/05/small-business.jpg");
        Contact stacy = new Contact("Stacy","11-15-2000","http://recruitstaffonline.com/wp-content/uploads/2013/05/small-business.jpg");
        Contact ashley = new Contact("Ashley","07-02-1999","http://recruitstaffonline.com/wp-content/uploads/2013/05/small-business.jpg");
        Contact matt = new Contact("Matt","03-29-2001","http://recruitstaffonline.com/wp-content/uploads/2013/05/small-business.jpg");
        Contact matt2 = new Contact("Matt2","03-29-2001","http://recruitstaffonline.com/wp-content/uploads/2013/05/small-business.jpg");
        Contact matt3 = new Contact("Matt3","03-29-2001","http://recruitstaffonline.com/wp-content/uploads/2013/05/small-business.jpg");
        Contact matt4 = new Contact("Matt4","03-29-2001","http://recruitstaffonline.com/wp-content/uploads/2013/05/small-business.jpg");
        Contact matt5 = new Contact("Matt5","03-29-2001","http://recruitstaffonline.com/wp-content/uploads/2013/05/small-business.jpg");
        Contact matt6 = new Contact("Matt6","03-29-2001","http://recruitstaffonline.com/wp-content/uploads/2013/05/small-business.jpg");
        Contact matt7 = new Contact("Matt7","03-29-2001","http://recruitstaffonline.com/wp-content/uploads/2013/05/small-business.jpg");
        Contact matt8 = new Contact("Matt8","03-29-2001","http://recruitstaffonline.com/wp-content/uploads/2013/05/small-business.jpg");
        Contact matt9 = new Contact("Matt9","03-29-2001","http://recruitstaffonline.com/wp-content/uploads/2013/05/small-business.jpg");
        Contact matt10 = new Contact("Matt10","03-29-2001","http://recruitstaffonline.com/wp-content/uploads/2013/05/small-business.jpg");
        Contact matt11 = new Contact("Matt11","03-29-2001","http://recruitstaffonline.com/wp-content/uploads/2013/05/small-business.jpg");

        //Add the Person objects to an ArrayList
        ArrayList<Contact> peopleList = new ArrayList<>();
        peopleList.add(john);
        peopleList.add(steve);
        peopleList.add(stacy);
        peopleList.add(ashley);
        peopleList.add(matt);
        peopleList.add(matt2);
        peopleList.add(matt3);
        peopleList.add(matt4);
        peopleList.add(matt5);
        peopleList.add(matt6);
        peopleList.add(matt7);
        peopleList.add(matt8);
        peopleList.add(matt9);
        peopleList.add(matt10);
        peopleList.add(matt11);

        ContactsListAdapter adapter = new ContactsListAdapter(getContext(), peopleList);
        mContactsRecyclerView.setAdapter(adapter);
        mContactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContactsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_contacts);
        testList();

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
