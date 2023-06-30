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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.R;
import com.thirumalaivasa.vehiclemanagement.ViewExpenseActivity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ExpenseListViewHolder> {

    Context context;
    ArrayList<ExpenseData> expenseDataArrayList;
    private final String TAG = "VehicleManagement";

    String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public ExpenseListAdapter(Context context, ArrayList<ExpenseData> expenseDataArrayList) {
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
        Date date = null;
        String dateValue = "";
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").parse(expenseDataArrayList.get(position).getDate());
            assert date != null;
            dateValue = date.getDate() + " " + month[date.getMonth()];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.litersImg.setVisibility(View.VISIBLE);
        holder.litersImg.setVisibility(View.VISIBLE);
        holder.priceLayout.setVisibility(View.VISIBLE);

        switch (expenseDataArrayList.get(position).getExpenseType()) {
            case "Refuel":
                Glide.with(context).load(R.drawable.refuel).into(holder.serviceTypeImg);
                holder.serviceTypeTv.setText("Refuel");
                holder.litersTv.setText(expenseDataArrayList.get(position).getLiters() + " " + "ltr");
                holder.litersImg.setImageResource(R.drawable.drop_24);
                holder.priceTv.setText(expenseDataArrayList.get(position).getPrice() + " " + "/ltr");
                holder.odometerTv.setText(expenseDataArrayList.get(position).getOdometer() + " " + "Km");
                holder.totalTv.setText(String.valueOf(expenseDataArrayList.get(position).getTotal()));
                holder.vehicleNoTv.setText(String.valueOf(expenseDataArrayList.get(position).getVno()));
                break;

            case "Service":

                Glide.with(context).load(R.drawable.repair).into(holder.serviceTypeImg);
                holder.serviceTypeTv.setText("Service");
                holder.litersTv.setText(String.valueOf(expenseDataArrayList.get(position).getServiceCharge()));
                holder.litersImg.setImageResource(R.drawable.cash_24);
                holder.priceTv.setText(expenseDataArrayList.get(position).getPrice() + "");
                holder.odometerTv.setText(expenseDataArrayList.get(position).getOdometer() + " " + "Km");
                holder.totalTv.setText(String.valueOf(expenseDataArrayList.get(position).getTotal()));
                holder.vehicleNoTv.setText(String.valueOf(expenseDataArrayList.get(position).getVno()));
                break;
            case "Other":

                Glide.with(context).load(R.drawable.expense).into(holder.serviceTypeImg);
                holder.serviceTypeTv.setText("Other");
                holder.litersTv.setText(String.valueOf(expenseDataArrayList.get(position).getServiceCharge()));
                holder.litersImg.setImageResource(R.drawable.cash_24);
                holder.priceTv.setText(expenseDataArrayList.get(position).getPrice() + "");
                holder.odometerTv.setText(expenseDataArrayList.get(position).getOdometer() + " " + "Km");
                holder.totalTv.setText(String.valueOf(expenseDataArrayList.get(position).getTotal()));
                holder.vehicleNoTv.setText(String.valueOf(expenseDataArrayList.get(position).getVno()));
                break;

            case "Salary":
                Glide.with(context).load(R.drawable.cash_48).into(holder.serviceTypeImg);
                holder.serviceTypeTv.setText("Salary");
                holder.odometerImg.setVisibility(View.INVISIBLE);
                holder.litersImg.setVisibility(View.INVISIBLE);
                holder.vehicleNoTv.setText(expenseDataArrayList.get(position).getDriverName());
                holder.totalTv.setText(String.valueOf(expenseDataArrayList.get(position).getTotal()));
                holder.priceLayout.setVisibility(View.INVISIBLE);
                break;
        }


        holder.dateTv.setText(dateValue);

        holder.expenseCard.setOnClickListener(view -> {
            double efficiency = 0;
            if (expenseDataArrayList.get(position).getExpenseType().equals("Refuel")) {
                if (expenseDataArrayList.get(position).isTankFilled()) {
                    int size = expenseDataArrayList.size();

                    if (position >= 0 && position != size - 1) {
                        for (int i = position + 1; i >= 0; i--) {
                            if (expenseDataArrayList.get(i).getExpenseType().equals("Refuel") && expenseDataArrayList.get(i).getVno().equals(expenseDataArrayList.get(position).getVno())) {
                                if (expenseDataArrayList.get(i).isTankFilled() && expenseDataArrayList.get(position).isTankFilled()) {
                                    efficiency = calculateEfficiency(expenseDataArrayList.get(i), expenseDataArrayList.get(position));
                                    break;
                                } else {
                                    efficiency = 0;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            Intent intent = new Intent(context, ViewExpenseActivity.class);
            intent.putExtra("ExpenseData", expenseDataArrayList.get(position));
            intent.putExtra("Efficiency", efficiency);
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

    private double calculateEfficiency(ExpenseData data1, ExpenseData data2) {

        //Temproary code for screenshot
        switch (data1.getVno()) {
            case "TN 02 L 5157":
                return 8.40;
            case "TN 22 BP 0950":
                return 10.50;
            case "TN 22 AQ 9334":
                return 11.00;
            case "TN 12 AF 8816":
                return 53.70;
            case "TN 12 AX 1075":
                return 50;
            default:
                return 0.0;
        }

//
//        double retValue;
//
//        long prevKm = data1.getOdometer();
//        long currentKm = data2.getOdometer();
//        double prevLtr = data1.getLiters();
//        double currentLtr = data2.getLiters();
//
//        long drivenKm;
//        double consumedLtr;
//
//        drivenKm = currentKm - prevKm;
//        consumedLtr = currentLtr - prevLtr;
//
//        if (consumedLtr == 0)
//            retValue = drivenKm / currentLtr;
//        else
//            retValue = drivenKm / consumedLtr;
//
//        DecimalFormat format = new DecimalFormat("#.00");
//        retValue = Double.parseDouble(format.format(retValue));
//        return retValue;

    }

}
