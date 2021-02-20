package com.mobius.callbreakandroid.game_activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobius.callbreakandroid.R;
import com.mobius.callbreakandroid.adapters.Adapter_CashGame_TableList;
import com.mobius.callbreakandroid.data_store.CashGame_TableList;
import com.mobius.callbreakandroid.data_store.PreferenceManager;
import com.mobius.callbreakandroid.dialog.NewCommonDialog;
import com.mobius.callbreakandroid.dialog.ServerError;
import com.mobius.callbreakandroid.interfaces.CashGameButtonListner;
import com.mobius.callbreakandroid.interfaces.ChooseServerService;
import com.mobius.callbreakandroid.socket_connection.ConnectionManager;
import com.mobius.callbreakandroid.socket_connection.Events;
import com.mobius.callbreakandroid.socket_connection.ResponseCodes;
import com.mobius.callbreakandroid.utility_base.C;
import com.mobius.callbreakandroid.utility_base.DeviceID;
import com.mobius.callbreakandroid.utility_base.EmitManager;
import com.mobius.callbreakandroid.utility_base.GlobalLoader_new;
import com.mobius.callbreakandroid.utility_base.Logger;
import com.mobius.callbreakandroid.utility_base.Parameters;
import com.mobius.callbreakandroid.utility_base.ParentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Activity_CallBreakList extends ParentActivity implements CashGameButtonListner {

    private static final String TAG = "Activity_CallBreak";
    private String userName, userState, IntentData;
    private boolean isForStageConnect = true;
    private ImageView clsNotification, whatsAppReferEarn;
    private TextView wallet_amount, txtNotificatoion, whatsAppReferEarnTxt;
    private RecyclerView CallbreakTableList;
    Adapter_CashGame_TableList adapter_cashGame_tableList;
    private NewCommonDialog newCommonDialog, commaneDialog;
    public static Handler handler;
    private C c = C.getInstance();
    private CountDownTimer countDownTimer, startLoaderTimer;
    private GlobalLoader_new loader;
    private String storeTimerData;
    private int addCashValue = 0;
    private boolean isFromLockButtonPopup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__call_break_list);
        c.conn.context = this;
        c.conn.activity = this;
        loader = new GlobalLoader_new(Activity_CallBreakList.this);
        GlobalLoaderSHOW(getResources().getString(R.string.PleaseWait));
        isForStageConnect = getIntent().getBooleanExtra("isForStageConnect", true);
        userName = getIntent().getStringExtra("userName");
        userState = getIntent().getStringExtra("userState");
//        PreferenceManager.setUserMobileNo(getIntent().getStringExtra("userMobileNumber"));
//        setUserRegistrationId();
        setDisplayMetrics();
        if (PreferenceManager.getUniqueId().length() <= 0) {
            new DeviceID().getID(getApplicationContext());
        }
        initHandler();
        ConnectionManager.setHandler(getHandler());
        setUpSocketConnection();
        c.practicGameFlage = true;
        findViewById();
    }

    public static Handler getHandler() {
        return handler;
    }

    private void setUserRegistrationId() {
        /*FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.e("TOKEN","Token Login---->" + token);
                    PreferenceManager.setRegistrationId(token);
                });
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);*/
    }

    private void setDisplayMetrics() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        c.height = displaymetrics.heightPixels;
        c.width = displaymetrics.widthPixels;
        if (c.height > c.width) {
            c.width = this.getResources().getDisplayMetrics().heightPixels;
            c.height = this.getResources().getDisplayMetrics().widthPixels;
        }
        c.CalculateDisplaySize(this);
    }

    private void setUpSocketConnection() {
        if (c.conn.isConnected()) c.conn.disconnect();
        String chooseServerUrl;
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("det", "android");

        if (!isForStageConnect) {
            chooseServerUrl = "https://api.rummy777.com/";
            if (PreferenceManager.getIsUserAlreadyLogin() && !c.isNullOrEmpty(PreferenceManager.getUserMobileNo())) {
                queryParams.put("mobileNumber", PreferenceManager.getUserMobileNo());
            }
            makeChooseServerCall(chooseServerUrl, queryParams);

        } else {
            chooseServerUrl = "https://stageapi.rummy777.com/";
            if (PreferenceManager.getIsUserAlreadyLogin() && !c.isNullOrEmpty(PreferenceManager.getUserMobileNo())) {
                queryParams.put("mobileNumber", PreferenceManager.getUserMobileNo());
            }
            makeChooseServerCall(chooseServerUrl, queryParams);
        }
    }

    public void makeChooseServerCall(final String params, Map<String, String> queryParams) {

        Logger.print(TAG, "PARAMS --> " + params);

        try {
            setNetworkType();
            c.CurrentVersion = "1.0";
            c.OSVersion = Build.VERSION.RELEASE;
            retrieveConfigData(params, queryParams).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject configObj = parseResponse(response.body().byteStream());
                            if (configObj == null) {
                                Logger.print(TAG, " <----- RETRIEVED NULL CONFIG DATA -----> ");
                                return;
                            }
                            setConfig(configObj);
//                            processConfigData(configObj);
                            c.ip = configObj.has("SOCKET_URL") ? configObj.getString("SOCKET_URL") : "https://stageplay.rummy777.com";
                            Logger.print(TAG, " ASSIGN STATIC c.ip : " + c.ip);
                            if (!c.MAINTAINANCE_MODE) {
                                if (!PreferenceManager.getUniqueId().isEmpty()) {
                                    startSocketConnection();
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.print(TAG, " " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Logger.print(TAG, " " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Logger.print(TAG, " " + e.getMessage());
        }

    }

    private void setNetworkType() {
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            if (conMan != null) {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState();
                if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
                    int networkType = 0;
                    if (telephonyManager != null) {
                        networkType = telephonyManager.getNetworkType();
                    }
                    c.NetworkType = resolveNetworkType(networkType);
                } else {
                    NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState();
                    if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                        c.NetworkType = "WIFI";
                    }
                }
            }
        } catch (Exception e) {
            Logger.print(TAG, " " + e.getMessage());
        }
    }

    private String resolveNetworkType(int paramInt) {

        switch (paramInt) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "2G";

            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "3G";

            case TelephonyManager.NETWORK_TYPE_LTE:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "4G";

            default:
                return "Unknown";
        }

    }

    private Call<ResponseBody> retrieveConfigData(String serverUrl, Map<String, String> params) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .connectTimeout(120, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .client(okHttpClient)
                .build();

        ChooseServerService chooseServerService = retrofit.create(ChooseServerService.class);
        return chooseServerService.getChooseServiceResponse(params);
    }

    private JSONObject parseResponse(InputStream stream) {
        JSONObject responseObj = new JSONObject();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream));
        String line;
        StringBuilder builder = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            stream.close();
            String strResp = builder.toString();
            String plainText;
            plainText = c.decrypt(strResp);
            responseObj = new JSONObject(plainText);
            return responseObj;
        } catch (Exception e) {
            Logger.print(TAG, " " + e.getMessage());
            try {
                responseObj.put("m_mode", true);
            } catch (Exception ex) {
                Logger.print(TAG, " " + ex.getMessage());
            }
        }
        return responseObj;
    }

    private void setConfig(JSONObject receiveObject1) throws JSONException {
        JSONObject config = new JSONObject(receiveObject1.toString());
        JSONObject data = config.getJSONObject(Parameters.config);
        config = new JSONObject();
        config.put(Parameters.data, data);
        c.jsonData.setConfigData(config);
    }

    private void startSocketConnection() {
        c.connectioncnt = 0;
        c.conn.Connected();
    }

    private void callSignUpEvent() {
        JSONObject jObj = new JSONObject();
        try {
            jObj.put(Parameters.Device_Type, "android");
            jObj.put(Parameters.ReferrerCode, "");
//            jObj.put(Parameters.Device_Id, PreferenceManager.getRegistrationId());
//            jObj.put(Parameters.un, userName);
            jObj.put(Parameters.un, "userName");
//            jObj.put(Parameters.mobileNumber, PreferenceManager.getUserMobileNo());
            jObj.put(Parameters.mobileNumber, "0000075368");
//            jObj.put(Parameters.state, userState);
            jObj.put(Parameters.state, "Sikkim");
            EmitManager.Process(jObj, Events.Signup_Process);
            GlobalLoaderSHOW(getResources().getString(R.string.PleaseWait));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        boolean showError = intent.getBooleanExtra("ShowError", false);
        boolean EXIT = intent.getBooleanExtra("EXIT", false);
        boolean PING_ERROR = intent.getBooleanExtra("PING_ERROR", false);
        boolean socketDisconnect = intent.getBooleanExtra("socketDisconnect", false);

        if (socketDisconnect) {
            Logger.print(TAG, "Socket Disconnected");
            finishLoaderTimer();
            c.isErrorPopup = false;
            String server_message = "It seems that connection to server have been Lost. Please reconnect!";
            new ServerError(Activity_CallBreakList.this, server_message);

        } else if (showError) {
            finishLoaderTimer();
            boolean val = intent.getBooleanExtra("value", false);
            String server_message;

            if (val) {
                Logger.print(TAG, "<<<<<<<<<<Internet connection to server lost");
                if (PreferenceManager.isInternetConnected()) {
                    server_message = "It seems that connection to server have been Lost. Please reconnect!";
                } else {
                    server_message = "No Internet connection available. Please make sure\n your device is connected to the Internet.";
                }
                new ServerError(Activity_CallBreakList.this, server_message);

            } else {
                server_message = "It seems you were Idle for long times, Please Reconnect!";
                new Handler().postDelayed(() -> {
                    Logger.print(TAG, "<<<<<<<<<<Idle for long time");
                    new ServerError(Activity_CallBreakList.this, server_message);
                }, 500);
            }
        }

        if (c.conn.isDoubleLogin) {
            EXIT = true;
            c.conn.isDoubleLogin = false;
        }

        if (EXIT && PreferenceManager.get_FbId().length() > 0) {
            Logger.print(TAG, "SSSSSSSSSSSSSSSSS 2");
            finishLoaderTimer();
            if (!PING_ERROR) {
                EXIT = false;
                Intent i = new Intent(Activity_CallBreakList.this, Activity_CallBreakList.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("showSwitchDialog", true);
                startActivity(i);
                PreferenceManager.setIsUserAlreadyLogin(false);
                PreferenceManager.setUserMobileNo("");
                PreferenceManager.set_id("");
                finish();
            } else {
                new ServerError(Activity_CallBreakList.this,
                        "It seems that connection to server have been Lost. Please reconnect!");
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getHandler() == null) {
            initHandler();
        }
        ConnectionManager.setHandler(getHandler());
        c.conn.context = this;
        c.conn.activity = this;
        wallet_amount.setText(String.format("₹ %s", c.getNumberFormatedValue(c.Chips)));
//        finishLoaderTimer();
        if (adapter_cashGame_tableList != null) {
            adapter_cashGame_tableList.notifyDataSetChanged();
        }
    }

    private void findViewById() {
        ImageView icn_close = findViewById(R.id.icn_close);
        ImageView htp = findViewById(R.id.htp);
        wallet_amount = findViewById(R.id.wallet_amount);
        whatsAppReferEarn = findViewById(R.id.whatsapp_refer_earn);
        whatsAppReferEarnTxt = findViewById(R.id.txt_wp_earn);
        CallbreakTableList = findViewById(R.id.callbreak_table_list);
        txtNotificatoion = findViewById(R.id.txt_notificatoion);
        clsNotification = findViewById(R.id.cls_notification);
        txtNotificatoion.setVisibility(View.GONE);
        clsNotification.setVisibility(View.GONE);
        icn_close.setOnClickListener(v -> {
            overridePendingTransition(0, R.anim.dialogue_scale_anim_exit);
            EmitManager.Process(new JSONObject(), Events.CloseCashGameBoard);
            finish();
        });
        htp.setOnClickListener(v -> {
            Intent i10 = new Intent(Activity_CallBreakList.this, Activity_HowToPlay.class);
            startActivity(i10);
            overridePendingTransition(0, R.anim.dialogue_scale_anim_exit);
        });
        whatsAppReferEarn.setOnClickListener(v -> wpReferAndEarn());
        /*whatsAppReferEarnTxt.setOnClickListener(v -> {
            wpReferAndEarn();
        });*/
        clsNotification.setOnClickListener(v -> {
            isNotificationShow(false, "");
            if (countDownTimer != null)
                countDownTimer.cancel();
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        CallbreakTableList.setLayoutManager(mLayoutManager);
        CallbreakTableList.setItemAnimator(new DefaultItemAnimator());
    }

    private void wpReferAndEarn() {
//        music_manager.buttonClick();
        String whatsApp = "Hi, \n" +
                "Come Join 777 Games - My Favourite and India's First Real Money Callbreak Platform now - \n" +
                "and get upto *Rs 9000* on your First deposit using code *REFER777*\n" +
                "Download the app now - rummy777.com  \uD83D\uDD25 \n" +
                "\n" +
                "Dont Compromise. Get the maximum *mileage* from *every rupee* spent.\n" +
                "\n" +
                "Use my unique code *_" + c.RFCode.toUpperCase() + "_* while signing up.\n" +
                "\n" +
                "Awesome Callbreak games are waiting for you, download the app now\n" +
                "to get *100% Instant cash bonus + 150% In game bonus* on your first deposit \uD83D\uDE0D\n";

        if (c.isAppInstalled(Activity_CallBreakList.this, "com.whatsapp")) {
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, whatsApp);
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                CommaneDialog("Whatsapp have not been installed");
            }
        } else {
            CommaneDialog("Whatsapp have not been installed");
        }
    }

    private void setData() {
        try {
            JSONObject object = new JSONObject(IntentData);
            JSONObject data = object.getJSONObject(Parameters.data);
            JSONArray lists = data.getJSONArray("lists");
            ArrayList<CashGame_TableList> cashGame_tableLists = new ArrayList<>();
            for (int i = 0; i < lists.length(); i++) {
                CashGame_TableList cashGame_tableList = new CashGame_TableList(lists.getJSONObject(i));
                cashGame_tableLists.add(cashGame_tableList);
            }
            adapter_cashGame_tableList = new Adapter_CashGame_TableList(this, cashGame_tableLists, true, false);
            CallbreakTableList.setAdapter(adapter_cashGame_tableList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initHandler() {
        handler = new Handler(msg -> {
            if (msg.what == ResponseCodes.StartLoader) {
                try {
                    JSONObject jObj = new JSONObject(msg.obj.toString());
                    String message = jObj.getString("message");
                    GlobalLoaderSHOW(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (msg.what == ResponseCodes.FinishLoader) {
                finishLoaderTimer();
            } else if (msg.what == ResponseCodes.SocketConnectionResp) {
                callSignUpEvent();
            } else if (msg.what == ResponseCodes.SignupResp) {
                JSONObject signUpProcess;
                try {
                    signUpProcess = new JSONObject(msg.obj.toString());
                    JSONObject UserInfo = signUpProcess;
                    c.specialOfferData = UserInfo.toString();
                    if (signUpProcess.getBoolean(Parameters.Flag)) {
//                        startLoaderTimer(getResources().getString(R.string.PleaseWait));
                        try {
                            JSONObject jobj = new JSONObject();
                            jobj.put("gt", "CallBreak");
                            EmitManager.Process(jobj, Events.GetPlayingtablecategoryList);
                            wallet_amount.setText(String.format("₹ %s", c.getNumberFormatedValue(c.Chips)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (signUpProcess.has(Parameters.errorCode) && signUpProcess.getInt(Parameters.errorCode) == 1058) {
                            finish();
                        }
                    }
                } catch (Exception e) {
                    Logger.print(TAG, " " + e.getMessage());
                }
            } else if (msg.what == ResponseCodes.GetPlayingtablecategoryListResp) {
                try {
                    finishLoaderTimer();
                    JSONObject object = new JSONObject(msg.obj.toString());
                    JSONObject data = object.getJSONObject(Parameters.data);
//                    if (data.has("roomUpdate") && data.getBoolean("roomUpdate")) {
                    IntentData = msg.obj.toString();
                    setData();
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (msg.what == ResponseCodes.SuspendeUserResp) {
                finishLoaderTimer();
                try {
                    JSONObject jObj = new JSONObject(msg.obj.toString());
                    if (jObj.has(Parameters.errorCode) && jObj.getString(Parameters.errorCode).equalsIgnoreCase("1005")) {
                        isNotificationShow(true, jObj.getString(Parameters.msg));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (msg.what == ResponseCodes.LockButtonClickEventResp) {
                finishLoaderTimer();
                try {
                    JSONObject jObj = new JSONObject(msg.obj.toString());
                    JSONObject data = jObj.getJSONObject(Parameters.data);
                    showCommaneDialog(data.has(Parameters.msg) ? data.getString(Parameters.msg) : "");
                    isFromLockButtonPopup = true;
                    addCashValue = data.has("amount") ? data.getInt("amount") : 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }/* else if (msg.what == ResponseCodes.GetAddCashDetailsResp) {
                finishLoaderTimer();
                try {
                    JSONObject jObj = new JSONObject(msg.obj.toString());
                    Intent i10 = new Intent(Activity_CallBreakList.this, ActivityAddCash.class);
                    i10.putExtra("DATA", jObj.toString());
                    if (isFromLockButtonPopup) {
                        isFromLockButtonPopup = false;
                        i10.putExtra("AddCahAmount", addCashValue);
                    }
                    startActivity(i10);
                    overridePendingTransition(android.R.anim.slide_in_left, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } */ else if (msg.what == ResponseCodes.GetTableInfoResp) {
//                PreferenceManager.isPlayingScreenOpen = true;
                try {
                    finishLoaderTimer();
                    JSONObject obj = new JSONObject(msg.obj.toString());
                    String gameType = obj.getJSONObject(Parameters.data).getString("gt");
                    if (gameType.equalsIgnoreCase("Callbreak")) {
                        Intent i = new Intent(this, CallBreak_PlayingScreen.class);
                        i.putExtra("IntentData", msg.obj.toString());
                        startActivity(i);
                    }
                    overridePendingTransition(android.R.anim.fade_in, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (msg.what == ResponseCodes.JoinTableResp) {
                String joinTableData = msg.obj.toString();
                new Handler().postDelayed(() -> {
                    if (CallBreak_PlayingScreen.handler != null) {
                        Message m = new Message();
                        m.what = ResponseCodes.JoinTableResp;
                        m.obj = joinTableData;
                        CallBreak_PlayingScreen.handler.sendMessage(m);
                    }
                }, 500);
            } else if (msg.what == ResponseCodes.RoundTimerStartResp) {
                storeTimerData = msg.obj.toString();
                new Handler().postDelayed(() -> {
                    if (CallBreak_PlayingScreen.handler != null) {
                        Message m = new Message();
                        m.what = ResponseCodes.RoundTimerStartResp;
                        m.obj = storeTimerData;
                        CallBreak_PlayingScreen.handler.sendMessage(m);
                    }
                }, 500);
            }
            return false;
        });
    }

    @Override
    public void playButtonClick(String id, String catId) {
        if (c.monyNoAllowRegion(PreferenceManager.getUserState())) {
            if (txtNotificatoion.getVisibility() != View.VISIBLE)
                isNotificationShow(true, "Cash Game in your region is not allowed. Please see our Terms and Conditions");
        } else {
            isNotificationShow(false, "");
            GlobalLoaderSHOW(getResources().getString(R.string.PleaseWait));
            try {
                JSONObject object = new JSONObject();
                c.tbId = id;
                c.catId = catId;
                object.put("tbId", id);
                object.put("catId", catId);
                EmitManager.Process(object, Events.JoinTables);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void lockButtonClick(String catId) {
        try {
            GlobalLoaderSHOW(getResources().getString(R.string.PleaseWait));
            JSONObject object = new JSONObject();
            object.put("catId", catId);
            EmitManager.Process(object, Events.LockButtonClickEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addCashButtonClick() {
//        startLoaderTimer(getResources().getString(R.string.PleaseWait));
//        EmitManager.Process(new JSONObject(), Events.GetAddCashDetails);
    }

    private void isNotificationShow(boolean show, String msg) {
        txtNotificatoion.setVisibility(show ? View.VISIBLE : View.GONE);
        clsNotification.setVisibility(show ? View.VISIBLE : View.GONE);
        txtNotificatoion.setText(msg);
        if (show) {
            countDownTimer = new CountDownTimer(4000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    if (txtNotificatoion.getVisibility() == View.VISIBLE) {
                        isNotificationShow(false, "");
                    }
                }
            }.start();
        }
    }

    private void startLoaderTimer(final String message) {
        try {
            finishLoaderTimer();
            startLoaderTimer = new CountDownTimer(1200, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    GlobalLoaderSHOW(message);
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCommaneDialog(String msg) {
        closeCommaneDialog();
        newCommonDialog = new NewCommonDialog.Builder()
                .setTitle("")
                .setMessage(msg)
                .setCancelable(false)
                .setCloseVisible(false)
                .setPositiveButton("ADD CASH", () -> {
//                    GlobalLoaderSHOW(getResources().getString(R.string.PleaseWait));
//                    EmitManager.Process(new JSONObject(), Events.GetAddCashDetails);
                    closeCommaneDialog();
                })
                .setNegativeButton("CANCEL", () -> {
//                    music_manager.buttonClick();
                    isFromLockButtonPopup = false;
                    closeCommaneDialog();
                })
                .create(Activity_CallBreakList.this);
        newCommonDialog.show();
    }

    private void closeCommaneDialog() {
        try {
            if (newCommonDialog != null) {
                if (newCommonDialog.isShowing()) {
                    newCommonDialog.cancel();
                    newCommonDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CommaneDialog(String diaMessage) {
        closeCommaneDialogs();
        commaneDialog = new NewCommonDialog.Builder()
                .setTitle("")
                .setMessage(diaMessage)
                .setCancelable(false)
                .setCloseVisible(false)
                .setPositiveButton(getResources().getString(R.string.OK), this::closeCommaneDialogs)
                .create(this);

        commaneDialog.show();
    }

    private void closeCommaneDialogs() {
        if (commaneDialog != null) {
            if (commaneDialog.isShowing()) {
                commaneDialog.dismiss();
            }
            commaneDialog = null;
        }
    }

    private void finishLoaderTimer() {
        try {
            if (startLoaderTimer != null) {
                startLoaderTimer.cancel();
            }
            LoaderFinish(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void LoaderFinish(int i) {
        try {
            loader.FinishMe(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GlobalLoaderSHOW(final String message) {
        try {
            runOnUiThread(() -> {
                loader.ShowMe("" + message);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (loader != null) {
                loader.Destroy();
                loader = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}