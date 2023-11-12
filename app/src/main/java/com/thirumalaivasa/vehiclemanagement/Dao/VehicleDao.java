package com.thirumalaivasa.vehiclemanagement.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;

import java.util.List;

@Dao
public interface VehicleDao {
    @Insert
    void insert(VehicleData vehicleData);
    @Insert
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

}
