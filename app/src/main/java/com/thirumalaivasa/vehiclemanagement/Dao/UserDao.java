package com.thirumalaivasa.vehiclemanagement.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.thirumalaivasa.vehiclemanagement.Models.UserData;


@Dao
public interface UserDao {
    @Insert
    void insert(UserData userData);

    @Update
    void Update(UserData userData);

    @Query("SELECT * FROM UserData LIMIT 1")
    UserData getUserData();

    // You can define other queries as needed
}
