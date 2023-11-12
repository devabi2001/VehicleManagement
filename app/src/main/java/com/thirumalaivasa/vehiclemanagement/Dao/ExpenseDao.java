package com.thirumalaivasa.vehiclemanagement.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    void insert(ExpenseData expenseData);


    @Insert
    void insertAll(List<ExpenseData> driverDataList);

    @Update
    void update(ExpenseData expenseData);

    @Delete
    void delete(ExpenseData expenseData);
    @Query("DELETE FROM ExpenseData")
    void deleteAllExpenses();
    @Query("SELECT * FROM ExpenseData ORDER BY date DESC")
    List<ExpenseData> getAllExpenses(String order);

    @Query("SELECT * FROM ExpenseData WHERE eId=:id")
    ExpenseData getExpenseById(String id);

    @Query("SELECT * FROM ExpenseData WHERE expenseType=:type ORDER BY date DESC")
    List<ExpenseData> getAllExpenseByType(String type);

    @Query("SELECT * FROM ExpenseData WHERE vno=:vNo ORDER BY date DESC")
    List<ExpenseData> getAllExpenseByVno(String vNo);

    @Query("SELECT * FROM ExpenseData WHERE expenseType = :type AND vno = :vNo ORDER BY date DESC")
    List<ExpenseData> getAllExpensesByTypeAndVno(String type, String vNo);




}
