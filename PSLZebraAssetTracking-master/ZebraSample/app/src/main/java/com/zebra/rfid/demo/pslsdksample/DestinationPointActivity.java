package com.zebra.rfid.demo.pslsdksample;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.zebra.rfid.api3.TagData;
import com.zebra.rfid.demo.pslsdksample.adapters.DestinationLocationsAdapter;
import com.zebra.rfid.demo.pslsdksample.adapters.DestinationTouchPointAdapter;
import com.zebra.rfid.demo.pslsdksample.databases.DatabaseHandler;
import com.zebra.rfid.demo.pslsdksample.helper.AppConstants;
import com.zebra.rfid.demo.pslsdksample.helper.ApplicationCommonMethods;
import com.zebra.rfid.demo.pslsdksample.helper.BeepClass;
import com.zebra.rfid.demo.pslsdksample.helper.ConnectionDetector;
import com.zebra.rfid.demo.pslsdksample.helper.SharedPreferencesManager;
import com.zebra.rfid.demo.pslsdksample.modals.DestinationLocations;
import com.zebra.rfid.demo.pslsdksample.modals.DestinationTouchPoints;
import com.zebra.rfid.demo.pslsdksample.modals.PSLUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

public class DestinationPointActivity extends AppCompatActivity implements DestinationPointRFIDHandler.ResponseHandlerInterface{

    DestinationPointRFIDHandler rfidHandler;
    final static String TAG = "RFID_SAMPLE";

    private TextView textTruckNumber;
    private Spinner spLocation,spTouchPoint;
    private Button btnSave,btnClear,btnLock;
    private Context context = this;

    private String selected_location = "";
    private String selected_touch_point = "";
    private String selected_location_id = "0";
    private String selected_touch_point_id = "0";

    List<DestinationLocations> destinationLocationsList;
    List<DestinationTouchPoints> destinationTouchPointsList;

    DestinationLocationsAdapter destinationLocationsAdapter;
    DestinationTouchPointAdapter destinationTouchPointAdapter;

    private ConnectionDetector cd;
    private boolean allow_to_press_trigger = true;
    private DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_destination_point);
        db = new DatabaseHandler(context);
        cd = new ConnectionDetector(context);
        findViews();

        rfidHandler = new DestinationPointRFIDHandler();
        rfidHandler.onCreate(this);

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
                            jsonObject.put("TouchPointType", AppConstants.DESTINATION_POINT_UPLOAD_TOUCH_POINT);//production D
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




                if(btnLock.getText().toString().equalsIgnoreCase("Lock")){
                    spLocation.setSelection(0);
                    if(destinationTouchPointsList!=null){
                        destinationTouchPointsList.clear();
                    }
                    try{
                        destinationLocationsAdapter.notifyDataSetChanged();
                        destinationTouchPointAdapter.notifyDataSetChanged();
                        // destinationAdapter.notifyDataSetChanged();
                    }catch (Exception e){

                    }
                    unlockAll();
                    spLocation.setSelection(0);
                    spTouchPoint.setSelection(0);
                    selected_location = "";selected_touch_point = "";
                    selected_location_id = "0";
                    selected_touch_point_id = "0";
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
    @Override
    public void handleTagdata(TagData[] tagData) {
        final StringBuilder sb = new StringBuilder();
        for (int index = 0; index < tagData.length; index++) {
            sb.append(tagData[index].getTagID() + "\n");
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data = ConvertHexStringToBinaryString(sb.toString());
                    Log.e("DATA11111111",data);
                    Log.e("DATA2",sb.toString());
                    // Log.e("VehicleNUM",PSLUtils.ConvertBinaryStringToAsciiSeven(""));

                    if(data.length()==240){
                        String veh = data.substring(64,169);//from 65 length 105

                        Log.e("Vehicle",veh);
                        Log.e("VehicleNUM", PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0",""));
                        textTruckNumber.setText(PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0",""));
                        BeepClass.successbeep(context);
                        // textrfid.setText(PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0",""));


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                  //  textTruckNumber.setText("");
                    BeepClass.errorbeep(context);
                }

            }
        });

    }

    @Override
    public void handleTriggerPress(boolean pressed) {
        if(allow_to_press_trigger) {
            String result1 = "0";
            int count = 0;
            result1 = rfidHandler.ReadAccess();

            if (result1 != null) {
                count++;
                if (result1.equals("a")) {
                    if (count == 1) {
                        result1 = rfidHandler.ReadAccess();
                    }

                }
            }

            final String result = result1;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String data = ConvertHexStringToBinaryString(result);
                        Log.e("DATA222222", data);
                        Log.e("DATA2", result);
                        // Log.e("VehicleNUM",PSLUtils.ConvertBinaryStringToAsciiSeven(""));

                        if (data.length() == 240) {
                            String veh = data.substring(64, 169);//from 65 length 105

                            Log.e("Vehicle", veh);

                            Log.e("VehicleNUM", PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0", ""));
                            textTruckNumber.setText(PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0", ""));
                            BeepClass.successbeep(context);
                            // textrfid.setText(PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0",""));

                            String vehicle_number = PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0", "");

                            allow_to_press_trigger = false;
                            if(selected_location.equalsIgnoreCase("0")){
                                Toast.makeText(context,"Please Select Location",Toast.LENGTH_SHORT).show();
                            }else  if(selected_touch_point_id.equalsIgnoreCase("0")){
                                Toast.makeText(context,"Please Select Touch Point",Toast.LENGTH_SHORT).show();
                            }else  if(textTruckNumber.getText().toString().equalsIgnoreCase("")){
                                Toast.makeText(context,"Please Read Truck Number",Toast.LENGTH_SHORT).show();
                            }else  if(textTruckNumber.getText().toString().length()<4){
                                Toast.makeText(context,"Please Read Truck Number",Toast.LENGTH_SHORT).show();
                            }else{

                                if(cd.isConnectingToInternet()){
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        allow_to_press_trigger = false;
                                        jsonObject.put("TruckID",textTruckNumber.getText().toString());
                                        jsonObject.put("TouchPointType", AppConstants.DESTINATION_POINT_UPLOAD_TOUCH_POINT);//production D
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        BeepClass.errorbeep(context);
                        //  textTruckNumber.setText("");
                        allow_to_press_trigger = true;
                    }
                }
            });




        }


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
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                btnClear.performClick();
            }
        });
        dialogButton.setVisibility(View.GONE);
        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
               /* if(btnLock.getText().toString().equalsIgnoreCase("UNLOCK")){
                    btnLock.setText("Unlock");
                    btnLock.setBackground(getResources().getDrawable(R.drawable.login_button_background));
                    LockAll();
                }else{
                    btnClear.performClick();
                }*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // btnClear.performClick();
                        if(btnLock.getText().toString().equalsIgnoreCase("UNLOCK")){
                            btnLock.setText("Unlock");
                            btnLock.setBackground(getResources().getDrawable(R.drawable.login_button_background));
                            LockAll();
                        }else{
                            btnClear.performClick();
                        }
                    }
                });
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

}
