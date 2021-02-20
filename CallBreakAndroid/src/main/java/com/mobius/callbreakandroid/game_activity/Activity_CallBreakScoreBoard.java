package com.mobius.callbreakandroid.game_activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobius.callbreakandroid.R;
import com.mobius.callbreakandroid.data_store.PreferenceManager;
import com.mobius.callbreakandroid.databinding.ActivityCallBreakScoreBoardBinding;
import com.mobius.callbreakandroid.dialog.NewCommonDialog;
import com.mobius.callbreakandroid.socket_connection.Events;
import com.mobius.callbreakandroid.socket_connection.ResponseCodes;
import com.mobius.callbreakandroid.utility_base.C;
import com.mobius.callbreakandroid.utility_base.EmitManager;
import com.mobius.callbreakandroid.utility_base.Parameters;
import com.mobius.callbreakandroid.utility_base.ParentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import androidx.databinding.DataBindingUtil;

public class Activity_CallBreakScoreBoard extends ParentActivity {

    ActivityCallBreakScoreBoardBinding binding;
    private MyClickHandler handlers;
    private TextView[] userName = new TextView[4];
    private TextView[] user1RoundPoint = new TextView[5];
    private TextView[] user2RoundPoint = new TextView[5];
    private TextView[] user3RoundPoint = new TextView[5];
    private TextView[] user4RoundPoint = new TextView[5];
    private TextView[] userTotalScore = new TextView[4];
    private ImageView[] userWinnerBg = new ImageView[4];
    private ImageView[] userCrown = new ImageView[4];
    private ImageView[] userWinnerTag = new ImageView[4];
    private NewCommonDialog drawGameDialog, WithdrawDialog;
    public static Handler handler;
    private C c = C.getInstance();
    private int activeplayer = 0;

    @Override
    protected void onResume() {
        super.onResume();
//        ConnectionManager.setHandler(handler);
//        c.conn.context = this;
//        c.conn.activity = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity__call_break_score_board);
        handlers = new MyClickHandler();
        binding.setHandlers(handlers);
        String userData = getIntent().getStringExtra(Parameters.IntentDATA);

        binding.icnClose.setVisibility(getIntent().
                getBooleanExtra("IsFromScoreBoard", false) ? View.VISIBLE : View.GONE);


        userName = new TextView[]{binding.user1Name, binding.user2Name, binding.user3Name, binding.user4Name};
        user1RoundPoint = new TextView[]{binding.user1Round1Score, binding.user1Round2Score, binding.user1Round3Score, binding.user1Round4Score, binding.user1Round5Score};
        user2RoundPoint = new TextView[]{binding.user2Round1Score, binding.user2Round2Score, binding.user2Round3Score, binding.user2Round4Score, binding.user2Round5Score};
        user3RoundPoint = new TextView[]{binding.user3Round1Score, binding.user3Round2Score, binding.user3Round3Score, binding.user3Round4Score, binding.user3Round5Score};
        user4RoundPoint = new TextView[]{binding.user4Round1Score, binding.user4Round2Score, binding.user4Round3Score, binding.user4Round4Score, binding.user4Round5Score};
        userTotalScore = new TextView[]{binding.user1Total, binding.user2Total, binding.user3Total, binding.user4Total};
        userWinnerBg = new ImageView[]{binding.user1Winner, binding.user2Winner, binding.user3Winner, binding.user4Winner};
        userCrown = new ImageView[]{binding.user1Crown, binding.user2Crown, binding.user3Crown, binding.user4Crown};
        userWinnerTag = new ImageView[]{binding.user1WinnerTag, binding.user2WinnerTag, binding.user3WinnerTag, binding.user4WinnerTag};

        initHandler();
        ShowThirdUser(false);
        ShowFourUser(false);
        showRoundThreeRow(false);
        showRoundFourRow(false);
        showRoundFiveRow(false);
        hideWinnerImages();
        setScoreBoardData(userData);
    }

    private void initHandler() {
        handler = new Handler(message -> {
            if (message.what == ResponseCodes.FinishCBScoreBoard) {
                Log.e("CHECK_ACTIVE", " in scoreboard handler ");
                finish();
                overridePendingTransition(0, R.anim.dialogue_scale_anim_exit);
            } else if (message.what == ResponseCodes.CallBreakWinnerDeclaredResp) {
                try {
                    JSONObject object = new JSONObject(message.obj.toString());
                    JSONObject data = object.getJSONObject(Parameters.data);
                    if (data.has("userWin") && data.has("msgs")) {
                        boolean userWin = data.getBoolean("userWin");
                        showWinLossPopup(data.getJSONObject("msgs").getString(PreferenceManager.get_id()), userWin ? "Play Now" : "Continue", "CANCEL");
                    } else if (data.has("msgs")) {
                        if (data.getJSONObject("msgs").has(PreferenceManager.get_id())) {
                            showWinLossPopup(data.getJSONObject("msgs").getString(PreferenceManager.get_id()), null, "OK");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }/* else if (message.what == ResponseCodes.CashWithdrawDetailsResp) {
                callLeaveTableEvent();
                Intent profile = new Intent(Activity_CallBreakScoreBoard.this, ActivityMyWallet.class);
                profile.putExtra(Parameters.Wallet_Info, message.obj.toString());
                profile.putExtra("openWithdrowScreen", true);
                startActivity(profile);
            } */ else if (message.what == ResponseCodes.DisplayCashWithdrawPopupResp) {
                try {
                    JSONObject object = new JSONObject(message.obj.toString());
                    JSONObject data = object.getJSONObject(Parameters.data);
                    if (data.has(Parameters.msg)) {
                        showWithdrawPopup(data.getString(Parameters.msg));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        });
    }

    private void setScoreBoardData(String userData) {
        try {
            JSONObject object = new JSONObject(userData);
            JSONObject data = object.getJSONObject(Parameters.data);
            JSONArray PlayersInfo = data.getJSONArray(Parameters.PlayersInfo);
//            JSONArray PlayersLeft = data.getJSONArray(Parameters.Left);
//            Log.e("CHECK_ACTIVE", " USER PI -> " + PlayersInfo.length() + " Left -> " + PlayersLeft.length());
            JSONObject User_Info;
            JSONArray roundSummary;
            boolean playerIsWinner;
            String winnerUserName;
            double userFinalPoint = 0;
            String userRoundPoint;
            for (int i = 0; i < PlayersInfo.length(); i++) {
                if (PlayersInfo.getJSONObject(i).has(Parameters.User_Info)) {
                    User_Info = PlayersInfo.getJSONObject(i).getJSONObject(Parameters.User_Info);
                    userName[activeplayer].setText(User_Info.getString(Parameters.un));
                    if (PlayersInfo.getJSONObject(i).has("roundSummary")) {
                        roundSummary = PlayersInfo.getJSONObject(i).getJSONArray("roundSummary");
                        for (int j = 0; j < roundSummary.length(); j++) {
                            userRoundPoint = new DecimalFormat("##.##")
                                    .format(roundSummary.getJSONObject(j).optDouble("roundPoint"));
                            userFinalPoint += roundSummary.getJSONObject(j).optDouble("roundPoint");
                            if (activeplayer == 0)
                                user1RoundPoint[j].setText(userRoundPoint);
                            else if (activeplayer == 1)
                                user2RoundPoint[j].setText(userRoundPoint);
                            else if (activeplayer == 2)
                                user3RoundPoint[j].setText(userRoundPoint);
                            else if (activeplayer == 3)
                                user4RoundPoint[j].setText(userRoundPoint);
                        }
                    }
                    userTotalScore[activeplayer].setText(new DecimalFormat("##.##").format(userFinalPoint));
                    Log.e("RSD", " userFinalPoint -> " + (new DecimalFormat("##.##").format(userFinalPoint))
                            + " activeplayer -> " + activeplayer);
                    activeplayer++;
                    userFinalPoint = 0;
                }
            }

            //TODO after discussion need to remove this comment and apply this logic..
//            for (int i = 0; i < PlayersLeft.length(); i++) {
//                if (PlayersLeft.getJSONObject(i).has(Parameters.User_Info)) {
//                    User_Info = PlayersLeft.getJSONObject(i).getJSONObject(Parameters.User_Info);
//                    userName[activeplayer].setText(User_Info.getString(Parameters.un));
//                    roundSummary = PlayersLeft.getJSONObject(i).getJSONArray("roundSummary");
//                    for (int j = 0; j < roundSummary.length(); j++) {
//                        userRoundPoint = new DecimalFormat("##.##").format(roundSummary.getJSONObject(j).optDouble("roundPoint"));
//                        userFinalPoint += roundSummary.getJSONObject(j).optDouble("roundPoint");
//                        if (activeplayer == 0)
//                            user1RoundPoint[j].setText(userRoundPoint);
//                        else if (activeplayer == 1)
//                            user2RoundPoint[j].setText(userRoundPoint);
//                        else if (activeplayer == 2)
//                            user3RoundPoint[j].setText(userRoundPoint);
//                        else if (activeplayer == 3)
//                            user4RoundPoint[j].setText(userRoundPoint);
//                    }
//                    userTotalScore[activeplayer].setText(new DecimalFormat("##.##").format(userFinalPoint));
//                    Log.e("RSD", " Left userFinalPoint -> " + (new DecimalFormat("##.##").format(userFinalPoint))
//                            + " i -> " + activeplayer);
//                    activeplayer++;
//                    userFinalPoint = 0;
//                }
//            }

            if (data.has("winnerDetails")) {
                JSONArray winnerDetails = data.getJSONArray("winnerDetails");
                for (int i = 0; i < winnerDetails.length(); i++) {
                    playerIsWinner = winnerDetails.getJSONObject(i).has(Parameters.type) &&
                            winnerDetails.getJSONObject(i).getString(Parameters.type).equalsIgnoreCase("winner");
                    if (playerIsWinner) {
                        winnerUserName = winnerDetails.getJSONObject(i).getString(Parameters.un);
                        for (int j = 0; j < userName.length; j++) {
                            if (userName[j] != null && userName[j].getText().equals(winnerUserName)) {
                                userWinnerBg[j].setVisibility(View.VISIBLE);
                                userCrown[j].setVisibility(View.VISIBLE);
                                userWinnerTag[j].setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    }
                }
            }

            int totalPlayRound = data.has("totalPlayRound") ? data.getInt("totalPlayRound") : 5;
            Log.e("CHECK_ACTIVE", " activeplayer -> " + activeplayer + " totalPlayRound -> " + totalPlayRound);
            ShowSecondUser(activeplayer > 1);
            ShowThirdUser(activeplayer > 2);
            ShowFourUser(activeplayer > 3);
            showRoundThreeRow(totalPlayRound > 2);
            showRoundFourRow(totalPlayRound > 3);
            showRoundFiveRow(totalPlayRound > 4);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ShowSecondUser(boolean visible) {
        binding.user2Img.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user2Name.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user2Round1Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user2Round2Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user2Round3Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user2Round4Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user2Round5Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user2Total.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void ShowThirdUser(boolean visible) {
        binding.user3Img.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user3Name.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user3Round1Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user3Round2Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user3Round3Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user3Round4Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user3Round5Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user3Total.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void ShowFourUser(boolean visible) {
        binding.user4Img.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user4Name.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user4Round1Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user4Round2Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user4Round3Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user4Round4Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user4Round5Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user4Total.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void showRoundThreeRow(boolean visible) {
        binding.id3.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.round3Container.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user1Round3Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user2Round3Score.setVisibility((visible && activeplayer > 1) ? View.VISIBLE : View.GONE);
        binding.user3Round3Score.setVisibility((visible && activeplayer > 2) ? View.VISIBLE : View.GONE);
        binding.user4Round3Score.setVisibility((visible && activeplayer > 3) ? View.VISIBLE : View.GONE);
    }

    private void showRoundFourRow(boolean visible) {
        binding.id4.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.round4Container.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user1Round4Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user2Round4Score.setVisibility((visible && activeplayer > 1) ? View.VISIBLE : View.GONE);
        binding.user3Round4Score.setVisibility((visible && activeplayer > 2) ? View.VISIBLE : View.GONE);
        binding.user4Round4Score.setVisibility((visible && activeplayer > 3) ? View.VISIBLE : View.GONE);
    }

    private void showRoundFiveRow(boolean visible) {
        binding.id5.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.round5Container.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user1Round5Score.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.user2Round5Score.setVisibility((visible && activeplayer > 1) ? View.VISIBLE : View.GONE);
        binding.user3Round5Score.setVisibility((visible && activeplayer > 2) ? View.VISIBLE : View.GONE);
        binding.user4Round5Score.setVisibility((visible && activeplayer > 3) ? View.VISIBLE : View.GONE);
    }

    private void hideWinnerImages() {
        for (int i = 0; i < userName.length; i++) {
            userWinnerBg[i].setVisibility(View.GONE);
            userCrown[i].setVisibility(View.GONE);
            userWinnerTag[i].setVisibility(View.GONE);
        }
    }

    private void showWinLossPopup(String msg, String txtBtn1, String txtBtn2) {
        try {
            closeDrawGameDialog();
            drawGameDialog = new NewCommonDialog.Builder()
                    .setTitle("")
                    .setPopupBg(R.drawable.cb_popup_bg)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setCloseVisible(false)
                    .setPositiveButton(txtBtn1, () -> {
                        if (txtBtn1.equalsIgnoreCase("Play Now")) {
                            c.practicGameFlage = false;
                            callLeaveTableEvent();
                        } else if (txtBtn1.equalsIgnoreCase("Continue")) {
                            try {
                                JSONObject object = new JSONObject();
                                object.put("tbId", c.tbId);
                                object.put("catId", c.catId);
                                EmitManager.Process(object, Events.JoinTables);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton(txtBtn2, () -> {
                        callLeaveTableEvent();
                    })
                    .create(Activity_CallBreakScoreBoard.this);
            drawGameDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeDrawGameDialog() {
        try {
            if (drawGameDialog != null) {
                if (drawGameDialog.isShowing()) {
                    drawGameDialog.dismiss();
                    drawGameDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWithdrawPopup(String msg) {
        try {
            closeWithdrawPopup();
            WithdrawDialog = new NewCommonDialog.Builder()
                    .setTitle("")
                    .setPopupBg(R.drawable.cb_popup_bg)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setCloseVisible(false)
                    .setPositiveButton("Withdraw", () -> {
//                        EmitManager.Process(new JSONObject(), Events.CashWithdrawDetails);
                    })
                    .setNegativeButton("cancel", () -> {
                        closeWithdrawPopup();
                    })
                    .create(Activity_CallBreakScoreBoard.this);
            WithdrawDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeWithdrawPopup() {
        try {
            if (WithdrawDialog != null) {
                if (WithdrawDialog.isShowing()) {
                    WithdrawDialog.dismiss();
                    WithdrawDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callLeaveTableEvent() {
        try {
            closeDrawGameDialog();
            finish();
            if (c.jsonData.getTableInfo() != null) {
                JSONObject jObj = new JSONObject();
                jObj.put("OnSeat", c.jsonData.getTableInfo().hasJoinedTable() ? 1 : 0);
                jObj.put("SubType", Parameters.LeavingTable);
                if (c.jsonData.getTableInfo().hasJoinedTable())
                    jObj.put(Parameters.SeatIndex, c.jsonData.getTableInfo().UserSeatIndex(PreferenceManager.get_id()));
                EmitManager.Process(jObj, Events.LeaveTable);
            }
        } catch (Exception e) {
            finish();
            e.printStackTrace();
        }
    }

    public class MyClickHandler {
        public void closeButtonClick(View view) {
            finish();
            overridePendingTransition(0, R.anim.dialogue_scale_anim_exit);
        }
    }
}