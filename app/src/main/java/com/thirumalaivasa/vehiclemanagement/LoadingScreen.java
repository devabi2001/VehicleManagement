package com.thirumalaivasa.vehiclemanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thirumalaivasa.vehiclemanagement.Dao.DriverDao;
import com.thirumalaivasa.vehiclemanagement.Dao.ExpenseDao;
import com.thirumalaivasa.vehiclemanagement.Dao.UserDao;
import com.thirumalaivasa.vehiclemanagement.Dao.VehicleDao;
import com.thirumalaivasa.vehiclemanagement.Helpers.ImageHelper;
import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Models.ImageData;
import com.thirumalaivasa.vehiclemanagement.Models.UserData;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class LoadingScreen extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private final String TAG = "VehicleManagement";
    private Intent intent;
    private List<VehicleData> vehicleData;
    private List<ExpenseData> expenseData;
    private List<DriverData> driverData;
    Timer timer;

    RoomDbHelper dbHelper;
    private boolean isNetworkAvail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading_screen);

        dbHelper = RoomDbHelper.getInstance(this);
        ImageView imageView = (ImageView) findViewById(R.id.car_image_view);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.move_forward);
        imageView.startAnimation(fadeInAnimation);

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(networkRequest, networkCallback);

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        boolean isLocalDataAvail = sharedPreferences.getBoolean("Is_Local_DB_Avail", false);
        String dbUpdateDate = sharedPreferences.getString("Last_Update_Data","2023-01-01");
        //If the changed made in local and not updated to firebase
        boolean isChangesMade = sharedPreferences.getBoolean("Is_Changes_Made",false);
        long dayDiff = compareDate(dbUpdateDate);
        boolean doUpdateReq = false;
        if(dayDiff > 1)
            doUpdateReq = true;
        if(isNetworkAvail){
            if(isChangesMade){
                //Update the firebase data
                //Changed data need to be stored seperately or copy to upload withou duplicating
            }
            if(doUpdateReq){
                //Download the data
                deleteCache();
                downloadData();
            }else{
                if(isLocalDataAvail){
                    loadActivity();
                }else{
                    //Download the data
                    downloadData();
                }
            }

        }else{
            //Use the localDb
            if(isLocalDataAvail){
                loadActivity();
            }else{
                //Show Internet Required Screen
                Toast.makeText(LoadingScreen.this, "Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.no_internet_layout);
                TextView refreshTv = findViewById(R.id.refresh_text_view);
                refreshTv.setOnClickListener(v -> recreate());
            }
        }




    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (timer != null)
            timer.cancel();
    }


    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            isNetworkAvail = true;
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            isNetworkAvail = false;
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            isNetworkAvail = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);

        }
    };

    private void downloadData(){

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseFirestore.getInstance();

        String uid = mAuth.getUid();


        vehicleData = new ArrayList<>();
        expenseData = new ArrayList<>();
        driverData = new ArrayList<>();

        Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(100);
                } catch (Exception e) {
                    Log.e(TAG, "Loading Screen", e);
                } finally {

                    if (mAuth.getCurrentUser() != null) {

                        if (mAuth.getCurrentUser().isEmailVerified()) {

                            getUserData(uid);

                        } else {


                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoadingScreen.this, "E-Mail is not verified", Toast.LENGTH_LONG).show();
                                }
                            });
                            intent = new Intent(LoadingScreen.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {

                        startActivity(new Intent(LoadingScreen.this, LoginActivity.class));
                        finish();
                    }
                }
            }
        };

        welcomeThread.start();

    }

    private long compareDate(String date){
        LocalDate givenDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        return ChronoUnit.DAYS.between(givenDate,LocalDate.now());

    }

    private void getUserData(String uid) {
        database.collection("UserData").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot result = task.getResult();
                        UserDao userDao = dbHelper.userDao();
                        UserData userData = result.toObject(UserData.class);
                        userDao.insert(userData);
                        downloadProfilePic();
                        getVehicleData(mAuth.getUid());
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(LoadingScreen.this, "User Might be removed Try Logging In Again", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoadingScreen.this, LoginActivity.class));
                    finish();
                });
    }

    private void downloadProfilePic() {


        SharedPreferences imagePreferences = getSharedPreferences("Images", MODE_PRIVATE);
        String profilePath = imagePreferences.getString("Profile", null);
        String logoPath = imagePreferences.getString("CompanyLogo", null);
        StorageReference storageRefProfile = FirebaseStorage.getInstance().getReference().child(mAuth.getUid() + "/profile.jpg");
        if (profilePath == null) {

            new ImageHelper().downloadPicture(LoadingScreen.this, "Profile", storageRefProfile)
                    .addOnSuccessListener(bitmap -> ImageData.setImage("Profile", bitmap));
        } else {
            Bitmap bitmap;
            File imageFile = new File(profilePath);
            if (imageFile.exists()) {

                bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                if (bitmap != null) {
                    ImageData.setImage("Profile", bitmap);
                }

            } else {
                new ImageHelper().downloadPicture(LoadingScreen.this, "Profile", storageRefProfile)
                        .addOnSuccessListener(new OnSuccessListener<Bitmap>() {
                            @Override
                            public void onSuccess(Bitmap bitmap) {
                                ImageData.setImage("Profile", bitmap);
                            }
                        });
            }
        }
        StorageReference storageRefLogo = FirebaseStorage.getInstance().getReference().child(mAuth.getUid() + "/CompanyLogo.jpg");
        if (logoPath == null) {
            new ImageHelper().downloadPicture(LoadingScreen.this, "CompanyLogo", storageRefLogo)
                    .addOnSuccessListener(new OnSuccessListener<Bitmap>() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            ImageData.setImage("CompanyLogo", bitmap);
                        }
                    });
        } else {
            Bitmap bitmap;
            File imageFile = new File(logoPath);
            if (imageFile.exists()) {

                bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                if (bitmap != null) {
                    ImageData.setImage("CompanyLogo", bitmap);
                }

            } else {
                new ImageHelper().downloadPicture(LoadingScreen.this, "CompanyLogo", storageRefLogo)
                        .addOnSuccessListener(new OnSuccessListener<Bitmap>() {
                            @Override
                            public void onSuccess(Bitmap bitmap) {
                                ImageData.setImage("CompanyLogo", bitmap);
                            }
                        });
            }
        }


    }

    private void deleteCache() {

        long timeThreshold = 24 * 60 * 60 * 1000;


        File cacheDir = getCacheDir();


        File[] cacheFiles = cacheDir.listFiles();

        if (cacheFiles != null) {

            for (File file : cacheFiles) {

                long currentTime = System.currentTimeMillis();
                long lastModifiedTime = file.lastModified();
                long timeDifference = currentTime - lastModifiedTime;

                if (timeDifference > timeThreshold) {

                    boolean isDeleted = file.delete();

                    if (isDeleted) {

                    } else {

                    }
                }
            }
        }

    }

    private void getVehicleData(String uid) {
        database.collection("Data").document(uid).collection("Vehicles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        vehicleData = (ArrayList<VehicleData>) result.toObjects(VehicleData.class);
                        VehicleDao vehicleDao = dbHelper.vehicleDao();
                        vehicleDao.insertAll(vehicleData);
                        for (VehicleData eachVehicle : vehicleData) {
                            SharedPreferences imagePreferences = getSharedPreferences("Images", MODE_PRIVATE);
                            String imagePath = imagePreferences.getString(eachVehicle.getRegistrationNumber(), null);
                            if (imagePath != null) {
                                File imageFile = new File(imagePath);
                                if (imageFile.exists()) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                                    if (bitmap != null) {
                                        ImageData.setImage(eachVehicle.getRegistrationNumber(), bitmap);
                                    }

                                }
                            }
                        }
                        getDriverData(uid);

                    } else {
                        Toast.makeText(LoadingScreen.this, "Can't able to retrieve data check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(LoadingScreen.this, "Can't able to retrieve data check your internet connection", Toast.LENGTH_SHORT).show());
    }


    private void getExpenseData(String uid) {
        database.collection("Data").document(uid).collection("Expense").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();

                        expenseData = (ArrayList<ExpenseData>) result.toObjects(ExpenseData.class);
                        ExpenseDao expenseDao = dbHelper.expenseDao();
                        expenseDao.insertAll(expenseData);
                        loadActivity();

                    }
                });
    }


    private void getDriverData(String uid) {
        database.collection("Data").document(uid).collection("DriverData")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();

                        driverData = (ArrayList<DriverData>) result.toObjects(DriverData.class);
                        DriverDao driverDao = dbHelper.driverDao();
                        driverDao.insertAll(driverData);
                        for (DriverData eachDriver : driverData) {
                            SharedPreferences imagePreferences = getSharedPreferences("Images", MODE_PRIVATE);
                            String imagePath = imagePreferences.getString(eachDriver.getDriverId(), null);
                            if (imagePath != null) {
                                File imageFile = new File(imagePath);
                                if (imageFile.exists()) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                                    if (bitmap != null) {
                                        ImageData.setImage(eachDriver.getDriverId(), bitmap);
                                    }

                                }
                            }
                        }
                        getExpenseData(uid);

                    } else {
                        Toast.makeText(LoadingScreen.this, "Can't able to retrieve data check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(LoadingScreen.this, "Can't able to retrieve data check your internet connection", Toast.LENGTH_SHORT).show());

    }


    private void loadActivity() {
        intent = new Intent(LoadingScreen.this, HomeScreen.class);
        startActivity(intent);
        finish();

    }


}