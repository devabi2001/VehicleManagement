package com.thirumalaivasa.vehiclemanagement.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ExpenseData expenseData);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ExpenseData> expenseDataList);

    @Update
    void update(ExpenseData expenseData);

    @Delete
    void delete(ExpenseData expenseData);

    @Query("DELETE FROM ExpenseData")
    void deleteAllExpenses();

    @Query("SELECT * FROM ExpenseData ORDER BY timestamp DESC")
    List<ExpenseData> getAllExpenses();


    @Query("SELECT * FROM ExpenseData WHERE eId=:id")
    ExpenseData getExpenseById(String id);

    @Query("SELECT * FROM ExpenseData WHERE expenseType=:type ORDER BY timestamp DESC")
    List<ExpenseData> getAllExpenseByType(String type);

    @Query("SELECT * FROM ExpenseData WHERE vno=:vNo ORDER BY timestamp DESC")
    List<ExpenseData> getAllExpenseByVno(String vNo);

    @Query("SELECT * FROM ExpenseData WHERE vno=:vNo AND expenseType=:type ORDER BY timestamp DESC LIMIT :limit")
    List<ExpenseData> getFilteredExpenses(String vNo, String type, String limit);

    @Query("SELECT * FROM ExpenseData WHERE vno=:vNo AND expenseType=:type AND timestamp < :maxTimestamp ORDER BY timestamp DESC LIMIT :limit")
    List<ExpenseData> getExpenseAbove(String vNo, String type, long maxTimestamp, int limit);

    @Query("SELECT * FROM ExpenseData WHERE vno=:vNo AND expenseType=:type AND timestamp > :minTimestamp ORDER BY timestamp ASC LIMIT :limit")
    List<ExpenseData> getExpenseBelow(String vNo, String type, long minTimestamp, int limit);


    @Query("SELECT * FROM ExpenseData WHERE vno=:vNo AND expenseType=:type ORDER BY timestamp DESC")
    List<ExpenseData> getAllExpenseByVnoAndType(String vNo, String type);

    @Query("SELECT * FROM ExpenseData WHERE expenseType = :type AND vno = :vNo ORDER BY timestamp DESC")
    List<ExpenseData> getAllExpensesByTypeAndVno(String type, String vNo);

    @Query("SELECT * FROM ExpenseData WHERE isSynced=0")
    List<ExpenseData> getUnsyncedData();

    @Query("SELECT COUNT(*) FROM VehicleData")
    int getCount();

}
