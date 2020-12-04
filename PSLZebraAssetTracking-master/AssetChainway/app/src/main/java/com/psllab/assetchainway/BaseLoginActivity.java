package com.psllab.assetchainway;

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

import com.psllab.assetchainway.helpers.SharedPreferencesManager;

public class BaseLoginActivity extends AppCompatActivity {
    private Context context= this;
    private String device_imei,device_id;
    private boolean hasPermissions = false;
    private String androidID = "A";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        //device_imei = getUniqueIMEI();

        SharedPreferencesManager.setDeviceID(context,device_id);
        SharedPreferencesManager.setDeviceImei(context,device_imei);

     /*   if(device_imei.equals("A")){
            showCustomErrorFinishDialog("Device IMEI Read permission not enabled, Enable from device setting");
        }*/



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