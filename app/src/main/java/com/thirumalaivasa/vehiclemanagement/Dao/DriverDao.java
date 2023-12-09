package com.thirumalaivasa.vehiclemanagement.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.thirumalaivasa.vehiclemanagement.Models.DriverData;

import java.util.List;

@Dao
public interface DriverDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DriverData driverData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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

    @Query("SELECT driverName FROM DriverData")
    List<String> getDriversName();

    @Query("SELECT * FROM DriverData WHERE isSynced=0")
    List<DriverData> getUnsyncedData();
}
