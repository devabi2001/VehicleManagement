package com.thirumalaivasa.vehiclemanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;
import com.thirumalaivasa.vehiclemanagement.Utils.DBUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.DateTimeUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.PickerUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.Util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddServiceActivity extends AppCompatActivity {

    private TextView dateTv, timeTv, notesTv;
    private EditText priceEt, serviceChargeEt, totalEt, odometerEt, descEt;
    private Spinner serviceTypeSpinner;

    private AutoCompleteTextView vehicleSpinner;
    private FirebaseAuth mAuth;
    private ExpenseData expenseData;
    private String selectedDate = "", selectedTime = "";
    private int mode = -1;
    private String expenseType = "";
    private RoomDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        findViews();
        mode = getIntent().getIntExtra("Mode", -1);
        expenseType = getIntent().getStringExtra("ExpenseType");

        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    protected void onStart() {
        super.onStart();
        dbHelper = RoomDbHelper.getInstance(AddServiceActivity.this);
        if (mode == 1) {
            List<String> vehicleNumList = dbHelper.vehicleDao().getAllVehicleNumber();
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item);
            arrayAdapter.add("Select Vehicle");
            vehicleSpinner.setText(arrayAdapter.getItem(0));
            arrayAdapter.addAll(vehicleNumList);
            arrayAdapter.remove("Select Vehicle");
            arrayAdapter.notifyDataSetChanged();
            vehicleSpinner.setAdapter(arrayAdapter);
            setServiceTypeSpinner(expenseType, null);

            String[] date_time = DateTimeUtils.getCurrentDateTime();
            selectedDate = date_time[0];
            selectedTime = date_time[1];
            dateTv.setText(selectedDate);
            timeTv.setText(selectedTime);

        } else if (mode == 2) {

            String id = getIntent().getStringExtra(Util.ID);
            ExpenseData previousData = dbHelper.expenseDao().getExpenseById(id);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item);
            arrayAdapter.add(previousData.getVno());
            vehicleSpinner.setText(arrayAdapter.getItem(0));
            expenseType = previousData.getExpenseType();

            if (expenseType.equals("Other")) {
                arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.other_expenses));
                serviceTypeSpinner.setAdapter(arrayAdapter);
            }
            setData(previousData);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        dateTv.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (mode == 2) {
                calendar = DateTimeUtils.convertTimestampToCalendar(expenseData.getTimestamp());
            }
            PickerUtils.showDatePicker(AddServiceActivity.this, ((year, month, day) -> {
                String selectedDate = Util.getDisplayDate(year, month, day);
                dateTv.setText(selectedDate);

            }), calendar);

        });
        timeTv.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (mode == 2) {
                calendar = DateTimeUtils.convertTimestampToCalendar(expenseData.getTimestamp());
            }
            PickerUtils.showTimePicker(AddServiceActivity.this, ((hour, min) -> {
                selectedTime = Util.getDisplayTime(hour, min);
                timeTv.setText(selectedTime);
            }), calendar);
        });

        priceEt.setOnFocusChangeListener((view, b) -> {

        });


        serviceChargeEt.setOnFocusChangeListener((view, b) -> {
            if (!b && !(serviceChargeEt.getText().toString().isEmpty()) && !(priceEt.getText().toString().isEmpty())) {
                double price, serviceCharge, total;
                serviceCharge = Double.parseDouble(serviceChargeEt.getText().toString());
                price = Double.parseDouble(priceEt.getText().toString());
                total = serviceCharge + price;
                totalEt.setText(String.format("%.2f", total));
            }

        });


        totalEt.setOnFocusChangeListener((view, b) -> {
            if (!b && !(totalEt.getText().toString().isEmpty()) && !(priceEt.getText().toString().isEmpty())) {
                double price, serviceCharge, total;
                total = Double.parseDouble(totalEt.getText().toString());
                price = Double.parseDouble(priceEt.getText().toString());
                serviceCharge = total - price;
                serviceChargeEt.setText(String.format("%.2f", serviceCharge));
            }

        });


        serviceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (serviceTypeSpinner.getSelectedItem().toString().equals("Oil Change") || serviceTypeSpinner.getSelectedItem().toString().equals("Clutch")) {
                    notesTv.setVisibility(View.VISIBLE);
                    notesTv.setText("Some Requires odometer value to be entered");
                } else {
                    notesTv.setVisibility(View.INVISIBLE);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_service_btn:
                if (verifyData())
                    addServiceData();
                break;
            case R.id.back_btn_service:
                finish();
                break;
        }
    }

    private boolean verifyData() {
        boolean retValue = true;
        if (vehicleSpinner.getText().toString().equals("Select Vehicle")) {
            Toast.makeText(this, "Select vehicle", Toast.LENGTH_SHORT).show();
            vehicleSpinner.performClick();
            retValue = false;
        }
        if (priceEt.getText().toString().isEmpty()) {
            priceEt.setError("Enter price");
            retValue = false;
        }
        if (serviceChargeEt.getText().toString().isEmpty()) {
            serviceChargeEt.setError("Enter service charge");
            retValue = false;
        }
        if (totalEt.getText().toString().isEmpty()) {
            totalEt.setError("Enter Total Amount");
            retValue = false;
        }
        if (serviceTypeSpinner.getSelectedItem().toString().equals("Oil Change") || serviceTypeSpinner.getSelectedItem().toString().equals("Clutch")) {
            odometerEt.setError("Enter Odometer Value");
            retValue = false;
        }
        return retValue;
    }

    private void addServiceData() {
        getData();
        expenseData.setSynced(false);
        dbHelper.expenseDao().insert(expenseData);
        DBUtils.dbChanged(this, true);
        Toast.makeText(this, "Data Added", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void setData(ExpenseData data) {
        setServiceTypeSpinner(data.getExpenseType(), data.getServiceType());
        Button addBtn = findViewById(R.id.add_service_btn);
        addBtn.setText("Update");

        String date = DateTimeUtils.getLocalDate(data.getTimestamp()).toString();
        String time = DateTimeUtils.getTimeWithoutSeconds(data.getTimestamp());
        dateTv.setText(date);
        timeTv.setText(time);
        priceEt.setText(String.valueOf(data.getPrice()));
        serviceChargeEt.setText(String.valueOf(data.getServiceCharge()));
        totalEt.setText(String.valueOf(data.getTotal()));
        odometerEt.setText(String.valueOf(data.getOdometer()));
        String desc = data.getDesc();
        if (desc != null && !(desc.isEmpty()))
            descEt.setText(desc);
    }

    private void getData() {
        //Common for refuel and service
        String desc, vno, eId;
        double price, total;
        long odometer;
        //Variables for service
        String serviceType;
        double serviceCharge;

        selectedDate = dateTv.getText().toString();
        selectedTime = timeTv.getText().toString();
        desc = descEt.getText().toString();
        vno = vehicleSpinner.getText().toString();
        eId = Util.generateId("Service", vno);
        serviceType = serviceTypeSpinner.getSelectedItem().toString();
        serviceCharge = Double.parseDouble(serviceChargeEt.getText().toString());
        price = Double.parseDouble(priceEt.getText().toString());
        total = Double.parseDouble(totalEt.getText().toString());
        odometer = Long.parseLong(odometerEt.getText().toString());

        long timestamp = DateTimeUtils.convertStringToMilliseconds(selectedDate, selectedTime);
        expenseData = new ExpenseData(expenseType, timestamp, desc, vno, eId, price, total, odometer, serviceType, serviceCharge, false);

    }

    private void setServiceTypeSpinner(String type, String val) {
        ArrayAdapter<String> arrayAdapter;
        String[] stringArray;
        int pos = 0;
        if (type.equals("Other")) {
            stringArray = getResources().getStringArray(R.array.other_expenses);
        } else if (type.equals("Service")) {
            stringArray = getResources().getStringArray(R.array.service_types);
        } else {
            stringArray = getResources().getStringArray(R.array.service_types);
        }
        if (val != null) {
            for (String value : stringArray) {
                if (val.equals(value)) {
                    break;
                }
                pos++;
            }
        }

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stringArray);
        arrayAdapter.notifyDataSetChanged();
        serviceTypeSpinner.setAdapter(arrayAdapter);
        serviceTypeSpinner.setSelection(pos);
    }


    private void findViews() {
        //TextView's
        dateTv = findViewById(R.id.date_service);
        timeTv = findViewById(R.id.time_service);

        notesTv = findViewById(R.id.note_service);

        //EditText's
        priceEt = findViewById(R.id.service_price_et);
        serviceChargeEt = findViewById(R.id.service_charge_et);
        totalEt = findViewById(R.id.service_total_et);
        odometerEt = findViewById(R.id.odometer_et_service);
        descEt = findViewById(R.id.desc_service_et);

        //Spinner's
        vehicleSpinner = findViewById(R.id.vehicle_spinner_service);
        serviceTypeSpinner = findViewById(R.id.service_types_spinner);

    }

}