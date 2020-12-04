package com.zebra.rfid.demo.pslsdksample;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.stetho.Stetho;
import com.zebra.rfid.demo.pslsdksample.databases.DatabaseHandler;
import com.zebra.rfid.demo.pslsdksample.helper.APIKeys;
import com.zebra.rfid.demo.pslsdksample.helper.AppConstants;
import com.zebra.rfid.demo.pslsdksample.helper.ApplicationCommonMethods;
import com.zebra.rfid.demo.pslsdksample.helper.ConnectionDetector;
import com.zebra.rfid.demo.pslsdksample.helper.SharedPreferencesManager;
import com.zebra.rfid.demo.pslsdksample.modals.TransactionTypes;
import com.zebra.rfid.demo.pslsdksample.modals.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class LoginActivity extends AppCompatActivity {

    private EditText edtLoginId,edtPassword;
    private Button btnLogin,btnClear;
    private Context context = this;
    private ConnectionDetector cd;
    private DatabaseHandler db;
    private ImageView btnSyncTransactions;

    private CheckBox chkRemember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_login);

        cd = new ConnectionDetector(context);
        cd = new ConnectionDetector(context);
        db = new DatabaseHandler(context);
        findViews();

        if(SharedPreferencesManager.getLoginSaved(context)){
            edtLoginId.setText(SharedPreferencesManager.getUserID(context));
            edtPassword.setText(SharedPreferencesManager.getUserPassword(context));
        }

        btnSyncTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cd.isConnectingToInternet()){
                    GetTransactionMaster("Please Wait...\nGetting Users", ApplicationCommonMethods.getSystemDateTimeInFormatt());
                }else{
                    Toast.makeText(context,context.getResources().getString(R.string.internet_error),Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = edtLoginId.getText().toString();
                String pass = edtPassword.getText().toString();
                if(email.equals("")){
                    Toast.makeText(context,"Enter valid user ID",Toast.LENGTH_SHORT).show();
                }else if(pass.equals("")){
                    Toast.makeText(context,"Enter valid password",Toast.LENGTH_SHORT).show();
                }else {
                    //TODO do something after login
                    if(db.getUserCount()> 0){
                        String data = db.getUserActive(email,pass);
                        if(data.equalsIgnoreCase("")){
                            Toast.makeText(context,"Invalid user",Toast.LENGTH_SHORT).show();
                        }else if(data.equalsIgnoreCase("true")){
                            String username = db.getUserFirstName(email,pass);


                            if(chkRemember.isChecked()){
                                SharedPreferencesManager.setLoginSaved(context,true);
                                SharedPreferencesManager.setUserID(context,email);
                                SharedPreferencesManager.setUserPassword(context,pass);
                            }
                            Intent dashboardIntent = new Intent(LoginActivity.this,DashboardActivity.class);
                            dashboardIntent.putExtra("user",username);
                            SharedPreferencesManager.setUserName(context,username);
                            startActivity(dashboardIntent);
                        }else if(data.equalsIgnoreCase("false")){
                            Toast.makeText(context,"Inactive user",Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(context,"User list not found , please sync and try again",Toast.LENGTH_LONG).show();
                    }
                    /*if(cd.isConnectingToInternet()){

                        if(progressDialog!=null){
                            progressDialog.dismiss();
                        }
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Please Wait\nAuthenticating user");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        try {
                            JSONObject post = new JSONObject();
                            post.put("user_name", email);
                            post.put("password", pass);
                            userLoginAPi(post);


                        } catch (JSONException e) {

                        }

                    }
                    else{
                        Toast.makeText(context,"Network connection error",Toast.LENGTH_SHORT).show();
                    }*/



                }
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtLoginId.setText("");
                edtPassword.setText("");
            }
        });

    }

    private void findViews() {
        chkRemember = (CheckBox) findViewById(R.id.chkRemember);
        edtLoginId = (EditText) findViewById(R.id.edtLoginId);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnSyncTransactions = (ImageView) findViewById(R.id.btnSyncTransactions);
    }



    ProgressDialog progressDialog;
    public void userLoginAPi(final JSONObject jsonObject){


        String loginurl = SharedPreferencesManager.getHostUrl(context)+AppConstants.LOGIN_METHOD;
        Log.e("LOGINREQUEST",loginurl);
        //  AndroidNetworking.get(loginurl)

        AndroidNetworking.post(loginurl)
                .addJSONObjectBody(jsonObject) // posting json
                //.addQueryParameter(jsonObject)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                //.setOkHttpClient(okHttpClient)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(progressDialog!=null){
                            progressDialog.dismiss();
                        }
                        // do anything with response
                        if(response!=null){

                            Log.e("RES",response.toString());
                            try {
                                boolean success = response.getBoolean("success");
                                if(success){


                                    //TODO do something after login
                                    Intent dashboardIntent = new Intent(LoginActivity.this,DashboardActivity.class);
                                    dashboardIntent.putExtra("user",edtLoginId.getText().toString().trim());
                                    startActivity(dashboardIntent);

                                }else{
                                    String error = response.getString("error");
                                    Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                                }


                            }catch (JSONException e){
                                Toast.makeText(context,"Server Error, Please try again.",Toast.LENGTH_SHORT).show();

                            }
                        }else{
                            Toast.makeText(context,"Server Error, Please try again..",Toast.LENGTH_SHORT).show();

                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        if(progressDialog!=null){
                            progressDialog.dismiss();
                            Log.e("ERROR",error.getErrorDetail());
                            Toast.makeText(context,"Server Error, Please try again...",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    /**
     * method to get transactions data from server
     * */
    public  void GetTransactionMaster(String progress_message,String date) {

        showProgress(context,progress_message);
       /* OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .build();*/

        String URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_TRANSACTION_TYPES;
        if(db.getUserCount()>0){
            URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_USER + db.getUserMaxDate();
        }else{
            URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_USER;
        }
        OkHttpClient okHttpClient = AppConstants.getUnsafeOkHttpClient();
        Log.e("URL",URL);
        //Log.e("URL",AppConstants.HOST_URL + AppConstants.UPLOAD_INVENTORY_DETAILS);
        //AndroidNetworking.post(SharedPreferencesManager.getHostUrl(context) + METHOD_NAME).addJSONObjectBody(jsonobject3)
        AndroidNetworking.get(URL)//.addJSONObjectBody(jsonobject3)
                //AndroidNetworking.get(SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_TRANSACTION_TYPES)//.addJSONObjectBody(jsonobject3)
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
                                List<User> transactionTypesList = new ArrayList<>();
                                if(success.equals("true")){

                                    JSONArray dataArray = result.getJSONArray("data");
                                    for(int i=0;i<dataArray.length();i++){

                                        JSONObject dataObject = dataArray.getJSONObject(i);
                                        User transactionTypes = new User();
                                        transactionTypes.setUser_id(dataObject.getString(APIKeys.USER_ID));
                                        transactionTypes.setUser_name(dataObject.getString(APIKeys.USER_NAME));
                                        transactionTypes.setUser_password(dataObject.getString(APIKeys.USER_PASSWORD));
                                        transactionTypes.setUser_firstname(dataObject.getString(APIKeys.USER_FIRSTNAME));
                                        transactionTypes.setUser_lastname(dataObject.getString(APIKeys.USER_USER_LASTNAME));
                                        transactionTypes.setUser_roleid(dataObject.getString(APIKeys.USER_ROLEID));


                                        // transactionTypes.setTransaction_modified_date(dataObject.getString(APIKeys.TRANSACTION_MODIFIED_DATE_TIME));

                                        String date = (dataObject.getString(APIKeys.USER_MODIFIED_DATE_TIME));
                                        date = date.replace('T',' ');

                                        if(date.contains(".")){
                                            if(date.length()>19){
                                                date = date.substring(0,19);
                                            }else{
                                                date = date.split(".")[0];
                                            }
                                        }
                                        transactionTypes.setUser_modified_date(date);


                                        transactionTypes.setUser_is_active(dataObject.getString(APIKeys.USER_IS_ACTIVE));
                                        transactionTypesList.add(transactionTypes);
                                    }
                                    // db.deleteTransactionMaster();
                                    db.storeUserMaster(transactionTypesList);
                                }else{
                                    Toast.makeText(context,error,Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("RES",e.getMessage());
                            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // device_imei = getUniqueIMEI();

        switch (id){

            case R.id.config:
                showPinDialog(context,"Enter Valid Pin");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Dialog pinDialog;
    public void showPinDialog(final Context context, String msg){
        pinDialog = new Dialog(context);
        pinDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pinDialog.setCancelable(false);
        pinDialog.setContentView(R.layout.custom_alert_dialog_layout);
        TextView text = (TextView) pinDialog.findViewById(R.id.text_dialog);
        final EditText edtPin = (EditText) pinDialog.findViewById(R.id.edtPin);
        edtPin.setVisibility(View.VISIBLE);
        text.setText(msg);
        Button dialogButton = (Button) pinDialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinDialog.dismiss();
                if(edtPin.getText().toString().trim().equalsIgnoreCase("2020")){
                    Intent i = new Intent(LoginActivity.this,URLConfigActivity.class);
                    startActivity(i);
                }else{
                    ApplicationCommonMethods.showCustomErrorDialog(context,"Invalid Pin");
                }
            }
        });
        pinDialog.getWindow().getAttributes().windowAnimations = R.style.FadeInOutAnimation;
        pinDialog.show();
    }

}
