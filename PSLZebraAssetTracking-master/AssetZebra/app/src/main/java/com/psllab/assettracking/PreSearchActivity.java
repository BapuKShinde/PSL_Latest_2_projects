package com.psllab.assettracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;

import com.psllab.assettracking.adapters.AsstPreSearchAdapter;
import com.psllab.assettracking.databases.DatabaseHandler;
import com.psllab.assettracking.modals.AssetMaster;

import java.util.ArrayList;
import java.util.List;


public class PreSearchActivity extends AppCompatActivity {

    private List<AssetMaster> cartonList = new ArrayList<>();
    private SearchView searchView;
    private RecyclerView recyclerView;
    private AsstPreSearchAdapter uploadPartialCartonAdapter;

    private DatabaseHandler db;
    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_search);

        db = new DatabaseHandler(context);

        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recycler_view);


        if (cartonList != null) {
            cartonList.clear();
        }
        cartonList = db.getAllMasterList();

        uploadPartialCartonAdapter = new AsstPreSearchAdapter(context, cartonList);
        int numberOfColumns = 1;
        recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));
        recyclerView.setAdapter(uploadPartialCartonAdapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryString) {
                uploadPartialCartonAdapter.getFilter().filter(queryString);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryString) {
                uploadPartialCartonAdapter.getFilter().filter(queryString);
                return false;
            }
        });



    }
}



