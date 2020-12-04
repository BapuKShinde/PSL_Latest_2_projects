package com.psllab.assettracking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.psllab.assettracking.helpers.BeepClass;
import com.psllab.assettracking.rfidhandlers.SearchRFIDHandler;
import com.zebra.rfid.api3.TagData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchRFIDHandler.ResponseHandlerInterface{

    SearchRFIDHandler rfidHandler;
    private Context context = this;
    static String search_type;
    static String search_value;
    ProgressBar progressBar;

    private ArrayList<HashMap<String, String>> tagList;
    List<Integer> perList;
    private HashMap<String, String> map;


    Button btnClear;
    TextView textSearch,textMax,textCount;
    Button btnInventory;
    ListView list;
    SimpleAdapter adapter;
    boolean is_search_on = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_search);



        rfidHandler = new SearchRFIDHandler();
        rfidHandler.onCreate(this);

        tagList = new ArrayList<HashMap<String, String>>();

        perList = new ArrayList<>();

        search_type = getIntent().getStringExtra("searchname");
        search_value = getIntent().getStringExtra("epc");

        findViews();//view initialization

        textSearch.setText("Search : "+search_type);


        adapter = new SimpleAdapter(context, tagList, R.layout.listtag_items,
                // new String[]{"tagUii", "tagLen", "tagCount", "tagRssi"},
                new String[]{"epc", "barcode", "rssi","per"},
                new int[]{R.id.textEpc, R.id.textBarcode, R.id.textRSSI, R.id.textPer});
        list.setAdapter(adapter);



        clearData();
        textCount.setText("0");

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();

                //progressBar.setProgress(0);
            }
        });
        btnInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * If inventory already started then stop it else start it.
                 * */

                try {
                    if(!is_search_on) {
                        //Start inventory
                        btnInventory.setText("Stop");
                        performInventory();
                        // readTag();
                    }else{
                        //stop inventory
                        btnInventory.setText("Start");
                        stopInventory();
                    }
                } catch (Exception e) {
                    // inventory_started = false;
                }
            }
        });





    }


    @Override
    public void handleTagdata(TagData[] tagData) {


    }

    public void performInventory(){
        String enc = "";
       /* if(SGTIN96.IsValidGtin(search_value)){
            long epoch = System.currentTimeMillis();
            epoch = (epoch / 1000);
            enc = SGTIN96.convertToSGTIN96(ApplicationCommonMethods.get14DigitBarcode(search_value),7,epoch,1);

            // String enc = SGTIN96.convertToSGTIN96(search_value,7,epoch,1);
            enc = enc.substring(0,12);


        }*/

        enc = search_value;
        is_search_on = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnInventory.setText("Stop");
                // btnSearch.setText("Stop");
                //btnSearch.setBackground(getResources().getDrawable(R.drawable.clear_button_background));
            }
        });
        rfidHandler.performInventory(enc);
    }
    public void handleLocateTagResponse(final int percentage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showTagLocationingDetails(percentage);
            }
        });
    }

    /**
     * UI Update
     * */
    int maximum_value = 0;
    public void UpdateMax(final int a){
        maximum_value = a;
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final int progress = a;
                    textMax.setText(String.valueOf(maximum_value) + " %");
                    progressBar.setProgress(progress);
                    textCount.setText(String.valueOf(tagList.size()));
                    adapter.notifyDataSetChanged();
                }
            });
        }catch (Exception e){

        }
    }
    private void showTagLocationingDetails(int percentage) {
        UpdateMax(percentage);


    }

    public void stopInventory(){
        btnInventory.setText("Start");
        // btnSearch.setText("Start");
        //  btnSearch.setBackground(getResources().getDrawable(R.drawable.login_button_background));
        is_search_on = false;
        rfidHandler.stopInventory();
    }

    @Override
    public void onBackPressed() {

        stopInventory();
        showCustomAlertBackPressDialog("Do You want to exit ?");


    }

    @Override
    public void handleTriggerPress(boolean pressed) {
        if(pressed)
            if(is_search_on){
                //  btnSearch.setText("Start");

                btnInventory.setText("Start");
                is_search_on = false;
                rfidHandler.stopInventory();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // btnSearch.setText("Start");
                        //btnSearch.setBackground(getResources().getDrawable(R.drawable.login_button_background));
                    }
                });

            }else{
                btnInventory.setText("Stop");
                performInventory();

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
     * clear data
     * */
    private void clearData() {
        //clear data
        // tv_count.setText("0");

        try {
            stopInventory();
            ClearUI();
        }catch (Exception e){

        }

    }

    /**
     * UI Update twhen clear data
     * */
    public void ClearUI(){
        try{


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnInventory.setText("Start");
                    stopInventory();
                    textMax.setText(String.valueOf("0 %"));
                    progressBar.setProgress(0);
                    textCount.setText(String.valueOf(tagList.size()));
                    if(tagList!=null){
                        tagList.clear();
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }catch (Exception e){

        }
    }
    /**
     * View initialization
     * */
    private void findViews() {

        //******************
        btnClear = (Button) findViewById(R.id.btnClear);
        textSearch = (TextView) findViewById(R.id.textSearch);
        textMax = (TextView) findViewById(R.id.textMax);
        textCount = (TextView) findViewById(R.id.textCount);



        list = (ListView) findViewById(R.id.list);

        btnInventory = (Button)findViewById(R.id.btnInventory);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btnInventory.setBackground(getResources().getDrawable(R.drawable.button_green_background));
        }
    }


    /**
     * custom alert back press dialog
     * */
    public void showCustomAlertBackPressDialog(String msg) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_dialog_layout2);
        ImageView image_dialog = (ImageView) dialog.findViewById(R.id.image_dialog);
        image_dialog.setVisibility(View.GONE);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        Button dialogButton2 = (Button) dialog.findViewById(R.id.btn_dialog_cancel);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
                //  new FreeReaderTask().execute();
            }
        });

        dialogButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    /**
     * sound
     * */
    public void Beep(final int a){
        try{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (a > 0 && a<=30) {
                            BeepClass.mutebeep(context);
                        } else if (a >= 31 && a<=60) {
                            BeepClass.beep100(context);
                        } else if (a >= 61 && a<=85) {
                            BeepClass.beep300(context);
                        } else if (a >=86) {
                            BeepClass.successbeep(context);
                        }
                    }catch(Exception e){

                    }
                }
            });
        }catch (Exception e){

        }
    }






}