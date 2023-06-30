package com.thirumalaivasa.vehiclemanagement.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserData implements Parcelable{
    private String uid,userName,email,contact,travelsName;
    private int totalVehicles,totalDrivers;


    public UserData() {
    }


    public UserData(String uid,String userName,String email, String contact,String travelsName,int totalVehicles,int totalDrivers) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.contact = contact;
        this.travelsName = travelsName;
        this.totalVehicles = totalVehicles;
        this.totalDrivers = totalDrivers;
    }


    protected UserData(Parcel in) {
        uid = in.readString();
        userName = in.readString();
        email = in.readString();
        contact = in.readString();
        travelsName = in.readString();
        totalVehicles = in.readInt();
        totalDrivers = in.readInt();
    }

    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };

    public int getTotalVehicles() {
        return totalVehicles;
    }

    public int getTotalDrivers() {
        return totalDrivers;
    }

    public void setTotalVehicles(int totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public void setTotalDrivers(int totalDrivers) {
        this.totalDrivers = totalDrivers;
    }

    public String getTravelsName() {
        return travelsName;
    }

    public void setTravelsName(String travelsName) {
        this.travelsName = travelsName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(userName);
        dest.writeString(email);
        dest.writeString(contact);
        dest.writeString(travelsName);
        dest.writeInt(totalVehicles);
        dest.writeInt(totalDrivers);
    }
}
