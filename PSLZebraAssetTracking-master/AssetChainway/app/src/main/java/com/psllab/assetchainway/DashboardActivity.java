package com.psllab.assetchainway;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.psllab.assetchainway.apihelpers.APIKeys;
import com.psllab.assetchainway.databases.DatabaseHandler;
import com.psllab.assetchainway.helpers.AppConstants;
import com.psllab.assetchainway.helpers.BeepClass;
import com.psllab.assetchainway.helpers.ConnectionDetector;
import com.psllab.assetchainway.helpers.SharedPreferencesManager;
import com.psllab.assetchainway.modals.AssetMaster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.psllab.assetchainway.helpers.ApplicationCommonMethods.showCustomErrorDialog;

public class DashboardActivity extends AppCompatActivity {
    private Button btnRegistration,btnInventory,btnSearch,btnSync,btnSingleAssetScan,btnTouchPointAssetScan;
    private Context context = this;
    private DatabaseHandler db;
    private ConnectionDetector cd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().hide();

        cd = new ConnectionDetector(context);
        db = new DatabaseHandler(context);

        btnRegistration = (Button) findViewById(R.id.btnRegistration);
        btnInventory = (Button) findViewById(R.id.btnInventory);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSync = (Button) findViewById(R.id.btnSync);
        btnSingleAssetScan = (Button) findViewById(R.id.btnSingleAssetScan);
        btnTouchPointAssetScan = (Button) findViewById(R.id.btnTouchPointAssetScan);

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {

                    CallMasterSyncAPI(context,  AppConstants.GET_ASSET_MASTER, "Please wait...\nAsset Master Sync is in process");

                } else {
                    BeepClass.errorbeep(context);
                    showCustomErrorDialog(context, getResources().getString(R.string.inactive_internet));
                }
            }
        });


        btnSingleAssetScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.getAssetMasterCount()>0){
                    Intent regIntent = new Intent(DashboardActivity.this,SingleAssetScanActivity.class);
                    startActivity(regIntent);
                }else{
                    showCustomErrorDialog(context, "Please Sync Asset Master");
                }

            }
        });

        btnTouchPointAssetScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(db.getAssetMasterCount()>0){
                    Intent regIntent = new Intent(DashboardActivity.this,TouchpointAssetScanActivity.class);
                    startActivity(regIntent);

                }else{
                    showCustomErrorDialog(context, "Please Sync Asset Master");
                }
            }
        });

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(DashboardActivity.this,AssetRegistrationActivity.class);
                startActivity(regIntent);
            }
        });

        btnInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.getAssetMasterCount()>0){
                    Intent regIntent = new Intent(DashboardActivity.this,AssetInventoryActivity.class);
                    startActivity(regIntent);
                }else{
                    showCustomErrorDialog(context, "Please Sync Asset Master");
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent regIntent = new Intent(DashboardActivity.this,AssetSearchActivity.class);

                if(db.getAssetMasterCount()>0){
                    Intent regIntent = new Intent(DashboardActivity.this,PreSearchActivity.class);
                    startActivity(regIntent);
                }else{

                   /* Intent regIntent = new Intent(DashboardActivity.this,SearchActivity.class);
                    regIntent.putExtra("epc","10040000000450534C202020");
                    regIntent.putExtra("searchname","abc");
                    startActivity(regIntent);*/

                    showCustomErrorDialog(context, "Please Sync Asset Master");
                }
                // Intent regIntent = new Intent(DashboardActivity.this,PreSearchActivity.class);
                // startActivity(regIntent);
            }
        });
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
    public void CallMasterSyncAPI(final Context context, String METHOD_NAME, String progress_message) {

        showProgress(context,progress_message);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .build();
        Log.e("URL", SharedPreferencesManager.getHostUrl(context)  + METHOD_NAME);
        AndroidNetworking.get(SharedPreferencesManager.getHostUrl(context)  + METHOD_NAME)//.addJSONObjectBody(jsonobject3)
                .setTag("test")
                .setPriority(Priority.LOW)
                .setOkHttpClient(okHttpClient) // passing a custom okHttpClient
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray result) {
                        hideProgressDialog();
                        Log.e("ERROR",result.toString());
                        if (result != null) {
                            try {

                                List<AssetMaster> assetMasterList = new ArrayList<>();
                                for(int i=0;i<result.length();i++){
                                    AssetMaster assetMaster = new AssetMaster();
                                    JSONObject dataObject = result.getJSONObject(i);
                                    //DataField1,DataField2,DataField3,DataField4,TagID,SerialNo,UserID,TransactionDateTime;
                                    assetMaster.setCID(dataObject.getString(APIKeys.K_ASSET_CID));
                                    assetMaster.setRefID(dataObject.getString(APIKeys.K_ASSET_REF_ID));
                                    assetMaster.setName(dataObject.getString(APIKeys.K_ASSET_NAME));
                                    assetMaster.setDescription(dataObject.getString(APIKeys.K_ASSET_DESCRIPTION));
                                    assetMaster.setCategoryID(dataObject.getString(APIKeys.K_ASSET_CATAGORY_ID));
                                    assetMaster.setCategoryName(dataObject.getString(APIKeys.K_ASSET_CATAGORY_NAME));
                                    assetMaster.setDataField1(dataObject.getString(APIKeys.K_ASSET_DATAFIELD_1));
                                    assetMaster.setDataField2(dataObject.getString(APIKeys.K_ASSET_DATAFIELD_2));
                                    assetMaster.setDataField3(dataObject.getString(APIKeys.K_ASSET_DATAFIELD_3));
                                    assetMaster.setDataField4(dataObject.getString(APIKeys.K_ASSET_DATAFIELD_4));
                                    assetMaster.setTagID(dataObject.getString(APIKeys.K_ASSET_TAG_ID));
                                    assetMaster.setSerialNo(dataObject.getString(APIKeys.K_ASSET_SR_NO));
                                    assetMaster.setUserID(dataObject.getString(APIKeys.K_ASSET_USER_ID));
                                    assetMaster.setTransactionDateTime(dataObject.getString(APIKeys.K_ASSET_TRANSACTION_DATE_TIME));

                                    assetMasterList.add(assetMaster);
                                }

                                db.deleteAssetMaster();
                                db.storeAssetMaster(assetMasterList);

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

}