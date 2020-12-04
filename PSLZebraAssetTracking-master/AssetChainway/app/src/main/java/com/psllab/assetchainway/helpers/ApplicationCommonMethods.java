package com.psllab.assetchainway.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


import com.psllab.assetchainway.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Bapusaheb Shinde on 02/Jan/2018.
 */

public class ApplicationCommonMethods {
    public static int getPercentage(int value) {
        int a = 0;
        switch (value) {

            case 39:
                a = 99;
                break;
            case 40:
                a = 98;
                break;
            case 41:
                a = 97;
                break;
            case 42:
                a = 96;
                break;
            case 43:
                a = 94;
                break;
            case 44:
                a = 92;
                break;
            case 45:
                a = 90;
                break;
            case 46:
                a = 89;
                break;
            case 47:
                a = 87;
                break;
            case 48:
                a = 85;
                break;
            case 49:
                a = 84;
                break;
            case 50:
                a = 82;
                break;
            case 51:
                a = 79;
                break;
            case 52:
                a = 75;
                break;
            case 53:
                a = 72;
                break;
            case 54:
                a = 70;
                break;
            case 55:
                a = 67;
                break;
            case 56:
                a = 65;
                break;
            case 57:
                a = 62;
                break;
            case 58:
                a = 60;
                break;
            case 59:
                a = 57;
                break;
            case 60:
                a = 54;
                break;
            case 61:
                a = 51;
                break;
            case 62:
                a = 48;
                break;
            case 63:
                a = 43;
                break;
            case 64:
                a = 40;
                break;
            case 65:
                a = 36;
                break;
            case 66:
                a = 33;
                break;
            case 67:
                a = 31;
                break;
            case 68:
                a = 29;
                break;
            case 69:
                a = 27;
                break;
            case 70:
                a = 25;
                break;
            case 71:
                a = 23;
                break;
            case 72:
                a = 21;
                break;
            case 73:
                a = 19;
                break;
            case 74:
                a = 17;
                break;
            case 75:
                a = 15;
                break;
            case 76:
                a = 13;
                break;
            case 77:
                a = 11;
                break;
            case 78:
                a = 10;
                break;
            case 79:
                a = 8;
                break;
            case 80:
                a = 7;
                break;
            case 81:
                a = 6;
                break;
            case 82:
                a = 5;
                break;
            case 83:
                a = 4;
                break;
            case 84:
                a = 3;
                break;
            case 85:
                a = 2;
                break;
            case 86:
                a = 1;
                break;
        }
        return a;
    }


    static Dialog errordialog, successdialog;

    public static void showCustomErrorDialog(Context context, String msg) {
        if (errordialog != null) {
            errordialog.dismiss();
        }
        if (successdialog != null) {
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
        errordialog.getWindow().getAttributes().windowAnimations = R.style.FadeInOutAnimation;
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                errordialog.show();
            }
        }
    }

    public static void showCustomSuccessDialog(Context context, String msg) {
        if (errordialog != null) {
            errordialog.dismiss();
        }
        if (successdialog != null) {
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
        successdialog.getWindow().getAttributes().windowAnimations = R.style.FadeInOutAnimation;
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                successdialog.show();
            }
        }

    }

    /**
     * Common method for showing custom alert error type dialog
     */
   /* public static void showCustomErrorDialog(Context context, String msg) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_dialog_layout);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    *//**
     * Common method for showing custom alert success type dialog
     *//*
    public static void showCustomSuccessDialog(Context context, String msg) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_success_dialog_layout);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }*/

    public static String stringToHex(String string) {
        StringBuilder buf = new StringBuilder(200);
        for (char ch: string.toCharArray()) {
            if (buf.length() > 0)
                buf.append(' ');
            buf.append(String.format("%02x", (int) ch));
        }
        return buf.toString();
    }
    /**
     * Common method for showing custom alert success type dialog
     */
    public static void showCustomSuccessSplashDialog(Context context, String msg) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_success_dialog_layout);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setVisibility(View.GONE);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                dialog.dismiss();// when the task active then close the dialog
                t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
            }
        }, 2000);
    }


    public static String getSystemDateTime1() {
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
            if (mont.trim().equals("1")) {
                mont = "Jan";
            }
            if (mont.trim().equals("2")) {
                mont = "Feb";
            }
            if (mont.trim().equals("3")) {
                mont = "Mar";
            }
            if (mont.trim().equals("4")) {
                mont = "Apr";
            }
            if (mont.trim().equals("5")) {
                mont = "May";
            }
            if (mont.trim().equals("6")) {
                mont = "Jun";
            }
            if (mont.trim().equals("7")) {
                mont = "Jul";
            }
            if (mont.trim().equals("8")) {
                mont = "Aug";
            }
            if (mont.trim().equals("9")) {
                mont = "Sep";
            }
            if (mont.trim().equals("10")) {
                mont = "Oct";
            }
            if (mont.trim().equals("11")) {
                mont = "Nov";
            }
            if (mont.trim().equals("12")) {
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
            if (mont.trim().equals("1")) {
                mont = "01";
            }
            if (mont.trim().equals("2")) {
                mont = "02";
            }
            if (mont.trim().equals("3")) {
                mont = "03";
            }
            if (mont.trim().equals("4")) {
                mont = "04";
            }
            if (mont.trim().equals("5")) {
                mont = "05";
            }
            if (mont.trim().equals("6")) {
                mont = "06";
            }
            if (mont.trim().equals("7")) {
                mont = "07";
            }
            if (mont.trim().equals("8")) {
                mont = "08";
            }
            if (mont.trim().equals("9")) {
                mont = "09";
            }
            if (mont.trim().equals("10")) {
                mont = "10";
            }
            if (mont.trim().equals("11")) {
                mont = "11";
            }
            if (mont.trim().equals("12")) {
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
            systemDate = (da + mont + yr + hor + min + secs);
            return systemDate;
        } catch (Exception e) {
            return "01011970000000";
        }
    }
    public static String getSystemDateTimeInFormat() {
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
            if (mont.trim().equals("1")) {
                mont = "01";
            }
            if (mont.trim().equals("2")) {
                mont = "02";
            }
            if (mont.trim().equals("3")) {
                mont = "03";
            }
            if (mont.trim().equals("4")) {
                mont = "04";
            }
            if (mont.trim().equals("5")) {
                mont = "05";
            }
            if (mont.trim().equals("6")) {
                mont = "06";
            }
            if (mont.trim().equals("7")) {
                mont = "07";
            }
            if (mont.trim().equals("8")) {
                mont = "08";
            }
            if (mont.trim().equals("9")) {
                mont = "09";
            }
            if (mont.trim().equals("10")) {
                mont = "10";
            }
            if (mont.trim().equals("11")) {
                mont = "11";
            }
            if (mont.trim().equals("12")) {
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
            systemDate = (yr +"-"+ mont + "-"+da +" "+ hor +":"+ min +":"+ secs);
            return systemDate;
        } catch (Exception e) {
          //  return "01011970000000";
            return "1970-01-01 00:00:00";
            //1970-01-01 00:00:00
        }
    }


    public static String getTime() {
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
            if (mont.trim().equals("1")) {
                mont = "01";
            }
            if (mont.trim().equals("2")) {
                mont = "02";
            }
            if (mont.trim().equals("3")) {
                mont = "03";
            }
            if (mont.trim().equals("4")) {
                mont = "04";
            }
            if (mont.trim().equals("5")) {
                mont = "05";
            }
            if (mont.trim().equals("6")) {
                mont = "06";
            }
            if (mont.trim().equals("7")) {
                mont = "07";
            }
            if (mont.trim().equals("8")) {
                mont = "08";
            }
            if (mont.trim().equals("9")) {
                mont = "09";
            }
            if (mont.trim().equals("10")) {
                mont = "10";
            }
            if (mont.trim().equals("11")) {
                mont = "11";
            }
            if (mont.trim().equals("12")) {
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
            systemDate = (hor + ":" +min );
            return systemDate;
        } catch (Exception e) {
            return "00:00";
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void showGPSDisabledAlertToUser(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Please turn on GPS")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static void showGPSEnaabledAlertToUser(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("GPS is enable in your device. Would you like to disable it?")
                .setCancelable(false)
                .setPositiveButton("Disable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}
