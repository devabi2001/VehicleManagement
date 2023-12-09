package com.thirumalaivasa.vehiclemanagement;

import static com.thirumalaivasa.vehiclemanagement.Utils.Util.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thirumalaivasa.vehiclemanagement.Utils.Util;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt,passwordEt;
    private Button loginBtn;
    private TextView forgotPwTv,registerNowTv;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loginBtn.setEnabled(true);
        loginBtn.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email,password;
            email = emailEt.getText().toString();
            password = passwordEt.getText().toString();
            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(LoginActivity.this, "Enter the credentials to login", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }else if( !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailEt.setError("Enter valid E-mail");
                Toast.makeText(LoginActivity.this, "Enter Valid E-mail", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
            else{

                progressBar.setVisibility(View.INVISIBLE);
                signIn(email,password);
                loginBtn.setEnabled(false);
            }
        });

        registerNowTv.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));

        forgotPwTv.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this,ForgetPwActivity.class)));

    }


    private void signIn(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String uid = mAuth.getUid();
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                database.collection("UserData").document(uid)
                        .get()
                        .addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                if(mAuth.getCurrentUser().isEmailVerified()) {
                                    Intent intent = new Intent(LoginActivity.this,LoadingScreen.class);
                                    startActivity(intent);
                                    finish();

                                }else{
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task11 -> {
                                        if(task11.isSuccessful()) {
                                            progressBar.setVisibility(View.GONE);
                                            loginBtn.setEnabled(true);
                                            Toast.makeText(LoginActivity.this, "Check your E-Mail for verification", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            progressBar.setVisibility(View.GONE);
                                            loginBtn.setEnabled(true);
                                            Toast.makeText(LoginActivity.this, "Can't verify email", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(e -> {
                                        e.printStackTrace();
                                        loginBtn.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this, "Can't verify email", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                        }).addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            loginBtn.setEnabled(true);
                            Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            Log.e(TAG, "Login Activity", e);
                        });
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "User don't have account", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            Log.e(TAG, "Login Activity", e);
        });
    }



    private void findViews(){
        emailEt = findViewById(R.id.email_login);
        passwordEt = findViewById(R.id.password_login);
        forgotPwTv = findViewById(R.id.forgot_login);
        registerNowTv = findViewById(R.id.register_login);

        progressBar = findViewById(R.id.progress_login);

        loginBtn = findViewById(R.id.login_btn);

    }





}