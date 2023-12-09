package com.thirumalaivasa.vehiclemanagement.Utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.thirumalaivasa.vehiclemanagement.Dao.DriverDao;
import com.thirumalaivasa.vehiclemanagement.Dao.ExpenseDao;
import com.thirumalaivasa.vehiclemanagement.Dao.UserDao;
import com.thirumalaivasa.vehiclemanagement.Dao.VehicleDao;
import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBUtils {

    private static final String[] modelTypes = {"Expense", "Vehicle", "Driver"};
    private static RoomDbHelper dbHelper;
    private final Context context;

    public DBUtils(Context context) {
        this.context = context;
    }

    public static void dbChanged(Context applicationContext, boolean value) {
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("Local_Db_Details", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Is_Local_Changed", value);
        String date = DateTimeUtils.getCurrentDateTime()[0];
        if (!value)
            editor.putString("Last_Update_Date", date);
        editor.apply();
    }

    public static void setDbAvail(Context context, boolean value) {
        SharedPreferences dbPreferences = context.getSharedPreferences("Local_Db_Details", MODE_PRIVATE);
        SharedPreferences.Editor editor = dbPreferences.edit();
        editor.putBoolean("Is_Local_Avail", value);
        editor.apply();
    }

    public static boolean isLocalDbAvail(Context context) {
        SharedPreferences dbPreferences = context.getSharedPreferences("Local_Db_Details", MODE_PRIVATE);
        return dbPreferences.getBoolean("Is_Local_Avail", false);
    }

    public static void addDeletedData(Context context, String type, String id) {
        List<String> modelList = Arrays.asList(modelTypes);
        if (!modelList.contains(type))
            return;
        SharedPreferences sharedPreferences = context.getSharedPreferences("Deleted_Data", MODE_PRIVATE);
        Set<String> stringSet;
        stringSet = sharedPreferences.getStringSet(type, null);
        if (stringSet == null)
            stringSet = new HashSet<>();
        stringSet.add(id);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(type, stringSet);
        editor.apply();
    }

    public static Set<String> getDeletedData(Context context, String type) {
        List<String> modelList = Arrays.asList(modelTypes);
        if (!modelList.contains(type))
            return null;
        SharedPreferences sharedPreferences = context.getSharedPreferences("Deleted_Data", MODE_PRIVATE);
        return sharedPreferences.getStringSet(type, null);
    }

    public static void removeDeletedData(Context context, String type) {
        List<String> modelList = Arrays.asList(modelTypes);
        if (!modelList.contains(type))
            return;
        SharedPreferences sharedPreferences = context.getSharedPreferences("Deleted_Data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(type, null);
        editor.apply();

    }
}
