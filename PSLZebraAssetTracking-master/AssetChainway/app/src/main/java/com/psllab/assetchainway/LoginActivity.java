package com.psllab.assetchainway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.stetho.Stetho;
import com.psllab.assetchainway.apihelpers.APIKeys;
import com.psllab.assetchainway.databases.DatabaseHandler;
import com.psllab.assetchainway.helpers.AppConstants;
import com.psllab.assetchainway.helpers.ApplicationCommonMethods;
import com.psllab.assetchainway.helpers.BeepClass;
import com.psllab.assetchainway.helpers.ConnectionDetector;
import com.psllab.assetchainway.helpers.GPSTracker;
import com.psllab.assetchainway.helpers.SharedPreferencesManager;
import com.psllab.assetchainway.modals.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class LoginActivity extends BaseLoginActivity implements View.OnClickListener {
    private LinearLayout llLogin;
    private EditText edtName;
    private EditText edtPassword;
    //private EditText edtProjectName;
    private CheckBox chkRemember;
    private Button btnLogin;
    private Button btnClear;
    private Button btnMenu;
    private ImageView imgUser;

    private Context context = this;
    private ConnectionDetector cd;
    private DatabaseHandler db;
    private GPSTracker gps;
    public String device_id;
    public String androidID = "A";


    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        findViews();

        cd = new ConnectionDetector(context);
        db = new DatabaseHandler(context);
        gps = new GPSTracker(context);
        //  CheckPermissionAndStartIntent();

        edtName.setText(SharedPreferencesManager.getLoginID(context));
        edtPassword.setText(SharedPreferencesManager.getLoginPassword(context));

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
   if(androidID.equals("A")){
            showCustomErrorFinishDialog("Device IMEI Read permission not enabled, Enable from device setting");
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

    private void findViews() {
        llLogin = (LinearLayout)findViewById( R.id.llLogin );
        edtName = (EditText)findViewById( R.id.edtName );
        edtPassword = (EditText)findViewById( R.id.edtPassword );
        //edtProjectName = (EditText)findViewById( R.id.edtProjectName );
        chkRemember = (CheckBox)findViewById( R.id.chkRemember );
        btnLogin = (Button)findViewById( R.id.btnLogin );
        btnClear = (Button)findViewById( R.id.btnClear );
        btnMenu = (Button)findViewById( R.id.btnMenu );
        imgUser = (ImageView) findViewById( R.id.imgUser );

        btnLogin.setOnClickListener( this );
        btnClear.setOnClickListener( this );
        imgUser.setOnClickListener( this );
        btnMenu.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        if ( v == btnLogin ) {
            // Handle clicks for btnLogin
           /* Intent i = new Intent(LoginActivity.this,DashboardActivity.class);
            startActivity(i);*/

          /*  Intent i = new Intent(LoginActivity.this,SearchActivity.class);
            startActivity(i);*/


          /*  Intent i = new Intent(LoginActivity.this,AssetInventoryActivity.class);
            startActivity(i);*/

           /* Intent i = new Intent(LoginActivity.this,SingleAssetScanActivity.class);
            startActivity(i);*/


              handleLoginClick(v);

        } else if ( v == btnClear ) {
            // Handle clicks for btnClear
            handleClearClick(v);
        }else if ( v == btnMenu ) {
            // Handle clicks for btnClear
            // handleClearClick(v);
            showPinDialog(context,"Enter Valid Pin");
        }else if ( v == imgUser ) {
            // Handle clicks for btnClear
            if(SharedPreferencesManager.getIsHostConfig(context)){
                ApplicationCommonMethods.showCustomErrorDialog(context, "Please Config URL From Menu Option");
            }else{
                handleUserSyncClick(v);
            }

        }


    }

    private void handleLoginClick(View v) {


        if(db.getUserCount()>0){
            if(edtName.getText().toString().equals("")){
                showCustomErrorDialog(context,"Please enter user name");
            }else if(edtPassword.getText().toString().equals("")){
                showCustomErrorDialog(context,"Please enter user password");
            }else {
                String username = edtName.getText().toString().trim();
                String paswd = edtPassword.getText().toString().trim();
                String act =db.getUserActive(edtName.getText().toString().trim(),edtPassword.getText().toString().trim());
                Log.e("ISACTIVE",act);

                if(db.getUserNameActive(edtName.getText().toString().trim(), edtPassword.toString().trim()).equalsIgnoreCase("true")){

                    if(chkRemember.isChecked()){
                        SharedPreferencesManager.setLoginID(context,username);
                        SharedPreferencesManager.setLoginPassword(context,paswd);
                    }else{
                        SharedPreferencesManager.setLoginID(context,"");
                        SharedPreferencesManager.setLoginPassword(context,"");
                    }

                    Intent i = new Intent(LoginActivity.this,DashboardActivity.class);
                    // Intent i = new Intent(LoginActivity.this,RegistrationActivity.class);
                    i.putExtra("usr", edtName.getText().toString().trim());
                    SharedPreferencesManager.setUserId(context,db.getUserID(username,paswd));
                    startActivity(i);
                }else if(db.getUserNameActive(edtName.getText().toString().trim(), edtPassword.toString().trim()).equalsIgnoreCase("false")){

                    ApplicationCommonMethods.showCustomErrorDialog(context, "Inactive User");
                }else if(db.getUserNameActive(edtName.getText().toString().trim(), edtPassword.toString().trim()).equalsIgnoreCase("")){

                    ApplicationCommonMethods.showCustomErrorDialog(context, "Invalid User Name or Password");
                }else{
                    ApplicationCommonMethods.showCustomErrorDialog(context, "Invalid User Name or Password");
                }
            }

        }else{
            ApplicationCommonMethods.showCustomErrorDialog(context, "Please Sync User Master");
        }


    }

    static ProgressDialog progressDialog;
    /**
     * method to show Progress Dialog
     * */

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
    /**
     * method to UPLOAD inventory data to server
     * */
    public void CallLoginAPI(final Context context, final JSONObject jsonobject3, String METHOD_NAME, String progress_message) {

        showProgress(context,progress_message);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .build();
        Log.e("URL", SharedPreferencesManager.getHostUrl(context)  + METHOD_NAME);
        AndroidNetworking.post(SharedPreferencesManager.getHostUrl(context)  + METHOD_NAME).addJSONObjectBody(jsonobject3)
                .setTag("test")
                .setPriority(Priority.LOW)
                .setOkHttpClient(okHttpClient) // passing a custom okHttpClient
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject result) {
                        hideProgressDialog();
                        Log.e("ERROR",result.toString());
                        if (result != null) {
                            try {
                                String success = result.getString("success");

                                if(success.equals("true")){
                                    Intent i = new Intent(LoginActivity.this,DashboardActivity.class);
                                    // Intent i = new Intent(LoginActivity.this,RegistrationActivity.class);
                                    startActivity(i);
                                }else{
                                    String error = result.getString("error");
                                    showCustomErrorDialog(context,error);
                                    //  btnCancel.performClick();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                //btnCancel.performClick();
                                Log.e("EXC",e.getMessage());
                            }
                        } else {
                            BeepClass.errorbeep(context);
                            Toast.makeText(context,context.getResources().getString(R.string.communication_error), Toast.LENGTH_SHORT).show();
                            //btnCancel.performClick();
                            // showCustomErrorDialog(context, context.getResources().getString(R.string.communication_error));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideProgressDialog();
                        Log.e("ERROR",anError.getErrorDetail());
                        // btnCancel.performClick();
                        Toast.makeText(context,context.getResources().getString(R.string.inactive_internet), Toast.LENGTH_SHORT).show();
                        // showCustomErrorDialog(context, context.getResources().getString(R.string.inactive_internet));
                    }
                });
    }



    /**
     * method to UPLOAD inventory data to server
     * */
    public void CallUserSyncAPI(final Context context, final JSONObject jsonobject3, String METHOD_NAME, String progress_message) {

        showProgress(context,progress_message);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .build();
        Log.e("URL", SharedPreferencesManager.getHostUrl(context)  + METHOD_NAME);
        AndroidNetworking.post(SharedPreferencesManager.getHostUrl(context)  + METHOD_NAME).addJSONObjectBody(jsonobject3)
                .setTag("test")
                .setPriority(Priority.LOW)
                .setOkHttpClient(okHttpClient) // passing a custom okHttpClient
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject result) {
                        hideProgressDialog();
                        Log.e("ERROR",result.toString());
                        if (result != null) {
                            try {
                                String success = result.getString("success");

                                if(success.equals("true")){
                                    Intent i = new Intent(LoginActivity.this,DashboardActivity.class);
                                    // Intent i = new Intent(LoginActivity.this,RegistrationActivity.class);
                                    startActivity(i);
                                }else{
                                    String error = result.getString("error");
                                    showCustomErrorDialog(context,error);
                                    //  btnCancel.performClick();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                //btnCancel.performClick();
                                Log.e("EXC",e.getMessage());
                            }
                        } else {
                            BeepClass.errorbeep(context);
                            Toast.makeText(context,context.getResources().getString(R.string.communication_error), Toast.LENGTH_SHORT).show();
                            //btnCancel.performClick();
                            // showCustomErrorDialog(context, context.getResources().getString(R.string.communication_error));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideProgressDialog();
                        Log.e("ERROR",anError.getErrorDetail());
                        // btnCancel.performClick();
                        Toast.makeText(context,context.getResources().getString(R.string.inactive_internet), Toast.LENGTH_SHORT).show();
                        // showCustomErrorDialog(context, context.getResources().getString(R.string.inactive_internet));
                    }
                });
    }

    Dialog errordialog,successdialog;
    public void showCustomErrorDialog(Context context, String msg) {
        if(errordialog!=null){
            errordialog.dismiss();
        }
        errordialog = new Dialog(context);
        errordialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errordialog.setCancelable(false);
        errordialog.setContentView(R.layout.custom_alert_dialog_layout);
        TextView text = (TextView) errordialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) errordialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errordialog.dismiss();

            }
        });
        errordialog.show();
    }



    /**
     * Common method for showing custom alert success type dialog
     */
    public void showCustomSuccessDialog(Context context, String msg) {
        successdialog = new Dialog(context);
        successdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successdialog.setCancelable(false);
        successdialog.setContentView(R.layout.custom_alert_success_dialog_layout);
        TextView text = (TextView) successdialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) successdialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successdialog.dismiss();
            }
        });
        successdialog.show();
    }

    private void handleClearClick(View v) {
        edtName.setText("");
        edtPassword.setText("");
        chkRemember.setChecked(false);


    }

    /**
     * method to get transactions data from server
     * */
    public  void GetUserMaster(String progress_message,String methodname) {

        showProgress(context,progress_message);
       /* OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .build();*/

        String URL = AppConstants.HOST_URL + AppConstants.GET_USERS;

        URL = SharedPreferencesManager.getHostUrl(context)  + methodname;

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
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray result) {
                        hideProgressDialog();
                        if (result != null) {

                            JSONArray res = result;
                            try {
                                List<User> userList = new ArrayList<>();

                                for(int i=0;i<res.length();i++){

                                    JSONObject dataObject = res.getJSONObject(i);
                                    User user = new User();
                                    user.setUserID(dataObject.getString(APIKeys.K_USER_ID));
                                    user.setUserName(dataObject.getString(APIKeys.K_USER_USERNAME));
                                    user.setPassword(dataObject.getString(APIKeys.K_USER_PASSWORD));
                                    user.setFirstName(dataObject.getString(APIKeys.K_USER_FIRST_NAME));
                                    user.setLastName(dataObject.getString(APIKeys.K_USER_LAST_NAME));
                                    // user.setCompanyCode(dataObject.getString(APIKeys.K_USER_COMPANY_CODE));
                                    user.setCompanyCode("PSL");
                                    user.setTransactionDateTime(dataObject.getString(APIKeys.K_USER_TRANSACTION_DATE_TIME));
                                    user.setIsActive(dataObject.getString(APIKeys.K_USER_IS_ACTIVE));

                                    userList.add(user);
                                    // transactionTypesList.add(transactionTypes);
                                }
                                // db.deleteTransactionMaster();
                                db.deleteUserMaster();
                                db.storeUserMaster(userList);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("RES",e.getMessage());
                            }

                            // showCustomSuccessConfirmationDialog(context,getResources().getString(R.string.url_validation),url);
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
    private void handleUserSyncClick(View v) {
        edtName.setText("");
        edtPassword.setText("");
        chkRemember.setChecked(false);

        if(cd.isConnectingToInternet()){

            GetUserMaster("Please Wait...\nValidating URL", AppConstants.GET_USERS);
        }else{
            Toast.makeText(context,context.getResources().getString(R.string.internet_error),Toast.LENGTH_SHORT).show();
        }


    }

}