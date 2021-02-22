package com.mobius.callbreakandroid.data_store;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mobius.callbreakandroid.utility_base.C;
import com.mobius.callbreakandroid.utility_base.Parameters;

import java.util.ArrayList;
import java.util.Arrays;

//import com.google.firebase.FirebaseApp;

//import com.crashlytics.android.Crashlytics;

//import io.fabric.sdk.android.Fabric;


public class PreferenceManager extends Application {


    public static String _id = "", username = "", email = "", fb_id = "", fb_accessToken = "";
    public static Context Pref_Context;

    public static boolean isPlayingScreenOpen = false;
    static SharedPreferences preferences;
    static SharedPreferences.Editor prefEditor;

    public static String getLastLogin() {
        return preferences.getString("lastLogin", "");
    }

    public static void setLastLogin(String lastLoginDate) {
        prefEditor.putString("lastLogin", lastLoginDate).apply();
    }

    public static String getUniqueId() {
        return preferences.getString("uniqueId", "");
    }

    public static void setUniqueId(String uniqueid) {
        prefEditor.putString("uniqueId", uniqueid).apply();
    }

    public static String getVersionNumber() {
        return preferences.getString("VersionNo", "1.0");
    }

    public static void setVersionNumber(String uniqueid) {
        prefEditor.putString("VersionNo", uniqueid).apply();
    }

    public static void setUserCardsForRejoin(ArrayList<Item_Card> selfUserCard, String tableId) {

        if (selfUserCard != null && !selfUserCard.isEmpty() && !tableId.isEmpty()) {
            Gson gson = new Gson();
            String json = gson.toJson(selfUserCard);
            prefEditor.putString("MyObject", json);
            prefEditor.putString("PrevTableId", tableId);
            prefEditor.apply();
        }
    }

    public static String getPrevTableId() {
        return preferences.getString("PrevTableId", "");
    }

    public static ArrayList<Item_Card> getUserCardsForRejoin() {
        try {
            Gson gson = new Gson();
            ArrayList<Item_Card> mStudentObject = null;
            String json = preferences.getString("MyObject", "");
            Item_Card[] mStudentObject1 = gson.fromJson(json, Item_Card[].class);
            if (mStudentObject1 != null && mStudentObject1.length > 0)
                mStudentObject = new ArrayList<>(Arrays.asList(mStudentObject1));
            return mStudentObject;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean getIsUserAlreadyLogin() {
        return preferences.getBoolean("IsUserLogin", false);
    }

    public static void setIsUserAlreadyLogin(boolean b) {
        prefEditor.putBoolean("IsUserLogin", b).apply();
    }

    public static String getUserMobileNo() {
        return preferences.getString("userNo", "");
    }

    public static void setUserMobileNo(String uniqueid) {
        prefEditor.putString("userNo", uniqueid).apply();
    }

    public static String getUserAmount() {
        return preferences.getString("user_amount", "");
    }

    public static void setUserAmount(String useramount) {
        prefEditor.putString("user_amount", useramount).apply();
    }

    public static boolean getSound() {
        return preferences.getBoolean("Mute", true);
    }

    public static void setSound(boolean b) {
        prefEditor.putBoolean("Mute", b).apply();
    }

    public static boolean getNotification() {
        return preferences.getBoolean("Notification", true);
    }

    public static void setNotification(boolean b) {
        prefEditor.putBoolean("Notification", b).apply();
    }

    public static boolean getVibrate() {
        return preferences.getBoolean("Vibrate", true);
    }

    public static void setVibrate(boolean b) {
        prefEditor.putBoolean("Vibrate", b).apply();
    }

    public static boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public static boolean isInternetIsOn() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static Context getContext() {
        return Pref_Context;
    }

    public static String get_id() {
        return preferences.getString(Parameters.User_Id, "");
    }

    public static void set_id(String _id) {
        PreferenceManager._id = _id;
        prefEditor.putString(Parameters.User_Id, _id).apply();
    }


    public static String getUserName() {
        return preferences.getString(Parameters.User_Name, "");
    }

    public static void setUserName(String username) {
        PreferenceManager.username = username;
        prefEditor.putString(Parameters.User_Name, username).apply();
    }

    public static int getUserLevel() {
        return preferences.getInt("User_Level", 0);
    }

    public static void setUserLevel(int userlvl) {
        prefEditor.putInt("User_Level", userlvl).apply();
    }

    public static int getUserLevelPoint() {
        return preferences.getInt("User_Level_Point", 0);
    }

    public static void setUserLevelPoint(int userlvl) {
        prefEditor.putInt("User_Level_Point", userlvl).apply();
    }

    public static void setUserState(String userrestate) {
        prefEditor.putString("user_state", userrestate).apply();
    }

    public static String getUserState() {
        return preferences.getString("user_state", "");
    }

    public static Boolean get_FirstTime() {

        return preferences.getBoolean(Parameters.isFirstTime, true);
    }

    public static void set_FirstTime(Boolean b) {

        prefEditor.putBoolean(Parameters.isFirstTime, b).apply();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Pref_Context = getApplicationContext();
        preferences = getSharedPreferences("CallBreak_DB", MODE_PRIVATE);
        prefEditor = preferences.edit();
        initSingletons();
    }

    protected void initSingletons() {
        // Initialize the instance of MySingleton
        C.getInstance();
    }
}

