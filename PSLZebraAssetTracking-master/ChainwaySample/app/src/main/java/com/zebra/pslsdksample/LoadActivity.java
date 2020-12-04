package com.zebra.pslsdksample;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.deviceapi.entity.SimpleRFIDEntity;
import com.zebra.pslsdksample.adapters.DestinationAdapter;
import com.zebra.pslsdksample.adapters.MaterialAdapter;
import com.zebra.pslsdksample.adapters.SourceAdapter;
import com.zebra.pslsdksample.adapters.TransactionTypesAdapter;
import com.zebra.pslsdksample.baseuhf.BaseUHFActivity;
import com.zebra.pslsdksample.databases.DatabaseHandler;
import com.zebra.pslsdksample.helper.AppConstants;
import com.zebra.pslsdksample.helper.ApplicationCommonMethods;
import com.zebra.pslsdksample.helper.BeepClass;
import com.zebra.pslsdksample.helper.ConnectionDetector;
import com.zebra.pslsdksample.helper.SharedPreferencesManager;
import com.zebra.pslsdksample.modals.PSLUtils;
import com.zebra.pslsdksample.modals.TasksModal;
import com.zebra.pslsdksample.modals.TransactionTypes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

public class LoadActivity extends BaseUHFActivity implements View.OnClickListener{


    final static String TAG = "RFID_SAMPLE";
    private Context context = this;
    //public TextView textStatus;
    private TextView textStatus;
    private Spinner spTransactionType;
    private Spinner spMaterial;
    private Spinner spSource;
    private Spinner spDestination;
    private TextView textTaskNumber;
    private TextView textTruckNumber;
    private Button btnDetails;
    private Button btnSave;
    private Button btnClear;
    private Button btnBack;
    private Button btnLock;

    private TransactionTypesAdapter transactionTypesAdapter;
    private MaterialAdapter materialAdapter;
    private SourceAdapter sourceAdapter;
    private DestinationAdapter destinationAdapter;
    private List<TransactionTypes> transactionTypesList;
    private List<TasksModal> materialList;
    private List<TasksModal> sourceList;
    private List<TasksModal> destinationList;
    DatabaseHandler db;
    private ConnectionDetector cd;

    String selected_transaction_type = "";
    String selected_transaction_type_id = "";
    String selected_material = "";
    String selected_source = "";
    String selected_source_plant = "";
    String selected_source_batch = "";
    String selected_destination = "";
    String selected_destination_plant = "";
    String selected_destination_batch = "";
    String source_plant = "Select Source Plant";
    String inquery = "";
    boolean allow_to_press_trigger = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_load);

        db = new DatabaseHandler(context);
        cd = new ConnectionDetector(context);
        findViews();
        initSound();
        initUHF();

        source_plant = getIntent().getStringExtra("source_plant");
        inquery = getIntent().getStringExtra("inquery");

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textTaskNumber.getText().toString().equals("") || textTaskNumber.getText().toString().length()<3){
                    Toast.makeText(context, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                }else{
                    TasksModal tasksModal = db.getTaskDetailsByTaskunumber(textTaskNumber.getText().toString().trim());

                    if(tasksModal!=null){

                        String[] taskdetails = new String[11];
                        String tasknuber = " Task No. : "+textTaskNumber.getText().toString();
                        String transtype = " Trns Type : "+db.getTransactiontypeByTransactionID(tasksModal.getTask_transaction_id());
                        String material =  "Material : "+tasksModal.getTask_product();

                        String date = tasksModal.getTask_date();
                        date = date.replace('T',' ');

                        if(date.contains(".")){
                            if(date.length()>19){
                                date = date.substring(0,19);
                            }else{
                                date = date.split(".")[0];
                            }
                        }

                        String taskdate = " Task date : "+date;
                        String sourcelocation = " Source Loc : "+tasksModal.getTask_source_location();
                        String sourcebatch = " Source Batch : "+tasksModal.getTask_source_batch();
                        String sourceplant = " Source Plant : "+tasksModal.getTask_source_plant();

                        String destlocation = " Dest Loc : "+tasksModal.getTask_destination_location();
                        String destbatch = " Dest Batch : "+tasksModal.getTask_destination_batch();
                        String destplant = " Dest Plant : "+tasksModal.getTask_destination_plant();

                        String description = " Description : "+tasksModal.getTask_description();




                        taskdetails[0] = tasknuber;
                        taskdetails[1] = transtype;
                        taskdetails[2] = material;
                        taskdetails[3] = taskdate;
                        taskdetails[4] = sourcelocation;
                        taskdetails[5] = sourcebatch;
                        taskdetails[6] = sourceplant;

                        taskdetails[7] = destlocation;
                        taskdetails[8] = destbatch;
                        taskdetails[9] = destplant;

                        taskdetails[10] = description;


                        showTaskDetailsDialog(taskdetails);

                    }else{
                        Toast.makeText(context, "No Details found ...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_transaction_type.equalsIgnoreCase("") || selected_transaction_type.equalsIgnoreCase("Select Transaction") ){
                    Toast.makeText(context,"Please Select Transaction Type",Toast.LENGTH_SHORT).show();
                }else  if(selected_material.equalsIgnoreCase("Select Material") || selected_material.equalsIgnoreCase("") ){
                    Toast.makeText(context,"Please Select Material",Toast.LENGTH_SHORT).show();
                }else  if(selected_source.equalsIgnoreCase("Select Source") || selected_source.equalsIgnoreCase("") ){
                    Toast.makeText(context,"Please Select Source",Toast.LENGTH_SHORT).show();
                }else  if(selected_destination.equalsIgnoreCase("Select Destination") || selected_destination.equalsIgnoreCase("") ){
                    Toast.makeText(context,"Please Select Destination",Toast.LENGTH_SHORT).show();
                }else  if(textTruckNumber.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(context,"Please Read Truck Number",Toast.LENGTH_SHORT).show();
                }else  if(textTaskNumber.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(context,"Task Number cannot be Empty",Toast.LENGTH_SHORT).show();
                }else{

                    if(cd.isConnectingToInternet()){
                        try {
                            JSONObject jsonObject = new JSONObject();
                            allow_to_press_trigger = false;
                            jsonObject.put("TruckID",textTruckNumber.getText().toString());
                            jsonObject.put("TouchPointType",AppConstants.LOAD_UPLOAD_TOUCH_POINT);//production M
                           // jsonObject.put("IsMobileLoading",true);//taskid
                            jsonObject.put("TaskTypeID",db.getIDFromTaskNumber(textTaskNumber.getText().toString()));
                            jsonObject.put("STONumber",textTaskNumber.getText().toString());//taskid
                            //jsonObject.put("TransactionTypeID",selected_transaction_type_id);//taskid




                            //TransactionTypeIDs
                           // jsonObject.put("RouteID",selectedLaddle.getId());//laddleid
                           // jsonObject.put("LotNumber",selectedLaddle.getDescription());//laddledesc
                          //  jsonObject.put("VesselCode",selectedCastDetailModal.getCast_no());//castno
                          //  jsonObject.put("BundurName",selectedCastDetailModal.getCast_location());//location
                           // jsonObject.put("PermitNo",selectedCastDetailModal.getCast_Prefix());//
                           // jsonObject.put("PrintSrNo",selectedCastDetailModal.getCast_id());//castID
                            jsonObject.put("ValidatePrevTrip",true);//castID
                            jsonObject.put("UserID", SharedPreferencesManager.getUserName(context));
                            Log.e("LOAD UPLOAD",jsonObject.toString());
                            UploadLoad("Please Wait ... \nUploading Transaction.", jsonObject);

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

        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = btnLock.getText().toString().trim();

                if(!string.equalsIgnoreCase("Lock")){
                    btnLock.setText("Lock");
                    unlockAll();
                   // LockAll();
                }else{
                     if(transactionTypesList.size()<1){

                        Toast.makeText(context,"No transaction type found",Toast.LENGTH_SHORT).show();
                    }else if(materialList.size()<1){

                        Toast.makeText(context,"No material found",Toast.LENGTH_SHORT).show();
                    }else if(sourceList.size()<1){
                        Toast.makeText(context,"No source found",Toast.LENGTH_SHORT).show();

                    }else if(destinationList.size()<1){

                        Toast.makeText(context,"No destination found",Toast.LENGTH_SHORT).show();
                    }else if(textTaskNumber.getText().toString().length()<8){
                         Toast.makeText(context,"No Task Number found",Toast.LENGTH_SHORT).show();

                     }else if(selected_material.equalsIgnoreCase("Select Source")){
                         Toast.makeText(context,"Material Not Selected",Toast.LENGTH_SHORT).show();

                     }else if(selected_source.equalsIgnoreCase("Select Source")){
                         Toast.makeText(context,"Source Not Selected",Toast.LENGTH_SHORT).show();

                     }else if(selected_destination.equalsIgnoreCase("Select Destination")){
                         Toast.makeText(context,"Destination Not Selected",Toast.LENGTH_SHORT).show();

                     }else{
                        btnLock.setText("Unlock");
                        btnLock.setBackground(getResources().getDrawable(R.drawable.login_button_background));
                        LockAll();
                       // unlockAll();
                    }
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textTruckNumber.setText("");
               // textTaskNumber.setText("");

                allow_to_press_trigger = true;


                if(btnLock.getText().toString().equalsIgnoreCase("LOCK")){
                    unlockAll();
                    spTransactionType.setSelection(0);
                    if(materialList!=null){
                        materialList.clear();
                    }
                    if(sourceList!=null){
                        sourceList.clear();
                    }
                    if(destinationList!=null){
                        destinationList.clear();
                    }
                }
                try{
                   // transactionTypesAdapter.notifyDataSetChanged();
                    materialAdapter.notifyDataSetChanged();
                    sourceAdapter.notifyDataSetChanged();
                    destinationAdapter.notifyDataSetChanged();
                }catch (Exception e){

                }



            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unlockAll();
                textTaskNumber.setText("");
                textTruckNumber.setText("");
                finish();
            }
        });


        transactionTypesList = db.getAllTransactionsTypes(inquery);
        transactionTypesAdapter = new TransactionTypesAdapter(context,transactionTypesList);
        spTransactionType.setAdapter(transactionTypesAdapter);


        spTransactionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

               // Log.e("ID",transactionTypesList.get(i).getTransaction_id());
                if(materialList!=null){
                    materialList.clear();
                }


                if(btnLock.getText().toString().equalsIgnoreCase("LOCK")){
                    unlockAll();
                   // spTransactionType.setSelection(0);
                    if(materialList!=null){
                        materialList.clear();
                    }
                    if(sourceList!=null){
                        sourceList.clear();
                    }
                    if(destinationList!=null){
                        destinationList.clear();
                    }
                }
                try{
                    // transactionTypesAdapter.notifyDataSetChanged();
                    materialAdapter.notifyDataSetChanged();
                    sourceAdapter.notifyDataSetChanged();
                    destinationAdapter.notifyDataSetChanged();
                }catch (Exception e){

                }

                  materialList = db.getAllMaterialsByTransactionTypes(transactionTypesList.get(i).getTransaction_id());

                selected_transaction_type_id = transactionTypesList.get(i).getTransaction_id();

                if(materialList.size()>0){
                    TasksModal tasksModal = new TasksModal();

                    tasksModal.setTasks_id("0");
                    tasksModal.setTask_product("Select Material");
                    tasksModal.setTask_transaction_id("0");
                    tasksModal.setTask_description("Select Material");
                    materialList.add(0,tasksModal);
                }
                  materialAdapter = new MaterialAdapter(context,materialList);
                  spMaterial.setAdapter(materialAdapter);
                selected_transaction_type = transactionTypesList.get(i).getTransaction_id();

                try{
                   // String tasknumber = db.getTaskNumber(materialList.get(spMaterial.getSelectedItemPosition()).getTask_transaction_id(),materialList.get(spMaterial.getSelectedItemPosition()).getTask_product(),sourceList.get(spSource.getSelectedItemPosition()).getTask_source_location(),destinationList.get(i).getTask_destination_location());
                    // String tasknumber = db.getTaskNumberByTaskId(materialList.get(i).getTasks_id());
                   // textTaskNumber.setText(tasknumber);
                }catch (Exception e){
                    textTaskNumber.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spMaterial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                try{
                   // String tasknumber = db.getTaskNumber(materialList.get(spMaterial.getSelectedItemPosition()).getTask_transaction_id(),materialList.get(spMaterial.getSelectedItemPosition()).getTask_product(),sourceList.get(spSource.getSelectedItemPosition()).getTask_source_location(),destinationList.get(i).getTask_destination_location());
                    String tasknumber = db.getTaskNumberByTaskId(materialList.get(i).getTasks_id());
                    textTaskNumber.setText(tasknumber);
                }catch (Exception e){
                    textTaskNumber.setText("");
                }
                if(sourceList!=null){
                    sourceList.clear();
                }

                if(btnLock.getText().toString().equalsIgnoreCase("LOCK")){
                    unlockAll();

                    if(sourceList!=null){
                        sourceList.clear();
                    }
                    if(destinationList!=null){
                        destinationList.clear();
                    }
                }
                try{
                    // transactionTypesAdapter.notifyDataSetChanged();

                    sourceAdapter.notifyDataSetChanged();
                    destinationAdapter.notifyDataSetChanged();
                }catch (Exception e){

                }

                selected_material = materialList.get(i).getTask_product();

                //Log.e("ID",transactionTypesList.get(i).getTransaction_id());
                sourceList = db.getAllSourceByTransactionTypesAndMaterial(selected_transaction_type,selected_material);
                if(sourceList.size()>0){
                    TasksModal tasksModal = new TasksModal();

                    tasksModal.setTasks_id("0");
                    tasksModal.setTask_source_location("Select Source");
                    // tasksModal.setTask_transaction_id("0");
                    sourceList.add(0,tasksModal);

                    sourceAdapter = new SourceAdapter(context,sourceList);
                    spSource.setAdapter(sourceAdapter);
                }





                Log.e("TRANS1",materialList.get(i).getTask_transaction_id());
                Log.e("SOURCE",String.valueOf(sourceList.size()));

                /*if(destinationList!=null){
                    destinationList.clear();
                }
                destinationList = db.getAllDestinationByTransactionTypesAndMaterial(materialList.get(i).getTask_transaction_id(),materialList.get(i).getTask_product());
                destinationAdapter = new DestinationAdapter(context,destinationList);
                spDestination.setAdapter(destinationAdapter);
                Log.e("TRANS2",materialList.get(i).getTask_transaction_id());
                Log.e("DEST",String.valueOf(destinationList.size()));
                try{
                    String tasknumber = db.getTaskNumber(materialList.get(spMaterial.getSelectedItemPosition()).getTask_transaction_id(),materialList.get(spMaterial.getSelectedItemPosition()).getTask_product(),sourceList.get(spSource.getSelectedItemPosition()).getTask_source_location(),destinationList.get(i).getTask_destination_location());
                    // String tasknumber = db.getTaskNumberByTaskId(materialList.get(i).getTasks_id());
                    textTaskNumber.setText("Task No:"+tasknumber);
                }catch (Exception e){
                    textTaskNumber.setText("Task No:");
                }*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(spDestination.getSelectedItemPosition()==0){
                    selected_destination = destinationList.get(i).getTask_destination_location();

                }else{
                    selected_destination = destinationList.get(i).getTask_destination_location().split("/")[1];
                    selected_destination_plant = destinationList.get(i).getTask_destination_location().split("/")[0];
                    selected_destination_batch = destinationList.get(i).getTask_destination_location().split("/")[2];


                }

                try{
                     String tasknumber = db.getTaskNumber(selected_transaction_type,selected_material,selected_source,selected_destination,selected_source_plant,selected_source_batch,selected_destination_plant,selected_destination_batch);
                   // String tasknumber = db.getTaskNumberByTaskId(materialList.get(i).getTasks_id());
                        textTaskNumber.setText(tasknumber);
                        Log.e("excnotintask",tasknumber);
                    }catch (Exception e){
                        textTaskNumber.setText("");
                        Log.e("excintask",e.getMessage());
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(destinationList!=null){
                    destinationList.clear();
                }
                if(btnLock.getText().toString().equalsIgnoreCase("LOCK")){
                    unlockAll();
                    // spTransactionType.setSelection(0);
                }
                try{
                    // transactionTypesAdapter.notifyDataSetChanged();
                   // materialAdapter.notifyDataSetChanged();
                   // sourceAdapter.notifyDataSetChanged();
                    destinationAdapter.notifyDataSetChanged();
                }catch (Exception e){

                }

                if(spSource.getSelectedItemPosition()==0){
                    selected_source = sourceList.get(i).getTask_source_location();

                }else{
                    Log.e("SOURCE",sourceList.get(i).getTask_source_location());
                    selected_source = sourceList.get(i).getTask_source_location().split("/")[1];
                    selected_source_plant = sourceList.get(i).getTask_source_location().split("/")[0];
                    selected_source_batch = sourceList.get(i).getTask_source_location().split("/")[2];
                }

                destinationList = db.getAllDestinationByTransactionTypesAndMaterialAndSource(selected_transaction_type,selected_material,selected_source);

                if(destinationList.size()>0){
                    TasksModal tasksModal = new TasksModal();
                    tasksModal.setTasks_id("0");
                    tasksModal.setTask_destination_location("Select Destination");
                    destinationList.add(0,tasksModal);
                    destinationAdapter = new DestinationAdapter(context,destinationList);
                    spDestination.setAdapter(destinationAdapter);

                    //Log.e("TRANS2",materialList.get(i).getTask_transaction_id());
                    Log.e("DEST",String.valueOf(destinationList.size()));
                }



                try{
                   // String tasknumber = db.getTaskNumber(materialList.get(spMaterial.getSelectedItemPosition()).getTask_transaction_id(),materialList.get(spMaterial.getSelectedItemPosition()).getTask_product(),sourceList.get(spSource.getSelectedItemPosition()).getTask_source_location(),destinationList.get(i).getTask_destination_location());
                    // String tasknumber = db.getTaskNumberByTaskId(materialList.get(i).getTasks_id());
                   // textTaskNumber.setText(tasknumber);
                }catch (Exception e){
                    textTaskNumber.setText("");
                }


              /*  try{
                    String tasknumber = db.getTaskNumber(materialList.get(spMaterial.getSelectedItemPosition()).getTask_transaction_id(),materialList.get(spMaterial.getSelectedItemPosition()).getTask_product(),sourceList.get(spSource.getSelectedItemPosition()).getTask_source_location(),destinationList.get(i).getTask_destination_location());
                    // String tasknumber = db.getTaskNumberByTaskId(materialList.get(i).getTasks_id());
                    textTaskNumber.setText("Task No:"+tasknumber);
                }catch (Exception e){
                    textTaskNumber.setText("Task No:");
                }*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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


    Dialog taskDetailsDialog;
    public void showTaskDetailsDialog(String []details){
        if(taskDetailsDialog!=null){
            taskDetailsDialog.dismiss();
        }

        taskDetailsDialog = new Dialog(context);
        taskDetailsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        taskDetailsDialog.setCancelable(false);
        taskDetailsDialog.setContentView(R.layout.custom_alert_task_details_layout);
        ListView list = (ListView) taskDetailsDialog.findViewById(R.id.taskDetailsList);


        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.task_details_list_adapter_layout, details);

        list.setAdapter(adapter);

        Button dialogButton = (Button) taskDetailsDialog.findViewById(R.id.btnBack);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskDetailsDialog.dismiss();
            }
        });
        taskDetailsDialog.getWindow().getAttributes().windowAnimations = R.style.FadeInOutAnimation;
        if (context instanceof Activity){
            Activity activity = (Activity)context;
            if ( !activity.isFinishing() ) {
                taskDetailsDialog.show();
            }
        }
    }

    /**
     * method to get transactions data from server
     * */
    public  void UploadLoad(String progress_message,JSONObject jsonObject) {

        showProgress(context,progress_message);
       /* OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.TIME_OUT, TimeUnit.SECONDS)
                .build();*/

        String URL = "";

        URL = SharedPreferencesManager.getHostUrl(context) + AppConstants.POST_LOAD_TRIPDETAILS;

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
                                Log.e("RESLOAD",result.toString());
                                String success = result.getString("status");
                                String error = result.getString("message");
                                if(success.equals("true")){
                                    //TODO SUCCESS
                                    showCustomSuccessDialog("Transaction Saved Successfully");

                                }else{
                                    //TODO FAIL
                                    showCustomErrorDialog(error);

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
        dialog.setContentView(R.layout.custom_alert_error_dialog_layout);
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
        final Button dialogButton = (Button) dialog2.findViewById(R.id.btn_dialog);


        dialogButton.setVisibility(View.GONE);
        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // btnClear.performClick();
                        if(btnLock.getText().toString().equalsIgnoreCase("UNLOCK")){
                            btnLock.setText("Unlock");
                            btnLock.setBackground(getResources().getDrawable(R.drawable.login_button_background));
                            LockAll();
                            textTruckNumber.setText("");
                        }else{
                            selected_destination = "";
                            selected_destination_batch = "";
                            selected_destination_plant = "";
                            selected_source = "";
                            selected_source_batch = "";
                            selected_source_plant = "";
                            btnClear.performClick();
                        }
                    }
                });


                dialog2.dismiss();
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, AppConstants.DIALOG_DISMISS_COUNT); // the timer will count 5 seconds....

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                if(btnLock.getText().toString().equalsIgnoreCase("UNLOCK")){
                    btnLock.setText("Unlock");
                    btnLock.setBackground(getResources().getDrawable(R.drawable.login_button_background));
                    LockAll();
                }else{
                    btnClear.performClick();
                }


            }
        });
        dialog2.show();
    }
    /**
     * Trigger Press
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

       // Log.e("TRIGGER","IN");
       // Log.e("TRIGGER Code","IN"+keyCode);
        if (keyCode == 139 ||keyCode == 280 || keyCode == 142 || keyCode == 293) {
            if (event.getRepeatCount() == 0) {
                /**
                 * If inventory already started then stop it else start it.
                 * */
                if (allow_to_press_trigger) {
                    allow_to_press_trigger = true;
                    SimpleRFIDEntity entity = mReader.readData("00000000", RFIDWithUHF.BankEnum.UII, AppConstants.EPC_FROM,AppConstants.EPC_LENGTH);
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
                                            Log.e("DATA2",tidData);
                                            // Log.e("VehicleNUM",PSLUtils.ConvertBinaryStringToAsciiSeven(""));

                                            if(data.length()==240){
                                                String veh = data.substring(64,169);//from 65 length 105

                                                Log.e("Vehicle",veh);

                                                Log.e("VehicleNUM", PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0",""));
                                                String vehicle_number = PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0","");



                                                if(ApplicationCommonMethods.isValidTruckNumber(vehicle_number)){
                                                    textTruckNumber.setText(PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0",""));
                                                    BeepClass.successbeep(context);
                                                    Log.e("VALID","VALID Truck Number");
                                                }else{
                                                    textTruckNumber.setText("");
                                                    BeepClass.errorbeep(context);
                                                    Log.e("INVALID","INVALID Truck Number");
                                                }




                                                if(selected_transaction_type.equalsIgnoreCase("") || selected_transaction_type.equalsIgnoreCase("Select Transaction") ){
                                                    Toast.makeText(context,"Please Select Transaction Type",Toast.LENGTH_SHORT).show();
                                                }else  if(selected_material.equalsIgnoreCase("Select Material") || selected_material.equalsIgnoreCase("") ){
                                                    Toast.makeText(context,"Please Select Material",Toast.LENGTH_SHORT).show();
                                                }else  if(selected_source.equalsIgnoreCase("Select Source") || selected_source.equalsIgnoreCase("") ){
                                                    Toast.makeText(context,"Please Select Source",Toast.LENGTH_SHORT).show();
                                                }else  if(selected_destination.equalsIgnoreCase("Select Destination") || selected_destination.equalsIgnoreCase("") ){
                                                    Toast.makeText(context,"Please Select Destination",Toast.LENGTH_SHORT).show();
                                                }else  if(textTruckNumber.getText().toString().equalsIgnoreCase("")){
                                                    Toast.makeText(context,"Please Read Truck Number",Toast.LENGTH_SHORT).show();
                                                }else  if(textTaskNumber.getText().toString().equalsIgnoreCase("")){
                                                    Toast.makeText(context,"Task Number cannot be Empty",Toast.LENGTH_SHORT).show();
                                                }else  if(textTruckNumber.getText().toString().length()<4){
                                                    Toast.makeText(context,"Please Read Truck Number",Toast.LENGTH_SHORT).show();
                                                }else{

                                                    if(cd.isConnectingToInternet()){
                                                        try {
                                                            JSONObject jsonObject = new JSONObject();
                                                            allow_to_press_trigger = false;


                                                            jsonObject.put("TruckID",textTruckNumber.getText().toString());
                                                            jsonObject.put("TouchPointType",AppConstants.LOAD_UPLOAD_TOUCH_POINT);//production M
                                                            // jsonObject.put("IsMobileLoading",true);//taskid
                                                            jsonObject.put("TaskTypeID",db.getIDFromTaskNumber(textTaskNumber.getText().toString()));
                                                            jsonObject.put("STONumber",textTaskNumber.getText().toString());//taskid
                                                            //jsonObject.put("TransactionTypeID",selected_transaction_type_id);//taskid




                                                            //TransactionTypeIDs
                                                            // jsonObject.put("RouteID",selectedLaddle.getId());//laddleid
                                                            // jsonObject.put("LotNumber",selectedLaddle.getDescription());//laddledesc
                                                            //  jsonObject.put("VesselCode",selectedCastDetailModal.getCast_no());//castno
                                                            //  jsonObject.put("BundurName",selectedCastDetailModal.getCast_location());//location
                                                            // jsonObject.put("PermitNo",selectedCastDetailModal.getCast_Prefix());//
                                                            // jsonObject.put("PrintSrNo",selectedCastDetailModal.getCast_id());//castID
                                                            jsonObject.put("ValidatePrevTrip",true);//castID
                                                            jsonObject.put("UserID", SharedPreferencesManager.getUserName(context));
                                                            Log.e("LOAD UPLOAD",jsonObject.toString());



                                                          /*  jsonObject.put("TruckID",textTruckNumber.getText().toString());
                                                            jsonObject.put("TouchPointType",AppConstants.LOAD_UPLOAD_TOUCH_POINT);//production M
                                                           // jsonObject.put("IsMobileLoading",true);//taskid
                                                            jsonObject.put("TaskTypeID",db.getIDFromTaskNumber(textTaskNumber.getText().toString()));
                                                            jsonObject.put("STONumber",textTaskNumber.getText().toString());//taskid
                                                            jsonObject.put("TransactionTypeID",selected_transaction_type_id);//taskid

                                                            allow_to_press_trigger = false;
                                                            //TransactionTypeIDs
                                                            // jsonObject.put("RouteID",selectedLaddle.getId());//laddleid
                                                            // jsonObject.put("LotNumber",selectedLaddle.getDescription());//laddledesc
                                                            // jsonObject.put("VesselCode",selectedCastDetailModal.getCast_no());//castno
                                                            // jsonObject.put("BundurName",selectedCastDetailModal.getCast_location());//location
                                                            // jsonObject.put("PermitNo",selectedCastDetailModal.getCast_Prefix());//prefix
                                                            // jsonObject.put("PrintSrNo",selectedCastDetailModal.getCast_id());//castID
                                                            jsonObject.put("UserID", SharedPreferencesManager.getUserName(context));
                                                            Log.e("LOAD UPLOAD",jsonObject.toString());*/
                                                            UploadLoad("Please Wait ... \nUploading Transaction for\n Vehicle :- "+vehicle_number, jsonObject);

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            allow_to_press_trigger = true;
                                                        }

                                                    }else{
                                                        allow_to_press_trigger = true;
                                                        Toast.makeText(context,context.getResources().getString(R.string.internet_error),Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                // textrfid.setText(PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0",""));


                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            BeepClass.errorbeep(context);
                                            allow_to_press_trigger = true;
                                            Log.e("EXC",e.getMessage());
                                        }
                                    }
                                });

                            }else{
                                allow_to_press_trigger = true;
                            }
                        }else{
                            allow_to_press_trigger = true;
                            BeepClass.errorbeep(context);
                        }


                    }else{
                        allow_to_press_trigger = true;
                        BeepClass.errorbeep(context);
                    }

                } else {
                   // allow_to_press_trigger = true;

                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void print(int keyCode) {
    }

    public void LockAll(){
        btnLock.setBackground(getResources().getDrawable(R.drawable.login_button_background));
        spDestination.setEnabled(false);
        spDestination.setClickable(false);
       // spDestination.setOnItemSelectedListener(null);

        spSource.setEnabled(false);
        spSource.setClickable(false);
       // spSource.setOnItemSelectedListener(null);

        spMaterial.setEnabled(false);
        spMaterial.setClickable(false);
        //spMaterial.setOnItemSelectedListener(null);

        spTransactionType.setEnabled(false);
        spTransactionType.setClickable(false);
        //spTransactionType.setOnItemSelectedListener(null);
    }
    public void unlockAll(){
        btnLock.setBackground(getResources().getDrawable(R.drawable.clear_button_background));
       // spDestination.setOnItemSelectedListener(null);
        spDestination.setEnabled(true);
        spDestination.setClickable(true);

        spSource.setEnabled(true);
        spSource.setClickable(true);
        //spSource.setOnItemSelectedListener(null);

        spMaterial.setEnabled(true);
        spMaterial.setClickable(true);
       // spMaterial.setOnItemSelectedListener(null);

        spTransactionType.setEnabled(true);
        spTransactionType.setClickable(true);
       // spTransactionType.setOnItemSelectedListener(null);
    }


    private void findViews() {
        textStatus = (TextView)findViewById( R.id.textStatus );
        spTransactionType = (Spinner)findViewById( R.id.spTransactionType );
        spMaterial = (Spinner)findViewById( R.id.spMaterial );
        spSource = (Spinner)findViewById( R.id.spSource );
        spDestination = (Spinner)findViewById( R.id.spDestination );
        textTaskNumber = (TextView)findViewById( R.id.textTaskNumber );
        textTruckNumber = (TextView)findViewById( R.id.textTruckNumber );
        btnDetails = (Button)findViewById( R.id.btnDetails );
        btnSave = (Button)findViewById( R.id.btnSave );
        btnSave.setVisibility(View.GONE);
        btnClear = (Button)findViewById( R.id.btnClear );
        btnBack = (Button)findViewById( R.id.btnBack );
        btnLock = (Button)findViewById( R.id.btnLock );

        btnDetails.setOnClickListener( this );
        btnSave.setOnClickListener( this );
        btnClear.setOnClickListener( this );
        btnBack.setOnClickListener( this );
        btnLock.setOnClickListener( this );
    }
    @Override
    public void onClick(View v) {
        if ( v == btnDetails ) {
            // Handle clicks for btnDetails
        } else if ( v == btnSave ) {
            // Handle clicks for btnSave
        } else if ( v == btnClear ) {
            // Handle clicks for btnClear
        } else if ( v == btnBack ) {
            // Handle clicks for btnBack
        } else if ( v == btnLock ) {
            // Handle clicks for btnLock
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
        ///rfidHandler.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
       // String status = rfidHandler.onResume();
       // statusTextViewRFID.setText(status);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // rfidHandler.onDestroy();
        unlockAll();
    }
}