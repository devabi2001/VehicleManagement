package com.thirumalaivasa.vehiclemanagement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thirumalaivasa.vehiclemanagement.Helpers.ImageHelper;
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;

import java.util.Calendar;

public class AddDriverActivity extends AppCompatActivity {

    private ImageView driverImageBtn;
    private EditText driverNameEt, driverContactEt, licenseNumEt, licenseExpDateEt, salaryEt;
    private ProgressBar progressBar;

    private AutoCompleteTextView salPeriodSpinnerATV;

    private DriverData driverData;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore database;

    private final String TAG = "VehicleManagement";
    private int selectedDay, selectedMonth, selectedYear;
    private String selectedDate;

    private int mode = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);
        findViews();
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseFirestore.getInstance();

        final Calendar c = Calendar.getInstance();

        mode = getIntent().getIntExtra("Mode", -1);
        if(mode == -1){
            Toast.makeText(AddDriverActivity.this, "Can't add data please report the issue", Toast.LENGTH_SHORT).show();
            finish();
        }
        driverData = getIntent().getParcelableExtra("DriverData");


        if (mode == 1) {
            setSalPeriodSpinner(null);

            //this date and time is used only for id creation
            // on below line we are getting
            // our day, month and year.
            selectedYear = c.get(Calendar.YEAR);
            selectedMonth = c.get(Calendar.MONTH);
            selectedDay = c.get(Calendar.DAY_OF_MONTH);
            selectedDate = selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear;
            licenseExpDateEt.setText(selectedDate);
        } else if (mode == 2) {
            setData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        licenseExpDateEt.setShowSoftInputOnFocus(false);


        driverImageBtn.setOnClickListener(view -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 1000);
        });

        licenseExpDateEt.setOnClickListener(view -> {

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddDriverActivity.this,
                    (datePicker, year, month, day) -> {

                        String d, m;
                        if (day < 10)
                            d = "0" + day;
                        else
                            d = String.valueOf(day);
                        if (month <= 10)
                            m = "0" + (month + 1);
                        else
                            m = String.valueOf(month + 1);

                        selectedDate = d + "-" + m + "-" + year;
                        licenseExpDateEt.setText(selectedDate);
                        selectedDay = day;
                        selectedMonth = month;
                        selectedYear = year;
                    }, selectedYear, selectedMonth, selectedDay);


            datePickerDialog.show();

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {

                assert data != null;
                imageUri = data.getData();
                Glide.with(AddDriverActivity.this)
                        .load(imageUri)
                        .into(driverImageBtn);
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.add_driver_btn:
                if (verifyData()) {
                    progressBar.setVisibility(View.VISIBLE);
                    addData();
                }
                break;
        }
    }


    private boolean verifyData() {
        boolean retValue = true;

        if (driverNameEt.getText().toString().isEmpty()) {
            driverNameEt.setError("Enter Driver  Name");
            retValue = false;
        }

        if (driverContactEt.getText().toString().isEmpty()) {
            driverContactEt.setError("Enter Driver Contact");
            retValue = false;
        }

        if (licenseNumEt.getText().toString().isEmpty()) {
            licenseNumEt.setError("Enter License Number");
            retValue = false;
        }
        if (licenseExpDateEt.getText().toString().isEmpty()) {
            licenseExpDateEt.setError("Enter License Expiry Date");
            retValue = false;
        }

        if (salaryEt.getText().toString().isEmpty()) {
            salaryEt.setError("Enter Salary");
            retValue = false;
        }

        return retValue;
    }

    private void addData() {
        getData();

        String uid = mAuth.getUid();
        if (uid != null)
            database.collection("Data").document(uid)
                    .collection("DriverData").document(driverData.getDriverId())
                    .set(driverData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (imageUri != null)
                                uploadImage();
                            else
                                finish();
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.e(TAG, "Exception In Adding Driver Data", task.getException());
                            Toast.makeText(AddDriverActivity.this, "Error!!! Try again later ", Toast.LENGTH_SHORT).show();
                        }
                    });
    }

    private void uploadImage() {
        String uid = FirebaseAuth.getInstance().getUid();
        String fileName = uid + "/driver/" + driverData.getDriverId() + ".jpg";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference picRef = storageReference.child(fileName);
        Task<Boolean> uploadTask = new ImageHelper().uploadPicture(AddDriverActivity.this, imageUri, storageReference);
        uploadTask.addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                new ImageHelper().savePicture(AddDriverActivity.this, imageUri, driverData.getDriverId());
            }
        });

    }

    private void getData() {

        String driverName, contact, licenseNum, licenseExpDate, driverId, salPeriod;
        double salary;

        driverName = driverNameEt.getText().toString();
        contact = driverContactEt.getText().toString();
        licenseNum = licenseNumEt.getText().toString();
        licenseExpDate = licenseExpDateEt.getText().toString();
        driverId = generateId(driverName, licenseNum);
        salary = Double.parseDouble(salaryEt.getText().toString());
        salPeriod = salPeriodSpinnerATV.getText().toString();

        driverData = new DriverData(driverName, contact, licenseNum, licenseExpDate, driverId, salary, salPeriod);

    }

    private void setData() {

        driverNameEt.setText(driverData.getDriverName());
        driverContactEt.setText(driverData.getContact());
        licenseNumEt.setText(driverData.getLicenseNum());
        licenseExpDateEt.setText(driverData.getLicenseExpDate());
        salaryEt.setText(String.valueOf(driverData.getSalary()));

        Button addBtn = findViewById(R.id.add_driver_btn);
        addBtn.setText("Update");
        setSalPeriodSpinner(driverData.getSalPeriod());
    }

    private String generateId(String name, String licenseNum) {
        String retValue;

        String n = name.replace(" ", "");
        String l = licenseNum.replace(" ", "");
        retValue = n + "$" + l;
        return retValue;
    }

    private void setSalPeriodSpinner(String previousPeriod){
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(AddDriverActivity.this, R.array.salary_period, R.layout.drop_down_item);
        if(previousPeriod!=null)
            salPeriodSpinnerATV.setText(previousPeriod);
        else
            salPeriodSpinnerATV.setText(arrayAdapter.getItem(0));
        arrayAdapter.notifyDataSetChanged();
        salPeriodSpinnerATV.setAdapter(arrayAdapter);


    }

    private void findViews() {
        driverImageBtn = findViewById(R.id.driver_image_add);

        driverNameEt = findViewById(R.id.driver_name_et);
        driverContactEt = findViewById(R.id.driver_contact_et);
        licenseNumEt = findViewById(R.id.driver_license_et);
        licenseExpDateEt = findViewById(R.id.driver_license_exp_date_et);
        salaryEt = findViewById(R.id.driver_salary_et);

        salPeriodSpinnerATV = findViewById(R.id.salary_period_spinner);

        progressBar = findViewById(R.id.progress_add_driver);

    }

}