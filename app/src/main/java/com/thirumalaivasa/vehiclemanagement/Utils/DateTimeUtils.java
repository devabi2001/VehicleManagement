package com.thirumalaivasa.vehiclemanagement.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static long compareDate(String date1, String date2) {
        LocalDate localDate1 = LocalDate.parse(date1, formatter);
        LocalDate localDate2 = LocalDate.parse(date2, formatter);
        Instant instant1 = localDate1.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant instant2 = localDate2.atStartOfDay(ZoneOffset.UTC).toInstant();
        return ChronoUnit.DAYS.between(instant1, instant2);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return localTime.format(formatter);
    }

    public static LocalDateTime getLocalDateTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String[] getCurrentDateTime() {
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        return new String[]{formatDate(localDate), localTime.toString()};
    }

    public static String calculateDateBefore(int durationInMonths) {
        LocalDate currentDate = LocalDate.now();
        LocalDate resultDate = currentDate.minusMonths(durationInMonths);
        return formatDate(resultDate);
    }

    public static long calculateDaysDifference(LocalDate date1, LocalDate date2) {
        return ChronoUnit.DAYS.between(date1, date2);
    }

    public static long calculateDaysDifference(String date1, String date2) {
        LocalDate d1 = LocalDate.parse(date1, formatter);
        LocalDate d2 = LocalDate.parse(date2, formatter);
        return calculateDaysDifference(d1, d2);
    }

    public static LocalDate stringToLocalDate(String d1) {
        return LocalDate.parse(d1, formatter);
    }

    public static long stringToTimeStamp(String d1) {
        LocalDate localDate = stringToLocalDate(d1);
        LocalDateTime localDateTime = localDate.atStartOfDay();
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        return instant.toEpochMilli();
    }

    public static String formatDate(LocalDate localDate) {
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
