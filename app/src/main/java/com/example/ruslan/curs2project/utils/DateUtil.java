package com.example.ruslan.curs2project.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Ruslan on 18.02.2018.
 */

public class DateUtil {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        DateUtil.dateFormat = dateFormat;
    }

    public static String getDateInStr(Date date){
        return dateFormat.format(date);
    }

    public static Date getDateFromStr(String dateStr){
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            Log.d(Const.TAG_LOG,"DateTransform failed");
        }
        return null;
    }

    public static long getFilterYear(){
        Date date = new Date();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR,Const.FILTER_YEAR);
        return calendar.getTime().getTime();
    }

}
