package com.thirumalaivasa.vehiclemanagement.Dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;

import java.util.List;

@Dao
public interface VehicleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(VehicleData vehicleData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<VehicleData> vehicleDataList);


    @Update
    void update(VehicleData vehicleData);
    @Delete
    void delete(VehicleData vehicleData);
    @Query("DELETE FROM VehicleData")
    void deleteAllVehicles();

    @Query("SELECT * FROM VehicleData")
    List<VehicleData> getAllVehicles();

    @Query("SELECT registrationNumber FROM VehicleData")
    List<String> getAllVehicleNumber();

    @Query("SELECT * FROM VehicleData WHERE registrationNumber=:regNum LIMIT 1")
    VehicleData getVehicleByRegNum(String regNum);

    @Query("SELECT * FROM VehicleData WHERE isSynced=0")
    List<VehicleData> getUnsyncedData();

    @Query("SELECT vehicleClass FROM VehicleData WHERE registrationNumber=:regNum")
    String getVehicleClass(String regNum);

    @Query("SELECT fuelCapacity,fuelType FROM VehicleData WHERE registrationNumber=:regNum")
    Cursor getFuelDetails(String regNum);

    @Query("SELECT COUNT(*) FROM VehicleData")
    int getCount();

    @Query("SELECT imagePath FROM VehicleData WHERE registrationNumber=:regNum")
    String getImagePath(String regNum);

}
