package com.thirumalaivasa.vehiclemanagement;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
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

        mode = getIntent().getIntExtra("mode",-1);
        if (mode == -1) {
            finish();
        } else if (mode == 2) {
            String vehicleNum = getIntent().getStringExtra("VehicleNum");
            if (vehicleNum == null || vehicleNum.trim().isEmpty())
                finish();
            RoomDbHelper dbHelper = RoomDbHelper.getInstance(AddVehicleActivity.this);
            vehicleData = dbHelper.vehicleDao().getVehicleByRegNum(vehicleNum);

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