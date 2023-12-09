package com.thirumalaivasa.vehiclemanagement;

import static com.thirumalaivasa.vehiclemanagement.Utils.Util.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;
import com.thirumalaivasa.vehiclemanagement.Utils.DBUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.DateTimeUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.PickerUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.Util;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

public class AddRefuelActivity extends AppCompatActivity {
    //Widgets
    private TextView dateTv, timeTv, notesTv;
    private EditText priceEt, literEt, totalEt, odometerEt, descEt;
    private AutoCompleteTextView vehicleSpinner;
    private SwitchCompat fillTankSwitch;


    private ExpenseData expenseData;

    private VehicleData selectedVehicleData;

    private String selectedDate = "", selectedTime = "";

    private String selectedVehicle = "";
    private int selectedVehiclePos = -1;

    private int mode = -1;
    private DecimalFormat decimalFormat;

    private RoomDbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_refuel);
        findViews();

        decimalFormat = new DecimalFormat("#.00");
        dbHelper = RoomDbHelper.getInstance(AddRefuelActivity.this);
        mode = getIntent().getIntExtra("Mode", -1);
        if (mode == -1) {
            Toast.makeText(AddRefuelActivity.this, "Can't' add or edit refuel data", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (mode > 2)
            mode = 1;

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mode == 1) {

            List<String> vehicleNumList = dbHelper.vehicleDao().getAllVehicleNumber();

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item);
            arrayAdapter.add("Select Vehicle");
            vehicleSpinner.setText(arrayAdapter.getItem(0));

            if (vehicleNumList != null && vehicleNumList.size() != 0) {
                arrayAdapter.addAll(vehicleNumList);
            } else {
                Toast.makeText(AddRefuelActivity.this, "No vehicle's data found", Toast.LENGTH_SHORT).show();
                finish();
            }
            arrayAdapter.remove("Select Vehicle");
            arrayAdapter.notifyDataSetChanged();
            vehicleSpinner.setAdapter(arrayAdapter);
            String[] date_time = DateTimeUtils.getCurrentDateTime();
            selectedDate = date_time[0];
            selectedTime = date_time[1];
            dateTv.setText(selectedDate);
            timeTv.setText(selectedTime);
        } else if (mode == 2) {
            String id = getIntent().getStringExtra("ID");
            if (id == null)
                finish();

            expenseData = dbHelper.expenseDao().getExpenseById(id);

            if (expenseData == null) {
                Toast.makeText(AddRefuelActivity.this, "Refuel data not found", Toast.LENGTH_SHORT).show();
                finish();
            }
            String vno = expenseData.getVno();
            selectedVehicleData = dbHelper.vehicleDao().getVehicleByRegNum(vno);
            vehicleSpinner.setText(vno);
            selectedVehicle = expenseData.getVno();
            setData(expenseData);

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
            PickerUtils.showDatePicker(AddRefuelActivity.this, ((year, month, day) -> {
                String selectedDate = Util.getDisplayDate(year, month, day);
                dateTv.setText(selectedDate);

            }), calendar);

        });


        timeTv.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (mode == 2) {
                calendar = DateTimeUtils.convertTimestampToCalendar(expenseData.getTimestamp());
            }
            PickerUtils.showTimePicker(AddRefuelActivity.this, ((hour, min) -> {
                selectedTime = Util.getDisplayTime(hour, min);
                timeTv.setText(selectedTime);

            }), calendar);
        });


        priceEt.setOnFocusChangeListener((view, b) -> {

            if (!b) {
                if (priceEt.getText().toString().isEmpty()) {
                    if (!literEt.getText().toString().isEmpty() && !totalEt.getText().toString().isEmpty()) {
                        double price, liter, total;
                        liter = Double.parseDouble(literEt.getText().toString());
                        total = Double.parseDouble(totalEt.getText().toString());
                        price = total / liter;

                        price = Double.parseDouble(decimalFormat.format(price));
                        priceEt.setText(String.valueOf(price));
                    }
                } else {
                    if (literEt.getText().toString().isEmpty() && !totalEt.getText().toString().isEmpty()) {
                        double liter, price, total;
                        price = Double.parseDouble(priceEt.getText().toString());
                        total = Double.parseDouble(totalEt.getText().toString());
                        liter = total / price;


                        liter = Double.parseDouble(decimalFormat.format(liter));
                        literEt.setText(String.valueOf(liter));
                    } else if (!literEt.getText().toString().isEmpty() && totalEt.getText().toString().isEmpty()) {
                        double total, liter, price;
                        price = Double.parseDouble(priceEt.getText().toString());
                        liter = Double.parseDouble(literEt.getText().toString());
                        total = price * liter;

                        total = Double.parseDouble(decimalFormat.format(total));
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

        literEt.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                if (literEt.getText().toString().isEmpty()) {
                    if (!priceEt.getText().toString().isEmpty() && !totalEt.getText().toString().isEmpty()) {
                        double price, liter, total;
                        price = Double.parseDouble(priceEt.getText().toString());
                        total = Double.parseDouble(totalEt.getText().toString());
                        liter = total / price;
                        liter = Double.parseDouble(decimalFormat.format(liter));
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
        });

        totalEt.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                if (totalEt.getText().toString().isEmpty()) {
                    if (!literEt.getText().toString().isEmpty() && !priceEt.getText().toString().isEmpty()) {
                        double price, liter, total;
                        liter = Double.parseDouble(literEt.getText().toString());
                        price = Double.parseDouble(priceEt.getText().toString());
                        total = price * liter;
                        total = Double.parseDouble(decimalFormat.format(total));
                        totalEt.setText(String.valueOf(total));
                    }
                } else {
                    if (literEt.getText().toString().isEmpty() && !priceEt.getText().toString().isEmpty()) {
                        double liter, price, total;
                        price = Double.parseDouble(priceEt.getText().toString());
                        total = Double.parseDouble(totalEt.getText().toString());
                        liter = total / price;
                        liter = Double.parseDouble(decimalFormat.format(liter));
                        literEt.setText(String.valueOf(liter));
                    } else if (!literEt.getText().toString().isEmpty() && priceEt.getText().toString().isEmpty()) {
                        double total, liter, price;
                        total = Double.parseDouble(totalEt.getText().toString());
                        liter = Double.parseDouble(literEt.getText().toString());
                        price = total / liter;
                        price = Double.parseDouble(decimalFormat.format(price));
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
        });


        fillTankSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                fillTankSwitch.setTextColor(ContextCompat.getColor(AddRefuelActivity.this, R.color.icon));
            else
                fillTankSwitch.setTextColor(ContextCompat.getColor(AddRefuelActivity.this, R.color.text_color));
        });

        vehicleSpinner.setOnItemClickListener((parent, view, position, id) -> {
            selectedVehicle = vehicleSpinner.getText().toString();
            if (!selectedVehicle.trim().isEmpty())
                selectedVehicleData = dbHelper.vehicleDao().getVehicleByRegNum(selectedVehicle);
            selectedVehiclePos = position;

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
        expenseData.setSynced(false);
        if (mode == 1)
            dbHelper.expenseDao().insert(expenseData);
        else
            dbHelper.expenseDao().update(expenseData);
        DBUtils.dbChanged(this, true);
        Toast.makeText(this, "Data Added", Toast.LENGTH_SHORT).show();
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void setData(ExpenseData data) {
        Button addBtn = findViewById(R.id.add_refuel_btn);
        addBtn.setText("Update");
        selectedDate = DateTimeUtils.getLocalDate(data.getTimestamp()).toString();
        selectedTime = DateTimeUtils.getTimeWithoutSeconds(data.getTimestamp());
        dateTv.setText(selectedDate);
        timeTv.setText(selectedTime);
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
        String desc, vno, eId = "";
        double price, total;
        long odometer;
        //Variables for refuel
        double liters;
        boolean isTankFilled;

        desc = descEt.getText().toString();
        vno = selectedVehicle;

        liters = Double.parseDouble(literEt.getText().toString());
        price = Double.parseDouble(priceEt.getText().toString());
        total = Double.parseDouble(totalEt.getText().toString());
        odometer = Long.parseLong(odometerEt.getText().toString());
        isTankFilled = fillTankSwitch.isChecked();

        long timestamp = DateTimeUtils.convertStringToMilliseconds(selectedDate, selectedTime);
        Log.i(TAG, "getData: " + timestamp);
        Log.i(TAG, "getData: " + selectedTime);
        Log.i(TAG, "getData: " + selectedDate);
        if (mode == 1) {
            eId = Util.generateId("refuel", vno);
        } else if (mode == 2) {
            eId = expenseData.geteId();
        }

        expenseData = new ExpenseData("Refuel", timestamp, desc, vno, eId, price, total, odometer, liters, isTankFilled, false);
    }

    @SuppressLint("SetTextI18n")
    private void setFuelSwitch() {
        if (!(literEt.getText().toString().isEmpty())) {
            double liter = Double.parseDouble(literEt.getText().toString());
            if (mode == 1) {
                if (!(selectedVehicle.equals("Select Vehicle") || selectedVehicle.isEmpty()) && selectedVehiclePos != -1) {
                    if (liter >= selectedVehicleData.getFuelCapacity()) {
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
                int tankCap = selectedVehicleData.getFuelCapacity();
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