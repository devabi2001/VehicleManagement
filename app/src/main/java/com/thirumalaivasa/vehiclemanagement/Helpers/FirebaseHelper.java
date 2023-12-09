package com.thirumalaivasa.vehiclemanagement.Helpers;

import static com.thirumalaivasa.vehiclemanagement.Utils.Util.TAG;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.thirumalaivasa.vehiclemanagement.Utils.DBUtils;

public class FirebaseHelper {
    private DocumentSnapshot userDataResult;
    private QuerySnapshot vehiclesDataResult;
    private QuerySnapshot driverDataResult;
    private QuerySnapshot expenseDataResult;

    public Task<Boolean> downloadAllData(Context context, String uid) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> userDataTask = database.collection("UserData").document(uid).get();
        Task<QuerySnapshot> vehiclesDataTask = database.collection("Data").document(uid).collection("Vehicles").get();
        Task<QuerySnapshot> driverDataTask = database.collection("Data").document(uid).collection("DriverData").get();
        Task<QuerySnapshot> expenseDataTask = database.collection("Data").document(uid).collection("Expense").get();

        Tasks.whenAllComplete(userDataTask, vehiclesDataTask, driverDataTask, expenseDataTask)
                .addOnCompleteListener(results -> {
                    boolean allTasksSuccessful = true;

                    for (Task<?> task : results.getResult()) {
                        if (!task.isSuccessful()) {
                            // Handle failure for each task individually
                            allTasksSuccessful = false;
                            Exception exception = task.getException();
                            if (exception == null)
                                exception = new Exception("Firebase download task failed");
                            if (task == userDataTask) {
                                taskCompletionSource.setResult(false);
                                Log.e(TAG, "[FirebaseDownloadHelper] Cant't able to download user data ", exception);
                                taskCompletionSource.setException(new Exception("User data not downloaded"));
                            } else if (task == vehiclesDataTask) {
                                Log.e(TAG, "[FirebaseDownloadHelper] Cant't able to download vehicle data ", exception);
                                taskCompletionSource.setException(new Exception("Vehicle data not downloaded"));
                            } else if (task == expenseDataTask) {
                                Log.e(TAG, "[FirebaseDownloadHelper] Cant't able to download expense data ", exception);
                                taskCompletionSource.setException(new Exception("Expense data not downloaded"));
                            } else if (task == driverDataTask) {
                                Log.e(TAG, "[FirebaseDownloadHelper] Cant't able to download driver data ", exception);
                                taskCompletionSource.setException(new Exception("Driver data not downloaded"));
                            }

                        } else {
                            if (task == userDataTask)
                                userDataResult = (DocumentSnapshot) task.getResult();
                            else if (task == vehiclesDataTask)
                                vehiclesDataResult = (QuerySnapshot) task.getResult();
                            else if (task == expenseDataTask)
                                expenseDataResult = (QuerySnapshot) task.getResult();
                            else if (task == driverDataTask)
                                driverDataResult = (QuerySnapshot) task.getResult();
                            DBUtils.setDbAvail(context, true);
                        }
                    }
                    if (allTasksSuccessful)
                        taskCompletionSource.setResult(true);
                }).addOnFailureListener(taskCompletionSource::setException);

        return taskCompletionSource.getTask();
    }


    public DocumentSnapshot getUserDataResult() {
        return userDataResult;
    }

    public QuerySnapshot getVehiclesDataResult() {
        return vehiclesDataResult;
    }

    public QuerySnapshot getDriverDataResult() {
        return driverDataResult;
    }

    public QuerySnapshot getExpenseDataResult() {
        return expenseDataResult;
    }

    public Task<Boolean> deleteDocumentById(CollectionReference collectionReference, String documentId) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        collectionReference.document(documentId).delete().addOnSuccessListener(unused -> {
            taskCompletionSource.setResult(true);
        }).addOnFailureListener(e -> {
            taskCompletionSource.setResult(false);
            taskCompletionSource.setException(e);
        });
        return taskCompletionSource.getTask();
    }

    public Task<Boolean> deleteDocumentsByIds(CollectionReference collectionReference, List<String> documentIds) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        WriteBatch batch = database.batch();
        for (String id : documentIds) {
            DocumentReference expenseRef = collectionReference.document(id);
            batch.delete(expenseRef);
        }
        batch.commit().addOnSuccessListener(task -> {

            taskCompletionSource.setResult(true);
        }).addOnFailureListener(e -> {
            taskCompletionSource.setResult(false);
            taskCompletionSource.setException(e);
        });
        return taskCompletionSource.getTask();
    }
}
