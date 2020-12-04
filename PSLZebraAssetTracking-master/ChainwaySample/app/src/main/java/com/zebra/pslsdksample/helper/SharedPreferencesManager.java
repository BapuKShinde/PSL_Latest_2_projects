package com.zebra.pslsdksample.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String SHARED_PREF_NAME = "ASSET_SHARED_PREF";


    private static final String USER = "USER_NAME";


    private static final String HOST_URL = "HOST_URL";
    private static final String CAST_HOST_URL = "CAST_HOST_URL";
    private static final String APPLICATION_VERSION = "APPLICATION_VERSION";
    private static final String IS_HOST_CONFIG = "IS_HOST_CONFIG";
    private static final String IS_CAST_HOST_CONFIG = "IS_CAST_HOST_CONFIG";
    private static final String IS_LOGIN_SAVED = "IS_LOGIN_SAVED";
    private static final String USER_ID = "USER_ID";
    private static final String USER_PASSWORD = "USER_PASSWORD";

    private SharedPreferencesManager() {}

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }



    public static String getUserName(Context context) {
        return getSharedPreferences(context).getString(USER , "");
    }

    public static void setUserName(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER , newValue);
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

    public static String getCastHostUrl(Context context) {
        return getSharedPreferences(context).getString(CAST_HOST_URL , "");
    }

    public static void setCastHostUrl(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(CAST_HOST_URL , newValue);
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

    public static boolean getIsCastHostConfig(Context context) {
        return getSharedPreferences(context).getBoolean(IS_CAST_HOST_CONFIG , false);
    }

    public static void setIsCastHostConfig(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_CAST_HOST_CONFIG , newValue);
        editor.commit();
    }


    public static boolean getLoginSaved(Context context) {
        return getSharedPreferences(context).getBoolean(IS_LOGIN_SAVED , false);
    }

    public static void setLoginSaved(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_LOGIN_SAVED , newValue);
        editor.commit();
    }

    public static void setUserID(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER_ID , newValue);
        editor.commit();
    }

    public static String getUserID(Context context) {
        return getSharedPreferences(context).getString(USER_ID , "");
    }

    public static void setUserPassword(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER_PASSWORD , newValue);
        editor.commit();
    }

    public static String getUserPassword(Context context) {
        return getSharedPreferences(context).getString(USER_PASSWORD , "");
    }


}
