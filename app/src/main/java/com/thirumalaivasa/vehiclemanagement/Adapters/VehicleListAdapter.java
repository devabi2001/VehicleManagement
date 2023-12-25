package com.thirumalaivasa.vehiclemanagement.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thirumalaivasa.vehiclemanagement.Dao.VehicleDao;
import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.R;
import com.thirumalaivasa.vehiclemanagement.Utils.Util;
import com.thirumalaivasa.vehiclemanagement.ViewVehicleActivity;

import java.util.List;

public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.VehicleListViewHolder> {

    Context context;
    List<String> vehicleList;

    VehicleDao vehicleDao;

    public VehicleListAdapter(Context context, List<String> vehicleList) {
        this.context = context;
        this.vehicleList = vehicleList;
        RoomDbHelper dbHelper = RoomDbHelper.getInstance(context);
        this.vehicleDao = dbHelper.vehicleDao();
    }

    @NonNull
    @Override
    public VehicleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VehicleListViewHolder vvh;
        View view = LayoutInflater.from(context).inflate(R.layout.vehicle_list_layout, parent, false);
        vvh = new VehicleListViewHolder(view);
        return vvh;
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleListViewHolder holder, int position) {

        int pos = position;
        String vehicleNumber = vehicleList.get(pos);
        String imagePath = vehicleDao.getImagePath(vehicleNumber);
        holder.licensePlate.setText(vehicleNumber);
        Glide.with(context)
                .load(imagePath)
                .circleCrop()
                .placeholder(R.drawable.car_24)
                .error(R.drawable.car_24)
                .into(holder.vehicleImg);

        holder.vehicleCard.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewVehicleActivity.class);
            intent.putExtra(Util.ID, vehicleNumber);
            context.startActivity(intent);

        });


    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }


    public static class VehicleListViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout vehicleCard;

        private final ImageView vehicleImg;

        private final TextView licensePlate;

        public VehicleListViewHolder(@NonNull View itemView) {
            super(itemView);

            vehicleCard = itemView.findViewById(R.id.vehicle_card_list);

            licensePlate = itemView.findViewById(R.id.vehicle_no_list);
            vehicleImg = itemView.findViewById(R.id.vehicle_img_list);
        }
    }


}

