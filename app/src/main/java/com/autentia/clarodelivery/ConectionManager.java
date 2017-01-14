package com.autentia.clarodelivery;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Edison Saez on 12-01-2017.
 */

public class ConectionManager {

    public static String getTypeConnection(Activity activity) {

        try {

            ConnectivityManager cmManager = (ConnectivityManager) activity
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cmManager.getAllNetworkInfo();
            for (NetworkInfo net : netInfo) {
                if (net.getTypeName().equalsIgnoreCase("wifi")) {
                    if (net.isConnected()) {
                        return "Wifi";
                    }
                }
                if (net.getTypeName().equalsIgnoreCase("mobile")) {
                    if (net.isConnected()) {
                        return "Red";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Sin señal";

    }

    public static String getCarrierName(Context mContext) {
        try {

            TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getNetworkOperatorName();

        } catch (Exception e) {
            return "Sin operador";
        }
    }

    public static String getLevelConnection(Context context) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        int levelPorcentage = level * 100 / 5;

        return levelPorcentage + "%";
    }

    public class SimpleTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            return isInternetWorking();
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);



        }
    }

    public boolean isInternetWorking() {
        boolean success = false;
        try {
            URL url = new URL("https://google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            success = connection.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

//    public static boolean hasInternet(Activity activity) {
//
//        boolean hasConnectedWifi = false;
//        boolean hasConnectedMobile = false;
//
//        try {
//            ConnectivityManager cmManager = (ConnectivityManager) activity
//                    .getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo[] netInfo = cmManager.getAllNetworkInfo();
//            for (NetworkInfo net : netInfo) {
//                if (net.getTypeName().equalsIgnoreCase("wifi")) {
//                    if (net.isConnected()) {
//                        hasConnectedWifi = true;
//                    }
//                }
//                if (net.getTypeName().equalsIgnoreCase("mobile")) {
//                    if (net.isConnected()) {
//                        hasConnectedMobile = true;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return hasConnectedWifi || hasConnectedMobile;
//
//    }
//    TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); String carrierName = manager.getNetworkOperatorName();
}
