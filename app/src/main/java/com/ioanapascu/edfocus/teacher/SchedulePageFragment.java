package com.ioanapascu.edfocus.teacher;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.google.firebase.database.ValueEventListener;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.Course;
import com.ioanapascu.edfocus.model.ScheduleEntry;
import com.ioanapascu.edfocus.model.ScheduleEntryAndCourse;
import com.ioanapascu.edfocus.others.ScheduleListAdapter;
import com.ioanapascu.edfocus.utils.FirebaseUtils;
import com.ioanapascu.edfocus.utils.Utils;
import com.ioanapascu.edfocus.views.NoCoursesDialog;

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
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // variables
    private int mDayIndex; // can be 0, 1, 2, .., 6
    private ScheduleListAdapter mScheduleListAdapter;
    private ArrayList<ScheduleEntryAndCourse> mEntries;
    private ArrayList<String> mCourseNames, mCourseIds;
    private String mClassId;
    private FirebaseUtils firebase;

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
        firebase = new FirebaseUtils(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_page, container, false);

        mEntries = new ArrayList<>();
        mCourseNames = new ArrayList<>();
        mCourseIds = new ArrayList<>();

        mScheduleListView = view.findViewById(R.id.list_schedule);
        mNoCoursesLayout = view.findViewById(R.id.layout_no_courses);
        mAddCourseButton = view.findViewById(R.id.btn_add_course);

        // only teacher can add courses to schedule
        if (firebase.getCurrentUserType().equals("teacher")) {
            mAddCourseButton.setVisibility(View.VISIBLE);
        }

        mAddCourseButton.setOnClickListener(this);

        mScheduleListAdapter = new ScheduleListAdapter(getContext(), R.layout.row_schedule_entry,
                mEntries, mClassId, DAYS[mDayIndex], firebase.getCurrentUserType());
        mScheduleListView.setAdapter(mScheduleListAdapter);

        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        // swipe to refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displaySchedule();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        displaySchedule();

        return view;
    }

    private void displaySchedule() {
        String day = DAYS[mDayIndex];

        // retrieve schedule from firebase for the currently selected day, sorted by starting time
        firebase.mClassScheduleRef.child(mClassId).child(day).orderByChild("startsAt").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEntries.clear();
                mScheduleListAdapter.notifyDataSetChanged();

                if (dataSnapshot.getChildrenCount() > 0) {
                    mNoCoursesLayout.setVisibility(View.GONE);

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        final ScheduleEntry entry = data.getValue(ScheduleEntry.class);

                        // retrieve course info
                        ValueEventListener coursesListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Course course = dataSnapshot.getValue(Course.class);

                                mEntries.add(new ScheduleEntryAndCourse(entry, course));
                                mScheduleListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        firebase.mClassCoursesRef.child(mClassId).child(entry.getCourseId()).addListenerForSingleValueEvent(coursesListener);
                    }
                } else {
                    mNoCoursesLayout.setVisibility(View.VISIBLE);
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
                Long startsAt = Utils.hourMinuteToMillis(startTimePicker.getCurrentHour(), startTimePicker.getCurrentMinute());
                Long endsAt = Utils.hourMinuteToMillis(endTimePicker.getCurrentHour(), endTimePicker.getCurrentMinute());
                String courseId = mCourseIds.get(coursesSpinner.getSelectedItemPosition());

                // get id where to put the new entry for schedule in firebase
                String entryId = firebase.mClassScheduleRef.child(mClassId).child(DAYS[mDayIndex]).push().getKey();
                ScheduleEntry entry = new ScheduleEntry(entryId, startsAt, endsAt, courseId);

                // save to firebase
                firebase.mClassScheduleRef.child(mClassId).child(DAYS[mDayIndex]).child(entryId).setValue(entry);
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
     * If hour is 2 and minute 21 returns "02:21"
     */
    private String getTimeString(int hour, int minute) {
        String time = "";

        if (hour < 10) time += "0" + hour;
        else time += hour;

        time += ":";

        if (minute < 10) time += "0" + minute;
        else time += minute;

        return time;
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
        firebase.mClassCoursesRef.child(mClassId).addValueEventListener(new ValueEventListener() {
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
            // if there are no courses for this class show error
            // otherwise show dialog to add a course to the schedule
            firebase.mClassCoursesRef.child(mClassId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() == 0) {
                        NoCoursesDialog dialog = new NoCoursesDialog((ScheduleActivity) getContext(),
                                mClassId, R.string.schedule_no_courses_error);
                        dialog.show();
                    } else showAddDialog();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}