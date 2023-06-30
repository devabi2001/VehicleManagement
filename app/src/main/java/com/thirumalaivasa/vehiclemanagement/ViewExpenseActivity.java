package com.thirumalaivasa.vehiclemanagement;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;


public class ViewExpenseActivity extends AppCompatActivity {
    //Refuel Card
    private TextView vehicleNoTv, expenseTypeTv, dateTv, timeTv, fuelType, totalCostRefuel, priceLtr, volume, odometerRefuel, tankFilled, percentOfTank, efficiencyTv, totalCostSalary, salaryType;
    //Service Card
    private TextView serviceType, totalCostService, priceService, serviceCharge, odometerService;

    private LinearLayout refuelCard, serviceCard, salaryCard;

    private ExpenseData expenseData;
    private double efficiency = 0;

    private final String TAG = "VehicleManagement";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);
        findViews();

        expenseData = new ExpenseData();
        expenseData = getIntent().getParcelableExtra("ExpenseData");

        if (expenseData.getExpenseType().equals("Refuel")) {
            efficiency = getIntent().getDoubleExtra("Efficiency", 0);
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

        setValues();

    }


    @SuppressLint("SetTextI18n")
    private void setValues() {
        if (expenseData.getExpenseType().equals("Refuel")) {

            vehicleNoTv.setText(expenseData.getVno());
            expenseTypeTv.setText(expenseData.getExpenseType());
            dateTv.setText(expenseData.getDate());
            timeTv.setText(expenseData.getTime());
            fuelType.setText(expenseData.getFuelType());
            totalCostRefuel.setText(String.valueOf(expenseData.getTotal()));
            priceLtr.setText(String.valueOf(expenseData.getPrice()));
            volume.setText(expenseData.getLiters() + " " + "ltr's");
            odometerRefuel.setText(expenseData.getOdometer() + " " + "Km");
            if (expenseData.isTankFilled())
                tankFilled.setText("Tank Filled: Yes");
            else
                tankFilled.setText("Tank Filled: No");

            percentOfTank.setText(expenseData.getPercentOfTank() + "% of tank filled");
            efficiencyTv.setText(efficiency + " Ltr's/Km");


        } else if (expenseData.getExpenseType().equals("Service") || expenseData.getExpenseType().equals("Other")) {
            vehicleNoTv.setText(expenseData.getVno());
            expenseTypeTv.setText(expenseData.getExpenseType());
            dateTv.setText(expenseData.getDate());
            timeTv.setText(expenseData.getTime());
            serviceType.setText(expenseData.getServiceType());
            totalCostService.setText(String.valueOf(expenseData.getTotal()));
            priceService.setText(String.valueOf(expenseData.getPrice()));
            serviceCharge.setText(String.valueOf(expenseData.getServiceCharge()));
            odometerService.setText(expenseData.getOdometer() + " " + "Km");
        } else {

            vehicleNoTv.setText(expenseData.getDriverName());
            dateTv.setText(expenseData.getDate());
            timeTv.setText(expenseData.getTime());
            salaryType.setText(expenseData.getSalaryType());
            totalCostSalary.setText(String.valueOf(expenseData.getTotal()));
        }
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
                if (expenseData.getExpenseType().equals("Refuel")) {
                    intent = new Intent(ViewExpenseActivity.this, AddRefuelActivity.class);

                } else if (expenseData.getExpenseType().equals("Service") || expenseData.getExpenseType().equals("Other")) {
                    intent = new Intent(ViewExpenseActivity.this, AddServiceActivity.class);
                } else if (expenseData.getExpenseType().equals("Salary") ) {
                    intent = new Intent(ViewExpenseActivity.this, AddSalaryActivity.class);
                }

                intent.putExtra("ExpenseData", expenseData);
                intent.putExtra("Mode", 2);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void deleteData() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getUid();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ViewExpenseActivity.this);
        alertBuilder.setTitle("Delete?")
                .setCancelable(true)
                .setMessage("Are sure? want to delete")
                .setPositiveButton("Yes", (dialogInterface, i) -> database.collection("Data").document(uid).collection("Expense")
                        .document(expenseData.geteId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ViewExpenseActivity.this, "Data Deleted", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ViewExpenseActivity.this, "Can't delete try again later", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        })).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
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

        fuelType = findViewById(R.id.fuel_type);
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