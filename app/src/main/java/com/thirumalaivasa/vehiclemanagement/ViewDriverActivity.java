package com.thirumalaivasa.vehiclemanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thirumalaivasa.vehiclemanagement.Dao.DriverDao;
import com.thirumalaivasa.vehiclemanagement.Helpers.FirebaseHelper;
import com.thirumalaivasa.vehiclemanagement.Helpers.ImageHelper;
import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Models.ImageData;
import com.thirumalaivasa.vehiclemanagement.Utils.DBUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewDriverActivity extends AppCompatActivity {

    ImageView driverImg;
    TextView nameTv, contactTv, licenseNumTv, licenseExpTv, salaryTv, salaryPeriodTv;
    ProgressBar imgProgress, progressBar;

    DriverData driverData;


    private Uri imageUri;
    private StorageReference storageReference;

    private RoomDbHelper dbHelper;
    private DriverDao driverDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_driver);
        findViews();
        storageReference = FirebaseStorage.getInstance().getReference();
        dbHelper = RoomDbHelper.getInstance(ViewDriverActivity.this);
        driverDao = dbHelper.driverDao();
        String id = getIntent().getStringExtra(Util.ID);
        driverData = driverDao.getDriverById(id);
        if (driverData == null) {
            Toast.makeText(this, "Data Not Found!", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setText();
        downloadDriverPic();
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn_view_driver:
                finish();
                break;
            case R.id.edit_btn:
                Intent intent = new Intent(ViewDriverActivity.this, AddDriverActivity.class);
                intent.putExtra("Mode", 2);
                intent.putExtra(Util.ID, driverData.getDriverId());
                startActivity(intent);
                finish();
                break;
            case R.id.delete_btn:
                deleteData();
                break;
            case R.id.driver_img:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1000);
                break;
        }
    }


    private void deleteData() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ViewDriverActivity.this);
        alertBuilder.setTitle("Delete?")
                .setCancelable(true)
                .setMessage("This will delete all the vehicle data including refuel and service data's")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressBar.setVisibility(View.VISIBLE);
                        driverDao.delete(driverData);
                        if (Util.checkNetwork(getSystemService(ConnectivityManager.class))) {
                            String uid = FirebaseAuth.getInstance().getUid();
                            if (uid == null) {
                                DBUtils.addDeletedData(ViewDriverActivity.this, Util.VEHICLE, driverData.getDriverId());
                                return;
                            }
                            FirebaseFirestore database = FirebaseFirestore.getInstance();
                            CollectionReference collection = database.collection("Data").document(uid).collection("Vehicles");
                            new FirebaseHelper().deleteDocumentById(collection, driverData.getDriverId()).addOnCompleteListener(task -> {
                                if (!task.isSuccessful())
                                    DBUtils.addDeletedData(ViewDriverActivity.this, "Vehicle", driverData.getDriverId());
                            });

                        } else
                            DBUtils.addDeletedData(ViewDriverActivity.this, "Vehicle", driverData.getDriverId());

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressBar.setVisibility(View.INVISIBLE);
                        dialogInterface.cancel();
                    }
                });
        alertBuilder.show();

    }


    private void setText() {
        nameTv.setText(driverData.getDriverName());
        contactTv.setText(driverData.getContact());
        licenseNumTv.setText(driverData.getLicenseNum());
        licenseExpTv.setText(driverData.getLicenseExpDate());
        salaryTv.setText(String.valueOf(driverData.getSalary()));
        salaryPeriodTv.setText(driverData.getSalPeriod());
    }

    private void downloadDriverPic() {
        //The name off the picture will be "profile.jpg" inside the path of uid
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getUid() + "/driver/" + driverData.getDriverId() + ".jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                SharedPreferences imagePreferences = getSharedPreferences("Images", MODE_PRIVATE);
                String imagePath = imagePreferences.getString(driverData.getDriverId(), null);
                if (imagePath == null) {
                    new ImageHelper().downloadPicture(ViewDriverActivity.this, driverData.getDriverId(), storageRef)
                            .addOnSuccessListener(new OnSuccessListener<Bitmap>() {
                                @Override
                                public void onSuccess(Bitmap bitmap) {
                                    ImageData.setImage(driverData.getDriverId(), bitmap);
                                    setImage();
                                }
                            });
                } else {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        if (bitmap != null) {
                            ImageData.setImage(driverData.getDriverId(), bitmap);
                            setImage();
                        }
                    } else {
                        new ImageHelper().downloadPicture(ViewDriverActivity.this, driverData.getDriverId(), storageRef)
                                .addOnSuccessListener(new OnSuccessListener<Bitmap>() {
                                    @Override
                                    public void onSuccess(Bitmap bitmap) {
                                        ImageData.setImage(driverData.getDriverId(), bitmap);
                                        setImage();
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

    private void setImage() {
        imgProgress.setVisibility(View.GONE);
        if (ImageData.getImage(driverData.getDriverId()) != null) {
            Glide.with(ViewDriverActivity.this)
                    .load(ImageData.getImage(driverData.getDriverId()))
                    .circleCrop()
                    .into(driverImg);
        } else {
            Glide.with(ViewDriverActivity.this)
                    .load(R.drawable.driver_cartoon)
                    .circleCrop()
                    .into(driverImg);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {

                imageUri = data.getData();
                Glide.with(this)
                        .load(imageUri)
                        .circleCrop()
                        .into(driverImg);

                uploadImage();
            }
        }
    }


    private void uploadImage() {
        String uid = FirebaseAuth.getInstance().getUid();
        String fileName = uid + "/driver/" + driverData.getDriverId() + ".jpg";
        StorageReference picRef = storageReference.child(fileName);
        Task<Boolean> uploadTask = new ImageHelper().uploadPicture(ViewDriverActivity.this, imageUri, picRef);
        uploadTask.addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                new ImageHelper().savePicture(ViewDriverActivity.this, imageUri, driverData.getDriverId());
                setImage();
            }
        });

    }

    private void findViews() {
        driverImg = findViewById(R.id.driver_img);
        nameTv = findViewById(R.id.driver_name_tv);
        contactTv = findViewById(R.id.driver_contact_tv);
        licenseExpTv = findViewById(R.id.license_exp_date);
        licenseNumTv = findViewById(R.id.license_num_tv);
        salaryTv = findViewById(R.id.salary_tv);
        salaryPeriodTv = findViewById(R.id.salary_period_tv);
        imgProgress = findViewById(R.id.progress_driver_img);
        progressBar = findViewById(R.id.progress_driver);
    }
}