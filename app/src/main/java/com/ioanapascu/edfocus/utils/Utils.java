package com.ioanapascu.edfocus.utils;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ioana Pascu on 6/10/2018.
 */

public class Utils {

    public static String getDateString(int year, int month, int day) {
        String date = "";
        date += year + "-";

        if (month < 10) date += "0" + month;
        else date += month;

        date += "-";

        if (day < 10) date += "0" + day;
        else date += day;

        return date;
    }

    public static String formatMessageDate(long dateInMillis) {
        String result = "";
        Date date = new Date(dateInMillis);

        if (DateUtils.isToday(dateInMillis)) {
            result = new SimpleDateFormat("HH:mm").format(date);
        } else {
            result = new SimpleDateFormat("yyyy-MM-dd").format(date);
        }

        return result;
    }

    public static String formatLastSeenDate(long dateInMillis) {
        String result = "";
        Date date = new Date(dateInMillis);

        if (DateUtils.isToday(dateInMillis)) {
            result = " today at ";
            result += new SimpleDateFormat("HH:mm").format(date);
        } else {
            result = " ";
            result += new SimpleDateFormat("yyyy-MM-dd").format(date);
            result += " at ";
            result += new SimpleDateFormat("HH:mm").format(date);
        }

        return result;
    }

}
