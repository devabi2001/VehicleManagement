package com.thirumalaivasa.vehiclemanagement;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;
import com.thirumalaivasa.vehiclemanagement.Models.DriverData;
import com.thirumalaivasa.vehiclemanagement.Models.ExpenseData;
import com.thirumalaivasa.vehiclemanagement.Models.ImageData;
import com.thirumalaivasa.vehiclemanagement.Models.UserData;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;
import java.util.ArrayList;
import java.util.Collections;


public class HomeScreen extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private TextView navUserName;
    private ImageView navUserPic, homeUserPic;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    public UserData userData;
    public ArrayList<VehicleData> vehicleDataList;
    public ArrayList<ExpenseData> expenseDataList;
    public ArrayList<DriverData> driverDataList;
    //Fragments
    HomeFragment homeFragment;
    VehicleFragment vehicleFragment;
    DriverFragment driverFragment;
    ReportFragment reportFragment;
    private static int selectedFragmentId;
    //Bottom Navigation
    BottomNavigationView bottomNavigationView;

    private final String TAG = "VehicleManagement";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        findViews();
        initializeVariables();


        //Get Intent Values
        userData = getIntent().getParcelableExtra("UserData");
        vehicleDataList = getIntent().getParcelableArrayListExtra("VehicleData");
        expenseDataList = getIntent().getParcelableArrayListExtra("ExpenseData");
        driverDataList = getIntent().getParcelableArrayListExtra("DriverData");

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent;
            switch (id) {
                case R.id.nav_log_out:
                    mAuth.signOut();
                    intent = new Intent(HomeScreen.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;


//                    intent = new Intent(HomeScreen.this, ReportActivity.class);
//                    intent.putExtra("UserData", userData);
//                    intent.putParcelableArrayListExtra("ExpenseData", expenseDataList);
//                    intent.putParcelableArrayListExtra("VehicleData", vehicleDataList);
//                    intent.putParcelableArrayListExtra("DriverData", driverDataList);
//                    startActivity(intent);


                case R.id.nav_fuel_price:
                    intent = new Intent(HomeScreen.this, FuelPriceActivity.class);
                    startActivity(intent);
                    break;

            }
            return true;
        });

        //initializing the drawer menu design
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        selectedFragmentId = R.id.bottom_home;
        bottomNavigationView.setSelectedItemId(selectedFragmentId);
        bottomNavigationView.setBackground(null);


    }

    @Override
    protected void onResume() {
        super.onResume();
        drawerLayout.closeDrawers();
        String uid = FirebaseAuth.getInstance().getUid();
        if (userData != null) {
            navUserName.setText(userData.getUserName());
            setProfilePic();

            FirebaseFirestore database = FirebaseFirestore.getInstance();
            if (uid != null) {
                CollectionReference vehicleCollection = database.collection("Data").document(uid).collection("Vehicles");
                vehicleCollection.addSnapshotListener(MetadataChanges.INCLUDE, (value, error) -> {
                    if (value != null) {
                        if (value.size() != vehicleDataList.size()) {
                            vehicleDataList.clear();
                            vehicleDataList = new ArrayList<>();
                            vehicleDataList = (ArrayList<VehicleData>) value.toObjects(VehicleData.class);

                        }
                    }

//                    for (DocumentChange documentChange : value.getDocumentChanges()) {
//                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
//                            // Handle added document
//                            // You can access the changed document data using documentChange.getDocument().getData()
//                        } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
//                            // Handle modified document
//                            // You can access the changed document data using documentChange.getDocument().getData()
//                        } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
//                            // Handle removed document
//                            // You can access the changed document data using documentChange.getDocument().getData()
//                        }
//                    }

                });

                CollectionReference driverCollection = database.collection("Data").document(uid).collection("DriverData");
                driverCollection.addSnapshotListener(MetadataChanges.INCLUDE, (value, error) -> {
                    if (value != null) {
                        if (value.size() != driverDataList.size()) {
                            driverDataList.clear();
                            driverDataList = new ArrayList<>();
                            driverDataList = (ArrayList<DriverData>) value.toObjects(DriverData.class);
                        }
                    }
                });

                CollectionReference expenseCollection = database.collection("Data").document(uid).collection("Expense");
                expenseCollection.addSnapshotListener(MetadataChanges.INCLUDE, (value, error) -> {
                    if (value != null) {
                        if (value.size() != expenseDataList.size()) {
                            expenseDataList.clear();
                            expenseDataList = new ArrayList<>();
                            expenseDataList = (ArrayList<ExpenseData>) value.toObjects(ExpenseData.class);
                            Collections.sort(expenseDataList);
                        }
                    }
                });
            }

            assert uid != null;
            DocumentReference userCollection = database.collection("UserData").document(uid);
            userCollection.addSnapshotListener((value, error) -> {
                if (value != null) {
                    userData = new UserData();
                    userData = value.toObject(UserData.class);
                }
            });
        }

        navUserPic.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, ProfileActivity.class);
            intent.putExtra("UserData", userData);
            startActivity(intent);
            drawerLayout.closeDrawers();
        });

        homeUserPic.setOnClickListener(v -> {


            AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this, R.style.CustomAlertDialogStyle);
            View popupView = getLayoutInflater().inflate(R.layout.popup_image, null);
            ImageView imageView = popupView.findViewById(R.id.popup_image_view);
            // Set the image resource programmatically if needed
            if (ImageData.getImage("Profile") != null) {

                Glide.with(HomeScreen.this)
                        .load(ImageData.getImage("Profile"))
                        .into(imageView);
            } else {
                Glide.with(HomeScreen.this)
                        .load(R.drawable.person_24)
                        .into(imageView);
            }


            builder.setView(popupView);
            builder.setCancelable(true);



            AlertDialog alertDialog = builder.create();
            alertDialog.show();


        });


    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.bottom_home:

                selectedFragmentId = R.id.bottom_home;
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                return true;

            case R.id.bottom_vehicle:
                selectedFragmentId = R.id.bottom_vehicle;
                getSupportFragmentManager().beginTransaction().replace(R.id.container, vehicleFragment).commit();
                return true;

            case R.id.bottom_driver:
                selectedFragmentId = R.id.bottom_driver;
                getSupportFragmentManager().beginTransaction().replace(R.id.container, driverFragment).commit();
                return true;

            case R.id.bottom_report:
                selectedFragmentId = R.id.bottom_report;
                getSupportFragmentManager().beginTransaction().replace(R.id.container, reportFragment).commit();
                return true;


        }

        return false;
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {

        if (view.getId() == R.id.open_btm_sheet_btn) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
            bottomSheetDialog.show(getSupportFragmentManager(), "BottomSheet");
        }
    }

    private void setProfilePic() {

        if (ImageData.getImage("Profile") != null) {

            Glide.with(this)
                    .load(ImageData.getImage("Profile"))
                    .circleCrop()
                    .into(navUserPic);
            Glide.with(this)
                    .load(ImageData.getImage("Profile"))
                    .circleCrop()
                    .into(homeUserPic);
        } else {
            navUserPic.setBackgroundColor(Color.WHITE);
            Glide.with(this)
                    .load(R.drawable.person_24)
                    .circleCrop()
                    .into(navUserPic);
            Glide.with(this)
                    .load(R.drawable.person_24)
                    .circleCrop()
                    .into(homeUserPic);

        }


    }


    private void initializeVariables() {
        mAuth = FirebaseAuth.getInstance();

        //Initialize Array Varibles
        userData = new UserData();
        vehicleDataList = new ArrayList<>();
        expenseDataList = new ArrayList<>();

        //Initialize Fragment Objects
        homeFragment = new HomeFragment();
        vehicleFragment = new VehicleFragment();
        driverFragment = new DriverFragment();
        reportFragment = new ReportFragment();
    }


    private void findViews() {
        drawerLayout = findViewById(R.id.drawer);

        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navUserName = headerView.findViewById(R.id.nav_user_name);
        navUserPic = headerView.findViewById(R.id.nav_user_pic);
        homeUserPic = findViewById(R.id.user_pic_home);
        toolbar = findViewById(R.id.toolbar);

        bottomNavigationView = findViewById(R.id.bottomNavView);
    }

}