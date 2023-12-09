package com.thirumalaivasa.vehiclemanagement;

import static com.thirumalaivasa.vehiclemanagement.Utils.Util.TAG;

import android.annotation.SuppressLint;

import android.content.Intent;

import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.thirumalaivasa.vehiclemanagement.Dao.ExpenseDao;
import com.thirumalaivasa.vehiclemanagement.Helpers.FirebaseHelper;
import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Utils.DBUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.DateTimeUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.Util;

import java.time.LocalDateTime;
import java.util.List;

public class ViewExpenseActivity extends AppCompatActivity {
    //Refuel Card
    private TextView vehicleNoTv, expenseTypeTv, dateTv, timeTv, fuelTypeEt, totalCostRefuel, priceLtr, volume, odometerRefuel, tankFilled, percentOfTank, efficiencyTv, totalCostSalary, salaryType;
    //Service Card
    private TextView serviceType, totalCostService, priceService, serviceCharge, odometerService;

    private LinearLayout refuelCard, serviceCard, salaryCard;

    private ExpenseData expenseData;
    private double efficiency = 0.0;

    private ExpenseDao expenseDao;

    private RoomDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);
        findViews();
        String id = getIntent().getStringExtra("ID");

        dbHelper = RoomDbHelper.getInstance(ViewExpenseActivity.this);
        expenseDao = dbHelper.expenseDao();
        expenseData = expenseDao.getExpenseById(id);

        if (expenseData == null)
            finish();

        if (expenseData.getExpenseType().equals("Refuel")) {
            refuelCard.setVisibility(View.VISIBLE);
            serviceCard.setVisibility(View.GONE);
            salaryCard.setVisibility(View.GONE);
        } else if (expenseData.getExpenseType().equals("Service")) {
            refuelCard.setVisibility(View.GONE);
            serviceCard.setVisibility(View.VISIBLE);
            salaryCard.setVisibility(View.GONE);
        } else {
            refuelCard.setVisibility(View.GONE);
            serviceCard.setVisibility(View.GONE);
            salaryCard.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalDateTime localDateTime = DateTimeUtils.getLocalDateTime(expenseData.getTimestamp());
        String time = localDateTime.getHour() + ":" + localDateTime.getMinute();
        String date = localDateTime.getDayOfMonth() + "-" + localDateTime.getMonthValue() + "-" + localDateTime.getYear();
        if (expenseData.getExpenseType().equals("Refuel"))
            if (expenseData.isTankFilled() && expenseData.getOdometer() != 0)
                efficiency = calculateEfficiency();

        setValues(date, time);

    }


    @SuppressLint("SetTextI18n")
    private void setValues(String date, String time) {
        if (expenseData.getExpenseType().equals("Refuel")) {
            Cursor fuelDetails = dbHelper.vehicleDao().getFuelDetails(expenseData.getVno());
            fuelDetails.moveToFirst();
            int fuelCap = Integer.parseInt(fuelDetails.getString(0));
            String fuelType = fuelDetails.getString(1);
            vehicleNoTv.setText(expenseData.getVno());
            expenseTypeTv.setText(expenseData.getExpenseType());
            dateTv.setText(date);
            timeTv.setText(time);
            fuelTypeEt.setText(fuelType);
            totalCostRefuel.setText(String.valueOf(expenseData.getTotal()));
            priceLtr.setText(String.valueOf(expenseData.getPrice()));
            volume.setText(expenseData.getLiters() + " " + "ltr's");
            odometerRefuel.setText(expenseData.getOdometer() + " " + "Km");
            if (expenseData.isTankFilled())
                tankFilled.setText("Tank Filled: Yes");
            else
                tankFilled.setText("Tank Filled: No");
            double percent = (expenseData.getLiters() / fuelCap) * 100;
            percentOfTank.setText(percent + "% of tank filled");
            efficiencyTv.setText(efficiency + " Ltr's/Km");


        } else if (expenseData.getExpenseType().equals("Service") || expenseData.getExpenseType().equals("Other")) {
            vehicleNoTv.setText(expenseData.getVno());
            expenseTypeTv.setText(expenseData.getExpenseType());
            dateTv.setText(date);
            timeTv.setText(time);
            serviceType.setText(expenseData.getServiceType());
            totalCostService.setText(String.valueOf(expenseData.getTotal()));
            priceService.setText(String.valueOf(expenseData.getPrice()));
            serviceCharge.setText(String.valueOf(expenseData.getServiceCharge()));
            odometerService.setText(expenseData.getOdometer() + " " + "Km");
        } else {

            vehicleNoTv.setText(expenseData.getDriverName());
            dateTv.setText(date);
            timeTv.setText(time);
            salaryType.setText(expenseData.getSalaryType());
            totalCostSalary.setText(String.valueOf(expenseData.getTotal()));
        }
    }

    private double calculateEfficiency() {
        double retValue = 0.0;
        String vno = expenseData.getVno();
        String type = "Refuel";
        long timestamp = expenseData.getTimestamp();
        int limit = 1;
        long currentOdometer = expenseData.getOdometer();
        List<ExpenseData> expenseBelowList = expenseDao.getExpenseBelow(vno, type, timestamp, limit);
        if (expenseBelowList != null && expenseBelowList.size() > 0) {
            ExpenseData expenseBelow = expenseBelowList.get(0);
            boolean isBelowFull = expenseBelow.isTankFilled();
            long belowOdometer = expenseBelow.getOdometer();
            if (isBelowFull && expenseBelow.getOdometer() != 0 && currentOdometer < belowOdometer) {
                long drivenDistance = currentOdometer - belowOdometer;
                double consumedFuel = expenseData.getLiters();
                if (consumedFuel > 0.0)
                    retValue = drivenDistance / consumedFuel;
                return retValue;
            }
        }

        return retValue;
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn_view_exp:
                finish();
                break;
            case R.id.delete_btn:
                deleteData();
                break;
            case R.id.edit_btn:
                Intent intent = null;
                switch (expenseData.getExpenseType()) {
                    case "Refuel":
                        intent = new Intent(ViewExpenseActivity.this, AddRefuelActivity.class);
                        break;
                    case "Service":
                    case "Other":
                        intent = new Intent(ViewExpenseActivity.this, AddServiceActivity.class);
                        break;
                    case "Salary":
                        intent = new Intent(ViewExpenseActivity.this, AddSalaryActivity.class);
                        break;
                }

                if (intent != null) {
                    intent.putExtra("Mode", 2);
                    intent.putExtra(Util.ID, expenseData.geteId());
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    private void deleteData() {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ViewExpenseActivity.this);
        alertBuilder.setTitle("Delete?")
                .setCancelable(true)
                .setMessage("Are sure? want to delete")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    dbHelper.expenseDao().delete(expenseData);
                    if (Util.checkNetwork(getSystemService(ConnectivityManager.class))) {
                        String uid = FirebaseAuth.getInstance().getUid();
                        if (uid == null) {
                            DBUtils.addDeletedData(ViewExpenseActivity.this, Util.EXPENSE, expenseData.eId);
                            return;
                        }
                        FirebaseHelper firebaseHelper = new FirebaseHelper();
                        CollectionReference collection = FirebaseFirestore.getInstance().collection("Data").document(uid).collection("Expense");
                        Task<Boolean> booleanTask = firebaseHelper.deleteDocumentById(collection, expenseData.eId);
                        if (!booleanTask.isSuccessful())
                            DBUtils.addDeletedData(ViewExpenseActivity.this, "Expense", expenseData.eId);
                    } else {
                        DBUtils.addDeletedData(ViewExpenseActivity.this, "Expense", expenseData.eId);
                    }
                    finish();
                }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
        alertBuilder.show();

    }

    private void findViews() {
        vehicleNoTv = findViewById(R.id.vehicle_no_expense);
        expenseTypeTv = findViewById(R.id.expense_type_expense);
        dateTv = findViewById(R.id.date_expense);
        timeTv = findViewById(R.id.time_expense);

        refuelCard = findViewById(R.id.refuel_layout);
        serviceCard = findViewById(R.id.service_layout);
        salaryCard = findViewById(R.id.salary_layout);

        fuelTypeEt = findViewById(R.id.fuel_type);
        totalCostRefuel = findViewById(R.id.total_cost_refuel);
        priceLtr = findViewById(R.id.price_ltr_refuel);
        volume = findViewById(R.id.volume_refuel);
        odometerRefuel = findViewById(R.id.odometer_refuel);
        tankFilled = findViewById(R.id.tank_filled);
        percentOfTank = findViewById(R.id.percent_of_tank);
        efficiencyTv = findViewById(R.id.efficiency_vehicle_view);

        serviceType = findViewById(R.id.service_type);
        totalCostService = findViewById(R.id.total_cost_service);
        priceService = findViewById(R.id.price_service);
        serviceCharge = findViewById(R.id.service_charge);
        odometerService = findViewById(R.id.odometer_service);

        totalCostSalary = findViewById(R.id.total_cost_salary);
        salaryType = findViewById(R.id.salary_type);


    }
}