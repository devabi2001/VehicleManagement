package com.thirumalaivasa.vehiclemanagement.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class DriverData implements Parcelable {
    String driverName,contact,licenseNum,licenseExpDate,driverId,salPeriod;
    double salary;


    public DriverData() {
    }

    public DriverData(String driverName, String contact, String licenseNum, String licenseExpDate, String driverId, double salary, String salPeriod) {
        this.driverName = driverName;
        this.contact = contact;
        this.licenseNum = licenseNum;
        this.licenseExpDate = licenseExpDate;
        this.driverId = driverId;
        this.salary = salary;
        this.salPeriod = salPeriod;
    }

    protected DriverData(Parcel in) {
        driverName = in.readString();
        contact = in.readString();
        licenseNum = in.readString();
        licenseExpDate = in.readString();
        driverId = in.readString();
        salary = in.readDouble();
        salPeriod = in.readString();
    }

    public static final Creator<DriverData> CREATOR = new Creator<DriverData>() {
        @Override
        public DriverData createFromParcel(Parcel in) {
            return new DriverData(in);
        }

        @Override
        public DriverData[] newArray(int size) {
            return new DriverData[size];
        }
    };

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

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getSalPeriod() {
        return salPeriod;
    }

    public void setSalPeriod(String salPeriod) {
        this.salPeriod = salPeriod;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(driverName);
        parcel.writeString(contact);
        parcel.writeString(licenseNum);
        parcel.writeString(licenseExpDate);
        parcel.writeString(driverId);
        parcel.writeDouble(salary);
        parcel.writeString(salPeriod);
    }
}
