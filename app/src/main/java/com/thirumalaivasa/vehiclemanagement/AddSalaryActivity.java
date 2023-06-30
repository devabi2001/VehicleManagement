package com.thirumalaivasa.vehiclemanagement;

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
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class AddSalaryActivity extends AppCompatActivity {

//    Spinner driverSpinner, salaryTypeSpinner;

    private AutoCompleteTextView driverSpinnerATV,salaryTypeSpinnerATV;
    private TextView dateTv, amountTv, descTv, timeTv;
    private EditText amountEt, descEt;
    private ProgressBar progressBar;

    private ExpenseData expenseData, previousData;


    private final String TAG = "VehicleManagement";

    private String selectedDate = "", selectedTime = "";
    private int selectedDay, selectedMonth, selectedYear, selectedHour, selectedMin;

    private int mode = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_salary);

        findViews();
        ArrayList<DriverData> driverDataList = getIntent().getParcelableArrayListExtra("DriverData");
        mode = getIntent().getIntExtra("Mode", -1);
        setSalarySpinner(null);
        if (mode == 1) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item);
            arrayAdapter.add("Select Driver");
            driverSpinnerATV.setText(arrayAdapter.getItem(0));
            for (DriverData data : driverDataList) {
                arrayAdapter.add(data.getDriverName());
            }
            arrayAdapter.notifyDataSetChanged();
            arrayAdapter.remove("Select Driver");
            driverSpinnerATV.setAdapter(arrayAdapter);


            final Calendar c = Calendar.getInstance();

            // on below line we are getting
            // our day, month and year.
            selectedYear = c.get(Calendar.YEAR);
            selectedMonth = c.get(Calendar.MONTH);
            selectedDay = c.get(Calendar.DAY_OF_MONTH);
            // on below line we are getting our hour, minute.
            selectedHour = c.get(Calendar.HOUR_OF_DAY);
            selectedMin = c.get(Calendar.MINUTE);

            selectedDate = selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear;
            selectedTime = selectedHour + ":" + selectedMin;
            dateTv.setText(selectedDate);
            timeTv.setText(selectedTime);
            String[] date_time = getDateAndTime();
            dateTv.setText(date_time[0]);
            timeTv.setText(date_time[1]);


        } else if (mode == 2) {
            previousData = new ExpenseData();
            previousData = getIntent().getParcelableExtra("ExpenseData");
//            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item);
//            arrayAdapter.add(previousData.getDriverName());
//            arrayAdapter.notifyDataSetChanged();
//            driverSpinnerATV.setAdapter(arrayAdapter);
            driverSpinnerATV.setText(previousData.getDriverName());
            setData();


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
    protected void onResume() {
        super.onResume();


        dateTv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddSalaryActivity.this,
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddSalaryActivity.this,
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


        amountEt.setOnFocusChangeListener((view, b) -> {
            if (b) {
                amountTv.setVisibility(View.VISIBLE);
                amountEt.setHint("");
            } else if (amountEt.getText().toString().isEmpty()) {
                amountTv.setVisibility(View.INVISIBLE);
                amountEt.setHint("Amount");
            } else
                amountTv.setVisibility(View.VISIBLE);
        });

        descEt.setOnFocusChangeListener((view, b) -> {
            if (b) {
                descTv.setVisibility(View.VISIBLE);
                descEt.setHint("");
            } else if (amountEt.getText().toString().isEmpty()) {
                descTv.setVisibility(View.INVISIBLE);
                descEt.setHint("Description");
            } else
                descTv.setVisibility(View.VISIBLE);
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
        getData();
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        if (uid != null)
            database.collection("Data").document(uid)
                    .collection("Expense").document(expenseData.geteId())
                    .set(expenseData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddSalaryActivity.this, "Data Added", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.e(TAG, "Add Salary Data", task.getException());
                            Toast.makeText(AddSalaryActivity.this, "Error!!! Try again later ", Toast.LENGTH_SHORT).show();
                        }
                    });
    }

    private void getData() {
        expenseData = new ExpenseData();
        String driverName, salaryType, desc, date, time, eid;
        double paidAmt;

        driverName = driverSpinnerATV.getText().toString();
        salaryType = salaryTypeSpinnerATV.getText().toString();
        paidAmt = Double.parseDouble(amountEt.getText().toString());
        desc = descEt.getText().toString();
        date = dateTv.getText().toString();
        time = timeTv.getText().toString();
        String[] dateAndTime = getDateAndTime();
        if (mode == 1) {

            eid = generateId(dateAndTime[0], dateAndTime[1], driverName);

        } else {
            eid = previousData.geteId();

        }
        expenseData = new ExpenseData("Salary", date, time, desc, eid, driverName, salaryType, paidAmt);


    }

    private void setData() {

        setSalarySpinner(previousData.getSalaryType());


        Button addBtn = findViewById(R.id.add_salary_btn);
        addBtn.setText("Update");
        amountTv.setVisibility(View.VISIBLE);
        descTv.setVisibility(View.VISIBLE);
        amountEt.setText(String.valueOf(previousData.getTotal()));
        descEt.setText(previousData.getDesc());

//        salaryTypeSpinnerATV.setText(position);
//        call setSalarySpinner()
        dateTv.setText(previousData.getDate());
        timeTv.setText(previousData.getTime());
    }


    private void setSalarySpinner(String previousType){
        String[] stringArray = getResources().getStringArray(R.array.salary_type);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item);
        if(previousData!=null)
            arrayAdapter.add(previousType);
        for (String value : stringArray) {

            if(!(value.equals(previousType))) {
                arrayAdapter.add(value);
            }
        }

        arrayAdapter.notifyDataSetChanged();

        salaryTypeSpinnerATV.setText(arrayAdapter.getItem(0));
        salaryTypeSpinnerATV.setAdapter(arrayAdapter);

    }

    private String generateId(String date, String time, String driverName) {
        String retValue = "";
        String d = date.replace("-", "");
        String t = time.replace(":", "");
        String dN = driverName.replace(" ", "");
        retValue = dN + "salary" + d + t;
        return retValue;
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


    private void findViews() {

        driverSpinnerATV = findViewById(R.id.driver_list_spinner);
        salaryTypeSpinnerATV = findViewById(R.id.salary_type_spinner);

        dateTv = findViewById(R.id.date_salary);
        timeTv = findViewById(R.id.time_salary);
        amountTv = findViewById(R.id.amount_tv);
        descTv = findViewById(R.id.desc_tv);


        descEt = findViewById(R.id.desc_et);
        amountEt = findViewById(R.id.amount_et);

        progressBar = findViewById(R.id.progress_add_salary);


    }

}