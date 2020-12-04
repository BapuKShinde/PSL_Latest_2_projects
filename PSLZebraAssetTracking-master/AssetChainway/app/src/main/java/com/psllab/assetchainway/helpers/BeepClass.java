package com.psllab.assetchainway.helpers;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.psllab.assetchainway.R;


/**
 * Created by Admin on 06/Nov/2017.
 */

public class BeepClass {
    public static void successbeepold(Context context){
        try {

            MediaPlayer sound1 = MediaPlayer.create(context, R.raw.successbeep);
            if (sound1.isPlaying() == true) {
                sound1.pause();
            } else {
                sound1.start();
            }
            sound1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                }
            });
        }catch (Exception e){

        }
    }

    public static void successbeep(Context context){
        try {

            MediaPlayer sound1 = MediaPlayer.create(context, R.raw.tagtone);
            if (sound1.isPlaying() == true) {
                sound1.pause();
            } else {
                sound1.start();
            }
            sound1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                }
            });
        }catch (Exception e){

        }
    }

    public static void beep40(Context context){
        try {
            MediaPlayer sound1 = MediaPlayer.create(context, R.raw.blep_40ms);
            if (sound1.isPlaying() == true) {
                sound1.pause();
            } else {
                sound1.start();
            }
            sound1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                }
            });
        }catch (Exception e){

        }
    }

    public static void beep100(Context context){
        try {

            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
                }

            MediaPlayer sound1 = MediaPlayer.create(context, R.raw.blep_100ms);
            if (sound1.isPlaying() == true) {
                sound1.pause();
            } else {
                sound1.start();
            }
            sound1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                }
            });
        }catch (Exception e){

        }
    }
    public static void beep300(Context context){
        try {
            MediaPlayer sound1 = MediaPlayer.create(context, R.raw.blep_300ms);
            if (sound1.isPlaying() == true) {
                sound1.pause();
            } else {
                sound1.start();
            }
            sound1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                }
            });
        }catch (Exception e){

        }
    }


    public static void errorbeep(Context context){
        try {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            MediaPlayer sound1 = MediaPlayer.create(context, R.raw.errorbeep1);
            if (sound1.isPlaying() == true) {
                sound1.pause();
            } else {
                sound1.start();
            }
            sound1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                }
            });
        }catch (Exception e){

        }
    }


    public static void mutebeep(Context context){
        try {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            MediaPlayer sound1 = MediaPlayer.create(context, R.raw.mute);
            if (sound1.isPlaying() == true) {
                sound1.pause();
            } else {
                sound1.start();
            }
            sound1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                }
            });
        }catch (Exception e){

        }
    }



}
