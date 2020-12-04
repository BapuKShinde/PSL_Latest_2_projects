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
import com.psllab.assetchainway.helpers.ConnectionDetector;
import com.psllab.assetchainway.helpers.GPSTracker;
import com.psllab.assetchainway.helpers.SharedPreferencesManager;
import com.psllab.assetchainway.helpers.StringUtils;
import com.psllab.assetchainway.modals.AssetMaster;
import com.psllab.assetchainway.rfidbase.BaseUHFActivity;

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

public class AssetInventoryActivity extends BaseUHFActivity {

    //UHF
    private boolean loopFlag = false;
  //  private int inventoryFlag = 2;//1 anti, 2- anti collision, 0-single

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

    private Timer beepTimer;
    private int valid_speed = 0;
    private int speed = 0;
    private String inventory_mode = "0";
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_asset_inventory);


        initSound();//sound initialization
        initUHF(); //��ʼ�� uhf initialization


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
                if(loopFlag){
                    //  btnSearch.setText("Start");
                    loopFlag = false;
                    stopInventory();
                   // rfidHandler.stopInventory();
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


        beepTimer = new Timer();
        beepTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //Called each time when 1000 milliseconds (1 second) (the period parameter)
                if (loopFlag) {
                    if (valid_speed > 0) {
                        SuccessBeep();
                    }
                   // fr.rate = String.valueOf(speed) + " Tag(s)/Sec";
                   // binding.setStockCountFrequencyModel(fr);
                    speed = 0;
                    valid_speed = 0;
                    //ShowColorByPower();
                } else {
                    //ResetColorByPower();
                }

            }

        }, 0, AppConstants.BEEP_TIMER_LIMIT);

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

    @Override
    protected void onDestroy() {


        if (mReader != null) {
            mReader.free();
        }

        gps.stopUsingGPS();

        super.onDestroy();
        if (beepTimer != null) {
            beepTimer.cancel();
        }

        if (epcList != null) {
            epcList.clear();
        }

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
                if (!loopFlag) {
                    readTag();
                   // binding.swInventory.setChecked(true);
                } else {
                    stopInventory();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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


    /**
     * Play sound
     */
    public void SuccessBeep() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playSound(1);
            }
        });
    }

    /**
     * Add valid data in List
     */
    private void addEPCToList(final String EPCVALUE,final String tid, final String rssi) {
        try {
            if (!TextUtils.isEmpty(EPCVALUE)) {
                final String epc = EPCVALUE;
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

                        valid_speed++;

                    }
                }
            }
        } catch (Exception e) {

        }
    }
    /**
     * stop inventoey
     */
    private void stopInventory() {

        if (loopFlag) {
            loopFlag = false;
            if (mReader.stopInventory()) {

            } else {

            }
        }
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
            if (inventory_mode.equals("0")) {
                mReader.setEPCTIDMode(false);
            } else {
                mReader.setEPCTIDMode(true);
            }
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

      /*  public void run() {
            String[] res = null;
            while (loopFlag) {
                res = mReader.readTagFromBuffer();//.readTagFormBuffer();
                if (res != null) {
                    Message msg = handler.obtainMessage();
                    msg.obj = mReader.convertUiiToEPC(res[1]) + "@" + res[2];
                    speed++;
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
        }*/

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
                    speed++;
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