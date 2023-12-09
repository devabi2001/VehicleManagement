package com.thirumalaivasa.vehiclemanagement.Helpers;

import static com.thirumalaivasa.vehiclemanagement.Utils.Util.TAG;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.thirumalaivasa.vehiclemanagement.Dao.UserDao;
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Models.UserData;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;
import com.thirumalaivasa.vehiclemanagement.Utils.DBUtils;
import com.thirumalaivasa.vehiclemanagement.ViewDriverActivity;

import java.util.List;
import java.util.Set;

public class FirebaseUploadService extends Service {
    FirebaseFirestore database;
    private UserData userData;
    private List<VehicleData> vehicleDataList;
    private List<ExpenseData> expenseDataList;
    private List<DriverData> driverDataList;
    private Set<String> expenseDeleteList;
    private Set<String> vehicleDeleteList;
    private Set<String> driverDeleteList;
    private String uid;
    private RoomDbHelper dbHelper;
    private int updateCounter = 0;
    private int totalUpdates = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database = FirebaseFirestore.getInstance();
        dbHelper = RoomDbHelper.getInstance(getApplicationContext());

        userData = dbHelper.userDao().getUnsyncedData();
        uid = userData.getUid();

        vehicleDataList = dbHelper.vehicleDao().getUnsyncedData();
        expenseDataList = dbHelper.expenseDao().getUnsyncedData();
        driverDataList = dbHelper.driverDao().getUnsyncedData();

        expenseDeleteList = DBUtils.getDeletedData(getApplicationContext(), "Expense");
        vehicleDeleteList = DBUtils.getDeletedData(getApplicationContext(), "Vehicle");
        driverDeleteList = DBUtils.getDeletedData(getApplicationContext(), "Driver");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (userData != null) {
            //update user  data
            totalUpdates++;
            updateUserData();
        }

        if (vehicleDataList != null && vehicleDataList.size() > 0) {
            //update vehicle datas
            totalUpdates++;
            updateVehicleData();
        }

        if (expenseDataList != null && expenseDataList.size() > 0) {
            //update expense datas
            totalUpdates++;
            updateExpenseData();
        }

        if (driverDataList != null && driverDataList.size() > 0) {
            //update driver datas
            totalUpdates++;
            updateDriverData();
        }

        if (expenseDeleteList != null && expenseDeleteList.size() > 0) {
            deleteExpenseData();
        }
        if (vehicleDeleteList != null && vehicleDeleteList.size() > 0) {
            deleteVehicleData();
        }
        if (driverDeleteList != null && driverDeleteList.size() > 0) {
            deleteDriverData();
        }

        return START_STICKY;
    }

    private void chkAllUpdated() {
        updateCounter++;
        if (updateCounter == totalUpdates) {
            DBUtils.dbChanged(getApplicationContext(), false);
        }
    }

    private void updateUserData() {
        if (uid == null || uid.trim().length() == 0)
            return;
        database.collection("UserData").document(uid).set(userData)
                .addOnCompleteListener(task12 -> {
                    dbHelper.userDao().Update(userData);
                    chkAllUpdated();
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "[FirebaseUploadService--updateUserData] " + e.getMessage());
                });
    }

    private void updateVehicleData() {
        if (uid == null || uid.trim().length() == 0)
            return;
        WriteBatch batch = database.batch();
        for (VehicleData vehicleData : vehicleDataList) {
            DocumentReference document = database.collection("Data").document(uid).collection("Vehicles").document(vehicleData.getRegistrationNumber());
            batch.set(document, vehicleData);
        }

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (VehicleData vehicleData : vehicleDataList) {
                    vehicleData.setSynced(true);
                    dbHelper.vehicleDao().update(vehicleData);
                    chkAllUpdated();
                }
            } else {
                Log.e(TAG, "[FirebaseUploadService--updateVehicleData] Upload Failed: " + task.getException());
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "[FirebaseUploadService--updateVehicleData] Upload Failed: " + e.getMessage());
        });
    }

    private void updateExpenseData() {
        if (uid == null || uid.trim().length() == 0)
            return;
        WriteBatch batch = database.batch();
        for (ExpenseData expenseData : expenseDataList) {
            DocumentReference document = database.collection("Data").document(uid).collection("Expense").document(expenseData.geteId());
            batch.set(document, expenseData);
        }

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (ExpenseData expenseData : expenseDataList) {
                    expenseData.setSynced(true);
                    dbHelper.expenseDao().update(expenseData);
                    chkAllUpdated();
                }
            } else {
                Log.e(TAG, "[FirebaseUploadService--updateExpenseData] Upload Failed: " + task.getException());
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "[FirebaseUploadService--updateExpenseData] Upload Failed: " + e.getMessage());
        });
    }

    private void updateDriverData() {
        if (uid == null || uid.trim().length() == 0)
            return;
        WriteBatch batch = database.batch();
        for (DriverData driverData : driverDataList) {
            DocumentReference document = database.collection("Data").document(uid)
                    .collection("DriverData").document(driverData.getDriverId());
            batch.set(document, driverData);
        }

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DriverData driverData : driverDataList) {
                    driverData.setSynced(true);
                    dbHelper.driverDao().update(driverData);
                    chkAllUpdated();
                }
            } else {
                Log.e(TAG, "[FirebaseUploadService--updateDriverData] Upload Failed: " + task.getException());
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "[FirebaseUploadService--updateDriverData] Upload Failed: " + e.getMessage());
        });
    }

    private void deleteVehicleData() {
        WriteBatch batch = database.batch();
        for (String id : expenseDeleteList) {
            DocumentReference expenseRef = database.collection("Data")
                    .document(uid)
                    .collection("Vehicles")
                    .document(id);

            batch.delete(expenseRef);
        }
        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Remove from delete list
                DBUtils.removeDeletedData(getApplicationContext(), "Vehicle");
            }
        });

    }

    private void deleteExpenseData() {
        WriteBatch batch = database.batch();
        for (String id : expenseDeleteList) {
            DocumentReference expenseRef = database.collection("Data")
                    .document(uid)
                    .collection("Expense")
                    .document(id);

            batch.delete(expenseRef);
        }
        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Remove from delete list
                DBUtils.removeDeletedData(getApplicationContext(), "Expense");
            }
        });
    }

    private void deleteDriverData() {
        WriteBatch batch = database.batch();
        for (String id : expenseDeleteList) {
            DocumentReference expenseRef = database.collection("Data")
                    .document(uid)
                    .collection("DriverData")
                    .document(id);

            batch.delete(expenseRef);
        }
        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Remove from delete list
                DBUtils.removeDeletedData(getApplicationContext(), "Driver");
            }
        });

    }

}
