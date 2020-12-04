package com.zebra.pslsdksample;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.zebra.pslsdksample.databases.DatabaseHandler;
import com.zebra.pslsdksample.helper.APIKeys;
import com.zebra.pslsdksample.helper.AppConstants;
import com.zebra.pslsdksample.helper.ApplicationCommonMethods;
import com.zebra.pslsdksample.helper.ConnectionDetector;
import com.zebra.pslsdksample.helper.SharedPreferencesManager;
import com.zebra.pslsdksample.modals.CastDetail;
import com.zebra.pslsdksample.modals.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class CastURLConfigActivity extends AppCompatActivity {
    private Context context = this;
    ProgressDialog progressDialog;
    private String HOST_URL;
    private boolean host_config = false;
    private EditText edtUrl;
    private Button btnConfig,btnClear2;
    private ConnectionDetector cd;
    private DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_u_r_l_config);
        findViews();

        cd = new ConnectionDetector(context);
        db = new DatabaseHandler(context);

        HOST_URL = SharedPreferencesManager.getCastHostUrl(context);
        host_config = SharedPreferencesManager.getIsCastHostConfig(context);

        edtUrl.setText(HOST_URL);


        btnClear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtUrl.setText(HOST_URL);
            }
        });
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtUrl.getText().toString().length()<8){
                    showCustomErrorDialog(context,getResources().getString(R.string.enter_config_url));
                }else{


                    if(cd.isConnectingToInternet()){
                        String url = edtUrl.getText().toString().trim();
                        GetTransactionMaster("Please Wait...\nValidating URL", ApplicationCommonMethods.getSystemDateTimeInFormatt(),url);
                    }else{
                        Toast.makeText(context,context.getResources().getString(R.string.internet_error),Toast.LENGTH_SHORT).show();
                    }



                }
            }
        });

    }




    /**
     * method to get transactions data from server
     * */
    public  void GetTransactionMaster(String progress_message,String date,final String url) {

        showProgress(context,progress_message);
       /* OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .build();*/

        String URL = AppConstants.BASE_HOST + AppConstants.GET_TRANSACTION_TYPES;

       // URL = url  + AppConstants.GET_USER;

        URL = url + AppConstants.GET_CASTS;

        OkHttpClient okHttpClient = AppConstants.getUnsafeOkHttpClient();
        Log.e("URL",URL);
        //Log.e("URL",AppConstants.HOST_URL + AppConstants.UPLOAD_INVENTORY_DETAILS);
        //AndroidNetworking.post(AppConstants.BASE_HOST + METHOD_NAME).addJSONObjectBody(jsonobject3)
        AndroidNetworking.get(URL)//.addJSONObjectBody(jsonobject3)
                //AndroidNetworking.get(AppConstants.BASE_HOST + AppConstants.GET_TRANSACTION_TYPES)//.addJSONObjectBody(jsonobject3)
                .setTag("test")
                .setPriority(Priority.LOW)
                .setOkHttpClient(okHttpClient) // passing a custom okHttpClient
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject result) {
                        hideProgressDialog();
                        if (result != null) {

                            try {
                                Log.e("RES",result.toString());
                                String success = result.getString("status");
                                String error = result.getString("message");
                                List<CastDetail> transactionTypesList = new ArrayList<>();
                                if(success.equals("true")){

                                    JSONArray dataArray = result.getJSONArray("data");
                                    for(int i=0;i<dataArray.length();i++){

                                        JSONObject dataObject = dataArray.getJSONObject(i);
                                        CastDetail transactionTypes = new CastDetail();
                                        transactionTypes.setCast_id(dataObject.getString(APIKeys.CAST_ID));
                                        transactionTypes.setCast_Prefix(dataObject.getString(APIKeys.CAST_PREFIX));
                                        transactionTypes.setCast_no(dataObject.getString(APIKeys.CAST_NO));
                                        transactionTypes.setCast_location(dataObject.getString(APIKeys.CAST_LOCATION));
                                        transactionTypes.setCast_task_id(dataObject.getString(APIKeys.CAST_TASK_ID));
                                        transactionTypes.setCast_task_no(dataObject.getString(APIKeys.CAST_TASK_NO));

                                        transactionTypesList.add(transactionTypes);
                                    }
                                    db.deleteCastMaster();
                                    db.storeCastMaster(transactionTypesList);


                                }else{
                                    Toast.makeText(context,error,Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("RES",e.getMessage());
                            }

                            showCustomSuccessConfirmationDialog(context,getResources().getString(R.string.url_validation),url);
                        } else {
                            Toast.makeText(context,context.getResources().getString(R.string.communication_error),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideProgressDialog();
                        Log.e("ERR",anError.getErrorDetail());
                        Toast.makeText(context,context.getResources().getString(R.string.inactive_internet),Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void showProgress(Context context, String progress_message){
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(progress_message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //  progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
    /**
     * method to hide Progress Dialog
     * */
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
    private void findViews() {
        edtUrl = (EditText)findViewById( R.id.edtUrl );
        btnClear2 = (Button)findViewById( R.id.btnClear2);
        btnConfig = (Button)findViewById( R.id.btnConfig);


    }

    Dialog dialog,dialog2;
    public void showCustomErrorDialog(Context context, String msg) {
        if(dialog!=null){
            dialog.dismiss();
        }
        if(dialog2!=null){
            dialog2.dismiss();
        }
        dialog = new Dialog(context);
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
    public  void showCustomSuccessDialog(Context context, String msg){
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
                finish();
            }
        });
        dialog.show();
    }

    Dialog dialogsuccess;
    public  void showCustomSuccessConfirmationDialog(final Context context, String msg,final String url){
        if(dialogsuccess!=null){
            dialogsuccess.dismiss();
        }
        dialogsuccess = new Dialog(context);
        dialogsuccess.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogsuccess.setCancelable(false);
        dialogsuccess.setContentView(R.layout.custom_alert_success_confirmation_dialog_layout);
        TextView text = (TextView) dialogsuccess.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) dialogsuccess.findViewById(R.id.btn_dialog);
        Button dialogCancel = (Button) dialogsuccess.findViewById(R.id.btnCancel);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogsuccess.dismiss();
                SharedPreferencesManager.setIsCastHostConfig(context,true);
                SharedPreferencesManager.setCastHostUrl(context,url);
                HOST_URL = url;
                showCustomSuccessDialog(context,getResources().getString(R.string.url_config_success));
            }
        });
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogsuccess.dismiss();

            }
        });
        dialogsuccess.show();
    }
}