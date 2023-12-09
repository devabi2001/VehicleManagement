package com.thirumalaivasa.vehiclemanagement;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thirumalaivasa.vehiclemanagement.Adapters.ExpenseListAdapter;
import com.thirumalaivasa.vehiclemanagement.Dao.ExpenseDao;
import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private final String TAG = "VehicleManagement";


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager rvl;
    private RecyclerView.Adapter rva;
    private AutoCompleteTextView vehicleSpinner, serviceSpinner;

    private String selectedVehicle, selectedService;

    private List<ExpenseData> expenseDataArrayList;
    private List<ExpenseData> initialData;
    private List<String> vehicleNumList;

    private List<String> driverNameList;

    private boolean isLoading;
    private boolean isLastPage;
    private int currentPage;

    private RoomDbHelper dbHelper;

    public HomeFragment() {
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

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        findViews(view);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


        dbHelper = RoomDbHelper.getInstance(getContext());
        expenseDataArrayList = dbHelper.expenseDao().getAllExpenses();
        vehicleNumList = dbHelper.vehicleDao().getAllVehicleNumber();
        driverNameList = dbHelper.driverDao().getDriversName();

        setInitialData();
        rva.setHasStableIds(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(rvl);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setAdapter(rva);
        //loadPage();
        if (expenseDataArrayList != null && expenseDataArrayList.size() > 0) {
            setFilterSpinner(0);
        }

        setServiceSpinner();
/*
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    loadPage();
                }
            }
        });
*/
        vehicleSpinner.setOnItemClickListener((parent, view, position, id) -> {
            selectedVehicle = vehicleSpinner.getText().toString();
            List<ExpenseData> filteredList = filter(selectedVehicle, serviceSpinner.getText().toString());
            rva = new ExpenseListAdapter(getActivity(), filteredList);
            recyclerView.setAdapter(rva);
        });


        serviceSpinner.setOnItemClickListener((parent, view, position, id) -> {
            selectedService = serviceSpinner.getText().toString();
            if (selectedService.equals("Salary")) {
                setFilterSpinner(1);
            } else {
                setFilterSpinner(0);
            }
            List<ExpenseData> filteredList = filter(selectedVehicle, serviceSpinner.getText().toString());
            rva = new ExpenseListAdapter(getActivity(), filteredList);
            recyclerView.setAdapter(rva);
        });


    }

    private void setInitialData() {
        initialData = new ArrayList<>();
        setFilterSpinner(0);
        selectedVehicle = "All";
        selectedService = "All";
        isLoading = false;
        isLastPage = false;
        currentPage = 0;
        rvl = new LinearLayoutManager(getActivity());
        rva = new ExpenseListAdapter(getActivity(), expenseDataArrayList);
    }

    /*
        private void loadPage() {
            // Check if we are already loading data or if we have loaded all the pages

            if (isLoading || isLastPage) {

                return;
            }

            // Show the progress bar
            //progressBar.setVisibility(View.VISIBLE);

            // Set the loading flag to true
            isLoading = true;

            // Load the data for the next page
            // Here you would retrieve the data for the next page, e.g., from an API or a database

            ArrayList<ExpenseData> newObjects = loadDataForPage(currentPage + 1);

            // Check if there is more data to load
            if (newObjects.size() == 0) {
                isLastPage = true;
            } else {
                // Increment the current page number
                currentPage++;

                // Add the new data to the existing list
                initialData.addAll(newObjects);

                // Notify the adapter that the data has changed
                rva.notifyDataSetChanged();
            }

            // Hide the progress bar and reset the loading flag
            // progressBar.setVisibility(View.GONE);
            isLoading = false;
        }

        private ArrayList<ExpenseData> loadDataForPage(int page) {
            // Here you would retrieve the data for the given page, e.g., from an API or a database
            // In this example, we will simulate loading data from a local array list

            int startIndex = (page == 1) ? 0 : ((page - 1) * 20);
            int endIndex = Math.min(startIndex + 20, expenseDataArrayList.size());

            ArrayList<ExpenseData> newData = new ArrayList<>();
            for (int i = startIndex; i < endIndex; i++) {

                newData.add(expenseDataArrayList.get(i));
            }
            return newData;
        }

    */
    private List<ExpenseData> filter(String selectedVehicle, String selectedService) {
        List<ExpenseData> retValue = new ArrayList<>();
        if (selectedVehicle.equals("All") && selectedService.equals("All"))
            return expenseDataArrayList;
        else if (selectedVehicle.equals("All")) {
            for (ExpenseData expenseData : expenseDataArrayList) {
                if (expenseData.getExpenseType() == null)
                    continue;
                if (expenseData.getExpenseType().equals(selectedService))
                    retValue.add(expenseData);
            }
        } else if (selectedService.equals("All")) {
            for (ExpenseData expenseData : expenseDataArrayList) {
                if (expenseData.getVno() == null)
                    continue;
                if (expenseData.getVno().equals(selectedVehicle))
                    retValue.add(expenseData);
            }
        } else if (selectedService.equals("Salary")) {
            for (ExpenseData expenseData : expenseDataArrayList) {
                if (expenseData.getExpenseType() == null || expenseData.getDriverName() == null)
                    continue;
                if (expenseData.getDriverName().equals(selectedVehicle))
                    retValue.add(expenseData);
            }
        } else {
            for (ExpenseData expenseData : expenseDataArrayList) {
                if (expenseData.getVno() == null || expenseData.getExpenseType() == null)
                    continue;

                if (expenseData.getVno().equals(selectedVehicle))
                    if (expenseData.getExpenseType().equals(selectedService))
                        retValue.add(expenseData);
            }
        }

        return retValue;
    }

    //Sets the spinner values for vehicleSpinner
    // The spinner values will be changed  when the service spinner value is salary
    //When salary selected driver names will be listed
    //Otherwise Vehicle numbers will be listed
    private void setFilterSpinner(int what) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.drop_down_item);
        arrayAdapter.add("All");
        selectedVehicle = "All";
        switch (what) {
            case 0:
                if (vehicleNumList != null && vehicleNumList.size() > 0) {
                    arrayAdapter.addAll(vehicleNumList);
                    arrayAdapter.notifyDataSetChanged();
                    vehicleSpinner.setText(arrayAdapter.getItem(0));
                    vehicleSpinner.setAdapter(arrayAdapter);
                }
                break;
            case 1:
                if (driverNameList != null && driverNameList.size() > 0) {
                    arrayAdapter.addAll(driverNameList);
                    arrayAdapter.notifyDataSetChanged();
                    vehicleSpinner.setText(arrayAdapter.getItem(0));
                    vehicleSpinner.setAdapter(arrayAdapter);
                }
                break;
        }

    }

    private void setServiceSpinner() {

        String[] expenseArray = getResources().getStringArray(R.array.expenses_types);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.drop_down_item, expenseArray);
        arrayAdapter.notifyDataSetChanged();
        serviceSpinner.setText(arrayAdapter.getItem(0));
        serviceSpinner.setAdapter(arrayAdapter);
    }


    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.home_list_rv);
        vehicleSpinner = view.findViewById(R.id.vehicle_spinner);
        serviceSpinner = view.findViewById(R.id.service_spinner);

    }
}