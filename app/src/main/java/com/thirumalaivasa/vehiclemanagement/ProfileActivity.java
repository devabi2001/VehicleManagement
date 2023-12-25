package com.thirumalaivasa.vehiclemanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thirumalaivasa.vehiclemanagement.Helpers.RoomDbHelper;
import com.thirumalaivasa.vehiclemanagement.Models.ImageData;
import com.thirumalaivasa.vehiclemanagement.Models.UserData;

public class ProfileActivity extends AppCompatActivity {

    private UserData userData;
    private ImageView profilePic,companyLogoPic;
    private ImageButton backBtn,editBtn;
    private Button logoutBtn;
    private TextView userName, email, contact, travelsName;
    private Uri imageUri;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        RoomDbHelper dbHelper = RoomDbHelper.getInstance(ProfileActivity.this);
        userData = dbHelper.userDao().getUserData();
        if (userData == null) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        findViews();
        storageReference = FirebaseStorage.getInstance().getReference();


    }

    @Override
    protected void onResume() {
        super.onResume();

        setProfilePic();
        userName.setText(userData.getUserName());
        email.setText(userData.getEmail());
        contact.setText(userData.getContact());
        travelsName.setText(userData.getTravelsName());

        backBtn.setOnClickListener(view -> finish());

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        profilePic.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this, R.style.CustomAlertDialogStyle);
            View popupView = getLayoutInflater().inflate(R.layout.popup_image, null);
            ImageView imageView = popupView.findViewById(R.id.popup_image_view);
            // Set the image resource programmatically if needed
            if (ImageData.getImage("Profile") != null) {

                Glide.with(ProfileActivity.this)
                        .load(ImageData.getImage("Profile"))
                        .into(imageView);
            } else {
                Glide.with(ProfileActivity.this)
                        .load(R.drawable.person_outline)
                        .into(imageView);
            }

            builder.setView(popupView);
            builder.setCancelable(true);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        companyLogoPic.setOnClickListener(view->{
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this, R.style.CustomAlertDialogStyle);
            View popupView = getLayoutInflater().inflate(R.layout.popup_image, null);
            ImageView imageView = popupView.findViewById(R.id.popup_image_view);
            // Set the image resource programmatically if needed
            if (ImageData.getImage("CompanyLogo") != null) {

                Glide.with(ProfileActivity.this)
                        .load(ImageData.getImage("CompanyLogo"))
                        .into(imageView);
            } else {
                Glide.with(ProfileActivity.this)
                        .load(R.drawable.person_outline)
                        .into(imageView);
            }

            builder.setView(popupView);
            builder.setCancelable(true);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
            startActivity(intent);

        });

    }

    private void setProfilePic() {
        Glide.with(ProfileActivity.this)
                .load(userData.getProfileImagePath())
                .placeholder(R.drawable.person_24)
                .error(R.drawable.person_24)
                .circleCrop()
                .into(profilePic);
        try {
            Glide.with(ProfileActivity.this)
                    .load(userData.getCompanyImagePath())
                    .into(companyLogoPic);
        } catch (Exception e) {
            companyLogoPic.setVisibility(View.INVISIBLE);
        }

    }

    private void findViews() {
        profilePic = findViewById(R.id.profile_pic_profile);

        backBtn = findViewById(R.id.back_btn_profile);
        editBtn = findViewById(R.id.edit_btn_profile);
        userName = findViewById(R.id.user_name_profile);
        email = findViewById(R.id.email_profile);
        contact = findViewById(R.id.contact_profile);
        travelsName = findViewById(R.id.travels_name_profile);
        companyLogoPic = findViewById(R.id.travels_logo_profile);
        logoutBtn = findViewById(R.id.log_out_btn);
    }
}