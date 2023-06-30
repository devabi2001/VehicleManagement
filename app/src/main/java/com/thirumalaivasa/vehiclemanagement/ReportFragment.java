package com.thirumalaivasa.vehiclemanagement;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Models.UserData;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class ReportFragment extends Fragment {

    private final String TAG = "VM";

    //Widgets
    //Button
    private Button printBtn;
    //Spinner
    private Spinner dateSpinner;
    private PieChart expenseChart;

    private LinearLayout dateLayout;

    private TextView startDateTv, endDateTv;

    //OverallCard
    private TextView refuelAmtTv, salAmtTv, serviceAmtTv, otherAmtTv, totalAmtTv;

    //FuelCard
    private TextView noOfRefTv, totalFuelTv, avgFVehicleTv, avgFDayTv;

    //ServiceCard
    private TextView noOfServTv, totalAmtSerTv;

    //SalaryCard
    private TextView totalSalTv, avgSalDriver, avgSalDay;


    private ArrayList<ExpenseData> expenseDataArrayList;
    private ArrayList<VehicleData> vehicleDataArrayList;
    private ArrayList<DriverData> driverDataArrayList;

    private UserData userData;

    private String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private String monthYear;

    private String startDate, endDate;

    private int selectedDay, selectedMonth, selectedYear;

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
        monthYear = getCurrentMonthYear();
        String d[] = monthYear.split("-");
        selectedDay = Integer.parseInt(d[0]);
        selectedMonth = Integer.parseInt(d[1]) - 1;
        selectedYear = Integer.parseInt(d[2]);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (adapterView.getItemAtPosition(i).equals("Custom")) {
                    startDate = monthYear;
                    endDate = monthYear;
                    startDateTv.setText(startDate);
                    endDateTv.setText(endDate);
                    dateLayout.setVisibility(View.VISIBLE);
                } else if (adapterView.getItemAtPosition(i).equals("3 Month's")) {
                    dateLayout.setVisibility(View.GONE);
                    startDate = calculateMonth(-3);
                    endDate = getCurrentMonthYear();
                    calculateBetween(startDate, endDate);
                } else if (adapterView.getItemAtPosition(i).equals("6 Month's")) {
                    dateLayout.setVisibility(View.GONE);
                    startDate = calculateMonth(-6);
                    endDate = getCurrentMonthYear();
                    calculateBetween(startDate, endDate);
                } else if (adapterView.getItemAtPosition(i).equals("1 Year")) {
                    dateLayout.setVisibility(View.GONE);
                    startDate = calculateMonth(-12);
                    endDate = getCurrentMonthYear();
                    calculateBetween(startDate, endDate);
                } else {
                    dateLayout.setVisibility(View.GONE);
                    calculateCurrentMonth();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        startDateTv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                DatePickerDialog startDatePicker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String d, m;
                                if (day < 10)
                                    d = "0" + day;
                                else
                                    d = String.valueOf(day);
                                if (month <= 10)
                                    m = "0" + (month + 1);
                                else
                                    m = String.valueOf(month + 1);

                                String selecetedDate = d + "-" + m + "-" + year;
                                startDateTv.setText(selecetedDate);
                                selectedDay = day;
                                selectedMonth = month;
                                selectedYear = year;
                                if (compareDate(selecetedDate, endDate) == -1) {
                                    calculateBetween(selecetedDate, endDate);
                                } else {
                                    Toast.makeText(getContext(), "Start Date is after End Date ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, selectedYear, selectedMonth, selectedDay);


                startDatePicker.show();

            }
        });

        endDateTv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                DatePickerDialog endDatePicker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String d, m;
                                if (day < 10)
                                    d = "0" + day;
                                else
                                    d = String.valueOf(day);
                                if (month <= 10)
                                    m = "0" + (month + 1);
                                else
                                    m = String.valueOf(month + 1);

                                String selectedDate = d + "-" + m + "-" + year;
                                endDateTv.setText(selectedDate);
                                selectedDay = day;
                                selectedMonth = month;
                                selectedYear = year;
                                if (compareDate(startDate, selectedDate) == 1) {
                                    calculateBetween(startDate, selectedDate);
                                } else {
                                    Toast.makeText(getContext(), "End Date is before Start Date!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, selectedYear, selectedMonth, selectedDay);


                endDatePicker.show();

            }
        });


        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });

    }

    private void initializeVariables() {
        if (getActivity() != null) {
            userData = getActivity().getIntent().getParcelableExtra("UserData");
            expenseDataArrayList = getActivity().getIntent().getParcelableArrayListExtra("ExpenseData");
            vehicleDataArrayList = getActivity().getIntent().getParcelableArrayListExtra("VehicleData");
            driverDataArrayList = getActivity().getIntent().getParcelableArrayListExtra("DriverData");
        }
    }

    //Method Used to get current month and year device month and year in a format MMM-yyyy (i.e:) Jan-2023
    private String getCurrentMonthYear() {
        Calendar calendar = Calendar.getInstance();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.of(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH));
        return localDate.format(dtf);
    }


    //To get the date's for the report of 1 year 6 month and 3 month subtract the date
    private String calculateMonth(int duration) {

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MONTH, duration);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.of(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH));

        return localDate.format(dtf);

    }

    private void calculateBetween(String start, String end) {


        int totalExpense = 0, refuelExpense = 0, serviceExpense = 0, otherExpense = 0, salExpense = 0;
        double totalFuel = 0, totalPetrol = 0, totalDiesel = 0, totalCng = 0;

        int noOfRefuel = 0, noOfService = 0;


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate sDate = LocalDate.parse(start, formatter);
        LocalDate eDate = LocalDate.parse(end, formatter);
        for (ExpenseData data : expenseDataArrayList) {
            LocalDate dateToCheck = LocalDate.parse(data.getDate(), formatter); // date to check
            if (isBetween(dateToCheck, sDate, eDate)) {

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
            } else {
                continue;
            }
        }

        int rPercent = 0, sPercent = 0, oPercent = 0, salPercent = 0;


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
            double avgFVehicle = totalFuel / vehicleDataArrayList.size();
            double avgFDay = totalFuel / getNoOfDays(monthYear);
            setChart(rPercent, sPercent, oPercent, salPercent);
            setAmount(totalExpense, refuelExpense, serviceExpense, otherExpense, salExpense);
            setFuel(noOfRefuel, totalFuel, avgFVehicle, avgFDay);
            setService(noOfService, serviceExpense);
            setSalary(salExpense);
        }
    }

    public boolean isBetween(LocalDate dateToCheck, LocalDate startDate, LocalDate endDate) {
        return dateToCheck.isAfter(startDate) && dateToCheck.isBefore(endDate);
    }

    private int compareDate(String d1, String d2) {

// Parse date strings into LocalDate objects
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date1 = LocalDate.parse(d1, dateFormatter);
        LocalDate date2 = LocalDate.parse(d2, dateFormatter);

// Compare dates
        if (date1.isAfter(date2)) {
            return 1;
        } else if (date1.isBefore(date2)) {
            return -1;
        } else {
            return 0;
        }
    }


    private int getNoOfDays(String value) {
        int retValue = 31;
        String[] ymd = value.split("-");
        String givenMonth = ymd[0];
        int year = Integer.parseInt(ymd[1]);
        Calendar calendar = Calendar.getInstance();

        int m = 0;
        for (int i = 0; i < month.length; i++) {
            if (month[i].equals(givenMonth)) {
                m = i;
                break;
            }
        }
        calendar.set(year, m, 1);
        retValue = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return retValue;
    }


    private void calculateCurrentMonth() {

        int totalExpense = 0, refuelExpense = 0, serviceExpense = 0, otherExpense = 0, salExpense = 0;
        double totalFuel = 0, totalPetrol = 0, totalDiesel = 0, totalCng = 0;

        int noOfRefuel = 0, noOfService = 0;

        for (ExpenseData data : expenseDataArrayList) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            LocalDate dateToCheck = LocalDate.parse(data.getDate(), formatter); // date to check
            YearMonth currentMonth = YearMonth.now(); // get the current year and month
            boolean isDateInCurrentMonth = currentMonth.equals(YearMonth.from(dateToCheck));
            if (isDateInCurrentMonth) {

                totalExpense += data.getTotal();
                switch (data.getExpenseType()) {
                    case "Refuel":
                        noOfRefuel++;
                        refuelExpense += data.getTotal();

                        totalFuel += data.getLiters();
//                        if (data.getFuelType().equalsIgnoreCase("Diesel")) {
//                            totalDiesel += data.getLiters();
//                        } else if (data.getFuelType().equalsIgnoreCase("Petrol")) {
//                            totalPetrol += data.getLiters();
//                        } else if (data.getFuelType().equalsIgnoreCase("Cng")) {
//                            totalCng += data.getLiters();
//                        }
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
        int rPercent = 0, sPercent = 0, oPercent = 0, salPercent = 0;


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
            double avgFVehicle = totalFuel / vehicleDataArrayList.size();
            double avgFDay = totalFuel / getNoOfDays(monthYear);
            setChart(rPercent, sPercent, oPercent, salPercent);
            setAmount(totalExpense, refuelExpense, serviceExpense, otherExpense, salExpense);
            setFuel(noOfRefuel, totalFuel, avgFVehicle, avgFDay);
            setService(noOfService, serviceExpense);
            setSalary(salExpense);
        }


    }


    private void setAmount(int totalExp, int rExp, int sExp, int oExp, int salExp) {
        totalAmtTv.setText(totalExp + "/-");
        refuelAmtTv.setText(rExp + "/-");
        salAmtTv.setText(salExp + "/-");
        serviceAmtTv.setText(sExp + "/-");
        otherAmtTv.setText(oExp + "/-");
    }

    private void setChart(int rPercent, int sPercent, int oPercent, int salPercent) {

        expenseChart.addPieSlice(
                new PieModel(
                        "Refuel",
                        rPercent,
                        getResources().getColor(R.color.refuel_graph)));

        expenseChart.addPieSlice(
                new PieModel(
                        "Service",
                        sPercent,
                        getResources().getColor(R.color.service_graph)));

        expenseChart.addPieSlice(
                new PieModel(
                        "Other",
                        oPercent,
                        getResources().getColor(R.color.other_graph)));

        expenseChart.addPieSlice(
                new PieModel(
                        "Salary",
                        salPercent,
                        getResources().getColor(R.color.salary_graph)));
        expenseChart.startAnimation();

    }

    private void setFuel(int noOfRefuel, double totalFuel, double avgFVehicle, double avgFDay) {
        noOfRefTv.setText(String.valueOf(noOfRefuel));
        totalFuelTv.setText(String.valueOf(totalFuel));
        avgFVehicleTv.setText(String.format("%.2f", avgFVehicle));
        avgFDayTv.setText(String.format("%.2f", avgFDay));

    }

    private void setService(int noOfService, int serviceExpense) {
        totalAmtSerTv.setText(String.valueOf(serviceExpense));
        noOfServTv.setText(String.valueOf(noOfService));
    }

    private void setSalary(int salaryExpense) {

        totalSalTv.setText(String.valueOf(salaryExpense));

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


    private void findViews(View view) {

        dateSpinner = view.findViewById(R.id.date_spinner_report);
        expenseChart = view.findViewById(R.id.expense_chart);
        dateLayout = view.findViewById(R.id.custom_date_layout);
        startDateTv = view.findViewById(R.id.start_date_tv);
        endDateTv = view.findViewById(R.id.end_date_tv);
        totalAmtTv = view.findViewById(R.id.total_expense_amt);
        refuelAmtTv = view.findViewById(R.id.refuel_amount);
        salAmtTv = view.findViewById(R.id.salary_amount);
        serviceAmtTv = view.findViewById(R.id.service_amount);
        otherAmtTv = view.findViewById(R.id.other_amount);
        noOfRefTv = view.findViewById(R.id.total_no_refuel_gen_rep);
        totalFuelTv = view.findViewById(R.id.total_fuel_gen_rep);
        avgFVehicleTv = view.findViewById(R.id.avg_vehicle_rep);
        avgFDayTv = view.findViewById(R.id.avg_day_rep);
        noOfServTv = view.findViewById(R.id.total_no_service_gen_rep);
        totalAmtSerTv = view.findViewById(R.id.total_amt_service_gen_rep);
        totalSalTv = view.findViewById(R.id.total_sal_amt_gen_rep);
        avgSalDriver = view.findViewById(R.id.avg_sal_driver_gen_rep);
        avgSalDay = view.findViewById(R.id.avg_sal_day_gen_rep);

        printBtn = view.findViewById(R.id.print_btn);


    }
}