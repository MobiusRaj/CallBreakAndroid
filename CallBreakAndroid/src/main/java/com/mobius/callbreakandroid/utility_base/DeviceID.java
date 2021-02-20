//---------------------------------------
//Class: DeviceID
//Usage: Generate a unique ID for your
//     app installation
//---------------------------------------

package com.mobius.callbreakandroid.utility_base;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;

import com.mobius.callbreakandroid.data_store.PreferenceManager;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class DeviceID {
    private static final String TAG = "DeviceID";

    private static String ID = null;
    private Context context;

    // return a cached unique ID for each device
    public String getID(Context context) {
        this.context = context;
        // if the ID isn't cached inside the class itself
        if (ID == null) {
            //get it from database / settings table (implement your own method here)
            ID = PreferenceManager.getUniqueId();
        }

        // if the saved value was incorrect
        if (ID.equals("") || ID.length() <= 0) {
            // generate a new ID
            generateID();
            Logger.print(TAG, "SERIALLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL => 6 " + ID);
        }

        return ID;
    }

    // generate a unique ID for each device
    // use available schemes if possible / generate a random signature instead
    private void generateID() {


        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = null;
        if (manager != null) {
            info = manager.getConnectionInfo();
        }
        Logger.print(TAG, "SERIAL => 1 ");
        String mac = info != null ? info.getMacAddress() : null;
        if (mac != null && mac.toUpperCase().equals("02:00:00:00:00:00")) {

            try {
                List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface nif : all) {
                    Logger.print(TAG, "SERIAL => 12 ");
                    if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                    Logger.print(TAG, "SERIAL => 13 ");

                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        if (PreferenceManager.getUniqueId().isEmpty()) {
                            try {
                                String device_unique_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
                                Logger.print(TAG, "SERIAL => 2 " + device_unique_id);
                                PreferenceManager.setUniqueId(device_unique_id);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(Integer.toHexString(b & 0xFF) + ":");
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    PreferenceManager.setUniqueId(res1.toString());
                    Logger.print(TAG, "SERIAL => 3 " + res1.toString());
                }
                Logger.print(TAG, "SERIAL => 11 ");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            if (info != null && info.getMacAddress() != null) {
                Logger.print(TAG, "SERIAL => 4 " + info.getMacAddress().toUpperCase());
                PreferenceManager.setUniqueId(info.getMacAddress().toUpperCase());
            }
        }

        try {
            if (PreferenceManager.getUniqueId().isEmpty() || PreferenceManager.getUniqueId().equals("02:00:00:00:00:00")) {
                String device_unique_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
                PreferenceManager.setUniqueId(device_unique_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}