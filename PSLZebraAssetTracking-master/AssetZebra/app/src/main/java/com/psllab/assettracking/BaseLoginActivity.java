package com.psllab.assettracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.psllab.assettracking.helpers.SharedPreferencesManager;

public class BaseLoginActivity extends AppCompatActivity {

    private Context context= this;
    private String device_imei,device_id;
    private boolean hasPermissions = false;
    private String androidID = "A";

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        device_imei = getUniqueIMEI();

        SharedPreferencesManager.setDeviceID(context,device_id);
        SharedPreferencesManager.setDeviceImei(context,device_imei);

        if(device_imei.equals("A")){
            showCustomErrorFinishDialog("Device IMEI Read permission not enabled, Enable from device setting");
        }


        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        else{
            //  androidID=getUniqueIMEI();
            androidID = getUniqueIMEI();
            SharedPreferencesManager.setDeviceImei(context,androidID);
            androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            SharedPreferencesManager.setDeviceID(context,androidID);
        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (!hasPermissions(this, PERMISSIONS)) {

        }
        else{
            androidID = getUniqueIMEI();
            SharedPreferencesManager.setDeviceImei(context,androidID);
            androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            SharedPreferencesManager.setDeviceID(context,androidID);
        }

    }

    public String getUniqueIMEI() {
        String imei = "A";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return "A";
            }
            imei = telephonyManager.getDeviceId();
            //Log.e("imei", "=" + imei);
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return android.os.Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "A";
    }


    public void showCustomErrorFinishDialog(String msg){
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
        dialog.getWindow().getAttributes().windowAnimations = R.style.FadeInOutAnimation;
        dialog.show();
    }


}