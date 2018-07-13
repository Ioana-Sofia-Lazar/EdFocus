package com.ioanapascu.edfocus.utils;

import android.support.design.widget.TextInputLayout;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Ioana Pascu on 6/10/2018.
 */

public class Utils {

    private static String[] MONTHS = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
    private static String[] DAYS = {"", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    public static boolean toggleFieldError(TextInputLayout inputLayout, String input, String message) {
        if (input.isEmpty()) {
            inputLayout.setErrorEnabled(true);
            inputLayout.setError(message);
            return false;
        }
        inputLayout.setErrorEnabled(false);
        return true;
    }

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
        String result;
        Date date = new Date(dateInMillis);

        if (DateUtils.isToday(dateInMillis)) {
            result = new SimpleDateFormat("HH:mm").format(date);
        } else {
            result = new SimpleDateFormat("yyyy-MM-dd").format(date);
        }

        return result;
    }

    public static String millisToDateString(long dateInMillis) {
        Date date = new Date(dateInMillis);
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String millisToTimeString(long dateInMillis) {
        Date date = new Date(dateInMillis);
        return new SimpleDateFormat("HH:mm").format(date);
    }

    public static int millisToHour(long dateInMillis) {
        Date date = new Date(dateInMillis);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int millisToMinute(long dateInMillis) {
        Date date = new Date(dateInMillis);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    public static int millisToDay(long dateInMillis) {
        Date date = new Date(dateInMillis);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String millisToDayName(long dateInMillis) {
        Date date = new Date(dateInMillis);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return DAYS[calendar.get(Calendar.DAY_OF_WEEK)];
    }

    public static int millisToMonth(long dateInMillis) {
        Date date = new Date(dateInMillis);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    public static String millisToMonthName(long dateInMillis) {
        Date date = new Date(dateInMillis);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return MONTHS[calendar.get(Calendar.MONTH)];
    }

    public static int millisToYear(long dateInMillis) {
        Date date = new Date(dateInMillis);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static long yearMonthDayToMillis(int year, int month, int day) {
        Date date = new GregorianCalendar(year, month, day).getTime();
        return date.getTime();
    }

    public static long yearMonthDayHourMinuteToMillis(int year, int month, int day, int hour, int minute) {
        Date date = new GregorianCalendar(year, month, day, hour, minute).getTime();
        return date.getTime();
    }

    public static long hourMinuteToMillis(int hour, int minute) {
        Date date = new GregorianCalendar(2018, 1, 1, hour, minute).getTime();
        return date.getTime();
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
