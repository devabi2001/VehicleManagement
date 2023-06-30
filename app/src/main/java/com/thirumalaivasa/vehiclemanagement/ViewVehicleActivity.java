package com.thirumalaivasa.vehiclemanagement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thirumalaivasa.vehiclemanagement.Helpers.ImageHelper;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Models.ImageData;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewVehicleActivity extends AppCompatActivity {

    private TextView vNoTv, oNameTv, sonOfTv, chassisTv, engineTv, manuTv, modelTv, yearTv, fTypeTv, fCapTv, taxTv, insuranceTv, fitnessTv, puccTv, permitTv, colorTv, vehicleClassTv;

    private ImageView vehicleImage;

    private ProgressBar progressBar, vehicleImgProgress;

    private VehicleData vehicleData;

    private Uri imageUri;

    private StorageReference storageReference;
    private final String TAG = "VehicleManagement";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vehicle);
        findViews();
        storageReference = FirebaseStorage.getInstance().getReference();
        vehicleData = getIntent().getParcelableExtra("VehicleData");

    }


    @Override
    protected void onResume() {
        super.onResume();
        setText();
        downloadVehiclePic();
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
                Intent intent = new Intent(ViewVehicleActivity.this,AddVehicleActivity.class);
                intent.putExtra("VehicleData",vehicleData);
                intent.putExtra("mode",2);
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
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressBar.setVisibility(View.VISIBLE);
                        assert uid != null;
                        database.collection("Data").document(uid).collection("Vehicles")
                                .document(vehicleData.getRegistrationNumber()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            deleteExpense();

                                        } else {
                                            Toast.makeText(ViewVehicleActivity.this, "Can't delete try again later", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressBar.setVisibility(View.INVISIBLE);
                        dialogInterface.cancel();
                    }
                }).setNeutralButton("Backup Data", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressBar.setVisibility(View.INVISIBLE);
                        dialogInterface.cancel();
                        Toast.makeText(ViewVehicleActivity.this, "This feature need to be added", Toast.LENGTH_SHORT).show();
                    }
                });
        alertBuilder.show();

    }

    private void deleteExpense() {
        FirebaseFirestore database1 = FirebaseFirestore.getInstance();
        FirebaseFirestore database2 = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getUid();
        List<String> eidList = new ArrayList<>();
        assert uid != null;
        database1.collection("Data").document(uid).collection("Expense").whereEqualTo("vno", vehicleData.getRegistrationNumber()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<ExpenseData> expenseData = task.getResult().toObjects(ExpenseData.class);
                            for (ExpenseData data : expenseData) {
                                eidList.add(data.geteId());
                            }
                            for (String eid : eidList) {
                                database2.collection("Data").document(uid).collection("Expense").document(eid).delete();
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(ViewVehicleActivity.this, "Vehicle Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
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
            Task<Boolean> booleanTask = new ImageHelper().uploadPicture(ViewVehicleActivity.this, imageUri, picRef);
            booleanTask.addOnSuccessListener(new OnSuccessListener<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    new ImageHelper().savePicture(ViewVehicleActivity.this, imageUri, vehicleData.getRegistrationNumber());
                    setImage();
                }
            });
        }
    }
    private void setImage() {
        vehicleImgProgress.setVisibility(View.GONE);
        if (ImageData.getImage(vehicleData.getRegistrationNumber()) != null) {
            Glide.with(ViewVehicleActivity.this).load(ImageData.getImage(vehicleData.getRegistrationNumber()))
                    .circleCrop().into(vehicleImage);

        } else {
            Glide.with(ViewVehicleActivity.this).load(R.drawable.car_24)
                    .circleCrop().into(vehicleImage);

        }
    }

    private void downloadVehiclePic() {
        //The name off the picture will be "profile.jpg" inside the path of uid
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getUid() + "/vehicle/" + vehicleData.getRegistrationNumber() + ".jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                SharedPreferences imagePreferences = getSharedPreferences("Images", MODE_PRIVATE);
                String imagePath = imagePreferences.getString(vehicleData.getRegistrationNumber(), null);
                if (imagePath == null) {
                    new ImageHelper().downloadPicture(ViewVehicleActivity.this, vehicleData.getRegistrationNumber(), storageRef)
                            .addOnSuccessListener(new OnSuccessListener<Bitmap>() {
                                @Override
                                public void onSuccess(Bitmap bitmap) {
                                    if (bitmap != null) {
                                        ImageData.setImage(vehicleData.getRegistrationNumber(), bitmap);
                                        setImage();
                                    } else {
                                        Glide.with(ViewVehicleActivity.this).load(R.drawable.car_24)
                                                .circleCrop().into(vehicleImage);
                                    }
                                }
                            });
                } else {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        // Step 5: Decode the image file into a Bitmap object
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        if (bitmap != null) {
                            ImageData.setImage(vehicleData.getRegistrationNumber(), bitmap);
                            setImage();
                        }
                    } else {
                        new ImageHelper().downloadPicture(ViewVehicleActivity.this, vehicleData.getRegistrationNumber(), storageRef)
                                .addOnSuccessListener(new OnSuccessListener<Bitmap>() {
                                    @Override
                                    public void onSuccess(Bitmap bitmap) {
                                        if (bitmap != null) {
                                            ImageData.setImage(vehicleData.getRegistrationNumber(), bitmap);
                                            setImage();
                                        } else {
                                            Glide.with(ViewVehicleActivity.this).load(R.drawable.car_24)
                                                    .circleCrop().into(vehicleImage);
                                        }

                                    }
                                });
                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                setImage();
            }
        });
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