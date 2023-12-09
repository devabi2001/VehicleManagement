package com.thirumalaivasa.vehiclemanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thirumalaivasa.vehiclemanagement.Adapters.DriverListAdapter;
import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;

import java.util.List;


public class DriverFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager rvl;
    private RecyclerView.Adapter rva;

    private List<DriverData> driverDataList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver, container, false);
        findViews(view);
        RoomDbHelper dbHelper = RoomDbHelper.getInstance(getContext());
        driverDataList = dbHelper.driverDao().getAllDrivers();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setHasFixedSize(true);
        rvl = new LinearLayoutManager(getActivity());
        rva = new DriverListAdapter(driverDataList, getActivity());

        rva.setHasStableIds(true);

        recyclerView.setLayoutManager(rvl);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setAdapter(rva);

    }

    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.driver_recycler_view);
    }
}