package com.thirumalaivasa.vehiclemanagement.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "UserData")
public class UserData {

    @PrimaryKey(autoGenerate = true)
    private long primaryKey;
    private String uid, userName, email, contact, travelsName;

    private boolean isSynced;
    private int totalVehicles, totalDrivers;

    public UserData() {
    }

    public UserData(String uid, String userName, String email, String contact, String travelsName, boolean isSynced, int totalVehicles, int totalDrivers) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.contact = contact;
        this.travelsName = travelsName;
        this.isSynced = isSynced;
        this.totalVehicles = totalVehicles;
        this.totalDrivers = totalDrivers;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getTravelsName() {
        return travelsName;
    }

    public void setTravelsName(String travelsName) {
        this.travelsName = travelsName;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public int getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(int totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public int getTotalDrivers() {
        return totalDrivers;
    }

    public void setTotalDrivers(int totalDrivers) {
        this.totalDrivers = totalDrivers;
    }

    public long getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(long primaryKey) {
        this.primaryKey = primaryKey;
    }
}
