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
    public final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String TAG = "C";
    public static long PRIVAE_TABLE_COST = 0;
    public static boolean SPECIAL_OFFER = false;
    public static boolean CHIP_STORE = false;
    public static boolean COIN_STORE = false;

    // SET THIS FLAG false, DO NOT WANT TO SHOW OFFER ON CLOSING OF CHIPS STORE
    public static boolean SHOW_CHIP_STORE_OFFER = true;

    // SET THIS FLAG false, DO NOT WANT TO SHOW OFFER ON CLOSING OF COIN STORE
    public static boolean SHOW_COIN_STORE_OFFER = true;

    public static boolean ONGAME_LOAD = false;
    public static int VIDEO_BONUS = 0;
    public static int ADSPER = 100;
    public static List<Integer> PRIVATE_BOOT_VALUE = new ArrayList<>();
    public static String MoreGameData;
    public static boolean gettableinfo = false;
    public static String PrvJoin;
    public static boolean PrvJoinvalue = false;
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
    //public final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmqluhzEtvSeHdf/m44TmmOpX800aC4CoDuXMwvJyjXXlQdgWz/BUSQ01UJ/iYv7MHGUmev8HTO0V81OMh5bFwxiDCmTBJbozBZ8Kb3f7hnqyxFsIJXH4NVSMN1u36APXSw74rwwzFEcsonXmgGj3R8QWfiuc5SrshKjH4cUz0AFxX/mWZckCCqw0NCXpGDLaD7kWo2qY7a6owHLThHkQEcSxKLBZqUnBkbDVY4xKy44uQIlT2/zrjprimNxeR4sr6omZvsD3Vq/b690GtQe66CIuVhSyUZDqbYCRDbo+Oxe2uGoxbMpQ8rMeJQcvx0c44iKWtZfzJ2enHGlnVoGv0QIDAQAB";
    public final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhnYPS3Ljd4OwkRhmwVa5rySZGLJppV8h4F5TcGKMzzT+KO7ZD+LuX/FKU9GxI/ABWX5nRBE5+cHX0f2Tgp/6+IynDL05qAYiuH9laMsMiHQkkR/c9fBgqQAmwD6HwFE7F5eb5REFMqtXJ+Lc3WlRj7HHVLm6ZX85hz0KOUAFiPcMY80YX7y4ZhyoRoKtemA2buDqFNXBje02uhHiBMDmwJyIv1mFgw2+HKMz4sV7gRc38+7OeJJ0Y2k+JtfqfYntK4JqtSyev0BSwEQVQgRSNl0Qn+xzqyXxHlmy6Z26FVZ+sI4PkH+YtWLHiOuR8zFUxLLMoa0Y2vRZFnVytBviCwIDAQAB";
    public final int Falli = 0;
    public final int Chirkut = 1;
    public final int Kaali = 2;
    public final int Laal = 3;
    public final int Joker = 4;
    private final JSONObject JsonResultCard;
    public ImageView bannerImg;
    public boolean isFBPostEnable = true;
    public boolean isDealMode = false;
    public boolean isBetMode = false;
    public boolean isQuickMode = false;
    public boolean isPoolMode = false;
    public boolean is101PoolMode = false;
    public boolean isPointsRummy = false;
    public boolean isTwoPlayerTableOrNot = false;
    public int remainingTimeForWinScreen = 0;
    public boolean openPromoScreenInOnResume = false;
    public boolean showmoreGameDialog = false;
    public boolean isnotshowpop, showGiftBounsflag, showGiftMsgflag;
    public String dailyRewardNotification;
    public boolean isDealModeWinnerAvailable = false;
    public int[] destChipsLoc = new int[2];
    public int viplevel;
    public String IFR_MSG = "";

    // Sign up start
    public String totalActivUser = "";
    public String userCupenCode = "";
    public boolean PassDataToDashboard = false;
    public JSONObject maintenanceObject = new JSONObject();
    public JSONObject jObject = new JSONObject();
    public String[] country;
    public String PanCardPicture;
    public JSONObject StoreContact;

    public boolean INVITE_FRIEND = false;

    public boolean isToShowSpecialOfferPopup = false;
    public boolean isVideoAvailibility = false;


    public boolean ISRATE_DATA_IAR = false;
    public String ISRATE_DATA_IAR_TYPE = "";

    public String giftcode = "";
    public String gift_msg = "";
    public long PromotionalBonus;
    public long collectandCollectShareBonus = 0;
    public int MAX_DEADWOOD_POINT = 80;
    public int DailyBonusCollectPercentage;
    public int WeeklyBonusCollectPercentage;
    public long secondDeclarePenalty = 0, thirdDeclarePenalty = 0, fourthDeclarePenalty = 0;
    public int LuckyDrawBonusCollectPercentage;

    public boolean isToShowDailyRewardsPopup = false;
    public long dailyRewardsAmount = 0;
    public String dailyRewardsMessage = "";


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


    public String GTIdataForRejoin = null;
    public int lcp = 0;
    public Typeface typeface;
    public int connectioncnt = 0;
    public int height, width;
    public String RFCode = "ABCDEFG01";
    public String Unique_Id = "ABCDEFG01";
    public String RFLink = "";
    public String tbId = "";
    public String catId = "";
    public boolean practicGameFlage = false;


    public long WATCH_VIDEO_REWARD = 0L;
    public ArrayList<HashMap<String, String>> entryData = new ArrayList<>();
    public double initialBootValue = 10L;
    public String initialCategoryId = "";
    public int ChallangeOnOffFlag = 0;
    public boolean isShowDailyRewardScreen = true;
    public boolean isWatchVideoAvailable;

    public String TwoDaysBack = "";
    public boolean isFinishGlobalLoader = false;
    public boolean isShowLevelUpScreen = false;
    public long levelupReward = 0L;
    public boolean isShowVipUnlockScreen = false;
    public boolean isJoinedPreviousTable = false;
    public boolean IsmoreGameopen = false;
    public boolean exitToLobby = false;
    public boolean isSwitchingTable = false;
//    public boolean isNoSpaceInTable = false;
//    public String noSpaceInTableMsg = "";
    public int ERROR_CODE;
    public String tableType = "";
    public boolean isRejoin = false;
    public String whichPlayingIsRejoin;
    public int connectWhenDisconnect = 0;
    public boolean isAcceptNotification = false;
    public boolean isDealmodeEnable = true, isBetModeEnable = true, isQuickModeEnable = true, isPoolModeEnable = false;
    public ArrayList<String> packagesname_get = new ArrayList<>();
    public String VerifyAppData;
    public boolean IsLoseScreenopen = false;
    public boolean IsDealsopen = false;
    public boolean DisplayWithdrawPopup = false;
    public String DisplayWithdrawPopupMsg = "";


    public boolean MORE_APP_CAMPAIGN;
    public boolean IsmoreGameopenShow = false;
    public JSONObject GetGameStatusData = new JSONObject();
    public long MORE_APP_REWARD;
    public long FreeChip = 0;
    public boolean Special_moreGameopen = false;
    public boolean open_level_ip = false;
    public boolean isFreeChips = false;
    public boolean isMagicBoxShow = false;
    public String MAGIC_DC = "";
    public String ip = "";
    // mini games and coinstore
    public NumberFormat formatter1 = new DecimalFormat(
            "##,##,##,##,##,##,##,##,##,##,##,##,###");
    public String Resolution = "1024x768";
    public String SENDER_ID1 = "317236663728";
    public String ReferrerCode = "";
    public boolean isDailyBonusWaiting = false;
    public boolean isErrorPopup = false;
    public boolean showBootValueScreen = false;
    public Activity currentActivity;
    public boolean irDBButtonShown = false;
    public JSONObject irOfferPlan;
    public String irOfferWebLink = "";
    public String irOfferDBImage = "";
    public String DeviceType = "android";
    public long IFBC = 50000;
    public String SignUpBanner = "";
    public boolean updateAvailable = false;
    public String updateMsg = "";
    public String updateURL = "";
    public String updateVersion = "";
    public String CurrentVersion = "1.0";
    public String OSVersion = "4.2";
    public String ProfilePicture;
    public int RefernceInvited;
    public int isVIPUser = 0;
    public long Chips = 0L;
    public int UserRemainInGameBonus = 0;

    public String GET_JUSPAY_CLIENT_AUTH_TOKEN = "";

    public long CrownInfoChips = 0L;
    public int Level = 0;
    public int ProgressPercentage = 0;
    public int inviteFriendsCounter = 0;
    public int inviteFriendsTotalChips = 0;
    public int shareButoonFlowType = 0;
    public String MAINTENANCE_OFFER_MESSAGE = "";
    public int MessageCount = 0;
    public String TableId = "";
    public String TERMS_SERVICE = "";
    public String PRIVACY_POLICY = "";
    public String ANDROID_MOREGAMES = "";
    public long MaxBonusChips = 4800;
    public String APP_PAGELINK;
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
    public boolean SHOW_ONLINE_PLAYER_COUNTER = true;
    public String TFR;
    public String TPR;
    public String FLR;
    public String FPR;
    public String ARR;
    public boolean isSpecialOfferShown = true;
    public boolean isThrownOutByServer = false;
    public boolean isThrownOutBySerevrQMode = false;
    public boolean ClosePreviousTablePopup = false;
    public boolean isWithdrawSuccess = false;
    public String NetworkType = "2G";
    public long IFR = 0L;
    public long IFRR = 0L;
    public long MTIF = 0L;
    public String[] Texts;
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
    public int lastSelectedPosition = -1;
    public String imageUpload = "upload";
    public String panUpload = "panCardUpload";
    private boolean lowVersion;

    public String cityName;
    public String stateName;
    public String countryName;
    public String gpsLattitude;
    public String gpsLongitude;
    //    public JSONArray userApplications = new JSONArray();
    public ArrayList<String> userApplications = new ArrayList<String>();


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

    public String getNumberFormatedValue1(long val) {
        String returnVal;

        if (val == 0) {
            returnVal = "0";
        } else if (val <= 100000) {
            returnVal = formatter.format(val);
        } else {
            returnVal = numDifferentiation1(val);
        }
        return returnVal;
    }

    public String GetShortName(String name) {
        String r;
        String rr[] = name.split("\\s+");
        r = rr[0];
        if (rr.length >= 2) {
            r = rr[0];
        }
        return r;
    }

    public long getTimeinMillis(String timeStamp) {
        Date date = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = sdf.parse(timeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public String getDurationString(int seconds, boolean gift) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        if (hours == 0 && gift) {
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

    private String numDifferentiation1(long val) {
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

//        Logger.print(TAG, "<<<<<<<<<<<< CalculateDisplaySize 11 NAVI POINT "+getRealScreenSize(context));
//        width = width + getNavigationBarSize(context);
//        int r = GCD(width, height);
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


    /*public int userCurrentLevelBackground(int level) {
        if (level == 1)
            return R.drawable.icn_bronze;
        else if (level == 2)
            return R.drawable.icn_silver;
        else if (level == 3)
            return R.drawable.icn_gold;
        else if (level == 4)
            return R.drawable.icn_platinum;
        else if (level == 5)
            return R.drawable.icn_vip;
        else if (level == 6)
            return R.drawable.icn_vipstar;
        else if (level == 7)
            return R.drawable.icn_vipelite;
        else
            return R.drawable.icn_defult_lel;
    }*/

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
