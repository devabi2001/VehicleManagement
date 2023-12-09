package com.thirumalaivasa.vehiclemanagement.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

    public static long compareDate(String date1, String date2) {
        Instant d1 = Instant.parse(date1 + "T00:00:00Z");
        Instant d2 = Instant.parse(date2 + "T00:00:00Z");
        return ChronoUnit.DAYS.between(d1, d2);
    }

    public static LocalDate getLocalDate(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalTime getLocalTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return instant.atZone(ZoneId.systemDefault()).toLocalTime();
    }

    public static String getTimeWithoutSeconds(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalTime localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime();

        // Format time without seconds and milliseconds
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        return localTime.format(formatter);
    }

    public static LocalDateTime getLocalDateTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String[] getCurrentDateTime() {
        long timestamp = System.currentTimeMillis();
        LocalDate localDate = getLocalDate(timestamp);
        LocalTime localTime = getLocalTime(timestamp);
        localTime = localTime.truncatedTo(java.time.temporal.ChronoUnit.MINUTES);
        String[] retArray = new String[2];
        retArray[0] = formatDate(localDate);
        retArray[1] = String.valueOf(localTime);
        return retArray;
    }

    private static String formatDate(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return localDate.format(formatter);
    }

    public static String getMonth(LocalDateTime dateTime) {
        // Format month abbreviation (MMM)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM");
        return dateTime.format(formatter);
    }

    public static long convertStringToMilliseconds(String dateString, String timeString) {
        try {
            String dateTimeString = dateString + " " + timeString;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date date = dateFormat.parse(dateTimeString);
            return date != null ? date.getTime() : 0;
        } catch (ParseException e) {
            e.printStackTrace(); // Handle parsing exception as needed
            return -1; // Return -1 to indicate an error
        }
    }

    public static Calendar convertStringToCalendar(String dateString) {
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date = sdf.parse(dateString);
            if (date != null) {
                calendar.setTime(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static Calendar convertLocalDateToCalendar(LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
        return calendar;
    }

    public static Calendar convertTimestampToCalendar(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar;
    }


}
