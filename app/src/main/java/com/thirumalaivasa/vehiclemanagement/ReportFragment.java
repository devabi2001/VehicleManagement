package com.thirumalaivasa.vehiclemanagement;

import static com.thirumalaivasa.vehiclemanagement.Utils.DateTimeUtils.calculateDateBefore;
import static com.thirumalaivasa.vehiclemanagement.Utils.DateTimeUtils.calculateDaysDifference;
import static com.thirumalaivasa.vehiclemanagement.Utils.DateTimeUtils.compareDate;
import static com.thirumalaivasa.vehiclemanagement.Utils.DateTimeUtils.formatDate;
import static com.thirumalaivasa.vehiclemanagement.Utils.DateTimeUtils.getCurrentDateTime;
import static com.thirumalaivasa.vehiclemanagement.Utils.DateTimeUtils.stringToTimeStamp;
import static com.thirumalaivasa.vehiclemanagement.Utils.Util.getDisplayDate;
import static com.thirumalaivasa.vehiclemanagement.Utils.Util.getFormattedString;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Utils.PickerUtils;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class ReportFragment extends Fragment {

    private AutoCompleteTextView dateSpinner;
    private PieChart expenseChart;
    private TextView startDateTv, endDateTv;

    //OverallCard
    private TextView refuelAmtTv, salAmtTv, serviceAmtTv, otherAmtTv, totalAmtTv;

    //FuelCard
    private TextView noOfRefTv, totalFuelTv, avgFVehicleTv, avgFDayTv, totalAmtRefTv;

    //ServiceCard
    private TextView noOfServTv, totalAmtSerTv;

    //SalaryCard
    private TextView totalSalTv, avgSalDriverTv, avgSalDayTv;


    private List<ExpenseData> expenseDataArrayList;
    private int driverCount = 0;
    private int vehicleCount = 0;


    private String startDate, endDate;


    public ReportFragment() {
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
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        findViews(view);
        initializeVariables();
        setDateSpinner();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        dateSpinner.setOnItemClickListener((parent, view, position, id) -> {
            setDateRange(dateSpinner.getText().toString());
            calculateBetween(startDate, endDate);
        });

        startDateTv.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            PickerUtils.showDatePicker(getContext(), ((year, month, day) -> {
                startDate = getDisplayDate(year, month, day);
                startDateTv.setText(startDate);
                if (compareDate(startDate, endDate) > 0) {
                    calculateBetween(startDate, endDate);
                } else {
                    Toast.makeText(getContext(), "Start Date is after End Date ", Toast.LENGTH_SHORT).show();
                }
            }), calendar);
        });

        endDateTv.setOnClickListener(view -> {

            Calendar calendar = Calendar.getInstance();
            PickerUtils.showDatePicker(getContext(), ((year, month, day) -> {
                endDate = getDisplayDate(year, month, day);
                endDateTv.setText(endDate);
                if (compareDate(startDate, endDate) > 0) {
                    calculateBetween(startDate, endDate);
                } else {
                    Toast.makeText(getContext(), "End Date is before Start Date!!", Toast.LENGTH_SHORT).show();
                }
            }), calendar);

        });

    }

    private void initializeVariables() {
        RoomDbHelper dbHelper = RoomDbHelper.getInstance(getContext());
        expenseDataArrayList = dbHelper.expenseDao().getAllExpenses();
        vehicleCount = dbHelper.vehicleDao().getCount();
        driverCount = dbHelper.driverDao().getCount();
    }

    private void setDateTv(boolean clickable) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.arrow_drop_down_24);
        if (clickable) {
            startDateTv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
            endDateTv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
        } else {
            startDateTv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
            endDateTv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        }
        startDateTv.setClickable(clickable);
        endDateTv.setClickable(clickable);

    }

    private void setDateRange(String selectedValue) {
        String todayDate = getCurrentDateTime()[0];
        endDate = todayDate;
        setDateTv(false);
        switch (selectedValue) {
            case "Custom":
                startDate = todayDate;
                setDateTv(true);
                break;
            case "3 Month's":
                startDate = calculateDateBefore(3);
                break;
            case "6 Month's":
                startDate = calculateDateBefore(6);
                break;
            case "1 Year":
                startDate = calculateDateBefore(12);
                break;
            default:
                LocalDate currentDate = LocalDate.now();
                LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
                LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
                startDate = formatDate(firstDayOfMonth);
                endDate = formatDate(lastDayOfMonth);
                break;
        }
        startDateTv.setText(startDate);
        endDateTv.setText(endDate);
    }

    private void calculateBetween(String start, String end) {


        double totalExpense = 0, refuelExpense = 0, serviceExpense = 0, otherExpense = 0, salExpense = 0;
        double totalFuel = 0, totalPetrol = 0, totalDiesel = 0, totalCng = 0;

        int noOfRefuel = 0, noOfService = 0;

        long diffDays = calculateDaysDifference(start, end);

        long sTimeStamp = stringToTimeStamp(start);
        long eTimeStamp = stringToTimeStamp(end);

        for (ExpenseData data : expenseDataArrayList) {
            long timestamp = data.getTimestamp();
            if (timestamp >= sTimeStamp && timestamp <= eTimeStamp) {
                totalExpense += data.getTotal();
                switch (data.getExpenseType()) {
                    case "Refuel":
                        noOfRefuel++;
                        refuelExpense += data.getTotal();
                        totalFuel += data.getLiters();
                        break;
                    case "Service":
                        noOfService++;
                        serviceExpense += data.getTotal();
                        break;
                    case "Salary":
                        salExpense += data.getTotal();
                        break;
                    case "Other":
                        otherExpense += data.getTotal();
                        break;
                }
            }
        }

        double rPercent = 0, sPercent = 0, oPercent = 0, salPercent = 0;


        try {
            rPercent = refuelExpense * 100 / totalExpense;
            sPercent = serviceExpense * 100 / totalExpense;
            oPercent = otherExpense * 100 / totalExpense;
            salPercent = salExpense * 100 / totalExpense;

        } catch (ArithmeticException e) {
            e.printStackTrace();
        } finally {
            if (totalExpense == 0) {
                expenseChart.setVisibility(View.INVISIBLE);
            } else {
                expenseChart.setVisibility(View.VISIBLE);
            }
            double avgFVehicle = totalFuel / vehicleCount;
            double avgFDay = totalFuel / diffDays;

            double avgSalDriver = salExpense / driverCount;
            double avgSalDay = salExpense / diffDays;


            setChart(rPercent, sPercent, oPercent, salPercent);
            setAmount(totalExpense, refuelExpense, serviceExpense, otherExpense, salExpense);
            setFuel(noOfRefuel, totalFuel, avgFVehicle, avgFDay, refuelExpense);
            setService(noOfService, serviceExpense);
            setSalary(salExpense, avgSalDriver, avgSalDay);
        }
    }

    private void setAmount(double totalExp, double rExp, double sExp, double oExp, double salExp) {
        totalAmtTv.setText(getFormattedString(totalExp));
        refuelAmtTv.setText(getFormattedString(rExp));
        salAmtTv.setText(getFormattedString(salExp));
        serviceAmtTv.setText(getFormattedString(sExp));
        otherAmtTv.setText(getFormattedString(oExp));
    }

    private void setChart(double rPercent, double sPercent, double oPercent, double salPercent) {

        expenseChart.addPieSlice(
                new PieModel(
                        "Refuel",
                        (float) rPercent,
                        getResources().getColor(R.color.refuel_graph)));

        expenseChart.addPieSlice(
                new PieModel(
                        "Service",
                        (float) sPercent,
                        getResources().getColor(R.color.service_graph)));

        expenseChart.addPieSlice(
                new PieModel(
                        "Other",
                        (float) oPercent,
                        getResources().getColor(R.color.other_graph)));

        expenseChart.addPieSlice(
                new PieModel(
                        "Salary",
                        (float) salPercent,
                        getResources().getColor(R.color.salary_graph)));
        expenseChart.startAnimation();

    }

    private void setFuel(int noOfRefuel, double totalFuel, double avgFVehicle, double avgFDay, double refuelExpense) {
        noOfRefTv.setText(String.valueOf(noOfRefuel));
        totalFuelTv.setText(getFormattedString(totalFuel));
        avgFVehicleTv.setText(getFormattedString(avgFVehicle));
        avgFDayTv.setText(getFormattedString(avgFDay));
        totalAmtRefTv.setText(getFormattedString(refuelExpense));

    }

    private void setService(int noOfService, double serviceExpense) {

        totalAmtSerTv.setText(getFormattedString(serviceExpense));
        noOfServTv.setText(String.valueOf(noOfService));
    }

    private void setSalary(double salaryExpense, double avgSalDriver, double avgSalDay) {

        totalSalTv.setText(getFormattedString(salaryExpense));
        avgSalDriverTv.setText(getFormattedString(avgSalDriver));
        avgSalDayTv.setText(getFormattedString(avgSalDay));

    }


    private void sendData() {
        Gson gson = new Gson();
        String jsonData = gson.toJson(expenseDataArrayList);
        AsyncHttpClient client = new AsyncHttpClient();
        String apiUrl = "http://192.168.50.165:5000/api/store_data";

// Send the POST request with JSON data
        try {
            client.post(getContext(), apiUrl, new StringEntity(jsonData), "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    // Handle the success response

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    // Handle the failure response

                }
            });
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void setDateSpinner() {

        String[] dateArray = getResources().getStringArray(R.array.report_types);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.drop_down_item, dateArray);
        arrayAdapter.notifyDataSetChanged();
        dateSpinner.setText(arrayAdapter.getItem(0));
        dateSpinner.setAdapter(arrayAdapter);
        setDateRange(dateSpinner.getText().toString());
        calculateBetween(startDate, endDate);
    }

    private void findViews(View view) {

        dateSpinner = view.findViewById(R.id.date_spinner_report);
        expenseChart = view.findViewById(R.id.expense_chart);
        startDateTv = view.findViewById(R.id.start_date_tv);
        endDateTv = view.findViewById(R.id.end_date_tv);
        totalAmtTv = view.findViewById(R.id.total_expense_amt);
        refuelAmtTv = view.findViewById(R.id.refuel_amount);
        salAmtTv = view.findViewById(R.id.salary_amount);
        serviceAmtTv = view.findViewById(R.id.service_amount);
        otherAmtTv = view.findViewById(R.id.other_amount);
        noOfRefTv = view.findViewById(R.id.total_no_refuel_gen_rep);
        totalAmtRefTv = view.findViewById(R.id.total_amt_refuel_gen_rep);
        totalFuelTv = view.findViewById(R.id.total_fuel_gen_rep);
        avgFVehicleTv = view.findViewById(R.id.avg_vehicle_rep);
        avgFDayTv = view.findViewById(R.id.avg_day_rep);
        noOfServTv = view.findViewById(R.id.total_no_service_gen_rep);
        totalAmtSerTv = view.findViewById(R.id.total_amt_service_gen_rep);
        totalSalTv = view.findViewById(R.id.total_sal_amt_gen_rep);
        avgSalDriverTv = view.findViewById(R.id.avg_sal_driver_gen_rep);
        avgSalDayTv = view.findViewById(R.id.avg_sal_day_gen_rep);


    }
}