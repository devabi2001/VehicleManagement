package com.thirumalaivasa.vehiclemanagement.Models;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "DriverData", indices = {@Index(value = "driverId", unique = true)})
public class DriverData {

    @PrimaryKey(autoGenerate = true)
    private long primaryKey;

    private String driverName, contact, licenseNum, licenseExpDate, driverId, salPeriod;
    private double salary;
    private boolean isSynced;
    private String imagePath;

    public DriverData() {
    }

    public DriverData(String driverName, String contact, String licenseNum, String licenseExpDate, String driverId, String salPeriod, double salary, boolean isSynced, String imagePath) {
        this.driverName = driverName;
        this.contact = contact;
        this.licenseNum = licenseNum;
        this.licenseExpDate = licenseExpDate;
        this.driverId = driverId;
        this.salPeriod = salPeriod;
        this.salary = salary;
        this.isSynced = isSynced;
        this.imagePath = imagePath;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLicenseNum() {
        return licenseNum;
    }

    public void setLicenseNum(String licenseNum) {
        this.licenseNum = licenseNum;
    }

    public String getLicenseExpDate() {
        return licenseExpDate;
    }

    public void setLicenseExpDate(String licenseExpDate) {
        this.licenseExpDate = licenseExpDate;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getSalPeriod() {
        return salPeriod;
    }

    public void setSalPeriod(String salPeriod) {
        this.salPeriod = salPeriod;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public long getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(long primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
