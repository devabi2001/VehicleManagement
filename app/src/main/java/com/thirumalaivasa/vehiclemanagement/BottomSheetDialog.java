package com.thirumalaivasa.vehiclemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private ImageView addVehicle, refuelBtn, serviceBtn, expenseBtn, addDriver, salary;
    private FloatingActionButton closeBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
        findViews(v);

        addVehicle.setOnClickListener(view -> {
            Intent intent = new Intent(v.getContext(), AddVehicleActivity.class);
            intent.putExtra("Mode", 1);
            dismiss();
            startActivity(intent);
        });
        refuelBtn.setOnClickListener(view -> {
            Intent intent = new Intent(v.getContext(), AddRefuelActivity.class);
            intent.putExtra("Mode", 1);
            dismiss();
            startActivity(intent);
        });
        serviceBtn.setOnClickListener(view -> {
            Intent intent = new Intent(v.getContext(), AddServiceActivity.class);
            intent.putExtra("Mode", 1);
            intent.putExtra("ExpenseType", "Service");
            dismiss();
            startActivity(intent);
        });
        expenseBtn.setOnClickListener(view -> {
            Intent intent = new Intent(v.getContext(), AddServiceActivity.class);
            intent.putExtra("Mode", 1);
            intent.putExtra("ExpenseType", "Other");
            dismiss();
            startActivity(intent);
        });

        addDriver.setOnClickListener(view -> {
            Intent intent = new Intent(v.getContext(),AddDriverActivity.class);
            intent.putExtra("Mode",1);
            dismiss();
            startActivity(intent);
        });

        salary.setOnClickListener(view -> {
            Intent intent = new Intent(v.getContext(),AddSalaryActivity.class);
            intent.putExtra("Mode", 1);
            dismiss();
            startActivity(intent);
        });


        closeBtn.setOnClickListener(view -> dismiss());
        return v;
    }

    private void findViews(View v) {
        addVehicle = v.findViewById(R.id.add_vehicle);
        refuelBtn = v.findViewById(R.id.add_refuel);
        serviceBtn = v.findViewById(R.id.add_service);
        expenseBtn = v.findViewById(R.id.add_expense);
        addDriver = v.findViewById(R.id.add_driver);
        salary = v.findViewById(R.id.salary);

        closeBtn = v.findViewById(R.id.close_btm_btn);
    }


}
