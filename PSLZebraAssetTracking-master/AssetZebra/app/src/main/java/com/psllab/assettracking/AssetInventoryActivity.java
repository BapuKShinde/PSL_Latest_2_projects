package com.psllab.assettracking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.psllab.assettracking.adapters.AssetInventoryAdapter;
import com.psllab.assettracking.apihelpers.APIKeys;
import com.psllab.assettracking.databases.DatabaseHandler;
import com.psllab.assettracking.helpers.AppConstants;
import com.psllab.assettracking.helpers.ApplicationCommonMethods;
import com.psllab.assettracking.helpers.ConnectionDetector;
import com.psllab.assettracking.helpers.GPSTracker;
import com.psllab.assettracking.helpers.SharedPreferencesManager;
import com.psllab.assettracking.modals.AssetMaster;
import com.psllab.assettracking.rfidhandlers.InventoryRFIDHandler;
import com.zebra.rfid.api3.TagData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import okhttp3.OkHttpClient;

public class AssetInventoryActivity extends AppCompatActivity implements InventoryRFIDHandler.ResponseHandlerInterface{

    InventoryRFIDHandler rfidHandler;
    final static String TAG = "RFID_SAMPLE";
    public boolean is_search_on = false;


    private List<AssetMaster> assetMasterList;
    private Set<String> epcList;

    private AssetInventoryAdapter assetInventoryAdapter;
    Button btnSave,btnClear,btnStop,btnUpload;
    private RecyclerView list;
    private TextView textValidCount,textLocation;

    private ConnectionDetector cd;
    private DatabaseHandler db;
    private Context context = this;

    private GPSTracker gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_inventory);
        getSupportActionBar().hide();

        rfidHandler = new InventoryRFIDHandler();
        rfidHandler.onCreate(this);

        db = new DatabaseHandler(context);
        cd = new ConnectionDetector(context);
        gps = new GPSTracker(context);

        assetMasterList = new ArrayList<AssetMaster>();
        epcList = new TreeSet<>();


        list = (RecyclerView)findViewById(R.id.list);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnStop = (Button) findViewById(R.id.btnStop);
        textValidCount = (TextView) findViewById(R.id.textValidCount);
        textLocation = (TextView) findViewById(R.id.textLocation);
        textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));

        textValidCount.setText("0");

        assetInventoryAdapter = new AssetInventoryAdapter(context,assetMasterList);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(assetInventoryAdapter);

        clearList();

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                stopInventory();
                textValidCount.setText(String.valueOf(epcList.size()));
                assetInventoryAdapter.notifyDataSetChanged();
                textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textValidCount.setText(String.valueOf(epcList.size()));
                assetInventoryAdapter.notifyDataSetChanged();
                textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));
                if(is_search_on){
                    //  btnSearch.setText("Start");
                    is_search_on = false;
                    rfidHandler.stopInventory();
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void run() {
                            // btnSearch.setText("Start");
                            // btnSearch.setBackground(getResources().getDrawable(R.drawable.login_button_background));
                        }
                    });

                }else{
                    performInventory();
                }

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                stopInventory();
                if(epcList!=null){
                    epcList.clear();
                }
                if(assetMasterList!=null){
                    assetMasterList.clear();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textValidCount.setText(String.valueOf(epcList.size()));
                        assetInventoryAdapter.notifyDataSetChanged();
                         textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));
                    }
                });

            }
        });
    }

    private void uploadData() {
        if(epcList.size()>0){
            if(cd.isConnectingToInternet()){

                JSONArray post_dict_array = new JSONArray();
                try {
                    for (int i = 0; i < assetMasterList.size(); i++) {
                        JSONObject post_dict = new JSONObject();

                        post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_TAG_ID, assetMasterList.get(i).getTagID());
                        post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_COMPANY_CODE, "16");
                        post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_GPS_LAT, String.valueOf(gps.getLatitude()));
                        post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_GPS_LONG, String.valueOf(gps.getLongitude()));
                        post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_GPS_LOCATION, String.valueOf(gps.getLatitude()) + "," + String.valueOf(gps.getLongitude()));

                        post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_TOUCH_POINT_ID, "0");
                        post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_REF_ID1, "");
                        post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_REF_ID2, "");
                        post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_TRANS_DATE_TIME, ApplicationCommonMethods.getSystemDateTimeInFormat());
                        post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_USER_ID, SharedPreferencesManager.getUserId(context));
                        post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_CATEGORY, assetMasterList.get(0).getCategoryID());
                        post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_SRNO, assetMasterList.get(0).getSerialNo());

                        post_dict_array.put(post_dict);

                    }
                    UploadAssetDetails(post_dict_array,"Please wait\nUploading Asset Details", AppConstants.POST_INVENTORY);

                }catch (JSONException e){

                }


            }else
            {
                ApplicationCommonMethods.showCustomErrorDialog(context,"No Internet");
            }

        }else{
            ApplicationCommonMethods.showCustomErrorDialog(context,"No Data To Upload");
        }
    }


    private void clearList() {
        // mSelectedIndex = -1;

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                assetInventoryAdapter.notifyDataSetChanged();
                textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));
            }
        });
      //  assetInventoryAdapter.notifyDataSetChanged();
       // textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));
    }

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
               // textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));
                if(!epcList.contains(epc)){
                    if(epc.length()>20){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                epcList.add(epc);
                                //for(int i=0;i<epcList.size();i++){
                                    AssetMaster assetMaster= db.getAssetDetailsByTagID(epc);
                                    if(assetMaster!=null){
                                        Log.e("NOT Null","NO");
                                        assetMasterList.add(assetMaster);
                                    }else{
                                        Log.e("NULL","NULL");
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
                               // }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        assetInventoryAdapter.notifyDataSetChanged();
                                        textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));
                                    }
                                });
                               // assetInventoryAdapter.notifyDataSetChanged();                                // itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, epcList);
                                // list.setAdapter(itemsAdapter);
                            }
                        });

                       // textValidCount.setText(String.valueOf(epcList.size()));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textValidCount.setText(String.valueOf(epcList.size()));
                                textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));
                                assetInventoryAdapter.notifyDataSetChanged();
                            }
                        });



                    }
                }
            }
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
    public void handleTriggerPress(boolean pressed) {
        if(pressed)
            if(is_search_on){
                //  btnSearch.setText("Start");
                is_search_on = false;
                rfidHandler.stopInventory();
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        // btnSearch.setText("Start");
                        // btnSearch.setBackground(getResources().getDrawable(R.drawable.login_button_background));
                    }
                });

            }else{
                performInventory();

            }

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
        rfidHandler.onDestroy();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void stopInventory(){
        //  btnSearch.setText("Start");
        // btnSearch.setBackground(getResources().getDrawable(R.drawable.login_button_background));
        is_search_on = false;
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
    public  void UploadAssetDetails(final JSONArray jsonobject3,String progress_message,String methodname) {

        showProgress(context,progress_message);
       /* OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .build();*/

        String URL = AppConstants.HOST_URL + AppConstants.POST_TAG_DETAILS;



        URL = SharedPreferencesManager.getHostUrl(context)  + methodname;
        Log.e("url",URL);
        Log.e("REQ",jsonobject3.toString());

        OkHttpClient okHttpClient = AppConstants.getUnsafeOkHttpClient();

        //Log.e("URL",AppConstants.HOST_URL + AppConstants.UPLOAD_INVENTORY_DETAILS);
        //AndroidNetworking.post(AppConstants.BASE_HOST + METHOD_NAME).addJSONObjectBody(jsonobject3)
        AndroidNetworking.post(URL).addJSONArrayBody(jsonobject3)
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
                                String success = result.getString("success");
                                if(success.equalsIgnoreCase("true")){
                                    ApplicationCommonMethods.showCustomSuccessDialog(context,"Details Uploaded Successfully");
                                    clearList();
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