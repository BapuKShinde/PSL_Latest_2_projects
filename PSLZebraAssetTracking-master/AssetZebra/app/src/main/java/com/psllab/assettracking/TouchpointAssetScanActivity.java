package com.psllab.assettracking;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.psllab.assettracking.adapters.AssetInventoryAdapter;
import com.psllab.assettracking.apihelpers.APIKeys;
import com.psllab.assettracking.databases.DatabaseHandler;
import com.psllab.assettracking.helpers.AppConstants;
import com.psllab.assettracking.helpers.ApplicationCommonMethods;
import com.psllab.assettracking.helpers.BeepClass;
import com.psllab.assettracking.helpers.ConnectionDetector;
import com.psllab.assettracking.helpers.GPSTracker;
import com.psllab.assettracking.helpers.SharedPreferencesManager;
import com.psllab.assettracking.modals.AssetMaster;
import com.psllab.assettracking.modals.User;
import com.psllab.assettracking.rfidhandlers.SingleAssetScanRFIDHandler;
import com.psllab.assettracking.rfidhandlers.SingleAssetTouchPointRFIDHandler;
import com.zebra.rfid.api3.TagData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import okhttp3.OkHttpClient;

public class TouchpointAssetScanActivity extends AppCompatActivity implements SingleAssetTouchPointRFIDHandler.ResponseHandlerInterface{

    SingleAssetTouchPointRFIDHandler rfidHandler;
    final static String TAG = "RFID_SAMPLE";
    public boolean is_search_on = false;

    private ConnectionDetector cd;
    private DatabaseHandler db;
    private Context context = this;
    private GPSTracker gps;

    private RecyclerView list;
    private TextView textValidCount,textLocation;
    private EditText edtTouchPoint;
    private LinearLayout llData;
    private List<AssetMaster> assetMasterList;
    private Set<String> epcList;
    private Set<String> touchepcList;
    private AssetInventoryAdapter assetInventoryAdapter;

    private Timer beepTimer;
    private Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchpoint_asset_scan);
        getSupportActionBar().hide();

        rfidHandler = new SingleAssetTouchPointRFIDHandler();
        rfidHandler.onCreate(this);


        db = new DatabaseHandler(context);
        cd = new ConnectionDetector(context);
        gps = new GPSTracker(context);

        assetMasterList = new ArrayList<AssetMaster>();
        epcList = new TreeSet<>();
        touchepcList = new TreeSet<>();


        list = (RecyclerView)findViewById(R.id.list);
        // btnSave = (Button) findViewById(R.id.btnSave);
        btnClear = (Button) findViewById(R.id.btnClear);
        //btnStop = (Button) findViewById(R.id.btnStop);
        textValidCount = (TextView) findViewById(R.id.textValidCount);
        edtTouchPoint = (EditText) findViewById(R.id.edtTouchPoint);
        llData = (LinearLayout) findViewById(R.id.llData);
        llData.setVisibility(View.GONE);
        textLocation = (TextView) findViewById(R.id.textLocation);
        textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));

        textValidCount.setText("0");

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllList();
            }
        });

        assetInventoryAdapter = new AssetInventoryAdapter(context,assetMasterList);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(assetInventoryAdapter);

        clearAllList();
        clearList();
    }
    private void clearList() {
        // mSelectedIndex = -1;

        timer_count = 0;
        if (epcList != null) {
            epcList.clear();
            //  mAdapter.notifyDataSetChanged();
            //  m_count = 0;
        }

        if (assetMasterList != null) {
            assetMasterList.clear();
            //  mAdapter.notifyDataSetChanged();
            //  m_count = 0;
        }
      //  assetInventoryAdapter.notifyDataSetChanged();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //edtTouchPoint.setText("");
               // llData.setVisibility(View.GONE);
                assetInventoryAdapter.notifyDataSetChanged();
                textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));
                textValidCount.setText(String.valueOf(epcList.size()));
            }
        });
    }


    private void clearAllList() {
        // mSelectedIndex = -1;

        timer_count = 0;
        if (epcList != null) {
            epcList.clear();
            //  mAdapter.notifyDataSetChanged();
            //  m_count = 0;
        }

        if (touchepcList != null) {
            touchepcList.clear();
            //  mAdapter.notifyDataSetChanged();
            //  m_count = 0;
        }

        if (assetMasterList != null) {
            assetMasterList.clear();
            //  mAdapter.notifyDataSetChanged();
            //  m_count = 0;
        }

      //  assetInventoryAdapter.notifyDataSetChanged();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                assetInventoryAdapter.notifyDataSetChanged();
                edtTouchPoint.setText("");
                llData.setVisibility(View.GONE);
                textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));
                textValidCount.setText(String.valueOf(epcList.size()));
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void handleTagdata(TagData[] tagData) {
        final StringBuilder sb = new StringBuilder();
        final StringBuilder sb2 = new StringBuilder();
        for (int index = 0; index < tagData.length; index++) {
            sb.append(tagData[index].getTagID() + "\n");
            final String epc = tagData[index].getTagID();
            if(!epc.isEmpty()){
                Log.e("DATA",epc);
                //TODO add data in adapter

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));
                    }
                });


                if(touchepcList.size()==0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            touchepcList.add(epc);
                            edtTouchPoint.setText(epc);
                            llData.setVisibility(View.VISIBLE);
                            if(timer_count>2){
                                stopInventory();
                            }

                            if(touchepcList.size()>1){
                                clearList();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        ApplicationCommonMethods.showCustomErrorDialog(context, "Multiple Tags Found,please try again");

                                        edtTouchPoint.setText("");
                                        llData.setVisibility(View.GONE);
                                        textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));
                                        textValidCount.setText(String.valueOf(epcList.size()));
                                    }
                                });


                                if(touchepcList!=null){
                                    touchepcList.clear();
                                }
                            }
                        }
                    });
                }else if(touchepcList.size()==1){
                    if(!epcList.contains(epc)){
                        if(epc.length()>20){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    epcList.add(epc);
                                  //  for(int i=0;i<epcList.size();i++){
                                        AssetMaster assetMaster= db.getAssetDetailsByTagID(epc);
                                        if(assetMaster!=null){
                                            assetMasterList.add(assetMaster);
                                        }else{
                                            //  TagID,SerialNo,UserID,TransactionDateTime;
                                            AssetMaster assetMaster1 = new AssetMaster();
                                            assetMaster1.setCID("-");
                                            assetMaster1.setRefID("-");
                                            assetMaster1.setName("-");
                                            assetMaster1.setDescription("-");
                                            assetMaster1.setCategoryID("-");
                                            assetMaster1.setCategoryName("-");
                                            assetMaster1.setDataField1("-");
                                            assetMaster1.setDataField2("-");
                                            assetMaster1.setDataField3("-");
                                            assetMaster1.setDataField4("-");
                                            assetMaster1.setTagID(epc);
                                            assetMaster1.setSerialNo("-");
                                            assetMaster1.setUserID("-");
                                            assetMaster1.setTransactionDateTime("-");
                                            assetMasterList.add(assetMaster1);
                                        }
                                        BeepClass.successbeep(context);
                                   // }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            assetInventoryAdapter.notifyDataSetChanged();
                                        }
                                    });
                                 //   assetInventoryAdapter.notifyDataSetChanged();
                                    // itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, epcList);
                                    // list.setAdapter(itemsAdapter);
                                }
                            });

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {



                                    textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));
                                    textValidCount.setText(String.valueOf(epcList.size()));
                                    assetInventoryAdapter.notifyDataSetChanged();
                                }
                            });


                            if(timer_count>2){
                                stopInventory();
                            }

                            if(epcList.size()>1){

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ApplicationCommonMethods.showCustomErrorDialog(context, "Multiple Tags Found,please try again");
                                    }
                                });

                                clearList();
                            }
                        }
                    }
                }

            }
        }

    }


    int timer_count = 0;
    @Override
    public void handleTriggerPress(boolean pressed) {
        if(pressed)
            timer_count = 0;

        if(epcList.size()==0 || touchepcList.size()==0){
            performInventory();

            beepTimer = new Timer();
            beepTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    timer_count = timer_count+1;
                }

            }, 0, AppConstants.BEEP_TIMER_LIMIT);

        }
        if(epcList.size()==1 && touchepcList.size()==1){
            // performInventory();
            //UPLOAD
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(cd.isConnectingToInternet()){

                        JSONObject post_dict = new JSONObject();
                        try {
                            post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_TAG_ID,assetMasterList.get(0).getTagID());
                            post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_COMPANY_CODE,"16");
                            post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_GPS_LAT,String.valueOf(gps.getLatitude()));
                            post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_GPS_LONG,String.valueOf(gps.getLongitude()));
                            post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_GPS_LOCATION,String.valueOf(gps.getLatitude())+","+String.valueOf(gps.getLongitude()));

                            post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_TOUCH_POINT_ID,edtTouchPoint.getText().toString().toUpperCase());
                            post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_REF_ID1,"");
                            post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_REF_ID2,"");
                            post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_TRANS_DATE_TIME,ApplicationCommonMethods.getSystemDateTimeInFormat());
                            post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_USER_ID, SharedPreferencesManager.getUserId(context));
                            post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_CATEGORY,assetMasterList.get(0).getCategoryID());
                            post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_SRNO,assetMasterList.get(0).getSerialNo());

                            UploadAssetDetails(post_dict,"Please wait\nUploading Asset Details",AppConstants.POST_TAG_DETAILS);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else{
                        ApplicationCommonMethods.showCustomErrorDialog(context,"No Internet");
                    }
                }
            });
        }
        if(epcList.size()>1){
            // performInventory();
            //UPLOAD
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ApplicationCommonMethods.showCustomErrorDialog(context, "Multiple Tags Found, please try again");
                }
            });

            clearList();
        }
        if(touchepcList.size()>1){
            // performInventory();
            //UPLOAD
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ApplicationCommonMethods.showCustomErrorDialog(context, "Multiple Tags Found, please try again");
                }
            });

            clearAllList();
        }

    }

    public void performInventory(){
        is_search_on = true;
        runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                // btnSearch.setText("Stop");
                // btnSearch.setBackground(getResources().getDrawable(R.drawable.clear_button_background));
            }
        });
        rfidHandler.performInventory();
    }

    public void showDialog(String message,final String action){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setMessage("Are you sure you want to Sync Asset Data ?")
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO
                        dialog.cancel();
                        if(action.equalsIgnoreCase("BACK")){
                            finish();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {

        if(epcList.size()>0){
            showDialog("Are you sure you want to cancel ?","BACK");
        }else{
            super.onBackPressed();
        }
        stopInventory();

    }

    @Override
    protected void onPause() {
        super.onPause();
        rfidHandler.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        String status = rfidHandler.onResume();
        // statusTextViewRFID.setText(status);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (beepTimer != null) {
            beepTimer.cancel();
        }
        rfidHandler.onDestroy();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void stopInventory(){
        //  btnSearch.setText("Start");
        // btnSearch.setBackground(getResources().getDrawable(R.drawable.login_button_background));
        if(touchepcList.size()==1){
            llData.setVisibility(View.VISIBLE);
        }
        is_search_on = false;
        timer_count = 0;
        if (beepTimer != null) {
            beepTimer.cancel();
        }
        rfidHandler.stopInventory();
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
     * method to get transactions data from server
     * */
    public  void UploadAssetDetails(final JSONObject jsonobject3,String progress_message,String methodname) {

        showProgress(context,progress_message);
       /* OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .build();*/

        String URL = AppConstants.HOST_URL + AppConstants.POST_TAG_DETAILS;

        URL = SharedPreferencesManager.getHostUrl(context)  + methodname;

        OkHttpClient okHttpClient = AppConstants.getUnsafeOkHttpClient();
        Log.e("URL",URL);
        //Log.e("URL",AppConstants.HOST_URL + AppConstants.UPLOAD_INVENTORY_DETAILS);
        //AndroidNetworking.post(AppConstants.BASE_HOST + METHOD_NAME).addJSONObjectBody(jsonobject3)
        AndroidNetworking.post(URL).addJSONObjectBody(jsonobject3)
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
                                String success = result.getString("success");

                                if(success.equalsIgnoreCase("true")){
                                    ApplicationCommonMethods.showCustomSuccessDialog(context,"Details Uploaded Successfully");
                                    clearAllList();
                                }else{
                                    String error = result.getString("error");
                                    ApplicationCommonMethods.showCustomErrorDialog(context,error);
                                }

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
}