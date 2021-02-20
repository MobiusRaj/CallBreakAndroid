package com.mobius.callbreakandroid.data_store;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.view.WindowManager;

//import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mobius.callbreakandroid.utility_base.C;
import com.mobius.callbreakandroid.utility_base.Parameters;

import java.util.ArrayList;
import java.util.Arrays;

//import com.crashlytics.android.Crashlytics;

//import io.fabric.sdk.android.Fabric;


public class PreferenceManager extends Application {


    public static String _id = "", username = "", email = "", fb_id = "", fb_accessToken = "";
    public static Context Pref_Context;
    public static String APP_ID = "706880752780113";
    public static String APP_NAMESPACE = "Indian Rummy";
    public static String SENDASK_ObjectID = "1092203337469077";

    /*  Tx2SCDjGddt4rW94q7ESji */
    private static final String AF_DEV_KEY = "Tx2SCDjGddt4rW94q7ESji";

    private static final String ORDER_ID = "orderId";

    //    public class C{
    public static boolean isPlayingScreenOpen = false;
    static SharedPreferences preferences;
    static SharedPreferences.Editor prefEditor;

    // mini games and coin store
    public static void Setisfree(String val) {

        prefEditor.putString("isfree", val).apply();
    }

    // public boolean isFirst=false;

    public static String getisfree() {

        return preferences.getString("isfree", "");
    }

    public static String getRegistrationId() {
        return preferences.getString("registrationId", "");
    }

    public static void setRegistrationId(String registrationid) {
        if (prefEditor != null)
            prefEditor.putString("registrationId", registrationid).apply();
    }

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
//            new UserCardRejoinAsyncHelper(selfUserCard,tableId).execute();
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

    public static int getOpenCounter() {
        return preferences.getInt("openCounter", 0);
    }

    public static void setOpenCounter(int counter) {
        prefEditor.putInt("openCounter", counter).apply();
    }

    public static int getDBVersion() {
        return preferences.getInt("dbVersion", 0);
    }

    public static void setDBVersion(int counter) {
        prefEditor.putInt("dbVersion", counter).apply();
    }

    public static boolean getFlagForAdOnSpecialOffer() {
        return preferences.getBoolean("FlagForAdOnSpecialOffer", false);

    }

    // public static int getDivisor() {
    // return preferences.getInt("Divisor", 14);
    // }
    //
    // public static void setDivisor(int divisor) {
    // prefEditor.putInt("Divisor", divisor).apply();
    // }

    public static void setFlagForAdOnSpecialOffer(boolean b) {
        prefEditor.putBoolean("FlagForAdOnSpecialOffer", b).apply();
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

    public static String getUserReturnUrl() {
        return preferences.getString("return_url", "");
    }

    public static void setUserReturnUrl(String rtnurl) {
        prefEditor.putString("return_url", rtnurl).apply();
    }

    public static String getUserOrderID() {
        return preferences.getString("order_id", "");
    }

    public static void setUserOrderID(String orderid) {
        prefEditor.putString("order_id", orderid).apply();
    }

    public static String getUserProductID() {
        return preferences.getString("product_id", "");
    }

    public static void setUserProductID(String productid) {
        prefEditor.putString("product_id", productid).apply();
    }

    public static String getUserEmailID() {
        return preferences.getString("user_email", "");
    }

    public static void setUserEmailID(String amount) {
        prefEditor.putString("user_email", amount).apply();
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

    public static int getVipDisplay() {
        return preferences.getInt("vipflag", 1);
    }

    public static void setVipdisplay(int vipflag) {
        prefEditor.putInt("vipflag", vipflag).apply();
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

    public static boolean getPopupShown() {
        return preferences.getBoolean("popupshown", false);
    }

    public static void setPopupShown(boolean b) {
        prefEditor.putBoolean("popupshown", b).apply();
    }

    public static String getLastDiscardedVersion() {
        String version = "";
        try {
            PackageManager manager = Pref_Context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(Pref_Context.getPackageName(), 0);
            version = info.versionName;
        } catch (NameNotFoundException e) {

            e.printStackTrace();
        }
        return preferences.getString("LastDiscardedVersion", version);
    }

    public static void setLastDiscardedVersion(String version) {
        prefEditor.putString("LastDiscardedVersion", version).apply();
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

    public static boolean getChallengePopup() {
        return preferences.getBoolean("ChallengePopup", true);
    }

    public static void setChallengePopup(boolean status) {
        prefEditor.putBoolean("ChallengePopup", status).apply();
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

    public static String get_FbId() {
        return preferences.getString(Parameters.FB_Id, "");
    }

    public static void set_FbId(String FB_Id) {
        fb_id = FB_Id;
        prefEditor.putString(Parameters.FB_Id, FB_Id).apply();
    }

    public static String get_FB_accessToken() {
        return preferences.getString(Parameters.FB_Token, "");
    }

    public static void set_FB_accessToken(String FB_accessToken) {
        fb_accessToken = FB_accessToken;
        prefEditor.putString(Parameters.FB_Token, FB_accessToken).apply();
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

    public static void setUserReward(String userreward) {
        prefEditor.putString("user_reward", userreward).apply();
    }

    public static String getUserReward() {
        return preferences.getString("user_reward", "");
    }

    public static void setUserState(String userrestate) {
        prefEditor.putString("user_state", userrestate).apply();
    }

    public static String getUserState() {
        return preferences.getString("user_state", "");
    }

    public static String getlastFacebookPost() {
        return preferences.getString("fbpost", "");
    }

    public static void setlastFacebookPost(String username) {
        prefEditor.putString("fbpost", username).apply();
    }

    public static String getUserEmail() {
        return preferences.getString(Parameters.User_Email, "");
    }

    public static void setUserEmail(String email) {
        PreferenceManager.email = email;
        prefEditor.putString(Parameters.User_Email, email).apply();
    }


    public static boolean getShowNewPointInfo() {
        return preferences.getBoolean("newPointInfo", true);
    }

    public static void setShowNewPointInfo(boolean b) {
        prefEditor.putBoolean("newPointInfo", b).apply();
    }

    public static String get_UserLoginType() {

        return preferences.getString(Parameters.User_LoginType, Parameters.Guest);
    }

    public static void set_UserLoginType(String string) {

        prefEditor.putString(Parameters.User_LoginType, string).apply();
    }

    public static Boolean get_FirstTime() {

        return preferences.getBoolean(Parameters.isFirstTime, true);
    }

    public static void set_FirstTime(Boolean b) {

        prefEditor.putBoolean(Parameters.isFirstTime, b).apply();
    }

    public static String get_ReferenceLink() {
        return preferences.getString(Parameters.rfl, "");
    }

    public static void set_ReferenceLink(String b) {

        prefEditor.putString(Parameters.rfl, b).apply();
    }


    public static void setOrderId(int orderId) {
        preferences.edit().putInt(ORDER_ID, orderId).apply();
    }

    public static int getOrderId() {
        if (preferences.contains(ORDER_ID)) {
            return preferences.getInt(ORDER_ID, 0);
        } else {
            return 0;
        }
    }


    public static void showDialog(Dialog dialog) {
        try {
            Context context = ((ContextWrapper) dialog.getContext()).getBaseContext();

            if (context instanceof Activity) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed() && !dialog.isShowing()) {
                        try {
                            if (dialog.getWindow() != null) {
                                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                                dialog.show();
                                dialog.getWindow().getDecorView().setSystemUiVisibility(((Activity) context).getWindow().getDecorView().getSystemUiVisibility());
                                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (!((Activity) context).isFinishing() && !dialog.isShowing()) {
                        try {
                            if (dialog.getWindow() != null) {
                                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                                dialog.show();
                                dialog.getWindow().getDecorView().setSystemUiVisibility(((Activity) context).getWindow().getDecorView().getSystemUiVisibility());
                                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                try {
                    if (dialog.getWindow() != null && !dialog.isShowing()) {
                        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                        dialog.show();
                        dialog.getWindow().getDecorView().setSystemUiVisibility(dialog.getWindow().getDecorView().getSystemUiVisibility());
                        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        FirebaseApp.initializeApp(getApplicationContext());

        Pref_Context = getApplicationContext();

        preferences = getSharedPreferences("CallBreak_DB", MODE_PRIVATE);
        prefEditor = preferences.edit();

        initSingletons();

        /*DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);*/

        // set log to true

        final C c = C.getInstance();

        /*try {
            registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle bundle) {
                    c.currentActivity = activity;
                    if (!(activity instanceof Activity_MobiusLogin || activity instanceof FacebookActivity)) {
                        if (c.conn.socket1 == null) {
                            Intent intent = new Intent(activity, Activity_MobiusLogin.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(0);
                        }
                    }
                }

                @Override
                public void onActivityStarted(Activity activity) {
                }

                @Override
                public void onActivityResumed(Activity activity) { }

                @Override
                public void onActivityPaused(Activity activity) { }

                @Override
                public void onActivityStopped(Activity activity) { }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle bundle) { }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (activity instanceof Activity_NewDashBoard) {
                        NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        if (mNM != null)
                            mNM.cancelAll();
                    }
                    try {
                        c.closeBuddyRequestDialog();
                        ServerError.CloseConfirmationDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    protected void initSingletons() {
        // Initialize the instance of MySingleton
        C.getInstance();
    }

    private static class UserCardRejoinAsyncHelper extends AsyncTask<Void, Void, Void> {

        private ArrayList<Item_Card> selfUserCard;
        private String tableId;

        UserCardRejoinAsyncHelper(ArrayList<Item_Card> selfUserCard, String tableId) {

            this.selfUserCard = selfUserCard;
            this.tableId = tableId;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (selfUserCard != null && !selfUserCard.isEmpty() && !tableId.isEmpty()) {
                Gson gson = new Gson();
                String json = gson.toJson(selfUserCard);
                prefEditor.putString("MyObject", json);
                prefEditor.putString("PrevTableId", tableId);
                prefEditor.apply();
            }

            return null;
        }
    }
//    }
}

