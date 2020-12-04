package com.zebra.pslsdksample;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.JsonArray;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.deviceapi.entity.SimpleRFIDEntity;
import com.zebra.pslsdksample.adapters.DestinationLocationsAdapter;
import com.zebra.pslsdksample.adapters.DestinationTouchPointAdapter;
import com.zebra.pslsdksample.adapters.MaterialAdapter;
import com.zebra.pslsdksample.baseuhf.BaseUHFActivity;
import com.zebra.pslsdksample.databases.DatabaseHandler;
import com.zebra.pslsdksample.helper.APIKeys;
import com.zebra.pslsdksample.helper.AppConstants;
import com.zebra.pslsdksample.helper.ApplicationCommonMethods;
import com.zebra.pslsdksample.helper.BeepClass;
import com.zebra.pslsdksample.helper.ConnectionDetector;
import com.zebra.pslsdksample.helper.GPSTracker;
import com.zebra.pslsdksample.helper.SharedPreferencesManager;
import com.zebra.pslsdksample.modals.DestinationLocations;
import com.zebra.pslsdksample.modals.DestinationTouchPoints;
import com.zebra.pslsdksample.modals.PSLUtils;
import com.zebra.pslsdksample.modals.TasksModal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

public class DestinationPointActivity extends BaseUHFActivity {
    private TextView textTruckNumber,textMaterial;
    private Spinner spLocation,spTouchPoint;
    private Button btnSave,btnClear,btnLock;
    private Context context = this;

    private String selected_location = "";
    private String selected_touch_point = "";
    private String selected_location_id = "0";
    private String selected_touch_point_id = "0";
    private String TransactionNumber = "0";

    List<DestinationLocations> destinationLocationsList;
    List<DestinationTouchPoints> destinationTouchPointsList;

    DestinationLocationsAdapter destinationLocationsAdapter;
    DestinationTouchPointAdapter destinationTouchPointAdapter;

    private ConnectionDetector cd;
    private boolean allow_to_press_trigger = true;
    private DatabaseHandler db;
    private GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_destination_point);

        gps = new GPSTracker(context);
        db = new DatabaseHandler(context);
        cd = new ConnectionDetector(context);
        findViews();

        initSound();
        initUHF();

        destinationLocationsList = new ArrayList<>();
        destinationTouchPointsList = new ArrayList<>();
        final DestinationLocations destinationLocations = new DestinationLocations();
        destinationLocations.setLocation_name("Select Location");
        destinationLocations.setLocation_id("0");
        destinationLocations.setLocation_code("0");
        if(destinationLocationsList!=null){
            destinationLocationsList.clear();
        }
        destinationLocationsList = db.getAllDistinctDestinationLocations();
        destinationLocationsList.add(0,destinationLocations);

        destinationLocationsAdapter = new DestinationLocationsAdapter(context,destinationLocationsList);
        spLocation.setAdapter(destinationLocationsAdapter);
        spLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                if(destinationTouchPointsList!=null){
                    destinationTouchPointsList.clear();
                }
                destinationTouchPointsList = db.getAllDistinctDestinationTouchPointsForLocation(destinationLocationsList.get(i).getLocation_id());

                selected_location = destinationLocationsList.get(i).getLocation_name();
                selected_location_id = destinationLocationsList.get(i).getLocation_id();


                if(destinationTouchPointsList.size()>0){
                    DestinationTouchPoints tasksModal = new DestinationTouchPoints();

                    tasksModal.setDest_id("0");
                    tasksModal.setDest_touch_point_id("0");
                    tasksModal.setDest_touch_point_name("Select Touch Point");

                    destinationTouchPointsList.add(0,tasksModal);
                }
                destinationTouchPointAdapter = new DestinationTouchPointAdapter(context,destinationTouchPointsList);
                spTouchPoint.setAdapter(destinationTouchPointAdapter);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spTouchPoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selected_touch_point_id = destinationTouchPointsList.get(i).getDest_touch_point_id();
                selected_touch_point = destinationTouchPointsList.get(i).getDest_touch_point_name();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_location.equalsIgnoreCase("0")){
                    Toast.makeText(context,"Please Select Location",Toast.LENGTH_SHORT).show();
                }else  if(selected_touch_point_id.equalsIgnoreCase("0")){
                    Toast.makeText(context,"Please Select Touch Point",Toast.LENGTH_SHORT).show();
                }else  if(textTruckNumber.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(context,"Please Read Truck Number",Toast.LENGTH_SHORT).show();
                }else{

                    if(cd.isConnectingToInternet()){
                        try {
                            JSONObject jsonObject = new JSONObject();
                            allow_to_press_trigger = false;
                            jsonObject.put("TruckID",textTruckNumber.getText().toString());
                            jsonObject.put("TouchPointType",AppConstants.DESTINATION_POINT_UPLOAD_TOUCH_POINT);//production D
                            jsonObject.put("TouchPointID",selected_touch_point_id);//taskid
                            jsonObject.put("LocationID",selected_location_id);//taskid
                            jsonObject.put("IsMobileLoading",true);//taskid
                           // jsonObject.put("TaskTypeID",selectedCastDetailModal.getCast_task_id());//taskid
                            //jsonObject.put("RouteID",selectedLaddle.getId());//laddleid
                            //jsonObject.put("STONumber",textTaskNumber.getText().toString());//taskid
                            //jsonObject.put("LotNumber",selectedLaddle.getDescription());//laddledesc
                            //jsonObject.put("VesselCode",selectedCastDetailModal.getCast_no());//castno
                            //jsonObject.put("BundurName",selectedCastDetailModal.getCast_location());//location
                            //jsonObject.put("PermitNo",selectedCastDetailModal.getCast_Prefix());//prefix
                            //jsonObject.put("PrintSrNo",selectedCastDetailModal.getCast_id());//castID
                            jsonObject.put("UserID", SharedPreferencesManager.getUserName(context));
                            UploadDestinationPoint("Please Wait ... \nUploading Transaction.", jsonObject);
                            Log.e("DESTUPLOAD",jsonObject.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                            allow_to_press_trigger = true;
                        }

                    }else{
                        allow_to_press_trigger = true;
                        Toast.makeText(context,context.getResources().getString(R.string.internet_error),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textTruckNumber.setText("");
                textMaterial.setText("");
                TransactionNumber = "";

                if(btnLock.getText().toString().equalsIgnoreCase("Lock")){
                    unlockAll();

                    spLocation.setSelection(0);
                    spLocation.setSelection(0);
                    spTouchPoint.setSelection(0);
                    selected_location = "";selected_touch_point = "";
                    selected_location_id = "0";
                    selected_touch_point_id = "0";
                    if(destinationTouchPointsList!=null){
                        destinationTouchPointsList.clear();
                    }
                    try{
                        destinationLocationsAdapter.notifyDataSetChanged();
                        destinationTouchPointAdapter.notifyDataSetChanged();
                        // destinationAdapter.notifyDataSetChanged();
                    }catch (Exception e){

                    }
                }else{

                }


                // finish();
                allow_to_press_trigger = true;

            }
        });
       btnLock.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(btnLock.getText().toString().equalsIgnoreCase("Lock")){
                   if(destinationLocationsList.size()<1){

                       Toast.makeText(context,"No Locations found",Toast.LENGTH_SHORT).show();
                   }else if(destinationTouchPointsList.size()<1){

                       Toast.makeText(context,"No Touch Points found",Toast.LENGTH_SHORT).show();
                   }else if(selected_location_id.equalsIgnoreCase("0")){
                       Toast.makeText(context,"Location Not Selected",Toast.LENGTH_SHORT).show();

                   }else if(selected_touch_point_id.equalsIgnoreCase("0")){
                       Toast.makeText(context,"Touch point Not Selected",Toast.LENGTH_SHORT).show();

                   }else{
                       btnLock.setText("Unlock");
                       btnLock.setBackground(getResources().getDrawable(R.drawable.login_button_background));
                       LockAll();
                       // unlockAll();
                   }
               }else{
                   btnLock.setText("Lock");
                   unlockAll();
               }
           }
       });



    }


    public void LockAll(){
        btnLock.setBackground(getResources().getDrawable(R.drawable.login_button_background));

        spTouchPoint.setEnabled(false);
        spTouchPoint.setClickable(false);
        //spMaterial.setOnItemSelectedListener(null);

        spLocation.setEnabled(false);
        spLocation.setClickable(false);
        //spTransactionType.setOnItemSelectedListener(null);
    }
    public void unlockAll(){
        btnLock.setBackground(getResources().getDrawable(R.drawable.clear_button_background));

        spTouchPoint.setEnabled(true);
        spTouchPoint.setClickable(true);
        // spMaterial.setOnItemSelectedListener(null);

        spLocation.setEnabled(true);
        spLocation.setClickable(true);
        // spTransactionType.setOnItemSelectedListener(null);
    }


    private void findViews(){
        spLocation = (Spinner) findViewById(R.id.spLocation);
        spTouchPoint = (Spinner) findViewById(R.id.spTouchPoint);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setVisibility(View.GONE);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnLock = (Button) findViewById(R.id.btnLock);

        textTruckNumber = (TextView) findViewById(R.id.textTruckNumber);
        textMaterial = (TextView) findViewById(R.id.textMaterial);
    }

    public boolean get_material_on_trigger_press = false;
    public boolean allow_to_upload_on_trigger_press = false;


    /**
     * Trigger Press
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 139 ||keyCode == 280 || keyCode == 142 || keyCode == 293) {
            if (event.getRepeatCount() == 0) {
                /**
                 * If inventory already started then stop it else start it.
                 * */
                if(textTruckNumber.getText().toString().trim().length()>4 && allow_to_press_trigger){
                    try {
                        if(textMaterial.getText().toString().trim().equalsIgnoreCase("")){
                            Toast.makeText(context,"Please Get Material Data",Toast.LENGTH_SHORT).show();

                        }else{
                            callUploadAPI();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        allow_to_press_trigger= true;
                    }
                }
                if (allow_to_press_trigger && textTruckNumber.getText().toString().trim().equalsIgnoreCase("")) {
                    SimpleRFIDEntity entity = mReader.readData("00000000", RFIDWithUHF.BankEnum.UII, AppConstants.EPC_FROM,AppConstants.EPC_LENGTH);
                    // SimpleRFIDEntity entity = mReader.readData("00000000", RFIDWithUHF.BankEnum.UII,2,15);
                    if (entity != null) {
                        final String tidData = entity.getData();
                        if(tidData!=null){
                            if(tidData.length()>20){
                                Log.e("TID",tidData);
                                //todo need to false
                                allow_to_press_trigger = true;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String data = ConvertHexStringToBinaryString(tidData);
                                            Log.e("DATA",data);
                                           // Log.e("DATA2",tidData);
                                            // Log.e("VehicleNUM",PSLUtils.ConvertBinaryStringToAsciiSeven(""));

                                            if(data.length()==240){



                                                String veh = data.substring(64,169);//from 65 length 105

                                                Log.e("Vehicle",veh);

                                                Log.e("VehicleNUM", PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0",""));
                                               // textTruckNumber.setText(PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0",""));
                                               // BeepClass.successbeep(context);

                                                String vehicle_number = PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0","");

                                                if(ApplicationCommonMethods.isValidTruckNumber(vehicle_number)){
                                                    textTruckNumber.setText(PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0",""));
                                                    BeepClass.successbeep(context);
                                                }else{
                                                    textTruckNumber.setText("");
                                                    BeepClass.errorbeep(context);
                                                }

                                                if(selected_location.equalsIgnoreCase("0")){
                                                    textTruckNumber.setText("");
                                                    textMaterial.setText("");
                                                    Toast.makeText(context,"Please Select Location",Toast.LENGTH_SHORT).show();
                                                }else  if(selected_touch_point_id.equalsIgnoreCase("0")){
                                                    textTruckNumber.setText("");
                                                    textMaterial.setText("");
                                                    Toast.makeText(context,"Please Select Touch Point",Toast.LENGTH_SHORT).show();
                                                }else  if(textTruckNumber.getText().toString().equalsIgnoreCase("")){
                                                    textTruckNumber.setText("");
                                                    textMaterial.setText("");
                                                    Toast.makeText(context,"Please Read Truck Number",Toast.LENGTH_SHORT).show();
                                                }else  if(textTruckNumber.getText().toString().trim().length()<5){
                                                    textTruckNumber.setText("");
                                                    textMaterial.setText("");
                                                    Toast.makeText(context,"Please Read Truck Number",Toast.LENGTH_SHORT).show();
                                                }else{
                                                      if(cd.isConnectingToInternet()){
                                                          GetMaterialForTruckNumber(getResources().getString(R.string.get_tasks_progress_message), textTruckNumber.getText().toString().trim());
                                                      }else{
                                                          //callUploadAPI();
                                                          Toast.makeText(context,context.getResources().getString(R.string.internet_error),Toast.LENGTH_SHORT).show();
                                                      }
                                                  }



                                                // textrfid.setText(PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0",""));

                                            }else{
                                                allow_to_press_trigger = true;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            BeepClass.errorbeep(context);
                                            allow_to_press_trigger = true;
                                            Log.e("EXC",e.getMessage());
                                        }
                                    }
                                });

                            }
                        }else{
                            allow_to_press_trigger = true;
                        }

                    }else{
                        allow_to_press_trigger = true;
                    }

                } else {

                }
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    public void callUploadAPI()  {
        allow_to_press_trigger = true;

        if(selected_location.equalsIgnoreCase("0")){
            Toast.makeText(context,"Please Select Location",Toast.LENGTH_SHORT).show();
        }else  if(selected_touch_point_id.equalsIgnoreCase("0")){
            Toast.makeText(context,"Please Select Touch Point",Toast.LENGTH_SHORT).show();
        }else  if(textTruckNumber.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(context,"Please Read Truck Number",Toast.LENGTH_SHORT).show();
        }else  if(textTruckNumber.getText().toString().trim().length()<5){
            Toast.makeText(context,"Please Read Truck Number",Toast.LENGTH_SHORT).show();
        }else {

            String vehicle_number = textTruckNumber.getText().toString().trim();
            if (cd.isConnectingToInternet()) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    allow_to_press_trigger = false;
                    jsonObject.put("TruckID", textTruckNumber.getText().toString());
                    jsonObject.put("TouchPointType", AppConstants.DESTINATION_POINT_UPLOAD_TOUCH_POINT);//production D
                    jsonObject.put("TouchPointID", selected_touch_point_id);//taskid
                    jsonObject.put("LocationID", selected_location_id);//taskid
                    jsonObject.put("TransactionNumber", TransactionNumber);//taskid

                   // jsonObject.put("IsMobileLoading", true);//taskid
                    jsonObject.put("PersonName", String.valueOf(gps.getLatitude()));//latitude
                    jsonObject.put("CompanyName",String.valueOf(gps.getLongitude()));//longitude
                    // jsonObject.put("TaskTypeID",selectedCastDetailModal.getCast_task_id());//taskid
                    //jsonObject.put("RouteID",selectedLaddle.getId());//laddleid
                    //jsonObject.put("STONumber",textTaskNumber.getText().toString());//taskid
                    //jsonObject.put("LotNumber",selectedLaddle.getDescription());//laddledesc
                    //jsonObject.put("VesselCode",selectedCastDetailModal.getCast_no());//castno
                    //jsonObject.put("BundurName",selectedCastDetailModal.getCast_location());//location
                    //jsonObject.put("PermitNo",selectedCastDetailModal.getCast_Prefix());//prefix
                    //jsonObject.put("PrintSrNo",selectedCastDetailModal.getCast_id());//castID
                    jsonObject.put("UserID", SharedPreferencesManager.getUserName(context));
                    UploadDestinationPoint("Please Wait ... \nUploading Transaction for\nVehicle :- "+vehicle_number, jsonObject);
                    Log.e("DESTUPLOAD", jsonObject.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                    allow_to_press_trigger = true;
                }

            } else {
                allow_to_press_trigger = true;
                Toast.makeText(context, context.getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
        }
    }
    public  String ConvertHexStringToBinaryString(String strHex)

            throws Exception {

        try {

            String binStr = "";

            int length = strHex.length();

            for (int i = 0; i < length; i++) {

                binStr += CharToBinaryString(strHex.charAt(i));

            }

            return binStr;


        } catch (Exception ex) {


        }

        return null;

    }



    public String CharToBinaryString(char c) throws Exception {

        try {

            switch (c) {

                case '0':

                    return "0000";

                case '1':

                    return "0001";

                case '2':

                    return "0010";

                case '3':

                    return "0011";

                case '4':

                    return "0100";

                case '5':

                    return "0101";

                case '6':

                    return "0110";

                case '7':

                    return "0111";

                case '8':

                    return "1000";

                case '9':

                    return "1001";

                case 'a':

                case 'A':

                    return "1010";

                case 'b':

                case 'B':

                    return "1011";

                case 'c':

                case 'C':

                    return "1100";

                case 'd':

                case 'D':

                    return "1101";

                case 'e':

                case 'E':

                    return "1110";

                case 'f':

                case 'F':

                    return "1111";

                default:

                    throw new Exception("Input is not a  Hex. string");

            }

        } catch (Exception ex) {



        }

        return "";

    }

    /**
     * method to get transactions data from server
     * */
    public  void UploadDestinationPoint(String progress_message,JSONObject jsonObject) {

        showProgress(context,progress_message);
       /* OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .build();*/

        String URL = "";

        URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.POST_TRIPDETAILS;

        OkHttpClient okHttpClient = AppConstants.getUnsafeOkHttpClient();
        Log.e("URL",URL);
        ////Log.e("URL",AppConstants.HOST_URL + AppConstants.UPLOAD_INVENTORY_DETAILS);
        AndroidNetworking.post(URL).addJSONObjectBody(jsonObject)
                // AndroidNetworking.get(URL)//.addJSONObjectBody(jsonobject3)
                //AndroidNetworking.get(SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_TRANSACTION_TYPES)//.addJSONObjectBody(jsonobject3)
                .setTag("test")
                .setPriority(Priority.LOW)
                .setOkHttpClient(okHttpClient) // passing a custom okHttpClient
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject result) {
                        hideProgressDialog();
                        allow_to_press_trigger = true;
                        if (result != null) {
                            try {
                                Log.e("RES",result.toString());
                                String success = result.getString("status");
                                String error = result.getString("message");
                                if(success.equals("true")){
                                    //TODO SUCCESS
                                    showCustomSuccessDialog("Transaction Saved Successfully");

                                }else{
                                    //TODO FAIL
                                    //  showCustomSuccessDialog(error);
                                    ApplicationCommonMethods.showCustomErrorDialog(context,error);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                //Log.e("RES",e.getMessage());
                            }
                        } else {
                            Toast.makeText(context,context.getResources().getString(R.string.communication_error),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideProgressDialog();
                        allow_to_press_trigger = true;
                        //Log.e("ERR",anError.getErrorDetail());
                        Toast.makeText(context,context.getResources().getString(R.string.inactive_internet),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    Dialog dialog,dialog2;
    public void showCustomSuccessDialog(String msg) {
        if(dialog!=null){
            dialog.dismiss();
        }
        if(dialog2!=null){
            dialog2.dismiss();
        }
        dialog2 = new Dialog(context);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setCancelable(false);
        dialog2.setContentView(R.layout.custom_alert_success_dialog_layout);
        TextView text = (TextView) dialog2.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) dialog2.findViewById(R.id.btn_dialog);
        dialogButton.setVisibility(View.GONE);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                btnClear.performClick();
            }
        });


        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        btnClear.performClick();
                    }
                });
               // btnClear.performClick();
                dialog2.dismiss();
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, AppConstants.DIALOG_DISMISS_COUNT); // the timer will count 5 seconds....

        dialog2.show();
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


    /**
     * method to get transactions data from server
     * */
    public  void GetMaterialForTruckNumber(String progress_message,String truck_number) {

        showProgress(context,progress_message);
       /* OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .build();*/
        OkHttpClient okHttpClient = AppConstants.getUnsafeOkHttpClient();

        // AndroidNetworking.post(SharedPreferencesManager.getHostUrl(context) + METHOD_NAME).addJSONObjectBody(jsonobject3)
        String URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.GET_MATERIAL_DETAILS_FOR_TRUCK_NUMBER+truck_number;

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
                        TransactionNumber = "";
                        if (result != null) {
                            try {
                                Log.e("RESMATERIAL",result.toString());
                                String success = result.getString("status");
                                String error = result.getString("message");
                                if(success.equals("true")){

                                    JSONArray jsonArray = result.getJSONArray("data");

                                        JSONObject dataObject = jsonArray.getJSONObject(0);
                                        String description = dataObject.getString("Description");
                                        String transnumber = dataObject.getString("TransactionNumber");
                                          TransactionNumber = transnumber;
                                        textMaterial.setText(description);

                                }else{
                                    Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                                    try {
                                      //  callUploadAPI();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                               // callUploadAPI();
                                Toast.makeText(context,context.getResources().getString(R.string.something_error),Toast.LENGTH_SHORT).show();
                                Log.e("RES",e.getMessage());
                            }
                        } else {
                           // callUploadAPI();
                            Toast.makeText(context,context.getResources().getString(R.string.communication_error),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideProgressDialog();
                       // callUploadAPI();
                        Log.e("ERR",anError.getErrorDetail());
                        Toast.makeText(context,context.getResources().getString(R.string.inactive_internet),Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
