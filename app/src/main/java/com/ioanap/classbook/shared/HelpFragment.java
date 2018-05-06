package com.ioanap.classbook.shared;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.ioanap.classbook.R;
import com.ioanap.classbook.utils.FAQExpandableListAdapter;

/**
 * Created by ioana on 2/23/2018.
 */

public class HelpFragment extends Fragment {

    private static final String TAG = "HelpFragment";

    // widgets
    ExpandableListView mQuestionsList;

    // variables
    FAQExpandableListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mQuestionsList = view.findViewById(R.id.list_questions);

        mAdapter = new FAQExpandableListAdapter(getContext());
        mQuestionsList.setAdapter(mAdapter);

        // arrow bounds
        mQuestionsList.setIndicatorBounds(GetPixelFromDips(0), GetPixelFromDips(50));
    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

}
