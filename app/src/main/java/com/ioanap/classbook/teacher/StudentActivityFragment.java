package com.ioanap.classbook.teacher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Course;
import com.ioanap.classbook.model.Grade;
import com.ioanap.classbook.model.GradeDb;
import com.ioanap.classbook.utils.GradeCell;
import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleRecyclerView;
import com.jaychang.srv.decoration.SectionHeaderProvider;
import com.jaychang.srv.decoration.SimpleSectionHeaderProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ioana on 3/15/2018.
 */
public class StudentActivityFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String STUDENT_ID = "STUDENT_ID";
    public static final String CLASS_ID = "CLASS_ID";

    // widgets
    private SimpleRecyclerView mGradesRecycler;

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

        mGradesRecycler = view.findViewById(R.id.recycler);

        mStudentGradesRef = FirebaseDatabase.getInstance().getReference().child("studentGrades");
        mClassCoursesRef = FirebaseDatabase.getInstance().getReference().child("classCourses");
        mGrades = new ArrayList<>();

        if (mPageIndex == 0) {
            // display grades
            // todo get g=from database
            addRecyclerGradesHeaders();
            bindGrades();
        } else {
            // display absences
            // todo
        }

        return view;
    }

    private void addRecyclerGradesHeaders() {
        SectionHeaderProvider<Grade> headerProvider = new SimpleSectionHeaderProvider<Grade>() {
            @NonNull
            @Override
            public View getSectionHeaderView(@NonNull Grade grade, int i) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.row_header, null, false);
                TextView textView = view.findViewById(R.id.text_course);
                textView.setText(grade.getCourseName());
                return view;
            }

            @Override
            public boolean isSameSection(@NonNull Grade grade, @NonNull Grade nextGrade) {
                return grade.getCourseId().equals(nextGrade.getCourseId());
            }

            @Override
            public boolean isSticky() {
                return true;
            }
        };
        mGradesRecycler.setSectionHeader(headerProvider);
    }

    // bind data to RecyclerView
    private void bindGrades() {
        getGrades();

        // grades are sorted by course
        List<GradeCell> cells = new ArrayList<>();

        // loop through grades instantiating their cells and adding to cells collection
        for (Grade grade : mGrades) {
            GradeCell cell = new GradeCell(grade);
            // There are two default cell listeners: OnCellClickListener<CELL, VH, T> and OnCellLongClickListener<CELL, VH, T>
            cell.setOnCellClickListener(new SimpleCell.OnCellClickListener<Grade>() {
                @Override
                public void onCellClicked(@NonNull Grade item) {
                    Toast.makeText(getContext(), item.getName(), Toast.LENGTH_SHORT).show();
                }
            });
            cell.setOnCellLongClickListener(new SimpleCell.OnCellLongClickListener<Grade>() {
                @Override
                public void onCellLongClicked(@NonNull Grade item) {
                    Toast.makeText(getContext(), item.getDescription(), Toast.LENGTH_SHORT).show();
                }
            });
            cells.add(cell);
        }
        mGradesRecycler.addCells(cells);
    }

    // returns a list of grades
    private void getGrades() {
        Log.d("~~", mClassId + " " + mStudentId);
        // retrieve schedule from firebase for the currently selected day, sorted by starting time
        mStudentGradesRef.child(mClassId).child(mStudentId).orderByChild("courseId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mGrades.clear();

                if (dataSnapshot.getChildrenCount() > 0) {
                    // todo show message no grades for any course
                    // mNoCoursesLayout.setVisibility(View.GONE);

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        final GradeDb gradeDb = data.getValue(GradeDb.class);

                        // retrieve course info
                        mClassCoursesRef.child(mClassId).child(gradeDb.getCourseId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Course course = dataSnapshot.getValue(Course.class);

                                mGrades.add(new Grade(gradeDb, course.getName()));
                                Log.d("~~", gradeDb.getId());

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


    @Override
    public void onClick(View view) {

    }
}