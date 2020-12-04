package com.psllab.assetchainway.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String SHARED_PREF_NAME = "SHARED_PREF";

    private static final String DEVICEIMEI = "DEVICE_IMEI";
    private static final String DEVICEID = "DEVICE_ID";

    private static final String LOGIN_USER_NAME = "LOGIN_USER_NAME";
    private static final String LOGIN_USER_PASSWORD = "LOGIN_USER_PASSWORD";
    private static final String IS_LOGIN_USER_REMEMBERED = "IS_LOGIN_USER_REMEMBERED";

    private static final String IS_UPDATE_AVAILABLE = "IS_UPDATE_AVAILABLE";
    private static final String IS_URL_CONFIGURED = "IS_URL_CONFIGURED";
    private static final String IS_HOST_CONFIG = "IS_HOST_CONFIG";
    private static final String HOST_URL = "HOST_URL";
    private static final String SAVEDPOWER = "SAVED_POWER";
    private static final String USERID = "USER_ID";

    private SharedPreferencesManager() {}

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static String getLoginID(Context context) {
        return getSharedPreferences(context).getString(LOGIN_USER_NAME , "");
    }

    public static void setLoginID(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(LOGIN_USER_NAME , newValue);
        editor.commit();
    }


    public static String getDeviceID(Context context) {
        return getSharedPreferences(context).getString(DEVICEID , "");
    }

    public static void setDeviceID(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(DEVICEID , newValue);
        editor.commit();
    }


    public static String getLoginPassword(Context context) {
        return getSharedPreferences(context).getString(LOGIN_USER_PASSWORD , "");
    }

    public static void setLoginPassword(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(LOGIN_USER_PASSWORD , newValue);
        editor.commit();
    }


    public static boolean getLoginRemember(Context context) {
        return getSharedPreferences(context).getBoolean(IS_LOGIN_USER_REMEMBERED , false);
    }

    public static void setLoginRemember(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_LOGIN_USER_REMEMBERED , newValue);
        editor.commit();
    }


    public static boolean isUpdateAvailable(Context context) {
        return getSharedPreferences(context).getBoolean(IS_UPDATE_AVAILABLE , false);
    }

    public static void setUpdateAvailable(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_UPDATE_AVAILABLE , newValue);
        editor.commit();
    }

    public static String getHostUrl(Context context) {
        return getSharedPreferences(context).getString(HOST_URL , "");
    }

    public static void setHostUrl(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(HOST_URL , newValue);
        editor.commit();
    }

    public static String getDeviceImei(Context context) {
        return getSharedPreferences(context).getString(DEVICEIMEI , "");
    }

    public static void setDeviceImei(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(DEVICEIMEI , newValue);
        editor.commit();
    }

    public static String getUserId(Context context) {
        return getSharedPreferences(context).getString(DEVICEIMEI , "0");
    }

    public static void setUserId(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(DEVICEIMEI , newValue);
        editor.commit();
    }


    public static int getSavedPower(Context context) {
        return getSharedPreferences(context).getInt(SAVEDPOWER , 0);
    }

    public static void setSavedPower(Context context, int newValue) {

        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SAVEDPOWER , newValue);
        editor.commit();
    }

    public static boolean getIsHostConfig(Context context) {
        return getSharedPreferences(context).getBoolean(IS_HOST_CONFIG , false);
    }

    public static void setIsHostConfig(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_HOST_CONFIG , newValue);
        editor.commit();
    }

}
