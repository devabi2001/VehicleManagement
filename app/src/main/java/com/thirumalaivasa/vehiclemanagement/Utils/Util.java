package com.thirumalaivasa.vehiclemanagement.Utils;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

import java.util.Locale;

public class Util {

    public static final String TAG = "VehicleManagement";
    public static final String VEHICLE = "Vehicle";
    public static final String EXPENSE = "Expense";
    public static final String DRIVER = "Driver";
    public static final String USER = "User";
    public static final String ID = "ID";
    private static boolean isNetworkAvail = true;
    private static final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            isNetworkAvail = true;
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            isNetworkAvail = false;
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            isNetworkAvail = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);

        }
    };

    public static boolean checkNetwork(ConnectivityManager connectivityManager) {
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        connectivityManager.requestNetwork(networkRequest, networkCallback);
        return isNetworkAvail;
    }

    public static String generateId(String type, String uniqueId) {
        long timestamp = System.currentTimeMillis();
        uniqueId = uniqueId.replace(" ", "");
        if (type.equals("Driver"))
            return type + uniqueId;
        return type + timestamp + uniqueId;
    }

    public static String getDisplayDate(int y, int m, int d) {
        String t1, t2;
        if (d < 10)
            t1 = "0" + d;
        else
            t1 = String.valueOf(d);
        if (m <= 10)
            t2 = "0" + (m + 1);
        else
            t2 = String.valueOf(m + 1);
        return t1 + "-" + t2 + "-" + y;
    }

    public static String getDisplayTime(int hour, int min) {
        String h, m;
        if (hour < 10)
            h = "0" + hour;
        else
            h = String.valueOf(hour);
        if (min < 10)
            m = "0" + min;
        else
            m = String.valueOf(min);

        // on below line we are setting selected time in our text view.
        return h + ":" + m;
    }

    public static String getFormattedString(Object s) {
        String format = "%.2f";
        return String.format(Locale.getDefault(), format, s);
    }

}
