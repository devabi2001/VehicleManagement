package com.thirumalaivasa.vehiclemanagement;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thirumalaivasa.vehiclemanagement.Helpers.ImageHelper;
import com.thirumalaivasa.vehiclemanagement.Models.ImageData;
import com.thirumalaivasa.vehiclemanagement.Models.UserData;

public class ProfileEditActivity extends AppCompatActivity {

    private UserData userData;

    private EditText userNameEt, emailEt, contactEt, companyNameEt;
    private ImageView profileIv, companyLogoIv;
    private Button updateBtn;
    private ImageButton backBtn;
    private Uri imageUri;
    private  StorageReference storageReference;
    private boolean isProfileChanged=false,isLogoChanged=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        userData = getIntent().getParcelableExtra("userData");
        if (userData == null)
            finish();
        findViews();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setValues();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                updateData();
                if(isProfileChanged)
                    uploadImage("profile");
                if(isLogoChanged)
                    uploadImage("CompanyLogo");
            }
        });

        profileIv.setOnClickListener(v ->{
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            profileResult.launch(galleryIntent);
        });

        companyLogoIv.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            logoResult.launch(galleryIntent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private final ActivityResultLauncher<Intent> profileResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            isProfileChanged = true;
                            imageUri = data.getData();
                            Glide.with(ProfileEditActivity.this)
                                    .load(imageUri)
                                    .into(profileIv);
                        }

                    }

                }
            }
    );
    private final ActivityResultLauncher<Intent> logoResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            isLogoChanged = true;
                            imageUri = data.getData();
                            Glide.with(ProfileEditActivity.this)
                                    .load(imageUri)
                                    .into(companyLogoIv);
                        }

                    }

                }
            }
    );

    private void getData(){
        userData.setUserName(userNameEt.getText().toString());
        userData.setContact(contactEt.getText().toString());
        userData.setTravelsName(companyNameEt.getText().toString());
    }

    private void uploadImage(String file) {

        String uid = userData.getUid();

        String fileName = uid + "/"+file+".jpg";
        StorageReference picRef = storageReference.child(fileName);
        Task<Boolean> uploadTask = new ImageHelper().uploadPicture(ProfileEditActivity.this, imageUri, picRef);
        uploadTask.addOnSuccessListener(aBoolean -> {
            new ImageHelper().savePicture(ProfileEditActivity.this, imageUri, file);
        });

    }
    private void updateData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection("UserData").document(uid).set(userData)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(ProfileEditActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(ProfileEditActivity.this, "Profile Not Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    });

        }
    }

    private void setValues() {
        userNameEt.setText(userData.getUserName());
        emailEt.setText(userData.getEmail());
        contactEt.setText(userData.getContact());
        companyNameEt.setText(userData.getTravelsName());

        if (ImageData.getImage("Profile") != null) {
            Glide.with(ProfileEditActivity.this)
                    .load(ImageData.getImage("Profile"))
                    .into(profileIv);
        } else {
            Glide.with(ProfileEditActivity.this)
                    .load(R.drawable.add_photo_24)
                    .into(profileIv);
        }
        if (ImageData.getImage("CompanyLogo") != null) {
            Glide.with(ProfileEditActivity.this)
                    .load(ImageData.getImage("CompanyLogo"))
                    .into(companyLogoIv);
        } else {
            Glide.with(ProfileEditActivity.this)
                    .load(R.drawable.add_photo_24)
                    .into(companyLogoIv);
        }
    }

    private void findViews() {
        userNameEt = findViewById(R.id.user_name_profile_edit);
        emailEt = findViewById(R.id.email_profile_edit);
        contactEt = findViewById(R.id.contact_profile_edit);
        companyNameEt = findViewById(R.id.company_name_profile_edit);

        profileIv = findViewById(R.id.profile_pic_profile_edit);
        companyLogoIv = findViewById(R.id.company_logo_profile_edit);

        updateBtn = findViewById(R.id.update_btn);

        backBtn = findViewById(R.id.back_btn_profile_edit);
    }
}