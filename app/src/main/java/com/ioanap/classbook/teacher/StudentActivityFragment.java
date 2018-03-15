package com.ioanap.classbook.teacher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Grade;
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

    private DatabaseReference mScheduleRef, mClassCoursesRef;

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
        //this.addRecyclerHeaders();
        //this.bindData();

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

    // bind data to our RecyclerView
    private void bindGrades() {
        List<Grade> grades = getGrades();
        // grades are sorted by course
        List<GradeCell> cells = new ArrayList<>();

        // loop through grades instantiating thier cells and adding to cells collection
        for (Grade grade : grades) {
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
    private ArrayList<Grade> getGrades() {
        ArrayList<Grade> grades = new ArrayList<>();

        grades.add(new Grade("1", "test 1", "F", "c1", "s1", "2017-05-02", "very very gud", "course1"));
        grades.add(new Grade("2", "test 2", "F", "c1", "s1", "2017-05-02", "very very gud", "course1"));
        grades.add(new Grade("3", "test 3", "F", "c2", "s1", "2017-05-02", "very very gud", "course2"));
        grades.add(new Grade("4", "test 4", "F", "c2", "s1", "2017-05-02", "very very gud", "course2"));
        grades.add(new Grade("1", "test 1", "F", "c1", "s1", "2017-05-02", "very very gud", "course1"));
        grades.add(new Grade("2", "test 2", "F", "c1", "s1", "2017-05-02", "very very gud", "course1"));
        grades.add(new Grade("3", "test 3", "F", "c2", "s1", "2017-05-02", "very very gud", "course2"));
        grades.add(new Grade("4", "test 4", "F", "c2", "s1", "2017-05-02", "very very gud", "course2"));
        grades.add(new Grade("1", "test 1", "F", "c1", "s1", "2017-05-02", "very very gud", "course1"));
        grades.add(new Grade("2", "test 2", "F", "c1", "s1", "2017-05-02", "very very gud", "course1"));
        grades.add(new Grade("3", "test 3", "F", "c2", "s1", "2017-05-02", "very very gud", "course2"));
        grades.add(new Grade("4", "test 4", "F", "c2", "s1", "2017-05-02", "very very gud", "course2"));
        grades.add(new Grade("1", "test 1", "F", "c1", "s1", "2017-05-02", "very very gud", "course1"));
        grades.add(new Grade("2", "test 2", "F", "c1", "s1", "2017-05-02", "very very gud", "course1"));
        grades.add(new Grade("3", "test 3", "F", "c2", "s1", "2017-05-02", "very very gud", "course2"));
        grades.add(new Grade("4", "test 4", "F", "c2", "s1", "2017-05-02", "very very gud", "course2"));

        return grades;
    }


    @Override
    public void onClick(View view) {

    }
}