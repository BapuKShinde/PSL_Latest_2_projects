package com.zebra.rfid.demo.pslsdksample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zebra.rfid.api3.TagData;
import com.zebra.rfid.demo.pslsdksample.modals.PSLUtils;

public class MainActivity extends AppCompatActivity implements RFIDHandler.ResponseHandlerInterface {

    public TextView statusTextViewRFID = null;
    private TextView textrfid;
    private TextView testStatus;

    RFIDHandler rfidHandler;
    final static String TAG = "RFID_SAMPLE";
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // UI
        statusTextViewRFID = findViewById(R.id.textStatus);
        textrfid = findViewById(R.id.textViewdata);
        testStatus = findViewById(R.id.testStatus);

        // RFID Handler
        rfidHandler = new RFIDHandler();
        rfidHandler.onCreate(this);

        // set up button click listener
        Button test = findViewById(R.id.button);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = rfidHandler.Test1();
                testStatus.setText(result);
            }
        });

        Button test2 = findViewById(R.id.button2);
        test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = rfidHandler.Test2();
                testStatus.setText(result);
            }
        });

        Button defaultButton = findViewById(R.id.button3);
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = rfidHandler.Defaults();
                testStatus.setText(result);
            }
        });


        Button readButton = findViewById(R.id.button4);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = "0";
                 result = rfidHandler.ReadAccess();
                //testStatus.setText(result);
                if(result!=null)
                textrfid.setText(result);

                String abc = "0";
                if(result!=null)
                abc = String.valueOf(result.length());

                testStatus.setText(abc);
                //  text.setText(response_tagData.getMemoryBankData());



                try {
                    String data = ConvertHexStringToBinaryString(result);
                    Log.e("DATA",data);
                    Log.e("DATA2",result);
                   // Log.e("VehicleNUM",PSLUtils.ConvertBinaryStringToAsciiSeven(""));

                    if(data.length()==240){
                        String veh = data.substring(64,169);//from 65 length 105

                        Log.e("Vehicle",veh);

                        Log.e("VehicleNUM",PSLUtils.ConvertBinaryStringToAsciiSeven(veh));
                        textrfid.setText(PSLUtils.ConvertBinaryStringToAsciiSeven(veh).trim().replace("\0",""));


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
        statusTextViewRFID.setText(status);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rfidHandler.onDestroy();
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
                textrfid.append(sb.toString()+"\nLENGTH"+String.valueOf(sb.length()));

            }
        });
    }

    @Override
    public void handleTriggerPress(boolean pressed) {
        if (pressed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textrfid.setText("");

                    textrfid.setText("Trigger");
                }
            });
            rfidHandler.performInventory();
        } else
            rfidHandler.stopInventory();
    }
}
