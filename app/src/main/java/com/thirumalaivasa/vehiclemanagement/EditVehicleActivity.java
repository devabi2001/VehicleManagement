package com.thirumalaivasa.vehiclemanagement;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;


public class EditVehicleActivity extends AppCompatActivity {

    private Button updateBtn;
    private ImageButton backBtn;

    //Search Vehicle

    private ImageView addVehicleImgBtn;


    private EditText ownerNameEt, fatherNameEt, regNumEt, vClassEt, vManuEt, vModelEt, regDateEt, colorEt, engineNumEt, chassisNumEt, fuelTypeEt, fuelCapEt, fitnessEt, insuranceEt, taxEt, permitEt, puccEt;

    private VehicleData vehicleData;

    private Uri imageUri;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_vehicle);

    }
}