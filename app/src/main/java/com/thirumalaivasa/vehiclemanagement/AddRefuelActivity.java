package com.thirumalaivasa.vehiclemanagement;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class AddRefuelActivity extends AppCompatActivity {
    //Widgets
    private TextView dateTv, timeTv, priceTv, literTv, totalTv, odometerTv, descTv, notesTv;
    private EditText priceEt, literEt, totalEt, odometerEt, descEt;
    private AutoCompleteTextView vehicleSpinner;
    ArrayAdapter<String> arrayAdapter;
    private SwitchCompat fillTankSwitch;


    private ExpenseData expenseData, previousData;

    private ArrayList<VehicleData> vehicleDataList;

    //Firebase References
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;

    private String selectedDate = "", selectedTime = "";
    private int selectedDay, selectedMonth, selectedYear, selectedHour, selectedMin;

    private String selectedVehicle = "";
    private int selectedVehiclePos = -1;

    private final String TAG = "VehicleManagement";

    private int mode = -1;
    private DecimalFormat decimalFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_refuel);
        findViews();

        decimalFormat = new DecimalFormat("#.00");
        mode = getIntent().getIntExtra("Mode", -1);
        if (mode == -1) {
            Toast.makeText(AddRefuelActivity.this, "Can't' add or edit refuel data", Toast.LENGTH_SHORT).show();
            finish();
        }
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        if (mode == 1) {
            vehicleDataList = getIntent().getParcelableArrayListExtra("VehicleData");

            if (vehicleDataList == null || vehicleDataList.size() == 0) {
                Toast.makeText(AddRefuelActivity.this, "No vehicle's data found", Toast.LENGTH_SHORT).show();
                finish();
            }

            arrayAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item);
            arrayAdapter.add("Select Vehicle");
            vehicleSpinner.setText(arrayAdapter.getItem(0));

            for (VehicleData data : vehicleDataList) {
                arrayAdapter.add(data.getRegistrationNumber());
            }
            arrayAdapter.remove("Select Vehicle");
            arrayAdapter.notifyDataSetChanged();
            vehicleSpinner.setAdapter(arrayAdapter);


            final Calendar c = Calendar.getInstance();

            // on below line we are getting
            // our day, month and year.
            selectedYear = c.get(Calendar.YEAR);
            selectedMonth = c.get(Calendar.MONTH);
            selectedDay = c.get(Calendar.DAY_OF_MONTH);
            // on below line we are getting our hour, minute.
            selectedHour = c.get(Calendar.HOUR_OF_DAY);
            selectedMin = c.get(Calendar.MINUTE);
        } else if (mode == 2) {

            previousData = getIntent().getParcelableExtra("ExpenseData");

            if (previousData == null) {
                Toast.makeText(AddRefuelActivity.this, "Refuel data not found", Toast.LENGTH_SHORT).show();
                finish();
            }
            arrayAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item);
            arrayAdapter.add(previousData.getVno());
            arrayAdapter.notifyDataSetChanged();
            vehicleSpinner.setText(arrayAdapter.getItem(0));
            vehicleSpinner.setAdapter(arrayAdapter);
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddRefuelActivity.this,
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddRefuelActivity.this,
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


        priceEt.setOnFocusChangeListener((view, b) -> {
            if (b) {
                priceTv.setVisibility(View.VISIBLE);
                priceEt.setHint("");
            } else if (priceEt.getText().toString().isEmpty()) {
                priceTv.setVisibility(View.INVISIBLE);
                priceEt.setHint("Price/L");
            } else priceTv.setVisibility(View.VISIBLE);

            if (!b) {
                if (priceEt.getText().toString().isEmpty()) {
                    if (!literEt.getText().toString().isEmpty() && !totalEt.getText().toString().isEmpty()) {
                        double price, liter, total;
                        liter = Double.parseDouble(literEt.getText().toString());
                        total = Double.parseDouble(totalEt.getText().toString());
                        price = total / liter;

                        price = Double.parseDouble(decimalFormat.format(price));
                        priceTv.setVisibility(View.VISIBLE);
                        priceEt.setText(String.valueOf(price));
                    }
                } else {
                    if (literEt.getText().toString().isEmpty() && !totalEt.getText().toString().isEmpty()) {
                        double liter, price, total;
                        price = Double.parseDouble(priceEt.getText().toString());
                        total = Double.parseDouble(totalEt.getText().toString());
                        liter = total / price;


                        liter = Double.parseDouble(decimalFormat.format(liter));
                        literTv.setVisibility(View.VISIBLE);
                        literEt.setText(String.valueOf(liter));
                    } else if (!literEt.getText().toString().isEmpty() && totalEt.getText().toString().isEmpty()) {
                        double total, liter, price;
                        price = Double.parseDouble(priceEt.getText().toString());
                        liter = Double.parseDouble(literEt.getText().toString());
                        total = price * liter;

                        total = Double.parseDouble(decimalFormat.format(total));
                        totalTv.setVisibility(View.VISIBLE);
                        totalEt.setText(String.valueOf(total));
                    }
//                        else if(!literEt.getText().toString().isEmpty() && !totalEt.getText().toString().isEmpty()){
//                            double total,liter,price;
//                            total = Double.parseDouble(totalEt.getText().toString());
//                            liter = Double.parseDouble(literEt.getText().toString());
//                            price = Double.parseDouble(priceEt.getText().toString());
//                            if(price != (total/liter)){
//                                priceEt.setError("Enter Valid Amount");
//                            }
//                        }
                }
            }


        });

        literEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    literTv.setVisibility(View.VISIBLE);
                    literEt.setHint("");
                } else if (literEt.getText().toString().isEmpty()) {
                    literTv.setVisibility(View.INVISIBLE);
                    literEt.setHint("Liters");
                } else
                    literTv.setVisibility(View.VISIBLE);
                if (!b) {
                    if (literEt.getText().toString().isEmpty()) {
                        if (!priceEt.getText().toString().isEmpty() && !totalEt.getText().toString().isEmpty()) {
                            double price, liter, total;
                            price = Double.parseDouble(priceEt.getText().toString());
                            total = Double.parseDouble(totalEt.getText().toString());
                            liter = total / price;
                            liter = Double.parseDouble(decimalFormat.format(liter));
                            literTv.setVisibility(View.VISIBLE);
                            literEt.setText(String.valueOf(liter));
                        }
                    } else {
                        if (priceEt.getText().toString().isEmpty() && !totalEt.getText().toString().isEmpty()) {
                            double liter, price, total;
                            liter = Double.parseDouble(literEt.getText().toString());
                            total = Double.parseDouble(totalEt.getText().toString());
                            price = total / liter;
                            price = Double.parseDouble(decimalFormat.format(price));
                            priceEt.setVisibility(View.VISIBLE);
                            priceEt.setText(String.valueOf(price));
                        } else if (!priceEt.getText().toString().isEmpty() && totalEt.getText().toString().isEmpty()) {
                            double total, liter, price;
                            price = Double.parseDouble(priceEt.getText().toString());
                            liter = Double.parseDouble(literEt.getText().toString());
                            total = price * liter;
                            total = Double.parseDouble(decimalFormat.format(total));
                            totalTv.setVisibility(View.VISIBLE);
                            totalEt.setText(String.valueOf(total));
                        }
//                        else if(!priceEt.getText().toString().isEmpty() && !totalEt.getText().toString().isEmpty()){
//                            double total,liter,price;
//                            total = Double.parseDouble(totalEt.getText().toString());
//                            liter = Double.parseDouble(literEt.getText().toString());
//                            price = Double.parseDouble(priceEt.getText().toString());
//                            if(liter != (total/price)){
//                                literEt.setError("Enter Valid Amount");
//                            }
//                        }
                    }
                }
                setFuelSwitch();
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
                    totalEt.setHint("Total cost");
                } else
                    totalTv.setVisibility(View.VISIBLE);
                if (!b) {
                    if (totalEt.getText().toString().isEmpty()) {
                        if (!literEt.getText().toString().isEmpty() && !priceEt.getText().toString().isEmpty()) {
                            double price, liter, total;
                            liter = Double.parseDouble(literEt.getText().toString());
                            price = Double.parseDouble(priceEt.getText().toString());
                            total = price * liter;
                            total = Double.parseDouble(decimalFormat.format(total));
                            totalTv.setVisibility(View.VISIBLE);
                            totalEt.setText(String.valueOf(total));
                        }
                    } else {
                        if (literEt.getText().toString().isEmpty() && !priceEt.getText().toString().isEmpty()) {
                            double liter, price, total;
                            price = Double.parseDouble(priceEt.getText().toString());
                            total = Double.parseDouble(totalEt.getText().toString());
                            liter = total / price;
                            liter = Double.parseDouble(decimalFormat.format(liter));
                            literTv.setVisibility(View.VISIBLE);
                            literEt.setText(String.valueOf(liter));
                        } else if (!literEt.getText().toString().isEmpty() && priceEt.getText().toString().isEmpty()) {
                            double total, liter, price;
                            total = Double.parseDouble(totalEt.getText().toString());
                            liter = Double.parseDouble(literEt.getText().toString());
                            price = total / liter;
                            price = Double.parseDouble(decimalFormat.format(price));
                            priceTv.setVisibility(View.VISIBLE);
                            priceEt.setText(String.valueOf(price));
                        }
//                        else if(!literEt.getText().toString().isEmpty() && !priceEt.getText().toString().isEmpty()){
//                            double total,liter,price;
//                            total = Double.parseDouble(totalEt.getText().toString());
//                            liter = Double.parseDouble(literEt.getText().toString());
//                            price = Double.parseDouble(priceEt.getText().toString());
//                            if(total != (price*liter)){
//                                totalEt.setError("Enter Valid Amount");
//                            }
//                        }
                    }
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

        descEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        fillTankSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    fillTankSwitch.setTextColor(ContextCompat.getColor(AddRefuelActivity.this, R.color.icon));
                else
                    fillTankSwitch.setTextColor(ContextCompat.getColor(AddRefuelActivity.this, R.color.text_color));
            }
        });

        vehicleSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedVehicle = vehicleSpinner.getText().toString();
                selectedVehiclePos= position;

            }
        });


    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_refuel_btn:
                if (!validateAmount()) {
                    totalEt.setError("Total amount is not valid");
                    return;
                }
                if (verifyData())
                    addRefuelData();
                break;
            case R.id.back_btn_refuel:
                finish();
                break;
        }
    }

    private boolean validateAmount() {
        double price, liters, total;
        price = Double.parseDouble(priceEt.getText().toString());
        liters = Double.parseDouble(literEt.getText().toString());
        total = Double.parseDouble(totalEt.getText().toString());

        double calculatedTotal = price * liters;
        double difference = Math.abs(calculatedTotal - total);

        return difference <= 1.0;
    }


    private boolean verifyData() {
        boolean retValue = true;
//        if (vehicleSpinner.getSelectedItem().toString().equals("Select Vehicle")) {
        if (selectedVehicle.equals("Select Vehicle") || selectedVehicle.isEmpty()) {
            Toast.makeText(this, "Select vehicle", Toast.LENGTH_SHORT).show();
            vehicleSpinner.performClick();
            retValue = false;
        }
        if (priceEt.getText().toString().isEmpty()) {
            priceEt.setError("Enter price / ltr");
            retValue = false;
        }
        if (literEt.getText().toString().isEmpty()) {
            literEt.setError("Enter Liters Filled");
            retValue = false;
        }
        if (totalEt.getText().toString().isEmpty()) {
            totalEt.setError("Enter Total Amount");
            retValue = false;
        }
        if (odometerEt.getText().toString().isEmpty()) {
            odometerEt.setError("Enter Odometer Value");
            retValue = false;
        }
        return retValue;
    }

    private void addRefuelData() {
        getData();

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null)
            database.collection("Data").document(uid)
                    .collection("Expense").document(expenseData.geteId())
                    .set(expenseData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddRefuelActivity.this, "Data Added", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Log.e(TAG, "Add Refuel Data", task.getException());
                                Toast.makeText(AddRefuelActivity.this, "Error!!! Try again later ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    }

    @SuppressLint("SetTextI18n")
    private void setData(ExpenseData data) {
        Button addBtn = findViewById(R.id.add_refuel_btn);
        addBtn.setText("Update");
        priceTv.setVisibility(View.VISIBLE);
        literTv.setVisibility(View.VISIBLE);
        totalTv.setVisibility(View.VISIBLE);
        odometerTv.setVisibility(View.VISIBLE);
        descTv.setVisibility(View.VISIBLE);

        dateTv.setText(data.getDate());
        timeTv.setText(data.getTime());
        literEt.setText(String.valueOf(data.getLiters()));
        priceEt.setText(String.valueOf(data.getPrice()));
        totalEt.setText(String.valueOf(data.getTotal()));
        odometerEt.setText(String.valueOf(data.getOdometer()));
        fillTankSwitch.setChecked(data.isTankFilled());
        String desc = data.getDesc();
        if (desc != null && !(desc.isEmpty()))
            descEt.setText(desc);

    }


    private void getData() {
        //Common for refuel and service
        String date, time, desc, vno, eId = "";
        double price, total;
        long odometer;
        //Variables for refuel
        double liters;
        boolean isTankFilled;
        String fuelType = "";

        double tankCap = 0.0;
        double percentage = 0.0;


        date = dateTv.getText().toString();
        time = timeTv.getText().toString();
        desc = descEt.getText().toString();
        //vno = vehicleSpinner.getSelectedItem().toString();
        vno = selectedVehicle;

        liters = Double.parseDouble(literEt.getText().toString());
        price = Double.parseDouble(priceEt.getText().toString());
        total = Double.parseDouble(totalEt.getText().toString());
        odometer = Long.parseLong(odometerEt.getText().toString());
        isTankFilled = fillTankSwitch.isChecked();


        if (mode == 1) {
            eId = generateId(date, time, vno);

            for (VehicleData vehicle : vehicleDataList) {
                if (vehicle.getRegistrationNumber().equals(selectedVehicle)) {
                    tankCap = vehicle.getFuelCapacity();
                    percentage = (liters / tankCap) * 100;
                    percentage = Double.parseDouble(decimalFormat.format(percentage));
                    fuelType = vehicle.getFuelType();
                    break;
                }
            }
        } else if (mode == 2) {
            eId = previousData.geteId();
            tankCap = (previousData.getLiters() / previousData.getPercentOfTank()) * 100;
            percentage = (liters / tankCap) * 100;
            percentage = Double.parseDouble(decimalFormat.format(percentage));
        }

        expenseData = new ExpenseData("Refuel", date, time, desc, vno, eId, price, total, odometer, liters, isTankFilled, percentage, fuelType);

    }

    private String generateId(String date, String time, String vno) {
        String retValue = "";
        String uId = Objects.requireNonNull(mAuth.getUid()).substring(0, 5);
        String d = date.replace("-", "");
        String t = time.replace(":", "");
        String v = vno.replace(" ", "");
        retValue = uId + "refuel" + d + t + v;
        return retValue;
    }


    @SuppressLint("SimpleDateFormat")
    private String[] getDateAndTime() {
        String[] retValue = new String[2];
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        retValue[0] = sdf.format(c.getTime());
        sdf = new SimpleDateFormat("hh:mm");
        retValue[1] = sdf.format(c.getTime());
        return retValue;
    }

    @SuppressLint("SetTextI18n")
    private void setFuelSwitch() {
        if (!(literEt.getText().toString().isEmpty())) {
            double liter = Double.parseDouble(literEt.getText().toString());
            if (mode == 1) {
//                if (!(vehicleSpinner.getSelectedItem().toString().equals("Select Vehicle"))) {
                Log.i(TAG, "setFuelSwitch: "+selectedVehicle);
                if (!(selectedVehicle.equals("Select Vehicle") || selectedVehicle.isEmpty()) && selectedVehiclePos != -1) {
                    if (liter >= vehicleDataList.get(selectedVehiclePos).getFuelCapacity()) {
                        fillTankSwitch.setChecked(true);
                        fillTankSwitch.setEnabled(false);
                        notesTv.setVisibility(View.VISIBLE);
                        notesTv.setText("   Refilled fuel liters is greater than or equal to vehicle's tank capacity, filling tank switch is activated");
                    } else {
                        fillTankSwitch.setChecked(false);
                        fillTankSwitch.setEnabled(true);
                        notesTv.setText("");
                        notesTv.setVisibility(View.INVISIBLE);
                    }
                }
            } else {
                int tankCap = (int) ((previousData.getLiters() / previousData.getPercentOfTank()) * 100);
                if (liter >= tankCap) {
                    fillTankSwitch.setChecked(true);
                    fillTankSwitch.setEnabled(false);
                    notesTv.setVisibility(View.VISIBLE);
                    notesTv.setText("   Refilled fuel liters is greater than or equal to vehicle's tank capacity filling tank switch is activated");
                } else {
                    fillTankSwitch.setChecked(false);
                    fillTankSwitch.setEnabled(true);
                    notesTv.setText("");
                    notesTv.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void findViews() {

        ///Text View's
        dateTv = findViewById(R.id.date_refuel);
        timeTv = findViewById(R.id.time_refuel);
        priceTv = findViewById(R.id.fuel_price_tv);
        literTv = findViewById(R.id.fuel_liters_tv);
        totalTv = findViewById(R.id.fuel_total_tv);
        odometerTv = findViewById(R.id.odometer_tv_refuel);
        descTv = findViewById(R.id.desc_refuel_tv);
        notesTv = findViewById(R.id.note_refuel);


        priceEt = findViewById(R.id.fuel_price_et);
        literEt = findViewById(R.id.fuel_liters_et);
        totalEt = findViewById(R.id.fuel_total_et);
        odometerEt = findViewById(R.id.odometer_et_refuel);
        descEt = findViewById(R.id.desc_refuel_et);

        vehicleSpinner = findViewById(R.id.vehicle_spinner_refuel);

        fillTankSwitch = findViewById(R.id.tank_fill_switch);
    }
}