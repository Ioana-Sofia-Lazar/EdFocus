package com.ioanap.classbook.shared;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Event;
import com.ioanap.classbook.utils.EventsStickyAdapter;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class EventsActivity extends BaseActivity {

    // variables
    private EventsStickyAdapter mEventsAdapter;
    private ArrayList<Event> mEvents;
    private String mClassId;

    // widgets
    private RelativeLayout mNoEventsLayout;
    private FloatingActionButton mAddEventFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(this, false);
        setContentView(R.layout.activity_events);

        mEvents = new ArrayList<>();

        // get class id
        Intent myIntent = getIntent();
        mClassId = myIntent.getStringExtra("classId");

        // widgets
        mNoEventsLayout = findViewById(R.id.layout_no_events);
        mAddEventFab = findViewById(R.id.fab_add_event);

        // only teacher can add events
        String userType = getCurrentUserType();
        if (userType.equals("teacher")) mAddEventFab.setVisibility(View.VISIBLE);

        mAddEventFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEventDialog(-1);
            }
        });

        // events list
        StickyListHeadersListView stickyList = findViewById(R.id.list_events);
        mEventsAdapter = new EventsStickyAdapter(this, R.layout.row_event,
                R.layout.row_event_header, mEvents, userType);
        stickyList.setAdapter(mEventsAdapter);

        displayEvents();
    }

    private void displayEvents() {
        mClassEventsRef.child(mClassId).orderByChild("compareValue").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEvents.clear();

                if (dataSnapshot.getChildrenCount() > 0) {
                    mNoEventsLayout.setVisibility(View.GONE);

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Event event = data.getValue(Event.class);
                        mEvents.add(event);

                        mEventsAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * @param position index of clicked item for editing or -1 if we user wants to create new event
     */
    public void showEventDialog(final int position) {
        final String eventId = position == -1 ? null : mEvents.get(position).getId();
        final Dialog mDialog = new Dialog(EventsActivity.this);
        mDialog.setContentView(R.layout.dialog_add_event);

        // dialog widgets
        TextView titleText = mDialog.findViewById(R.id.text_title);
        TextView infoText = mDialog.findViewById(R.id.text_info);
        final DatePicker datePicker = mDialog.findViewById(R.id.date_picker);
        final TimePicker timePicker = mDialog.findViewById(R.id.time_picker);
        Button createBtn = mDialog.findViewById(R.id.btn_create);
        ImageView deleteBtn = mDialog.findViewById(R.id.btn_delete);
        ImageView cancelImg = mDialog.findViewById(R.id.img_cancel);
        final EditText nameText = mDialog.findViewById(R.id.text_name);
        final EditText locationText = mDialog.findViewById(R.id.text_location);
        final EditText descriptionText = mDialog.findViewById(R.id.text_description);

        timePicker.setIs24HourView(true);

        // modify widgets according to the mode
        if (position != -1) {
            // edit mode
            titleText.setText("Edit Event");
            createBtn.setText("Save");

            // load event info and display it in widgets
            mClassEventsRef.child(mClassId).child(eventId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Event event = dataSnapshot.getValue(Event.class);

                            nameText.setText(event.getName());
                            descriptionText.setText(event.getDescription());
                            locationText.setText(event.getLocation());

                            // parse time and set the time picker
                            String time = event.getTime();
                            String[] parts = time.split("\\:");
                            timePicker.setCurrentHour(Integer.parseInt(parts[0]));
                            timePicker.setCurrentMinute(Integer.parseInt(parts[1]));
                            //timePicker.setHour(Integer.parseInt(parts[0]));

                            // parse date and set the date picker
                            String date = event.getDate();
                            parts = date.split("-");
                            datePicker.updateDate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1,
                                    Integer.parseInt(parts[2]));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );
        } else {
            deleteBtn.setVisibility(View.GONE);
        }

        // button click
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get info introduced by user
                String name = nameText.getText().toString();
                String description = descriptionText.getText().toString();
                String location = locationText.getText().toString();
                String time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-"
                        + datePicker.getDayOfMonth();

                Event event = new Event(eventId, date, time, location, name, description,
                        getCompareValue(date, time));

                // creating a new event
                if (position == -1) {
                    // get id where to put the info
                    String newEventId = mClassEventsRef.child(mClassId).push().getKey();
                    event.setId(newEventId);
                    // save to firebase
                    mClassEventsRef.child(mClassId).child(newEventId).setValue(event);
                } else {
                    // editing event
                    mClassEventsRef.child(mClassId).child(eventId).setValue(event);
                }

                mDialog.dismiss();
            }
        });

        // delete event button click
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show confirmation dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EventsActivity.this);

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to delete this event?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // delete from firebase
                                // todo
                                mClassEventsRef.child(mClassId).child(eventId).removeValue();
                                dialog.dismiss();
                                mDialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // create and show alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
            }
        });

        // x button click (cancel)
        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    /**
     * @param date in format "2018-2-13"
     * @param time in format "12:30"
     * @return long value that will be used to sort events in Firebase e.g. 201802131230
     */
    private long getCompareValue(String date, String time) {
        long value = 0;

        String[] parts = date.split("-");
        value = Long.parseLong(parts[0]);
        value = value * 100 + Long.parseLong(parts[1]);
        value = value * 100 + Long.parseLong(parts[2]);
        parts = time.split("\\:");
        value = value * 100 + Long.parseLong(parts[0]);
        value = value * 100 + Long.parseLong(parts[1]);

        return value;
    }

}
