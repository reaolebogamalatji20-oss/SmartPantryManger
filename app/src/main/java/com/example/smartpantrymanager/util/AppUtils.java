package com.example.smartpantrymanager.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public final class AppUtils {
    private AppUtils() {}

    public static String formatQuantity(double q) {
        return q == Math.floor(q) ? String.valueOf((long) q) : String.valueOf(q);
    }

    public static boolean isExpiringSoon(String isoDate, int withinDays) {
        if (isoDate == null || isoDate.isEmpty()) return false;
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(isoDate);
            if (d == null) return false;
            long diffDays = (d.getTime() - System.currentTimeMillis()) / (24L * 60 * 60 * 1000);
            return diffDays <= withinDays;
            } catch (ParseException e) {
            return false;
        }
    }
}
