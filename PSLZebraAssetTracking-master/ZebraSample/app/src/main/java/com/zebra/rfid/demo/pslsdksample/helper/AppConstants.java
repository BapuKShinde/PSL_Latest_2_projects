package com.zebra.rfid.demo.pslsdksample.helper;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class AppConstants {


    public static int DIALOG_DISMISS_COUNT = 3000;
    public static String DESTINATION_POINT_UPLOAD_TOUCH_POINT = "D";//Production "D"
    public static String CAST_ASSIGNMENT_UPLOAD_TOUCH_POINT = "M";//Production "M"
    public static String LOAD_UPLOAD_TOUCH_POINT = "M";//Production "M"

    //public static final String BASE_HOST = "http://35.162.46.102/shabashi/api/PslShabashi/";
    public static final String BASE_HOST = "https://trucktoken.sesavedanta.co.in/OTSService";
    //public static final String BASE_HOST = "http://14.142.30.7/OTSService/";
    //public static final String LOGIN_METHOD = "Login?user_name=";
    public static final String LOGIN_METHOD = "/Login";
    public static final String GET_TRANSACTION_TYPES = "/PDA/GetTransactionTypes?ModifiedDateTime=";
    public static final String GET_USER = "/PDA/GetUsers?ModifiedDateTime=";
    public static final String GET_TASKS = "/PDA/GetTasks?ModifiedDateTime=";
    public static final String GET_CASTS = "/PDA/GetCastDetail";
    public static final String GET_SOURCE_PLANTS = "/PDA/GetSourcePlants";
    public static final String GET_LOCATIONS = "/PDA/GetLocations";
    public static final String GET_TOUCH_POINTS = "/PDA/GetTouchPoints";
    public static final String POST_TRIPDETAILS = "/Tripdetails";
    //http://14.142.30.7/OTSService/PDA/GetTasks?ModifiedDateTime=2020-01-20 11:04:53
    public static final int TIME_OUT = 60;
    //"UserName":"psladmin","Password":"psladmin!23"

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
