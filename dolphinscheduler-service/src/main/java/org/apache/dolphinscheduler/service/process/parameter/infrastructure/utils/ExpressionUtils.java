package org.apache.dolphinscheduler.service.process.parameter.infrastructure.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public abstract class ExpressionUtils {

    public static final String DATE_PATTERN_STR = "yyyy-MM-dd";

    public static final String DATE_PATTERN_INT = "yyyyMMdd";

    private ExpressionUtils() {
        throw new AssertionError();
    }

    public static String now() {
        Date date = new Date();
        return format(date);
    }

    public static String getDay() {
        int result = LocalDateTime.now().getDayOfMonth();
        return String.valueOf(result);
    }

    public static String getMonth() {
        int result = LocalDateTime.now().getMonth().getValue();
        return String.valueOf(result);
    }

    public static String getYear() {
        int result = LocalDateTime.now().getYear();
        return String.valueOf(result);
    }

    public static String getFirstDayOfMonth(String dateStr) throws ParseException {
        Date date = DateUtils.parseDate(dateStr, DATE_PATTERN_STR);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return format(calendar.getTime());
    }

    public static String getLastDayOfMonth(String dateStr) throws ParseException {
        Date date = DateUtils.parseDate(dateStr, DATE_PATTERN_STR);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return format(calendar.getTime());
    }

    public static String addYears(String dateStr, int amount) throws ParseException {
        Date date = DateUtils.parseDate(dateStr, DATE_PATTERN_STR);
        Date result = DateUtils.addYears(date, amount);
        return format(result);
    }

    public static String addMonths(String dateStr, int amount) throws ParseException {
        Date date = DateUtils.parseDate(dateStr, DATE_PATTERN_STR);
        Date result = DateUtils.addMonths(date, amount);
        return format(result);
    }

    public static String addWeeks(String dateStr, int amount) throws ParseException {
        Date date = DateUtils.parseDate(dateStr, DATE_PATTERN_STR);
        Date result = DateUtils.addWeeks(date, amount);
        return format(result);
    }

    public static String addDays(String date, int amount) throws ParseException {
        Date now = DateUtils.parseDate(date, DATE_PATTERN_STR);
        Date result = DateUtils.addDays(now, amount);
        return format(result);
    }

    public static String now(Date executeTime) {
        return format(executeTime);
    }

    public static String getYear(Date executeTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        return simpleDateFormat.format(executeTime);
    }

    public static String getMonth(Date executeTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        return simpleDateFormat.format(executeTime);
    }

    public static String getDay(Date executeTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
        return simpleDateFormat.format(executeTime);
    }

    private static String format(Date date) {
        return DateFormatUtils.format(date, DATE_PATTERN_STR);
    }

    public static String formatToInt(String dateStr) throws ParseException {
        Date date = DateUtils.parseDate(dateStr, DATE_PATTERN_STR);
        return DateFormatUtils.format(date, DATE_PATTERN_INT);
    }
}
