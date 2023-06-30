package com.thirumalaivasa.vehiclemanagement;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thirumalaivasa.vehiclemanagement.Adapters.VehicleListAdapter;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;

import java.util.ArrayList;

public class VehicleFragment extends Fragment {

    private final String TAG = "VehicleManagement";


    private RecyclerView recyclerView;
    RecyclerView.LayoutManager rvl;
    RecyclerView.Adapter rva;

    ArrayList<VehicleData> vehicleDataArrayList;

    public VehicleFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vehicle, container, false);
        findViews(view);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        vehicleDataArrayList = ((HomeScreen) requireActivity()).vehicleDataList;
        setListData();


    }

    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.home_list_rv);


    }



    private void setListData(){
        recyclerView.setHasFixedSize(true);
        rvl = new LinearLayoutManager(getActivity());
        rva = new VehicleListAdapter(getActivity(),vehicleDataArrayList);
        rva.setHasStableIds(true);
        recyclerView.setLayoutManager(rvl);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setAdapter(rva);

    }

}


