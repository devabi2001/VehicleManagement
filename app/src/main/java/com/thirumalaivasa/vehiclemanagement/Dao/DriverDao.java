package com.thirumalaivasa.vehiclemanagement.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.thirumalaivasa.vehiclemanagement.Models.DriverData;

import java.util.List;

@Dao
public interface DriverDao {
    @Insert
    void insert(DriverData driverData);

    @Insert
    void insertAll(List<DriverData> driverDataList);
    @Update
    void update(DriverData DriverData);
    @Delete
    void delete(DriverData DriverData);
    @Query("DELETE FROM DriverData")
    void deleteAllDrivers();
    @Query("SELECT * FROM DriverData")
    List<DriverData> getAllDrivers();

    @Query("SELECT * FROM DriverData WHERE driverId=:id")
    DriverData getDriverById(String id);
}
