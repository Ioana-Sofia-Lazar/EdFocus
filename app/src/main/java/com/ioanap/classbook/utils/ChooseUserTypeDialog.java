package com.ioanap.classbook.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ioanap.classbook.R;

public class ChooseUserTypeDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "ChooseUserTypeDialog";

    // variables
    private final float ALPHA = (float) 0.2; // opacity of user types that are not selected
    private String mSelectedUserType;

    // widgets
    Button mContinueButton;

    // choose user type included layout
    View mChooseUserTypeView;
    LinearLayout mChooseTeacherLayout, mChoosePupilLayout, mChooseParentLayout;
    ImageView mChooseTeacherImg, mChoosePupilImg, mChooseParentImg;
    TextView mChooseTeacherText, mChoosePupilText, mChooseParentText;

    public interface OnUserTypeSelectedListener {
        void getUserType(String type);
    }

    private OnUserTypeSelectedListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_choose_user_type, container, false);

        // widgets
        mContinueButton = (Button) view.findViewById(R.id.btn_continue);

        // included layout
        mChooseUserTypeView = view.findViewById(R.id.choose_user_type);

        mChooseTeacherLayout = (LinearLayout) mChooseUserTypeView.findViewById(R.id.linear_layout_choose_teacher);
        mChooseTeacherImg = (ImageView) mChooseUserTypeView.findViewById(R.id.img_choose_teacher);
        mChooseTeacherText = (TextView) mChooseUserTypeView.findViewById(R.id.text_choose_teacher);

        mChoosePupilLayout = (LinearLayout) mChooseUserTypeView.findViewById(R.id.linear_layout_choose_pupil);
        mChoosePupilImg = (ImageView) mChooseUserTypeView.findViewById(R.id.img_choose_pupil);
        mChoosePupilText = (TextView) mChooseUserTypeView.findViewById(R.id.text_choose_pupil);

        mChooseParentLayout = (LinearLayout) mChooseUserTypeView.findViewById(R.id.linear_layout_choose_parent);
        mChooseParentImg = (ImageView) mChooseUserTypeView.findViewById(R.id.img_choose_parent);
        mChooseParentText = (TextView) mChooseUserTypeView.findViewById(R.id.text_choose_parent);

        // listeners
        mContinueButton.setOnClickListener(this);
        mChooseTeacherLayout.setOnClickListener(this);
        mChoosePupilLayout.setOnClickListener(this);
        mChooseParentLayout.setOnClickListener(this);

        // default user type selected is teacher
        mSelectedUserType = "teacher";
        setAlphasUserTypes((float) 1, ALPHA, ALPHA);

        return view;
    }

    /**
     * Called when a fragment is first attached to its context.
     */
    public void onAttach(Context context) {
        try {
            mListener = (OnUserTypeSelectedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("must implement OnUserTypeSelectedListener");
        }
        super.onAttach(context);
    }

    @Override
    public void onClick(View view) {
        if (view == mContinueButton) {
            // send selected user type to fragment and dismiss dialog
            mListener.getUserType(mSelectedUserType);
            getDialog().dismiss();
        }
        if (view == mChooseTeacherLayout) {
            // user chose Teacher type
            setAlphasUserTypes((float) 1, ALPHA, ALPHA);
            mSelectedUserType = "teacher";
        }
        if (view == mChoosePupilLayout) {
            // user chose Pupil type
            setAlphasUserTypes(ALPHA, (float) 1, ALPHA);
            mSelectedUserType = "child";
        }
        if (view == mChooseParentLayout) {
            // user chose Parent type
            setAlphasUserTypes(ALPHA, ALPHA, (float) 1);
            mSelectedUserType = "parent";
        }
    }

    /**
     * Sets alphas for user type images and text. (If we select one of the 3 options, the other 2
     * options will become less visible)
     *
     * @param teacher
     * @param pupil
     * @param parent
     */
    private void setAlphasUserTypes(float teacher, float pupil, float parent) {
        mChooseTeacherImg.setAlpha(teacher);
        mChooseTeacherText.setAlpha(teacher);
        mChoosePupilImg.setAlpha(pupil);
        mChoosePupilText.setAlpha(pupil);
        mChooseParentImg.setAlpha(parent);
        mChooseParentText.setAlpha(parent);
    }


}
