package com.thirumalaivasa.vehiclemanagement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thirumalaivasa.vehiclemanagement.Dao.VehicleDao;
import com.thirumalaivasa.vehiclemanagement.Helpers.FirebaseHelper;
import com.thirumalaivasa.vehiclemanagement.Helpers.ImageHelper;
import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;
import com.thirumalaivasa.vehiclemanagement.Utils.DBUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.Util;

public class ViewVehicleActivity extends AppCompatActivity {

    private TextView vNoTv, oNameTv, sonOfTv, chassisTv, engineTv, manuTv, modelTv, yearTv, fTypeTv, fCapTv, taxTv, insuranceTv, fitnessTv, puccTv, permitTv, colorTv, vehicleClassTv;

    private ImageView vehicleImage;

    private ProgressBar progressBar, vehicleImgProgress;

    private VehicleData vehicleData;

    private Uri imageUri;

    private StorageReference storageReference;

    private RoomDbHelper dbHelper;
    private VehicleDao vehicleDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vehicle);
        findViews();
        storageReference = FirebaseStorage.getInstance().getReference();
        String vehicleNum = getIntent().getStringExtra(Util.ID);
        if (vehicleNum == null || vehicleNum.trim().isEmpty()) {
            Toast.makeText(this, "Vehicle data is not available", Toast.LENGTH_SHORT).show();
            finish();
        }
        dbHelper = RoomDbHelper.getInstance(ViewVehicleActivity.this);
        vehicleDao = dbHelper.vehicleDao();
        vehicleData = vehicleDao.getVehicleByRegNum(vehicleNum);

    }


    @Override
    protected void onResume() {
        super.onResume();
        setText();
        setImage();
        //   downloadVehiclePic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void setText() {
        vNoTv.setText(vehicleData.getRegistrationNumber());
        oNameTv.setText(vehicleData.getOwnerName());
        sonOfTv.setText(vehicleData.getFatherName());

        chassisTv.setText(vehicleData.getChassisNumber());
        engineTv.setText(vehicleData.getEngineNumber());

        vehicleClassTv.setText(vehicleData.getVehicleClass());

        manuTv.setText(vehicleData.getManufacturer());
        modelTv.setText(vehicleData.getManufacturerModel());
        yearTv.setText(vehicleData.getRegistrationDate());
        fTypeTv.setText(vehicleData.getFuelType());
        fCapTv.setText(String.valueOf(vehicleData.getFuelCapacity()));

        taxTv.setText(vehicleData.getMvTaxValidity());
        insuranceTv.setText(vehicleData.getInsuranceValidity());
        permitTv.setText(vehicleData.getPermitValidity());
        fitnessTv.setText(vehicleData.getFitnessValidity());
        puccTv.setText(vehicleData.getPucValidity());
        colorTv.setText(vehicleData.getColour());

    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn_view_vehicle:
                finish();
                break;
            case R.id.delete_btn_vehicle:
                deleteData();
                break;
            case R.id.edit_btn_vehicle:
                Intent intent = new Intent(ViewVehicleActivity.this, AddVehicleActivity.class);
                intent.putExtra("VehicleNum", vehicleData.getRegistrationNumber());
                intent.putExtra("mode", 2);
                startActivity(intent);
                finish();
                break;
            case R.id.vehicle_img:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1000);
                break;
        }
    }


    private void deleteData() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getUid();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ViewVehicleActivity.this);
        alertBuilder.setTitle("Delete?")
                .setCancelable(true)
                .setMessage("This will delete all the vehicle data including refuel and service data's")
                .setPositiveButton("Delete", (dialogInterface, i) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    vehicleDao.delete(vehicleData);
                    if (Util.checkNetwork(getSystemService(ConnectivityManager.class))) {
                        if (uid == null) {
                            DBUtils.addDeletedData(ViewVehicleActivity.this, Util.VEHICLE, vehicleData.getRegistrationNumber());
                            return;
                        }
                        CollectionReference collection = database.collection("Data").document(uid).collection("Vehicles");
                        new FirebaseHelper().deleteDocumentById(collection, vehicleData.getRegistrationNumber()).addOnCompleteListener(task -> {
                            if (!task.isSuccessful())
                                DBUtils.addDeletedData(ViewVehicleActivity.this, "Vehicle", vehicleData.getRegistrationNumber());
                        });

                    } else
                        DBUtils.addDeletedData(ViewVehicleActivity.this, "Vehicle", vehicleData.getRegistrationNumber());

                }).setNegativeButton("Cancel", (dialogInterface, i) -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    dialogInterface.cancel();
                });
        alertBuilder.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    imageUri = data.getData();
                    uploadImage();
                }
            }
        }
    }


    private void uploadImage() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (imageUri != null) {
            String fileName = uid + "/vehicle/" + vehicleData.getRegistrationNumber() + ".jpg";
            StorageReference picRef = storageReference.child(fileName);
            Task<String> booleanTask = new ImageHelper().uploadPicture(ViewVehicleActivity.this, imageUri, picRef);
            booleanTask.addOnSuccessListener(aBoolean -> {
                new ImageHelper().savePicture(ViewVehicleActivity.this, imageUri, vehicleData.getRegistrationNumber());
                setImage();
            });
        }
    }

    private void setImage() {
        vehicleImgProgress.setVisibility(View.GONE);
        Glide.with(ViewVehicleActivity.this).load(vehicleData.getImagePath())
                .circleCrop()
                .placeholder(R.drawable.car_24)
                .error(R.drawable.car_24)
                .into(vehicleImage);

    }

    private void findViews() {


        vehicleImgProgress = findViewById(R.id.progress_vehicle_img);


        vNoTv = findViewById(R.id.vehicle_no_view);
        oNameTv = findViewById(R.id.owner_name_vehicle_view);
        sonOfTv = findViewById(R.id.of_vehicle_view);

        chassisTv = findViewById(R.id.chassis_num_vehicle_view);
        engineTv = findViewById(R.id.engine_num_vehicle_view);
        vehicleClassTv = findViewById(R.id.class_vehicle_view);

        manuTv = findViewById(R.id.manu_vehicle_view);
        modelTv = findViewById(R.id.model_vehicle_view);
        yearTv = findViewById(R.id.reg_date_vehicle_view);
        fTypeTv = findViewById(R.id.fuel_type_vehicle_view);
        fCapTv = findViewById(R.id.fuel_cap_vehicle_view);

        taxTv = findViewById(R.id.tax_date_vehicle_view);
        insuranceTv = findViewById(R.id.insurance_vehicle_view);
        fitnessTv = findViewById(R.id.fitness_vehicle_view);
        puccTv = findViewById(R.id.pucc_vehicle_view);
        permitTv = findViewById(R.id.permit_vehicle_view);
        colorTv = findViewById(R.id.color_vehicle_view);

        vehicleImage = findViewById(R.id.vehicle_img);

        progressBar = findViewById(R.id.progress_vehicle);

    }
}