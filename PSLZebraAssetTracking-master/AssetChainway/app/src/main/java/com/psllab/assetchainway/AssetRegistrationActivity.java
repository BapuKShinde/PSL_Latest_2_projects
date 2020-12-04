package com.psllab.assetchainway;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AssetRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_registration);
        getSupportActionBar().hide();
    }
}