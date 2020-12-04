package com.zebra.pslsdksample.baseuhf;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rscja.deviceapi.RFIDWithUHF;
import com.zebra.pslsdksample.R;
import com.zebra.pslsdksample.helper.SharedPreferencesManager;

import java.util.HashMap;

public class BaseUHFActivity extends AppCompatActivity {

    // public Reader mReader;
    public boolean allow_to_press_trigger = true;
    public RFIDWithUHF mReader;
    HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    private SoundPool soundPool;
    private float volumnRatio;
    private AudioManager am;
    private Context context = this;
   // public GPSTracker gps;

    private boolean uhf_success = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // gps = new GPSTracker(context);
    }

    public void initUHF() {
        try {
            mReader = RFIDWithUHF.getInstance();
        } catch (Exception ex) {
            toastMessage(ex.getMessage());
            return;
        }

        if (mReader != null) {
            new InitTask().execute();
        }
    }

    public void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * �豸�ϵ��첽��
     *
     * @author liuruifeng
     */

    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        //ProgressDialog mypDialog;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return mReader.init();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(mypDialog!=null){
                mypDialog.dismiss();
            }
           // mypDialog.cancel();
            if (!result) {
                Toast.makeText(BaseUHFActivity.this, "init fail",
                        Toast.LENGTH_SHORT).show();
                uhf_success = false;
                allow_to_press_trigger = false;
            }else{
                mReader.setPower(30);
              //  SharedPreferencesManager.setSavedPower(context,30);
                uhf_success = true;
                new SetFilterTask().execute();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if(mypDialog!=null){
                mypDialog.dismiss();
            }
            mypDialog = new ProgressDialog(BaseUHFActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("Initializing Reader\nPlease wait");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }
    }

    @Override
    protected void onDestroy() {

        if(fdialog!=null){
            fdialog.dismiss();
        }
        if (mReader != null) {
            mReader.free();
        }
        if(mypDialog!=null){
            mypDialog.dismiss();
        }
     //   gps.stopUsingGPS();
        super.onDestroy();
    }


    public void initSound(){
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(this, R.raw.barcodebeep, 1));
        soundMap.put(2, soundPool.load(this, R.raw.serror, 1));
        am = (AudioManager) this.getSystemService(AUDIO_SERVICE);// 实例化AudioManager对象
    }
    /**
     * 播放提示音
     *
     * @param id 成功1，失败2
     */
    public void playSound(int id) {

        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 返回当前AudioManager对象的最大音量值
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);// 返回当前AudioManager对象的音量值
        volumnRatio = audioCurrentVolumn / audioMaxVolumn;
        try {
            soundPool.play(soundMap.get(id), volumnRatio, // 左声道音量
                    volumnRatio, // 右声道音量
                    0, // 优先级，0为最低
                    0, // 循环次数，0无不循环，-1无永远循环
                    1 // 回放速度 ，该值在0.5-2.0之间，1为正常速度
            );
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    ProgressDialog mypDialog;
    public class SetFilterTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
           //return mReader.setFilter(1, 0, "");//1 - UII,2-TID
           return mReader.setFilter(2, 0, "");//1 - UII,2-TID
           // return mReader.setFilter(RFIDWithUHF.BankEnum.UII,32, 0, "",false);//1 - UII,2-TID
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(mypDialog!=null){
                mypDialog.dismiss();
            }
           // mypDialog.cancel();
            if (!result) {
                uhf_success = false;
                allow_to_press_trigger = false;
                showCustomErrorFinishDialog("UHF Initialization Fail");
                //Toast.makeText(BaseUHFActivity.this, "init fail", Toast.LENGTH_SHORT).show();
            }else{
                uhf_success = true;
                allow_to_press_trigger = true;
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            if(mypDialog!=null){
                mypDialog.dismiss();
            }
            mypDialog = new ProgressDialog(BaseUHFActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("Initializing Reader\nPlease wait");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }
    }

    public void showCustomErrorSettingDialog(String msg){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_error_dialog_layout);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                // Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        dialog.getWindow().getAttributes().windowAnimations = R.style.FadeInOutAnimation;
        dialog.show();
    }


    Dialog fdialog;
    public void showCustomErrorFinishDialog(String msg){
        fdialog = new Dialog(context);
        fdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fdialog.setCancelable(false);
        fdialog.setContentView(R.layout.custom_alert_error_dialog_layout);
        TextView text = (TextView) fdialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) fdialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fdialog.dismiss();
                finish();
            }
        });
        fdialog.getWindow().getAttributes().windowAnimations = R.style.FadeInOutAnimation;
        fdialog.show();
    }

}
