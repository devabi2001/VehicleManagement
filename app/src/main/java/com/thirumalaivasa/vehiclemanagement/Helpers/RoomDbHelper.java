package com.thirumalaivasa.vehiclemanagement.Helpers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.thirumalaivasa.vehiclemanagement.Dao.DriverDao;
import com.thirumalaivasa.vehiclemanagement.Dao.ExpenseDao;
import com.thirumalaivasa.vehiclemanagement.Dao.UserDao;
import com.thirumalaivasa.vehiclemanagement.Dao.VehicleDao;
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Models.UserData;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;

@Database(entities = {UserData.class, VehicleData.class, ExpenseData.class, DriverData.class}, version = 3, exportSchema = false)
public abstract class RoomDbHelper extends RoomDatabase {
    private static volatile RoomDbHelper INSTANCE;
    public abstract UserDao userDao();
    public abstract VehicleDao vehicleDao();
    public abstract ExpenseDao expenseDao();
    public abstract DriverDao driverDao();
    public static RoomDbHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (RoomDbHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    RoomDbHelper.class,
                                    "app_database"
                            ).fallbackToDestructiveMigration()
                            .allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void clearAllTables() {

    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(@NonNull DatabaseConfiguration databaseConfiguration) {
        return null;
    }


}
