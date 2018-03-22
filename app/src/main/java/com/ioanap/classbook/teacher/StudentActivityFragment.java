package com.ioanap.classbook.teacher;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Course;
import com.ioanap.classbook.model.Grade;
import com.ioanap.classbook.model.GradeDb;
import com.ioanap.classbook.utils.StudentGradesStickyAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by ioana on 3/15/2018.
 */
public class StudentActivityFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String STUDENT_ID = "STUDENT_ID";
    public static final String CLASS_ID = "CLASS_ID";
    HashMap<String, Long> mHeaderIds;
    StudentGradesStickyAdapter mAdapter;
    // widgets
    private RelativeLayout mNoGradesLayout;
    // variables
    private int mPageIndex; // can be 0 (Grades Page) or 1(Absences Page)
    private String mClassId, mStudentId;
    private ArrayList<Grade> mGrades;
    private DatabaseReference mStudentGradesRef, mClassCoursesRef;

    public static StudentActivityFragment newInstance(int page, String studentId, String classId) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(STUDENT_ID, studentId);
        args.putString(CLASS_ID, classId);
        StudentActivityFragment fragment = new StudentActivityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = getArguments().getInt(ARG_PAGE);
        mClassId = getArguments().getString(CLASS_ID);
        mStudentId = getArguments().getString(STUDENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_activity, container, false);

        // widgets
        mNoGradesLayout = view.findViewById(R.id.layout_no_grades);

        mStudentGradesRef = FirebaseDatabase.getInstance().getReference().child("studentGrades");
        mClassCoursesRef = FirebaseDatabase.getInstance().getReference().child("classCourses");
        mGrades = new ArrayList<>();
        mHeaderIds = new HashMap<>();

        if (mPageIndex == 0) {
            // display grades
            StickyListHeadersListView stickyList = view.findViewById(R.id.list_grades);
            mAdapter = new StudentGradesStickyAdapter(getContext(),
                    R.layout.row_grade, R.layout.row_header, mGrades, mHeaderIds);
            stickyList.setAdapter(mAdapter);
            getGrades();
        } else {
            // display absences
            // todo
        }

        return view;
    }

    // returns a list of grades
    private void getGrades() {
        // retrieve schedule from firebase for the currently selected day, sorted by starting time
        mStudentGradesRef.child(mClassId).child(mStudentId).orderByChild("courseId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mGrades.clear();

                if (dataSnapshot.getChildrenCount() > 0) {
                    mNoGradesLayout.setVisibility(View.GONE);

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        final GradeDb gradeDb = data.getValue(GradeDb.class);

                        // retrieve course info
                        mClassCoursesRef.child(mClassId).child(gradeDb.getCourseId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Course course = dataSnapshot.getValue(Course.class);

                                mGrades.add(new Grade(gradeDb, course.getName()));
                                addToHashMap(course.getId());
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addToHashMap(String courseId) {
        // if this course id already exists return
        if (mHeaderIds.containsKey(courseId)) {
            return;
        }

        // insert this courseId with value of bigget id + 1
        // get biggest id
        long maxId = -1;
        for (Map.Entry<String, Long> entry : mHeaderIds.entrySet()) {
            if (entry.getValue() > maxId) {
                maxId = entry.getValue();
            }
        }

        mHeaderIds.put(courseId, maxId + 1);

    }

    @Override
    public void onClick(View view) {

    }
}