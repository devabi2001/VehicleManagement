package com.thirumalaivasa.vehiclemanagement.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    private FirebaseAuth mAuth;
    private FirebaseFirestore database;

    public FirebaseUtil() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
    }

    public void deleteData() {

    }
}
