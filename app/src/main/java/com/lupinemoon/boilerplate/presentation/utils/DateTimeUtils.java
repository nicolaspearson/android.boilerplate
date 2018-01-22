package com.lupinemoon.boilerplate.presentation.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

public class DateTimeUtils {

    /**
     * This method parses and converts the time time zone of
     * a timestamp string received from the API.
     * The API always returns the timestamp with UTC time zone,
     * this gets converted to the users default time zone.
     *
     * @param dateTimeString the timestamp to format and convert
     * @return the formatted date with time zone adjusted
     */
    public static Date parseFullDateTimeString(String dateTimeString, String serverFormat) {
        if (!TextUtils.isEmpty(dateTimeString)) {
            try {
                // The API does not return time zone therefore we need to adjust it
                Calendar calendar = Calendar.getInstance();
                TimeZone calendarTimeZone = calendar.getTimeZone();

                // Parse and set the time zone to UTC as received from the API
                SimpleDateFormat sourceFormat = new SimpleDateFormat(
                        serverFormat,
                        Locale.getDefault());

                sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date parsed = sourceFormat.parse(dateTimeString);

                // Convert the date to the users local time zone
                SimpleDateFormat destFormat = new SimpleDateFormat(
                        serverFormat,
                        Locale.getDefault());

                destFormat.setTimeZone(calendarTimeZone);
                destFormat.format(parsed);
                return destFormat.getCalendar().getTime();
            } catch (Exception e) {
                Timber.e(e, "Error parsing date %s", dateTimeString);
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * This method parses a UTC timestamp and converts it to the Local time zone
     *
     * @param dateTimeString the timestamp to format and convert
     * @return the formatted date with time zone adjusted to the Local time zone
     */
    public static String convertDateTimeStringToLocal(String dateTimeString, String serverFormat) {
        if (!TextUtils.isEmpty(dateTimeString)) {
            try {
                // The API does not return time zone therefore we need to adjust it
                Calendar calendar = Calendar.getInstance();
                TimeZone calendarTimeZone = calendar.getTimeZone();

                // Parse and set the time zone to UTC as received from the API
                SimpleDateFormat sourceFormat = new SimpleDateFormat(
                        serverFormat,
                        Locale.getDefault());

                sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date parsed = sourceFormat.parse(dateTimeString);

                // Convert the date to the users local time zone
                SimpleDateFormat destFormat = new SimpleDateFormat(
                        serverFormat,
                        Locale.getDefault());

                destFormat.setTimeZone(calendarTimeZone);
                return destFormat.format(parsed);
            } catch (Exception e) {
                Timber.e(e, "Error parsing date %s", dateTimeString);
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * This method parses a local timestamp and converts it UTC time zone
     *
     * @param dateTimeString the timestamp to format and convert
     * @return the formatted date with time zone adjusted to UTC
     */
    public static String convertDateTimeStringToUtc(String dateTimeString, String serverFormat) {
        if (!TextUtils.isEmpty(dateTimeString)) {
            try {
                // The API does not return time zone therefore we need to adjust it
                Calendar calendar = Calendar.getInstance();
                TimeZone calendarTimeZone = calendar.getTimeZone();

                // Parse and set the time zone to UTC as received from the API
                SimpleDateFormat sourceFormat = new SimpleDateFormat(
                        serverFormat,
                        Locale.getDefault());

                sourceFormat.setTimeZone(calendarTimeZone);
                Date parsed = sourceFormat.parse(dateTimeString);

                // Convert the date to the users local time zone
                SimpleDateFormat destFormat = new SimpleDateFormat(
                        serverFormat,
                        Locale.getDefault());

                destFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                return destFormat.format(parsed);
            } catch (Exception e) {
                Timber.e(e, "Error parsing date %s", dateTimeString);
                return null;
            }
        } else {
            return null;
        }
    }

    public static Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null && !TextUtils.isEmpty(date.toString())) {
            calendar.setTime(date);
        }
        return calendar;
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public static String getCurrentUtcTimeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(new Date());
    }

    public static String getFilenameTimeStamp() {
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(new Date());
    }

    public static String formatDate(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String getCalendarTimeStamp(Calendar calendar) {
        if (calendar != null) {
            return new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()).format(calendar.getTime());
        }
        return "";
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
        }
        return false;
    }

    public static boolean isSameWeek(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR));
        }
        return false;
    }

    public static boolean isToday(Calendar cal) {
        return isSameDay(cal, Calendar.getInstance());
    }

    public static boolean isYesterday(Calendar cal) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return isSameDay(cal, calendar);
    }

    public static Date getDateMidnight(String timestamp, String serverFormat) {
        Date date = parseFullDateTimeString(timestamp, serverFormat);
        Calendar calendar = dateToCalendar(date != null ? date : new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
