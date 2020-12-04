package com.psllab.assettracking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AssetSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_search);
        getSupportActionBar().hide();
    }
}