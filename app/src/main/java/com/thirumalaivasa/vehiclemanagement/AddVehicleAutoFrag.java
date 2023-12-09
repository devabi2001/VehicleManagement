package com.thirumalaivasa.vehiclemanagement;

import static com.thirumalaivasa.vehiclemanagement.Utils.Util.TAG;

import android.app.Activity;
import android.app.Dialog;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.thirumalaivasa.vehiclemanagement.Models.VehicleData;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class AddVehicleAutoFrag extends Fragment {
    //Widgets
    //OnToolBar
    private Button addBtn, manualBtn;
    private ImageButton backBtn;

    //Search Vehicle
    private EditText vehicleNumEt;
    private ImageView searchBtn,addVehicleImgBtn;
    //DialogBox
    private EditText engineNumConfirmEt;

    private ScrollView scrollView;
    //Vehicle Details in scrollView
    private EditText ownerNameEt, fatherNameEt, regNumEt, vClassEt, vManuEt, vModelEt, regDateEt, colorEt, engineNumEt, chassisNumEt, fuelTypeEt, fuelCapEt, fitnessEt, insuranceEt, taxEt, permitEt, puccEt;

    private String responseValue;
    boolean isVehicleVerified = false;

    boolean isManual = false;

    private VehicleData vehicleData;

    private Uri imageUri;

//    public static final String API_KEY = "9b124b06d0msh9d3dede722f81bfp1171f7jsnbd08df500278";
//    //Url need to be changed if location based prices required
//    public static final String BASE_URL = "https://vehicle-rc-information.p.rapidapi.com/api/";
//    private static final String API_ENDPOINT = "https://vehicle-rc-information.p.rapidapi.com/";

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_vehicle_auto, container, false);
        findViews(view);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        mAuth = FirebaseAuth.getInstance();

        searchBtn.setOnClickListener(v -> {

            try {
                String vehicleNum = vehicleNumEt.getText().toString();
                if (vehicleNum.isEmpty()) {
                    Toast.makeText(getContext(), "Enter Vehicle Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                vehicleNum = vehicleNum.toUpperCase();
                vehicleNum.replaceAll(" ", "");
                Pattern p = Pattern.compile("^[A-Z]{2}\\d{2}[A-Z]{1,2}\\d{4}$");
                Matcher m = p.matcher(vehicleNum);
                if (m.matches()) {
                    //responseValue = "{\"RapidAPI\":\"https:\\/\\/rapidapi.com\\/fatehbrar92\\/api\\/vehicle-rc-information\",\"result\":{\"npermit_issued_by\":null,\"variant\":null,\"current_address\":\"VILLAGE MOOSA, DISTRICT, MANSA, Mansa, Punjab, 151508\",\"permit_no\":null,\"status\":\"id_found\",\"is_financed\":null,\"noc_details\":null,\"father_name\":\"BALKAUR SINGH\",\"noc_valid_upto\":null,\"registration_date\":\"2017-11-24\",\"colour\":\"S WHITE\",\"puc_number\":\"HR06901890001502\",\"registered_place\":\"SANGRUR RTA, Punjab\",\"seating_capacity\":\"7\",\"mv_tax_upto\":\"31-Oct-2032\",\"norms_type\":null,\"body_type\":\"SALOON\",\"owner_serial_number\":\"2\",\"wheelbase\":\"0\",\"fitness_upto\":\"2032-11-23\",\"financer\":\"\",\"fuel_type\":\"DIESEL\",\"puc_valid_upto\":\"2024-02-13\",\"status_verification\":\"ACTIVE\",\"npermit_no\":null,\"npermit_upto\":null,\"manufacturer_model\":\"FORTUNER 2WD 2.8L 6MT\",\"permit_issue_date\":null,\"state\":null,\"cubic_capacity\":\"2755\",\"vehicle_class\":\"LMV\",\"insurance_validity\":\"2024-02-23\",\"noc_issue_date\":null,\"owner_name\":\"SHUBHDEEP SINGH\",\"manufacturer\":\"TOYOTA KIRLOSKAR MOTOR PVT LTD\",\"vehicle_category\":\"LMV\",\"permanent_address\":\"VILLAGE MOOSA, DISTRICT, MANSA, Mansa, Punjab, 151508\",\"insurance_name\":\"Tata AIG General Insurance Co. Ltd.\",\"owner_mobile_no\":\"6280645181\",\"unladden_weight\":\"2135\",\"chassis_number\":\"MBJGA3GS8003748230817\",\"engine_number\":\"1GDA123541\",\"blacklist_status\":null,\"permit_validity_upto\":null,\"permit_validity_from\":null,\"status_verfy_date\":\"2023-03-18\",\"masked_name\":false,\"insurance_policy_no\":\"\",\"m_y_manufacturing\":\"8\\/2017\",\"number_of_cylinder\":\"4\",\"gross_vehicle_weight\":\"0\",\"registration_number\":\"PB65AM0008\",\"sleeper_capacity\":null,\"standing_capacity\":null,\"status_message\":null,\"permit_type\":null,\"noc_status\":null}}\n";
                    searchVehicle(vehicleNum);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });


        addBtn.setOnClickListener(v -> {
            if (!isVehicleVerified) {
                return;
            }
            int fuelCap = Integer.parseInt(fuelCapEt.getText().toString());


            if (fuelCapEt.getText().toString().isEmpty() || fuelCap == 0) {
                Toast.makeText(getContext(), "Enter fuel capacity", Toast.LENGTH_SHORT).show();
                fuelCapEt.setError("Enter fuel capacity");
                return;
            }

            if (isManual) {
                getInputData();
                if (vehicleData.getRegistrationNumber().isEmpty()) {
                    Toast.makeText(getContext(), "Enter  Vehicle Number", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            addVehicle(vehicleData, fuelCap);

        });

        backBtn.setOnClickListener(v -> {
            getActivity().finish();
        });

        addVehicleImgBtn.setOnClickListener(v -> {

            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryActivity.launch(galleryIntent);
        });

    }

    private final ActivityResultLauncher<Intent> galleryActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            addBtn.setEnabled(false);
                            uploadImage();
                        }
                    }
                    Glide.with(getContext())
                            .load(imageUri)
                            .circleCrop()
                            .into(addVehicleImgBtn);

                }
            }
    );


    private void uploadImage() {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String uid = mAuth.getUid();
        if (imageUri != null) {
            String fileName = uid + "/vehicle/"+vehicleData.getRegistrationNumber()+".jpg";
            StorageReference picRef = storageReference.child(fileName);

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (bmp != null) {
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            }
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = picRef.putBytes(data);
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful())

                    picRef.getDownloadUrl().addOnCompleteListener(task1 -> {
                        Uri result = task1.getResult();
                        vehicleData.setVehiclePic(String.valueOf(result));
                        Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        addBtn.setEnabled(true);
                    });
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                addBtn.setEnabled(true);
            });

        }
    }


    private synchronized void searchVehicle(@NonNull String vehicleNum) throws UnsupportedEncodingException {

        AsyncHttpClient client = new AsyncHttpClient();

        StringEntity entity = new StringEntity("{\"VehicleNumber\": \"" + vehicleNum + "\"}");
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        client.addHeader("X-RapidAPI-Key", "9b124b06d0msh9d3dede722f81bfp1171f7jsnbd08df500278");
        client.addHeader("X-RapidAPI-Host", "vehicle-rc-information.p.rapidapi.com");

        client.post(getContext(), "https://vehicle-rc-information.p.rapidapi.com/", entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Handle successful responseSystem.out.println(new String(responseBody));
                if (statusCode == 200) {
                    responseValue = new String(responseBody);
                    verifyResponseData();
                } else {
                    Toast.makeText(getContext(), "Failed to get vehicle data Enter Data Manually", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed To get response from API: Code: " + statusCode);
                    enterManually();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Handle error response
                Toast.makeText(getContext(), "Failed to get vehicle data Enter Data Manually", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed To get response from API: Code: " + statusCode);
                enterManually();
            }
        });

    }

    private VehicleData createVehicleData(Map<String, Object> givenVehicleData, int fuelCap) {
        VehicleData vehicleData = new VehicleData();
        vehicleData.setRegistrationNumber(givenVehicleData.get("registration_number").toString().replaceAll("(?<=[a-zA-Z])(?=\\d)|(?<=\\d)(?=[a-zA-Z])", " "));


        vehicleData.setChassisNumber(String.valueOf(givenVehicleData.get("chassis_number")));
        vehicleData.setEngineNumber(String.valueOf(givenVehicleData.get("engine_number")));
        vehicleData.setManufacturer(String.valueOf(givenVehicleData.get("manufacturer")));
        vehicleData.setManufacturerModel(String.valueOf(givenVehicleData.get("manufacturer_model")));
        vehicleData.setRegistrationDate(String.valueOf(givenVehicleData.get("registration_date")));
        vehicleData.setVehicleClass(String.valueOf(givenVehicleData.get("vehicle_class")));
        vehicleData.setFuelType(String.valueOf(givenVehicleData.get("fuel_type")));
        vehicleData.setColour(String.valueOf(givenVehicleData.get("colour")));
        vehicleData.setPermitValidity(String.valueOf(givenVehicleData.get("permit_validity_upto")));
        vehicleData.setMvTaxValidity(String.valueOf(givenVehicleData.get("mv_tax_upto")));
        vehicleData.setFitnessValidity(String.valueOf(givenVehicleData.get("fitness_upto")));
        vehicleData.setInsuranceValidity(String.valueOf(givenVehicleData.get("insurance_validity")));
        vehicleData.setPucValidity(String.valueOf(givenVehicleData.get("puc_valid_upto")));
        vehicleData.setRegisteredPlace(String.valueOf(givenVehicleData.get("registered_place")));
        vehicleData.setOwnerName(String.valueOf(givenVehicleData.get("owner_name")));
        vehicleData.setFatherName(String.valueOf(givenVehicleData.get("father_name")));


        vehicleData.setFuelCapacity(fuelCap);


        return vehicleData;

    }

    private void displayData(VehicleData vehicleData) {
        ownerNameEt.setText(vehicleData.getOwnerName());
        fatherNameEt.setText(vehicleData.getFatherName());
        regNumEt.setText(vehicleData.getRegistrationNumber());
        vClassEt.setText(vehicleData.getVehicleClass());

        vManuEt.setText(vehicleData.getManufacturer());
        vModelEt.setText(vehicleData.getManufacturerModel());
        regDateEt.setText(vehicleData.getRegistrationDate());
        colorEt.setText(vehicleData.getColour());

        engineNumEt.setText(vehicleData.getEngineNumber());
        chassisNumEt.setText(vehicleData.getChassisNumber());
        fuelTypeEt.setText(String.valueOf(vehicleData.getFuelType()));
        fuelCapEt.setText(String.valueOf(vehicleData.getFuelCapacity()));

        fitnessEt.setText(vehicleData.getFitnessValidity());
        insuranceEt.setText(vehicleData.getInsuranceValidity());
        taxEt.setText(vehicleData.getMvTaxValidity());
        permitEt.setText(vehicleData.getPermitValidity());
        puccEt.setText(vehicleData.getPucValidity());


    }

    private void addVehicle(VehicleData vehicleData, int fuelCap) {
        vehicleData.setFuelCapacity(fuelCap);
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

    private void verifyResponseData() {
        if (responseValue != null && !responseValue.isEmpty()) {
            Gson gson = new Gson();

            Map<String, Object> jsonMap = gson.fromJson(responseValue, Map.class);
            // Get the result map from the JSON string
            Map<String, Object> resultMap = (Map<String, Object>) jsonMap.get("result");

            String status = String.valueOf(resultMap.get("status"));
            String engineNumber = (String.valueOf(resultMap.get("engine_number")));
            String lastFourChars = engineNumber.substring(engineNumber.length() - 4);

            // String chassisNumber = (String.valueOf(resultMap.get("chassis_number")));

            if (isVehicleVerified) {
                Toast.makeText(getContext(), "Vehicle already verified ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (engineNumber.isEmpty()) {
                Toast.makeText(getContext(), "Can't Verify this Vehicle try manual method", Toast.LENGTH_SHORT).show();
                return;
            }

            if (status.equals("id_found")) {

                //If data found add verification
                // Ask the user to enter engine number and verify the number
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.verify_dialog_layout);

                engineNumConfirmEt = dialog.findViewById(R.id.engine_number_et);
                dialog.setTitle("Verify Vehicle:");

                Button okButton = dialog.findViewById(R.id.verify_btn);
                okButton.setOnClickListener(v1 -> {
                    String userInput = engineNumConfirmEt.getText().toString();
                    if (userInput.isEmpty()) {
                        //User Didnt Enter any value in the text box show a toast or setError to the editText
                        engineNumConfirmEt.setError("Enter last 4 digit of engine number to verify.");
                        engineNumConfirmEt.setText("");
                    } else if (userInput.equals(lastFourChars)) {
                        scrollView.setVisibility(View.VISIBLE);
                        isVehicleVerified = true;
//                        vehicleNumEt.setVisibility(View.INVISIBLE);
//                        searchBtn.setVisibility(View.INVISIBLE);
                        vehicleData = createVehicleData(resultMap, 0);
                        displayData(vehicleData);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Engine number doesn't match", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }


                });

                Button cancelButton = dialog.findViewById(R.id.cancel_button);
                cancelButton.setOnClickListener(v -> {
                    isVehicleVerified = false;
                    dialog.dismiss();
                });

                dialog.show();
            } else {
                //Add Manual Entry Code
                scrollView.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Vehicle data not found. Try manual method", Toast.LENGTH_SHORT).show();
                enterManually();

            }


        } else {
            scrollView.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Enter Valid Vehicle Number", Toast.LENGTH_SHORT).show();

        }
    }

    private void enterManually() {
        isManual = true;
        scrollView.setVisibility(View.VISIBLE);
        vehicleNumEt.setVisibility(View.INVISIBLE);
    }

    private void getInputData() {

        vehicleData.setOwnerName(String.valueOf(ownerNameEt.getText()));
        vehicleData.setFatherName(String.valueOf(fatherNameEt.getText()));
        vehicleData.setRegistrationNumber(String.valueOf(regNumEt.getText()));
        vehicleData.setVehicleClass(String.valueOf(vClassEt.getText()));

        vehicleData.setManufacturer(String.valueOf(vManuEt.getText()));
        vehicleData.setManufacturerModel(String.valueOf(vModelEt.getText()));
        vehicleData.setRegistrationDate(String.valueOf(regDateEt.getText()));
        vehicleData.setColour(String.valueOf(colorEt.getText()));

        vehicleData.setEngineNumber(String.valueOf(engineNumEt.getText()));
        vehicleData.setChassisNumber(String.valueOf(chassisNumEt.getText()));
        vehicleData.setFuelType(String.valueOf(fuelTypeEt.getText()));
        vehicleData.setFuelCapacity(Integer.parseInt(String.valueOf(fuelCapEt.getText())));

        vehicleData.setFitnessValidity(String.valueOf(fitnessEt.getText()));
        vehicleData.setInsuranceValidity(String.valueOf(insuranceEt.getText()));
        vehicleData.setMvTaxValidity(String.valueOf(taxEt.getText()));
        vehicleData.setPermitValidity(String.valueOf(permitEt.getText()));
        vehicleData.setPucValidity(String.valueOf(puccEt.getText()));
    }


    private void findViews(View view) {

        backBtn = view.findViewById(R.id.back_btn);
        addBtn = view.findViewById(R.id.add_vehicle_btn);

        vehicleNumEt = view.findViewById(R.id.vehicle_num_et);
        searchBtn = view.findViewById(R.id.search_btn);
        addVehicleImgBtn =  view.findViewById(R.id.add_vehicle_img);

        manualBtn = view.findViewById(R.id.manual_btn);

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