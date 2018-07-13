package com.ioanapascu.edfocus.shared;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
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
import com.ioanapascu.edfocus.BaseActivity;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.model.Event;
import com.ioanapascu.edfocus.others.EventsStickyAdapter;
import com.ioanapascu.edfocus.utils.Utils;

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
        String userType = firebase.getCurrentUserType();
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
        firebase.mClassEventsRef.child(mClassId).orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEvents.clear();
                mEventsAdapter.notifyDataSetChanged();

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
        final TextInputLayout nameTil = mDialog.findViewById(R.id.til_name);
        final EditText locationText = mDialog.findViewById(R.id.text_location);
        final TextInputLayout locationTil = mDialog.findViewById(R.id.til_location);
        final EditText descriptionText = mDialog.findViewById(R.id.text_description);
        final TextInputLayout descriptionTil = mDialog.findViewById(R.id.til_description);

        timePicker.setIs24HourView(true);

        // modify widgets according to the mode
        if (position != -1) {
            // edit mode
            titleText.setText("Edit Event");
            createBtn.setText("Save");
            infoText.setVisibility(View.GONE);

            // load event info and display it in widgets
            firebase.mClassEventsRef.child(mClassId).child(eventId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Event event = dataSnapshot.getValue(Event.class);

                            nameText.setText(event.getName());
                            descriptionText.setText(event.getDescription());
                            locationText.setText(event.getLocation());

                            // set the time picker
                            Long millis = event.getDate();
                            timePicker.setCurrentHour(Utils.millisToHour(millis));
                            timePicker.setCurrentMinute(Utils.millisToMinute(millis));
                            //timePicker.setHour(Integer.parseInt(parts[0]));

                            // set the date picker
                            datePicker.updateDate(Utils.millisToYear(millis),
                                    Utils.millisToMonth(millis), Utils.millisToDay(millis));
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
                Long date = Utils.yearMonthDayHourMinuteToMillis(datePicker.getYear(),
                        datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());

                // validation
                boolean valid = Utils.toggleFieldError(nameTil, name, "Please enter a name for the event.");
                valid = Utils.toggleFieldError(locationTil, location, "Please enter a location for the event.") && valid;
                if (!valid) {
                    return;
                }

                Event event = new Event(eventId, date, location, name, description);

                // creating a new event
                if (position == -1) {
                    // get id where to put the info
                    String newEventId = firebase.mClassEventsRef.child(mClassId).push().getKey();
                    event.setId(newEventId);
                    // save to firebase
                    firebase.mClassEventsRef.child(mClassId).child(newEventId).setValue(event);
                } else {
                    // editing event
                    firebase.mClassEventsRef.child(mClassId).child(eventId).setValue(event);
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
                                firebase.mClassEventsRef.child(mClassId).child(eventId).removeValue();
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

}
