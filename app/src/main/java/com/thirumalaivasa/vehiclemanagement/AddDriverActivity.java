package com.thirumalaivasa.vehiclemanagement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thirumalaivasa.vehiclemanagement.Helpers.ImageHelper;
import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;
import com.thirumalaivasa.vehiclemanagement.Utils.DBUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.DateTimeUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.PickerUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.Util;

public class AddDriverActivity extends AppCompatActivity {

    private ImageView driverImageBtn;
    private EditText driverNameEt, driverContactEt, licenseNumEt, licenseExpDateEt, salaryEt;
    private ProgressBar progressBar;

    private AutoCompleteTextView salPeriodSpinnerATV;

    private DriverData driverData;
    private Uri imageUri;

    private String selectedDate;

    private RoomDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);
        findViews();

        dbHelper = RoomDbHelper.getInstance(AddDriverActivity.this);

        int mode = getIntent().getIntExtra("Mode", -1);
        if (mode == -1) {
            Toast.makeText(AddDriverActivity.this, "Can't add data please report the issue", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (mode == 1) {
            setSalPeriodSpinner(null);
            selectedDate = DateTimeUtils.getCurrentDateTime()[0];
            licenseExpDateEt.setText(selectedDate);
        } else if (mode == 2) {
            String driverId = getIntent().getStringExtra(Util.ID);
            if (driverId == null)
                finish();
            driverData = dbHelper.driverDao().getDriverById(driverId);
            if (driverData == null)
                finish();
            setData(driverData);
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

        licenseExpDateEt.setOnClickListener(v -> {
            PickerUtils.showDatePicker(AddDriverActivity.this, ((year, month, day) -> {
                selectedDate = Util.getDisplayDate(year, month, day);
                licenseExpDateEt.setText(selectedDate);
            }));
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
                    getData();
                    uploadImage();
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
        driverData.setSynced(false);
        dbHelper.driverDao().insert(driverData);
        DBUtils.dbChanged(AddDriverActivity.this, true);
        finish();
    }


    private void uploadImage() {
        if (imageUri != null) {
            String uid = FirebaseAuth.getInstance().getUid();
            String fileName = uid + "/driver/" + driverData.getDriverId() + ".jpg";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference picRef = storageReference.child(fileName);
            Task<String> task = new ImageHelper().uploadPicture(AddDriverActivity.this, imageUri, picRef);
            task.addOnSuccessListener(s -> {
                driverData.setImagePath(s);
                addData();
                new ImageHelper().savePicture(AddDriverActivity.this, imageUri, driverData.getDriverId());
            }).addOnFailureListener(e -> {
                driverData.setImagePath(null);
                new ImageHelper().savePicture(AddDriverActivity.this, imageUri, driverData.getDriverId());
                //Add sharedPref and store the image name try re-uploading later
                addData();
            });
        } else {
            driverData.setImagePath(null);
            addData();
        }
    }

    private void getData() {

        String driverName, contact, licenseNum, licenseExpDate, driverId, salPeriod;
        double salary;

        driverName = driverNameEt.getText().toString();
        contact = driverContactEt.getText().toString();
        licenseNum = licenseNumEt.getText().toString();
        licenseExpDate = licenseExpDateEt.getText().toString();
        int idLength = 5;
        if (driverName.length() < 5)
            idLength = driverName.length();
        driverId = generateId(driverName.substring(0, idLength), licenseNum);
        salary = Double.parseDouble(salaryEt.getText().toString());
        salPeriod = salPeriodSpinnerATV.getText().toString();

        driverData = new DriverData(driverName, contact, licenseNum, licenseExpDate, driverId, salPeriod, salary, false, null);

    }

    private void setData(DriverData data) {

        driverNameEt.setText(data.getDriverName());
        driverContactEt.setText(data.getContact());
        licenseNumEt.setText(data.getLicenseNum());
        licenseExpDateEt.setText(data.getLicenseExpDate());
        salaryEt.setText(String.valueOf(data.getSalary()));

        Button addBtn = findViewById(R.id.add_driver_btn);
        addBtn.setText("Update");
        setSalPeriodSpinner(data.getSalPeriod());
    }

    private String generateId(String name, String licenseNum) {
        String n = name.replace(" ", "");
        String l = licenseNum.replace(" ", "");
        return n + "$" + l;
    }

    private void setSalPeriodSpinner(String previousPeriod) {
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(AddDriverActivity.this, R.array.salary_period, R.layout.drop_down_item);
        if (previousPeriod != null)
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