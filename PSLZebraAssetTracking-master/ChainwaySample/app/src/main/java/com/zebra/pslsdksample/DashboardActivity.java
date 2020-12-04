package com.zebra.pslsdksample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.stetho.Stetho;
import com.zebra.pslsdksample.databases.DatabaseHandler;
import com.zebra.pslsdksample.helper.APIKeys;
import com.zebra.pslsdksample.helper.AppConstants;
import com.zebra.pslsdksample.helper.ApplicationCommonMethods;
import com.zebra.pslsdksample.helper.ConnectionDetector;
import com.zebra.pslsdksample.helper.SharedPreferencesManager;
import com.zebra.pslsdksample.modals.CastDetail;
import com.zebra.pslsdksample.modals.DestinationLocations;
import com.zebra.pslsdksample.modals.DestinationTouchPoints;
import com.zebra.pslsdksample.modals.SourcePlant;
import com.zebra.pslsdksample.modals.TasksModal;
import com.zebra.pslsdksample.modals.TransactionTypes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class DashboardActivity extends AppCompatActivity {
private Button btnLoad,btnAssignment,btnSyncTransactions,btnSyncTasks,btnSyncLocAndTouchPoints,btnDestinationPoint;
private TextView textUser;
private ConnectionDetector cd;
private DatabaseHandler db;
private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_dashboard);
        db = new DatabaseHandler(context);
        cd = new ConnectionDetector(context);

        btnLoad = (Button) findViewById(R.id.btnLoad);
        btnAssignment = (Button) findViewById(R.id.btnAssignment);
        btnSyncTransactions = (Button) findViewById(R.id.btnSyncTransactions);
        btnSyncTasks = (Button) findViewById(R.id.btnSyncTasks);
        btnSyncLocAndTouchPoints = (Button) findViewById(R.id.btnSyncLocAndTouchPoints);
        btnDestinationPoint = (Button) findViewById(R.id.btnDestinationPoint);
        textUser = (TextView) findViewById(R.id.textUser);

        textUser.setText("Welcome : "+getIntent().getStringExtra("user"));


        btnDestinationPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.getDestinationLoocationsCount()>0 && db.getDestinationTouchPointsCount()>0){

                        Intent loadIntent = new Intent(DashboardActivity.this,DestinationPointActivity.class);
                        startActivity(loadIntent);


                }else{
                    Toast.makeText(context, "Please Sync Destination Locations and Touch Points", Toast.LENGTH_SHORT).show();
                }

            }
        });




        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.getTransactionsCount()>0 && db.getTasksCount()>0){
                    if(db.getSourcePlantCount()==0){
                        Toast.makeText(context,"No Source Plant Available,Please Sync Tasks and Transactions Again...",Toast.LENGTH_LONG).show();

                    }else{
                        Intent loadIntent = new Intent(DashboardActivity.this,PreLoadActivity.class);
                        startActivity(loadIntent);
                    }

                }else{
                    Toast.makeText(context, "Please Sync Tasks and transactions", Toast.LENGTH_SHORT).show();
                }

            }
        });



        btnAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(db.getTransactionsCount()>0 && db.getTasksCount()>0){
                    Intent loadIntent = new Intent(DashboardActivity.this,AssignmentActivity.class);
                    startActivity(loadIntent);
                }else{
                    Toast.makeText(context, "Please Sync Tasks and transactions", Toast.LENGTH_SHORT).show();
                }*/

               if(!SharedPreferencesManager.getIsCastHostConfig(context)){
                   Intent i = new Intent(DashboardActivity.this,CastURLConfigActivity.class);
                   startActivity(i);
               }else{
                   if(cd.isConnectingToInternet()){
                       GetCastMaster("Please Wait...\nGetting Cast Details", ApplicationCommonMethods.getSystemDateTimeInFormatt());
                   }else{
                       Toast.makeText(context,context.getResources().getString(R.string.internet_error),Toast.LENGTH_SHORT).show();
                   }
               }


            }
        });

        btnSyncTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cd.isConnectingToInternet()){
                    GetTasksMaster(getResources().getString(R.string.get_tasks_progress_message), ApplicationCommonMethods.getSystemDateTimeInFormatt());
                }else{
                    Toast.makeText(context,context.getResources().getString(R.string.internet_error),Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSyncLocAndTouchPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cd.isConnectingToInternet()){
                    GetLocationMaster(getResources().getString(R.string.get_tasks_progress_message), ApplicationCommonMethods.getSystemDateTimeInFormatt());
                }else{
                    Toast.makeText(context,context.getResources().getString(R.string.internet_error),Toast.LENGTH_SHORT).show();
                }
            }
        });
        //btnSyncLocAndTouchPoints


        btnSyncTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cd.isConnectingToInternet()){
                    GetTransactionMaster(getResources().getString(R.string.get_transaction_progress_message), ApplicationCommonMethods.getSystemDateTimeInFormatt());
                }else{
                    Toast.makeText(context,context.getResources().getString(R.string.internet_error),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * method to get transactions data from server
     * */
    public  void GetCastMaster(String progress_message,String date) {

        showProgress(context,progress_message);
       /* OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .build();*/

        String URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_TRANSACTION_TYPES;

            URL = SharedPreferencesManager.getCastHostUrl(context) + AppConstants.GET_CASTS;

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

                                    if(db.getAllCastList().size()>0){
                                        Intent loadIntent = new Intent(DashboardActivity.this,AssignmentActivity.class);
                                        startActivity(loadIntent);
                                    }else{
                                        Toast.makeText(context,"No Cast Detail list found, please try again",Toast.LENGTH_SHORT).show();
                                    }
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

    /**
     * method to get transactions data from server
     * */
    public  void GetTransactionMaster(String progress_message,String date) {

        showProgress(context,progress_message);

        String URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_TRANSACTION_TYPES;
        if(db.getTransactionsCount()>0){
            URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_TRANSACTION_TYPES + db.getTreansMaxDate();
        }else{
            URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_TRANSACTION_TYPES;
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
                                List<TransactionTypes> transactionTypesList = new ArrayList<>();
                                if(success.equals("true")){

                                    JSONArray dataArray = result.getJSONArray("data");
                                    for(int i=0;i<dataArray.length();i++){

                                        JSONObject dataObject = dataArray.getJSONObject(i);
                                        TransactionTypes transactionTypes = new TransactionTypes();
                                        transactionTypes.setTransaction_id(dataObject.getString(APIKeys.TRANSACTION_ID));
                                        transactionTypes.setTransaction_description(dataObject.getString(APIKeys.TRANSACTION_DESCRIPTION));
                                        transactionTypes.setTransaction_location_type(dataObject.getString(APIKeys.TRANSACTION_LOCATION_TYPE));


                                       // transactionTypes.setTransaction_modified_date(dataObject.getString(APIKeys.TRANSACTION_MODIFIED_DATE_TIME));

                                        String date = (dataObject.getString(APIKeys.TRANSACTION_MODIFIED_DATE_TIME));
                                        date = date.replace('T',' ');

                                        if(date.contains(".")){
                                            if(date.length()>19){
                                                date = date.substring(0,19);
                                            }else{
                                                date = date.split(".")[0];
                                            }
                                        }
                                        transactionTypes.setTransaction_modified_date(date);


                                        transactionTypes.setTransaction_is_active(dataObject.getString(APIKeys.TRANSACTION_IS_ACTIVE));

                                       if(dataObject.getString(APIKeys.TRANSACTION_IS_ACTIVE).equalsIgnoreCase("true")){
                                           transactionTypesList.add(transactionTypes);
                                       }

                                    }
                                   // db.deleteTransactionMaster();
                                    db.storeTransactionMaster(transactionTypesList);

                                    if(cd.isConnectingToInternet()){
                                        GetSourcePlantMaster(getResources().getString(R.string.get_transaction_progress_message), ApplicationCommonMethods.getSystemDateTimeInFormatt());
                                    }else{
                                        Toast.makeText(context,context.getResources().getString(R.string.internet_error),Toast.LENGTH_SHORT).show();
                                    }

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


    /**
     * method to get transactions data from server
     * */
    public  void GetSourcePlantMaster(String progress_message,String date) {

        showProgress(context,progress_message);

        String URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_SOURCE_PLANTS ;

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
                                List<SourcePlant> transactionTypesList = new ArrayList<>();
                                if(success.equals("true")){

                                    JSONArray dataArray = result.getJSONArray("data");
                                    for(int i=0;i<dataArray.length();i++){

                                        JSONObject dataObject = dataArray.getJSONObject(i);

                                        String subplants = dataObject.getString(APIKeys.SP_SUB_PLANT);


                                        String[] subplant = subplants.split(",");
                                        for(int s = 0;s<subplant.length;s++){
                                            Log.e("SUBPLANT",subplant[s]);
                                            SourcePlant transactionTypes = new SourcePlant();
                                            transactionTypes.setSp_id(dataObject.getString(APIKeys.SP_ID));
                                            transactionTypes.setSp_main_plant(dataObject.getString(APIKeys.SP_MAIN_PLANT));
                                            transactionTypes.setSp_sub_plant(subplant[s]);
                                            // transactionTypes.setTransaction_modified_date(dataObject.getString(APIKeys.TRANSACTION_MODIFIED_DATE_TIME));

                                            String date = (dataObject.getString(APIKeys.SP_MODIFIED_DATE_TIME));
                                            date = date.replace('T',' ');

                                            if(date.contains(".")){
                                                if(date.length()>19){
                                                    date = date.substring(0,19);
                                                }else{
                                                    date = date.split(".")[0];
                                                }
                                            }
                                            transactionTypes.setSp_modified_date_time(date);



                                            String crdate = (dataObject.getString(APIKeys.SP_CREATED_DATE_TIME));
                                            crdate = crdate.replace('T',' ');

                                            if(crdate.contains(".")){
                                                if(crdate.length()>19){
                                                    crdate = crdate.substring(0,19);
                                                }else{
                                                    crdate = crdate.split(".")[0];
                                                }
                                            }
                                            transactionTypes.setSp_created_date_time(crdate);


                                            transactionTypes.setSp_is_active(dataObject.getString(APIKeys.SP_IS_ACTIVE));
                                            transactionTypesList.add(transactionTypes);
                                        }


                                    }
                                    // db.deleteTransactionMaster();
                                    db.deleteSourcePlant();
                                    db.storeSourcePlantMaster(transactionTypesList);
                                    Log.e("Sourceplant","DOne");



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

    /**
     * method to get transactions data from server
     * */
    public  void GetTasksMaster(String progress_message,String date) {

        showProgress(context,progress_message);
       /* OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .build();*/
        OkHttpClient okHttpClient = AppConstants.getUnsafeOkHttpClient();

        // AndroidNetworking.post(SharedPreferencesManager.getHostUrl(context) + METHOD_NAME).addJSONObjectBody(jsonobject3)
        String URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_TASKS;
        if(db.getTasksCount()>0){
            URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_TASKS + db.getTasksMaxDate();
        }else{
            URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_TASKS;
        }
        Log.e("URL",URL);
        AndroidNetworking.get(URL)//.addJSONObjectBody(jsonobject3)
       // AndroidNetworking.get(SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_TASKS )//.addJSONObjectBody(jsonobject3)
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
                                if(success.equals("true")){
                                    JSONArray dataArray = result.getJSONArray("data");
                                    List<TasksModal> tasksModalList = new ArrayList<>();
                                    //db.deleteTaskMaster();
                                    for(int i=0;i<dataArray.length();i++){

                                        JSONObject dataObject = dataArray.getJSONObject(i);
                                        TasksModal tasksModal = new TasksModal();
                                        tasksModal.setTasks_id(dataObject.getString(APIKeys.TASK_ID));
                                        tasksModal.setTask_number(dataObject.getString(APIKeys.TASK_NUMBER));
                                        tasksModal.setTask_date(dataObject.getString(APIKeys.TASK_DATE));
                                        tasksModal.setTask_gate_number(dataObject.getString(APIKeys.TASK_GATE_NUMBER));
                                        tasksModal.setTask_start_date(dataObject.getString(APIKeys.TASK_START_DATE));
                                        tasksModal.setTask_end_date(dataObject.getString(APIKeys.TASK_END_DATE));
                                        tasksModal.setTask_po_sto_number(dataObject.getString(APIKeys.TASK_PO_STO_NUMBER));
                                        tasksModal.setTask_sr_number(dataObject.getString(APIKeys.TASK_SR_NUMBER));
                                        tasksModal.setTask_sto_po_number(dataObject.getString(APIKeys.TASK_STO_PO_NUMBER));
                                        tasksModal.setTask_sto_po_sr_number(dataObject.getString(APIKeys.TASK_STO_PO_SR_NUMBER));
                                        tasksModal.setTask_permit_number(dataObject.getString(APIKeys.TASK_PERMIT_NUMBER));
                                        tasksModal.setTask_product(dataObject.getString(APIKeys.TASK_PRODUCT));
                                        tasksModal.setTask_description(dataObject.getString(APIKeys.TASK_DESCRIPTION));
                                        tasksModal.setTask_trans_code(dataObject.getString(APIKeys.TASK_TRANSACTION_CODE));
                                        tasksModal.setTask_transporter(dataObject.getString(APIKeys.TASK_TRANSPORTER));
                                        tasksModal.setTask_vendor(dataObject.getString(APIKeys.TASK_VENDOR));
                                        tasksModal.setTask_route_code(dataObject.getString(APIKeys.TASK_ROUTE_CODE));
                                        tasksModal.setTask_source_plant(dataObject.getString(APIKeys.TASK_SOURCE_PLANT));
                                        tasksModal.setTask_source_location(dataObject.getString(APIKeys.TASK_SOURCE_LOCATION));
                                        tasksModal.setTask_source_batch(dataObject.getString(APIKeys.TASK_SOURCE_BATCH));
                                        tasksModal.setTask_destination_location(dataObject.getString(APIKeys.TASK_DESTINATION_LOCATION));
                                        tasksModal.setTask_destination_plant(dataObject.getString(APIKeys.TASK_DESTINATION_PLANT));
                                        tasksModal.setTask_destination_batch(dataObject.getString(APIKeys.TASK_DESTINATION_BATCH));
                                        tasksModal.setTask_overload(dataObject.getString(APIKeys.TASK_OVERLOAD));
                                        tasksModal.setTask_barge_mandatory(dataObject.getString(APIKeys.TASK_BARGE_MANDATORY));
                                        tasksModal.setTask_created_by(dataObject.getString(APIKeys.TASK_CREATED_BY));
                                        tasksModal.setTask_created_date_time(dataObject.getString(APIKeys.TASK_CREATED_DATE_TIME));
                                        tasksModal.setTask_modified_by(dataObject.getString(APIKeys.TASK_MODIFIED_BY));
                                        tasksModal.setTask_transaction_id(dataObject.getString(APIKeys.TASK_TRANSACTION_TYPE));

                                        String date = (dataObject.getString(APIKeys.TASK_MODIFIED_DATE_TIME));
                                        date = date.replace('T',' ');


                                        if(date.contains(".")){
                                            if(date.length()>19){
                                                date = date.substring(0,19);
                                            }else{
                                                date = date.split(".")[0];
                                            }
                                        }
                                        tasksModal.setTask_modified_date_time(date);

                                        tasksModal.setTask_is_active(dataObject.getString(APIKeys.TASK_IS_ACTIVE));

                                       // if(dataObject.getString(APIKeys.TASK_IS_ACTIVE).equalsIgnoreCase("true")){
                                            tasksModalList.add(tasksModal);

                                      //  }

                                    }
                                    db.storeTaskMaster(tasksModalList);
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



    /**
     * method to get transactions data from server
     * */
    public  void GetLocationMaster(String progress_message,String date) {

        showProgress(context,progress_message);
       /* OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .build();*/
        OkHttpClient okHttpClient = AppConstants.getUnsafeOkHttpClient();

        // AndroidNetworking.post(SharedPreferencesManager.getHostUrl(context) + METHOD_NAME).addJSONObjectBody(jsonobject3)
        String URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_LOCATIONS;

        Log.e("URL",URL);
        AndroidNetworking.get(URL)//.addJSONObjectBody(jsonobject3)
                // AndroidNetworking.get(SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_TASKS )//.addJSONObjectBody(jsonobject3)
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
                                if(success.equals("true")){
                                    JSONArray dataArray = result.getJSONArray("data");
                                    List<DestinationLocations> tasksModalList = new ArrayList<>();
                                    //db.deleteTaskMaster();
                                    for(int i=0;i<dataArray.length();i++){

                                        JSONObject dataObject = dataArray.getJSONObject(i);
                                        DestinationLocations tasksModal = new DestinationLocations();
                                        tasksModal.setLocation_id(dataObject.getString(APIKeys.LOC_ID));
                                        tasksModal.setLocation_code(dataObject.getString(APIKeys.LOC_CODE));
                                        tasksModal.setLocation_desc(dataObject.getString(APIKeys.LOC_DESC));
                                        tasksModal.setLocation_type(dataObject.getString(APIKeys.LOC_TYPE));
                                        tasksModal.setLocation_name(dataObject.getString(APIKeys.LOC_NAME));
                                        tasksModal.setLocation_is_weighbridge_required(dataObject.getString(APIKeys.LOC_IS_WEIGHBRIDGE_REQUIRED));
                                        tasksModal.setLocation_area(dataObject.getString(APIKeys.LOC_AREA));

                                        String date = (dataObject.getString(APIKeys.LOC_MODIFIED_DATE_TIME));
                                        date = date.replace('T',' ');


                                        if(date.contains(".")){
                                            if(date.length()>19){
                                                date = date.substring(0,19);
                                            }else{
                                                date = date.split(".")[0];
                                            }
                                        }
                                        tasksModal.setLocation_modified_date(date);

                                        tasksModal.setLocation_is_active(dataObject.getString(APIKeys.LOC_IS_ACTIVE));
                                        if(dataObject.getString(APIKeys.LOC_IS_ACTIVE).equalsIgnoreCase("true")){
                                            tasksModalList.add(tasksModal);

                                        }


                                    }
                                    db.deleteDestinationLocation();
                                    db.storeDestinationLocationMaster(tasksModalList);

                                    if(cd.isConnectingToInternet()){
                                        GetTouchPointMaster(getResources().getString(R.string.get_tasks_progress_message), ApplicationCommonMethods.getSystemDateTimeInFormatt());
                                    }else{
                                        Toast.makeText(context,context.getResources().getString(R.string.internet_error),Toast.LENGTH_SHORT).show();
                                    }

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



    /**
     * method to get transactions data from server
     * */
    public  void GetTouchPointMaster(String progress_message,String date) {

        showProgress(context,progress_message);
       /* OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .build();*/
        OkHttpClient okHttpClient = AppConstants.getUnsafeOkHttpClient();

        // AndroidNetworking.post(SharedPreferencesManager.getHostUrl(context) + METHOD_NAME).addJSONObjectBody(jsonobject3)
        String URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_TOUCH_POINTS;

        Log.e("URL",URL);
        AndroidNetworking.get(URL)//.addJSONObjectBody(jsonobject3)
                // AndroidNetworking.get(SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_TASKS )//.addJSONObjectBody(jsonobject3)
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
                                if(success.equals("true")){
                                    JSONArray dataArray = result.getJSONArray("data");
                                    List<DestinationTouchPoints> tasksModalList = new ArrayList<>();
                                    //db.deleteTaskMaster();
                                    for(int i=0;i<dataArray.length();i++){

                                        JSONObject dataObject = dataArray.getJSONObject(i);
                                        DestinationTouchPoints tasksModal = new DestinationTouchPoints();
                                        tasksModal.setDest_id(dataObject.getString(APIKeys.TP_ID));
                                        tasksModal.setDest_touch_point_id(dataObject.getString(APIKeys.TP_TPID));
                                        tasksModal.setDest_touch_point_name(dataObject.getString(APIKeys.TP_NAME));
                                        tasksModal.setDest_location_id(dataObject.getString(APIKeys.TP_LOCATION_ID));
                                        tasksModal.setDest_is_destination_point(dataObject.getString(APIKeys.TP_IS_DESTINATION_POINT));

                                        String date = (dataObject.getString(APIKeys.TP_MODIFIED_DATE_TIME));
                                        date = date.replace('T',' ');


                                        if(date.contains(".")){
                                            if(date.length()>19){
                                                date = date.substring(0,19);
                                            }else{
                                                date = date.split(".")[0];
                                            }
                                        }
                                        tasksModal.setDest_modified_date_time(date);

                                        String crdate = (dataObject.getString(APIKeys.TP_MODIFIED_DATE_TIME));
                                        crdate = crdate.replace('T',' ');


                                        if(crdate.contains(".")){
                                            if(crdate.length()>19){
                                                crdate = crdate.substring(0,19);
                                            }else{
                                                crdate = crdate.split(".")[0];
                                            }
                                        }
                                        tasksModal.setDest_created_date_time(crdate);


                                        tasksModal.setDest_is_active(dataObject.getString(APIKeys.TP_IS_ACTIVE));

                                        if(dataObject.getString(APIKeys.TP_IS_ACTIVE).equalsIgnoreCase("true")){
                                            tasksModalList.add(tasksModal);

                                        }

                                    }
                                    db.deleteDestinationTouchPoint();
                                    db.storeDestinationTouchPointMaster(tasksModalList);
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



    ProgressDialog progressDialog;
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
}
