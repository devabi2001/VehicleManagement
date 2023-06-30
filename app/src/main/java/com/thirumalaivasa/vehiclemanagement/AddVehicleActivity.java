package com.thirumalaivasa.vehiclemanagement;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;


public class AddVehicleActivity extends AppCompatActivity {

    public VehicleData vehicleData;

    //AddVehicleAutoFrag addVehicleAutoFrag;
    AddVehicleManualFragment addVehicleManualFragment;
    int mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        vehicleData = getIntent().getParcelableExtra("VehicleData");
        mode = getIntent().getIntExtra("mode",-1);
        if(mode == -1){
            finish();
        }
        //addVehicleAutoFrag = new AddVehicleAutoFrag();
        addVehicleManualFragment = new AddVehicleManualFragment();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //getSupportFragmentManager().beginTransaction().replace(R.id.container, addVehicleAutoFrag).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, addVehicleManualFragment).commit();
    }
}