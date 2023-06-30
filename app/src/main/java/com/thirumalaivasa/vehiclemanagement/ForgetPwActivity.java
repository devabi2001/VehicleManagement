package com.thirumalaivasa.vehiclemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPwActivity extends AppCompatActivity {

    private EditText emailEt;
    private Button resetBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pw);
        mAuth = FirebaseAuth.getInstance();
        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();


        resetBtn.setOnClickListener(view -> {

            String email = emailEt.getText().toString();
            if(email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(ForgetPwActivity.this, "Enter Registered Email-Id", Toast.LENGTH_SHORT).show();
                emailEt.setError("Enter Valid Email-Id");
                emailEt.requestFocus();

            }else{
                progressBar.setVisibility(View.VISIBLE);
                requestPassChange(email);
            }
        });
    }

    private void requestPassChange(String email){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(ForgetPwActivity.this, "Check Your email to reset password", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(ForgetPwActivity.this, "Try again later", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.INVISIBLE);
        });
    }

    private void findViews(){
        emailEt = findViewById(R.id.email_fpw);
        resetBtn = findViewById(R.id.reset_pw_btn);
        progressBar = findViewById(R.id.progress_fpw);
    }
}