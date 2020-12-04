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
import com.zebra.rfid.demo.pslsdksample.adapters.CastDetailAdapter;
import com.zebra.rfid.demo.pslsdksample.adapters.LaddleAdapter;
import com.zebra.rfid.demo.pslsdksample.adapters.MaterialAdapter;
import com.zebra.rfid.demo.pslsdksample.databases.DatabaseHandler;
import com.zebra.rfid.demo.pslsdksample.helper.AppConstants;
import com.zebra.rfid.demo.pslsdksample.helper.BeepClass;
import com.zebra.rfid.demo.pslsdksample.helper.ConnectionDetector;
import com.zebra.rfid.demo.pslsdksample.helper.SharedPreferencesManager;
import com.zebra.rfid.demo.pslsdksample.modals.CastDetail;
import com.zebra.rfid.demo.pslsdksample.modals.Laddle;
import com.zebra.rfid.demo.pslsdksample.modals.PSLUtils;
import com.zebra.rfid.demo.pslsdksample.modals.TasksModal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

public class AssignmentActivity extends AppCompatActivity implements AssignmentRFIDHandler.ResponseHandlerInterface{

    AssignmentRFIDHandler rfidHandler;
    final static String TAG = "RFID_SAMPLE";
    private Context context = this;

    public TextView textStatus;
    private TextView textTruckNumber;
    private Spinner spLocation;
    private Spinner spLaddle;
    private TextView textTaskNumber;
    private TextView textCastNumber;
    private Button btnSave;
    private Button btnClear;
    private Button btnBack;
    private TextView textLocationDetails;
    private boolean allow_to_press_trigger = true;

    private String selected_catst_location = "Select Location";
    private String selected_laddle = "Select Laddle";

    String[] laddle_id = {"0","1","2","3","4","5"};
    String[] laddle_description = {"Select Laddle","L1","L2","L3","L4","L5"};

    List<CastDetail> castList;
    List<Laddle> laddleList;
    private DatabaseHandler db;
    private CastDetailAdapter castAdapter;
    private LaddleAdapter laddleAdapter;
    private CastDetail selectedCastDetailModal = null;
    private Laddle selectedLaddle;
    private ConnectionDetector cd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_assignment);
        db = new DatabaseHandler(context);
        cd = new ConnectionDetector(context);
        findViews();
        // RFID Handler
        rfidHandler = new AssignmentRFIDHandler();
        rfidHandler.onCreate(this);

        castList = db.getAllCastList();
        laddleList = new ArrayList<>();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_catst_location.equalsIgnoreCase("Select Location")){
                    Toast.makeText(context,"Please Select Location",Toast.LENGTH_SHORT).show();
                }else  if(selected_laddle.equalsIgnoreCase("Select Laddle")){
                    Toast.makeText(context,"Please Select Laddle",Toast.LENGTH_SHORT).show();
                }else  if(textTruckNumber.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(context,"Please Read Truck Number",Toast.LENGTH_SHORT).show();
                }else  if(textTaskNumber.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(context,"Task Number cannot be Empty",Toast.LENGTH_SHORT).show();
                }else  if(textCastNumber.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(context,"Cast Number cannot be Empty",Toast.LENGTH_SHORT).show();
                }else{

                    if(cd.isConnectingToInternet()){
                        try {
                            JSONObject jsonObject = new JSONObject();

                            jsonObject.put("TruckID",textTruckNumber.getText().toString());
                            jsonObject.put("TouchPointType",AppConstants.CAST_ASSIGNMENT_UPLOAD_TOUCH_POINT);//production M
                            jsonObject.put("TaskTypeID",selectedCastDetailModal.getCast_task_id());//taskid
                            jsonObject.put("RouteID",selectedLaddle.getId());//laddleid
                            jsonObject.put("STONumber",textTaskNumber.getText().toString());//taskid
                            jsonObject.put("LotNumber",selectedLaddle.getDescription());//laddledesc
                            jsonObject.put("VesselCode",selectedCastDetailModal.getCast_no());//castno
                            jsonObject.put("IsMobileLoading",true);//taskid
                            jsonObject.put("BundurName",selectedCastDetailModal.getCast_location());//location
                            jsonObject.put("PermitNo",selectedCastDetailModal.getCast_Prefix());//prefix
                            jsonObject.put("PrintSrNo",selectedCastDetailModal.getCast_id());//castID
                            jsonObject.put("UserID", SharedPreferencesManager.getUserName(context));
                            UploadAssignment("Please Wait ... \nUploading Transaction.", jsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else{
                        Toast.makeText(context,context.getResources().getString(R.string.internet_error),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        CastDetail castDetail = new CastDetail();
        castDetail.setCast_id("");
        castDetail.setCast_Prefix("");
        castDetail.setCast_task_id("");
        castDetail.setCast_no("");
        castDetail.setCast_task_no("");
        castDetail.setCast_location("Select Location");
        castList.add(0,castDetail);
        selectedCastDetailModal = castDetail;

        for(int i=0;i<laddle_id.length;i++){
            Laddle laddle = new Laddle();
            laddle.setId(laddle_id[i]);
            laddle.setDescription(laddle_description[i]);
            laddleList.add(laddle);
        }
        selectedLaddle = laddleList.get(0);

        castAdapter = new CastDetailAdapter(context,castList);
        spLocation.setAdapter(castAdapter);

        laddleAdapter = new LaddleAdapter(context,laddleList);
        spLaddle.setAdapter(laddleAdapter);


        spLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_catst_location = castList.get(i).getCast_location();
                selectedCastDetailModal = castList.get(i);

                textTaskNumber.setText(selectedCastDetailModal.getCast_task_no());
                textCastNumber.setText(selectedCastDetailModal.getCast_no());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spLaddle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_laddle = laddleList.get(i).getDescription();
                selectedLaddle = laddleList.get(i);

                //textTaskNumber.setText(selectedCastDetailModal.getCast_task_no());
               // textCastNumber.setText(selectedCastDetailModal.getCast_no());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textTaskNumber.setText("");
                textTruckNumber.setText("");
                textCastNumber.setText("");
                spLocation.setSelection(0);
                spLaddle.setSelection(0);
                //finish();

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textTaskNumber.setText("");
                textTruckNumber.setText("");
                textCastNumber.setText("");
                spLocation.setSelection(0);
                spLaddle.setSelection(0);
                finish();
            }
        });






    }

    private void findViews() {
        textStatus = (TextView) findViewById(R.id.textStatus);
        textTruckNumber = (TextView)findViewById( R.id.textTruckNumber );
        spLocation = (Spinner)findViewById( R.id.spLocation );
        spLaddle = (Spinner)findViewById( R.id.spLaddle );
        textTaskNumber = (TextView)findViewById( R.id.textTaskNumber );
        textCastNumber = (TextView)findViewById( R.id.textCastNumber );
        btnSave = (Button)findViewById( R.id.btnSave );
        btnSave.setVisibility(View.GONE);
        btnClear = (Button)findViewById( R.id.btnClear );
        btnBack = (Button)findViewById( R.id.btnBack );
        textLocationDetails = (TextView)findViewById( R.id.textLocationDetails );
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
                    Log.e("DATA111111",data);
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
                   // textTruckNumber.setText("");
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
                        Log.e("DATA2222222", data);
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
                            if(selected_catst_location.equalsIgnoreCase("Select Location")){
                                Toast.makeText(context,"Please Select Location",Toast.LENGTH_SHORT).show();
                            }else  if(selected_laddle.equalsIgnoreCase("Select Laddle")){
                                Toast.makeText(context,"Please Select Laddle",Toast.LENGTH_SHORT).show();
                            }else  if(textTruckNumber.getText().toString().equalsIgnoreCase("")){
                                Toast.makeText(context,"Please Read Truck Number",Toast.LENGTH_SHORT).show();
                            }else  if(textTruckNumber.getText().toString().length()<4){
                                Toast.makeText(context,"Please Read Truck Number",Toast.LENGTH_SHORT).show();
                            }else  if(textTaskNumber.getText().toString().equalsIgnoreCase("")){
                                Toast.makeText(context,"Task Number cannot be Empty",Toast.LENGTH_SHORT).show();
                            }else  if(textCastNumber.getText().toString().equalsIgnoreCase("")){
                                Toast.makeText(context,"Cast Number cannot be Empty",Toast.LENGTH_SHORT).show();
                            }else{

                                if(cd.isConnectingToInternet()){
                                    try {
                                        JSONObject jsonObject = new JSONObject();

                                        jsonObject.put("TruckID",textTruckNumber.getText().toString());
                                        jsonObject.put("TouchPointType",AppConstants.CAST_ASSIGNMENT_UPLOAD_TOUCH_POINT);//production M
                                        jsonObject.put("TaskTypeID",selectedCastDetailModal.getCast_task_id());//taskid
                                        jsonObject.put("RouteID",selectedLaddle.getId());//laddleid
                                        jsonObject.put("STONumber",textTaskNumber.getText().toString());//taskid
                                        jsonObject.put("LotNumber",selectedLaddle.getDescription());//laddledesc
                                        jsonObject.put("VesselCode",selectedCastDetailModal.getCast_no());//castno
                                        jsonObject.put("IsMobileLoading",true);//taskid
                                        jsonObject.put("BundurName",selectedCastDetailModal.getCast_location());//location
                                        jsonObject.put("PermitNo",selectedCastDetailModal.getCast_Prefix());//prefix
                                        jsonObject.put("PrintSrNo",selectedCastDetailModal.getCast_id());//castID
                                        jsonObject.put("UserID", SharedPreferencesManager.getUserName(context));
                                        UploadAssignment("Please Wait ... \nUploading Transaction for\nVehicle:- "+vehicle_number, jsonObject);

                                        allow_to_press_trigger = false;
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
                        allow_to_press_trigger = true;
                        // textTruckNumber.setText("");
                    }                }
            });



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

    /**
     * method to get transactions data from server
     * */
    public  void UploadAssignment(String progress_message,JSONObject jsonObject) {

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
                                    showCustomSuccessDialog(error);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                allow_to_press_trigger = true;
                                //Log.e("RES",e.getMessage());
                            }
                        } else {
                            allow_to_press_trigger = true;
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
    public void showCustomErrorDialog(String msg) {
        if(dialog!=null){
            dialog.dismiss();
        }
        if(dialog2!=null){
            dialog2.dismiss();
        }
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_dialog_layout);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogButton.setVisibility(View.GONE);
        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                dialog.dismiss();
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, AppConstants.DIALOG_DISMISS_COUNT); // the timer will count 5 seconds....

        dialog.show();
    }


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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         btnClear.performClick();

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

}
