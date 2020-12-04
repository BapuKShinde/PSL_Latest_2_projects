package com.psllab.assetchainway;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.psllab.assetchainway.helpers.ApplicationCommonMethods;
import com.psllab.assetchainway.helpers.BeepClass;
import com.psllab.assetchainway.helpers.StringUtils;
import com.psllab.assetchainway.rfidbase.BaseSearchUHFActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends BaseSearchUHFActivity {
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
    Timer addTimer,clearTimer;
    //boolean is_search_on = false;


    //UHF
    private boolean loopFlag = false;
    private int inventoryFlag = 2;//1 anti, 2- anti collision, 0-single


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_search);

        initSound();//sound initialization
        initUHF(); //��ʼ�� uhf initialization


        tagList = new ArrayList<HashMap<String, String>>();

        perList = new ArrayList<>();

        search_type = getIntent().getStringExtra("searchname");
       // search_type = "ABCD";
        search_value = getIntent().getStringExtra("epc");
       // search_value = "30361f74945a9e4000a3c907";

        findViews();//view initialization

        textSearch.setText("Search : "+search_type);


        adapter = new SimpleAdapter(context, tagList, R.layout.listtag_items,
                // new String[]{"tagUii", "tagLen", "tagCount", "tagRssi"},
                new String[]{"epc", "barcode", "rssi","per"},
                new int[]{R.id.textEpc, R.id.textBarcode, R.id.textRSSI, R.id.textPer});
        list.setAdapter(adapter);


        new SetFilterTask().execute();
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
                    if(!loopFlag) {
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


        clearTimer = new Timer();
        //Set the schedule function and rate
        clearTimer.scheduleAtFixedRate(new TimerTask() {

                                           @Override
                                           public void run() {
                                               if(loopFlag){
                                                   UpdateUI(maximum_value);

                                                   // btnInventory.setText("Stop");

                                               }else{
                                                   clearData();
                                               }
                                           }

                                       },
                //Set how long before to start calling the TimerTask (in milliseconds)
                0,
                //Set the amount of time between each execution (in milliseconds)
                1000);


    }

    /**
     * Determine if the EPC is in the list
     *
     * @param strEPC index
     * @return
     */
    public int checkIsExist(String strEPC) {
        int existFlag = -1;
        try {
            if (StringUtils.isEmpty(strEPC)) {
                return existFlag;
            }

            String tempStr = "";
            for (int i = 0; i < tagList.size(); i++) {
                HashMap<String, String> temp = new HashMap<String, String>();
                temp = tagList.get(i);

                tempStr = temp.get("epc");

                if (strEPC.equals(tempStr)) {
                    existFlag = i;
                    break;
                }
            }
        }catch (Exception e){

        }

        return existFlag;
    }



    private void addEPCToList(String epc, String rssi) {

        if(!epc.isEmpty()){
            try {
                if (epc.length() > 24) {
                    epc = epc.substring(0, 24);
                }
                //search_value = epc;
                if (epc.equalsIgnoreCase(search_value)) {
                    int index = checkIsExist(epc);
                    map = new HashMap<String, String>();
                    map.put("epc", epc);
                    map.put("rssi", "0");
                    map.put("barcode", search_value);

                    String rs = rssi;
                    double rss = Double.parseDouble(rs) * (-1);
                    int value = (int) Math.round(rss);


                    int a = 0;
                    if (value >= 0 && value < 39) {
                        a = 100;
                    }else if (value > 86) {
                        a = 1;
                    }else{
                        a = ApplicationCommonMethods.getPercentage(value);
                    }



                    if (a > 100) {
                        a = 100;
                    }
                    map.put("per", String.valueOf(a));

                    if (a > maximum_value) {
                        maximum_value = (int) Math.round(a);
                        UpdateMax(maximum_value);
                    }
                    if (index == -1) {
                        tagList.add(map);
                    } else {
                        tagList.set(index, map);
                    }
                    adapter.notifyDataSetChanged();
                }
            }catch (Exception e){
            }
        }



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
        loopFlag = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnInventory.setText("Stop");
                // btnSearch.setText("Stop");
                //btnSearch.setBackground(getResources().getDrawable(R.drawable.clear_button_background));
            }
        });
       // rfidHandler.performInventory(enc);

        readTag();
    }

    /**
     * UI Update
     * */
    int maximum_value = 0;
    /**
     * UI Update
     * */
    public void UpdateUI(final int a){
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final int progress = a;
                    textMax.setText(String.valueOf(maximum_value) + " %");
                    progressBar.setProgress(progress);
                    textCount.setText(String.valueOf(tagList.size()));
                    adapter.notifyDataSetChanged();
                    //GEIGER SEARCH CHANGE
                    if (tagList != null) {
                        tagList.clear();
                    }
                    maximum_value = 0;

                    if (a > 0) {

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Beep(progress);
                                } catch (Exception e) {

                                }
                            }

                        });
                    }
                }
            });
        }catch (Exception e){

        }
    }

    /**
     * UI Update
     * */
    public void UpdateMax(final int a){
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

    public void stopInventory(){
        btnInventory.setText("Start");
        // btnSearch.setText("Start");
        //  btnSearch.setBackground(getResources().getDrawable(R.drawable.login_button_background));
       // loopFlag = false;
       // rfidHandler.stopInventory();




        try{
            if (loopFlag) {
                loopFlag = false;
                if (mReader.stopInventory()) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        btnInventory.setBackground(getResources().getDrawable(R.drawable.button_green_background));
                    }
                    textMax.setText(String.valueOf((int) Math.round(0)) +" %");
                    progressBar.setProgress((int) Math.round(0));
                    btnInventory.setText("Start");
                } else {
                    initUHF();
                }
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onBackPressed() {

        stopInventory();
        showCustomAlertBackPressDialog("Do You want to exit ?");


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
                //finish();
                new FreeReaderTask().execute();
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





    /**
     * start inventory
     * */
    private void readTag() {
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnInventory.setBackground(getResources().getDrawable(R.drawable.button_green_background));
            }
            if(mReader!=null){
                if(loopFlag){

                }else{
                    mReader.setEPCTIDMode(false);
                }

            }

            switch (inventoryFlag) {
                case 1:// 单标签循环 -- Single label cycle
                {
                    if (mReader.startInventoryTag((byte) 0, (byte) 0)) {
                        btnInventory.setText("Stop");
                        loopFlag = true;
                        new TagThread(StringUtils.toInt("10", 0)).start();
                    } else {
                        mReader.stopInventory();
                        initUHF();
                    }
                }
                break;

                case 2:// 防碰撞 anti collision
                {
                    //int initQ = Byte.valueOf((String) SpinnerQ.getSelectedItem(), 10);
                    int initQ = Byte.valueOf(String.valueOf(3), 10);
                    if (mReader.startInventoryTag((byte) 1, initQ)) {
                        btnInventory.setText("Stop");
                        loopFlag = true;
                        new TagThread(StringUtils.toInt("10", 0)).start();
                    } else {
                        mReader.stopInventory();
                    }
                }
                break;
                default:
                    break;
            }
        }catch (Exception e){

        }
    }

    /**
     *Worker thread to read data from reader
     * */
    class TagThread extends Thread {
        HashMap<String, String> map;
        private int mBetween = 80;

        public TagThread(int iBetween) {
            mBetween = iBetween;
        }

        public void run() {

            String[] res = null;
            while (loopFlag) {

                res = mReader.readTagFromBuffer();//.readTagFormBuffer();

                if (res != null) {
                    try {
                        final String epc = mReader.convertUiiToEPC(res[1]);
                        final String rssi = res[2];

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(epc!=null)
                                    addEPCToList(epc ,rssi);
                            }
                        });

                        // mReader.init();
                        // TagThread.sleep(10);
                        sleep(0);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    /**
     * Trigger press
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 139 || keyCode == 280 || keyCode == 142 || keyCode == 293) {
            if (event.getRepeatCount() == 0) {
                /**
                 * If inventory already started then stop it else start it.
                 * */
                try {

                    if (!loopFlag) {

                        btnInventory.setText("Stop");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            btnInventory.setBackground(getResources().getDrawable(R.drawable.button_green_background));
                        }
                        performInventory();
                    } else {
                        stopInventory();
                    }
                }catch (Exception e){

                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Reset Filter
     *
     * @author Bapusaheb Shinde
     */
    ProgressDialog mypDialog;
    public class FreeReaderTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return mReader.setFilter(1,0,"");

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if(mypDialog!=null){
                mypDialog.dismiss();
            }

            finish();
            if (!result) {
                Toast.makeText(SearchActivity.this, "init fail",
                        Toast.LENGTH_SHORT).show();
                finish();
            }else{
                finish();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if(mypDialog!=null){
                mypDialog.dismiss();
            }
            mypDialog = new ProgressDialog(SearchActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("Making Reader Free\nPlease wait");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }
    }

    /**
     * set Filter
     *
     * @author Bapusaheb Shinde
     */
    public class SetFilterTask extends AsyncTask<String, Integer, Boolean> {


        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            try{
                return mReader.setFilter(1, 0, search_value);//1 - UII, 2-TID

            }catch(Exception e){
                return mReader.setFilter(1, 0, "");//1 - UII, 2-TID

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(mypDialog!=null){
                mypDialog.dismiss();
            }
            if (!result) {
                Toast.makeText(SearchActivity.this, "UHF Initialization fail",
                        Toast.LENGTH_SHORT).show();
                finish();
            }else{

            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if(mypDialog!=null){
                mypDialog.dismiss();
                mypDialog.dismiss();
            }
            mypDialog = new ProgressDialog(SearchActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("Setting filter\nPlease wait");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }
    }
    @Override
    protected void onDestroy() {
        if (mReader != null) {
            mReader.free();
        }

        if (clearTimer != null) {
            clearTimer.cancel();
        }
        super.onDestroy();

    }

}