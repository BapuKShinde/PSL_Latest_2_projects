package com.zebra.rfid.demo.pslsdksample;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.zebra.rfid.demo.pslsdksample.adapters.SourcePlantAdapter;
import com.zebra.rfid.demo.pslsdksample.databases.DatabaseHandler;
import com.zebra.rfid.demo.pslsdksample.modals.SourcePlant;
import com.zebra.rfid.demo.pslsdksample.modals.TasksModal;

import java.util.List;

public class PreLoadActivity extends AppCompatActivity {

    private String selected_sourceplant = "Select Source Plant";


    List<SourcePlant> sourcePlantList;
    private SourcePlantAdapter sourcePlantAdapter;
    private Context context = this;
    private Button btnNext,btnBack;
    private DatabaseHandler db;
    Spinner spSourcePlant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_load);
        db = new DatabaseHandler(context);

        spSourcePlant = (Spinner) findViewById(R.id.spSourcePlant);
        //spSourcePlant.setOnItemSelectedListener(this);

        sourcePlantList = db.getAllDistinctMainSourcePlant();

        Log.e("SIZE",String.valueOf(sourcePlantList.size()));

        SourcePlant initialSourceplant = new SourcePlant();
        initialSourceplant.setSp_main_plant("Select Source Plant");
        sourcePlantList.add(0,initialSourceplant);

        sourcePlantAdapter = new SourcePlantAdapter(context,sourcePlantList);
        spSourcePlant.setAdapter(sourcePlantAdapter);
        //Creating the ArrayAdapter instance having the country list
        // ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,source_plants);
        // aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        // spin.setAdapter(aa);



        btnNext = (Button) findViewById(R.id.btnNext);
        btnBack = (Button) findViewById(R.id.btnBack);


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(selected_sourceplant.contains("Select Source Plant")){
                    Toast.makeText(context,"Please Select Source Plant",Toast.LENGTH_SHORT).show();
                }else{

                    List<SourcePlant> sublist = db.getAllSubPlantForMainPlant(selected_sourceplant);

                    String inquery = "(";
                    for(int i=0;i<sublist.size();i++){
                        if(i==sublist.size()-1){
                            inquery = inquery+"'"+sublist.get(i).getSp_sub_plant()+"')";
                        }else{
                            inquery = inquery+"'"+sublist.get(i).getSp_sub_plant()+"',";
                        }

                    }

                    Log.e("SUBINQUERY",inquery);
                    List<TasksModal> transinquerylist = db.getAllTransactionTypesBymainplant(inquery);


                    String inquerytrans = "(";
                    for(int i=0;i<transinquerylist.size();i++){
                        if(i==transinquerylist.size()-1){
                            inquerytrans = inquerytrans+"'"+transinquerylist.get(i).getTask_transaction_id()+"')";
                        }else{
                            inquerytrans = inquerytrans+"'"+transinquerylist.get(i).getTask_transaction_id()+"',";
                        }

                    }

                    Log.e("TRANSINQUERY",inquerytrans);

                    Intent loadIntent = new Intent(PreLoadActivity.this,LoadActivity.class);
                    loadIntent.putExtra("source_plant",selected_sourceplant);
                    loadIntent.putExtra("inquery",inquerytrans);
                    startActivity(loadIntent);
                }

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        spSourcePlant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_sourceplant = sourcePlantList.get(i).getSp_main_plant();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


}