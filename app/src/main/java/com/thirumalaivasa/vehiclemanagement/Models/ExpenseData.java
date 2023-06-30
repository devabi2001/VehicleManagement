package com.thirumalaivasa.vehiclemanagement.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseData implements Parcelable, Comparable<ExpenseData> {
    //Common for refuel and service and other expenses
    String expenseType, date, time, desc, vno, eId;
    double price, total;
    long odometer;
    //Variables for refuel
    double liters=0.0;
    boolean isTankFilled=false;
    double percentOfTank=0.0;

    String fuelType;

    //Variables for service and other expense
    String serviceType;
    double serviceCharge=0.0;

    //Driver Salary
    String driverName,salaryType;




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


    protected ExpenseData(Parcel in) {
        expenseType = in.readString();
        date = in.readString();
        time = in.readString();
        desc = in.readString();
        vno = in.readString();
        eId = in.readString();
        price = in.readDouble();
        total = in.readDouble();
        odometer = in.readLong();
        liters = in.readDouble();
        isTankFilled = in.readByte() != 0;
        percentOfTank = in.readDouble();
        fuelType = in.readString();
        serviceType = in.readString();
        serviceCharge = in.readDouble();
        driverName = in.readString();
        salaryType = in.readString();
    }

    public static final Creator<ExpenseData> CREATOR = new Creator<ExpenseData>() {
        @Override
        public ExpenseData createFromParcel(Parcel in) {
            return new ExpenseData(in);
        }

        @Override
        public ExpenseData[] newArray(int size) {
            return new ExpenseData[size];
        }
    };

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


    public String getFuelType() {

        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    @Override
    public int compareTo(ExpenseData expenseData) {
        Date date1 = null, date2 = null;
        Date time1 = null, time2 = null;

        try {
            date1 = new SimpleDateFormat("dd-MM-yyyy").parse(this.date);
            time1 = new SimpleDateFormat("hh:mm").parse(this.time);

            date2 = new SimpleDateFormat("dd-MM-yyyy").parse(expenseData.date);
            time2 = new SimpleDateFormat("hh:mm").parse(expenseData.time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date1!=null && date2!=null && time1 !=null && time2!=null) {
            if (date1.compareTo(date2) > 0) {
                return -1;
            } else if (date1.compareTo(date2) < 0) {
                return 1;
            } else if (date1.compareTo(date2) == 0) {
                if (time1.compareTo(time2) > 0)
                    return -1;
                else if (time1.compareTo(time2) < 0)
                    return 1;
                else
                    return 0;
            }
        }

        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(expenseType);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(desc);
        parcel.writeString(vno);
        parcel.writeString(eId);
        parcel.writeDouble(price);
        parcel.writeDouble(total);
        parcel.writeLong(odometer);
        parcel.writeDouble(liters);
        parcel.writeByte((byte) (isTankFilled ? 1 : 0));
        parcel.writeDouble(percentOfTank);
        parcel.writeString(fuelType);
        parcel.writeString(serviceType);
        parcel.writeDouble(serviceCharge);
        parcel.writeString(driverName);
        parcel.writeString(salaryType);
    }


//    @Override
//    public int compareTo(ContactList contactList) {
//        {
//            if(frequecy == contactList.frequecy){
//                return 0;
//            }else if(frequecy < contactList.frequecy)
//                return 1;
//            else
//                return -1;
//        }
//    }

}
