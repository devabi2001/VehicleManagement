package com.thirumalaivasa.vehiclemanagement.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.thirumalaivasa.vehiclemanagement.Models.UserData;


@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserData userData);

    @Update
    void Update(UserData userData);

    @Query("SELECT * FROM UserData LIMIT 1")
    UserData getUserData();

    @Query("SELECT * FROM UserData WHERE isSynced=0 LIMIT 1")
    UserData getUnsyncedData();

    @Query("SELECT uid FROM UserData LIMIT 1")
    String getUid();


}
