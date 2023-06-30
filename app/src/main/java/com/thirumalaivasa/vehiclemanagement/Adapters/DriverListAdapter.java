package com.thirumalaivasa.vehiclemanagement.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;
import com.thirumalaivasa.vehiclemanagement.Models.ImageData;
import com.thirumalaivasa.vehiclemanagement.R;
import com.thirumalaivasa.vehiclemanagement.ViewDriverActivity;

import java.util.ArrayList;

public class DriverListAdapter extends RecyclerView.Adapter<DriverListAdapter.DriverListViewHolder>{

    ArrayList<DriverData> driverDataArrayList;
    Context context;
    private final String TAG = "VehicleManagement";
    public DriverListAdapter(ArrayList<DriverData> driverDataArrayList, Context context) {
        this.driverDataArrayList = driverDataArrayList;
        this.context = context;

    }

    @NonNull
    @Override
    public DriverListAdapter.DriverListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DriverListAdapter.DriverListViewHolder dvh;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_list_layout, parent, false);
        dvh = new DriverListAdapter.DriverListViewHolder(view);
        return dvh;
    }

    @Override
    public void onBindViewHolder(@NonNull DriverListAdapter.DriverListViewHolder holder, int position) {
        int pos = position;

        holder.driverName.setText(driverDataArrayList.get(pos).getDriverName());
        String driverId = driverDataArrayList.get(pos).getDriverId();
        if(ImageData.getImage(driverId) !=null){
            Glide.with(context)
                    .load(ImageData.getImage(driverId))
                    .circleCrop()
                    .into(holder.driverImg);
        }

        holder.callBtn.setOnClickListener(view -> {
            Toast.makeText(context, "Making a call", Toast.LENGTH_SHORT).show();
            String phone = driverDataArrayList.get(pos).getContact();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            context.startActivity(intent);
        });

        holder.driverCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewDriverActivity.class);
                intent.putExtra("DriverData",driverDataArrayList.get(pos));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return driverDataArrayList.size();
    }


    public static class DriverListViewHolder extends RecyclerView.ViewHolder {

        TextView driverName;
        ImageView driverImg,callBtn;
        RelativeLayout driverCard;

        public DriverListViewHolder(@NonNull View itemView) {
            super(itemView);
            driverName = itemView.findViewById(R.id.driver_name);
            driverImg = itemView.findViewById(R.id.driver_img);
            callBtn = itemView.findViewById(R.id.call_btn);
            driverCard = itemView.findViewById(R.id.driver_card);
        }
    }

}
