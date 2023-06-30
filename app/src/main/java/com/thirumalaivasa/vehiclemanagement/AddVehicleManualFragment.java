package com.thirumalaivasa.vehiclemanagement;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thirumalaivasa.vehiclemanagement.Helpers.ImageHelper;
import com.thirumalaivasa.vehiclemanagement.Models.ImageData;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AddVehicleManualFragment extends Fragment {

    private final String TAG = "VehicleManagement";

    private Button addBtn;
    private ImageButton backBtn;

    //Search Vehicle

    private ImageView addVehicleImgBtn;

    private ScrollView scrollView;
    //Vehicle Details in scrollView
    private EditText ownerNameEt, fatherNameEt, regNumEt, vClassEt, vManuEt, vModelEt, regDateEt, colorEt, engineNumEt, chassisNumEt, fuelTypeEt, fuelCapEt, fitnessEt, insuranceEt, taxEt, permitEt, puccEt;

    private VehicleData vehicleData;
    private int mode;

    private Uri imageUri;
    private String imageUrl = "";

    private FirebaseAuth mAuth;

    public AddVehicleManualFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_vehicle_manual, container, false);
        findViews(view);
        vehicleData = new VehicleData();

        mAuth = FirebaseAuth.getInstance();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mode = ((AddVehicleActivity)requireActivity()).mode;
        if(mode == 1){
            addBtn.setText("Add");
        } else if (mode == 2) {
            vehicleData = ((AddVehicleActivity)(requireActivity())).vehicleData;
            addBtn.setText("Update");
            setValues();
        }
        addBtn.setOnClickListener(v -> {
                getInputData();
                if (verifyData()) {
                    if (validateDates()) {
                        if (imageUri != null)
                            uploadImage();
                        addVehicle(vehicleData);
                    }
                }


        });

        backBtn.setOnClickListener(v -> {
            getActivity().finish();
        });

        addVehicleImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryActivity.launch(galleryIntent);
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
                        }
                    }
                    Glide.with(getContext())
                            .load(imageUri)
                            .circleCrop()
                            .into(addVehicleImgBtn);


                }
            }
    );



    private void getInputData() {
        //Owner Details
        vehicleData.setOwnerName(String.valueOf(ownerNameEt.getText()));
        vehicleData.setRegistrationNumber(String.valueOf(regNumEt.getText()));
        //Fuel
        vehicleData.setFuelType(String.valueOf(fuelTypeEt.getText()));
        vehicleData.setFuelCapacity(Integer.parseInt(String.valueOf(fuelCapEt.getText())));
        //Date's
        vehicleData.setFitnessValidity(String.valueOf(fitnessEt.getText()));
        vehicleData.setInsuranceValidity(String.valueOf(insuranceEt.getText()));
        vehicleData.setMvTaxValidity(String.valueOf(taxEt.getText()));
        vehicleData.setPermitValidity(String.valueOf(permitEt.getText()));
        vehicleData.setPucValidity(String.valueOf(puccEt.getText()));
        /* Nullable Values  */
        vehicleData.setFatherName(String.valueOf(fatherNameEt.getText()));
        //Vehicle Details
        vehicleData.setVehicleClass(String.valueOf(vClassEt.getText()));
        vehicleData.setManufacturer(String.valueOf(vManuEt.getText()));
        vehicleData.setManufacturerModel(String.valueOf(vModelEt.getText()));
        vehicleData.setRegistrationDate(String.valueOf(regDateEt.getText()));
        vehicleData.setColour(String.valueOf(colorEt.getText()));
        //Engine & Chassis
        vehicleData.setEngineNumber(String.valueOf(engineNumEt.getText()));
        vehicleData.setChassisNumber(String.valueOf(chassisNumEt.getText()));

        vehicleData.setVehiclePic(imageUrl);
    }

    private void setValues() {
        if (vehicleData.getOwnerName() != null)
            ownerNameEt.setText(vehicleData.getOwnerName());
        if (vehicleData.getRegistrationNumber() != null)
            regNumEt.setText(vehicleData.getRegistrationNumber());
        if (vehicleData.getFuelType() != null)
            fuelTypeEt.setText(vehicleData.getFuelType());
        if (vehicleData.getFuelCapacity() != 0)
            fuelCapEt.setText(String.valueOf(vehicleData.getFuelCapacity()));
        if (vehicleData.getFitnessValidity() != null)
            fitnessEt.setText(vehicleData.getFitnessValidity());
        if (vehicleData.getInsuranceValidity() != null)
            insuranceEt.setText(vehicleData.getInsuranceValidity());
        if (vehicleData.getMvTaxValidity() != null)
            taxEt.setText(vehicleData.getMvTaxValidity());
        if (vehicleData.getPermitValidity() != null)
            permitEt.setText(vehicleData.getPermitValidity());
        if (vehicleData.getPucValidity() != null)
            puccEt.setText(vehicleData.getPucValidity());
        if (vehicleData.getFatherName() != null)
            fatherNameEt.setText(vehicleData.getFatherName());
        if (vehicleData.getVehicleClass() != null)
            vClassEt.setText(vehicleData.getVehicleClass());
        if (vehicleData.getManufacturer() != null)
            vManuEt.setText(vehicleData.getManufacturer());
        if (vehicleData.getManufacturerModel() != null)
            vModelEt.setText(vehicleData.getManufacturerModel());
        if (vehicleData.getRegistrationDate() != null)
            regDateEt.setText(vehicleData.getRegistrationDate());
        if (vehicleData.getColour() != null)
            colorEt.setText(vehicleData.getColour());
        if (vehicleData.getEngineNumber() != null)
            engineNumEt.setText(vehicleData.getEngineNumber());
        if (vehicleData.getChassisNumber() != null)
            chassisNumEt.setText(vehicleData.getChassisNumber());

        if (ImageData.getImage(vehicleData.getRegistrationNumber()) != null) {
            Glide.with(getContext()).load(ImageData.getImage(vehicleData.getRegistrationNumber()))
                    .circleCrop().into(addVehicleImgBtn);
        }
    }

    private boolean verifyData() {
        boolean retValue = true;
        if (vehicleData.getOwnerName().isEmpty()) {
            ownerNameEt.setError("Enter Owner Name");
            retValue = false;
        }

        if (vehicleData.getRegistrationNumber().isEmpty()) {
            regNumEt.setError("Enter Vehicle Number");
            retValue = false;
        }

        if (vehicleData.getFuelType().isEmpty()) {
            fuelTypeEt.setError("Enter Fuel Type");
            retValue = false;
        }

        if (vehicleData.getFuelCapacity() == 0) {
            fuelCapEt.setError("Enter Fuel Capacity");
            retValue = false;
        }
        if (vehicleData.getFuelCapacity() < 0) {
            fuelCapEt.setError("Enter Valid Fuel Capacity");
            retValue = false;
        }

        if (vehicleData.getFitnessValidity().isEmpty()) {
            fitnessEt.setError("Enter Fitness Validity");
            retValue = false;
        }
        if (vehicleData.getInsuranceValidity().isEmpty()) {
            insuranceEt.setError("Enter Insurance Validity");
            retValue = false;
        }
        if (vehicleData.getMvTaxValidity().isEmpty()) {
            taxEt.setError("Enter Tax Validity");
            retValue = false;
        }
        if (vehicleData.getPermitValidity().isEmpty()) {
            permitEt.setError("Enter Permit Validity");
            retValue = false;
        }
        if (vehicleData.getPucValidity().isEmpty()) {
            puccEt.setError("Enter PUCC Validity");
            retValue = false;
        }

        return retValue;
    }

    private boolean validateDates() {
        boolean retValue = true;
        String[] dateFormat = {"dd/MM/yyyy","dd-MM-yyyy","yyyy-MM-dd","yyyy/MM/dd","yyyy-dd-MM","yyyy/dd/MM"};
        //Fitness Date
        if (!isValidDateFormat(vehicleData.getFitnessValidity(), dateFormat)) {
            fitnessEt.setError("Enter Valid Date");
            retValue = false;
        }
        //Insurance Date
        if (!isValidDateFormat(vehicleData.getInsuranceValidity(), dateFormat)) {
            insuranceEt.setError("Enter Valid Date");
            retValue = false;
        }
        //Tax Date
        if (!isValidDateFormat(vehicleData.getMvTaxValidity(), dateFormat)) {
            taxEt.setError("Enter Valid Date");
            retValue = false;
        }
        //Permit Date
        if (!isValidDateFormat(vehicleData.getPermitValidity(), dateFormat)) {
            permitEt.setError("Enter Valid Date");
            retValue = false;
        }
        //PUCC Date
        if (!isValidDateFormat(vehicleData.getPucValidity(), dateFormat)) {
            puccEt.setError("Enter Valid Date");
            retValue = false;
        }

        return retValue;
    }


    private boolean isValidDateFormat(String dateStr, String[] formatStrs) {
        for(String formatStr: formatStrs) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr);
                dateFormat.setLenient(false);
                Date date = dateFormat.parse(dateStr);
                if(date != null){
                    return true;
                }

            } catch (ParseException e) {

            }
        }
        return false;
    }

    private void addVehicle(VehicleData vehicleData) {

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String uid = mAuth.getUid();
        if (uid != null)
            database.collection("Data").document(uid).collection("Vehicles").document(vehicleData.getRegistrationNumber())
                    .set(vehicleData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity().getApplicationContext(), "Data Added", Toast.LENGTH_SHORT).show();
                            getActivity().finish();

                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Can't able to add data try again later", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getActivity().getApplicationContext(), "Can't able to add data try again later", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Adding Vehicle Failed ", e);
                        getActivity().finish();

                    });

    }
    private void uploadImage() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (imageUri != null) {
            String fileName = uid + "/vehicle/" + vehicleData.getRegistrationNumber() + ".jpg";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference picRef = storageReference.child(fileName);
            Task<Boolean> booleanTask = new ImageHelper().uploadPicture(getContext(), imageUri, picRef);
            booleanTask.addOnSuccessListener(new OnSuccessListener<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    new ImageHelper().savePicture(getContext(), imageUri, vehicleData.getRegistrationNumber());

                }
            });
        }
    }

    private void findViews(View view) {

        backBtn = view.findViewById(R.id.back_btn);
        addBtn = view.findViewById(R.id.add_vehicle_btn);

        addVehicleImgBtn = view.findViewById(R.id.add_vehicle_img);

        scrollView = view.findViewById(R.id.scroll_view);

        ownerNameEt = view.findViewById(R.id.owner_name_et);
        fatherNameEt = view.findViewById(R.id.father_name_et);
        regNumEt = view.findViewById(R.id.vehicle_license_et);
        vClassEt = view.findViewById(R.id.vehicle_class_et);
        vManuEt = view.findViewById(R.id.manufacturer_et);
        vModelEt = view.findViewById(R.id.vehicle_model_et);
        regDateEt = view.findViewById(R.id.registered_date_et);
        colorEt = view.findViewById(R.id.vehicle_color_et);
        engineNumEt = view.findViewById(R.id.engine_number_et);
        chassisNumEt = view.findViewById(R.id.chassis_number_et);
        fuelTypeEt = view.findViewById(R.id.fuel_type_et);
        fuelCapEt = view.findViewById(R.id.fuel_cap_et);
        fitnessEt = view.findViewById(R.id.fitness_et);
        insuranceEt = view.findViewById(R.id.insurance_et);
        taxEt = view.findViewById(R.id.tax_et);
        permitEt = view.findViewById(R.id.permit_et);
        puccEt = view.findViewById(R.id.pucc_et);

    }
}