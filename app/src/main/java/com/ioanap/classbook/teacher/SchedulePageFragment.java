package com.ioanap.classbook.teacher;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Course;
import com.ioanap.classbook.model.ScheduleEntry;
import com.ioanap.classbook.model.ScheduleEntryAndCourse;
import com.ioanap.classbook.utils.ScheduleListAdapter;

import java.util.ArrayList;

/**
 * Created by ioana on 2/28/2018.
 */

public class SchedulePageFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String CLASS_ID = "CLASS_ID";
    private static final String[] DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    // widgets
    private ListView mScheduleListView;
    private RelativeLayout mNoCoursesLayout;
    private Button mAddCourseButton;

    // variables
    private int mDayIndex; // can be 0, 1, 2, .., 6
    private ScheduleListAdapter mScheduleListAdapter;
    private ArrayList<ScheduleEntryAndCourse> mEntries;
    private ArrayList<String> mCourseNames, mCourseIds;
    private String mClassId;

    private DatabaseReference mScheduleRef, mClassCoursesRef;

    public static SchedulePageFragment newInstance(int page, String classId) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(CLASS_ID, classId);
        SchedulePageFragment fragment = new SchedulePageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDayIndex = getArguments().getInt(ARG_PAGE);
        mClassId = getArguments().getString(CLASS_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_page, container, false);

        mEntries = new ArrayList<>();
        mCourseNames = new ArrayList<>();
        mCourseIds = new ArrayList<>();

        mScheduleRef = FirebaseDatabase.getInstance().getReference().child("schedule");
        mClassCoursesRef = FirebaseDatabase.getInstance().getReference().child("classCourses");

        mScheduleListView = view.findViewById(R.id.list_schedule);
        mNoCoursesLayout = view.findViewById(R.id.layout_no_courses);
        mAddCourseButton = view.findViewById(R.id.btn_add_course);

        mAddCourseButton.setOnClickListener(this);

        mScheduleListAdapter = new ScheduleListAdapter(getContext(), R.layout.row_schedule_entry, mEntries, mClassId, DAYS[mDayIndex]);
        mScheduleListView.setAdapter(mScheduleListAdapter);

        displaySchedule();

        return view;
    }

    private void displaySchedule() {
        String day = DAYS[mDayIndex];

        // retrieve schedule from firebase for the currently selected day, sorted by starting time
        mScheduleRef.child(mClassId).child(day).orderByChild("compareValue").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEntries.clear();

                if (dataSnapshot.getChildrenCount() > 0) {
                    mNoCoursesLayout.setVisibility(View.GONE);

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        final ScheduleEntry entry = data.getValue(ScheduleEntry.class);

                        // retrieve course info
                        mClassCoursesRef.child(mClassId).child(entry.getCourseId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Course course = dataSnapshot.getValue(Course.class);

                                mEntries.add(new ScheduleEntryAndCourse(entry, course));
                                mScheduleListAdapter.notifyDataSetChanged();
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

    private void showAddDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_schedule_course);

        // dialog widgets
        final TimePicker startTimePicker = dialog.findViewById(R.id.time_picker_start);
        startTimePicker.setIs24HourView(true);
        final TimePicker endTimePicker = dialog.findViewById(R.id.time_picker_end);
        endTimePicker.setIs24HourView(true);
        Button createBtn = dialog.findViewById(R.id.btn_create);
        ImageView cancelImg = dialog.findViewById(R.id.img_cancel);
        final Spinner coursesSpinner = dialog.findViewById(R.id.spinner_courses);

        populateSpinner(coursesSpinner);

        // add course button click
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get info introduced by user
                String startsAt = startTimePicker.getCurrentHour() + ":" + startTimePicker.getCurrentMinute();
                String endsAt = endTimePicker.getCurrentHour() + ":" + endTimePicker.getCurrentMinute();
                String courseId = mCourseIds.get(coursesSpinner.getSelectedItemPosition());

                // get id where to put the new entry for schedule in firebase
                String entryId = mScheduleRef.child(mClassId).child(DAYS[mDayIndex]).push().getKey();
                float compareValue = getStartsAtFloat(startsAt);
                ScheduleEntry entry = new ScheduleEntry(entryId, startsAt, endsAt, courseId, compareValue);

                // save to firebase
                mScheduleRef.child(mClassId).child(DAYS[mDayIndex]).child(entryId).setValue(entry);
                dialog.dismiss();
            }
        });

        // x button click (cancel)
        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    /**
     * Value that starting times will be compared by
     *
     * @param startsAt string in format hh:mm
     * @return float hh.mm
     */
    private float getStartsAtFloat(String startsAt) {
        String[] parts = startsAt.split(":");
        float time = Float.parseFloat(parts[0] + "." + parts[1]);

        return time;
    }

    private void populateSpinner(Spinner spinner) {
        // set adapter for spinner
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, mCourseNames);

        // spinner row layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attach data adapter to spinner
        spinner.setAdapter(dataAdapter);

        // get courses from firebase
        mClassCoursesRef.child(mClassId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCourseNames.clear();

                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Course course = data.getValue(Course.class);
                        mCourseNames.add(course.getName());
                        mCourseIds.add(course.getId());

                        dataAdapter.notifyDataSetChanged();
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
        if (view == mAddCourseButton) {
            showAddDialog();
        }
    }
}