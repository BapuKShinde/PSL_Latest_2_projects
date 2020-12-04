package com.psllab.assetchainway.helpers;

import android.app.ProgressDialog;
import android.content.Context;

public class APIClass {

    static ProgressDialog progressDialog;

    /**
     * method to show Progress Dialog
     */
    public static void showProgress(Context context, String progress_message) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(progress_message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    /**
     * method to hide Progress Dialog
     */
    public static void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
