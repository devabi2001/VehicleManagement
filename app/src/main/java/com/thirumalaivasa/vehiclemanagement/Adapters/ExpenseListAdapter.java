package com.thirumalaivasa.vehiclemanagement.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.R;
import com.thirumalaivasa.vehiclemanagement.Utils.DateTimeUtils;
import com.thirumalaivasa.vehiclemanagement.Utils.Util;
import com.thirumalaivasa.vehiclemanagement.ViewExpenseActivity;

import java.time.LocalDateTime;
import java.util.List;


public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ExpenseListViewHolder> {

    Context context;
    List<ExpenseData> expenseDataArrayList;
    private final String TAG = "VehicleManagement";

    String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public ExpenseListAdapter(Context context, List<ExpenseData> expenseDataArrayList) {
        this.context = context;
        this.expenseDataArrayList = expenseDataArrayList;
    }


    @NonNull
    @Override
    public ExpenseListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ExpenseListViewHolder evh;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_list_layout, parent, false);
        evh = new ExpenseListViewHolder(view);
        return evh;
    }


    @SuppressLint({"ResourceAsColor", "SimpleDateFormat", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ExpenseListViewHolder holder, int position) {
        ExpenseData expenseData = expenseDataArrayList.get(position);
        String dateValue = "";

        LocalDateTime dateTime = DateTimeUtils.getLocalDateTime(expenseData.getTimestamp());
        dateValue = dateTime.getDayOfMonth() + " " + DateTimeUtils.getMonth(dateTime);


        holder.litersImg.setVisibility(View.VISIBLE);
        holder.litersImg.setVisibility(View.VISIBLE);
        holder.priceLayout.setVisibility(View.VISIBLE);

        switch (expenseData.getExpenseType()) {
            case "Refuel":
                Glide.with(context).load(R.drawable.refuel).into(holder.serviceTypeImg);
                holder.serviceTypeTv.setText("Refuel");
                holder.litersTv.setText(expenseData.getLiters() + " " + "ltr");
                holder.litersImg.setImageResource(R.drawable.drop_24);
                holder.priceTv.setText(expenseData.getPrice() + " " + "/ltr");
                holder.odometerTv.setText(expenseData.getOdometer() + " " + "Km");
                holder.totalTv.setText(String.valueOf(expenseData.getTotal()));
                holder.vehicleNoTv.setText(String.valueOf(expenseData.getVno()));
                break;

            case "Service":

                Glide.with(context).load(R.drawable.repair).into(holder.serviceTypeImg);
                holder.serviceTypeTv.setText("Service");
                holder.litersTv.setText(String.valueOf(expenseData.getServiceCharge()));
                holder.litersImg.setImageResource(R.drawable.cash_24);
                holder.priceTv.setText(expenseData.getPrice() + "");
                holder.odometerTv.setText(expenseData.getOdometer() + " " + "Km");
                holder.totalTv.setText(String.valueOf(expenseData.getTotal()));
                holder.vehicleNoTv.setText(String.valueOf(expenseData.getVno()));
                break;
            case "Other":

                Glide.with(context).load(R.drawable.expense).into(holder.serviceTypeImg);
                holder.serviceTypeTv.setText("Other");
                holder.litersTv.setText(String.valueOf(expenseData.getServiceCharge()));
                holder.litersImg.setImageResource(R.drawable.cash_24);
                holder.priceTv.setText(expenseData.getPrice() + "");
                holder.odometerTv.setText(expenseData.getOdometer() + " " + "Km");
                holder.totalTv.setText(String.valueOf(expenseData.getTotal()));
                holder.vehicleNoTv.setText(String.valueOf(expenseData.getVno()));
                break;

            case "Salary":
                Glide.with(context).load(R.drawable.cash_48).into(holder.serviceTypeImg);
                holder.serviceTypeTv.setText("Salary");
                holder.odometerImg.setVisibility(View.INVISIBLE);
                holder.litersImg.setVisibility(View.INVISIBLE);
                holder.vehicleNoTv.setText(expenseData.getDriverName());
                holder.totalTv.setText(String.valueOf(expenseData.getTotal()));
                holder.priceLayout.setVisibility(View.INVISIBLE);
                break;
        }


        holder.dateTv.setText(dateValue);

        holder.expenseCard.setOnClickListener(view -> {

            Intent intent = new Intent(context, ViewExpenseActivity.class);
            intent.putExtra(Util.ID, expenseData.geteId());
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return expenseDataArrayList.size();
    }

    public static class ExpenseListViewHolder extends RecyclerView.ViewHolder {

        TextView serviceTypeTv, vehicleNoTv, dateTv, odometerTv, totalTv, litersTv, priceTv;
        ImageView litersImg, serviceTypeImg, odometerImg;

        LinearLayout expenseCard;

        LinearLayout priceLayout;

        public ExpenseListViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceTypeTv = itemView.findViewById(R.id.service_type_title);
            vehicleNoTv = itemView.findViewById(R.id.vehicle_no_list);
            dateTv = itemView.findViewById(R.id.date_list);
            odometerTv = itemView.findViewById(R.id.odometer_list);
            totalTv = itemView.findViewById(R.id.total_list);
            litersTv = itemView.findViewById(R.id.liters_list);
            priceTv = itemView.findViewById(R.id.price_list);


            expenseCard = itemView.findViewById(R.id.expense_card);

            serviceTypeImg = itemView.findViewById(R.id.service_type_img);
            litersImg = itemView.findViewById(R.id.liters_img);
            odometerImg = itemView.findViewById(R.id.odometer_img);

            priceLayout = itemView.findViewById(R.id.price_layout);
        }
    }

}
