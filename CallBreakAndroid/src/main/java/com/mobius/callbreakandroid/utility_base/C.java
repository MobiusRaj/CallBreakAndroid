package com.mobius.callbreakandroid.utility_base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mobius.callbreakandroid.data_store.JsonData;
import com.mobius.callbreakandroid.socket_connection.AES;
import com.mobius.callbreakandroid.socket_connection.ConnectionManager;

import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class C {

    private static final String TAG = "C";
    // ---------------------------------
    public static boolean isMaintenanaceScreenOpen = false;
    private static C instance;
    public final ConnectionManager conn;
    public final JsonData jsonData;
    public final String key = "DFK8s58uWFCF4Vs8NCrgTxfMLwjL9WUy";
    public final byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    public final NumberFormat formatter = new DecimalFormat(
            "###,###,###,###,###,###,###,###,###,###,###,###,###");

    private final JSONObject JsonResultCard;
    public final int Falli = 0;
    public final int Chirkut = 1;
    public final int Kaali = 2;
    public final int Laal = 3;
    public final int Joker = 4;
    public int viplevel;
    public String IFR_MSG = "";

    // Sign up start
    public String totalActivUser = "";
    public boolean INVITE_FRIEND = false;
    public boolean isToShowSpecialOfferPopup = false;
    public long collectandCollectShareBonus = 0;
    public int MAX_DEADWOOD_POINT = 80;
    public int DailyBonusCollectPercentage;
    public int WeeklyBonusCollectPercentage;
    public long secondDeclarePenalty = 0, thirdDeclarePenalty = 0, fourthDeclarePenalty = 0;
    public int LuckyDrawBonusCollectPercentage;

    public int dscl, dfhl;

    public long Coins = 0L;
    public int adFlag;
    public int adProb;
    public long FBLoginReward = 0L;
    public String specialOfferData;
    public boolean isShowReconnectTextOnLoader = false;
    public boolean isUserPressedExitGame = false;
    public boolean showNochipsPopup = false;
    public boolean isShowTutorial = false;
    public boolean isCloseLoaderOnDashBoardOnResume = false;

    public int lcp = 0;
    public Typeface typeface;
    public int connectioncnt = 0;
    public int height, width;
    public String RFCode = "ABCDEFG01";
    public String RFLink = "";
    public String tbId = "";
    public String catId = "";
    public boolean practicGameFlage = false;

    public ArrayList<HashMap<String, String>> entryData = new ArrayList<>();
    public double initialBootValue = 10L;

    public boolean isFinishGlobalLoader = false;
    public boolean isJoinedPreviousTable = false;
    public boolean IsmoreGameopen = false;
    public boolean exitToLobby = false;
    public boolean isSwitchingTable = false;
    public boolean isRejoin = false;
    public String whichPlayingIsRejoin;
    public int connectWhenDisconnect = 0;
    public boolean isAcceptNotification = false;
    public boolean isDealmodeEnable = true, isBetModeEnable = true, isQuickModeEnable = true, isPoolModeEnable = false;
    public boolean DisplayWithdrawPopup = false;
    public String DisplayWithdrawPopupMsg = "";


    public boolean MORE_APP_CAMPAIGN;
    public long MORE_APP_REWARD;
    public long FreeChip = 0;
    public boolean isFreeChips = false;
    public boolean isMagicBoxShow = false;
    public String MAGIC_DC = "";
    public String ip = "";
    public String SENDER_ID1 = "317236663728";
    public boolean isErrorPopup = false;
    public boolean showBootValueScreen = false;

    public String DeviceType = "android";
    public long IFBC = 50000;
    public String CurrentVersion = "1.0";
    public String OSVersion = "4.2";
    public String ProfilePicture;
    public int RefernceInvited;
    public long Chips = 0L;
    public int UserRemainInGameBonus = 0;

    public int Level = 0;
    public int ProgressPercentage = 0;
    public String MAINTENANCE_OFFER_MESSAGE = "";
    public String TableId = "";
    public String TERMS_SERVICE = "";
    public String PRIVACY_POLICY = "";
    public String ANDROID_MOREGAMES = "";
    public long MaxBonusChips = 4800;
    public boolean MAINTAINANCE_MODE;
    public boolean BIO_GPS_PERMISSION;
    public String REMOTE_ASSET_BASE_URL;
    public String BASE_URL;
    public int SEND_CHIPS_COMMISSION1;
    public int SEND_CHIPS_COMMISSION2;
    public int SEND_CHIPS_COMMISSION3;
    public int WEEKLY_WINNER_REWARD1;
    public int WEEKLY_WINNER_REWARD2;
    public int WEEKLY_WINNER_REWARD3;
    public int DBVersion;
    public long INVITE_FRIEND_REWARD;
    public int NO_LIMIT_PERCENTAGE;
    public int ROUND_START_TIMER;
    public int DEAL_CARD_ANIMATION_TIME;
    public int COLLECT_BOOT_VALUE_TIME;
    public int SLOW_TABLE_TIMER;
    public long DEFAULT_FIREND_BONUS;
    public long DEFAULT_PER_LEVEL_BONUS;
    // --------------------------------
    public long FB_SHARE_BONUS_LEVEL_UP;
    public long DEFAULT_ADD_BONUS;
    public String VERSIONCODE;
    public boolean FORCEFULLYDOWNLOAD = false;
    public String TFR;
    public String TPR;
    public String FLR;
    public String FPR;
    public String ARR;
    public boolean isThrownOutByServer = false;
    public String NetworkType = "2G";
    public long IFR = 0L;
    public long IFRR = 0L;
    public long MTIF = 0L;

    public int BonusIncSeconds = 0;
    public int CountOfInvite = 250;
    public int CountOfPlayed = 100;
    public long LuckyWinnerReward = 1000000;
    public long dealModePotValue = 20000;
    public int LuckyWinnerRewardTimes = 5;
    public int Bootval = 0;
    public int BVRS, MBVR;
    public boolean WEBSPOF;
    public String WBSPURL, WBSPIM, WBSPIM1;
    public boolean WEB3XOF;
    public String WB3XURL, WB3XIM, WB3XIM1;
    public boolean WEBMOREGAME;
    public String WBMGAND, WBMGIM, WBMGIM1;
    public boolean OFSB = false;
    public int Orl_Height, Orl_Width;
    private boolean lowVersion;

    private C() {
        conn = new ConnectionManager(this);
        jsonData = new JsonData(this);
        JsonResultCard = new JSONObject();
        lowVersion = false;
    }

    private static void initInstance() {
        if (instance == null) {
            instance = new C();
        }
    }

    public static C getInstance() {
        if (instance == null) {
            initInstance();
        }
        return instance;
    }

    private static double RoundTo2Decimals(double amount) {
        DecimalFormat df2 = new DecimalFormat("###.##");
        return Double.valueOf(df2.format(amount));
    }

    private static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = null;
        if (windowManager != null) {
            display = windowManager.getDefaultDisplay();
        }
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchMethodException e) {
            }
        }
        return size;
    }

    // ---------------------------------------------- GAME
    // -----------------------------------------------

    public String getNumberFormatedValue(double val) {
        String returnVal;

        if (val == 0) {
            returnVal = "0";
        } else if (val < 100000) {
            returnVal = formatter.format(val);
        } else {
            returnVal = numDifferentiation(val);
        }
        return returnVal;
    }

    public int getCardSuit(String value) {

        if (value.equalsIgnoreCase("f")) {
            return Falli;
        } else if (value.equalsIgnoreCase("l")) {
            return Laal;
        } else if (value.equalsIgnoreCase("c")) {
            return Chirkut;
        } else if (value.equalsIgnoreCase("k")) {
            return Kaali;
        } else {
            return Joker;
        }

    }

    public String decrypt(String data) {
        String response = data;
        byte[] keyBytes;
        byte[] cipherData;
        try {
            keyBytes = key.getBytes("UTF-8");
            cipherData = AES.decrypt(ivBytes, keyBytes, Base64.decode(data.getBytes("UTF-8"), Base64.NO_WRAP));
            response = new String(cipherData, "UTF-8");
        } catch (Exception e) {

        }
        return response;
    }

    public String numDifferentiation(double val) {
        try {

            String returnVal = "";

            double d;
            String iteration;

            if (val >= 1000000000000L) {
                d = (double) val / 1000000000000L;
                iteration = "T";
            } else if (val >= 1000000000) {
                d = (double) val / 1000000000;
                iteration = "B";
            } else if (val >= 10000000) {
                d = (double) val / 10000000;
                iteration = "Cr";
            } else if (val >= 100000) {
                d = (double) val / 100000;
                iteration = "L";
            } else if (val >= 1000) {
                d = (double) val / 1000;
                iteration = "K";
            } else {
                return String.valueOf(val);
            }

            boolean isRound = (d * 10) % 10 == 0;
            if (isRound) {
                String numberD[] = String.valueOf(d).split(Pattern.quote("."));
                if (Integer.valueOf(numberD[1]) > 0)
                    returnVal = d + " " + iteration;
                else
                    returnVal = ((long) d) + " " + iteration;
            } else {
                double dd = RoundTo2Decimals(d);
                String numberD[] = String.valueOf(dd).split(Pattern.quote("."));
                if (Integer.valueOf(numberD[1]) > 0)
                    returnVal = dd + " " + iteration;
                else
                    returnVal = ((long) dd) + " " + iteration;
            }

            return returnVal;

        } catch (Exception e) {
            Log.e("Numdiff", "numDifferentiation == EXP == " + e);
            e.printStackTrace();
            return String.valueOf(val);
        }
    }

    public String getTimeStamp(long millis) {
        Date date = new Date(millis);
        DateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(date);
    }

    public void CalculateDisplaySize(Context context) {
        int height2;
        Point Display = getRealScreenSize(context);

        width = Display.x;
        height = Display.y;
        Orl_Width = Display.x;
        Orl_Height = Display.y;

        Logger.print(TAG, "<<<<<<<<<<<< CalculateDisplaySize 11 " + width + "  >>>> h >> " + height);

        if (height > width) {
            height = Display.x;
            width = Display.y;
            Orl_Height = Display.x;
            Orl_Width = Display.y;
        }

        double ratio = (double) 9 / (double) 16;
        //==== Set New height acording ratio of  16:9
        height2 = (int) (ratio * width);
        if (height2 > height) {
            ratio = (double) 16 / (double) 9;
            //==== Set New height acording ratio of  16:9
            width = (int) (ratio * height);
//            height2 = height;
        } else {
            height = height2;
        }

        Logger.print(TAG, "<<<<<<<<<<<< CalculateDisplaySize " + width + "  >>>> h >> " + height);
    }

    public void hideNavigationToDialog(Context context, Dialog dialog) {
        if (dialog.getWindow() != null) {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
        dialog.getWindow().getDecorView().setSystemUiVisibility(((Activity) context).getWindow().getDecorView().getSystemUiVisibility());
        if (!((Activity) context).isFinishing()) {
            dialog.show();
        }
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public String userCurrentLevelName(int level) {
        if (level == 1)
            return "BRONZE";
        else if (level == 2)
            return "SILVER";
        else if (level == 3)
            return "GOLD";
        else if (level == 4)
            return "PLATINUM";
        else if (level == 5)
            return "VIP";
        else if (level == 6)
            return "VIP STAR";
        else if (level == 7)
            return "VIP ELITE";
        else
            return "NA";
    }

    public boolean monyNoAllowRegion(String region) {
        return region.equalsIgnoreCase("Assam") || region.equalsIgnoreCase("Odisha") ||
                /*region.equalsIgnoreCase("Sikkim") || */region.equalsIgnoreCase("Telangana")
                || region.equalsIgnoreCase("Kerala") || region.equalsIgnoreCase("Andhra Pradesh")
                || region.equalsIgnoreCase("Tamil Nadu");
    }

    public boolean isAppInstalled(Context context, String targetPackage) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        try {
            pm.getPackageInfo(targetPackage,
                    PackageManager.GET_META_DATA);
            ApplicationInfo ai = pm.getApplicationInfo(targetPackage, 0);
            return ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
