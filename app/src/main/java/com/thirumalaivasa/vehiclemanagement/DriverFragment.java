package com.thirumalaivasa.vehiclemanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thirumalaivasa.vehiclemanagement.Adapters.DriverListAdapter;
import com.thirumalaivasa.vehiclemanagement.Adapters.ExpenseListAdapter;
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;


public class DriverFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager rvl;
    private RecyclerView.Adapter rva;

    private ArrayList<DriverData> driverDataArrayList;


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
        driverDataArrayList = new ArrayList<>();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        driverDataArrayList = ((HomeScreen) requireActivity()).driverDataList;


        recyclerView.setHasFixedSize(true);
        rvl = new LinearLayoutManager(getActivity());
        rva = new DriverListAdapter(driverDataArrayList, getActivity());

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