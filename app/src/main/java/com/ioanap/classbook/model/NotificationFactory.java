package com.ioanap.classbook.model;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ioana Pascu on 5/7/2018.
 */

public class NotificationFactory {

    public static Notification getNotification(NotificationTypesEnum type, String message) {
        Notification notification = null;
        /*long compareValue = getCompareValue();
        Map<String, Object> extras = new HashMap<>();

        switch (type) {
            case EVENT:
                extras.clear();
                extras.put("classId", );
                notification = new Notification("Events", message, "ic_events",
                        "com.ioanap.classbook_TARGET_EVENT_NOTIFICATION", compareValue, false, extras);
                break;
        }*/

        return notification;
    }

    /**
     * date in format "2018-2-13", time in format "12:30"
     *
     * @return long value that will be used to sort notifications in Firebase e.g. 201802131230
     */
    private static long getCompareValue() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Date now = new Date();

        String date = dateFormat.format(now);
        String time = timeFormat.format(now);

        long value = 0;

        String[] parts = date.split("-");
        value = Long.parseLong(parts[0]);
        value = value * 100 + Long.parseLong(parts[1]);
        value = value * 100 + Long.parseLong(parts[2]);
        parts = time.split("\\:");
        value = value * 100 + Long.parseLong(parts[0]);
        value = value * 100 + Long.parseLong(parts[1]);

        Log.d("~~compareValue", String.valueOf(value));

        return value;
    }

}
