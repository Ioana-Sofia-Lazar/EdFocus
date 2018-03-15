package com.ioanap.classbook.utils;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.ioanap.classbook.R;
import com.ioanap.classbook.model.Contact;
import com.ioanap.classbook.teacher.StudentsActivity;

import java.util.ArrayList;

/**
 * Created by ioana on 3/14/2018.
 */

public class ActionModeCallback implements ActionMode.Callback {

    private Context context;
    private StudentsListAdapter mStudentsListAdapter;
    private ArrayList<Contact> mStudents;


    public ActionModeCallback(Context context, StudentsListAdapter studentsListAdapter, ArrayList<Contact> students) {
        this.context = context;
        this.mStudentsListAdapter = studentsListAdapter;
        this.mStudents = students;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // inflate menu
        mode.getMenuInflater().inflate(R.menu.menu_selected_students, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // sometimes the menu will not be visible so set the visibility manually
        // according to SDK levels
        if (Build.VERSION.SDK_INT < 11) {
            MenuItemCompat.setShowAsAction(menu.findItem(R.id.option_remove), MenuItemCompat.SHOW_AS_ACTION_NEVER);
        } else {
            menu.findItem(R.id.option_remove).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_remove:
                break;

        }
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // when action mode is destroyed remove selection and set action mode to null
        mStudentsListAdapter.removeSelection();
        StudentsActivity listFragment = new StudentsActivity();
        ((StudentsActivity) context).setNullActionMode();
    }
}