package com.thirumalaivasa.vehiclemanagement.Models;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "ExpenseData", indices = {@Index(value = "eId", unique = true)})
public class ExpenseData {
    public String eId;
    @PrimaryKey(autoGenerate = true)

    private long primaryKey;
    //Common for refuel and service and other expenses
    private String expenseType;
    private long timestamp;
    private String desc;
    private String vno;
    private boolean isSynced;
    private double price, total;
    private long odometer;
    //Variables for refuel
    private double liters = 0.0;
    private boolean isTankFilled = false;

    //Variables for service and other expense
    private String serviceType;
    private double serviceCharge = 0.0;

    //Driver Salary
    private String driverName, salaryType;


    public ExpenseData() {
    }

    //Constructor for Refuel Data
    @Ignore
    public ExpenseData(String expenseType, long timestamp, String desc, String vno, String eId, double price, double total, long odometer, double liters, boolean isTankFilled, boolean isSynced) {
        this.expenseType = expenseType;
        this.timestamp = timestamp;
        this.desc = desc;
        this.vno = vno;
        this.eId = eId;
        this.price = price;
        this.total = total;
        this.odometer = odometer;
        this.liters = liters;
        this.isTankFilled = isTankFilled;
        this.isSynced = isSynced;
    }

    //Constructor for Service Data
    @Ignore
    public ExpenseData(String expenseType, long timestamp, String desc, String vno, String eId, double price, double total, long odometer, String serviceType, double serviceCharge, boolean isSynced) {
        this.expenseType = expenseType;
        this.timestamp = timestamp;
        this.desc = desc;
        this.vno = vno;
        this.eId = eId;
        this.price = price;
        this.total = total;
        this.odometer = odometer;
        this.serviceType = serviceType;
        this.serviceCharge = serviceCharge;
        this.isSynced = isSynced;
    }

    //Constructor for Salary
    @Ignore
    public ExpenseData(String expenseType, long timestamp, String desc, String eId, String driverName, String salaryType, double total, boolean isSynced) {
        this.expenseType = expenseType;
        this.timestamp = timestamp;
        this.desc = desc;
        this.eId = eId;
        this.driverName = driverName;
        this.salaryType = salaryType;
        this.total = total;
        this.isSynced = isSynced;
    }

    public long getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(long primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
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
