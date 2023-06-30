package com.thirumalaivasa.vehiclemanagement;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddServiceActivity extends AppCompatActivity {

    private TextView dateTv, timeTv, priceTv, serviceChargeTv, totalTv, odometerTv, descTv, notesTv;
    private EditText priceEt, serviceChargeEt, totalEt, odometerEt, descEt, serviceTypeEt;
    private Spinner serviceTypeSpinner;

    private AutoCompleteTextView vehicleSpinner,serviceTypeSpinnerATV;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private ExpenseData expenseData;
    private String selectedDate = "", selectedTime = "";
    private int selectedDay, selectedMonth, selectedYear, selectedHour, selectedMin;
    private int mode = -1;
    private String expenseType = "";

    private final String TAG = "VehicleManagement";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        findViews();
        mode = getIntent().getIntExtra("Mode", -1);
        expenseType = getIntent().getStringExtra("ExpenseType");

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        if (mode == 1) {
            ArrayList<VehicleData> vehicleDataList = getIntent().getParcelableArrayListExtra("VehicleData");

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item);
            arrayAdapter.add("Select Vehicle");
            vehicleSpinner.setText(arrayAdapter.getItem(0));

            for (VehicleData data : vehicleDataList) {
                arrayAdapter.add(data.getRegistrationNumber());
            }
            arrayAdapter.remove("Select Vehicle");
            arrayAdapter.notifyDataSetChanged();
            vehicleSpinner.setAdapter(arrayAdapter);

            if (expenseType.equals("Other")) {
                arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.other_expenses));
                serviceTypeSpinner.setAdapter(arrayAdapter);
            }

            final Calendar c = Calendar.getInstance();

            // on below line we are getting
            // our day, month and year.
            selectedYear = c.get(Calendar.YEAR);
            selectedMonth = c.get(Calendar.MONTH);
            selectedDay = c.get(Calendar.DAY_OF_MONTH);
            // on below line we are getting our hour, minute.
            selectedHour = c.get(Calendar.HOUR_OF_DAY);
            selectedMin = c.get(Calendar.MINUTE);
        }
        else if (mode == 2) {

            ExpenseData previousData = getIntent().getParcelableExtra("ExpenseData");

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
            arrayAdapter.add(previousData.getVno());
            arrayAdapter.notifyDataSetChanged();
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            vehicleSpinner.setAdapter(arrayAdapter);


            expenseType = previousData.getExpenseType();

            if (expenseType.equals("Other")) {
                arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.other_expenses));
                serviceTypeSpinner.setAdapter(arrayAdapter);
            }
            setData(previousData);

            // on below line we are getting the date value by parsing the date and time values from string to set date and time picker values
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
            LocalDate date = LocalDate.parse(previousData.getDate(), dateFormat);
            LocalTime time = LocalTime.parse(previousData.getTime(), timeFormat);


            selectedYear = date.getYear();
            selectedMonth = date.getMonthValue();
            selectedDay = date.getDayOfMonth();
            // on below line we are getting our hour, minute.
            selectedHour = time.getHour();
            selectedMin = time.getMinute();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mode == 1) {
            String[] date_time = getDateAndTime();
            dateTv.setText(date_time[0]);
            timeTv.setText(date_time[1]);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        dateTv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddServiceActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
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
                                dateTv.setText(selectedDate);
                                selectedDay = day;
                                selectedMonth = month;
                                selectedYear = year;
                            }
                        }, selectedYear, selectedMonth, selectedDay);


                datePickerDialog.show();

            }
        });

        timeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // on below line we are initializing our Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddServiceActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String h, m;
                                if (hourOfDay < 10)
                                    h = "0" + hourOfDay;
                                else
                                    h = String.valueOf(hourOfDay);
                                if (minute < 10)
                                    m = "0" + minute;
                                else
                                    m = String.valueOf(minute);

                                // on below line we are setting selected time in our text view.
                                selectedTime = h + ":" + m;
                                timeTv.setText(selectedTime);
                                selectedHour = hourOfDay;
                                selectedMin = minute;

                            }
                        }, selectedHour, selectedMin, false);


                // at last we are calling show to display our time picker dialog.
                timePickerDialog.show();

            }
        });

        priceEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    priceTv.setVisibility(View.VISIBLE);
                    priceEt.setHint("");
                } else if (priceEt.getText().toString().isEmpty()) {
                    priceTv.setVisibility(View.INVISIBLE);
                    priceEt.setHint("Price");
                } else
                    priceTv.setVisibility(View.VISIBLE);


            }
        });


        serviceChargeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    serviceChargeTv.setVisibility(View.VISIBLE);
                    serviceChargeEt.setHint("");
                } else if (serviceChargeEt.getText().toString().isEmpty()) {
                    serviceChargeTv.setVisibility(View.INVISIBLE);
                    serviceChargeEt.setHint("Service Charge");
                } else
                    serviceChargeTv.setVisibility(View.VISIBLE);
                if (!b && !(serviceChargeEt.getText().toString().isEmpty()) && !(priceEt.getText().toString().isEmpty())) {
                    double price, serviceCharge, total;
                    serviceCharge = Double.parseDouble(serviceChargeEt.getText().toString());
                    price = Double.parseDouble(priceEt.getText().toString());
                    total = serviceCharge + price;
                    totalEt.setText(String.format("%.2f", total));
                }

            }
        });


        totalEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    totalTv.setVisibility(View.VISIBLE);
                    totalEt.setHint("");
                } else if (totalEt.getText().toString().isEmpty()) {
                    totalTv.setVisibility(View.INVISIBLE);
                    totalEt.setHint("Total Cost");
                } else
                    totalTv.setVisibility(View.VISIBLE);
                if (!b && !(totalEt.getText().toString().isEmpty()) && !(priceEt.getText().toString().isEmpty())) {
                    double price, serviceCharge, total;
                    total = Double.parseDouble(totalEt.getText().toString());
                    price = Double.parseDouble(priceEt.getText().toString());
                    serviceCharge = total - price;
                    serviceChargeEt.setText(String.format("%.2f", serviceCharge));
                }

            }
        });


        odometerEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    odometerTv.setVisibility(View.VISIBLE);
                    odometerEt.setHint("");
                } else if (odometerEt.getText().toString().isEmpty()) {
                    odometerTv.setVisibility(View.INVISIBLE);
                    odometerEt.setHint("Odometer");
                } else
                    odometerTv.setVisibility(View.VISIBLE);


            }
        });


        descTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    descTv.setVisibility(View.VISIBLE);
                    descEt.setHint("");
                } else if (descEt.getText().toString().isEmpty()) {
                    descTv.setVisibility(View.INVISIBLE);
                    descEt.setHint("Description");
                } else
                    descTv.setVisibility(View.VISIBLE);

            }
        });


        serviceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItem().toString().equals("Others"))
                    serviceTypeEt.setVisibility(View.VISIBLE);
                else
                    serviceTypeEt.setVisibility(View.GONE);
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
        String uid = mAuth.getUid();
        if (uid != null) {
            database.collection("Data").document(uid)
                    .collection("Expense").document(expenseData.geteId())
                    .set(expenseData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddServiceActivity.this, "Data Added", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Log.e(TAG, "Add Service Data", task.getException());
                                Toast.makeText(AddServiceActivity.this, "Error!!! Try again later ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    private void setData(ExpenseData data) {
        String[] stringArray;

        if (data.getExpenseType().equals("Service"))
            stringArray = getResources().getStringArray(R.array.service_types);
        else
            stringArray = getResources().getStringArray(R.array.other_expenses);
        int position = -1;
        for (String value : stringArray) {
            if (value.equals(data.getServiceType())) {
                position++;
                break;
            }
            position++;
        }
        Button addBtn = findViewById(R.id.add_service_btn);
        addBtn.setText("Update");
        priceTv.setVisibility(View.VISIBLE);
        serviceChargeTv.setVisibility(View.VISIBLE);
        totalTv.setVisibility(View.VISIBLE);
        odometerTv.setVisibility(View.VISIBLE);
        descTv.setVisibility(View.VISIBLE);

        dateTv.setText(data.getDate());
        timeTv.setText(data.getTime());
        priceEt.setText(String.valueOf(data.getPrice()));
        serviceChargeEt.setText(String.valueOf(data.getServiceCharge()));
        totalEt.setText(String.valueOf(data.getTotal()));
        odometerEt.setText(String.valueOf(data.getOdometer()));

        //Set service type in spinner and edit text value
        if (position != -1 && position < stringArray.length) {
            serviceTypeSpinner.setSelection(position);
            String serviceType = serviceTypeSpinner.getSelectedItem().toString();
            if (serviceType.equals("Others")) {
                serviceTypeEt.setVisibility(View.VISIBLE);
                serviceTypeEt.setText(data.getServiceType());
            }
        }


        String desc = data.getDesc();
        if (desc != null && !(desc.isEmpty()))
            descEt.setText(desc);
    }

    private void getData() {
        //Common for refuel and service
        String date, time, desc, vno, eId;
        double price, total;
        long odometer;
        //Variables for service
        String serviceType;
        double serviceCharge;

        date = dateTv.getText().toString();
        time = timeTv.getText().toString();
        desc = descEt.getText().toString();
        vno = vehicleSpinner.getText().toString();
        eId = generateId(date, time, vno);
        serviceType = serviceTypeSpinner.getSelectedItem().toString();
        serviceCharge = Double.parseDouble(serviceChargeEt.getText().toString());
        price = Double.parseDouble(priceEt.getText().toString());
        total = Double.parseDouble(totalEt.getText().toString());
        odometer = Long.parseLong(odometerEt.getText().toString());

        if (serviceTypeSpinner.getSelectedItem().toString().equals("Others")) {
            serviceType = serviceTypeEt.getText().toString();
        }


        expenseData = new ExpenseData(expenseType, date, time, desc, vno, eId, price, total, odometer, serviceType, serviceCharge);

    }

    private String[] getDateAndTime() {
        String retValue[] = new String[2];
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        retValue[0] = sdf.format(c.getTime());
        sdf = new SimpleDateFormat("hh:mm");
        retValue[1] = sdf.format(c.getTime());
        return retValue;
    }

    private String generateId(String date, String time, String vno) {
        String retValue = "";
        String uId = mAuth.getUid().substring(0, 5);
        String d = date.replace("-", "");
        String t = time.replace(":", "");
        String v = vno.replace(" ", "");
        retValue = uId + expenseType.toLowerCase(Locale.getDefault()) + d + t + v;
        return retValue;
    }


    private void findViews() {
        //TextView's
        dateTv = findViewById(R.id.date_service);
        timeTv = findViewById(R.id.time_service);
        priceTv = findViewById(R.id.service_price_tv);
        serviceChargeTv = findViewById(R.id.service_charge_tv);
        totalTv = findViewById(R.id.service_total_tv);
        odometerTv = findViewById(R.id.odometer_tv_service);
        descTv = findViewById(R.id.desc_service_tv);
        notesTv = findViewById(R.id.note_service);

        //EditText's
        priceEt = findViewById(R.id.service_price_et);
        serviceChargeEt = findViewById(R.id.service_charge_et);
        totalEt = findViewById(R.id.service_total_et);
        odometerEt = findViewById(R.id.odometer_et_service);
        descEt = findViewById(R.id.desc_service_et);
        serviceTypeEt = findViewById(R.id.service_type_et);

        //Spinner's
        vehicleSpinner = findViewById(R.id.vehicle_spinner_service);
        serviceTypeSpinner = findViewById(R.id.service_types_spinner);

    }

}