package com.thirumalaivasa.vehiclemanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thirumalaivasa.vehiclemanagement.Helpers.ImageHelper;
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Models.ImageData;
import com.thirumalaivasa.vehiclemanagement.Models.UserData;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

//This is the launcher activity
//This activity gets the data from the firebase
//It displays a loading screen for until the data received
//If there's no internet connection error msg will be shown
public class LoadingScreen extends AppCompatActivity {

    //Instances for Firebase access
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;

    //Used to create a log
    private final String TAG = "VehicleManagement";

    //Used to start another activities
    private Intent intent;

    //Values that are retrives from firebase will be stored here
    private UserData userData;
    private ArrayList<VehicleData> vehicleData;
    private ArrayList<ExpenseData> expenseData;
    private ArrayList<DriverData> driverData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading_screen);
//        ImageView loadingImage = findViewById(R.id.loading_iamge);
//        Glide.with(this).asGif().load(R.raw.gear_loader).into(loadingImage);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        if (!connected) {

            Toast.makeText(LoadingScreen.this, "Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.no_internet_layout);
            TextView refreshTv = findViewById(R.id.refresh_text_view);
            refreshTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate();
                }
            });
        } else {

            //Initializing the firebase authentication variable
            mAuth = FirebaseAuth.getInstance();
            //Initializing the firebase database variable where all collections and documents stored
            database = FirebaseFirestore.getInstance();
            //Getting and Storing the uid of the user from firebase
            String uid = mAuth.getUid();

            //Initializing the vehicle and expense data
            vehicleData = new ArrayList<>();
            expenseData = new ArrayList<>();
            driverData = new ArrayList<>();
            deleteCache();

            //Thread used to retrieve data from firebase
            //Thread where the data is retrieved
            Thread welcomeThread = new Thread() {
                @Override
                public void run() {
                    try {
                        super.run();
                        sleep(100);
                    } catch (Exception e) {
                        Log.e(TAG, "Loading Screen", e);
                    } finally {
                        //Check's if the current user is logged in or not
                        if (mAuth.getCurrentUser() != null) {
                            //If logged then checks whether the email is verified or not
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                //If verified then call the getUserData() method
                                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                                getUserData(uid);

                            } else {
                                //If not then open the Login Page
                                //Toast msg will be shown to user
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
                            //If the user not logged in then start the login activity
                            startActivity(new Intent(LoadingScreen.this, LoginActivity.class));
                            finish();
                        }
                    }
                }
            };
            //Start the thread
            welcomeThread.start();
        }
    }

    //When back pressed the timer for loading animation need to be canceled
    @Override
    public void onBackPressed() {
        super.onBackPressed();
     /*   if (timer != null)
            timer.cancel();*/
    }


    //This method retrieves the user data on successfully retrieving the data downloadProfilePic() method will be called
    private void getUserData(String uid) {

        //Data stored on collection Named "UserData" inside document named with the user id
        database.collection("UserData").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot result = task.getResult();
                            userData = result.toObject(UserData.class);
                            downloadProfilePic();
                            getVehicleData(mAuth.getUid());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //On failure a Toast will be printed and login page will be started
                        //Failure might happen when the user removed from the database by admin or when there's a network issue on client side
                        //Network issue's need to resolved by checking the network before trying to retrive data
                        Toast.makeText(LoadingScreen.this, "User Might be removed Try Logging In Again", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoadingScreen.this, LoginActivity.class));
                        finish();
                    }
                });
    }

    //This method download's the profile picture of the user from the firebase storage
    //Either on successfull or on failure of retrieve the getVehicleData() method will be called
    //The profile pic value of userData will be set to null on failure
    private void downloadProfilePic() {
        //The name off the picture will be "profile.jpg" inside the path of uid

//        long startTime = System.nanoTime();

        SharedPreferences imagePreferences = getSharedPreferences("Images", MODE_PRIVATE);
        String profilePath = imagePreferences.getString("Profile", null);
        String logoPath = imagePreferences.getString("CompanyLogo", null);
        StorageReference storageRefProfile = FirebaseStorage.getInstance().getReference().child(mAuth.getUid() + "/profile.jpg");
        if (profilePath == null) {

            new ImageHelper().downloadPicture(LoadingScreen.this, "Profile", storageRefProfile)
                    .addOnSuccessListener(new OnSuccessListener<Bitmap>() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            ImageData.setImage("Profile", bitmap);
                        }
                    });
        } else {
            Bitmap bitmap;
            File imageFile = new File(profilePath);
            if (imageFile.exists()) {
                // Step 5: Decode the image file into a Bitmap object
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
                // Step 5: Decode the image file into a Bitmap object
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
//        long stopTime = System.nanoTime();
//        double exeTime  = (stopTime - startTime)/1_000_000_000.0;
//        Log.i(TAG, "downloadProfilePic: "+exeTime);


    }

    private void deleteCache() {
        // Assuming you have the Context object and the desired time threshold in milliseconds
        long timeThreshold = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

// Get the cache directory
        File cacheDir = getCacheDir();

// Get the list of files in the cache directory
        File[] cacheFiles = cacheDir.listFiles();

        if (cacheFiles != null) {
            // Iterate through each file in the cache directory
            for (File file : cacheFiles) {
                // Check if the file's last modified time is older than the desired threshold
                long currentTime = System.currentTimeMillis();
                long lastModifiedTime = file.lastModified();
                long timeDifference = currentTime - lastModifiedTime;

                if (timeDifference > timeThreshold) {
                    // Delete the file
                    boolean isDeleted = file.delete();

                    if (isDeleted) {
                        // File deleted successfully
                    } else {
                        // File deletion failed
                    }
                }
            }
        }

    }

    //This method retrieve vehicle data from firebase database
    //On success the object's will be stored in arraylist vehicleData
    //After that getExpenseData() method will be called
    //On failure toast msg will be printed further process will be stopped this need to be rectified
    private void getVehicleData(String uid) {
        database.collection("Data").document(uid).collection("Vehicles")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            vehicleData = (ArrayList<VehicleData>) result.toObjects(VehicleData.class);
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
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoadingScreen.this, "Can't able to retrieve data check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //This method used to retrieve expense data from firebase database
    //On success the object's will stored in  arraylist expenseData
    //After that the list will be sorted based on date & time by calling sort() method from Collections class
    //Pass the userData, vehicleData, expenseData to HomeScreen activity
    //Start the activity
    private void getExpenseData(String uid) {
        database.collection("Data").document(uid).collection("Expense").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();

                            expenseData = (ArrayList<ExpenseData>) result.toObjects(ExpenseData.class);
                            Collections.sort(expenseData);
                            loadActivity();

                        }
                    }
                });
    }

    //This method used to retrieve driver data from firebase database
    //On Success the object's will be stored in arraylist driverData
    //After that getExpenseData will be called
    private void getDriverData(String uid) {
        database.collection("Data").document(uid).collection("DriverData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();

                            driverData = (ArrayList<DriverData>) result.toObjects(DriverData.class);
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
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoadingScreen.this, "Can't able to retrieve data check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void loadActivity() {
        intent = new Intent(LoadingScreen.this, HomeScreen.class);
        intent.putExtra("UserData", userData);
        intent.putParcelableArrayListExtra("VehicleData", vehicleData);
        intent.putParcelableArrayListExtra("ExpenseData", expenseData);
        intent.putParcelableArrayListExtra("DriverData", driverData);
        startActivity(intent);
        finish();
    }


}


//Method that handles the animation
   /* public void setText(final String s) {
        final int[] i = new int[1];
        i[0] = 0;
        final int length = s.length();
        final Handler handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (i[0] == 3) {
                    loadingText.setText("Loading");
                    i[0] = 0;
                } else {
                    char c = s.charAt(i[0]);
                    loadingText.append(String.valueOf(c));
                    i[0]++;
                }
            }
        };

        timer = new Timer();
        TimerTask taskEverySplitSecond = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
                if (i[0] == length - 1) {
                    //timer.cancel();
                }
            }
        };
        timer.schedule(taskEverySplitSecond, 1, 500);
    }
*/