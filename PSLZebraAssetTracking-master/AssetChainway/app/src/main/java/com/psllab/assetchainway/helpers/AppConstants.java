package com.psllab.assetchainway.helpers;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by Admin on 07/Jun/2018.
 */

public class AppConstants {
    //public static final String HOST_URL = "http://192.168.0.23:3004";//public
    public static final String HOST_URL = "http://192.168.0.20:9000";//public
    //public static final String HOST_URL = "http://35.162.46.102:3004";//public

    public static final String GET_USERS = "/api/user";//public

    public static final String GET_ASSET_MASTER = "/api/AssetMasters";//public
    public static final String POST_TAG_DETAILS = "/api/Sensors";//public
    public static final String POST_INVENTORY = "/api/PostInventotyData";//public




    //UHF PROPERTY
    public static final String K_ACCESS_PASSWORD = "00000000";
    public static final String K_ACCESS_MEMORY = "TID";
    public static final String K_ACCESS_START = "0";
    public static final String K_ACCESS_LENGTH = "32";
    public static final String K_ACCESS_LENGTH_TID = "6";
    public static final String K_ACCESS_MEMORY_USER = "USER";
    public static final String K_ACCESS_FAKE = "ABCDEFGHIJKLMNOP";

    public static final String K_ACCESS_MEMORY_EPC = "UII";


    //INT CONSTANTS
    public static final int BATTERY_LIMIT = 5;
    public static final int UHF_MINIMUM_POWER_LIMIT = 5;
    public static final int BEEP_TIMER_LIMIT = 1000;
    public static final int TIME_OUT = 20;

    public static final int DIALOG_AUTO_CLOSE_TIME = 4000;


    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            builder.connectTimeout(40, TimeUnit.SECONDS);
            builder.readTimeout(40, TimeUnit.SECONDS);
            builder.writeTimeout(40, TimeUnit.SECONDS);


            OkHttpClient okHttpClient = builder.build();

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
