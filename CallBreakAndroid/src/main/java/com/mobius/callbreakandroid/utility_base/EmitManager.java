package com.mobius.callbreakandroid.utility_base;

import android.util.Base64;

import com.mobius.callbreakandroid.data_store.PreferenceManager;
import com.mobius.callbreakandroid.socket_connection.AES;
import com.mobius.callbreakandroid.socket_connection.Events;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class EmitManager {
    private static final String TAG = "EmitManager";
    public static JSONObject JObj = new JSONObject();
    static byte[] cipherData;
    static String base64Text;
    static String event;
    static byte[] keyBytes;

    public static void Process(JSONObject jObject, String processName) {
        C c = C.getInstance();
        if (!processName.equalsIgnoreCase(Events.OnlineFriendsCounter))
            Logger.print(TAG, "@@@@@@@@EmitMAnager : " + processName);

        try {
            if (processName.equals(Events.Signup_Process)) {
                jObject.put(Parameters.SerialNumber, PreferenceManager.getUniqueId());
                jObject.put(Parameters.AndroidVersion, c.CurrentVersion);
                jObject.put(Parameters.AndroidOSVersion, c.OSVersion);
                jObject.put(Parameters.NetworkType, c.NetworkType);
                jObject.put(Parameters.LanguageCode, Locale.getDefault().getLanguage());
//                jObject.put(Parameters.Device_Id, PreferenceManager.getRegistrationId());
                jObject.put(Parameters.Device_Type, c.DeviceType);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        JObj = jObject;
        event = processName;

        if (!event.contentEquals(Events.OnlineFriendsCounter)) {
            c.conn.idleTime = 600;
        }

        JSONObject NewObj = new JSONObject();
        try {
            NewObj.put(Events.EventName, processName);
            NewObj.put(Parameters.data, jObject);
        } catch (JSONException e2) {
            e2.printStackTrace();
        }

        Logger.print(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>> Sending: " + NewObj);
        try {
            String s = NewObj.toString();
            keyBytes = c.key.getBytes("UTF-8");
            cipherData = AES.encrypt(c.ivBytes, keyBytes, s.getBytes("UTF-8"));
            base64Text = Base64.encodeToString(cipherData, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        c.conn.emit(Events.Request, base64Text);
    }
}