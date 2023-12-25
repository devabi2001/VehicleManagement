package com.thirumalaivasa.vehiclemanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.Models.UserData;


public class HomeScreen extends AppCompatActivity {

    private TextView navUserName;
    private ImageView navUserPic;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private UserData userData;
    //Fragments
    private Fragment homeFragment, vehicleFragment, driverFragment, reportFragment;
    private Fragment currentFrag;
    //Bottom Navigation
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        findViews();
        initializeVariables();
        RoomDbHelper dbHelper = RoomDbHelper.getInstance(HomeScreen.this);
        userData = dbHelper.userDao().getUserData();

        //initializing the drawer menu design
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onResume() {
        super.onResume();
        drawerLayout.closeDrawers();
        if (userData != null) {
            navUserName.setText(userData.getUserName());
            setProfilePic();
        }

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

                case R.id.nav_fuel_price:
                    intent = new Intent(HomeScreen.this, FuelPriceActivity.class);
                    startActivity(intent);
                    break;
            }
            return true;
        });


        bottomNavigationView.setOnItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (currentFrag != null)
                transaction.hide(currentFrag);
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    if (homeFragment == null) {
                        homeFragment = new HomeFragment();
                        transaction.add(R.id.container, homeFragment, "homeFragment");
                    }
                    currentFrag = homeFragment;
                    transaction.show(currentFrag);
                    break;

                case R.id.bottom_vehicle:
                    if (vehicleFragment == null) {
                        vehicleFragment = new VehicleFragment();
                        transaction.add(R.id.container, vehicleFragment, "vehicleFragment");
                    }
                    currentFrag = vehicleFragment;
                    transaction.show(currentFrag);
                    break;

                case R.id.bottom_driver:
                    if (driverFragment == null) {
                        driverFragment = new DriverFragment();
                        transaction.add(R.id.container, driverFragment, "driverFragment");
                    }
                    currentFrag = driverFragment;
                    transaction.show(currentFrag);
                    break;

                case R.id.bottom_report:
                    if (reportFragment == null) {
                        reportFragment = new ReportFragment();
                        transaction.add(R.id.container, reportFragment, "reportFragment");
                    }
                    currentFrag = reportFragment;
                    transaction.show(currentFrag);
                    break;


            }
            transaction.commit();
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        navUserPic.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, ProfileActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawers();
        });

//        homeUserPic.setOnClickListener(v -> {
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this, R.style.CustomAlertDialogStyle);
//            View popupView = getLayoutInflater().inflate(R.layout.popup_image, null);
//            ImageView imageView = popupView.findViewById(R.id.popup_image_view);
//            // Set the image resource programmatically if needed
//            if (ImageData.getImage("Profile") != null) {
//
//                Glide.with(HomeScreen.this)
//                        .load(ImageData.getImage("Profile"))
//                        .into(imageView);
//            } else {
//                Glide.with(HomeScreen.this)
//                        .load(R.drawable.person_24)
//                        .into(imageView);
//            }
//
//
//            builder.setView(popupView);
//            builder.setCancelable(true);
//
//
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//
//
//        });


    }


    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {

        if (view.getId() == R.id.open_btm_sheet_btn) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
            bottomSheetDialog.show(getSupportFragmentManager(), "BottomSheet");

        }
    }

    private void setProfilePic() {
        Glide.with(this)
                .load(userData.getProfileImagePath())
                .error(R.drawable.person_24)
                .placeholder(R.drawable.person_24)
                    .circleCrop()
                    .into(navUserPic);
    }


    private void initializeVariables() {
        mAuth = FirebaseAuth.getInstance();

        //Initialize Fragment Objects
//        homeFragment = new HomeFragment();
//        vehicleFragment = new VehicleFragment();
//        driverFragment = new DriverFragment();
//        reportFragment = new ReportFragment();
    }


    private void findViews() {
        drawerLayout = findViewById(R.id.drawer);

        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navUserName = headerView.findViewById(R.id.nav_user_name);
        navUserPic = headerView.findViewById(R.id.nav_user_pic);
        toolbar = findViewById(R.id.toolbar);

        bottomNavigationView = findViewById(R.id.bottomNavView);

    }

}