package com.thirumalaivasa.vehiclemanagement;

import static com.thirumalaivasa.vehiclemanagement.Utils.Util.TAG;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Utils.DBUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.DateTimeUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.PickerUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.Util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddSalaryActivity extends AppCompatActivity {

    private AutoCompleteTextView driverSpinnerATV, salaryTypeSpinnerATV;
    private TextView dateTv, timeTv;
    private EditText amountEt, descEt;
    private ProgressBar progressBar;

    private ExpenseData expenseData;
    private String selectedDate = "", selectedTime = "";

    private int mode = -1;
    private RoomDbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_salary);

        findViews();
        dbHelper = RoomDbHelper.getInstance(this);
        mode = getIntent().getIntExtra("Mode", -1);
        if (mode == -1)
            finish();
        if (mode > 2)
            mode = 1;

        setSalarySpinner(null);

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mode == 1) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item);
            arrayAdapter.add("Select Driver");
            driverSpinnerATV.setText(arrayAdapter.getItem(0));
            List<String> driversNameList = dbHelper.driverDao().getDriversName();
            arrayAdapter.addAll(driversNameList);

            arrayAdapter.notifyDataSetChanged();
            arrayAdapter.remove("Select Driver");
            driverSpinnerATV.setAdapter(arrayAdapter);
            String[] date_time = DateTimeUtils.getCurrentDateTime();
            selectedDate = date_time[0];
            selectedTime = date_time[1];
            dateTv.setText(selectedDate);
            timeTv.setText(selectedTime);


        } else if (mode == 2) {
            progressBar.setVisibility(View.VISIBLE);
            expenseData = new ExpenseData();
            expenseData = getIntent().getParcelableExtra("ExpenseData");
            driverSpinnerATV.setText(expenseData.getDriverName());
            setData(expenseData);
            progressBar.setVisibility(View.GONE);
        }


        dateTv.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (mode == 2) {
                calendar = DateTimeUtils.convertTimestampToCalendar(expenseData.getTimestamp());
            }
            PickerUtils.showDatePicker(AddSalaryActivity.this, ((year, month, day) -> {
                String selectedDate = Util.getDisplayDate(year, month, day);
                dateTv.setText(selectedDate);

            }), calendar);

        });

        timeTv.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (mode == 2) {
                calendar = DateTimeUtils.convertTimestampToCalendar(expenseData.getTimestamp());
            }
            PickerUtils.showTimePicker(AddSalaryActivity.this, ((hour, min) -> {
                selectedTime = Util.getDisplayTime(hour, min);
                timeTv.setText(selectedTime);

            }), calendar);
        });


    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.add_salary_btn:
                if (verifyData()) {
                    addData();
                }
                break;
        }

    }


    private boolean verifyData() {
        boolean retValue = true;

        if (driverSpinnerATV.getText().toString().equals("Select Driver")) {
            Toast.makeText(this, "Select driver", Toast.LENGTH_SHORT).show();
            driverSpinnerATV.performClick();
            retValue = false;
        }
        if (amountEt.getText().toString().isEmpty()) {
            amountEt.setError("Enter Amount");
            retValue = false;
        }

        return retValue;
    }

    private void addData() {
        progressBar.setVisibility(View.VISIBLE);
        getData();
        expenseData.setSynced(false);
        dbHelper.expenseDao().insert(expenseData);
        DBUtils.dbChanged(this, true);
        Toast.makeText(this, "Data Added", Toast.LENGTH_SHORT).show();
        finish();
        progressBar.setVisibility(View.GONE);
    }

    private void getData() {
        String driverName, salaryType, desc, eid;
        double paidAmt;

        driverName = driverSpinnerATV.getText().toString();
        salaryType = salaryTypeSpinnerATV.getText().toString();
        paidAmt = Double.parseDouble(amountEt.getText().toString());
        desc = descEt.getText().toString();
        dateTv.getText().toString();
        timeTv.getText().toString();
        if (mode == 1) {
            eid = Util.generateId("Salary", driverName);
        } else {
            eid = expenseData.geteId();
        }
        long timestamp = DateTimeUtils.convertStringToMilliseconds(selectedDate, selectedTime);
        expenseData = new ExpenseData("Salary", timestamp, desc, eid, driverName, salaryType, paidAmt, false);


    }

    private void setData(ExpenseData data) {

        setSalarySpinner(data.getSalaryType());

        Button addBtn = findViewById(R.id.add_salary_btn);
        addBtn.setText("Update");
        amountEt.setText(String.valueOf(data.getTotal()));
        descEt.setText(data.getDesc());

        selectedDate = DateTimeUtils.getLocalDate(data.getTimestamp()).toString();
        selectedTime = DateTimeUtils.getTimeWithoutSeconds(data.getTimestamp());
        dateTv.setText(selectedDate);
        timeTv.setText(selectedTime);
    }


    private void setSalarySpinner(String type) {
        String[] stringArray = getResources().getStringArray(R.array.salary_type);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item);
        arrayAdapter.addAll(stringArray);
        arrayAdapter.notifyDataSetChanged();
        int pos = 0;
        if (type != null) {
            for (String value : stringArray) {
                if (type.equals(value)) {
                    break;
                }
                pos++;
            }
        }

        salaryTypeSpinnerATV.setText(arrayAdapter.getItem(pos));
        salaryTypeSpinnerATV.setAdapter(arrayAdapter);

    }


    private void findViews() {

        driverSpinnerATV = findViewById(R.id.driver_list_spinner);
        salaryTypeSpinnerATV = findViewById(R.id.salary_type_spinner);

        dateTv = findViewById(R.id.date_salary);
        timeTv = findViewById(R.id.time_salary);

        descEt = findViewById(R.id.desc_et);
        amountEt = findViewById(R.id.amount_et);

        progressBar = findViewById(R.id.progress_add_salary);


    }

}