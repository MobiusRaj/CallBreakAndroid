package com.mobius.callbreakandroid.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobius.callbreakandroid.R;
import com.mobius.callbreakandroid.data_store.PreferenceManager;
import com.mobius.callbreakandroid.utility_base.C;
import com.mobius.callbreakandroid.utility_base.Logger;
import com.mobius.callbreakandroid.socket_connection.ResponseCodes;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerError {
    private static final String TAG = "ServerError";

    public static Dialog ConfirmationDialog;
    C c = C.getInstance();

    public ServerError(final Activity context, String Message) {
        try {
            if (isDialogOpen()) {
                c.conn.sendMessage(ResponseCodes.FinishLoader, "", false);
                ConfirmationDialog = new Dialog(context, R.style.Theme_Transparent);
                ConfirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                ConfirmationDialog.setCancelable(false);
                ConfirmationDialog.setContentView(R.layout.dialogreconnect);

                TextView title = ConfirmationDialog.findViewById(R.id.tvTitle);
                title.setText(String.format("%s", context.getResources().getString(R.string.check_connection)));

                TextView message = ConfirmationDialog.findViewById(R.id.tvMessage);

                Button btnOne = ConfirmationDialog.findViewById(R.id.button1);
                btnOne.setText(R.string.reconnect);

                Button btnTwo = ConfirmationDialog.findViewById(R.id.button2);
                btnTwo.setVisibility(View.GONE);

                LinearLayout noInternetTxt = ConfirmationDialog.findViewById(R.id.no_internet_txt);
                noInternetTxt.setVisibility(View.GONE);

                message.setText(Message);

                btnOne.setOnClickListener(v -> {
                    if (!c.conn.isConnected()) {
                        if (PreferenceManager.isInternetIsOn()) {
                            if (noInternetTxt.getVisibility() == View.VISIBLE)
                                noInternetTxt.setVisibility(View.GONE);
                            if (!c.conn.isConnected()) {
                                JSONObject jObj = new JSONObject();
                                try {
                                    jObj.put("message", "Please wait..");
                                    c.conn.sendMessage(ResponseCodes.StartLoader, jObj.toString(), true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                c.conn.Connected();
                                c.conn.isReconnected = true;
                                c.conn.isInIdleMode = false;
                            }
                            ConfirmationDialog.dismiss();
                        } else {
                            if (noInternetTxt.getVisibility() == View.GONE)
                                noInternetTxt.setVisibility(View.VISIBLE);
//                                Toast.makeText(context, "PLEASE CHECK YOUR INTERNET CONNECTION.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        c.isErrorPopup = false;
                        ConfirmationDialog.dismiss();
                    }
                });
                if (!context.isFinishing()) {
                    c.hideNavigationToDialog(context, ConfirmationDialog);
                }
                c.isErrorPopup = true;
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void CloseConfirmationDialog() {
        if (ConfirmationDialog != null) {
            if (ConfirmationDialog.isShowing())
                Logger.print(TAG, "Connnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnuuuuuuuuuuuuuuuu");
            ConfirmationDialog.dismiss();
        }
    }

    private boolean isDialogOpen() {
        return ConfirmationDialog == null || !ConfirmationDialog.isShowing();
    }

    private int getHeight(int val) {
        return c.height * val / 720;
    }

    private int getWidth(int val) {
        return c.width * val / 1280;
    }

}