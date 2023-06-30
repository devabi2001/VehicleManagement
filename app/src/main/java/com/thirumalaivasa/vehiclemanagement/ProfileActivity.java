package com.thirumalaivasa.vehiclemanagement;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thirumalaivasa.vehiclemanagement.Helpers.ImageHelper;
import com.thirumalaivasa.vehiclemanagement.Models.ImageData;
import com.thirumalaivasa.vehiclemanagement.Models.UserData;

public class ProfileActivity extends AppCompatActivity {

    private UserData userData;
    private ImageView profilePic,companyLogoPic;
    private ImageButton backBtn,editBtn;
    private Button logoutBtn;
    private TextView userName, email, contact, travelsName;
    private Uri imageUri;
    private final String TAG = "VehicleManagement";
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userData = getIntent().getParcelableExtra("UserData");
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

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,ProfileEditActivity.class);
                intent.putExtra("userData",userData);
                startActivity(intent);

            }
        });

    }

    private final ActivityResultLauncher<Intent> galleryActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            uploadImage();
                            ImageData.setImage("Profile", null);
                        }
                        setProfilePic();
                    }

                }
            }
    );

    private void setProfilePic() {
        if (ImageData.getImage("Profile") != null) {
            Glide.with(ProfileActivity.this)
                    .load(ImageData.getImage("Profile"))
                    .circleCrop()
                    .into(profilePic);
        } else {
            Glide.with(ProfileActivity.this)
                    .load(R.drawable.person_outline)
                    .circleCrop()
                    .into(profilePic);
        }
        if (ImageData.getImage("CompanyLogo") != null) {
            Glide.with(ProfileActivity.this)
                    .load(ImageData.getImage("CompanyLogo"))
                    .into(companyLogoPic);
        } else {
         companyLogoPic.setVisibility(View.GONE);
        }
    }

    private void uploadImage() {

        String uid = userData.getUid();

        String fileName = uid + "/profile.jpg";
        StorageReference picRef = storageReference.child(fileName);
        Task<Boolean> uploadTask = new ImageHelper().uploadPicture(ProfileActivity.this, imageUri, picRef);
        uploadTask.addOnSuccessListener(aBoolean -> {
            new ImageHelper().savePicture(ProfileActivity.this, imageUri, "Profile");
            setProfilePic();
        });

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