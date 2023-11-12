package com.thirumalaivasa.vehiclemanagement.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
@Entity(tableName = "ExpenseData")
public class ExpenseData{
    @PrimaryKey(autoGenerate = true)
    //Common for refuel and service and other expenses
    private String expenseType, date, time, desc, vno, eId;
    private double price, total;
    private long odometer;
    //Variables for refuel
    private double liters=0.0;
    private boolean isTankFilled=false;
    private double percentOfTank=0.0;

    private String fuelType;

    //Variables for service and other expense
    private String serviceType;
    private double serviceCharge=0.0;

    //Driver Salary
    private String driverName,salaryType;




    public ExpenseData() {
    }
    //Constructor for Refuel Data
    public ExpenseData(String expenseType, String date, String time, String desc, String vno, String eId, double price, double total, long odometer, double liters, boolean isTankFilled, double percentOfTank,String fuelType) {
        this.expenseType = expenseType;
        this.date = date;
        this.time = time;
        this.desc = desc;
        this.vno = vno;
        this.eId = eId;
        this.price = price;
        this.total = total;
        this.odometer = odometer;
        this.liters = liters;
        this.isTankFilled = isTankFilled;
        this.percentOfTank = percentOfTank;
        this.fuelType=fuelType;
    }
    //Constructor for Service Data
    public ExpenseData(String expenseType, String date, String time, String desc, String vno, String eId, double price, double total, long odometer, String serviceType, double serviceCharge) {
        this.expenseType = expenseType;
        this.date = date;
        this.time = time;
        this.desc = desc;
        this.vno = vno;
        this.eId = eId;
        this.price = price;
        this.total = total;
        this.odometer = odometer;
        this.serviceType = serviceType;
        this.serviceCharge = serviceCharge;
    }

    //Constructor for Salary
    public ExpenseData(String expenseType, String date, String time,String desc, String eId, String driverName, String salaryType, double total) {
        this.expenseType = expenseType;
        this.date = date;
        this.time = time;
        this.desc = desc;
        this.eId = eId;
        this.driverName = driverName;
        this.salaryType = salaryType;
        this.total = total;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getVno() {
        return vno;
    }

    public void setVno(String vno) {
        this.vno = vno;
    }

    public String geteId() {
        return eId;
    }

    public void seteId(String eId) {
        this.eId = eId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public long getOdometer() {
        return odometer;
    }

    public void setOdometer(long odometer) {
        this.odometer = odometer;
    }

    public double getLiters() {
        return liters;
    }

    public void setLiters(double liters) {
        this.liters = liters;
    }

    public boolean isTankFilled() {
        return isTankFilled;
    }

    public void setTankFilled(boolean tankFilled) {
        isTankFilled = tankFilled;
    }

    public double getPercentOfTank() {
        return percentOfTank;
    }

    public void setPercentOfTank(double percentOfTank) {
        this.percentOfTank = percentOfTank;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public double getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getSalaryType() {
        return salaryType;
    }

    public void setSalaryType(String salaryType) {
        this.salaryType = salaryType;
    }
}
