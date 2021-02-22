package com.mobius.callbreakandroid.game_activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobius.callbreakandroid.R;
import com.mobius.callbreakandroid.data_store.Item_Card;
import com.mobius.callbreakandroid.data_store.PreferenceManager;
import com.mobius.callbreakandroid.data_store.Table_Info;
import com.mobius.callbreakandroid.databinding.ActivityCallBreakPlayingScreenBinding;
import com.mobius.callbreakandroid.dialog.NewCommonDialog;
import com.mobius.callbreakandroid.dialog.ServerError;
import com.mobius.callbreakandroid.socket_connection.ConnectionManager;
import com.mobius.callbreakandroid.socket_connection.Events;
import com.mobius.callbreakandroid.socket_connection.ResponseCodes;
import com.mobius.callbreakandroid.utility_base.C;
import com.mobius.callbreakandroid.utility_base.EmitManager;
import com.mobius.callbreakandroid.utility_base.GlobalLoader_new;
import com.mobius.callbreakandroid.utility_base.Logger;
import com.mobius.callbreakandroid.utility_base.Music_Manager;
import com.mobius.callbreakandroid.utility_base.Parameters;
import com.mobius.callbreakandroid.utility_base.ParentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;

public class CallBreak_PlayingScreen extends ParentActivity implements View.OnClickListener {
    private static final String TAG = "CallBreak_PlayingScreen";
    private GlobalLoader_new loader;
    C c = C.getInstance();
    public static Handler handler;
    ActivityCallBreakPlayingScreenBinding binding;

    private Dialog tableInfoDialog, walletInfoDialog, errorDialog;

    private int isCardUp = -1;
    private final ArrayList<String> selectedCard = new ArrayList<>();
    public String gamePlayId = "";
    private ArrayList<Item_Card> card_BottomUser = new ArrayList<>();
    private boolean receivedGTI;
    private boolean RefreshView = false, isGameComeFromPauseMode = false,
            isStartPlaying = false;
    private boolean isRoundAlreadyStarted;
    private boolean isUserLeavetable;
    private boolean isUserTurn;
    private Timer UserTimer;
    private float totalTime = 0;
    private int seconds = 0;
    private int cnt = 0;
    private float currentProgress = 0;
    private float incProgress = 0;
    private boolean roundAlreadyStarted;
    private ImageView[] ivMyCards = new ImageView[14];
    private Music_Manager music_manager;

    private int BottomSeatIndex;
    private int TopSeatIndex;
    private int LeftSeatIndex;
    private int RightSeatIndex;

    private NotificationManager notificationManager;
    private final boolean[] DealCardToSeat = {false, false, false, false};
    private boolean isSeeMyCardCalled = false;

    private ScaleAnimation cAnimLeft;

    private final String[] cardString = {"c-1-0", "c-2-0", "c-3-0", "c-4-0", "c-5-0", "c-6-0", "c-7-0", "c-8-0", "c-9-0", "c-10-0", "c-11-0", "c-12-0",
            "c-13-0", "l-1-0", "l-2-0", "l-3-0", "l-4-0", "l-5-0", "l-6-0", "l-7-0", "l-8-0", "l-9-0", "l-10-0", "l-11-0", "l-12-0", "l-13-0",
            "k-1-0", "k-2-0", "k-3-0", "k-4-0", "k-5-0", "k-6-0", "k-7-0", "k-8-0", "k-9-0", "k-10-0", "k-11-0", "k-12-0", "k-13-0", "f-1-0", "f-2-0",
            "f-3-0", "f-4-0", "f-5-0", "f-6-0", "f-7-0", "f-8-0", "f-9-0", "f-10-0", "f-11-0", "f-12-0", "f-13-0"};

    private final int[] CardDefault = {R.drawable.c1, R.drawable.c2, R.drawable.c3, R.drawable.c4, R.drawable.c5, R.drawable.c6,
            R.drawable.c7, R.drawable.c8, R.drawable.c9, R.drawable.c10, R.drawable.c11, R.drawable.c12, R.drawable.c13,
            R.drawable.l1, R.drawable.l2, R.drawable.l3, R.drawable.l4, R.drawable.l5, R.drawable.l6, R.drawable.l7,
            R.drawable.l8, R.drawable.l9, R.drawable.l10, R.drawable.l11, R.drawable.l12, R.drawable.l13, R.drawable.k1,
            R.drawable.k2, R.drawable.k3, R.drawable.k4, R.drawable.k5, R.drawable.k6, R.drawable.k7, R.drawable.k8,
            R.drawable.k9, R.drawable.k10, R.drawable.k11, R.drawable.k12, R.drawable.k13, R.drawable.f1, R.drawable.f2,
            R.drawable.f3, R.drawable.f4, R.drawable.f5, R.drawable.f6, R.drawable.f7, R.drawable.f8, R.drawable.f9,
            R.drawable.f10, R.drawable.f11, R.drawable.f12, R.drawable.f13};

    private int CARD_WIDTH, CARD_HEIGHT;
    private int Screen_Height, Screen_Width;
    private long mLastClickTime;
    private CountDownTimer turnCountDownTimer;
    private Vibrator v;
    private int cardDistributeCounter;
    private boolean isAnimStop = false;
    private int bottomCards;
    private Handler cardHandler;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_break__playing_screen);
        loader = new GlobalLoader_new(this);
        music_manager = Music_Manager.getInstance(this);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initHandler();
        ConnectionManager.setHandler(handler);
        setupTableInfoDialog();
        setupWalletInfoDialog();
        SetUpBidSelectionDialog();

        ivMyCards = new ImageView[]{binding.ivCard1, binding.ivCard2, binding.ivCard3, binding.ivCard4, binding.ivCard5, binding.ivCard6, binding.ivCard7, binding.ivCard8, binding.ivCard9, binding.ivCard10, binding.ivCard11, binding.ivCard12, binding.ivCard13, binding.ivCard14};
        Screen_Height = c.height;
        Screen_Width = c.width;

        binding.loaderBg.setVisibility(View.VISIBLE);
        if (c.Chips <= 0 && !c.isRejoin) {
//            ShowMessagePopup("Sorry, you don't have enough Chips to Play !", true, "Out of Chips !");
            showFinishGameDialog("Alert", "Sorry, you don't have enough Chips to Play !");
        } else {
            try {
                if (getIntent().hasExtra("IntentData")) {
                    receivedGTI = true;
                    GetTableInfoProcess(getIntent().getStringExtra("IntentData"));
                } else if (getIntent().hasExtra(Parameters.data)) {
                    String data = getIntent().getStringExtra(Parameters.data);
                    String Event = getIntent().getStringExtra(Events.EventName);

                    Logger.print(TAG, "PLAYING TABLE CREATION EVENT CALL " + data + " " + Event);
                    assert Event != null;
                    if (data != null) {
                        // <<<--- When User Click on PLAY ON TABLE or CREATE PRIVATE TABLE --->>>
                        GlobalLoaderSHOW(getResources().getString(R.string.PleaseWait));
                        EmitManager.Process(new JSONObject(data), Event);
                    } else {
                        // <<<--- When User Click on PLAY NOW from Dashboard --->>>
                        GlobalLoaderSHOW(getResources().getString(R.string.PleaseWait));
                        EmitManager.Process(new JSONObject(), Event);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                finish();
                releaseMemory();
                overridePendingTransition(0, R.anim.dialogue_scale_anim_exit);

            }
        }

        binding.btnScoreInfo.setOnClickListener(this);
        binding.htpIcn.setOnClickListener(this);
        binding.tvUserChips.setOnClickListener(this);
        binding.walletTxt.setOnClickListener(this);
        binding.icnWallet.setOnClickListener(this);
        binding.btnDiscard.setOnClickListener(this);
        binding.btnSwitchTable.setOnClickListener(this);
        binding.playExit.setOnClickListener(this);
        binding.mainBg.setOnClickListener(this);
        binding.playSetting.setOnClickListener(this);
        binding.btnTblInfo.setOnClickListener(this);
        binding.getRoot().setOnClickListener(this);

        int w, h;
        CARD_WIDTH = w = getwidth(120);
        CARD_HEIGHT = h = w * 190 / 130;
        FrameLayout.LayoutParams frm;

        for (int i = 0; i < ivMyCards.length; i++) {
            frm = ((FrameLayout.LayoutParams) ivMyCards[i].getLayoutParams());
            frm.width = w;
            frm.height = h;
            frm.leftMargin = (w * 65 / 140) * (i);
            frm.gravity = Gravity.BOTTOM;
            frm.topMargin = h * 5 / 190;
            frm.bottomMargin = 0;
            ivMyCards[i].setImageResource(R.drawable.card);
            ivMyCards[i].setBackgroundResource(R.color.transparent);
            ivMyCards[i].setVisibility(View.INVISIBLE);
//            ivMyCards[i].setOnClickListener(cardClick);
            ivMyCards[i].setOnTouchListener(handleTouch);
        }

    }

    private View.OnTouchListener handleTouch = new View.OnTouchListener() {
        long startTime;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (!isUserTurn)
                return false;

//            int x = (int) event.getX();
//            int y = (int) event.getY();

            for (int k = 0; k < ivMyCards.length; k++) {
                if (v == ivMyCards[k]) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startTime = SystemClock.elapsedRealtime();
                            if (isCardUp != k) {
                                isCardUp = k;
                                if (selectedCard.size() > 0) {
                                    selectedCard.clear();
                                }
                                selectedCard.add(getCardString(card_BottomUser.get(k)));
                                reDrawUserCards(card_BottomUser);
                                discardVisible(true);
//                            } else {
//                                try {
//                                    JSONObject obj = new JSONObject();
//                                    obj.put("c", getCardString(card_BottomUser.get(k)));
//                                    EmitManager.Process(obj, Events.CardMove);
//                                    setClickable(ResponseCodes.ThrowCardOnDeckResp);
//                                    Log.e("SWIP_THROW", "touched down double tap card -> " + getCardString(card_BottomUser.get(k)));
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
                            }
                            Log.e("SWIP_THROW", "ACTION_DOWN getCardString -> " + getCardString(card_BottomUser.get(k)));
                            break;
                        case MotionEvent.ACTION_MOVE:
                            Log.e("SWIP_THROW", "ACTION_MOVE");
//                            if (SystemClock.elapsedRealtime() - startTime > 200)
//                                Log.e("SWIP_THROW", "moving: (" + x + ", " + y + ")");
                            break;
                        case MotionEvent.ACTION_UP:
                            Log.e("SWIP_THROW", "ACTION_UP");
                            if (SystemClock.elapsedRealtime() - startTime > 150 && isUserTurn)
                                try {
                                    JSONObject obj = new JSONObject();
                                    obj.put("c", getCardString(card_BottomUser.get(k)));
                                    EmitManager.Process(obj, Events.CardMove);
                                    setClickable(ResponseCodes.ThrowCardOnDeckResp);
                                    Log.e("SWIP_THROW", "touched up card -> " + getCardString(card_BottomUser.get(k)));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            break;
                    }
                }
            }
            return true;
        }
    };

    View.OnClickListener cardClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < ivMyCards.length; i++) {
                if (v == ivMyCards[i]) {
                    if (isUserTurn) {
                        if (isCardUp != i) {
                            isCardUp = i;
                            if (selectedCard.size() > 0) {
                                selectedCard.clear();
                            }
                            selectedCard.add(getCardString(card_BottomUser.get(i)));
                            reDrawUserCards(card_BottomUser);
                        } else {
                            try {
                                JSONObject obj = new JSONObject();
                                obj.put("c", getCardString(card_BottomUser.get(i)));
                                EmitManager.Process(obj, Events.CardMove);
                                setClickable(ResponseCodes.ThrowCardOnDeckResp);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        discardVisible(true);
                    }
                }
            }
        }
    };

    private void closeErrorDialog() {
        try {
            if (errorDialog != null) {
                if (errorDialog.isShowing()) {
                    errorDialog.cancel();
                    errorDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetTableInfoProcess(String data) {

        c.isRejoin = false;
        c.isAcceptNotification = false;
        /*if (ResultScreen.handler != null) {
            Message msgFinish = new Message();
            msgFinish.what = ResponseCodes.Finish_Resut_Screen;
            ResultScreen.handler.sendMessage(msgFinish);
        }*/

        ResetNewGame();
        refreshAllTableData();
        Logger.print(TAG, "isUserLeavetable Boolean Variable on GTI =>>>>>>> " + isUserLeavetable);
        JSONObject jObject;
        try {
            jObject = new JSONObject(data);
            if (jObject.getBoolean(Parameters.Flag)) {

                if (binding.loaderBg.getVisibility() == View.VISIBLE)
                    binding.loaderBg.setVisibility(View.GONE);

                Table_Info tableinfo = new Table_Info(jObject.getJSONObject(Parameters.data));
                c.jsonData.setTableInfo(tableinfo);
                if (!c.jsonData.getTableInfo().hasJoinedTable()) {
                    getTableInfo(c.jsonData.getTableInfo().getTableId());
                    return;
                }


                setTableInfoData(jObject.getJSONObject(Parameters.data));


                binding.bootValue.setText(String.valueOf(c.jsonData.getTableInfo().getBootValue()));
//                binding.v3.setVisibility(c.jsonData.getTableInfo().getGameType().equalsIgnoreCase("Points Rummy") ? View.GONE : View.VISIBLE);
//                icnCup.setVisibility(c.jsonData.getTableInfo().getGameType().equalsIgnoreCase("Points Rummy") ? View.GONE : View.VISIBLE);
//                winAmount.setVisibility(c.jsonData.getTableInfo().getGameType().equalsIgnoreCase("Points Rummy") ? View.GONE : View.VISIBLE);
                if (jObject.getJSONObject(Parameters.data).has("winAmount")) {
                    binding.winAmount.setText(jObject.getJSONObject(Parameters.data).optString("winAmount"));
                }

                if (jObject.getJSONObject(Parameters.data).has("tutorial")
                        && jObject.getJSONObject(Parameters.data).getBoolean("tutorial")) {
                    SetUpForPracticeGame();
                }

                Logger.print(TAG, "GetTable Info : " + c.jsonData.getTableInfo().getActivePlayers());

                JSONObject jObject1 = jObject.getJSONObject(Parameters.data);
                binding.tableId.setText(jObject1.getString("gameId"));

                if (c.isJoinedPreviousTable) {
                    if (binding.gameRound.getVisibility() == View.GONE) {
                        binding.gameRound.setVisibility(View.VISIBLE);
                    }
                    if (jObject1.has("round")) {
                        binding.gameRound.setText(String.format("- %s", jObject1.getString("round")));
                    }
                }

                JSONArray players = jObject1.getJSONArray(Parameters.PlayersInfo);
                Log.e("TABLE_LENGTH", " size → " + players.length());
                c.Bootval = jObject1.getInt(Parameters.BootValue);

//                BOOT_VALUE = c.isDealMode ? String.format("%s * pt", c.getNumberFormatedValue(jObject.getJSONObject(Parameters.data).optLong("stake")))
//                        : String.format(c.isPoolMode ? "%s" : "%s * pt", c.getNumberFormatedValue(bootValue));

                c.TableId = jObject.getJSONObject(Parameters.data).getString(Parameters._id);
                LoaderFinish(0);
                binding.notificationText.setVisibility(View.GONE);
                binding.notificationText.setText("");

                binding.tvUserChips.setText(String.format("₹%s", c.getNumberFormatedValue(c.Chips)));
                Logger.print(TAG, "UPDATE CHIPS:" + c.Chips + " " + PreferenceManager.get_id());


                if (players.length() == 2) {
                    if (players.getJSONObject(0).has(Parameters.User_Info)) {
                        Logger.print(TAG, "IF SUB 1");
                        if (players.getJSONObject(0).getJSONObject(Parameters.User_Info).getString(Parameters._id)
                                .equals(PreferenceManager.get_id())) {
                            Logger.print(TAG, "IF 1");
                            BottomSeatIndex = 0;
                            LeftSeatIndex = 1;
                            TopSeatIndex = 2;
                            RightSeatIndex = 3;
                        }
                    }
                    if (players.length() > 1 && players.getJSONObject(1).has(Parameters.User_Info)) {
                        Logger.print(TAG, "IF SUB 1");
                        if (players.getJSONObject(1).getJSONObject(Parameters.User_Info).getString(Parameters._id)
                                .equals(PreferenceManager.get_id())) {
                            Logger.print(TAG, "IF 2");
                            RightSeatIndex = 0;
                            TopSeatIndex = 3;
                            LeftSeatIndex = 2;
                            BottomSeatIndex = 1;
                        }
                    }
                    binding.fmUserTwo.setVisibility(View.VISIBLE);
                    binding.fmUserFive.setVisibility(View.INVISIBLE);
                    binding.fmUserThree.setVisibility(View.INVISIBLE);

                } else if (players.length() == 3) {
                    if (players.getJSONObject(0).has(Parameters.User_Info)) {
                        Logger.print(TAG, "IF SUB 1");
                        if (players.getJSONObject(0).getJSONObject(Parameters.User_Info).getString(Parameters._id)
                                .equals(PreferenceManager.get_id())) {
                            Logger.print(TAG, "IF 1");
                            BottomSeatIndex = 0;
                            LeftSeatIndex = 1;
                            TopSeatIndex = 2;
                            RightSeatIndex = 3;
                        }
                    }
                    if (players.length() > 1 && players.getJSONObject(1).has(Parameters.User_Info)) {
                        Logger.print(TAG, "IF SUB 1");
                        if (players.getJSONObject(1).getJSONObject(Parameters.User_Info).getString(Parameters._id)
                                .equals(PreferenceManager.get_id())) {
                            Logger.print(TAG, "IF 2");
                            RightSeatIndex = 0;
                            TopSeatIndex = 3;
                            LeftSeatIndex = 2;
                            BottomSeatIndex = 1;

                        }
                    }

                    if (players.length() > 2 && players.getJSONObject(2).has(Parameters.User_Info)) {
                        Logger.print(TAG, "IF SUB 1");
                        if (players.getJSONObject(2).getJSONObject(Parameters.User_Info).getString(Parameters._id)
                                .equals(PreferenceManager.get_id())) {
                            Logger.print(TAG, "IF 3");
                            RightSeatIndex = 1;
                            TopSeatIndex = 0;
                            LeftSeatIndex = 3;
                            BottomSeatIndex = 2;

                        }
//                            fmUserTwos.setVisibility(View.VISIBLE);
                    }
                    binding.fmUserThree.setVisibility(View.INVISIBLE);
                    binding.fmUserTwo.setVisibility(View.VISIBLE);
                    binding.fmUserFive.setVisibility(View.VISIBLE);

                } else if (players.length() == 4) {
                    if (players.getJSONObject(0).has(Parameters.User_Info)) {
                        Logger.print(TAG, "IF SUB 1");
                        if (players.getJSONObject(0).getJSONObject(Parameters.User_Info).getString(Parameters._id)
                                .equals(PreferenceManager.get_id())) {
                            Logger.print(TAG, "IF 1");
                            BottomSeatIndex = 0;
                            LeftSeatIndex = 1;
                            TopSeatIndex = 2;
                            RightSeatIndex = 3;

                        }
                    }
                    if (players.length() > 1 && players.getJSONObject(1).has(Parameters.User_Info)) {
                        Logger.print(TAG, "IF SUB 1");
                        if (players.getJSONObject(1).getJSONObject(Parameters.User_Info).getString(Parameters._id)
                                .equals(PreferenceManager.get_id())) {
                            Logger.print(TAG, "IF 2");
                            RightSeatIndex = 0;
                            TopSeatIndex = 3;
                            LeftSeatIndex = 2;
                            BottomSeatIndex = 1;

                        }
                    }

                    if (players.length() > 2 && players.getJSONObject(2).has(Parameters.User_Info)) {
                        Logger.print(TAG, "IF SUB 1");
                        if (players.getJSONObject(2).getJSONObject(Parameters.User_Info).getString(Parameters._id)
                                .equals(PreferenceManager.get_id())) {
                            Logger.print(TAG, "IF 3");
                            RightSeatIndex = 1;
                            TopSeatIndex = 0;
                            LeftSeatIndex = 3;
                            BottomSeatIndex = 2;

                        }
//                            fmUserTwos.setVisibility(View.VISIBLE);
                    }

                    if (players.length() > 3 && players.getJSONObject(3).has(Parameters.User_Info)) {
                        Logger.print(TAG, "IF SUB 1");
                        if (players.getJSONObject(3).getJSONObject(Parameters.User_Info).getString(Parameters._id)
                                .equals(PreferenceManager.get_id())) {
                            Logger.print(TAG, "IF 3");
                            RightSeatIndex = 2;
                            TopSeatIndex = 1;
                            LeftSeatIndex = 0;
                            BottomSeatIndex = 3;

                        }
                    }
//                        fmUserThree.setVisibility(View.INVISIBLE);
                    binding.fmUserTwo.setVisibility(View.VISIBLE);
                    binding.fmUserThree.setVisibility(View.VISIBLE);
                    binding.fmUserFive.setVisibility(View.VISIBLE);
                }

                setUserInfo(players);

                // Set Left Players Data
                try {
                    JSONArray leftPlayers = jObject1.optJSONArray(Parameters.Left);

                    if (leftPlayers != null && leftPlayers.length() > 0) {
                        for (int i = 0; i < leftPlayers.length(); i++) {

                            JSONObject jsonObject = leftPlayers.getJSONObject(i);

                            int seatIndex = jsonObject.getInt(Parameters.SeatIndex);
//                            String userName = jsonObject.getString(Parameters.User_Name);
//                            String userType = jsonObject.getString(Parameters.Passturntype);
//                            String userImage = jsonObject.getString(Parameters.ProfilePicture);
//                            int userPMS = jsonObject.getInt(Parameters.PoolModePoint);

//                            if (!userImage.contains("http")) {
//                                userImage = c.REMOTE_ASSET_BASE_URL + userImage;
//                            }

                            removeUserData(seatIndex);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (c.jsonData.getTableInfo().getActivePlayers() <= 1) {
                    Logger.print(TAG, "JT CALLED 2 XXXX" + c.jsonData.getTableInfo().getActivePlayers());
                    binding.helpText.setVisibility(View.VISIBLE);
                    binding.helpText.setText("Waiting for opponent.");
                } else {
                    enableSwitchButton(false, "MORE THAN ! ACTIVE PLAYER");
                }

                /* Declare And Finish Timer Setting */

//                if (jObject1.has(Parameters.Timer) && jObject1.getInt(Parameters.Timer) > 0)
//                    RoundTimerStartProcess(jObject1.getInt(Parameters.Timer));

                boolean pickedFromPile = false;
                long winningChips;
                JSONArray userCards = null;
                boolean isTocallSeeMyCardFromGTI = true;

                if (c.jsonData.getTableInfo().getTableStatus().equals(Parameters.RoundStarted) ||
                        c.jsonData.getTableInfo().getTableStatus().equals(Parameters.StartDealingCard) ||
                        c.jsonData.getTableInfo().getTableStatus().equals(Parameters.CollectingBootVal) ||
                        c.jsonData.getTableInfo().getTableStatus().equals(Parameters.BidSelect) ||
                        c.jsonData.getTableInfo().getTableStatus().equals("Declared")) {

                    Logger.print(TAG, "Round Already Started Plz Wait...");

                    JSONObject playerInfo;

                    for (int i = 0; i < players.length(); i++) {

                        playerInfo = players.getJSONObject(i);
                        try {
                            if (playerInfo.has(Parameters.SeatIndex) &&
                                    (playerInfo.getString(Parameters.Status).contentEquals("p") ||
                                            playerInfo.getString(Parameters.Status).contentEquals("PFP") ||
                                            playerInfo.getString(Parameters.Status).contentEquals("d"))) {

                                if (playerInfo.getInt(Parameters.SeatIndex) == BottomSeatIndex) {
                                    if (playerInfo.getString(Parameters.Status).contentEquals("PFP")) {
                                        pickedFromPile = true;
                                    }
                                }

                                Logger.print(TAG, "Deal Crad To Seat Index => " + playerInfo.getInt(Parameters.SeatIndex));
                                DealCardToSeat[playerInfo.getInt(Parameters.SeatIndex)] = true;

                                if (playerInfo.getInt(Parameters.SeatIndex) == BottomSeatIndex) {
                                    ArrayList<Item_Card> myCards = PreferenceManager.getUserCardsForRejoin();

                                    if (myCards != null) {
                                        card_BottomUser = myCards;
                                    }
                                    if (playerInfo.has(Parameters.Cards))
                                        userCards = playerInfo.getJSONArray(Parameters.Cards);

                                }
                            }
                            int btos = jObject1.has("btos") ? jObject1.getInt("btos") : -1;
                            int timer = jObject1.has(Parameters.Timer) ? jObject1.getInt(Parameters.Timer) : 0;
                            if (c.jsonData.getTableInfo().getTableStatus().equals(Parameters.BidSelect)
                                    && btos == playerInfo.getInt(Parameters.SeatIndex)
                                    && playerInfo.getInt("bid") <= 0) {
                                new Handler().postDelayed(() -> {
                                    ShowBidSelectionTimer(btos, timer);
                                    ShowBidSelectionDialog();
                                }, 200);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (jObject1.has("userThrowCards")) {
                        JSONObject userDiscardCards = jObject1.getJSONObject("userThrowCards");
                        setUserThrowCardOnDackInRejoin(userDiscardCards);
                    }

                    if (!c.jsonData.getTableInfo().getTableStatus().equals(Parameters.CollectingBootVal)) {
                        Log.e("Check_HUKEM", " GetTableInfoProcess hukumIndex → " + c.jsonData.getTableInfo().getHukamCard());
                    }


                    if (userCards != null && userCards.length() > 0) {

                        isTocallSeeMyCardFromGTI = false;
                        JSONObject jObj = new JSONObject();
                        jObj.put(Parameters.Cards, userCards);
                        JSONObject jObj2 = new JSONObject();
                        jObj2.put(Events.EventName, Events.SeeMyCard);
                        jObj2.put(Parameters.data, jObj);

                        Logger.print(TAG, "SEE MY CARDS EVENT IN GTI => " + jObj2.toString());

                        if (CallBreak_PlayingScreen.handler != null) {
                            Logger.print(TAG, "SEND SEE MY CARDS EVENT IN GTI => true");
                            SeeMyCardEvent(jObj2);
                        }
                    }

                    try {
                        winningChips = c.jsonData.getTableInfo().getPotValue();
                        Logger.print(TAG, "winning CHips =->>>>>>>>>>>>>>>>>>> " + winningChips);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!DealCardToSeat[BottomSeatIndex]) {
                        binding.helpText.setVisibility(View.VISIBLE);
                        binding.helpText.setText("Waiting for new Game");
                        roundAlreadyStarted = true;
                    }

//                    tvCardCounter.setText(String.format("%d", PileCardCounter));


                    Logger.print(TAG, "Rejoin 222222=>>>>>>>> " + c.TableId + " " + PreferenceManager.getPrevTableId() + " " + c.isJoinedPreviousTable);

                    if (c.isJoinedPreviousTable && c.TableId.contentEquals(PreferenceManager.getPrevTableId()) && isTocallSeeMyCardFromGTI) {
                        ArrayList<Item_Card> myCards = PreferenceManager.getUserCardsForRejoin();

                        if (myCards != null && myCards.size() > 0) {
                            card_BottomUser = myCards;
                            showPreLoader();
                            EmitManager.Process(new JSONObject(), Events.SeeMyCard);
                            isSeeMyCardCalled = true;
                            Logger.print(TAG, "card of BOTTOM USER => " + card_BottomUser.toString());

                        } else {
                            showPreLoader();
                            Logger.print(TAG, "Send card event send=>GTI 1");
                            EmitManager.Process(new JSONObject(), Events.SeeMyCard);
                            isSeeMyCardCalled = true;
                        }
                    } else if (DealCardToSeat[BottomSeatIndex] && !isSeeMyCardCalled && isTocallSeeMyCardFromGTI) {
                        Logger.print(TAG, "Send card event send=>GTI 2");
                        showPreLoader();
                        EmitManager.Process(new JSONObject(), Events.SeeMyCard);
                        isSeeMyCardCalled = true;
                    }


                    if (jObject.getJSONObject(Parameters.data).has(Parameters.TurnOfSeat)) {
                        int timerOfUser = jObject.getJSONObject(Parameters.data).getInt(Parameters.Timer);

                        if (timerOfUser > 0) {

                            int seatTurn = jObject.getJSONObject(Parameters.data).getInt(Parameters.TurnOfSeat);

                            JSONArray pilecards = jObject.getJSONObject(Parameters.data).getJSONArray(Parameters.pile);
                            String pilecard = "";

                            if (pilecards.length() > 0) {
                                pilecard = pilecards.getString(0);
                            }

                            JSONObject utsObj = new JSONObject();
                            utsObj.put("nt", seatTurn);
                            utsObj.put("pc", pilecard);

                            JSONObject utsobj2 = new JSONObject();
                            utsobj2.put("data", utsObj);
                            utsobj2.put("timer", timerOfUser);
                            if (pickedFromPile)
                                utsobj2.put("pickFromPile", true);
                            UserTurnStartedProcess(utsobj2.toString());
                        }
                    }
                }

                if (c.jsonData.getTableInfo().getTableStatus().equals(Parameters.RoundTimerStarted)
                        || c.jsonData.getTableInfo().getTableStatus().equals(Parameters.WaitingPlayers)) {
                    int timer = c.jsonData.getTableInfo().getOriginalJSON().getInt(Parameters.Timer);
                    RoundTimerStartProcess(timer);
                }

            } else {
                LoaderFinish(0);
//                c.isNoSpaceInTable = true;
//                c.noSpaceInTableMsg = jObject.getString(Parameters.msg);
//                c.ERROR_CODE = jObject.getInt(Parameters.errorCode);
                finish();
                releaseMemory();
                overridePendingTransition(0, R.anim.dialogue_scale_anim_exit);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SetUpForPracticeGame() {
        binding.icnWallet.setVisibility(View.GONE);
        binding.walletTxt.setVisibility(View.GONE);
        binding.tvUserChips.setVisibility(View.GONE);
        binding.icnCoin.setVisibility(View.GONE);
        binding.bootValue.setText("Practice Game");
        binding.v3.setVisibility(View.GONE);
        binding.icnCup.setVisibility(View.GONE);
        binding.winAmount.setVisibility(View.GONE);
    }

    private void setUserThrowCardOnDackInRejoin(JSONObject userDiscardCards) {
        try {
            int index;
            if (userDiscardCards.has(String.valueOf(BottomSeatIndex))) {
                index = Arrays.asList(cardString).indexOf(userDiscardCards.getString(String.valueOf(BottomSeatIndex)));
                binding.ivCardBottomCenter.setVisibility(View.VISIBLE);
                binding.ivCardBottomCenter.setBackgroundResource(CardDefault[index]);
            }
            if (userDiscardCards.has(String.valueOf(LeftSeatIndex))) {
                index = Arrays.asList(cardString).indexOf(userDiscardCards.getString(String.valueOf(LeftSeatIndex)));
                binding.ivCardLeftCenter.setVisibility(View.VISIBLE);
                binding.ivCardLeftCenter.setBackgroundResource(CardDefault[index]);
            }
            if (userDiscardCards.has(String.valueOf(RightSeatIndex))) {
                index = Arrays.asList(cardString).indexOf(userDiscardCards.getString(String.valueOf(RightSeatIndex)));
                binding.ivCardRightCenter.setVisibility(View.VISIBLE);
                binding.ivCardRightCenter.setBackgroundResource(CardDefault[index]);
            }
            if (userDiscardCards.has(String.valueOf(TopSeatIndex))) {
                index = Arrays.asList(cardString).indexOf(userDiscardCards.getString(String.valueOf(TopSeatIndex)));
                binding.ivCardTopCenter.setVisibility(View.VISIBLE);
                binding.ivCardTopCenter.setBackgroundResource(CardDefault[index]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SeeMyCardEvent(JSONObject MyCards) {
        isSeeMyCardCalled = false;
        LoaderFinish(0);
        try {
            JSONArray cards = MyCards.getJSONObject(Parameters.data).getJSONArray(Parameters.Cards);
            Logger.print(TAG, "QQQQQQQQQQQQQQQQQQQQQQQQQQQ => " + " Card Bottom User => " + card_BottomUser.size() + " Server Side => " + cards.length());
            if (cards.length() > 0) {
                if (card_BottomUser.size() > 0) {

                    String cardTemp;
                    ArrayList<Item_Card> tempArray = new ArrayList<>();
                    for (int temp = 0; temp < cards.length(); temp++) {
                        cardTemp = cards.getString(temp);
                        Item_Card card = getItemCard(cardTemp);
                        tempArray.add(card);
                    }
                    Logger.print(TAG, "QQQQQQQQQQQQQQQQQQQQQQQQQQQ => " + " Temp Array User => " + tempArray.size() + " " + tempArray.toString());
                    for (int j = card_BottomUser.size() - 1; j >= 0; j--) {
                        if (!tempArray.contains(card_BottomUser.get(j))) {
                            card_BottomUser.remove(j);
                        }
                    }

                    if (card_BottomUser.size() < 13 || card_BottomUser.size() > 14) {
                        card_BottomUser = new ArrayList<>(SortMyCardbyColor(tempArray));
                    }

                    Logger.print(TAG, "QQQQQQQQQQQQQQQQQQQQQQQQQQQ => " + " Card Bottom User => " + card_BottomUser.size() + " " + card_BottomUser.toString());

                } else {
                    String Card;
                    for (int i = 0; i < cards.length(); i++) {
                        Card = cards.getString(i);
                        Item_Card card = getItemCard(Card);
                        card_BottomUser.add(card);
                    }
                    card_BottomUser = new ArrayList<>(SortMyCardbyColor(card_BottomUser));
                }
                reDrawUserCards(card_BottomUser);
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }


    private void reDrawUserCards(ArrayList<Item_Card> userCards) {

        if (!DealCardToSeat[BottomSeatIndex]) {
            return;
        }

        Logger.print(TAG, "Card Bottomuser form method parameter " + userCards.size() + " " + userCards.toString());

        int indexOfs1;
        int userCardSize = userCards.size();
//        int ivMyCardsSize = ivMyCards.length;
        int w, h;

        for (ImageView ivMyCard : ivMyCards) {
            if (ivMyCard.getAnimation() != null) {
                ivMyCard.clearAnimation();
            }
            ivMyCard.setVisibility(View.GONE);
        }

        binding.fmuserCards.invalidate();

        binding.fmuserCards.bringToFront();
        CARD_WIDTH = w = getwidth(120);
        CARD_HEIGHT = w * 190 / 130;

        try {

            for (int i = 0; i < userCardSize && i < 14; i++) {

                Item_Card card = userCards.get(i);
                String s1 = getCardString(card);

                indexOfs1 = Arrays.asList(cardString).indexOf(s1);
                final ImageView ivCard = ivMyCards[i];
                ivCard.setTag(s1);


                ivCard.setImageResource(CardDefault[indexOfs1]);
                ivCard.setBackgroundResource(R.color.transparent);
//                ivCard.setImageResource(0);


                w = CARD_WIDTH;
                h = CARD_HEIGHT;
                FrameLayout.LayoutParams frm = ((FrameLayout.LayoutParams) ivCard.getLayoutParams());
                frm.width = w;
                frm.height = h;
                frm.leftMargin = (w * 65 / 140) * (i);
                frm.gravity = Gravity.BOTTOM;
                frm.topMargin = h * 5 / 190;
                frm.bottomMargin = (isCardUp == i ? getwidth(10) : 0);
                ivCard.setLayoutParams(frm);


//                posY[i] = getwidth(385);

                ivCard.bringToFront();
                ivMyCards[i].setVisibility(View.VISIBLE);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Item_Card> SortMyCardbyColor(ArrayList<Item_Card> userCards) {
        String card;
        ArrayList<Item_Card> kali = new ArrayList<>();
        ArrayList<Item_Card> lal = new ArrayList<>();
        ArrayList<Item_Card> fully = new ArrayList<>();
        ArrayList<Item_Card> circuit = new ArrayList<>();

        try {
            Collections.sort(userCards);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < userCards.size(); i++) {

            card = getCardString(userCards.get(i));

            if (card.contains("k")) {
                kali.add(userCards.get(i));
            }

            if (card.contains("f")) {
                fully.add(userCards.get(i));
            }

            if (card.contains("l")) {
                lal.add(userCards.get(i));
            }

            if (card.contains("c")) {
                circuit.add(userCards.get(i));
            }

        }

        userCards.clear();

        userCards.addAll(lal);
        userCards.addAll(fully);
        userCards.addAll(kali);
        userCards.addAll(circuit);
        return userCards;
    }

    // Convert string into Item_Card object
    private Item_Card getItemCard(String string) {

        Item_Card ic = new Item_Card();
        try {

            String[] separated = string.split("-");

            int CardColor = c.getCardSuit(separated[0]);
            ic.setCardColor(CardColor);
//            Logger.print(TAG, "Sort CardColor=> " + (CardColor));

            int CardValue = Integer.parseInt(separated[1]);
            ic.setCardValue(CardValue);
//            Logger.print(TAG, "Sort CardValue=> " + (CardValue));

            int deckNumber = Integer.parseInt(separated[2]);
            ic.setDeckNumber(deckNumber);
//            Logger.print(TAG, "Sort deckNumber=> " + (deckNumber));

            return ic;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Convert Item_Card object into card string
    private String getCardString(Item_Card object) {

        String card = "";
        if (object != null) {
            if (object.getCardColor() == c.Falli) {
                card = "f";
            } else if (object.getCardColor() == c.Chirkut) {
                card = "c";
            } else if (object.getCardColor() == c.Laal) {
                card = "l";
            } else if (object.getCardColor() == c.Kaali) {
                card = "k";
            } else if (object.getCardColor() == c.Joker) {
                card = "j";
            }
            card = card + "-" + (object.getCardValue()) + "-" + (object.getDeckNumber());
        }
        return card;
    }

    private void showPreLoader() {
        runOnUiThread(() -> {
            //noinspection UnusedAssignment
            GlobalLoaderSHOW(getResources().getString(R.string.PleaseWait));
        });
    }

    // set user info received in GTI
    private void setUserInfo(JSONArray players) {

        Logger.print(TAG, "PLAYERS" + players.toString());

        JSONObject getPlayerInfo;
        int sIndex;
        String profilePic;
        String userName;
        String bidText = "0/0";

        for (int i = 0; i < players.length(); i++) {

            try {
                getPlayerInfo = players.getJSONObject(i);

                Logger.print(TAG, "SIZE => " + players.length() + " " + getPlayerInfo.toString());
                Logger.print(TAG, "SEAT INDEX = > " + LeftSeatIndex + " " + RightSeatIndex + " " + BottomSeatIndex);

                if (getPlayerInfo.has(Parameters.User_Info)) {
                    sIndex = getPlayerInfo.getInt("si");

                    profilePic = getPlayerInfo.getJSONObject(Parameters.User_Info).getString(Parameters.ProfilePicture);
                    userName = getPlayerInfo.getJSONObject(Parameters.User_Info).getString(Parameters.User_Name);

                    userName = getProUserName(userName);

                    if (getPlayerInfo.has("bidWinCounter") && getPlayerInfo.has("bid")) {
                        bidText = getPlayerInfo.getInt("bidWinCounter") + "/" + getPlayerInfo.getInt("bid");
                    }

                    if (sIndex == BottomSeatIndex) {

                        binding.ivUserName.setVisibility(View.VISIBLE);
                        binding.tvUserBids.setVisibility(View.VISIBLE);
                        binding.ivUserName.setText(userName);
                        binding.tvUserBids.setText(bidText);

                    } else if (sIndex == RightSeatIndex) {
                        binding.ivUserNameFive.setVisibility(View.VISIBLE);
                        binding.tvUserFiveBids.setVisibility(View.VISIBLE);
                        if (isRoundAlreadyStarted) {
                            binding.ivUserFiveStatus.setText("NEXT GAME");
                            binding.ivUserFiveStatus.setVisibility(View.VISIBLE);
//                            binding.tvUserFiveBetChips.setVisibility(View.INVISIBLE);
//                            binding.tvUserFiveIcnChips.setVisibility(View.INVISIBLE);
                        } else {
                            binding.ivUserFiveStatus.setVisibility(View.GONE);

                        }
                        binding.ivUserNameFive.setText(userName);
                        binding.tvUserFiveBids.setText(bidText);
                        binding.userFiveBg.setBackgroundResource(R.drawable.new_bg_player);

//                        tvUserFiveBetChips.setText(c.isJoinedPreviousTable ? tvUserFiveTotalBetChips : String.format("%s", c.getNumberFormatedValue(virtualChips)));


                        Logger.print(TAG, "Profile Pic Right " + profilePic + " " + userName + " " + sIndex);


                    } else if (sIndex == LeftSeatIndex) {
                        binding.ivUserNameThree.setVisibility(View.VISIBLE);
                        binding.tvUserThreeBids.setVisibility(View.VISIBLE);
                        if (isRoundAlreadyStarted) {
                            binding.ivUserThreeStatus.setText("NEXT GAME");
                            binding.ivUserThreeStatus.setVisibility(View.VISIBLE);
//                            binding.tvUserThreeBetChips.setVisibility(View.INVISIBLE);
//                            binding.tvUserThreeIcnChips.setVisibility(View.INVISIBLE);
                        } else {
                            binding.ivUserThreeStatus.setVisibility(View.GONE);
                        }
                        binding.ivUserNameThree.setText(userName);
                        binding.tvUserThreeBids.setText(bidText);
                        binding.userThreeBg.setBackgroundResource(R.drawable.new_bg_player);
//                        tvUserThreeBetChips.setText(c.isJoinedPreviousTable ? tvUserThreeTotalBetChips : String.format("%s", c.getNumberFormatedValue(virtualChips)));

                        Logger.print(TAG, "Profile Pic Left " + profilePic + " " + userName + " " + sIndex);

                    } else if (sIndex == TopSeatIndex) {

                        binding.ivUserNameTwo.setVisibility(View.VISIBLE);
                        binding.tvUserTwoBids.setVisibility(View.VISIBLE);
                        if (isRoundAlreadyStarted) {
                            binding.ivUserTwoStatus.setVisibility(View.VISIBLE);
                            binding.ivUserTwoStatus.setText("NEXT GAME");
//                            binding.tvUserTwoBetChips.setVisibility(View.INVISIBLE);
//                            binding.tvUserTwoIcnChips.setVisibility(View.INVISIBLE);
                        } else {
                            binding.ivUserTwoStatus.setVisibility(View.GONE);
                        }
                        binding.ivUserNameTwo.setText(userName);
                        binding.tvUserTwoBids.setText(bidText);
                        binding.userTwoBg.setBackgroundResource(R.drawable.new_bg_player);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    // it returns shothand name of User
    private String getProUserName(String name) {
        try {
            String r;
            String[] rr = name.split("\\s+");
            r = rr[0];
            if (rr.length >= 2) {
                r = rr[0];
            }
            return r;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    private void setTableInfoData(JSONObject jsonObject) {
        try {
            JSONObject tblInfoData = jsonObject.getJSONObject("tableInfo");
            pointValue.setText(jsonObject.getString("bv"));
            initialDrop.setText(jsonObject.getString("gt"));
            middleDrop.setText(tblInfoData.getString("defaultNoBid"));
            infoDisConnection.setText(tblInfoData.getString("disconnectAutoLeave"));
            cardsDeck.setText(String.format("%s Deck(No Jokers)", tblInfoData.getString("cardDecks")));
            infoTurnTimer.setText(String.format("%s seconds", tblInfoData.getString("turnTime")));
            userTurnTimer = tblInfoData.has("turnTime") ? tblInfoData.getInt("turnTime") : 0;
            showTimer.setText(String.format("%s seconds", tblInfoData.getString("bidTimer")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getTableInfo(String tableId) {

        JSONObject jObj = new JSONObject();
        try {
            jObj.put(Parameters.User_Name, PreferenceManager.getUserName());
            jObj.put(Parameters.User_Id, PreferenceManager.get_id());
            jObj.put(Parameters.TableId, tableId);
            jObj.put(Parameters.fj, 1);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        EmitManager.Process(jObj, Events.GetTableInfo);
    }

    private void refreshAllTableData() {
        c.jsonData.setTableInfo(null);
//        SetGiftImage(ivGiftIconThree, "");
        removeUserData(RightSeatIndex);
        removeUserData(LeftSeatIndex);
        removeUserData(TopSeatIndex);
    }

    private void removeUserData(int seatIndex) {
        Logger.print(TAG, "USER PROFILE OFELIMINATE USER : " + "");

//        DisplayImageOptions imgOptions = new DisplayImageOptions.Builder()
//                .displayer(new RoundedBitmapDisplayer(1000))
//                .showImageOnLoading(R.drawable.profile_contaibnner)
//                .showImageForEmptyUri(R.drawable.profile_contaibnner)
//                .showImageOnFail(R.drawable.profile_contaibnner)
//                .cacheInMemory(false).bitmapConfig(Bitmap.Config.RGB_565)
//                .cacheOnDisk(false)
//                .build();

        if (seatIndex == RightSeatIndex) {
            binding.ivUserNameFive.setText("");
            binding.tvUserFiveBids.setVisibility(View.GONE);
            binding.ivUserDropFive.setVisibility(View.GONE);
            binding.ivUserFiveStatus.setVisibility(View.GONE);
            binding.userFiveBg.setBackgroundResource(R.drawable.new_bg_player);
            binding.ivCardRightCenter.setVisibility(View.INVISIBLE);
            if (binding.UserFiveProgress.getAnimation() != null)
                binding.UserFiveProgress.clearAnimation();

            binding.UserFiveProgress.setVisibility(View.INVISIBLE);
            binding.userFiveTimer.setVisibility(View.INVISIBLE);

        } else if (seatIndex == LeftSeatIndex) {
            binding.ivUserNameThree.setText("");
            binding.tvUserThreeBids.setVisibility(View.GONE);
            binding.ivUserDropThree.setVisibility(View.GONE);
            binding.ivUserThreeStatus.setVisibility(View.GONE);
            binding.userThreeBg.setBackgroundResource(R.drawable.new_bg_player);
            binding.ivCardLeftCenter.setVisibility(View.INVISIBLE);
            if (binding.UserThreeProgress.getAnimation() != null)
                binding.UserThreeProgress.clearAnimation();

            binding.UserThreeProgress.setVisibility(View.INVISIBLE);
            binding.userThreeTimer.setVisibility(View.INVISIBLE);

        } else if (seatIndex == TopSeatIndex) {
            binding.ivUserNameTwo.setText("");
            binding.tvUserTwoBids.setVisibility(View.GONE);
            binding.ivUserDropTwo.setVisibility(View.GONE);
            binding.ivUserTwoStatus.setVisibility(View.GONE);
            binding.userTwoBg.setBackgroundResource(R.drawable.new_bg_player);
            binding.ivCardTopCenter.setVisibility(View.INVISIBLE);
            if (binding.UserTwoProgress.getAnimation() != null)
                binding.UserTwoProgress.clearAnimation();

            binding.UserTwoProgress.setVisibility(View.INVISIBLE);
            binding.userTwoTimer.setVisibility(View.INVISIBLE);

        }
    }


    // it will reset table, when new game starts, and When response of GTI come.
    private void ResetNewGame() {

        isRoundAlreadyStarted = false;
        if (timer != null) {
            timer.cancel();
        }
        binding.notificationTextTimer.setVisibility(View.GONE);
        isUserLeavetable = false;
        isAnimStop = false;
        Logger.print(TAG, "Resetting Game =>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        isStartPlaying = false;
        isUserTurn = false;
        enableBackButton(true, "RESET NEW GAME");

        if (UserTimer != null) {
            UserTimer.cancel();
            UserTimer = null;
        }

        if (timer != null) {
            timer.cancel();
        }
        binding.notificationText.setVisibility(View.GONE);
        roundAlreadyStarted = false;
//        setClickable(ResponseCodes.DropTableResp);
        setClickable(ResponseCodes.ThrowCardOnDeckResp);
        binding.notificationTextTimer.setVisibility(View.GONE);
        binding.allnotificationText.setVisibility(View.GONE);

        if (binding.ivCardForDistribution.getAnimation() != null) {
            binding.ivCardForDistribution.clearAnimation();
        }

        discardVisible(false);

//        tvDeadwoodPoints.setVisibility(View.INVISIBLE);
        isSeeMyCardCalled = false;
        Arrays.fill(DealCardToSeat, false);
//        isUserFinish = false;

        binding.ivCardForDistribution.setVisibility(View.INVISIBLE);

        resetTimerAndAnimation(-1, true);

        if (binding.UserFiveProgress.getAnimation() != null) {
            binding.UserFiveProgress.clearAnimation();
        }

        if (binding.UserProgress.getAnimation() != null) {
            binding.UserProgress.clearAnimation();
        }

        if (binding.UserThreeProgress.getAnimation() != null) {
            binding.UserThreeProgress.clearAnimation();
        }

        if (binding.UserTwoProgress.getAnimation() != null) {
            binding.UserTwoProgress.clearAnimation();
        }

        isCardUp = -1;
        binding.tvUserBids.setText("0/0");
        binding.tvUserThreeBids.setText("0/0");
        binding.tvUserTwoBids.setText("0/0");
        binding.tvUserFiveBids.setText("0/0");
        clearSomeData();
        setViewData();
    }

    private void clearSomeData() {
        Logger.print(TAG, "Cleared......");
        try {
            if (cardHandler != null) {
                cardHandler.removeCallbacksAndMessages(null);
                cardHandler = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ImageView ivMyCard : ivMyCards) {
            if (ivMyCard.getAnimation() != null) {
                ivMyCard.clearAnimation();
            }
            ivMyCard.setVisibility(View.INVISIBLE);
            ivMyCard.setImageResource(R.drawable.card);
            ivMyCard.setBackgroundResource(R.color.transparent);
        }

        FrameLayout.LayoutParams frm;
        int w = CARD_WIDTH;
        int h = CARD_HEIGHT;
        for (int i = 0; i < ivMyCards.length; i++) {
            frm = ((FrameLayout.LayoutParams) ivMyCards[i].getLayoutParams());
            frm.width = w;
            frm.height = h;
            frm.leftMargin = (w * 65 / 140) * (i);
            frm.gravity = Gravity.BOTTOM;
            frm.topMargin = h * 5 / 190;
            frm.bottomMargin = 0;
            ivMyCards[i].setImageResource(R.drawable.card);
            ivMyCards[i].setBackgroundResource(R.color.transparent);
            ivMyCards[i].setVisibility(View.INVISIBLE);
        }

        if (card_BottomUser.size() > 0)
            card_BottomUser.clear();
    }

    // set player info existed in table
    private void setViewData() {

        if (c.jsonData.getTableInfo() != null) {
            c.jsonData.getTableInfo().setPlayer_info(c.jsonData.getTableInfo().getUnChangedplayer_info());
        }
    }

    private void discardVisible(boolean visibility) {

        if (visibility) {
            if (binding.btnDiscard.getVisibility() != View.VISIBLE) {
                binding.btnDiscard.setVisibility(View.VISIBLE);
//                Animation leftSwipe = AnimationUtils.loadAnimation(this, R.anim.anim_left_swipe);
//                binding.btnDiscard.startAnimation(leftSwipe);
            }

        } else {
            if (binding.btnDiscard.getVisibility() == View.VISIBLE) {
                binding.btnDiscard.setVisibility(View.GONE);
//                Animation rightSwipe = AnimationUtils.loadAnimation(this, R.anim.anim_right_swipe);
//                binding.btnDiscard.startAnimation(rightSwipe);
            }
        }
    }

    // Enable and Disable Click Events on Views
    private void setClickable(int responseCode) {
        Logger.print(TAG, "setClickable: " + responseCode);
        if (responseCode == RESULT_OK) {
            for (ImageView ivMyCard : ivMyCards) {
                ivMyCard.setClickable(true);
                ivMyCard.setOnTouchListener(handleTouch);
            }
            return;
        }

        if (responseCode == ResponseCodes.UserTurnStartedResp) {
//            ivCloseDeck.setClickable(false);
            for (ImageView ivMyCard : ivMyCards) {
                ivMyCard.setClickable(true);
                ivMyCard.setOnTouchListener(handleTouch);
            }
        } else if (responseCode == ResponseCodes.ThrowCardOnDeckResp) {
            for (ImageView ivMyCard : ivMyCards) {
                ivMyCard.setClickable(false);
                ivMyCard.setOnTouchListener(null);
            }

        }/* else if (responseCode == ResponseCodes.DropTableResp) {
            for (ImageView ivMyCard : ivMyCards) {
                ivMyCard.setClickable(false);
                ivMyCard.setOnTouchListener(null);
            }

        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        isGameComeFromPauseMode = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (c.isErrorPopup && !c.conn.isInIdleMode) {
            if (PreferenceManager.isInternetConnected() && !c.conn.isConnected()) {
                GlobalLoaderSHOW(getResources().getString(R.string.PleaseWait));
                ServerError.CloseConfirmationDialog();
                c.conn.Connected();
                c.isErrorPopup = false;
            }
        }

        if (handler == null) {
            initHandler();
        }

        c.conn.activity = this;
        c.conn.context = this;
        ConnectionManager.setHandler(handler);

        /* showing  preloader when ResultScreen is close automatically due to timer on Reconnection*/
        if (c.isShowReconnectTextOnLoader || c.isUserPressedExitGame) {
            c.isUserPressedExitGame = false;
            GlobalLoaderSHOW(getResources().getString(R.string.PleaseWait));
        }
        if (isGameComeFromPauseMode && RefreshView) {
            isGameComeFromPauseMode = false;
            RefreshView = false;
            if (card_BottomUser.size() > 0 && isAnimStop) {
                reDrawUserCards(card_BottomUser);
            }
        }
    }

    private void initHandler() {
        handler = new Handler(msg -> {
            switch (msg.what) {
                case ResponseCodes.UserWalletValueResp:
//                        if (walletInfoDialog.isShowing())
//                            walletInfoDialog.dismiss();
//                        try {
//                            JSONObject object = new JSONObject(msg.obj.toString());
//                            JSONObject data = object.getJSONObject(Parameters.data);
//                            InstantCash.setText(data.has("OTC") ? String.format("%s", c.getNumberFormatedValue(data.getDouble("OTC"))) : "0");
//                            Deposit.setText(data.has("Deposit") ? String.format("%s", c.getNumberFormatedValue(data.getDouble("Deposit"))) : "0");
//                            Winnings.setText(data.has("Game_Winning") ? String.format("%s", c.getNumberFormatedValue(data.getDouble("Game_Winning"))) : "0");
//                            ReleasedBonus.setText(data.has("Realease_Bonus") ? String.format("%s", c.getNumberFormatedValue(data.getDouble("Realease_Bonus"))) : "0");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        if (walletInfoDialog != null && !walletInfoDialog.isShowing()) {
//                            c.hideNavigationToDialog(this, walletInfoDialog);
//                        }
                    break;
                case ResponseCodes.RoundTimerStartResp:

                    LoaderFinish(0);
                    try {
                        if (card_BottomUser.size() > 0) {
                            card_BottomUser.clear();
                        }
                        JSONObject object = new JSONObject(msg.obj.toString());
                        JSONObject data = object.getJSONObject(Parameters.data);
                        if (data.has("gameId")) {
                            binding.tableId.setText(data.getString("gameId"));
                            gameId.setText(data.getString("gameId"));
                            gamePlayId = data.getString("gameId");
                        }
                        if (binding.btnScoreInfo.getVisibility() != View.VISIBLE)
                            binding.btnScoreInfo.setVisibility(View.VISIBLE);
                        c.ROUND_START_TIMER = data.getInt("time");
                        Log.e("CHECK_ACTIVE", " RoundTimerStartResp ");
                        RoundTimerStartProcess(c.ROUND_START_TIMER);
                        binding.helpText.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseCodes.DealNumberResp:
                    try {
                        JSONObject object = new JSONObject(msg.obj.toString());
                        JSONObject data = object.getJSONObject(Parameters.data);
                        if (data.has(Parameters.deal)) {
                            if (binding.gameRound.getVisibility() != View.VISIBLE) {
                                binding.gameRound.setVisibility(View.VISIBLE);
                            }
                            binding.gameRound.setText(String.format("- %s", data.getString(Parameters.deal)));
                        }
                        if (data.has("winAmount")) {
                            binding.winAmount.setText(data.getString("winAmount"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseCodes.GetTableInfoResp:
                    receivedGTI = true;
                    if (Activity_CallBreakScoreBoard.handler != null) {
                        Message m = new Message();
                        m.what = ResponseCodes.FinishCBScoreBoard;
                        Activity_CallBreakScoreBoard.handler.sendMessage(m);
                    }
                    GetTableInfoProcess(msg.obj.toString());
                    break;
                case ResponseCodes.STORE_USER_CARD:
                    Logger.print(TAG, "STOREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE=> " + card_BottomUser.size());
                    receivedGTI = false;
                    if (card_BottomUser.size() > 0) {
                        Logger.print(TAG, "TABLE IDDDDDDDDDDD FROM HANDLER=> " + c.TableId);
                        PreferenceManager.setUserCardsForRejoin(card_BottomUser, c.TableId);
                    }
                    break;
                case ResponseCodes.SeeMyCardResp:
                    try {
                        JSONObject MyCards = new JSONObject(msg.obj.toString());
                        SeeMyCardEvent(MyCards);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case ResponseCodes.UserTurnStartedResp:
                    UserTurnStartedProcess(msg.obj.toString());
                    break;
                case ResponseCodes.JoinTableResp:

                    if (receivedGTI) {
                        if (c.jsonData.getTableInfo() != null) {

                            if (c.jsonData.getTableInfo().getActivePlayers() <= 1) {
                                binding.helpText.setVisibility(View.VISIBLE);
                                binding.helpText.setText("Waiting for opponent.");

                            } else if (!roundAlreadyStarted) {
                                enableSwitchButton(false, "MORE THAN ! ACTIVE PLAYER");
                                Logger.print(TAG, "HelpText JOIN table message hide");
                                binding.helpText.setVisibility(View.GONE);
                            }

                            JoinTable(msg.obj.toString());

//                            if (c.jsonData.getTableInfo().getActivePlayers() > 1) {
//                                if (Invite_playscreen.handler != null) {
//                                    Invite_playscreen.handler.sendEmptyMessage(ResponseCodes.FINISH);
//                                }
//                            }
                        }
                    } else {
                        Logger.print(TAG, " IN JOIN TABLE RESPONSE GTI NOT RECEIVED : " + receivedGTI);
                    }

                    break;
                case ResponseCodes.StartDealingResp:

                    if (!receivedGTI) {
                        return false;
                    }

//                        dataWin = null;
                    enableBackButton(false, "START DEALING RESPONSE 111");
//                        declareButtonVisibility("SDR", false);

                    binding.notificationText.setText("");
                    binding.notificationText.setVisibility(View.INVISIBLE);
                    if (binding.notificationTextTimer.getVisibility() == View.VISIBLE) {
                        binding.notificationTextTimer.setVisibility(View.GONE);
                    }
//                        closeConfirmationDialog();

                    isRoundAlreadyStarted = true;
                    StartDealingProcess(msg.obj.toString());
                    break;
                case ResponseCodes.SelectBidResp:
                    handleBidSelectResponse(msg.obj.toString());
                    break;
                case ResponseCodes.CardMoveResp:
                    handleCardMoveResponse(msg.obj.toString());
                    break;
                case ResponseCodes.LeaveTableResp:
                    Logger.print(TAG, "LEAVE TABLE RESPONSE.. OBJECT : " + msg.obj);
                    if (msg.obj != null) {
                        LeaveTableProcess(msg.obj.toString());
                    }
                    break;
                case ResponseCodes.BidWinnerResp:
                    if (utterCards.size() > 0) {
                        utterCards.clear();
                    }
//                        binding.ivCardBottomCenter.setVisibility(View.INVISIBLE);
//                        binding.ivCardTopCenter.setVisibility(View.INVISIBLE);
//                        binding.ivCardRightCenter.setVisibility(View.INVISIBLE);
//                        binding.ivCardLeftCenter.setVisibility(View.INVISIBLE);
                    try {
                        JSONObject obj = new JSONObject(msg.obj.toString());
                        redrawCardsWithCurrentDeckCards();
                        int seatIndex = obj.getJSONObject(Parameters.data).getInt("bidWinSi");
                        int totalBidWin = obj.getJSONObject(Parameters.data).getInt("bidWinCounter");
                        int totalBid = obj.getJSONObject(Parameters.data).getInt("bid");
                        CollectCardAtWinningUser(seatIndex
                                , totalBidWin
                                , totalBid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case ResponseCodes.RSDResp:
                    ResetNewGame();
                    if (Activity_CallBreakScoreBoard.handler != null) {
                        String data = msg.obj.toString();
                        Message m = new Message();
                        m.what = ResponseCodes.FinishCBScoreBoard;
                        Activity_CallBreakScoreBoard.handler.sendMessage(m);
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(CallBreak_PlayingScreen.this, Activity_CallBreakScoreBoard.class);
                            intent.putExtra(Parameters.IntentDATA, data);
                            startActivity(intent);
                        }, 500);
                    } else {
                        Intent intent = new Intent(CallBreak_PlayingScreen.this, Activity_CallBreakScoreBoard.class);
                        intent.putExtra(Parameters.IntentDATA, msg.obj.toString());
                        startActivity(intent);
                    }
                    break;

                case ResponseCodes.GetRoundDetailsResp:
                    Intent intents = new Intent(CallBreak_PlayingScreen.this, Activity_CallBreakScoreBoard.class);
                    intents.putExtra(Parameters.IntentDATA, msg.obj.toString());
                    intents.putExtra("IsFromScoreBoard", true);
                    startActivity(intents);
                    break;
//                case ResponseCodes.CallBreakWinnerDeclaredResp:
//                    ResetNewGame();
//                    if (Activity_CallBreakScoreBoard.handler != null) {
//                        Message m = new Message();
//                        m.what = ResponseCodes.FinishCBScoreBoard;
//                        Activity_CallBreakScoreBoard.handler.sendMessage(m);
//                    }
//                    Intent intentWD = new Intent(CallBreak_PlayingScreen.this, CallBreak_ResultScreen.class);
//                    intentWD.putExtra(Parameters.IntentDATA, msg.obj.toString());
//                    startActivity(intentWD);
//                    break;
            }
            return false;
        });
    }

    // called when response of LT event come
    private void LeaveTableProcess(String string) {

        try {
            if (c.jsonData.getTableInfo() != null) {
                JSONObject d = new JSONObject(string);
                JSONObject jObj = d.getJSONObject(Parameters.data);
                int seatIndex = jObj.getInt(Parameters.SeatIndex);

                try {
                    if (c.jsonData.getTableInfo().getUnChangedplayer_info().get(seatIndex) != null) {
                        c.jsonData.getTableInfo().setActivePlayers(c.jsonData.getTableInfo().getActivePlayers() - 1);
                    }
                    Log.e("CHEK_ACTIV", " LT after update active player → " + c.jsonData.getTableInfo().getActivePlayers());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                DealCardToSeat[seatIndex] = false;

                //<editor-fold desc="You are leaving the table">
                if (jObj.has(Parameters.User_Id) && jObj.getString(Parameters.User_Id).equals(PreferenceManager.get_id())) {

                    if (jObj.has("auto") && jObj.getInt("auto") == 1) {
                        c.isThrownOutByServer = true;
                    }

                    isUserLeavetable = true;
                    c.isJoinedPreviousTable = false;
                    if (UserTimer != null) {
                        UserTimer.cancel();
                        UserTimer = null;
                    }

                    if (timer != null) {
                        timer.cancel();
                    }

                    if (Activity_CallBreakScoreBoard.handler != null) {
                        Message m = new Message();
                        m.what = ResponseCodes.FinishCBScoreBoard;
                        Activity_CallBreakScoreBoard.handler.sendMessage(m);
                    }

                    binding.notificationTextTimer.setVisibility(View.GONE);

                    if (c.jsonData.getTableInfo() != null)
                        c.jsonData.setTableInfo(null);


                    if (!c.isSwitchingTable && !c.isAcceptNotification) {
                        if (CallBreak_PlayingScreen.handler != null) {
                            CallBreak_PlayingScreen.handler.removeCallbacksAndMessages(null);
                        }

                        LoaderFinish(0);


                        c.isCloseLoaderOnDashBoardOnResume = true;
                        finish();
                        releaseMemory();
                        overridePendingTransition(0, 0);

                    } else {
                        isAnimStop = true;
                        c.isSwitchingTable = false;
                        receivedGTI = false;


                    }

                    if (jObj.has("noChips") && jObj.getInt("noChips") == 1) {
                        c.showNochipsPopup = true;
                    }

                }
                //</editor-fold>
                //<editor-fold desc="Opponent user leaving the table">
                else if (jObj.has(Parameters.SeatIndex) && jObj.has(Parameters.User_Id)) {

                    JSONObject blankObject = new JSONObject();


                    if (c.jsonData.getTableInfo().getUnChangedplayer_info().getJSONObject(seatIndex).length() > 0 &&
                            c.jsonData.getTableInfo().getUnChangedplayer_info().getJSONObject(seatIndex)
                                    .getJSONObject(Parameters.User_Info).getString(Parameters._id)
                                    .equals(jObj.getString(Parameters.User_Id))) {

                        c.jsonData.getTableInfo().getUnChangedplayer_info().put(seatIndex, blankObject);
                    }

                    removeUserData(seatIndex);

                    if (c.jsonData.getTableInfo().getActivePlayers() <= 1) {
                        new Handler().postDelayed(() -> {
                            ResetNewGame();
                            binding.helpText.setVisibility(View.VISIBLE);
                            binding.helpText.setText("Waiting for opponent.");
                        }, 2500);
                    }
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }

    private void handleCardMoveResponse(String s) {
        try {
            Logger.print(TAG, "handleCardMoveResponse: " + s);
            JSONObject obj = new JSONObject(s);
            if (obj.getJSONObject(Parameters.data).has(Parameters.msg)) {
                Toast.makeText(this, obj.getJSONObject(Parameters.data).getString(Parameters.msg), Toast.LENGTH_SHORT).show();
                setClickable(RESULT_OK);
                return;
            }

            int seatIndex = obj.getJSONObject(Parameters.data).getInt("turnSi");
            String card = obj.getJSONObject(Parameters.data).getString("tcard");
            utterCards.add(card);
            resetTimerAndAnimation(seatIndex, true);
            if (seatIndex == BottomSeatIndex) {
                isCardUp = -1;
                if (selectedCard.size() > 0)
                    selectedCard.clear();
                discardVisible(false);
                for (int i = 0; i < card_BottomUser.size(); i++) {
                    if (card.equals(card_BottomUser.get(i).getCardString())) {
                        card_BottomUser.remove(i);
                        throwCardOnDeckBottomUser(card, ivMyCards[i]);
                        break;
                    }
                }
            } else {
                AnimateUserThrowCardOnDeck(seatIndex, card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int animationPickCardThrowTime = 400;

    private void throwCardOnDeckBottomUser(String card, ImageView srcImageView) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.7f, 1.0f, 0.7f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(animationPickCardThrowTime);
        scaleAnimation.setFillAfter(false);

        int index = Arrays.asList(cardString).indexOf(card);

        int[] destLoc = new int[2];
        binding.ivCardBottomCenter.getLocationOnScreen(destLoc);
        int[] throwCardAnimLeftUserXY = new int[2];
        srcImageView.getLocationOnScreen(throwCardAnimLeftUserXY);

        float toX = destLoc[0] - throwCardAnimLeftUserXY[0];
        float toY = destLoc[1] - throwCardAnimLeftUserXY[1];

        Logger.print(TAG, "throwCardOnDeckBottomUser: start " + Arrays.toString(throwCardAnimLeftUserXY));
        Logger.print(TAG, "throwCardOnDeckBottomUser: dest " + Arrays.toString(destLoc));

        TranslateAnimation translateAnimation = new TranslateAnimation(0, toX, 0, toY);
        translateAnimation.setDuration(animationPickCardThrowTime);
        translateAnimation.setFillAfter(false);

        RotateAnimation rotate = new RotateAnimation(0, 180, 0.5f, 0.5f);
        rotate.setDuration(animationPickCardThrowTime);
        rotate.setFillAfter(false);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(rotate);
        set.addAnimation(scaleAnimation);
        set.addAnimation(translateAnimation);

        srcImageView.bringToFront();
        srcImageView.startAnimation(set);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                utterCards.clear();
                binding.ivCardBottomCenter.setVisibility(View.VISIBLE);
                binding.ivCardBottomCenter.setBackgroundResource(CardDefault[index]);
                srcImageView.setVisibility(View.GONE);
                redrawCardsWithCurrentDeckCards();
                reDrawUserCards(card_BottomUser);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void AnimateUserThrowCardOnDeck(int seatIndex, final String card) {
        int index = Arrays.asList(cardString).indexOf(card);
        int[] destLoc = new int[2];

        int x;
        int y;

        AlphaAnimation alfa = new AlphaAnimation(1.0f, 0.9f);
        alfa.setDuration(animationPickCardThrowTime);

        cAnimLeft = new ScaleAnimation(
                0.2f, 1.0f, // Start and end values for the X axis scaling
                0.2f, 1.0f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f);
        cAnimLeft.setDuration(animationPickCardThrowTime);
        cAnimLeft.setFillAfter(false);
        if (seatIndex == LeftSeatIndex) {
            binding.ivCardLeftCenter.getLocationOnScreen(destLoc);
            x = destLoc[0];
            y = destLoc[1];

            binding.ivuserthreethrowcard.setBackgroundResource(CardDefault[index]);

            binding.ivuserthreethrowcard.setVisibility(View.VISIBLE);
            binding.ivuserthreethrowcard.bringToFront();

            int[] throwCardAnimRightUserXY = new int[2];
            binding.ivuserthreethrowcard.getLocationOnScreen(throwCardAnimRightUserXY);
            int xLeft = throwCardAnimRightUserXY[0];
            int yLeft = throwCardAnimRightUserXY[1];

            Logger.print(TAG, "throwCardOnDeckBottomUser: dest " + Arrays.toString(destLoc));

            TranslateAnimation tAnimLeft = new TranslateAnimation(0, x - xLeft, 0, y - yLeft);
            tAnimLeft.setDuration(animationPickCardThrowTime);
            tAnimLeft.setFillAfter(false);

            RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(animationPickCardThrowTime);
            rotate.setFillAfter(false);

            AnimationSet animSetLeft = new AnimationSet(true);
//            animSetLeft.addAnimation(rotate);
//            animSetLeft.addAnimation(cAnimLeft);
            animSetLeft.addAnimation(tAnimLeft);
//            animSetLeft.addAnimation(alfa);

            animSetLeft.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {


                    binding.ivuserthreethrowcard.setVisibility(View.INVISIBLE);
                    if (binding.ivuserthreethrowcard.getAnimation() != null)
                        binding.ivuserthreethrowcard.clearAnimation();
                    binding.ivCardLeftCenter.setBackgroundResource(CardDefault[index]);
                    binding.ivCardLeftCenter.setVisibility(View.VISIBLE);
                }
            });
            binding.ivuserthreethrowcard.startAnimation(animSetLeft);

        } else if (seatIndex == RightSeatIndex) {
            binding.ivCardRightCenter.getLocationOnScreen(destLoc);
            x = destLoc[0];
            y = destLoc[1];

            binding.ivuserfivethrowcard.setBackgroundResource(CardDefault[index]);

            binding.ivuserfivethrowcard.bringToFront();
            binding.ivuserfivethrowcard.setVisibility(View.VISIBLE);

            Logger.print(TAG, "Initialize Right ANimation");
            int[] throwCardAnimRightUserXY = new int[2];
            binding.ivuserfivethrowcard.getLocationOnScreen(throwCardAnimRightUserXY);
            int xRight = throwCardAnimRightUserXY[0];
            int yRight = throwCardAnimRightUserXY[1];

            Logger.print(TAG, "throwCardOnDeckBottomUser: start " + Arrays.toString(throwCardAnimRightUserXY));
            Logger.print(TAG, "throwCardOnDeckBottomUser: dest " + Arrays.toString(destLoc));

            TranslateAnimation tAnimRight = new TranslateAnimation(0, x - xRight, 0, y - yRight);
            tAnimRight.setDuration(animationPickCardThrowTime);
            tAnimRight.setFillAfter(false);

            RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(animationPickCardThrowTime);
            rotate.setFillAfter(false);

            AnimationSet animSetRight = new AnimationSet(true);
//            animSetRight.addAnimation(rotate);
//            animSetRight.addAnimation(cAnimLeft);
            animSetRight.addAnimation(tAnimRight);
//            animSetRight.addAnimation(alfa);

            animSetRight.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.ivuserfivethrowcard.setVisibility(View.INVISIBLE);
                    if (binding.ivuserfivethrowcard.getAnimation() != null)
                        binding.ivuserfivethrowcard.clearAnimation();
                    binding.ivCardRightCenter.setBackgroundResource(CardDefault[index]);
                    binding.ivCardRightCenter.setVisibility(View.VISIBLE);
                }
            });
            binding.ivuserfivethrowcard.startAnimation(animSetRight);

        } else if (seatIndex == TopSeatIndex) {
            binding.ivCardTopCenter.getLocationOnScreen(destLoc);
            x = destLoc[0];
            y = destLoc[1];

            binding.ivusertwothrowcard.setBackgroundResource(CardDefault[index]);

            binding.ivusertwothrowcard.bringToFront();
            binding.ivusertwothrowcard.setVisibility(View.VISIBLE);

            Logger.print(TAG, "Initialize Top ANimation");
            int[] throwCardAnimTopUserXY = new int[2];
            binding.ivusertwothrowcard.getLocationOnScreen(throwCardAnimTopUserXY);
            int xTop = throwCardAnimTopUserXY[0];
            int yTop = throwCardAnimTopUserXY[1];


            Logger.print(TAG, "throwCardOnDeckBottomUser: start " + Arrays.toString(throwCardAnimTopUserXY));
            Logger.print(TAG, "throwCardOnDeckBottomUser: dest " + Arrays.toString(destLoc));

            TranslateAnimation tAnimTop = new TranslateAnimation(0, x - xTop, 0, y - yTop);
            tAnimTop.setDuration(animationPickCardThrowTime);
            tAnimTop.setFillAfter(false);

            AnimationSet animSetTop = new AnimationSet(true);
//            animSetTop.addAnimation(cAnimLeft);
            animSetTop.addAnimation(tAnimTop);
//            animSetTop.addAnimation(alfa);

            animSetTop.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.ivusertwothrowcard.setVisibility(View.INVISIBLE);
                    if (binding.ivusertwothrowcard.getAnimation() != null)
                        binding.ivusertwothrowcard.clearAnimation();
                    binding.ivCardTopCenter.setBackgroundResource(CardDefault[index]);
                    binding.ivCardTopCenter.setVisibility(View.VISIBLE);
                }
            });
            binding.ivusertwothrowcard.startAnimation(animSetTop);

        }
    }

    Dialog bidSelectionDialog;
    TextView bidSelectionPopupTitle;
    private int blackTints = Color.argb(150, 77, 77, 77);
    private int defaultTint = Color.argb(0, 0, 0, 0);

    private void handleBidSelectResponse(String s) {
        try {
            JSONObject obj = new JSONObject(s);
            JSONObject data = obj.getJSONObject(Parameters.data);
            int seatIndex = data.getInt("bssi");
            int previousBidSeatIndex = data.getInt("pbsSi");
            int previousBidSelect = data.getInt("pbs");
            int time = data.getInt("time");

            Logger.print(TAG, "handleBidSelectResponse: previousBidSeatIndex " + previousBidSeatIndex);

            if (previousBidSeatIndex == BottomSeatIndex) {
                binding.tvUserBids.setText(String.format("0/%d", previousBidSelect));
            } else if (previousBidSeatIndex == LeftSeatIndex) {
                binding.tvUserThreeBids.setText(String.format("0/%d", previousBidSelect));
            } else if (previousBidSeatIndex == TopSeatIndex) {
                binding.tvUserTwoBids.setText(String.format("0/%d", previousBidSelect));
            } else if (previousBidSeatIndex == RightSeatIndex) {
                binding.tvUserFiveBids.setText(String.format("0/%d", previousBidSelect));
            }
            if (seatIndex == BottomSeatIndex)
                ShowBidSelectionDialog();
            else
                HideBidSelectionDialog();
            ShowBidSelectionTimer(seatIndex, time);
            if (card_BottomUser.size() <= 0) {
                if (DealCardToSeat[BottomSeatIndex] && !isSeeMyCardCalled && !c.isRejoin) {
                    Logger.print(TAG, "Semy card event send=>");
                    EmitManager.Process(new JSONObject(), Events.SeeMyCard);
                    isSeeMyCardCalled = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SetUpBidSelectionDialog() {
        HideBidSelectionDialog();
        bidSelectionDialog = new Dialog(this, R.style.Theme_TransparentBuddies);
        bidSelectionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bidSelectionDialog.setContentView(R.layout.bid_popup);
        bidSelectionDialog.setCancelable(true);
        bidSelectionDialog.setOwnerActivity(CallBreak_PlayingScreen.this);
        bidSelectionPopupTitle = bidSelectionDialog.findViewById(R.id.title);

        Button[] btnBids = new Button[13];
        for (int i = 0; i < btnBids.length; i++) {
            int bid = i + 1;
            btnBids[i] = bidSelectionDialog.findViewById(getResources().getIdentifier("bid" + bid, "id", getPackageName()));

            btnBids[i].setOnClickListener(v -> {
                HideBidSelectionDialog();
                try {
                    JSONObject obj1 = new JSONObject();
                    obj1.put("bid", bid);
                    EmitManager.Process(obj1, Events.SelectBid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void ShowBidSelectionDialog() {
        HideBidSelectionDialog();
        if (bidSelectionDialog != null && !bidSelectionDialog.isShowing()) {
            c.hideNavigationToDialog(this, bidSelectionDialog);
        }
    }

    private void HideBidSelectionDialog() {
        if (bidSelectionDialog != null && bidSelectionDialog.isShowing()) {
            bidSelectionDialog.dismiss();
        }
    }

    private void ShowBidSelectionTimer(int seatIndex, int time) {

        resetTimerAndAnimation(seatIndex, false);
        turnCountDownTimer = new CountDownTimer(time * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                binding.userTimer.setText(String.format("%d", millisUntilFinished / 1000));
                binding.userTwoTimer.setText(String.format("%d", millisUntilFinished / 1000));
                binding.userThreeTimer.setText(String.format("%d", millisUntilFinished / 1000));
                binding.userFiveTimer.setText(String.format("%d", millisUntilFinished / 1000));
                if (bidSelectionDialog != null && bidSelectionDialog.isShowing() && bidSelectionPopupTitle != null) {
                    bidSelectionPopupTitle.setText(String.format("Select Your Bid in : %d", (int) (millisUntilFinished / 1000)));
                }
                if (millisUntilFinished == 0) {
                    HideBidSelectionDialog();
                }
            }

            public void onFinish() {
                binding.userTimer.setVisibility(View.GONE);
                binding.userTwoTimer.setVisibility(View.GONE);
                binding.userThreeTimer.setVisibility(View.GONE);
                binding.userFiveTimer.setVisibility(View.GONE);
            }
        }.start();

        if (UserTimer != null) {
            UserTimer.cancel();
            UserTimer = null;
        }

        int maxTimer = time * 10;
        binding.UserProgress.setMax(maxTimer * 2);
        binding.UserThreeProgress.setMax(maxTimer);
        binding.UserFiveProgress.setMax(maxTimer);
        binding.UserTwoProgress.setMax(maxTimer);
        currentProgress = 1;
        UserTimer = new Timer();
        seconds = time;
        timers = 0;
        UserTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (timers < time * 10) {
                    timers += 1;
                }

                currentProgress = currentProgress - incProgress;
                if (currentProgress <= 0) {
                    if (UserTimer != null) {
                        UserTimer.cancel();
                        UserTimer = null;
                    }
                }
                cnt++;
                if (cnt >= 10) {
                    cnt = 0;
                    seconds--;
                }

                runOnUiThread(() -> {
                    binding.UserProgress.setProgress(timers);
                    binding.UserThreeProgress.setProgress(timers);
                    binding.UserFiveProgress.setProgress(timers);
                    binding.UserTwoProgress.setProgress(timers);
                });
            }
        }, 0, 100);
    }

    private void StartDealingProcess(String data) {

        JSONObject SeeMyCardObj;
        try {
            SeeMyCardObj = new JSONObject(data);
            JSONArray Userinfo = SeeMyCardObj.getJSONObject(Parameters.data).getJSONArray(Parameters.Seats);
            try {
                for (int i = 0; i < Userinfo.length(); i++) {
                    int si = Userinfo.getInt(i);
                    Logger.print(TAG, "SEATS => " + si);
                    DealCardToSeat[si] = true;
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            isStartPlaying = true;

            int totalCardToDistribute = 0;

            if (Userinfo.length() == 2) {
                totalCardToDistribute = 26;
            } else if (Userinfo.length() == 3) {
                totalCardToDistribute = 39;
            } else if (Userinfo.length() == 4) {
                totalCardToDistribute = 52;
            }


            if (GameisOn()) {
                distributeCardsAnimation(totalCardToDistribute, Userinfo.length());
            } else {

                if (card_BottomUser.size() > 0) {
                    card_BottomUser = new ArrayList<>(SortMyCardbyColor(card_BottomUser));
                    reDrawUserCards(card_BottomUser);
//                    BottomUserToolTip.setVisibility(View.VISIBLE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void distributeCardsAnimation(final int totalCardToDistribute, final int totoalPlayers) {

        int[] userOneCardXY = new int[2];
        int[] userTwoCardXY = new int[2];
        int[] userThreeCardXY = new int[2];
        int[] userSixCardXY = new int[2];
        int[] closeDeckXY = new int[2];

//         Logger.print(TAG, "ANIMATION FORM " + BottomSeatIndex + " totalCardToDistribute → "
//                 + totalCardToDistribute +" totoalPlayers → "+totoalPlayers);

        cAnimLeft = new ScaleAnimation(
                1.0f, 0.2f, // Start and end values for the X axis scaling
                1.0f, 0.2f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f);
        cAnimLeft.setDuration(100);
        cAnimLeft.setFillAfter(false);


        binding.ivCardForDistribution.setVisibility(View.VISIBLE);
        binding.ivCardForDistribution.bringToFront();
        //frmTemp11.bringToFront();

        if (binding.ivCardForDistribution.getAnimation() != null) {
            binding.ivCardForDistribution.clearAnimation();
        }
        binding.ivCardForDistribution.setBackgroundResource(R.drawable.card);

        cardDistributeCounter = 0;
        isAnimStop = false;

        // user one card location
        binding.userThreeBg.getLocationOnScreen(userOneCardXY);
        int userOneCardX = userOneCardXY[0];
        int userOneCardY = userOneCardXY[1];

        // user two card location
        binding.userFiveBg.getLocationOnScreen(userTwoCardXY);
        int userTwoCardX = userTwoCardXY[0];
        int userTwoCardY = userTwoCardXY[1];


        //user Six card
        binding.userTwoBg.getLocationOnScreen(userSixCardXY);
        int userSixCardX = userSixCardXY[0];
        int userSixCardY = userSixCardXY[1];


        binding.ivCloseDeck.getLocationOnScreen(closeDeckXY);
        final int x = closeDeckXY[0];
        final int y = closeDeckXY[1];

        TranslateAnimation tAnimationTop = new TranslateAnimation(0, userSixCardX - x, 0, userSixCardY - y);
        tAnimationTop.setDuration(100);
        tAnimationTop.setFillAfter(false);

        ////////////////////////////////////////////////////

        TranslateAnimation tAnimationLeft = new TranslateAnimation(0, userOneCardX - x, 0, userOneCardY - y);
        tAnimationLeft.setDuration(100);
        tAnimationLeft.setFillAfter(false);

        TranslateAnimation tAnimationRight = new TranslateAnimation(0, userTwoCardX - x, 0, userTwoCardY - y);
        tAnimationRight.setDuration(100);
        tAnimationRight.setFillAfter(false);


        AlphaAnimation alfaAni = new AlphaAnimation(1.0f, 1.0f);
        alfaAni.setDuration(90);

        final AnimationSet setLeft = new AnimationSet(true);
        setLeft.addAnimation(cAnimLeft);
        setLeft.addAnimation(alfaAni);
        setLeft.addAnimation(tAnimationLeft);

        final AnimationSet setRight = new AnimationSet(true);
        setRight.addAnimation(cAnimLeft);
        setRight.addAnimation(alfaAni);
        setRight.addAnimation(tAnimationRight);

        final TranslateAnimation[] tAnimation = new TranslateAnimation[13];

        for (int c = 0; c < tAnimation.length; c++) {

            ivMyCards[6].getLocationOnScreen(userThreeCardXY);
            int userThreeCardX = userThreeCardXY[0];
            int userThreeCardY = userThreeCardXY[1];
            tAnimation[c] = new TranslateAnimation(0, userThreeCardX - x, 0, userThreeCardY - y);
            tAnimation[c].setDuration(100);
            tAnimation[c].setFillAfter(false);

        }

        final AnimationSet setTop = new AnimationSet(true);
        setTop.addAnimation(cAnimLeft);
        setTop.addAnimation(alfaAni);
        setTop.addAnimation(tAnimationTop);

        binding.allnotificationText.setText("Dealing Cards.");
        binding.allnotificationText.setVisibility(View.VISIBLE);

        bottomCards = 0;

        if (DealCardToSeat[BottomSeatIndex]) {
            binding.helpText.setText("");
            binding.helpText.setVisibility(View.INVISIBLE);
        }

        if (DealCardToSeat[BottomSeatIndex]) {
            binding.ivCardForDistribution.startAnimation(tAnimation[bottomCards]);
        } else if (DealCardToSeat[LeftSeatIndex]) {
            binding.ivCardForDistribution.startAnimation(setLeft);
        } else if (DealCardToSeat[RightSeatIndex]) {
            binding.ivCardForDistribution.startAnimation(setRight);
        } else if (DealCardToSeat[TopSeatIndex]) {
            binding.ivCardForDistribution.startAnimation(setTop);
        }

        for (TranslateAnimation aTAnimation : tAnimation) {

            aTAnimation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    if (GameisOn())
                        music_manager.play_CardDealing();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    if (GameisOn()) {
                        if (cardDistributeCounter >= totoalPlayers * 5) {
                            cardDistributeCounter = totalCardToDistribute;
                        }

                        if (binding.ivCardForDistribution.getAnimation() != null)
                            binding.ivCardForDistribution.clearAnimation();

//                        tvCardCounter.setText(String.format("%d", PileCardCounter));
                        cardDistributeCounter++;
                        ivMyCards[6].setVisibility(View.VISIBLE);
                        bottomCards++;
                        if (bottomCards == 13) {
                            cardDistributeCounter = totalCardToDistribute + 1;
                        }
                        if (!isAnimStop) {
                            if (cardDistributeCounter >= totalCardToDistribute) {
                                binding.allnotificationText.setVisibility(View.GONE);

                                isAnimStop = true;
                                if (binding.ivCardForDistribution.getAnimation() != null) {
                                    binding.ivCardForDistribution.clearAnimation();
                                }
                                binding.ivCardForDistribution.setVisibility(View.GONE);
                                cardDistributeCounter = 0;
                                FirstDrawAnim(card_BottomUser);

                            } else {
                                if (binding.ivCardForDistribution.getAnimation() != null)
                                    binding.ivCardForDistribution.clearAnimation();

                                if (DealCardToSeat[LeftSeatIndex])
                                    binding.ivCardForDistribution.startAnimation(setLeft);
                                else if (DealCardToSeat[TopSeatIndex])
                                    binding.ivCardForDistribution.startAnimation(setTop);
                                else if (DealCardToSeat[RightSeatIndex])
                                    binding.ivCardForDistribution.startAnimation(setRight);
                            }
                        } else {
                            cardDistributeCounter = 0;
                            if (binding.ivCardForDistribution.getAnimation() != null) {
                                binding.ivCardForDistribution.clearAnimation();
                            }
                            binding.ivCardForDistribution.setVisibility(View.GONE);

                            reDrawUserCards(card_BottomUser);
                        }
                    } else {
                        directSetUserCards();
                    }
                }
            });
        }

        setLeft.getAnimations().get(2).setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                if (GameisOn())
                    music_manager.play_CardDealing();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (GameisOn()) {
                    if (cardDistributeCounter >= totoalPlayers * 5) {
                        cardDistributeCounter = totalCardToDistribute;
                    }

                    if (binding.ivCardForDistribution.getAnimation() != null)
                        binding.ivCardForDistribution.clearAnimation();


//                    tvCardCounter.setText(String.format("%d", PileCardCounter));
                    cardDistributeCounter++;

                    if (!isAnimStop) {

                        if (cardDistributeCounter >= totalCardToDistribute) {
                            binding.allnotificationText.setVisibility(View.GONE);

                            if (binding.ivCardForDistribution.getAnimation() != null) {
                                binding.ivCardForDistribution.clearAnimation();
                            }
                            binding.ivCardForDistribution.setVisibility(View.GONE);
                            isAnimStop = true;
                            cardDistributeCounter = 0;
                            FirstDrawAnim(card_BottomUser);

                        } else {
                            if (binding.ivCardForDistribution.getAnimation() != null)
                                binding.ivCardForDistribution.clearAnimation();

                            if (DealCardToSeat[TopSeatIndex])
                                binding.ivCardForDistribution.startAnimation(setTop);
                            else if (DealCardToSeat[RightSeatIndex])
                                binding.ivCardForDistribution.startAnimation(setRight);
                            else if (DealCardToSeat[BottomSeatIndex])
                                binding.ivCardForDistribution.startAnimation(tAnimation[bottomCards]);
                        }
                    } else {
                        cardDistributeCounter = 0;
                        if (binding.ivCardForDistribution.getAnimation() != null) {
                            binding.ivCardForDistribution.clearAnimation();
                        }


                        reDrawUserCards(card_BottomUser);

                    }
                } else {
                    directSetUserCards();
                }
            }
        });


        setRight.getAnimations().get(2).setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                if (GameisOn())
                    music_manager.play_CardDealing();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (GameisOn()) {

                    if (cardDistributeCounter >= totoalPlayers * 5) {
                        cardDistributeCounter = totalCardToDistribute;
                    }

                    if (binding.ivCardForDistribution.getAnimation() != null)
                        binding.ivCardForDistribution.clearAnimation();


//                    tvCardCounter.setText(String.format("%d", PileCardCounter));
                    cardDistributeCounter++;

                    if (!isAnimStop) {

                        if (cardDistributeCounter >= totalCardToDistribute) {
                            binding.allnotificationText.setVisibility(View.GONE);


                            if (binding.ivCardForDistribution.getAnimation() != null) {
                                binding.ivCardForDistribution.clearAnimation();
                            }
                            binding.ivCardForDistribution.setVisibility(View.GONE);
                            isAnimStop = true;

                            cardDistributeCounter = 0;
                            FirstDrawAnim(card_BottomUser);

                        } else {
                            if (binding.ivCardForDistribution.getAnimation() != null)
                                binding.ivCardForDistribution.clearAnimation();


                            if (DealCardToSeat[BottomSeatIndex])
                                binding.ivCardForDistribution.startAnimation(tAnimation[bottomCards]);
                            else if (DealCardToSeat[LeftSeatIndex])
                                binding.ivCardForDistribution.startAnimation(setLeft);
                            else if (DealCardToSeat[TopSeatIndex])
                                binding.ivCardForDistribution.startAnimation(setTop);
                        }
                    } else {
                        cardDistributeCounter = 0;
                        if (binding.ivCardForDistribution.getAnimation() != null) {
                            binding.ivCardForDistribution.clearAnimation();
                        }

                        reDrawUserCards(card_BottomUser);
                    }
                } else {
                    directSetUserCards();
                }
            }
        });

        setTop.getAnimations().get(2).setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                if (GameisOn())
                    music_manager.play_CardDealing();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (GameisOn()) {

                    if (cardDistributeCounter >= totoalPlayers * 5) {
                        cardDistributeCounter = totalCardToDistribute;
                    }

                    if (binding.ivCardForDistribution.getAnimation() != null)
                        binding.ivCardForDistribution.clearAnimation();

//                    tvCardCounter.setText(String.format("%d", PileCardCounter));
                    cardDistributeCounter++;
                    if (!isAnimStop) {

                        if (cardDistributeCounter >= totalCardToDistribute) {
                            binding.allnotificationText.setVisibility(View.GONE);
                            if (binding.ivCardForDistribution.getAnimation() != null) {
                                binding.ivCardForDistribution.clearAnimation();
                            }
                            binding.ivCardForDistribution.setVisibility(View.GONE);

                            isAnimStop = true;
                            cardDistributeCounter = 0;
                            FirstDrawAnim(card_BottomUser);

                        } else {
                            if (binding.ivCardForDistribution.getAnimation() != null)
                                binding.ivCardForDistribution.clearAnimation();
                            if (DealCardToSeat[RightSeatIndex])
                                binding.ivCardForDistribution.startAnimation(setRight);
                            else if (DealCardToSeat[BottomSeatIndex])
                                binding.ivCardForDistribution.startAnimation(tAnimation[bottomCards]);
                            else if (DealCardToSeat[LeftSeatIndex])
                                binding.ivCardForDistribution.startAnimation(setLeft);
                        }
                    } else {
                        cardDistributeCounter = 0;
                        if (binding.ivCardForDistribution.getAnimation() != null) {
                            binding.ivCardForDistribution.clearAnimation();
                        }
                        binding.ivCardForDistribution.setVisibility(View.GONE);

                        reDrawUserCards(card_BottomUser);
                    }
                } else {
                    directSetUserCards();
                }
            }
        });
    }

    int cardAnimDuration = 650;

    /**
     * shows animation of played card. played card are transfered to the winner
     * (animation illusion)
     *
     * @param seat - seat number of winning player
     */
    private void CollectCardAtWinningUser(final int seat, int totalBidWin, int totalBid) {

//        selectedCard = -1;
//        firstSeatIndex = 0;

        int MyDeckX = getRelativeLeft(binding.userOneBg) + binding.userOneBg.getWidth() / 2;
        int BottomX = getRelativeLeft(binding.ivCardBottomCenter);
        int BottomY = getRelativeTop(binding.ivCardBottomCenter);
        int LeftX = getRelativeLeft(binding.ivCardLeftCenter);
        int LeftY = getRelativeTop(binding.ivCardLeftCenter);
        int TopX = getRelativeLeft(binding.ivCardTopCenter);
        int TopY = getRelativeTop(binding.ivCardTopCenter);
        int RightX = getRelativeLeft(binding.ivCardRightCenter);
        int RightY = getRelativeTop(binding.ivCardRightCenter);
        int LeftXLeft = getRelativeLeft(binding.ivuserthreethrowcard);
        int LeftYLeft = getRelativeTop(binding.ivuserthreethrowcard);
        int TopXTop = getRelativeLeft(binding.ivusertwothrowcard);
        int TopYTop = getRelativeTop(binding.ivusertwothrowcard);
        int RightXRight = getRelativeLeft(binding.ivuserfivethrowcard);
        int RightYRight = getRelativeTop(binding.ivuserfivethrowcard);

        if (seat == BottomSeatIndex) {

            AnimationSet setBottom = new AnimationSet(true);
            RotateAnimation rotateBottom = new RotateAnimation(0, 180,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateBottom.setStartOffset(50);
            rotateBottom.setDuration(cardAnimDuration - 50);

            setBottom.addAnimation(rotateBottom);

            float toX = MyDeckX - BottomX;
            float toY = getRelativeTop(binding.userOneBg) - BottomY;

            TranslateAnimation translateBottom = new TranslateAnimation(0, toX,
                    0, toY);
            translateBottom.setDuration(cardAnimDuration);

            setBottom.addAnimation(translateBottom);
            setBottom.setFillAfter(false);

            AnimationSet setLeft = new AnimationSet(true);
            RotateAnimation rotateLeft = new RotateAnimation(0, 90,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateLeft.setStartOffset(50);
            rotateLeft.setDuration(cardAnimDuration - 50);

            setLeft.addAnimation(rotateLeft);

            toX = MyDeckX - LeftX;
            toY = getRelativeTop(binding.userOneBg) - LeftY;

            TranslateAnimation translateLeft = new TranslateAnimation(0, toX,
                    0, toY);
            translateLeft.setDuration(cardAnimDuration);

            setLeft.addAnimation(translateLeft);
            setLeft.setFillAfter(false);

            toX = MyDeckX - TopX;
            toY = getRelativeTop(binding.userOneBg) - TopY;

            TranslateAnimation translateTop = new TranslateAnimation(0, toX, 0,
                    toY);
            translateTop.setDuration(cardAnimDuration);
            translateTop.setFillAfter(false);

            AnimationSet setRight = new AnimationSet(true);
            RotateAnimation rotateRight = new RotateAnimation(0, -90,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateRight.setStartOffset(50);
            rotateRight.setDuration(cardAnimDuration - 50);

            setRight.addAnimation(rotateRight);

            toX = MyDeckX - RightX;
            toY = getRelativeTop(binding.userOneBg) - RightY;

            TranslateAnimation translateRight = new TranslateAnimation(0, toX,
                    0, toY);
            translateRight.setDuration(cardAnimDuration);

            setRight.addAnimation(translateRight);
            setRight.setFillAfter(false);

//            if (c.jsonData.getTableInfo().getActivePlayers() == 2) {
//                binding.ivCardBottomCenter.startAnimation(setBottom);
//                if (DealCardToSeat[LeftSeatIndex]) {
//                    binding.ivCardLeftCenter.startAnimation(setLeft);
//                } else if (DealCardToSeat[TopSeatIndex]) {
//                    binding.ivCardTopCenter.startAnimation(setLeft);
//                } else if (DealCardToSeat[RightSeatIndex]) {
//                    binding.ivCardRightCenter.startAnimation(setLeft);
//                }
//            } else if (c.jsonData.getTableInfo().getActivePlayers() == 3) {
//                binding.ivCardBottomCenter.startAnimation(setBottom);
//                binding.ivCardLeftCenter.startAnimation(setLeft);
//                binding.ivCardTopCenter.startAnimation(translateTop);
//            } else {
//                binding.ivCardBottomCenter.startAnimation(setBottom);
//                binding.ivCardLeftCenter.startAnimation(setLeft);
//                binding.ivCardTopCenter.startAnimation(translateTop);
//                binding.ivCardRightCenter.startAnimation(setRight);
//            }

            binding.ivCardBottomCenter.bringToFront();
            binding.ivCardBottomCenter.startAnimation(setBottom);
            if (DealCardToSeat[LeftSeatIndex]) {
                binding.ivCardLeftCenter.bringToFront();
                binding.ivCardLeftCenter.startAnimation(setLeft);
            }
            if (DealCardToSeat[TopSeatIndex]) {
                binding.ivCardTopCenter.bringToFront();
                binding.ivCardTopCenter.startAnimation(translateTop);
            }
            if (DealCardToSeat[RightSeatIndex]) {
                binding.ivCardRightCenter.bringToFront();
                binding.ivCardRightCenter.startAnimation(setRight);
            }

        } else if (seat == LeftSeatIndex) {
            AnimationSet setBottom = new AnimationSet(true);
            RotateAnimation rotateBottom = new RotateAnimation(0, -90,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateBottom.setStartOffset(50);
            rotateBottom.setDuration(cardAnimDuration - 50);

            setBottom.addAnimation(rotateBottom);

//            float toX = myData.Ori_Width * 50 / -1280;
            BottomX = binding.getRoot().getWidth() / 2/*- (cWidth / 3))*/;

            float toX = LeftXLeft - BottomX;
            float toY = LeftYLeft - BottomY;

            TranslateAnimation translateBottom = new TranslateAnimation(0, toX,
                    0, toY);
            translateBottom.setDuration(cardAnimDuration);

            setBottom.addAnimation(translateBottom);
            setBottom.setFillAfter(false);

            AnimationSet setLeft = new AnimationSet(true);
            RotateAnimation rotateLeft = new RotateAnimation(0, -180,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateLeft.setStartOffset(50);
            rotateLeft.setDuration(cardAnimDuration - 50);

            setLeft.addAnimation(rotateLeft);

            toX = LeftXLeft - LeftX;
            toY = LeftYLeft - LeftY;

            TranslateAnimation translateLeft = new TranslateAnimation(0, toX,
                    0, toY);
            translateLeft.setDuration(cardAnimDuration);

            setLeft.addAnimation(translateLeft);
            setLeft.setFillAfter(false);

            AnimationSet setTop = new AnimationSet(true);
            RotateAnimation rotateTop = new RotateAnimation(0, 90,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateTop.setStartOffset(50);
            rotateTop.setDuration(cardAnimDuration - 50);

            setTop.addAnimation(rotateTop);

            toX = LeftXLeft - TopX;
            toY = LeftYLeft - TopY;

            TranslateAnimation translateTop = new TranslateAnimation(0, toX, 0,
                    toY);
            translateTop.setDuration(cardAnimDuration);
            translateTop.setFillAfter(false);

            setTop.addAnimation(translateTop);
            setTop.setFillAfter(false);

            toX = LeftXLeft - RightX;
            toY = LeftYLeft - RightY;

            TranslateAnimation translateRight = new TranslateAnimation(0, toX,
                    0, toY);
            translateRight.setDuration(cardAnimDuration);
            translateRight.setFillAfter(false);


            binding.ivCardBottomCenter.bringToFront();
            binding.ivCardBottomCenter.startAnimation(setBottom);
            if (DealCardToSeat[LeftSeatIndex]) {
                binding.ivCardLeftCenter.bringToFront();
                binding.ivCardLeftCenter.startAnimation(setLeft);
            }
            if (DealCardToSeat[TopSeatIndex]) {
                binding.ivCardTopCenter.bringToFront();
                binding.ivCardTopCenter.startAnimation(translateTop);
            }
            if (DealCardToSeat[RightSeatIndex]) {
                binding.ivCardRightCenter.bringToFront();
                binding.ivCardRightCenter.startAnimation(translateRight);
            }


        } else if (seat == TopSeatIndex) {

            float toX = TopXTop - BottomX;
            float toY = TopYTop - BottomY;

            TranslateAnimation translateBottom = new TranslateAnimation(0, toX,
                    0, toY);
            translateBottom.setDuration(cardAnimDuration);
            translateBottom.setFillAfter(false);

            AnimationSet setLeft = new AnimationSet(true);
            RotateAnimation rotateLeft = new RotateAnimation(0, 90,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateLeft.setStartOffset(50);
            rotateLeft.setDuration(cardAnimDuration - 50);

            setLeft.addAnimation(rotateLeft);

            toX = TopXTop - LeftX;
            toY = TopYTop - LeftY;

            TranslateAnimation translateLeft = new TranslateAnimation(0, toX,
                    0, toY);
            translateLeft.setDuration(cardAnimDuration);

            setLeft.addAnimation(translateLeft);
            setLeft.setFillAfter(false);

            AnimationSet setTop = new AnimationSet(true);
            RotateAnimation rotateTop = new RotateAnimation(0, -180,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateTop.setStartOffset(50);
            rotateTop.setDuration(cardAnimDuration - 50);

            setTop.addAnimation(rotateTop);

            toX = TopXTop - TopX;
            toY = TopYTop - TopY;

            TranslateAnimation translateTop = new TranslateAnimation(0, toX, 0,
                    toY);
            translateTop.setDuration(cardAnimDuration);
            translateTop.setFillAfter(false);

            setTop.addAnimation(translateTop);
            setTop.setFillAfter(false);

            AnimationSet setRight = new AnimationSet(true);
            RotateAnimation rotateRight = new RotateAnimation(0, -90,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateRight.setStartOffset(50);
            rotateRight.setDuration(cardAnimDuration - 50);
            setRight.addAnimation(rotateRight);

            toX = TopXTop - RightX;
            toY = TopYTop - RightY;

            TranslateAnimation translateRight = new TranslateAnimation(0, toX,
                    0, toY);
            translateRight.setDuration(cardAnimDuration);
            translateRight.setFillAfter(false);

            setRight.addAnimation(translateRight);

            binding.ivCardBottomCenter.bringToFront();
            binding.ivCardBottomCenter.startAnimation(translateBottom);
            if (DealCardToSeat[LeftSeatIndex]) {
                binding.ivCardLeftCenter.bringToFront();
                binding.ivCardLeftCenter.startAnimation(setLeft);
            }
            if (DealCardToSeat[TopSeatIndex]) {
                binding.ivCardTopCenter.bringToFront();
                binding.ivCardTopCenter.startAnimation(translateTop);
            }
            if (DealCardToSeat[RightSeatIndex]) {
                binding.ivCardRightCenter.bringToFront();
                binding.ivCardRightCenter.startAnimation(setRight);
            }


        } else if (seat == RightSeatIndex) {
            // RIGHT USER CARD COLLECT

            AnimationSet setBottom = new AnimationSet(true);
            RotateAnimation rotateBottom = new RotateAnimation(0, 90,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateBottom.setStartOffset(50);
            rotateBottom.setDuration(cardAnimDuration - 50);

            setBottom.addAnimation(rotateBottom);

            float toX = RightXRight - BottomX;
            float toY = RightYRight - BottomY;

            TranslateAnimation translateBottom = new TranslateAnimation(0, toX,
                    0, toY);
            translateBottom.setDuration(cardAnimDuration);
            translateBottom.setFillAfter(false);

            setBottom.addAnimation(translateBottom);
            setBottom.setFillAfter(false);

            toX = RightXRight - LeftX;
            toY = RightYRight - LeftY;

            TranslateAnimation translateLeft = new TranslateAnimation(0, toX,
                    0, toY);
            translateLeft.setDuration(cardAnimDuration);
            translateLeft.setFillAfter(false);

            AnimationSet setTop = new AnimationSet(true);
            RotateAnimation rotateTop = new RotateAnimation(0, -90,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateTop.setStartOffset(50);
            rotateTop.setDuration(cardAnimDuration - 50);

            setTop.addAnimation(rotateTop);

            toX = RightXRight - TopX;
            toY = RightYRight - TopY;

            TranslateAnimation translateTop = new TranslateAnimation(0, toX, 0,
                    toY);
            translateTop.setDuration(cardAnimDuration);
            translateTop.setFillAfter(false);

            setTop.addAnimation(translateTop);
            setTop.setFillAfter(false);

            AnimationSet setRight = new AnimationSet(true);
            RotateAnimation rotateRight = new RotateAnimation(0, -180,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateRight.setStartOffset(50);
            rotateRight.setDuration(cardAnimDuration - 50);
            setRight.addAnimation(rotateRight);

            toX = RightXRight - RightX;
            toY = RightYRight - RightY;

            TranslateAnimation translateRight = new TranslateAnimation(0, toX,
                    0, toY);
            translateRight.setDuration(cardAnimDuration);
            translateRight.setFillAfter(false);

            setRight.addAnimation(translateRight);

            binding.ivCardBottomCenter.bringToFront();
            binding.ivCardBottomCenter.startAnimation(setBottom);
            if (DealCardToSeat[LeftSeatIndex]) {
                binding.ivCardLeftCenter.bringToFront();
                binding.ivCardLeftCenter.startAnimation(translateLeft);
            }
            if (DealCardToSeat[TopSeatIndex]) {
                binding.ivCardTopCenter.bringToFront();
                binding.ivCardTopCenter.startAnimation(translateTop);
            }
            if (DealCardToSeat[RightSeatIndex]) {
                binding.ivCardRightCenter.bringToFront();
                binding.ivCardRightCenter.startAnimation(setRight);
            }
        }
        new Handler().postDelayed(() -> {
            String bidText = totalBidWin + "/" + totalBid;
            if (seat == BottomSeatIndex) {
                binding.tvUserBids.setText(bidText);
            } else if (seat == LeftSeatIndex) {
                binding.tvUserThreeBids.setText(bidText);
            } else if (seat == TopSeatIndex) {
                binding.tvUserTwoBids.setText(bidText);
            } else if (seat == RightSeatIndex) {
                binding.tvUserFiveBids.setText(bidText);
            }
            binding.ivCardBottomCenter.setVisibility(View.INVISIBLE);
            binding.ivCardTopCenter.setVisibility(View.INVISIBLE);
            binding.ivCardRightCenter.setVisibility(View.INVISIBLE);
            binding.ivCardLeftCenter.setVisibility(View.INVISIBLE);
        }, cardAnimDuration);
//        musicManager.hathComing();
    }

    private void FirstDrawAnim(ArrayList<Item_Card> userCards) {
        Logger.print(TAG, "User Cards before sortBy color => " + userCards.toString());
        card_BottomUser = userCards;
        if (card_BottomUser.size() > 0) {
            handlerTimer();
        }
        new Handler().postDelayed(() -> {
            reDrawUserCards(card_BottomUser);
            isAnimStop = true;
        }, 670);
    }

    int runCounter;

    private void handlerTimer() {
        try {
            cardHandler = new Handler();
            int flipAnimCounter = 0;
            final int size = ivMyCards.length;
            runCounter = 0;
            if (card_BottomUser.size() > 0) {
                while (flipAnimCounter < 13) {
                    Logger.print(TAG, "FLIPCOUNTER => " + flipAnimCounter);
                    cardHandler.postDelayed(() -> {
                        if (runCounter < size) {
                            try {
                                ivMyCards[runCounter].setImageResource(R.drawable.card);
                                Item_Card card = card_BottomUser.get(runCounter);
                                String s1 = getCardString(card);
                                int indexOfs1 = Arrays.asList(cardString).indexOf(s1);
                                if (indexOfs1 != -1) {
                                    final int id = CardDefault[indexOfs1];
                                    flip(ivMyCards[runCounter], ivMyCards[runCounter], id);
                                }
                                runCounter++;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 35 * flipAnimCounter);
                    flipAnimCounter++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Flip animation on card distribution
    private void flip(final ImageView front, final ImageView back, final int id) {

        new Handler().postDelayed(() -> back.setImageResource(id), 600 / 2);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(
                ObjectAnimator.ofFloat(front, "rotationY", 90).setDuration(
                        600 / 2),
                ObjectAnimator.ofInt(front, "visibility", View.VISIBLE)
                        .setDuration(0),
                ObjectAnimator.ofFloat(back, "rotationY", -90).setDuration(0),
                ObjectAnimator.ofInt(back, "visibility", View.VISIBLE)
                        .setDuration(0),
                ObjectAnimator.ofFloat(back, "rotationY", 0).setDuration(
                        600 / 2));
        set.start();

        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
    }


    private void directSetUserCards() {
        binding.notificationTextTimer.setVisibility(View.GONE);
        binding.allnotificationText.setVisibility(View.GONE);

        if (binding.ivCardForDistribution.getAnimation() != null) {
            binding.ivCardForDistribution.clearAnimation();
        }
        binding.ivCardForDistribution.setVisibility(View.GONE);

        if (card_BottomUser.size() > 0) {
            card_BottomUser = new ArrayList<>(SortMyCardbyColor(card_BottomUser));
            reDrawUserCards(card_BottomUser);
        }
    }

    // Returns value(Rank) of card
    private String getCardValue(String card) {
        if (card != null) {
            String[] splitedCard = card.split("-");
            if (splitedCard.length > 1)
                return splitedCard[1];
        }
        return "";
    }

    // Returns color of Card
    private String getCardColor(String card) {
        if (card != null) {
            String[] splitedCard = card.split("-");
            return splitedCard[0];
        }
        return "";
    }

    private int second;
    private int timercounter = 30000;
    private Timer timer = new Timer();

    private void RoundTimerStartProcess(int timer) {

        Logger.print(TAG, "TIMER 3");
        second = timer;
        Logger.print(TAG, "MSG Show Message Timer " + second);
        if (second > 0) {
            Logger.print(TAG, "MSG Show Message Timer");
            binding.notificationTextTimer.setVisibility(View.VISIBLE);
            Log.e("CHECK_ACTIVE", " before startTimer ");
            startTimer(second);
        }
    }

    private void startTimer(final int remainingSeconds) {
        Log.e("CHECK_ACTIVE", " in startTimer ");
        second = remainingSeconds;
        timercounter = second * 1000;
        Logger.print(TAG, "MSG Show Message Timer => 2 " + second);
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (second > 0) {
                        binding.tvTimerText2.setText(second + "");
                        if (second < remainingSeconds / 2) {
                            enableBackButton(false, "HALF TIMER REMAIN");
                        }
                    } else {
                        Log.e("CHECK_ACTIVE", " finish Timer handler -> " + (Activity_CallBreakScoreBoard.handler != null));
                        if (Activity_CallBreakScoreBoard.handler != null) {
                            Message m = new Message();
                            m.what = ResponseCodes.FinishCBScoreBoard;
                            Activity_CallBreakScoreBoard.handler.sendMessage(m);
                        }
                        binding.notificationTextTimer.setVisibility(View.GONE);
                        if (timer != null) {
                            timer.cancel();
                        }
                    }
                });
                timercounter = timercounter - 100;
                if (timercounter % 1000 == 0) {
                    second--;
                }
            }
        }, 0, 100);
    }


    private void UserTurnStartedProcess(String data) {
        HideBidSelectionDialog();

        binding.loaderBg.setVisibility(View.GONE);
        JSONObject UserTurnStartedObject;

        if (binding.allnotificationText.getVisibility() == View.VISIBLE)
            binding.allnotificationText.setVisibility(View.GONE);

        totalTime = (float) c.SLOW_TABLE_TIMER * 1000;
//        int timerSecond = c.SLOW_TABLE_TIMER;

        try {
            UserTurnStartedObject = new JSONObject(data);
            int seatIndex = UserTurnStartedObject.getJSONObject(Parameters.data).getInt(Parameters.NextTurn);


            enableBackButton(true, " UserTurn Started Process");
            mLastClickTime = SystemClock.elapsedRealtime() + 200;

            Logger.print(TAG, "MSG Hide Message");
            binding.notificationText.setVisibility(View.GONE);
            binding.notificationText.setText("");
            if (seatIndex == BottomSeatIndex) {
                isUserTurn = true;
                binding.UserProgress.setVisibility(View.VISIBLE);
                binding.userTimer.setVisibility(View.VISIBLE);
                if (!GameisOn()) {
                    showNotification("Take your turn...");
                }
                redrawCardsWithCurrentDeckCards();
            }
            resetTimerAndAnimation(seatIndex, false);
            SetUserTime(UserTurnStartedObject.has("timer") ? UserTurnStartedObject.getInt("timer") : userTurnTimer
            );

            if (isUserTurn(seatIndex)) {

                Logger.print(TAG, "Its User Turn 1 " + UserTurnStartedObject.toString());
                if (card_BottomUser.size() > 0) {
                    reDrawUserCards(card_BottomUser);
                }

                if (PreferenceManager.getVibrate() && !music_manager.isCallActive()) {
                    // Vibrate for 500 milliseconds
                    v.vibrate(500);
                }
                if (!GameisOn()) {
                    showNotification("Its your turn, please take your turn..");
                } else {
                    music_manager.play_Notification();
                }

                if (c.jsonData.getTableInfo().getPlayer_info().getJSONObject(seatIndex) == null) {
                    EmitManager.Process(new JSONObject(), Events.GetTableInfo);
                }
                isUserTurn = true;

                setClickable(ResponseCodes.UserTurnStartedResp);

//                if (isHukumcardInOpenDeck) {
//                    ivCloseCard.setClickable(false);
//                } else {
//                    ivCloseCard.setClickable(true);
//                }

            } else {
                isUserTurn = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // called when user join table from JT event
    private void JoinTable(String string) {

        try {
            JSONObject jObject = new JSONObject(string);
            if (!jObject.getBoolean(Parameters.Flag)) {
                return;
            }
            int seatIndex = jObject.getJSONObject(Parameters.data).getInt(Parameters.SeatIndex);
            if (jObject.getJSONObject(Parameters.data).has(Parameters._id)) {
                if (!jObject.getJSONObject(Parameters.data).getString(Parameters._id).equals(PreferenceManager.get_id())) {
                    c.jsonData.getTableInfo().setActivePlayers(c.jsonData.getTableInfo().getActivePlayers() + 1);
                }
            }

            JSONObject jOb = new JSONObject();
            JSONObject userinfo = jObject.getJSONObject(Parameters.data);
            jOb.put(Parameters.SeatIndex, seatIndex);
            jOb.put(Parameters.JoinTime, c.getTimeStamp(System.currentTimeMillis()));
            jOb.put(Parameters.Status, "");
            jOb.put(Parameters.User_Info, userinfo);

            c.jsonData.getTableInfo().getUnChangedplayer_info().put(seatIndex, jOb);

            if (c.jsonData.getTableInfo().getActivePlayers() <= 1) {
                binding.helpText.setVisibility(View.VISIBLE);
                binding.helpText.setText("Waiting for opponent.");
            } else if (!roundAlreadyStarted) {
                enableSwitchButton(false, "MORE THAN ! ACTIVE PLAYER");
                binding.helpText.setVisibility(View.GONE);
            }
            setDataForJoinTable(userinfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // set data of user in table from GTI response
    private void setDataForJoinTable(JSONObject userinfo) {
        Logger.print(TAG, "SET DATA FOR JOIN TABLE : " + userinfo);

        int sIndex;
        String userName;

        try {
            if (userinfo != null) {

                sIndex = userinfo.getInt(Parameters.SeatIndex);
                userName = userinfo.getString(Parameters.User_Name);
                userName = getProUserName(userName);

                if (sIndex != BottomSeatIndex && !GameisOn()) {
                    showNotification(userName + " has join table, New round started..");
                }

                if (sIndex == BottomSeatIndex) {

                    binding.ivUserName.setVisibility(View.VISIBLE);
                    binding.ivUserName.setText(userName);

                } else if (sIndex == RightSeatIndex) {

                    binding.ivUserNameFive.setVisibility(View.VISIBLE);
                    binding.ivUserFiveStatus.setText("NEXT GAME");
                    binding.ivUserFiveStatus.setVisibility(isRoundAlreadyStarted ? View.VISIBLE : View.GONE);
                    binding.tvUserFiveBids.setVisibility(View.VISIBLE);
                    binding.ivUserNameFive.setText(userName);
                    binding.userFiveBg.setBackgroundResource(R.drawable.new_bg_player);

                } else if (sIndex == LeftSeatIndex) {

                    binding.ivUserNameThree.setVisibility(View.VISIBLE);
                    binding.ivUserThreeStatus.setText("NEXT GAME");
                    binding.ivUserThreeStatus.setVisibility(isRoundAlreadyStarted ? View.VISIBLE : View.GONE);
                    binding.tvUserThreeBids.setVisibility(View.VISIBLE);
                    binding.ivUserNameThree.setText(userName);
                    binding.userThreeBg.setBackgroundResource(R.drawable.new_bg_player);

                } else if (sIndex == TopSeatIndex) {

                    binding.ivUserNameTwo.setVisibility(View.VISIBLE);
                    binding.ivUserTwoStatus.setVisibility(isRoundAlreadyStarted ? View.VISIBLE : View.GONE);
                    binding.ivUserTwoStatus.setText("NEXT GAME");
                    binding.tvUserTwoBids.setVisibility(View.VISIBLE);
                    binding.ivUserNameTwo.setText(userName);
                    binding.userTwoBg.setBackgroundResource(R.drawable.new_bg_player);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNotification(String Message) {
        if (PreferenceManager.getNotification()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationUtils mNotificationUtils = new NotificationUtils(this);
                NotificationCompat.Builder nb = mNotificationUtils.getAndroidChannelNotification(Message);
                mNotificationUtils.getManager().notify(101, nb.build());

            } else {
                Intent intent = new Intent(this, CallBreak_PlayingScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
                Notification mNotification = prepareNotification(Message, pIntent);
                mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, mNotification);
            }
        }
    }

    private Notification prepareNotification(String msg, PendingIntent intent) {

        Notification.Builder builder = new Notification.Builder(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_777);
            builder.setSmallIcon(R.drawable.icn_rewardpoint)
                    .setLargeIcon(largeIcon)
                    .setContentTitle("777 Games")
                    .setContentText(msg)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(intent);
        } else {
            builder.setSmallIcon(R.drawable.logo_777)
                    .setContentTitle("777 Games")
                    .setContentText(msg)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(intent);
        }
        Notification notification = builder.getNotification();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        return notification;
    }

    @Override
    public void onClick(View v) {
        if (binding.settingContainer.getVisibility() == View.VISIBLE)
            binding.settingContainer.setVisibility(View.GONE);
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        if (v == binding.playSetting) {
            music_manager.buttonClick();
            if (binding.settingContainer.getVisibility() != View.VISIBLE)
                binding.settingContainer.setVisibility(View.VISIBLE);
            else
                binding.settingContainer.setVisibility(View.GONE);
        } else if (v == binding.playExit) {
            music_manager.buttonClick();
            String msg;
            msg = isStartPlaying ? "You will lose entire bet \nif you leave table"
                    : "Are you sure \nwant to leave table ?";
            showFinishGameDialog("Alert", msg);

        } else if (v == binding.btnTblInfo) {
            music_manager.buttonClick();
            if (tableInfoDialog.isShowing())
                tableInfoDialog.dismiss();
            if (tableInfoDialog != null && !tableInfoDialog.isShowing()) {
                c.hideNavigationToDialog(this, tableInfoDialog);
            }
        } else if (v == binding.btnScoreInfo) {
            music_manager.buttonClick();
            EmitManager.Process(new JSONObject(), Events.GetRoundDetails);
        } else if (v == binding.htpIcn) {
            Intent i10 = new Intent(CallBreak_PlayingScreen.this, Activity_HowToPlay.class);
            startActivity(i10);
            overridePendingTransition(0, R.anim.dialogue_scale_anim_exit);
        } else if (v == binding.icnWallet || v == binding.tvUserChips || v == binding.walletTxt) {
            music_manager.buttonClick();
            EmitManager.Process(new JSONObject(), Events.UserWalletValue);
        } else if (v == binding.btnSwitchTable) {
            music_manager.buttonClick();
            switchTableCall();
        } else if (v == binding.btnDiscard) {
            music_manager.buttonClick();
            try {
                JSONObject obj = new JSONObject();
                obj.put("c", selectedCard.get(0));
                EmitManager.Process(obj, Events.CardMove);
                setClickable(ResponseCodes.ThrowCardOnDeckResp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void switchTableCall() {
        try {
            if (c.jsonData.getTableInfo() != null) {
                JSONObject jObj = new JSONObject();
                try {
                    setClickable(ResponseCodes.SwitchTableResp);
                    c.isSwitchingTable = true;
                    jObj.put(Parameters.CategoryId, c.jsonData.getTableInfo().getCatId());
                    jObj.put(Parameters.BootValue, c.jsonData.getTableInfo().getBootValue());
                    GlobalLoaderSHOW(getResources().getString(R.string.PleaseWait));
                    EmitManager.Process(jObj, Events.SwitchTable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Dialog finishGameDialog;

    private void closeFinishGameDialog() {
        try {
            if (finishGameDialog != null) {
                if (finishGameDialog.isShowing()) {
                    finishGameDialog.dismiss();
                    finishGameDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFinishGameDialog(String title, String msg) {
        closeFinishGameDialog();
        finishGameDialog = new NewCommonDialog.Builder()
                .setTitle(title)
                .setPopupBg(R.drawable.cb_popup_bg)
                .setMessage(msg)
                .setCancelable(false)
                .setCloseVisible(false)
                .setPositiveButton("Yes", () -> {
                    if (title.equalsIgnoreCase("Alert")) {
                        music_manager.buttonClick();
                        c.IsmoreGameopen = true;
                        c.exitToLobby = true;
                        c.isAcceptNotification = false;
                        LeaveTable();
                    }
                })
                .setNegativeButton("No", () -> music_manager.buttonClick())
                .create(CallBreak_PlayingScreen.this);
        finishGameDialog.show();
    }

    // user to called LeaveTable event
    private void LeaveTable() {

        try {
            JSONObject jObj = new JSONObject();
            jObj.put("OnSeat", c.jsonData.getTableInfo().hasJoinedTable() ? 1 : 0);
            jObj.put("SubType", Parameters.LeavingTable);
            if (c.jsonData.getTableInfo().hasJoinedTable())
                jObj.put(Parameters.SeatIndex, c.jsonData.getTableInfo().UserSeatIndex(PreferenceManager.get_id()));

            GlobalLoaderSHOW(getResources().getString(R.string.PleaseWait));
            EmitManager.Process(jObj, Events.LeaveTable);
        } catch (Exception e) {

            finish();
            releaseMemory();
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

//        if (StandUp_Btn.isClickable())
//            StandUp_Btn.performClick();
    }

    @Override
    public void finish() {
        super.finish();
        Log.e("FIND_ISSUE", " on FINISH called ");
    }

    public class NotificationUtils extends ContextWrapper {

        public static final String ANDROID_CHANNEL_ID = "com.Rummy777.ANDROID";
        public static final String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";
        private NotificationManager mManager;

        public NotificationUtils(Context base) {
            super(base);
            createChannels();
        }

        public void createChannels() {
            // create android channel
            NotificationChannel androidChannel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                        ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                // Sets whether notifications posted to this channel should display notification lights
                androidChannel.enableLights(true);
                // Sets whether notification posted to this channel should vibrate.
                androidChannel.enableVibration(true);
                // Sets the notification light color for notifications posted to this channel
                androidChannel.setLightColor(Color.GREEN);
                // Sets whether notifications posted to this channel appear on the lockscreen or not
                androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

                getManager().createNotificationChannel(androidChannel);
            }
        }

        public NotificationManager getManager() {
            if (mManager == null) {
                mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            return mManager;
        }

        public NotificationCompat.Builder getAndroidChannelNotification(String msg) {


            Intent intent = new Intent(this, CallBreak_PlayingScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            intent.putExtra("ReloadData", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(CallBreak_PlayingScreen.this, 0, intent, 0);
            Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.logo_777);
            return new NotificationCompat.Builder(this, ANDROID_CHANNEL_ID)
                    .setSmallIcon(R.drawable.icn_rewardpoint)
                    .setContentTitle("777 Games")
                    .setContentText(msg)
                    .setLargeIcon(largeIcon)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
        }
    }

    // return true if game is in forground and vice-versa.
    private boolean GameisOn() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().equalsIgnoreCase(this.getPackageName())) {
            isActivityFound = true;
        }

        if (isActivityFound) {
            return true;
        } else {
            RefreshView = true;
        }
        return false;
    }

    private void resetTimerAndAnimation(int seatIndex, boolean isFinishTimer) {
        binding.UserFiveProgress.setVisibility(View.INVISIBLE);
        binding.UserProgress.setVisibility(View.INVISIBLE);
        binding.UserThreeProgress.setVisibility(View.INVISIBLE);
        binding.UserTwoProgress.setVisibility(View.INVISIBLE);

        binding.userTimer.setVisibility(View.INVISIBLE);
        binding.userTwoTimer.setVisibility(View.INVISIBLE);
        binding.userThreeTimer.setVisibility(View.INVISIBLE);
        binding.userFiveTimer.setVisibility(View.INVISIBLE);

        if (turnCountDownTimer != null) {
            turnCountDownTimer.cancel();
        }

        if (seatIndex == LeftSeatIndex) {
            binding.userThreeTimer.setVisibility(isFinishTimer ? View.INVISIBLE : View.VISIBLE);
            binding.UserThreeProgress.setVisibility(isFinishTimer ? View.INVISIBLE : View.VISIBLE);

        } else if (seatIndex == RightSeatIndex) {
            binding.userFiveTimer.setVisibility(isFinishTimer ? View.INVISIBLE : View.VISIBLE);
            binding.UserFiveProgress.setVisibility(isFinishTimer ? View.INVISIBLE : View.VISIBLE);

        } else if (seatIndex == BottomSeatIndex) {
            binding.userTimer.setVisibility(isFinishTimer ? View.INVISIBLE : View.VISIBLE);
            binding.UserProgress.setVisibility(isFinishTimer ? View.INVISIBLE : View.VISIBLE);

        } else if (seatIndex == TopSeatIndex) {
            binding.userTwoTimer.setVisibility(isFinishTimer ? View.INVISIBLE : View.VISIBLE);
            binding.UserTwoProgress.setVisibility(isFinishTimer ? View.INVISIBLE : View.VISIBLE);
        }
    }

    int timers = 0;
    int maxTimer = 0;

    private void SetUserTime(int second) {

        turnCountDownTimer = new CountDownTimer(second * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                binding.userTimer.setText(String.format("%d", millisUntilFinished / 1000));
                binding.userTwoTimer.setText(String.format("%d", millisUntilFinished / 1000));
                binding.userThreeTimer.setText(String.format("%d", millisUntilFinished / 1000));
                binding.userFiveTimer.setText(String.format("%d", millisUntilFinished / 1000));
            }

            public void onFinish() {
                binding.userTimer.setVisibility(View.GONE);
                binding.userTwoTimer.setVisibility(View.GONE);
                binding.userThreeTimer.setVisibility(View.GONE);
                binding.userFiveTimer.setVisibility(View.GONE);
            }
        }.start();

        if (UserTimer != null) {
            UserTimer.cancel();
            UserTimer = null;
        }

        timers = (second == userTurnTimer) ? 0 : second * 10;
        maxTimer = userTurnTimer * 10;
        binding.UserProgress.setMax(maxTimer * 2);
        binding.UserThreeProgress.setMax(maxTimer);
        binding.UserFiveProgress.setMax(maxTimer);
        binding.UserTwoProgress.setMax(maxTimer);

        incProgress = (1 / (totalTime / 100));
        seconds = second;
        cnt = 0;
        currentProgress = 1;
        if (seconds != c.SLOW_TABLE_TIMER)
            currentProgress = (incProgress * (second * 10));

        UserTimer = new Timer();
        UserTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (timers < userTurnTimer * 10) {
                    timers += 1;
                }

                currentProgress = currentProgress - incProgress;
                if (currentProgress <= 0) {
                    if (UserTimer != null) {
                        UserTimer.cancel();
                        UserTimer = null;
                    }
                }
                cnt++;
                if (cnt >= 10) {
                    cnt = 0;
                    seconds--;
//                    if (seconds < 15) {
//                        if (isUserTurn) {
//                            if (seconds == 1) {
//                                Message showMsg2 = new Message();
////                                showMsg2.what = ResponseCodes.HideAllControllsOnTimeOut;
////                                showMsg2.arg1 = 1;
////                                if (handler != null)
////                                    handler.sendMessage(showMsg2);
//                            }
//                        }
//                    }
                }

                runOnUiThread(() -> {
//                        UserProgress.setProgress((int) currentProgress);
                    binding.UserProgress.setProgress(timers);
                    binding.UserThreeProgress.setProgress(timers);
                    binding.UserFiveProgress.setProgress(timers);
                    binding.UserTwoProgress.setProgress(timers);
                });
            }
        }, 0, 100);
    }


    private boolean isUserTurn(int SeatIndex) throws JSONException {
        for (int i = 0; i < c.jsonData.getTableInfo().getPlayer_info().length(); i++) {
            if (c.jsonData.getTableInfo().getPlayer_info().getJSONObject(i).length() > 0) {
                JSONObject j = c.jsonData.getTableInfo().getPlayer_info().getJSONObject(i);
                if (j.getInt(Parameters.SeatIndex) == SeatIndex && j.getJSONObject(Parameters.User_Info)
                        .get(Parameters._id).equals(PreferenceManager.get_id())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void enableBackButton(boolean enable, String caller) {
//        Logger.print(TAG, enable + " ENABLE_BACK_BUTTON -> " + caller);
        binding.playExit.setClickable(enable);
        binding.playExit.setAlpha(enable ? 1.0f : 0.5f);
    }

    private void enableSwitchButton(boolean enable, String caller) {
//        Logger.print(TAG, enable + " ENABLE_BACK_BUTTON -> " + caller);
        binding.btnSwitchTable.setClickable(enable);
        binding.btnSwitchTable.setAlpha(enable ? 1.0f : 0.5f);
    }


    public void LoaderFinish(final int i) {
        try {
            runOnUiThread(() -> {
                if (loader != null) {
                    loader.FinishMe(i);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GlobalLoaderSHOW(final String message) {
        try {
            runOnUiThread(() -> {
                Logger.print(TAG, "<<<<<<<< LOADER SHOW IN PLAY SCREEN: "
                        + message);
                loader.ShowMe("" + message);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    TextView gameId, infoGameType, pointValue, initialDrop, middleDrop, wrongShow, leaveTablePoint,
            infoDisConnection, cardsDeck, jokerDeck, infoTurnTimer, showTimer, txtPointValue, txtInitialDrop, txtMiddleDrop;
    ImageView closeInfoPopup;

    int userTurnTimer;

    private void setupTableInfoDialog() {
        if (tableInfoDialog != null && tableInfoDialog.isShowing()) {
            tableInfoDialog.dismiss();
        }
        tableInfoDialog = new Dialog(this, R.style.Theme_TransparentBuddies);
        tableInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tableInfoDialog.setContentView(R.layout.dialog_tableinfo);
        tableInfoDialog.setCancelable(true);
        tableInfoDialog.setOwnerActivity(CallBreak_PlayingScreen.this);

        closeInfoPopup = tableInfoDialog.findViewById(R.id.close_infopopup);
        closeInfoPopup.setBackgroundResource(R.drawable.ic_icn_close);
        closeInfoPopup.setOnClickListener(v -> {
            if (tableInfoDialog.isShowing())
                tableInfoDialog.dismiss();
        });
        ConstraintLayout diaRootView = tableInfoDialog.findViewById(R.id.diaRootView);
        diaRootView.setOnClickListener(v -> {
            if (tableInfoDialog.isShowing())
                tableInfoDialog.dismiss();
        });
        ImageView main_bg = tableInfoDialog.findViewById(R.id.main_bg);
        main_bg.setBackgroundResource(R.color.cb_popup_bg);
        ImageView idBg = tableInfoDialog.findViewById(R.id.id_bg);
        idBg.setBackgroundResource(R.drawable.bg_timeinfo);
        gameId = tableInfoDialog.findViewById(R.id.game_id);
        TextView txt_gametype = tableInfoDialog.findViewById(R.id.txt_gametype);
        txt_gametype.setVisibility(View.GONE);
        infoGameType = tableInfoDialog.findViewById(R.id.info_game_type);
        infoGameType.setText("CallBreak");
        txtPointValue = tableInfoDialog.findViewById(R.id.txt_point_value);
        txtPointValue.setText("Bet Value");
        pointValue = tableInfoDialog.findViewById(R.id.point_value);
        txtInitialDrop = tableInfoDialog.findViewById(R.id.txt_initial_drop);
        txtInitialDrop.setText("Game Type");
        initialDrop = tableInfoDialog.findViewById(R.id.initial_drop);
        txtMiddleDrop = tableInfoDialog.findViewById(R.id.txt_middle_drop);
        txtMiddleDrop.setText("Default Bid");
        middleDrop = tableInfoDialog.findViewById(R.id.middle_drop);

        TextView txt_trump_card = tableInfoDialog.findViewById(R.id.txt_trump_card);
        txt_trump_card.setVisibility(View.VISIBLE);
        ImageView spade_img = tableInfoDialog.findViewById(R.id.spade_img);
        spade_img.setVisibility(View.VISIBLE);

        TextView txt_wrong_show = tableInfoDialog.findViewById(R.id.txt_wrong_show);
        txt_wrong_show.setVisibility(View.GONE);
        wrongShow = tableInfoDialog.findViewById(R.id.wrong_show);
        wrongShow.setVisibility(View.GONE);
        View rj3 = tableInfoDialog.findViewById(R.id.rj3);
        rj3.setVisibility(View.GONE);
        TextView txt_leave_table = tableInfoDialog.findViewById(R.id.txt_leave_table);
        txt_leave_table.setVisibility(View.GONE);
        leaveTablePoint = tableInfoDialog.findViewById(R.id.leave_table);
        leaveTablePoint.setVisibility(View.GONE);
        infoDisConnection = tableInfoDialog.findViewById(R.id.info_disconnection);
        cardsDeck = tableInfoDialog.findViewById(R.id.cards);
        TextView txt_joker = tableInfoDialog.findViewById(R.id.txt_joker);
        txt_joker.setText("Leave table");
        jokerDeck = tableInfoDialog.findViewById(R.id.joker_deck);
        jokerDeck.setText("Entire Bet");
        infoTurnTimer = tableInfoDialog.findViewById(R.id.turn_timer);
        TextView txt_showtimer = tableInfoDialog.findViewById(R.id.txt_showtimer);
        txt_showtimer.setText("Bid Selection");
        showTimer = tableInfoDialog.findViewById(R.id.show_timer);
    }

    private TextView ReleasedBonus, Winnings, Deposit, InstantCash;
    private ConstraintLayout closeWalletinfopopup;

    private void setupWalletInfoDialog() {
        if (walletInfoDialog != null && walletInfoDialog.isShowing()) {
            walletInfoDialog.dismiss();
        }
        walletInfoDialog = new Dialog(this, R.style.Theme_TransparentBuddies);
        walletInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        walletInfoDialog.setContentView(R.layout.dialog_walletinfo);
        walletInfoDialog.setCancelable(true);
        walletInfoDialog.setOwnerActivity(this);

        closeWalletinfopopup = walletInfoDialog.findViewById(R.id.close_infopopup);
        closeWalletinfopopup.setOnClickListener(v -> {
            if (walletInfoDialog.isShowing())
                walletInfoDialog.dismiss();
        });
        InstantCash = walletInfoDialog.findViewById(R.id.InstantCash);
        Deposit = walletInfoDialog.findViewById(R.id.Deposit);
        Winnings = walletInfoDialog.findViewById(R.id.Winnings);
        ReleasedBonus = walletInfoDialog.findViewById(R.id.ReleasedBonus);
    }

    private void releaseMemory() {
        PreferenceManager.isPlayingScreenOpen = false;
        c.isCloseLoaderOnDashBoardOnResume = true;
        try {
            if (notificationManager != null) {
                notificationManager.cancelAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (loader != null) {
                loader.Destroy();
                loader = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (CallBreak_PlayingScreen.handler != null) {
            CallBreak_PlayingScreen.handler.removeCallbacksAndMessages(null);
            CallBreak_PlayingScreen.handler = null;
        }

//        closeBuyChipsDialog();
//        closeFinishGameDialog();
//        closeDropGameDialog();
//        closeConfirmationDialog();
        closeErrorDialog();


        try {
            if (UserTimer != null) {
                UserTimer.cancel();
                UserTimer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (cardHandler != null) {
                cardHandler.removeCallbacksAndMessages(null);
                cardHandler = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*try {
            if (declareTimerLimit != null) {
                declareTimerLimit.cancel();
                declareTimerLimit = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (declareTimerLimitAfterFinish != null) {
                declareTimerLimitAfterFinish.cancel();
                declareTimerLimitAfterFinish = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
                mCountDownTimer = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }


    private int getheight(int val) {

        return Screen_Height * val / 720;
    }

    private int getwidth(int val) {

        return Screen_Width * val / 1280;
    }

    ArrayList<String> utterCards = new ArrayList<>();

    private void redrawCardsWithCurrentDeckCards() {
        if (utterCards.isEmpty()) {
            for (ImageView ivCard : ivMyCards) {
                ivCard.setColorFilter(defaultTint);
//                ivCard.setClickable(true);
            }
            return;
        }
        ArrayList<Item_Card> hukamCards = new ArrayList<>();
        ArrayList<Item_Card> sameCards = new ArrayList<>();
        String s = utterCards.get(0);
        String color = getCardColor(s);

        for (int i = 0; i < card_BottomUser.size(); i++) {
            if (color.equals(card_BottomUser.get(i).getCardColorStr())) {
                sameCards.add(card_BottomUser.get(i));
            }
            if (card_BottomUser.get(i).getCardColor() == c.Kaali) {
                hukamCards.add(card_BottomUser.get(i));
            }
        }

        if (sameCards.size() > 0) {
//            Collections.sort(sameCards, new Comparator<Item_Card>() {
//                @Override
//                public int compare(Item_Card o1, Item_Card o2) {
//                    return o1.compareTo(o2);
//                }
//            });
            for (ImageView ivCard : ivMyCards) {
                if (ivCard.getTag() == null) {
                    return;
                }
                String card = ivCard.getTag().toString();
                Logger.print(TAG, "card " + card);
                int ind = Arrays.asList(cardString).indexOf(card);
                if (ind != -1 && getCardColor(card).equals(sameCards.get(0).getCardColorStr())) {
                    ivCard.setColorFilter(defaultTint);
                    ivCard.setClickable(true);
                } else {
                    ivCard.setColorFilter(blackTints);
                    ivCard.setClickable(false);
                }
            }
        } else {
//            if (hukamCards.size() > 0) {
//                for (int i = 0; i < card_BottomUser.size(); i++) {
//                    if (card_BottomUser.get(i).getCardColor() == c.Kaali) {
//                        ivMyCards[i].setColorFilter(defaultTint);
//                        ivMyCards[i].setClickable(true);
//                    } else {
//                        ivMyCards[i].setColorFilter(blackTints);
//                        ivMyCards[i].setClickable(false);
//                    }
//                }
//            } else {
            for (ImageView ivCard : ivMyCards) {
                ivCard.setColorFilter(defaultTint);
                ivCard.setClickable(true);
            }
//            }
        }
    }


    private int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }
}