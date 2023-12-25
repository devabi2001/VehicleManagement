package com.thirumalaivasa.vehiclemanagement;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thirumalaivasa.vehiclemanagement.Helpers.ImageHelper;
import com.thirumalaivasa.vehiclemanagement.Models.UserData;

import java.io.IOException;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextView signInTv;
    private EditText userNameEt, emailEt, passwordEt, confirmPassEt, contactEt, travelsEt;
    private Button regBtn;
    private ImageView  profilePicBtn, travelsLogoBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    FirebaseUser user;
    private StorageReference storageReference;

    private String userName, email, password, confirmpw, contact, travelsName = "";

    private final String TAG = "VehicleManagement";

    private UserData userData;
    private Uri profileUri, logoUri;

    private int viewClicked = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViews();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected void onResume() {
        super.onResume();

        regBtn.setOnClickListener(view -> {

            boolean isValid = true;
            email = emailEt.getText().toString();
            userName = userNameEt.getText().toString();
            contact = contactEt.getText().toString();
            password = passwordEt.getText().toString();
            confirmpw = confirmPassEt.getText().toString();
            travelsName = travelsEt.getText().toString();

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEt.setError("Enter Valid E-mail");
                Toast.makeText(RegisterActivity.this, "Enter Valid E-mail", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if (password.isEmpty()) {
                emailEt.setError("Enter Valid Password");
                Toast.makeText(RegisterActivity.this, "Enter Valid Password", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if (!verifyPassword(password)) {
                isValid = false;
            }
            if (userName.isEmpty()) {
                userNameEt.setError("Enter User Name");
                Toast.makeText(RegisterActivity.this, "Enter User Name", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if (contact.isEmpty()) {
                contactEt.setError("Enter Contact");
                Toast.makeText(RegisterActivity.this, "Enter Contact", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if (!(confirmpw.equals(password))) {
                confirmPassEt.setError("Password Not Match");
                isValid = false;
            }
            if (!Patterns.PHONE.matcher(contact).matches()) {
                contactEt.setError("Enter Valid Contact");
                Toast.makeText(RegisterActivity.this, "Enter Valid Contact", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if (isValid) {
                progressBar.setVisibility(View.VISIBLE);
                isEmailUsed();
            }

        });



        profilePicBtn.setOnClickListener(view -> {
            viewClicked = 0;
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryActivity.launch(galleryIntent);
        });
        travelsLogoBtn.setOnClickListener(view -> {
            viewClicked = 1;
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryActivity.launch(galleryIntent);

        });

        signInTv.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
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
                            try {

                                if (viewClicked == 0) {
                                    profileUri = data.getData();
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(RegisterActivity.this.getContentResolver(), profileUri);
                                    Glide.with(RegisterActivity.this)
                                            .load(bitmap)
                                            .into(profilePicBtn);
                                } else if (viewClicked == 1) {
                                    logoUri = data.getData();
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(RegisterActivity.this.getContentResolver(), logoUri);
                                    Glide.with(RegisterActivity.this)
                                            .load(bitmap)
                                            .into(travelsLogoBtn);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                }
            }
    );



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //This method used to show a popup msg when password rules aren't met
//    private void showPopup(Activity context, Point p) {
//
//        LinearLayout viewGroup = context.findViewById(R.id.pw_info_popup_layout);
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View layout = layoutInflater.inflate(R.layout.password_info_popup, viewGroup);
//
//        PopupWindow popupWindow = new PopupWindow(context);
//        popupWindow.setContentView(layout);
//        popupWindow.setFocusable(true);
//        int OFFSET_X = 30;
//        int OFFSET_Y = 60;
//        popupWindow.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);
//
//    }

    //This method used to validate the password
    //The Password should be 8 characters
    //At-least 1 uppercase, 1 lowercase, 1 special char, 1 digit
    private boolean verifyPassword(String password) {

        if (password.length() < 8)
            return false;

        int upperCharCount = 0, lowerCharCount = 0, numCount = 0, specialCount = 0;


        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            if (Character.isLetter(ch)) {
                if (Character.isUpperCase(ch))
                    upperCharCount += 1;
                if (Character.isLowerCase(ch))
                    lowerCharCount += 1;
            } else {
                if (Character.isDigit(ch))
                    numCount += 1;
                if (!Character.isDigit(ch) && !Character.isLetter(ch) && !Character.isWhitespace(ch))
                    specialCount += 1;
            }
        }

        return (upperCharCount >= 1 && lowerCharCount >= 1 && numCount >= 1 && specialCount >= 1);
    }

    //This method check's whether the given email id already existed or not
    private void isEmailUsed() {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (Objects.requireNonNull(task.getResult().getSignInMethods()).size() == 0) {
                createAccount(email, password);
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "email existed", Toast.LENGTH_SHORT).show();
            }

        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(RegisterActivity.this, "Can't register check your internet connection", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        });

    }

    private void createAccount(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        user = mAuth.getCurrentUser();
                        mAuth = FirebaseAuth.getInstance();
                        String uid = mAuth.getUid();
                        if (uid == null || uid.isEmpty())
                            return;

                        userData = new UserData(uid, userName, email, contact, travelsName, true, 0, 0, null, null);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("UserData").document(uid).set(userData)
                                .addOnCompleteListener(task12 -> {
                                    if (task12.isSuccessful()) {
                                        user.sendEmailVerification().addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                uploadImage(mAuth.getUid());
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(RegisterActivity.this, "Check your E-Mail for verification", Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                                finish();
                                            }

                                        }).addOnFailureListener(e -> {
                                            e.printStackTrace();
                                            Toast.makeText(RegisterActivity.this, "Can't verify email try logging in to verify", Toast.LENGTH_SHORT).show();
                                            finish();
                                        });

                                    }
                                }).addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    Log.e(TAG, "Register Activity", e);
                                    Toast.makeText(RegisterActivity.this, "User can't added", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                    }
                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Register Activity", e);
                    mAuth.signOut();
                    finish();
                });

    }

    private void uploadImage(String uid) {
        if (profileUri != null) {
            String fileName = uid + "/profile.jpg";
            StorageReference picRef = storageReference.child(fileName);
            Task<String> uploadTask = new ImageHelper().uploadPicture(RegisterActivity.this, profileUri, picRef);
            uploadTask.addOnSuccessListener(aBoolean -> new ImageHelper().savePicture(RegisterActivity.this, profileUri, "Profile"));
        }
        if (logoUri != null) {
            String fileName = uid + "/CompanyLogo.jpg";
            StorageReference picRef = storageReference.child(fileName);
            Task<String> uploadTask = new ImageHelper().uploadPicture(RegisterActivity.this, logoUri, picRef);
            uploadTask.addOnSuccessListener(aBoolean -> new ImageHelper().savePicture(RegisterActivity.this, profileUri, "CompanyLogo"));
        }
    }

        private void findViews () {
            userNameEt = findViewById(R.id.user_name_reg);
            emailEt = findViewById(R.id.email_reg);
            passwordEt = findViewById(R.id.password_reg);
            contactEt = findViewById(R.id.contact_reg);
            confirmPassEt = findViewById(R.id.confirm_password_reg);
            travelsEt = findViewById(R.id.travels_name_reg);
            regBtn = findViewById(R.id.register_btn);
            progressBar = findViewById(R.id.progress_reg);
            profilePicBtn = findViewById(R.id.profile_pic_reg);
            travelsLogoBtn = findViewById(R.id.travels_logo_reg);

            signInTv = findViewById(R.id.sign_in_reg);
        }
    }