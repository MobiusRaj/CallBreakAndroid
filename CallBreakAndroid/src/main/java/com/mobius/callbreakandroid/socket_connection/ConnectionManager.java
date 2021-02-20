package com.mobius.callbreakandroid.socket_connection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;

import com.mobius.callbreakandroid.R;
import com.mobius.callbreakandroid.data_store.PreferenceManager;
import com.mobius.callbreakandroid.dialog.NewCommonDialog;
import com.mobius.callbreakandroid.dialog.ServerError;
import com.mobius.callbreakandroid.game_activity.Activity_CallBreakList;
import com.mobius.callbreakandroid.game_activity.Activity_CallBreakScoreBoard;
import com.mobius.callbreakandroid.game_activity.CallBreak_PlayingScreen;
import com.mobius.callbreakandroid.utility_base.C;
import com.mobius.callbreakandroid.utility_base.EmitManager;
import com.mobius.callbreakandroid.utility_base.Logger;
import com.mobius.callbreakandroid.utility_base.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ConnectionManager {
    private static final String TAG = "ConnectionManager";

    /* public SocketIOClient socket = null; */

    public static Handler handler, handlerPing;
    public static boolean ManuallyClosed = false;
    public static boolean isDisconnectByUser = false;
    static byte[] keyBytes;
    static byte[] cipherData;
    static String plainText;
    private static Handler xhandler;
    final private int delay = 5000;
    public Context context;
    public Activity activity;
    public boolean isDoubleLogin = false;
    public long Maintainance_Start = 0, Maintainance_End = 0, Remove_After = 0;
    public boolean showMaintainanceTimer = false;
    public boolean isReconnected = false;
    public boolean maintenanceTimerStarted = false;
    public Socket socket1;
    public boolean open = false;
    public boolean ideal = false;
    public long idleTime = 600;
    public boolean isInIdleMode = false;
    Timer MaintainanceTimer = new Timer();
    int hbcnt = 0;
    int ping_counter, EventCounter = 0;
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (isConnected()) {

                emit("hb", "");
                ping_counter++;
                hbcnt++;

                handlerPing.postDelayed(this, 5000);

                if (hbcnt == 10) {

                    // server_message =
                    // "No Internet connection available. Please make sure your device is \nconnected to the Internet.";
                    //
                    // ServerError error = new ServerError(activity,
                    // server_message);

                    try {
                        Logger.print(TAG, "Diconnect.........!!!!2222");
                        disconnect();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    hbcnt = 0;
                }
            } else {
                if (handlerPing != null && runnable != null) {
                    handlerPing.removeCallbacks(runnable);
                }

                // server_message =
                // "No Internet connection available. Please make sure your device is \nconnected to the Internet.";
                // ServerError error = new ServerError(activity,
                // server_message);

                try {
                    Logger.print(TAG, "Diconnect.........!!!!3333");
                    disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };
    C c;
    private String server_message;
    private boolean hasSocketId = false;
    private Timer idleTimer;
    private Timer reconnectTimer = null;

    //Dialog ConfirmationDialogAddFriend;
    //Dialog friendrequestConfirmationDialog;
    private int reconnectCounter;
    private Emitter.Listener getdataListener = new Emitter.Listener() {

        @Override
        public void call(final Object... arg0) {
            try {
                GetDecodeEvet(arg0[0].toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public ConnectionManager(C c) {
        this.c = c;
    }

    public static Handler getHandler() {
        return xhandler;
    }

    public static void setHandler(Handler handler) {
        xhandler = handler;
    }

    public void Connected() {
        Logger.print(TAG, "CONNECTED CALLLLLLLL  ON : " + c.ip);
        try {

            IO.Options opts = new IO.Options();
            // opts.forceNew = true;
            opts.timeout = -1;
            opts.reconnection = false;

            socket1 = IO.socket(c.ip, opts);
            socket1.on("res", getdataListener);
            socket1.io().reconnection(false);

            // socket1.connect();

            socket1.on(Socket.EVENT_CONNECT_TIMEOUT, args -> Logger.print(TAG, "<<<<<<...............EVENT_CONNECT_TIMEOUT"));

            // this is the emit from the server
            // this is the emit from the server
            socket1.on(Socket.EVENT_CONNECT, args -> {
                Logger.print(TAG, "CONNECTED...............111 " + c.isFinishGlobalLoader);
                if (socket1 == null && (socket1.id() == null || socket1.id().equals("") || socket1.id().equals("null"))) {
                    return;
                }

                if (!hasSocketId) {
                    hasSocketId = true;
                } else {
                    return;
                }

                isInIdleMode = false;
                open = false;
                ideal = false;
                emit("hb", "");


                c.isShowReconnectTextOnLoader = false;
                isDisconnectByUser = false;
                try {
                    ServerError.CloseConfirmationDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Logger.print(TAG, "CONNECTED...............222 " + c.isFinishGlobalLoader);
                if (c.isFinishGlobalLoader) {
                    c.isFinishGlobalLoader = true;
                    sendMessage(ResponseCodes.FinishLoader, "", false);
                }

                if (reconnectTimer != null) {
                    reconnectTimer.cancel();
                    reconnectTimer = null;
                }
                Logger.print(TAG, "FROMMMMMMMMMMMMMMMMMMMMM => " + c.isRejoin);
                // if (c.isRejoin) {

                if (!(activity instanceof Activity_CallBreakList)) {

                    Logger.print(TAG, "<<<<....ZZZZ EVENT_CONNECT 2");
                    sendMessage(ResponseCodes.FinishLoader, "", false);
                    if (PreferenceManager.get_id().length() > 0) {
                        callSignUpEvent();
                    }
//                    CheckUserValidOrNot();

                } else {
                    Logger.print(TAG, "<<<<....ZZZZ EVENT_CONNECT 3");

                    if (c.connectioncnt == 0) {

                        Logger.print(TAG, "<<<<....ZZZZ EVENT_CONNECT 4 " + (Activity_CallBreakList.getHandler() != null));

                        if (Activity_CallBreakList.getHandler() != null) {
                            Message msg = new Message();
                            msg.what = ResponseCodes.SocketConnectionResp;
                            Activity_CallBreakList.getHandler().sendMessage(msg);
                        }
                    }
                }

                c.connectioncnt++;

                activity.runOnUiThread(() -> {
                    startChecking();
                    IdleTimer();
                });

                /*} else {

                    if (c.connectioncnt == 0) {
                        Message msg = new Message();
                        msg.what = ResponseCodes.CreateQueueResp;
                        Login.handler.sendMessage(msg);
                    }
                    c.connectioncnt++;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            startChecking();
                            startIdleChecking();
                        }
                    });
                }*/
            })
                    .on(Socket.EVENT_RECONNECT, arg0 -> Logger.print("<<<<<<<<<<............... EVENT_RECONNECT"))

                    .on(Socket.EVENT_CONNECT_ERROR, args -> Logger.print("<<<<<<<<<<<<............... EVENT_CONNECT_ERROR"))

                    .on(Socket.EVENT_CONNECT_TIMEOUT, args -> Logger.print("<<<<<<<<<<............... EVENT_CONNECT_TIMEOUT"))

                    .on(Socket.EVENT_ERROR, args -> Logger.print("<<<<<<<<<<<<......................... EVENT_ERROR"))

                    .on(Socket.EVENT_DISCONNECT, args -> {

                        Logger.print("<<<<<<<<<<<<<.............EVENT_DISCONNECT " + c.isRejoin);

                        hbcnt = 0;
                        if (idleTimer != null) {
                            idleTimer.cancel();
                            idleTimer = null;
                        }
                        if (handlerPing != null && runnable != null) {
                            handlerPing.removeCallbacks(runnable);
                        }

                        if (C.isMaintenanaceScreenOpen) {
                            return;
                        }

                        // if (c.isRejoin) {
                        hasSocketId = false;

                        if (!ideal && !open && !isDisconnectByUser) {
                            /*sendMessage(ResponseCodes.WINNER_FINISH, "", false);*/
                            c.isFinishGlobalLoader = true;

                            if (CallBreak_PlayingScreen.handler != null) {
                                Message message = new Message();
                                message.what = ResponseCodes.STORE_USER_CARD;
                                CallBreak_PlayingScreen.handler.sendMessage(message);
                                if (activity instanceof CallBreak_PlayingScreen)
                                    c.isFinishGlobalLoader = false;
                            }

                            Logger.print("<<<<....ZZZZ EVENT_DISCONNECT 2");

                            isDisconnectByUser = false;

                            c.connectWhenDisconnect++;
                            disconnect();

                            activity.runOnUiThread(() -> {
                                c.isShowReconnectTextOnLoader = true;
                                JSONObject jObj = new JSONObject();
                                try {
                                    jObj.put("message", "Reconnecting..");
                                    sendMessage(ResponseCodes.StartLoader, jObj.toString(), true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ReConnecting();
                            });
                            return;
                        }

                        if (ideal) {
                            isInIdleMode = true;
                            activity.runOnUiThread(() -> {
                                // show idle popup if user get 10 minutes of ideal
                                if (!isScreenPortrait()) {
                                    Logger.print("<<<<<<<<<<<< IDLE SCREEN SHOW");
                                    Intent intent = new Intent(activity.getApplicationContext(), Activity_CallBreakList.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("ShowError", true);
                                    intent.putExtra("value", false);
                                    activity.startActivity(intent);
                                } else {
                                    server_message = "No Internet connection available.\n Please make sure your device is connected \nto the Internet.";
                                    new ServerError(activity, server_message);
//                                    new ServerError(activity, server_message);
                                }
                                // activity.finish();
                            });
                            ideal = false;
                        }
                    });

            socket1.connect();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    private void callSignUpEvent() {
        JSONObject jObj = new JSONObject();
        try {
//            if (PreferenceManager.get_id().length() > 0) {
//                jObj.put(Parameters.User_Id, PreferenceManager.get_id());
//            }
            jObj.put(Parameters.Device_Type, "android");
//            jObj.put(Parameters.Device_Id, PreferenceManager.getRegistrationId());
            jObj.put(Parameters.mobileNumber, PreferenceManager.getUserMobileNo());
            EmitManager.Process(jObj, Events.Signup_Process);
//            globalLoaderShow(getResources().getString(R.string.PleaseWait));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ReConnecting() {
        Logger.print("<<<<....ZZZZ 0 " + isConnected());
        if (isConnected()) {
            return;
        }

        Logger.print("<<<<....ZZZZ 1");


        //sendMessage(ResponseCodes.Re_establishing_lostCo, "", false);
        {
            Logger.print("<<<<....ZZZZ 3");

            if (reconnectTimer != null) {
                reconnectTimer.cancel();
                reconnectTimer = null;
            }

            reconnectCounter = 0;

            reconnectTimer = new Timer();

            reconnectTimer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {

                    reconnectCounter--;

                    Logger.print("<<<<....ZZZZ 4");

                    if (reconnectCounter < 0) {

                        if (reconnectTimer != null) {
                            reconnectTimer.cancel();
                            reconnectTimer = null;
                        }
                        Logger.print("<<<<....ZZZZ 5");
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Logger.print("<<<<....ZZZZ 555");
                                c.isShowReconnectTextOnLoader = false;
                                server_message = "No Internet connection available.\n Please make sure your device is connected \nto the Internet.";
                                new ServerError(activity, server_message);
                            }
                        });
                    } else {

                        Logger.print("<<<<....ZZZZ 6");

                        if (isConnected()) {

                            if (reconnectTimer != null) {
                                reconnectTimer.cancel();
                                reconnectTimer = null;
                            }

                            Logger.print("<<<<....ZZZZ 7");

                        } else {
                            Logger.print("<<<<....ZZZZ 8");
                            c.connectioncnt = 0;
                            disconnect();
                            Connected();
                        }
                    }
                }
            }, 0, delay);
        }
    }

    private Boolean isScreenPortrait() {
        return activity.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                || activity.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
    }

    // checking that user is in playing screen or not.
    private void checkReconnectInPlaying(String json) {

        try {

            JSONObject responce = new JSONObject(json).getJSONObject("data");

            if (responce.has("rejoin")) {

                Logger.print(TAG, "SP in Connection Manager 6 => " + PreferenceManager.isPlayingScreenOpen);

                int rejoin = responce.getInt("rejoin");
                String rejoinID = responce.getString("rejoinID");

                if (rejoin == 1 || !rejoinID.equals("")) {

                    JSONObject data = new JSONObject();
                    data.put("_id", PreferenceManager.get_id());
                    c.isJoinedPreviousTable = true;
                    EmitManager.Process(data, Events.RejoinTable);

                } else {
                    Message me = new Message();
                    me.what = ResponseCodes.Finish_Playscreen;
                    if (CallBreak_PlayingScreen.handler != null) {
                        CallBreak_PlayingScreen.handler.sendMessage(me);
                    }
                }

            } else {

                Logger.print(TAG, "HAS TO FINISH PLAYING SCREEN");
                Message me = new Message();
                me.what = ResponseCodes.Finish_Playscreen;
                if (CallBreak_PlayingScreen.handler != null) {
                    CallBreak_PlayingScreen.handler.sendMessage(me);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Login with facebook user
    protected void LoginAsFacebookUser() {

        JSONObject jObj = new JSONObject();

        try {

            if (PreferenceManager.get_id().length() > 0) {
                jObj.put(Parameters.User_Id, PreferenceManager.get_id());
            }

            jObj.put(Parameters.User_Name, PreferenceManager.getUserName());
            jObj.put(Parameters.User_LoginType, "FB");
            jObj.put(Parameters.FB_Id, PreferenceManager.get_FbId());
            jObj.put(Parameters.ReferrerCode, c.ReferrerCode);
            jObj.put(Parameters.User_Email, PreferenceManager.getUserEmail());
            jObj.put(Parameters.FB_Token, PreferenceManager.get_FB_accessToken());
            jObj.put(Parameters.Permissions, new JSONArray());

        } catch (Exception e) {
            e.printStackTrace();
        }

//        EmitManager.Process(jObj, Events.Signup_Process);

    }

    private void GotEvent(String data) throws JSONException {
        JSONObject jObject = new JSONObject(data);
        String event = jObject.getString(Events.EventName);
        Logger.print(TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<< Receiving : " + data);
        EventReceived(event, plainText);
    }

    public boolean isConnected() {

        try {

            if (socket1 != null) {
                return socket1.connected();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;

    }

    public String getTimeStamp(long millis) {

        Date date = new Date(millis);
        DateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(date);

    }

    private void EventReceived(String event, String data) throws JSONException {


        // System.out.println("Receive data:"+data);

        JSONObject jObject;

        switch (event) {

            case Events.Signup_Process:

                try {
                    JSONObject dataObject = new JSONObject(data);
                    /*if (!dataObject.optBoolean("flag", false) && dataObject.optInt(Parameters.errorCode) == ResponseCodes.ERROR_ACCOUNT_SUSPEND) {

                        Logger.print(TAG, "SP in Connection Manager ACCOUNT IS SUSPENDED");
                        sendMessage(ResponseCodes.AccountSuspended, data, true);
                        break;
                    }*/
                    if (!dataObject.getBoolean(Parameters.Flag) && dataObject.has(Parameters.errorCode) && dataObject.getInt(Parameters.errorCode) == 1058) {
                        PreferenceManager.setIsUserAlreadyLogin(false);
                        PreferenceManager.setUserMobileNo("");
                        PreferenceManager.set_id("");
                        Intent history = new Intent(activity.getApplicationContext(), Activity_CallBreakList.class);
                        activity.startActivity(history);
                        /*if (!(activity instanceof Activity_NewDashBoard)) {
                            activity.finish();
                        }*/
                        break;
                    }
                    /*if (dataObject.getBoolean(Parameters.Flag)) {
                        JSONObject d = dataObject.getJSONObject("data");
                        if (d.has("isNew")) {
                            if (d.getBoolean("isNew")) {
                                Map<String, Object> eventValue = new HashMap<>();
                                eventValue.put("User_Id", d.has("id") ? d.getString("id") : "");
                                eventValue.put("referral_code/coupon_code", d.has("refer_code") && !d.get("refer_code").equals("") ? d.get("refer_code") : "NO");
                                //        eventValue.put(AFInAppEventParameterName.REVENUE, -200);
                                AppsFlyerLib.getInstance().trackEvent(context, "af_complete_registration", eventValue);

                            }
                        }
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                c.showBootValueScreen = false;

                c.jsonData.setSignUpData(data);

                Logger.print(TAG, "SP in Connection Manager 0 => " + PreferenceManager.isPlayingScreenOpen);

                if (c.connectWhenDisconnect > 0) {
                    Logger.print(TAG, "SP in Connection Manager 1=> " + PreferenceManager.isPlayingScreenOpen);
                    Logger.print(TAG, "SP in Connection Manager 1.1 => " + (CallBreak_PlayingScreen.handler != null));

                    if (CallBreak_PlayingScreen.handler != null) {
                        Message me = new Message();
                        me.what = ResponseCodes.UpdateChipsResp;
                        if (CallBreak_PlayingScreen.handler != null) {
                            CallBreak_PlayingScreen.handler.sendMessage(me);
                        }
                        checkReconnectInPlaying(data);

                    }/* else if (PreferenceManager.isPlayingScreenOpen || PlayScreen.handler != null) {
                        Logger.print(TAG, "SP in Connection Manager 2=> " + PreferenceManager.isPlayingScreenOpen);
                        Message me = new Message();
                        me.what = ResponseCodes.UpdateChipsResp;
                        if (PlayScreen.handler != null) {
                            PlayScreen.handler.sendMessage(me);
                        }
                        checkReconnectInPlaying(data);

                    } */
                    else {
                        Logger.print(TAG, "SP in Connection Manager 3=> " + PreferenceManager.isPlayingScreenOpen);
                        sendMessage(ResponseCodes.FinishLoader, "", false);
                        sendMessage(ResponseCodes.SignupResp, data, true);
                    }

                } else {
                    Logger.print(TAG, "SP in Connection Manager 4=> " + PreferenceManager.isPlayingScreenOpen);
                    /* when user signin from Login class */
                    sendMessage(ResponseCodes.SignupResp, data, true);
                    if (Activity_CallBreakList.handler != null) {
                        Logger.print(TAG, "SP in Connection Manager L 1 DASHBOARD HANDLER NOT NULL");
                        Message me = new Message();
                        me.what = ResponseCodes.SignupResp;
                        me.obj = data;
                        if (Activity_CallBreakList.handler != null) {
                            Activity_CallBreakList.handler.sendMessage(me);
                        }
                    }
                }
                break;


            /*case Events.AddCashSuccessfully:
                Logger.print("_Get_Add_Cash_Order_Details >>> ACS  >>> RECIEVED >>>   " + data);
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONObject d = jsonObject.getJSONObject("data");
                    Map<String, Object> eventValue = new HashMap<>();
//                      eventValue.put("User_Id", d.getString("UserId"));
//                      eventValue.put("first_deposit_amount", d.getInt("am`ount"));
                    eventValue.put("deposit_count", d.has("depositCounter") ? d.getString("depositCounter") : "0");
                    eventValue.put("deposit_value", d.getJSONObject("value").has("amount")
                            ? d.getJSONObject("value").getString("amount") : "");
                    eventValue.put("coupon_used", d.getJSONObject("value").has("couponCode")
                            && !d.getJSONObject("value").getString("couponCode").equals("")
                            ? d.getJSONObject("value").getString("couponCode") : "NO");

                    *//*if (d.getJSONObject("value").has("status")
                            && d.getJSONObject("value").getString("status").equalsIgnoreCase("CHARGED")) {
                        if (d.has("depositCounter") && d.getInt("depositCounter") == 1) {
                            AppsFlyerLib.getInstance().trackEvent(context, "Purchase", eventValue);
                        }
                        AppsFlyerLib.getInstance().trackEvent(context, "Deposit", eventValue);
                    }*//*

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                sendMessage(ResponseCodes.AddCashSuccessfullyResp, data, true);
                break;

            case Events.INVITE_FRIEND_OPEN_FROM_SETTINGS:
                sendMessage(ResponseCodes.INVITE_FRIEND_OPEN_FROM_SETTINGS, data, true);
                break;

            case Events.INVITE_FRIEND_REDEEM_CHIPS:

                JSONObject jsonObject1 = new JSONObject(data);
                JSONObject data1 = jsonObject1.optJSONObject(Parameters.data);

                int flag1 = data1.optInt("flg");

                if (flag1 == 4) {
                    if (Activity_NewDashBoard.handler != null) {
                        Message msg = new Message();
                        msg.what = ResponseCodes.GetMessageCountResp;
                        Activity_NewDashBoard.handler.sendMessage(msg);
                    }
                } else {
                    sendMessage(ResponseCodes.INVITE_FRIEND_REDEEM_CHIPS, data, true);
                }

                break;*/

            case Events.NewUserRegister:
                Logger.print("_SIGN UP >>> NUR  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.NewUserRegisterResp, data, true);
                break;
            case Events.NewUserLogin:
                Logger.print("_SIGN UP >>> NUL  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.NewUserLoginResp, data, true);
                break;
            case Events.CheckMobileNumber:
                Logger.print("_SIGN UP >>> CMN  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.CheckMobileNumberResp, data, true);
                break;
            case Events.CheckReferelOrCouponCode:
                Logger.print("_SIGN UP >>> CROCC  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.CheckReferelOrCouponCodeResp, data, true);
                break;
            case Events.CheckUserName:
                Logger.print("_SIGN UP >>> CUN  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.CheckUserNameResp, data, true);
                /*if (EditUserIdFragment.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.CheckUserNameResp;
                    msg.obj = data;
                    EditUserIdFragment.handler.sendMessage(msg);
                }*/
                break;
            case Events.VerifyEmail:
                Logger.print("_Profile >>> VEmail  >>> RECIEVED >>>   " + data);
//                sendMessage(ResponseCodes.VerifyEmailResp, data, true);
                /*if (UserProfileFragment.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.VerifyEmailResp;
                    msg.obj = data;
                    UserProfileFragment.handler.sendMessage(msg);
                }*/

                break;
            /*case Events.VerifyOTP:
                Logger.print("_Verify OTP >>> VOTP  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.VerifyOTPResp, data, true);
                if (WithdrawOTPFragment.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.VerifyOTPResp;
                    msg.obj = data;
                    WithdrawOTPFragment.handler.sendMessage(msg);
                }
                break;

            case Events.ResendOTP:
                Logger.print("_Resend OTP >>> ROTP  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.ResendOTPResp, data, true);
                if (WithdrawOTPFragment.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.ResendOTPResp;
                    msg.obj = data;
                    WithdrawOTPFragment.handler.sendMessage(msg);
                }
                break;*/

            case Events.OnlineUserCounter:
                Logger.print("_Resend OTP >>> ROTP  >>> RECIEVED >>>   " + data);
                try {
                    JSONObject object = new JSONObject(data);
                    JSONObject datas = object.getJSONObject(Parameters.data);
                    if (datas.has(Parameters.counter)) {
                        c.totalActivUser = datas.getString(Parameters.counter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendMessage(ResponseCodes.OnlineUserCounterResp, data, true);
                break;

            /*case Events.GetAddCashDetails:
                Logger.print("_Add cash >>> CWD  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.GetAddCashDetailsResp, data, true);
                if (WithdrawVerifyFragment.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.GetAddCashDetailsResp;
                    msg.obj = data;
                    WithdrawVerifyFragment.handler.sendMessage(msg);
                }
                break;

            case Events.CashWithdrawDetails:
                Logger.print("_Withdraw cash >>> GADC  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.CashWithdrawDetailsResp, data, true);
                if (Activity_CallBreakScoreBoard.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.CashWithdrawDetailsResp;
                    msg.obj = data;
                    Activity_CallBreakScoreBoard.handler.sendMessage(msg);
                }
                break;

            case Events.CashWithdrawRequest:
                Logger.print("_Withdraw cash >>> CWR  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.CashWithdrawRequestResp, data, true);
                if (WithdrawAmountFragment.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.CashWithdrawRequestResp;
                    msg.obj = data;
                    WithdrawAmountFragment.handler.sendMessage(msg);
                }
                break;

            case Events.GetUserTrancationHistory:
                Logger.print("_Transaction History >>> GUTH  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.GetUserTrancationHistoryResp, data, true);
                break;

            case Events.WithdrawRequestCancel:
                Logger.print("_Transaction History >>> WRC  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.WithdrawRequestCancelResp, data, true);
                break;

            case Events.MyReferralInfo:
                Logger.print("_Transaction History >>> MRI  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.MyReferralInfoResp, data, true);
                break;

            case Events.GetLoyaltyPointDetails:
                Logger.print("_Loyalty_Point_Details >>> GLPD  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.GetLoyaltyPointDetailsResp, data, true);
                break;

            case Events.GetRewardPointDetails:
                Logger.print("_Reward_Point_Details >>> GRPD  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.GetRewardPointDetailsResp, data, true);
                break;

            case Events.UserRewardPointBurn:
                Logger.print("_UserRewardPointBurn >>> URPB  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.UserRewardPointBurnResp, data, true);
                break;

            case Events.GetGameBonusSummaryDetails:
                Logger.print("_Get_GameBonus_Summary_Details >>> GGBSD  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.GetGameBonusSummaryDetailsResp, data, true);
                break;

            case Events.GetAddCashOrderDetails:
                Logger.print("_Get_Add_Cash_Order_Details >>> GACOD  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.GetAddCashOrderDetailsResp, data, true);
                break;*/

            case Events.GetPlayingtablecategoryList:
                Logger.print("_Get_Table_List >>> GPCL  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.GetPlayingtablecategoryListResp, data, true);
                break;

            case Events.JoinTables:
                Logger.print("_JoinTables_Response >>> JTB  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.JoinTablesResp, data, true);
                break;

            case Events.LockButtonClickEvent:
                Logger.print("_LBC_Response >>> LBC  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.LockButtonClickEventResp, data, true);
                break;

            case Events.SuspendeUser:
                Logger.print("_SuspendeUser_Response >>> SU  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.SuspendeUserResp, data, true);
                break;

            case Events.UserClickFinishButton:
                Logger.print("_UserClickFinishButton_Response >>> UCFB  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.UserClickFinishButtonResp, data, true);
                break;

            case Events.UserUpdateRewardPoint:
                Logger.print("_UserUpdateRewardPoint_Response >>> UURP  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.UserUpdateRewardPointResp, data, true);
                break;

            /*case Events.ContinueDealRummyNextRound:
                Logger.print("_ContinueDealRummyNextRound_Response >>> CDRNR  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.ContinueDealRummyNextRoundResp, data, true);
                break;

            case Events.CheckContinueDealRound:
                Logger.print("_CheckContinueDealRound_Response >>> CCDR  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.CheckContinueDealRoundResp, data, true);
                break;

            case Events.EliminateInPoolMode:
                Logger.print("_EliminateInPoolMode_Response >>> EIP  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.EliminateInPoolModeResp, data, true);
                break;

            case Events.PoolMoodWiner:
                Logger.print("_PoolMoodWiner_Response >>> PMWIN  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.PoolMoodWinerResp, data, true);
                break;

            case Events.CleverTapData:
                Logger.print("_CleverTapData_Response    >>> CTD  >>> RECIEVED >>>   " + data);
                if (Activity_NewDashBoard.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.CleverTapDataResp;
                    msg.obj = data;
                    Activity_NewDashBoard.handler.sendMessage(msg);
                } else {
                    c.sendUserDetailToCleverTap(activity.getApplicationContext(), data);
                }
//                sendMessage(ResponseCodes.CleverTapDataResp, data, true);
                break;

            case Events.AppsflyerData:
                Logger.print("_AppsflyerData_Response    >>> AFD  >>> RECIEVED >>>   " + data);
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONObject d = jsonObject.getJSONObject("data");
                    Map<String, Object> eventValue = new HashMap<>();
                    eventValue.put("Rummy Games Played", d.has("rpc") ? d.getString("rpc") : 0);
                    eventValue.put("Call break played", d.has("cpc") ? d.getString("cpc") : 0);
                    AppsFlyerLib.getInstance().trackEvent(context, "Playstore", eventValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case Events.NewUpdateAvailable:
                Logger.print("_NewUpdateAvailable_Response >>> NUA  >>> RECIEVED >>>   " + data);
                c.updateAvailable = true;
                sendMessage(ResponseCodes.NewUpdateAvailableResp, data, true);
                break;*/

            case Events.UserWalletValue:
                Logger.print("_UserWalletValue_Response >>> UWV  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.UserWalletValueResp, data, true);
                break;

            case Events.CPP:
                Logger.print("_CPP_Response >>> CPP  >>> RECIEVED >>>   " + data);
                sendMessage(ResponseCodes.CPPRes, data, true);
                break;

            case Events.My_Profile:
                sendMessage(ResponseCodes.ProfileResp, data, true);
                /*if (WithdrawVerifyFragment.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.ProfileResp;
                    msg.obj = data;
                    WithdrawVerifyFragment.handler.sendMessage(msg);
                }*/
                break;


            /*case Events.RaiseBetBootValue:
                sendMessage(ResponseCodes.RaiseBetBootValueResp, data, true);
                break;*/

            /*case Events.OtherUserDeclare:
                sendMessage(ResponseCodes.OtherUserDeclaredResp, data, true);
                break;

            case Events.WinnerOfGame:
                sendMessage(ResponseCodes.WinnerOfGame, data, true);
                break;*/

            case Events.ScoreBoard:
                sendMessage(ResponseCodes.ScoreBoardResp, data, true);
                break;

            /*case Events.DashboardOnlineBuddeis:
                Logger.print(TAG, "Get online friend counter");
                // sendMessage(ResponseCodes.OnlineFriendsCounterResp, data, true);

                if (Activity_NewDashBoard.handler != null) {
                    Logger.print(TAG, "Get online friend counter 1");
                    Message msg = new Message();
                    msg.what = ResponseCodes.DashboardOnlineBuddeisResp;
                    msg.obj = data;
                    Activity_NewDashBoard.handler.sendMessage(msg);
                }
                break;*/

            case Events.LeaveWitoutDropChipsCut:
                sendMessage(ResponseCodes.LeaveWitoutDropChipsCutResp, data, true);
                break;

            case Events.UniqueIdSearch:
                sendMessage(ResponseCodes.UniqueIdSearchResp, data, true);
                break;

            case Events.Boot_Values:
                if (data.length() > 0) {
                    if (c.entryData.size() > 0) {
                        c.entryData.clear();
                    }
                    JSONObject jObjEntryData = new JSONObject(data);
                    JSONArray entryDataArray = jObjEntryData.getJSONArray(Parameters.data);
                    for (int i = 0; i < entryDataArray.length(); i++) {
                        JSONObject myEntry = entryDataArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<>();
                        map.put(Parameters.Chips, myEntry.getString(Parameters._id));
                        //map.put(Parameters.roundpnt, myEntry.getString(Parameters.roundpnt));
                        map.put(Parameters.CategoryId, myEntry.getString(Parameters.CategoryId));
                        c.entryData.add(map);
                    }
                }

                sendMessage(ResponseCodes.Boot_ValuesResp, data, true);
                break;

            case Events.WinnerDeclared:
                sendMessage(ResponseCodes.WinnerDeclaredResp, data, true);
                break;

            /*case Events.InvalidDeclare:
                sendMessage(ResponseCodes.InvalidDeclareResp, data, true);
                break;

            case Events.Finish:
                sendMessage(ResponseCodes.Finish, data, true);
                break;

            case Events.OnlineFriendNotification:
                sendMessage(ResponseCodes.OnlineFriendNotificationResp, data, true);
                break;

            case Events.OnlineFriendsCounter:
                Logger.print(TAG, "Get online friend counter");
                // sendMessage(ResponseCodes.OnlineFriendsCounterResp, data, true);

                if (Activity_NewDashBoard.handler != null) {
                    Logger.print(TAG, "Get online friend counter 1");
                    Message msg = new Message();
                    msg.what = ResponseCodes.OnlineFriendsCounterResp;
                    msg.obj = data;
                    Activity_NewDashBoard.handler.sendMessage(msg);
                }
                break;*/

            case Events.SwitchTable:
                sendMessage(ResponseCodes.SwitchTableResp, data, true);
                break;


            case Events.ChangeName:
                sendMessage(ResponseCodes.ChangeNameResp, data, true);
                break;

            /*case Events.DropTable:
                sendMessage(ResponseCodes.DropTableResp, data, true);
                break;

            case Events.CHALLENGE_COMING:
                sendMessage(ResponseCodes.CHALLENGE_COMING, data, true);
                break;*/

            /*case Events.GetPlayingTableCategoryList:
                jObject = new JSONObject(data);
                c.jsonData.TableList = jObject.getJSONArray(Parameters.data);
                sendMessage(ResponseCodes.GetAllTablesResp, data, true);
                break;

            case Events.GetDealTableCategoryList:
                jObject = new JSONObject(data);
                c.jsonData.DealTableList = jObject.getJSONArray(Parameters.data);
                sendMessage(ResponseCodes.GetDealTableCategoryListResp, data, true);
                break;

            case Events.POOL_MODE_BET_LIST:
                jObject = new JSONObject(data);
                c.jsonData.PoolTableList = jObject.getJSONArray(Parameters.data);
                sendMessage(ResponseCodes.PoolModeBetListResp, data, true);
                break;

            case Events.GetBetTableCategoryList:
                jObject = new JSONObject(data);
                c.jsonData.BetTableList = jObject.getJSONArray(Parameters.data);
                sendMessage(ResponseCodes.GetBetTableCategoryListResp, data, true);
                break;

            case Events.GetQuickModeTableCategoryList:
                jObject = new JSONObject(data);
                c.jsonData.QuickModeTableList = jObject.getJSONArray(Parameters.data);
                sendMessage(ResponseCodes.GetQuickModeTableCategoryListResp, data, true);
                break;*/

            case Events.GetTableInfo:
                try {
                    jObject = new JSONObject(data);
                    c.TableId = jObject.getJSONObject(Parameters.data).getString(Parameters._id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendMessage(ResponseCodes.GetTableInfoResp, data, true);
                break;

            case Events.JoinTable:
                sendMessage(ResponseCodes.JoinTableResp, data, true);
                break;

            case Events.Notification:
                sendMessage(ResponseCodes.NotificationResp, data, true);
                break;

            case Events.LeaveTable:
                if (CallBreak_PlayingScreen.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.LeaveTableResp;
                    msg.obj = data;
                    CallBreak_PlayingScreen.handler.sendMessage(msg);
                } else
                    sendMessage(ResponseCodes.LeaveTableResp, data, true);
                break;

            /*case Events.TwoDaysBack:
                sendMessage(ResponseCodes.TwoDaysBackResp, data, true);
                break;

            case Events.UNBlockUserlist:
                sendMessage(ResponseCodes.UnBlockUserListResp, data, true);
                break;*/

            case Events.RoundTimerStart:
                if (CallBreak_PlayingScreen.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.RoundTimerStartResp;
                    msg.obj = data;
                    CallBreak_PlayingScreen.handler.sendMessage(msg);
                }
                sendMessage(ResponseCodes.RoundTimerStartResp, data, true);
                break;

            /*case Events.draw:
                sendMessage(ResponseCodes.DrawResp, data, true);
                break;

            case Events.round:
                sendMessage(ResponseCodes.RoundResp, data, true);
                break;*/

            case Events.CollectBootValue:
                sendMessage(ResponseCodes.CollectBootValueResp, data, true);
                break;

            case Events.StartDealingCard:
                sendMessage(ResponseCodes.StartDealingResp, data, true);
                break;

            case Events.UserTurnStarted:
                sendMessage(ResponseCodes.UserTurnStartedResp, data, true);
                break;

            case Events.TurnTimeout:
                sendMessage(ResponseCodes.TurnTimeoutResp, data, true);
                break;

            case Events.TurnTaken:
                sendMessage(ResponseCodes.TurnTakenResp, data, true);
                break;

            case Events.SeeMyCard:
                if (CallBreak_PlayingScreen.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.SeeMyCardResp;
                    msg.obj = data;
                    CallBreak_PlayingScreen.handler.sendMessage(msg);
                }
                sendMessage(ResponseCodes.SeeMyCardResp, data, true);
                break;

            /*case Events.CardSeen:
                sendMessage(ResponseCodes.CardSeenResp, data, true);
                break;

            case Events.CardPacked:
                sendMessage(ResponseCodes.CardPackedResp, data, true);
                break;

            case Events.ShowOnGame:
                sendMessage(ResponseCodes.ShowOnGameResp, data, true);
                break;*/

            case Events.DealNumber:
                if (CallBreak_PlayingScreen.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.DealNumberResp;
                    msg.obj = data;
                    CallBreak_PlayingScreen.handler.sendMessage(msg);
                }
                sendMessage(ResponseCodes.DealNumberResp, data, true);
                break;

            case Events.SelectBid:
                sendMessage(ResponseCodes.SelectBidResp, data, true);
                break;

            case Events.CardMove:
                sendMessage(ResponseCodes.CardMoveResp, data, true);
                break;

            case Events.BidWinner:
                sendMessage(ResponseCodes.BidWinnerResp, data, true);
                break;
            case Events.RSD:
                sendMessage(ResponseCodes.RSDResp, data, true);
                break;

            case Events.GetRoundDetails:
                if (CallBreak_PlayingScreen.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.GetRoundDetailsResp;
                    msg.obj = data;
                    CallBreak_PlayingScreen.handler.sendMessage(msg);
                }
                break;

            case Events.CallBreakWinnerDeclared:
                if (Activity_CallBreakScoreBoard.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.CallBreakWinnerDeclaredResp;
                    msg.obj = data;
                    Activity_CallBreakScoreBoard.handler.sendMessage(msg);
                } else
                    sendMessage(ResponseCodes.CallBreakWinnerDeclaredResp, data, true);
                break;

            case Events.DisplayCashWithdrawPopup:
                if (Activity_CallBreakScoreBoard.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.DisplayCashWithdrawPopupResp;
                    msg.obj = data;
                    Activity_CallBreakScoreBoard.handler.sendMessage(msg);
                } else {
                    c.DisplayWithdrawPopup = true;
                    jObject = new JSONObject(data);
                    c.DisplayWithdrawPopupMsg = jObject.getJSONObject(Parameters.data).optString(Parameters.msg);
                    sendMessage(ResponseCodes.DisplayCashWithdrawPopupResp, data, true);
                }
                break;

            case Events.UpdatedUserGameBonus:
                sendMessage(ResponseCodes.UpdatedUserGameBonusResp, data, true);
                break;

            case Events.UpdateUserLoyaltyLevelInfo:
                sendMessage(ResponseCodes.UpdateUserLoyaltyLevelInfoResp, data, true);
                break;

            case Events.GetGameConfig:
                jObject = new JSONObject(data);
                c.jsonData.setConfigData(jObject);
                sendMessage(ResponseCodes.GetGameConfigResp, data, true);
                break;

            /*case Events.HandleAllRequest:
                Logger.print(TAG, "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE 0");
                sendMessage(ResponseCodes.HandleAllRequestResp, data, true);
                break;*/

            case Events.UpdateChips:

                boolean sendUctoAllHandler = true;
                jObject = new JSONObject(data);
                JSONObject jObj2 = jObject.getJSONObject(Parameters.data);
                if (jObj2.has("t")) {
                    if (jObj2.getString("t").equalsIgnoreCase("lucky Winner") || jObj2.getString("t").equalsIgnoreCase("Weekly Winner") || jObj2.getString("t").equalsIgnoreCase("DailyBonus")) {
                        c.collectandCollectShareBonus = jObj2.getLong(Parameters.Chips) - c.Chips;
                        sendUctoAllHandler = false;
                    }
                }
                c.Chips = jObj2.getLong(Parameters.Chips);

                if (Activity_CallBreakList.handler != null) {
                    Message msg = new Message();
                    msg.what = ResponseCodes.UpdateChipsResp;
                    msg.obj = data;
                    Activity_CallBreakList.handler.sendMessage(msg);
                }

                if (sendUctoAllHandler) {
                    sendMessage(ResponseCodes.UpdateChipsResp, data, true);
                }

                c.showBootValueScreen = false;
                EmitManager.Process(new JSONObject(), Events.Boot_Values);
                break;

            case Events.NewConnectionCreated:
                try {
                    Logger.print(TAG, "S 14");
                    sendMessage(ResponseCodes.FinishLoader, "", false);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isDisconnectByUser = true;
                            isDoubleLogin = true;
                            Intent intent = new Intent(activity.getApplicationContext(), Activity_CallBreakList.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("EXIT", true);
                            activity.startActivity(intent);

                            if (!(activity instanceof Activity_CallBreakList)) {
                                activity.finish();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            /*case Events.DropCardCutChips:
                sendMessage(ResponseCodes.DropCardCutChipsResp, data, true);
                break;*/

            case Events.ExiststanceInPrevTable:
                sendMessage(ResponseCodes.ExiststanceInPrevTableResp, data, true);
                break;

            /*case Events.UpdateProfileInfo:
                jObject = new JSONObject(data);
                if (jObject.getBoolean(Parameters.Flag)) {
                    c.jsonData.userInfo.setUserName(jObject.getJSONObject(Parameters.data).getString(Parameters.User_Name));
                    PreferenceManager.setUserName(c.jsonData.userInfo.getUserName());
//                    sendMessage(ResponseCodes.UpdateProfileInfoResp, data, true);
                    if (Activity_UserProfile.handler != null) {
                        Message m = new Message();
                        m.what = ResponseCodes.UpdateProfileInfoResp;
                        m.obj = data;
                        Activity_UserProfile.handler.sendMessage(m);
                    }
                }
                if (AccountInfoFragment.handler != null) {
                    Message m = new Message();
                    m.what = ResponseCodes.UpdateProfileInfoResp;
                    m.obj = data;
                    AccountInfoFragment.handler.sendMessage(m);
                }

                break;*/

            case Events.MMN:
                sendMessage(ResponseCodes.MMNResp, data, true);
                final String message = new JSONObject(data).getJSONObject(Parameters.data).getString(Parameters.Message);
                Maintainance_Start = new JSONObject(data).getJSONObject(Parameters.data).getLong("StartAfter");
                Maintainance_End = new JSONObject(data).getJSONObject(Parameters.data).getLong("RemoveAfter");
                Remove_After = Maintainance_End - Maintainance_Start;
                startMaintainanceTimer();

                activity.runOnUiThread(() -> {
                    try {
                        new NewCommonDialog.Builder()
                                .setPopupBg(R.drawable.cb_popup_bg)
                                .setTitle("Message")
                                .setMessage(message)
                                .setPositiveButton("Ok", null)
                                .create(activity)
                                .show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                break;

            /*case Events.PickFromPile:
                sendMessage(ResponseCodes.PickFromPileResp, data, true);
                break;

            case Events.PickFromThrownCards:
                sendMessage(ResponseCodes.PickFromThrownCardsResp, data, true);
                break;*/

            case Events.ThrowCardOnDeck:
                sendMessage(ResponseCodes.ThrowCardOnDeckResp, data, true);
                break;

            /*case Events.DrawInDealMode:
                sendMessage(ResponseCodes.DrawInDealModeResp, data, true);
                break;*/

            /*case Events.DealModeWinner:
                sendMessage(ResponseCodes.DealModeWinnerResp, data, true);
                break;*/

            case Events.HeartBeat:
                hbcnt = 0;
                break;

        }
    }

    private void GetDecodeEvet(String event) {
        try {
            keyBytes = c.key.getBytes("UTF-8");
            cipherData = AES.decrypt(c.ivBytes, keyBytes,
                    Base64.decode(event, Base64.DEFAULT));
            plainText = new String(cipherData, "UTF-8");
            GotEvent(plainText);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void startMaintainanceTimer() {

        if (MaintainanceTimer != null) {
            MaintainanceTimer.cancel();
        }
        maintenanceTimerStarted = true;
        MaintainanceTimer = new Timer();
        MaintainanceTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                Logger.print(TAG, "Maintanance Timer => " + Maintainance_Start);
                if (Maintainance_Start <= 0) {
                    this.cancel();
                    Logger.print(TAG, "Maintanance Timer Transfer to Maint. class => "
                            + Maintainance_Start);
                    /*activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(activity
                                    .getApplicationContext(), Maintenance.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("value", 0);
                            activity.startActivity(intent);
                            activity.finish();
                            xhandler = null;
                        }
                    });*/
                } else {

                    Maintainance_Start--;
                    showMaintainanceTimer = false;
                    if (Maintainance_Start <= 180) {
                        showMaintainanceTimer = true;
                        sendMessage(ResponseCodes.MaintainaceTimeRemaining,
                                null, false);

                    }
                }
            }
        }, 0, 1000);
    }

    public String getDurationString(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;

        seconds = seconds % 60;
        if (hours == 0) {
            return twoDigitString(minutes) + ":" + twoDigitString(seconds);
        }
        return twoDigitString(hours) + ":" + twoDigitString(minutes) + ":"
                + twoDigitString(seconds);
    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);

    }

    private void startChecking() {

        if (handlerPing != null && runnable != null) {

            System.out.println("startChecking......");
            handlerPing.removeCallbacks(runnable);

        }

        handlerPing = new Handler();
        handlerPing.postDelayed(runnable, 5000);

        // startIdleChecking();
    }

    private void IdleTimer() {

        try {
            if (idleTimer != null) {
                idleTimer.cancel();
                idleTimer = null;
            }

            idleTime = 600;
            idleTimer = new Timer();
            idleTimer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {

                    idleTime--;

                    if (idleTime < 0) {
                        if (idleTimer != null) {
                            idleTimer.cancel();
                            idleTimer = null;
                        }
                        open = true;
                        ideal = true;
                        disconnect();
                        //sendMessage(ResponseCodes.Conn_message_remove, "", false);
                        //GlobalLoader.FinishMe(110);
                        setManuallyClosed(true);
                    }
                }
            }, 0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    public boolean isManuallyClosed() {
        return ManuallyClosed;
    }*/

    private void setManuallyClosed(boolean flag) {
        ManuallyClosed = flag;
    }

    public void emit(String event, String args) {

        try {
            if (event.equals(Events.HeartBeat)) {
//                Logger.print("Event 1 => " + event);
            } else {
                Logger.print(TAG, "Event => " + event);
                EventCounter = 1;
            }
            // if (isConnected()) {
            try {
                socket1.emit(event, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(int Response, String object, boolean hasObject) {

        if (xhandler != null) {
            Message msg = new Message();
            msg.what = Response;
            if (hasObject) {
                msg.obj = object;
            }
            xhandler.sendMessage(msg);
        }
    }

    public void disconnect() {
        Logger.print("Connection disconnected...by user.........");
        // c.isErrorPopup = true;
        try {
            if (socket1.connected()) {
                socket1.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
