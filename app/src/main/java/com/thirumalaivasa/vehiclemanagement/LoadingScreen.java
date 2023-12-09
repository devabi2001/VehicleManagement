package com.thirumalaivasa.vehiclemanagement;

import static com.thirumalaivasa.vehiclemanagement.Utils.Util.TAG;
import static com.thirumalaivasa.vehiclemanagement.Utils.Util.checkNetwork;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thirumalaivasa.vehiclemanagement.Dao.DriverDao;
import com.thirumalaivasa.vehiclemanagement.Dao.ExpenseDao;
import com.thirumalaivasa.vehiclemanagement.Dao.UserDao;
import com.thirumalaivasa.vehiclemanagement.Dao.VehicleDao;
import com.thirumalaivasa.vehiclemanagement.Helpers.FirebaseHelper;
import com.thirumalaivasa.vehiclemanagement.Helpers.FirebaseUploadService;
import com.thirumalaivasa.vehiclemanagement.Helpers.ImageHelper;
import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Models.ImageData;
import com.thirumalaivasa.vehiclemanagement.Models.UserData;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;
import com.thirumalaivasa.vehiclemanagement.Utils.DBUtils;

import java.io.File;
import java.util.List;
import java.util.Timer;


public class LoadingScreen extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private Timer timer;

    private RoomDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading_screen);

        dbHelper = RoomDbHelper.getInstance(this);
        ImageView imageView = findViewById(R.id.car_image_view);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.move_forward);
        imageView.startAnimation(fadeInAnimation);

        SharedPreferences sharedPreferences = getSharedPreferences("Local_Db_Details", Context.MODE_PRIVATE);
        boolean isLocalDataAvail = DBUtils.isLocalDbAvail(LoadingScreen.this);
//        String dbUpdateDate = sharedPreferences.getString("Last_Update_Date", "30-11-2023");
        //If the changed made in local and not updated to firebase
        boolean isLocalChanged = sharedPreferences.getBoolean("Is_Local_Changed", false);
        boolean isNetworkAvail = checkNetwork(getSystemService(ConnectivityManager.class));

        if (isNetworkAvail) {
            if (isLocalDataAvail) {
                if (isLocalChanged) {
                    Intent serviceIntent = new Intent(LoadingScreen.this, FirebaseUploadService.class);
                    startService(serviceIntent);
                }
                loadActivity();
            } else {
                downloadData();
            }
        } else {
            //Use the localDb
            if (isLocalDataAvail) {
                loadActivity();
            } else {
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


    private void downloadData() {
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseFirestore.getInstance();

        String uid = mAuth.getUid();
        Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();

                    if (mAuth.getCurrentUser() != null) {

                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            FirebaseHelper fdh = new FirebaseHelper();
                            fdh.downloadAllData(LoadingScreen.this, uid).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot userDataResult = fdh.getUserDataResult();
                                    UserDao userDao = dbHelper.userDao();
                                    UserData userData = userDataResult.toObject(UserData.class);
                                    userDao.insert(userData);
//                                        downloadProfilePic();
                                    QuerySnapshot vehiclesDataResult = fdh.getVehiclesDataResult();
                                    List<VehicleData> vehicleDataList = vehiclesDataResult.toObjects(VehicleData.class);
                                    VehicleDao vehicleDao = dbHelper.vehicleDao();
                                    vehicleDao.insertAll(vehicleDataList);
                                    for (VehicleData eachVehicle : vehicleDataList) {
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

                                    QuerySnapshot expenseDataResult = fdh.getExpenseDataResult();
                                    List<ExpenseData> expenseDataList = expenseDataResult.toObjects(ExpenseData.class);
                                    ExpenseDao expenseDao = dbHelper.expenseDao();
                                    expenseDao.insertAll(expenseDataList);
                                    QuerySnapshot driverDataResult = fdh.getDriverDataResult();
                                    List<DriverData> driverDataList = driverDataResult.toObjects(DriverData.class);
                                    DriverDao driverDao = dbHelper.driverDao();
                                    driverDao.insertAll(driverDataList);
                                    for (DriverData eachDriver : driverDataList) {
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
                                    loadActivity();
                                }
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "[LoadingScreen]: ", e);
                            });
                        } else {
                            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(LoadingScreen.this, "E-Mail is not verified", Toast.LENGTH_LONG).show());
                            startActivity(new Intent(LoadingScreen.this, LoginActivity.class));
                            finish();
                        }
                    } else {
                        startActivity(new Intent(LoadingScreen.this, LoginActivity.class));
                        finish();
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Loading Screen", e);
                }
            }
        };

        welcomeThread.start();

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

    private void loadActivity() {

        Intent intent = new Intent(LoadingScreen.this, HomeScreen.class);
        startActivity(intent);
        finish();

    }
}