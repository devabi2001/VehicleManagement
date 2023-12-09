package com.thirumalaivasa.vehiclemanagement;

import static com.thirumalaivasa.vehiclemanagement.Utils.Util.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.thirumalaivasa.vehiclemanagement.Utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class FuelPriceActivity extends AppCompatActivity implements LocationListener {

    //Widgets
    private TextView petrolPriceTv, dieselPriceTv;

    private AutoCompleteTextView locationSpinner;
    private ArrayAdapter<String> arrayAdapter;

    private String cityName;
    private CardView fuelPriceCard;
    private ProgressBar progressBar;
    private TextView locationTv;


    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LocationManager locationManager;


    private String petrolPrice = "0.0";
    private String dieselPrice = "0.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_price);
        findViews();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        arrayAdapter = new ArrayAdapter<>(FuelPriceActivity.this, R.layout.drop_down_item);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setLocationSpinner();

        locationSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedLocation = locationSpinner.getText().toString();
            if (selectedLocation.equalsIgnoreCase("Select Location")) {
                fuelPriceCard.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                removeLocationManager();
            } else if (selectedLocation.equalsIgnoreCase("Current Location")) {
                getLocation();
            } else {
                cityName = selectedLocation;
                makeApiRequest(cityName);
                removeLocationManager();
            }

        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        removeLocationManager();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.refresh_btn:
                makeApiRequest(cityName);
                break;
        }
    }

    //
    private void getPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                setLocationSpinner();
                //Permission Denied
            }
        }
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermission();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500000, 10, this);

    }

    private void removeLocationManager(){
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Handle location updates here
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Get the city name from the coordinates
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {

                cityName = addresses.get(0).getLocality();
                makeApiRequest(cityName);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setLocationSpinner() {

        String[] locationArray = getResources().getStringArray(R.array.locations);
        arrayAdapter = new ArrayAdapter<>(FuelPriceActivity.this, R.layout.drop_down_item, locationArray);
        arrayAdapter.notifyDataSetChanged();
        locationSpinner.setText(arrayAdapter.getItem(0));
        locationSpinner.setAdapter(arrayAdapter);
    }

    public void makeApiRequest(String cityName) {

        progressBar.setVisibility(View.VISIBLE);
        fuelPriceCard.setVisibility(View.VISIBLE);
        String url;
        AsyncHttpClient client = new AsyncHttpClient();
        if (cityName != null) {
            url = "http://192.168.50.165:5000/fuel_prices?city=" + cityName;
        } else {
            return;
        }

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    String responseData = new String(responseBody);
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(responseData);
                        petrolPrice = jsonResponse.getJSONObject(cityName).getString("petrol_price");
                        dieselPrice = jsonResponse.getJSONObject(cityName).getString("diesel_price");
                        setValues();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    petrolPrice = "0";
                    dieselPrice = "0";
                    Log.i(TAG, "Error Code:" + statusCode + " Can't able to retrieve data try again!");
                    Toast.makeText(FuelPriceActivity.this, "Error Code:" + statusCode + " Can't able to retrieve data try again!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                if (statusCode == 0) {
                    Log.e(TAG, "Code:0 Fuel API request denied", error);
                } else if (statusCode == 404) {
                    Log.e(TAG, "Code:404 Fuel API is not found", error);
                }
                Toast.makeText(FuelPriceActivity.this, "Error Code:" + statusCode + " Can't able to retrieve data try again!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void setValues() {

        petrolPriceTv.setText("Rs. " + petrolPrice);
        dieselPriceTv.setText("Rs. " + dieselPrice);
        locationTv.setText(cityName);

    }

    private void findViews() {
        petrolPriceTv = findViewById(R.id.petrol_price_tv);
        dieselPriceTv = findViewById(R.id.diesel_price_tv);
        locationSpinner = findViewById(R.id.location_spinner);
        fuelPriceCard = findViewById(R.id.fuel_price_card);
        progressBar = findViewById(R.id.fuel_price_progress);
        locationTv = findViewById(R.id.fuel_price_location);


    }


}