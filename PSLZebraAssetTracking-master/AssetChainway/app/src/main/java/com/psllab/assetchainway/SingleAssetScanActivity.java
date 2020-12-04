package com.psllab.assetchainway;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.psllab.assetchainway.adapters.AssetInventoryAdapter;
import com.psllab.assetchainway.apihelpers.APIKeys;
import com.psllab.assetchainway.databases.DatabaseHandler;
import com.psllab.assetchainway.helpers.AppConstants;
import com.psllab.assetchainway.helpers.ApplicationCommonMethods;
import com.psllab.assetchainway.helpers.BeepClass;
import com.psllab.assetchainway.helpers.ConnectionDetector;
import com.psllab.assetchainway.helpers.GPSTracker;
import com.psllab.assetchainway.helpers.SharedPreferencesManager;
import com.psllab.assetchainway.helpers.StringUtils;
import com.psllab.assetchainway.modals.AssetMaster;
import com.psllab.assetchainway.rfidbase.BaseUHFLowPowerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import okhttp3.OkHttpClient;

public class SingleAssetScanActivity extends BaseUHFLowPowerActivity {
    final static String TAG = "RFID_SAMPLE";
    public boolean loopFlag = false;

    private ConnectionDetector cd;
    private DatabaseHandler db;
    private Context context = this;
    private GPSTracker gps;


    private RecyclerView list;
    private TextView textValidCount,textLocation;
    private List<AssetMaster> assetMasterList;
    private Set<String> epcList;
    private AssetInventoryAdapter assetInventoryAdapter;

    private Timer beepTimer;
    private Button btnClear;
    int timer_count = 0;

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_single_asset_scan);

        getSupportActionBar().hide();

        initSound();//sound initialization
        initUHF(); //��ʼ�� uhf initialization

        db = new DatabaseHandler(context);
        cd = new ConnectionDetector(context);
        gps = new GPSTracker(context);

        assetMasterList = new ArrayList<AssetMaster>();
        epcList = new TreeSet<>();


        list = (RecyclerView)findViewById(R.id.list);
        // btnSave = (Button) findViewById(R.id.btnSave);
        btnClear = (Button) findViewById(R.id.btnClear);
        //btnStop = (Button) findViewById(R.id.btnStop);
        textValidCount = (TextView) findViewById(R.id.textValidCount);
        textLocation = (TextView) findViewById(R.id.textLocation);
        textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));

        textValidCount.setText("0");

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearList();
            }
        });

        assetInventoryAdapter = new AssetInventoryAdapter(context,assetMasterList);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(assetInventoryAdapter);

        clearList();


        /**
         * Handler which accepts data from worker thread
         * */
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // Bundle bundle = msg.getData();
                // String tagEPC = bundle.getString("tagEPC");
                String result = msg.obj + "";
                String[] strs = result.split("@");
                try{
                    addEPCToList(strs[0], strs[1],strs[2]);
                }catch (Exception e){

                }

            }
        };

    }

    /**
     * Add valid data in List
     */
    private void addEPCToList(final String EPCVALUE,final String tid, final String rssi) {
        try {
            if (!TextUtils.isEmpty(EPCVALUE)) {
               final String epc = EPCVALUE;
                if(!epc.isEmpty()){
                    Log.e("DATA",epc);
                    //TODO add data in adapterrunOn

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));
                        }
                    });


                    if(!epcList.contains(epc)){
                        if(epc.length()>20){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    epcList.add(epc);
                                    //for(int i=0;i<epcList.size();i++){
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
                                    // assetInventoryAdapter.notifyDataSetChanged();
                                    // itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, epcList);
                                    // list.setAdapter(itemsAdapter);
                                }
                            });

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textValidCount.setText(String.valueOf(epcList.size()));
                                    assetInventoryAdapter.notifyDataSetChanged();
                                    textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));

                                }
                            });
                            // assetInventoryAdapter.notifyDataSetChanged();

                            if(timer_count>2){
                                stopInventory();
                            }

                            if(epcList.size()>1){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        stopInventory();
                                        ApplicationCommonMethods.showCustomErrorDialog(context, "Multiple Tags Found, please try again");
                                    }
                                });
                                //ApplicationCommonMethods.showCustomErrorDialog(context, "Multiple Tags Found,please try again");
                                clearList();
                            }


                        }
                    }
                }
            }
        } catch (Exception e) {

        }
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textLocation.setText(String.valueOf(gps.getLatitude()+","+gps.getLongitude()));
                textValidCount.setText(String.valueOf(epcList.size()));
                assetInventoryAdapter.notifyDataSetChanged();

            }
        });


    }

    private void performInventory(){
        if(!loopFlag){
            readTag();
        }
    }

    /**
     * inventory configuration
     */
    int inventoryFlag = 1;//0-single inventory,1-inventory anti,2-inventory anti collision

    private void readTag() {
        {

            mReader.setEPCTIDMode(false);

            switch (inventoryFlag) {

                case 1:// 单标签循环 -- Single label cycle
                {
                    if (mReader.startInventoryTag((byte) 0, (byte) 0)) {
                        loopFlag = true;
                        new TagThread(StringUtils.toInt("5", 0)).start();
                    } else {
                        mReader.stopInventory();
                    }
                }
                break;
                case 2:// 防碰撞 anticollision
                {
                    //int initQ = Byte.valueOf((String) SpinnerQ.getSelectedItem(), 10);
                    int initQ = Byte.valueOf(String.valueOf(3), 10);
                    if (mReader.startInventoryTag((byte) 1, initQ)) {
                        loopFlag = true;
                        new TagThread(StringUtils.toInt("5", 0)).start();
                    } else {
                        mReader.stopInventory();
                    }
                }
                break;
                default:
                    break;
            }
        }
    }

    /**
     * Worker thread to read data from reader
     */
    class TagThread extends Thread {
        private int mBetween = 10;

        public TagThread(int iBetween) {
            mBetween = iBetween;
        }
        public void run() {
            String strTid;
            String strResult;
            String[] res = null;
            while (loopFlag) {
                res = mReader.readTagFromBuffer();//.readTagFormBuffer();
                if (res != null) {
                    strTid = res[0];
                   /* if (!strTid.equals("0000000000000000") && !strTid.equals("000000000000000000000000")) {
                        if (inventory_mode.equals("0")) {
                            strResult = "FFFFFFFFFFFFFFFFFFFFFFFF";
                        } else {
                            strResult = strTid;
                        }
                    } else {
                        strResult = "FFFFFFFFFFFFFFFFFFFFFFFF";
                    }*/
                    strResult = "FFFFFFFFFFFFFFFFFFFFFFFF";
                    Message msg = handler.obtainMessage();
                    msg.obj = mReader.convertUiiToEPC(res[1]) + "@" + strResult + "@" + res[2];
                   // speed++;
                    handler.sendMessage(msg);
                    msg = null;
                }
                try {
                    sleep(mBetween);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * Trigger Press
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 139 || keyCode == 280 || keyCode == 142 || keyCode == 293) {
            if (event.getRepeatCount() == 0) {
                /**
                 * If inventory already started then stop it else start it.
                 * */
               /* if (!loopFlag) {
                    performInventory();
                    //readTag();
                    // binding.swInventory.setChecked(true);
                } else {
                    stopInventory();
                }*/




                timer_count = 0;
                if(epcList.size()==0){
                    performInventory();

                    beepTimer = new Timer();
                    beepTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            timer_count = timer_count+1;
                        }

                    }, 0, AppConstants.BEEP_TIMER_LIMIT);

                }
                if(epcList.size()==1){
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

                                    post_dict.put(APIKeys.K_ASSET_UPLOAD_DETAILS_TOUCH_POINT_ID,"0");
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
                            stopInventory();
                            ApplicationCommonMethods.showCustomErrorDialog(context, "Multiple Tags Found, please try again");
                        }
                    });
                    //ApplicationCommonMethods.showCustomErrorDialog(context, "Multiple Tags Found, please try again");
                    clearList();
                }




            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void stopInventory(){
        //  btnSearch.setText("Start");
        // btnSearch.setBackground(getResources().getDrawable(R.drawable.login_button_background));
      //  is_search_on = false;
        timer_count = 0;
        if (beepTimer != null) {
            beepTimer.cancel();
        }

        if (loopFlag) {
            loopFlag = false;
            if (mReader.stopInventory()) {

            } else {

            }
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
     * method to get transactions data from server
     * */
    public  void UploadAssetDetails(final JSONObject jsonobject3, String progress_message, String methodname) {

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