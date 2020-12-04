package com.zebra.pslsdksample.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.zebra.pslsdksample.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Bapusaheb Shinde on 02/Nov/2017.
 */

public class ApplicationCommonMethods {


    public static  boolean isValidTruckNumber(String s) {
        String n = ".*[0-9].*";
        String a = ".*[A-Z].*";
        return s.matches(n) && s.matches(a);
    }

    static Dialog errordialog,successdialog;
    public static void showCustomErrorDialog(Context context, String msg){
        if(errordialog!=null){
            errordialog.dismiss();
        }
        if(successdialog!=null){
            successdialog.dismiss();
        }
        errordialog = new Dialog(context);
        errordialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errordialog.setCancelable(false);
        errordialog.setContentView(R.layout.custom_alert_dialog_layout);
        TextView text = (TextView) errordialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) errordialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errordialog.dismiss();
            }
        });

        dialogButton.setVisibility(View.GONE);
        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
               // btnClear.performClick();
                errordialog.dismiss();
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, AppConstants.DIALOG_DISMISS_COUNT); // the timer will count 5 seconds....

        errordialog.getWindow().getAttributes().windowAnimations = R.style.FadeInOutAnimation;
        if (context instanceof Activity){
            Activity activity = (Activity)context;
            if ( !activity.isFinishing() ) {
                errordialog.show();
            }
        }
    }

    public static void showCustomSuccessDialog(Context context, String msg){
        if(errordialog!=null){
            errordialog.dismiss();
        }
        if(successdialog!=null){
            successdialog.dismiss();
        }
        successdialog = new Dialog(context);
        successdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successdialog.setCancelable(false);
        successdialog.setContentView(R.layout.custom_alert_success_dialog_layout);
        TextView text = (TextView) successdialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) successdialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successdialog.dismiss();
            }
        });

        dialogButton.setVisibility(View.GONE);
        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                // btnClear.performClick();
                successdialog.dismiss();
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, AppConstants.DIALOG_DISMISS_COUNT); // the timer will count 5 seconds....

        successdialog.getWindow().getAttributes().windowAnimations = R.style.FadeInOutAnimation;
        if (context instanceof Activity){
            Activity activity = (Activity)context;
            if ( !activity.isFinishing() ) {
                successdialog.show();
            }
        }

    }


    public static String getSystemDateTime() {
        try {
            int year, monthformat, dateformat, sec;
            String da, mont, hor, min, yr, systemDate, secs;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            year = calendar.get(Calendar.YEAR);
            monthformat = calendar.get(Calendar.MONTH) + 1;
            dateformat = calendar.get(Calendar.DATE);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            sec = calendar.get(Calendar.SECOND);
            da = Integer.toString(dateformat);
            mont = Integer.toString(monthformat);
            hor = Integer.toString(hours);
            min = Integer.toString(minutes);
            secs = Integer.toString(sec);
            if (da.trim().length() == 1) {
                da = "0" + da;
            }
            if(mont.trim().equals("1")){
                mont = "Jan";
            }
            if(mont.trim().equals("2")){
                mont = "Feb";
            }
            if(mont.trim().equals("3")){
                mont = "Mar";
            }
            if(mont.trim().equals("4")){
                mont = "Apr";
            }
            if(mont.trim().equals("5")){
                mont = "May";
            }
            if(mont.trim().equals("6")){
                mont = "Jun";
            }
            if(mont.trim().equals("7")){
                mont = "Jul";
            }
            if(mont.trim().equals("8")){
                mont = "Aug";
            }
            if(mont.trim().equals("9")){
                mont = "Sep";
            }
            if(mont.trim().equals("10")){
                mont = "Oct";
            }
            if(mont.trim().equals("11")){
                mont = "Nov";
            }
            if(mont.trim().equals("12")){
                mont = "Dec";
            }
            if (hor.trim().length() == 1) {
                hor = "0" + hor;
            }
            if (min.trim().length() == 1) {
                min = "0" + min;
            }
            if (secs.trim().length() == 1) {
                secs = "0" + secs;
            }
            yr = Integer.toString(year);
            systemDate = (da + mont + yr + hor + min + secs);
            return systemDate;
        } catch (Exception e) {
            return "01Jan1970000000";
        }
    }

    public static String getSystemDateTimeDigit() {
        try {
            int year, monthformat, dateformat, sec;
            String da, mont, hor, min, yr, systemDate, secs;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            year = calendar.get(Calendar.YEAR);
            monthformat = calendar.get(Calendar.MONTH) + 1;
            dateformat = calendar.get(Calendar.DATE);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            sec = calendar.get(Calendar.SECOND);
            da = Integer.toString(dateformat);
            mont = Integer.toString(monthformat);
            hor = Integer.toString(hours);
            min = Integer.toString(minutes);
            secs = Integer.toString(sec);
            if (da.trim().length() == 1) {
                da = "0" + da;
            }
            if(mont.trim().equals("1")){
                mont = "01";
            }
            if(mont.trim().equals("2")){
                mont = "02";
            }
            if(mont.trim().equals("3")){
                mont = "03";
            }
            if(mont.trim().equals("4")){
                mont = "04";
            }
            if(mont.trim().equals("5")){
                mont = "05";
            }
            if(mont.trim().equals("6")){
                mont = "06";
            }
            if(mont.trim().equals("7")){
                mont = "07";
            }
            if(mont.trim().equals("8")){
                mont = "08";
            }
            if(mont.trim().equals("9")){
                mont = "09";
            }
            if(mont.trim().equals("10")){
                mont = "10";
            }
            if(mont.trim().equals("11")){
                mont = "11";
            }
            if(mont.trim().equals("12")){
                mont = "12";
            }
            if (hor.trim().length() == 1){
                hor = "0" + hor;
            }
            if (min.trim().length() == 1){
                min = "0" + min;
            }
            if (secs.trim().length() == 1) {
                secs = "0" + secs;
            }
            yr = Integer.toString(year);
            systemDate = (da + mont + yr + hor + min + secs);
            return systemDate;
        } catch (Exception e) {
            return "01011970000000";
        }
    }

    public static String getSystemDateTimeInFormatt() {
        try {
            int year, monthformat, dateformat, sec;
            String da, mont, hor, min, yr, systemDate, secs;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            year = calendar.get(Calendar.YEAR);
            monthformat = calendar.get(Calendar.MONTH) + 1;
            dateformat = calendar.get(Calendar.DATE);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            sec = calendar.get(Calendar.SECOND);
            da = Integer.toString(dateformat);
            mont = Integer.toString(monthformat);
            hor = Integer.toString(hours);
            min = Integer.toString(minutes);
            secs = Integer.toString(sec);
            if (da.trim().length() == 1) {
                da = "0" + da;
            }
            if(mont.trim().equals("1")){
                mont = "01";
            }
            if(mont.trim().equals("2")){
                mont = "02";
            }
            if(mont.trim().equals("3")){
                mont = "03";
            }
            if(mont.trim().equals("4")){
                mont = "04";
            }
            if(mont.trim().equals("5")){
                mont = "05";
            }
            if(mont.trim().equals("6")){
                mont = "06";
            }
            if(mont.trim().equals("7")){
                mont = "07";
            }
            if(mont.trim().equals("8")){
                mont = "08";
            }
            if(mont.trim().equals("9")){
                mont = "09";
            }
            if(mont.trim().equals("10")){
                mont = "10";
            }
            if(mont.trim().equals("11")){
                mont = "11";
            }
            if(mont.trim().equals("12")){
                mont = "12";
            }
            if (hor.trim().length() == 1) {
                hor = "0" + hor;
            }
            if (min.trim().length() == 1) {
                min = "0" + min;
            }
            if (secs.trim().length() == 1) {
                secs = "0" + secs;
            }
            yr = Integer.toString(year);
           // systemDate = (da + mont + yr + hor + min + secs);
            systemDate = (yr + "-" + mont + "-" + da + " " + hor +":"+ min +":"+ secs);
            return systemDate;
        } catch (Exception e) {
           // return "01011970000000";
            return "1970-01-01 00:00:00";
        }
    }



    public static long getSyncDifference(String start , String  end) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date startDate = simpleDateFormat.parse(start);
            Date endDate = simpleDateFormat.parse(end);
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        //return elapsedDays;
        return elapsedDays;

    }
    public static String get11DigitUniqueNumber() {
        try {
            int year, monthformat, dateformat, sec;
            String da, mont, hor, min, yr, systemDate, secs;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            year = calendar.get(Calendar.YEAR);
            monthformat = calendar.get(Calendar.MONTH) + 1;
            dateformat = calendar.get(Calendar.DATE);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            sec = calendar.get(Calendar.SECOND);
            da = Integer.toString(dateformat);
            mont = Integer.toString(monthformat);
            hor = Integer.toString(hours);
            min = Integer.toString(minutes);
            secs = Integer.toString(sec);
            if (da.trim().length() == 1) {
                da = "0" + da;
            }
            if(mont.trim().equals("1")){
                mont = "01";
            }
            if(mont.trim().equals("2")){
                mont = "02";
            }
            if(mont.trim().equals("3")){
                mont = "03";
            }
            if(mont.trim().equals("4")){
                mont = "04";
            }
            if(mont.trim().equals("5")){
                mont = "05";
            }
            if(mont.trim().equals("6")){
                mont = "06";
            }
            if(mont.trim().equals("7")){
                mont = "07";
            }
            if(mont.trim().equals("8")){
                mont = "08";
            }
            if(mont.trim().equals("9")){
                mont = "09";
            }
            if(mont.trim().equals("10")){
                mont = "10";
            }
            if(mont.trim().equals("11")){
                mont = "11";
            }
            if(mont.trim().equals("12")){
                mont = "12";
            }
            if (hor.trim().length() == 1) {
                hor = "0" + hor;
            }
            if (min.trim().length() == 1) {
                min = "0" + min;
            }
            if (secs.trim().length() == 1) {
                secs = "0" + secs;
            }
            yr = Integer.toString(year);
            yr = yr.substring(2,4);
           // secs = secs.substring(1,2);
            systemDate = (da + mont + yr + hor + min + secs);
            return systemDate;
        } catch (Exception e) {
            return "01019170000000";
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
    


}
