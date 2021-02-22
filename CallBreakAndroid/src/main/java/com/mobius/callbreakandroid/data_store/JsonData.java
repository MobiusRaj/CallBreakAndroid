package com.mobius.callbreakandroid.data_store;

import com.mobius.callbreakandroid.utility_base.C;
import com.mobius.callbreakandroid.utility_base.Logger;
import com.mobius.callbreakandroid.utility_base.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonData {
    private static final String TAG = "JsonData";

    public JSONObject ChipStore = new JSONObject();
    public JSONArray ChatMessages = new JSONArray();
    public JSONObject DailyBonus = new JSONObject();
    public JSONArray TableList = new JSONArray();
    public JSONArray DealTableList = new JSONArray();
    public JSONArray PoolTableList = new JSONArray();
    public JSONArray BetTableList = new JSONArray();
    public JSONArray QuickModeTableList = new JSONArray();
    public JSONObject PromotionalOffer = new JSONObject();
    public JSONObject OtherPlans = new JSONObject();
    public User_Info userInfo;
    public Table_Info tableInfo;
    public ArrayList<String> price = new ArrayList<String>();
    C c;
    // coin store
    JSONObject CoinsStore = new JSONObject();
    private JSONObject SignupData;

    public JsonData(C c) {

        this.c = c;
    }

    public JSONArray getChatMessages() {
        return ChatMessages;
    }

    public void setChatMessages(JSONArray chatMessages) {
        ChatMessages = chatMessages;
    }

    public JSONArray addChatMessage(JSONArray array, String UserId, String UserName, String Message, String profilePicture) {
        try {
            for (int i = 0; i < array.length(); i++) {
                if (array.getJSONObject(i).getString(Parameters.User_Id).equals(UserId)) {
                    JSONObject data = new JSONObject();
                    data.put(Parameters.User_Name, UserName);
                    data.put(Parameters.Message, Message);

                    array.getJSONObject(i).put("unread", true);
                    array.getJSONObject(i).put(Parameters.ProfilePicture, profilePicture);
                    array.getJSONObject(i).getJSONArray(Parameters.msg).put(data);

                    return array;

                }
            }

            JSONObject newObject = new JSONObject();
            newObject.put(Parameters.User_Id, UserId);

            if (!profilePicture.equals("")) {
                newObject.put(Parameters.ProfilePicture, profilePicture);
            }
            newObject.put("unread", true);

            JSONObject data = new JSONObject();
            data.put(Parameters.User_Name, UserName);
            data.put(Parameters.Message, Message);

            JSONArray newArray = new JSONArray();
            newArray.put(data);
            newObject.put(Parameters.msg, newArray);
            array.put(newObject);

            return array;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return array;
    }


    public Table_Info getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(Table_Info tableInfo) {
        this.tableInfo = tableInfo;

    }

    public JSONObject getSignUpData() {
        return SignupData;
    }

    public void setSignUpData(String data) {
        try {

            SignupData = new JSONObject(data);
            PreferenceManager.set_id(SignupData.getJSONObject(Parameters.data).getString(Parameters._id));
            PreferenceManager.setUserName(SignupData.getJSONObject(Parameters.data).getString(Parameters.User_Name));
            PreferenceManager.setUserLevel(SignupData.getJSONObject(Parameters.data).getJSONObject("userLoyaltyData").getInt("per"));

            PreferenceManager.setUserLevelPoint(SignupData.getJSONObject(Parameters.data).getJSONObject("userLoyaltyData").getInt("currentLevel"));
            PreferenceManager.setUserState(SignupData.getJSONObject(Parameters.data).getString(Parameters.state));

            if (c.isNullOrEmpty(SignupData.getJSONObject(Parameters.data).optString(Parameters.Chips))) {
                c.Chips = 0L;
            } else {
//                c.Chips = Long.parseLong(SignupData.getJSONObject(Parameters.data).optString(Parameters.Chips));
                c.Chips = SignupData.getJSONObject(Parameters.data).optLong(Parameters.Chips);
            }

            c.UserRemainInGameBonus = SignupData.getJSONObject(Parameters.data).optInt("urgb");

            if (c.isNullOrEmpty(SignupData.getJSONObject(Parameters.data).optString(Parameters.Coins))) {
                c.Coins = 0L;
            } else {
                c.Coins = Long.parseLong(SignupData.getJSONObject(Parameters.data).optString(Parameters.Coins));
            }

            if (SignupData.getJSONObject(Parameters.data).has(Parameters.BootValue)) {
                c.initialBootValue = SignupData.getJSONObject(Parameters.data).optLong(Parameters.BootValue);
            } else {
                c.initialBootValue = 10;
            }

            if (SignupData.getJSONObject(Parameters.data).has(Parameters.FreeChip)) {
                c.FreeChip = SignupData.getJSONObject(Parameters.data).optInt(Parameters.FreeChip);
            }

            if (SignupData.getJSONObject(Parameters.data).has(Parameters.ReferrerCode)) {
                c.RFCode = SignupData.getJSONObject(Parameters.data).optString(Parameters.ReferrerCode);
            }

            if (SignupData.getJSONObject(Parameters.data).has(Parameters.rfl)) {
                c.RFLink = SignupData.getJSONObject(Parameters.data).optString(Parameters.rfl);
            }

            c.dscl = SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.counters).optInt("dscl");
            c.dfhl = SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.counters).optInt("dfhl");

//            Logger.print(TAG, "SINGN UP CHIP>>>>>>>>" + Long.parseLong(SignupData.getJSONObject(Parameters.data).optString(Parameters.Chips)));

            c.Level = Integer.parseInt(SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.counters).optString(Parameters.LevelCompleted));

            c.ProgressPercentage = Integer.parseInt(SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.counters).optString(Parameters.ProgressPercentage));

//            c.isVIPUser = Integer.parseInt(SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.flags).optString(Parameters.FlagIsVIPUser));

            User_Info info = new User_Info(SignupData.getJSONObject(Parameters.data));

            userInfo = info;

            if (SignupData.getJSONObject(Parameters.data).has("adFlag")) {
                c.adFlag = SignupData.getJSONObject(Parameters.data).optInt("adFlag");
                Logger.print(TAG, "AD_FLAG SHOW AD OR NOT : " + c.adFlag);
            }

            if (SignupData.getJSONObject(Parameters.data).has("adProb")) {
                c.adProb = SignupData.getJSONObject(Parameters.data).optInt("adProb");
            }

            /*if (SignupData.getJSONObject(Parameters.data).has(Parameters.rejoin)) {*/
            if (SignupData.getJSONObject(Parameters.data).has(Parameters.rejoin)) {
                int rejoinInt = SignupData.getJSONObject(Parameters.data).optInt(Parameters.rejoin);
                c.isRejoin = rejoinInt != 0;
                Logger.print(TAG, "REJOIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIN => " + rejoinInt + " " + c.isRejoin);
            }

            c.whichPlayingIsRejoin = SignupData.getJSONObject(Parameters.data).has(Parameters.Game_Type)
                    ? SignupData.getJSONObject(Parameters.data).getString(Parameters.Game_Type) : "";

            if (SignupData.getJSONObject(Parameters.data).has(Parameters.promp)) {
                PromotionalOffer = SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.promp);
            }

            if (SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.flags).has("LCP")) {
                c.lcp = SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.flags).optInt("LCP");
            }

            if (SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.flags).has("_fti")) {
                c.isShowTutorial = SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.flags).optInt("_fti") == 0;
            }

            String lastLoginDate = SignupData.getJSONObject(Parameters.data).getJSONObject("lasts").getString(Parameters.LastLogin);
            if (PreferenceManager.getLastLogin().length() > 0) {
                String[] date = PreferenceManager.getLastLogin().split("T");
                c.isToShowSpecialOfferPopup = !date[0].contentEquals(lastLoginDate.split("T")[0]);
            } else {
                c.isToShowSpecialOfferPopup = true;
            }
            PreferenceManager.setLastLogin(lastLoginDate);
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    public JSONObject getChipStore() {
        return ChipStore;
    }

    public void setChipStore(JSONObject chipStore) {
        ChipStore = chipStore;
    }

    // coin store
    public JSONObject getCoinsStore() {

        return CoinsStore;

    }

    public void setCoinsStore(JSONObject cns) {
        CoinsStore = cns;
    }

    public void setConfigData(JSONObject jObject1) throws JSONException {


        Logger.print(TAG, "Config.................... : " + jObject1);

        c.MAINTAINANCE_MODE = jObject1.getJSONObject(Parameters.data).getBoolean(Parameters.MAINTAINANCE_MODE);

        c.BIO_GPS_PERMISSION = jObject1.getJSONObject(Parameters.data).optBoolean(Parameters.BIO_GPS_PERMISSION);

        c.DBVersion = jObject1.getJSONObject(Parameters.data).getInt(Parameters.TGDC);

        c.IFBC = jObject1.getJSONObject(Parameters.data).getLong(Parameters.INITIAL_FACEBOOK_CHIPS);

        c.SENDER_ID1 = jObject1.getJSONObject(Parameters.data).getString(Parameters.ANDROID_PROJECT_ID);

        if (jObject1.getJSONObject(Parameters.data).has(Parameters.TERMS_SERVICE)) {
            c.TERMS_SERVICE = jObject1.getJSONObject(Parameters.data).getString(Parameters.TERMS_SERVICE);
        }
        if (jObject1.getJSONObject(Parameters.data).has(Parameters.PRIVACY_POLICY)) {
            c.PRIVACY_POLICY = jObject1.getJSONObject(Parameters.data).getString(Parameters.PRIVACY_POLICY);
        }
        if (jObject1.getJSONObject(Parameters.data).has(Parameters.AND_MOREGAMES_NEW)) {
            c.ANDROID_MOREGAMES = jObject1.getJSONObject(Parameters.data).getString(Parameters.AND_MOREGAMES_NEW);
        }

        c.FORCEFULLYDOWNLOAD = jObject1.getJSONObject(Parameters.data).getBoolean(Parameters.AFDNV);

        c.VERSIONCODE = jObject1.getJSONObject(Parameters.data).getString(Parameters.AV);

        c.WEEKLY_WINNER_REWARD1 = jObject1.getJSONObject(Parameters.data).getInt(Parameters.WW1R);
        c.WEEKLY_WINNER_REWARD2 = jObject1.getJSONObject(Parameters.data).getInt(Parameters.WW2R);
        c.WEEKLY_WINNER_REWARD3 = jObject1.getJSONObject(Parameters.data).getInt(Parameters.WW3R);

        c.INVITE_FRIEND_REWARD = jObject1.getJSONObject(Parameters.data).getLong(Parameters.InviteFriendReward);

        if (c.SEND_CHIPS_COMMISSION1 == 0)
            c.SEND_CHIPS_COMMISSION1 = 10;

        if (c.SEND_CHIPS_COMMISSION2 == 0)
            c.SEND_CHIPS_COMMISSION2 = 25;

        if (c.SEND_CHIPS_COMMISSION3 == 0)
            c.SEND_CHIPS_COMMISSION3 = 50;

        if (c.NO_LIMIT_PERCENTAGE == 0)
            c.NO_LIMIT_PERCENTAGE = 10;

        c.ROUND_START_TIMER = jObject1.getJSONObject(Parameters.data).getInt(Parameters.ROUND_START_TIMER);

        c.DEAL_CARD_ANIMATION_TIME = jObject1.getJSONObject(Parameters.data).getInt(Parameters.DEAL_CARD_ANIMATION_TIME);

        c.COLLECT_BOOT_VALUE_TIME = jObject1.getJSONObject(Parameters.data).getInt(Parameters.COLLECT_BOOT_VALUE_TIME);

        c.SLOW_TABLE_TIMER = jObject1.getJSONObject(Parameters.data).getInt(Parameters.SLOW_TABLE_TIMER);

        c.REMOTE_ASSET_BASE_URL = jObject1.getJSONObject(Parameters.data).getString(Parameters.REMOTE_ASSET_BASE_URL).replace("https", "http");

        c.BASE_URL = jObject1.getJSONObject(Parameters.data).getString(Parameters.BASE_URL);

        c.TFR = jObject1.getJSONObject(Parameters.data).getString(Parameters.TFR);
        c.TPR = jObject1.getJSONObject(Parameters.data).getString(Parameters.TPR);
        c.FLR = jObject1.getJSONObject(Parameters.data).getString(Parameters.FLR);
        c.FPR = jObject1.getJSONObject(Parameters.data).getString(Parameters.FPR);
        c.ARR = jObject1.getJSONObject(Parameters.data).getString(Parameters.ARR);
        c.FBLoginReward = jObject1.getJSONObject(Parameters.data).getLong(Parameters.IFBC);
        c.DailyBonusCollectPercentage = jObject1.getJSONObject(Parameters.data).optInt("DBSP");
        c.WeeklyBonusCollectPercentage = jObject1.getJSONObject(Parameters.data).optInt("WWBEX");

        c.LuckyDrawBonusCollectPercentage = jObject1.getJSONObject(Parameters.data).optInt("LCBEX");

        c.INVITE_FRIEND = jObject1.getJSONObject(Parameters.data).optBoolean("INVITE_FRIEND");
        c.IFR_MSG = jObject1.getJSONObject(Parameters.data).getString("IFR_MSG");

        c.viplevel = jObject1.getJSONObject(Parameters.data).getInt("rlc");
        Logger.print(TAG, "FBFBFBFBFBFBFBFBFBFBFBFBFBF =. " + c.FBLoginReward);
        c.BonusIncSeconds = jObject1.getJSONObject(Parameters.data).getInt(Parameters.BTSEC);
        c.MaxBonusChips = 14400 / c.BonusIncSeconds;

        Logger.print(TAG, "BBBBBBB:" + jObject1.getJSONObject(Parameters.data).getInt(Parameters.BTSEC));

        c.DEFAULT_FIREND_BONUS = jObject1.getJSONObject(Parameters.data).getLong(Parameters.DEFAULT_FRIEND_BONUS);

        c.DEFAULT_ADD_BONUS = jObject1.getJSONObject(Parameters.data).optLong(Parameters.DEFAULT_ADD_BONUS);

        if (c.DEFAULT_ADD_BONUS == 0) {
            c.DEFAULT_ADD_BONUS = 500;
        }

        c.MORE_APP_CAMPAIGN = jObject1.getJSONObject(Parameters.data).getBoolean("MORE_APP_CAMPAIGN");
        c.MORE_APP_REWARD = jObject1.getJSONObject(Parameters.data).getLong("MORE_APP_REWARD");

        c.secondDeclarePenalty = jObject1.getJSONObject(Parameters.data).getLong("SDQ");
        c.thirdDeclarePenalty = jObject1.getJSONObject(Parameters.data).getLong("TDQ");
        c.fourthDeclarePenalty = jObject1.getJSONObject(Parameters.data).getLong("FDQ");

        c.DEFAULT_PER_LEVEL_BONUS = jObject1.getJSONObject(Parameters.data).getLong(Parameters.DEFAULT_PER_LEVEL_BONUS);

        if (jObject1.getJSONObject(Parameters.data).has(Parameters.FREECHIPS)) {
            c.isFreeChips = jObject1.getJSONObject(Parameters.data).getBoolean(Parameters.FREECHIPS);
        }

        if (jObject1.getJSONObject(Parameters.data).has(Parameters.MAGIC_FLAG)) {
            c.isMagicBoxShow = jObject1.getJSONObject(Parameters.data).getBoolean(Parameters.MAGIC_FLAG);
        }

        if (jObject1.getJSONObject(Parameters.data).has(Parameters.MAGIC_DC)) {
            c.MAGIC_DC = jObject1.getJSONObject(Parameters.data).getString(Parameters.MAGIC_DC);
        }


        c.DEFAULT_PER_LEVEL_BONUS = jObject1.getJSONObject(Parameters.data).getLong(Parameters.DEFAULT_PER_LEVEL_BONUS);

        if (jObject1.getJSONObject(Parameters.data).has(Parameters.LEVEL_SHARE_BONUS))
            c.FB_SHARE_BONUS_LEVEL_UP = jObject1.getJSONObject(Parameters.data).getLong(Parameters.LEVEL_SHARE_BONUS);

        c.CountOfInvite = jObject1.getJSONObject(Parameters.data).optInt(Parameters.FlagCountOfInvite);
        Logger.print(TAG, "FFFFFFFFFFFFFFF:" + jObject1.getJSONObject(Parameters.data).optInt(Parameters.FlagCountOfInvite));
        c.CountOfPlayed = jObject1.getJSONObject(Parameters.data).optInt(Parameters.FlagCountOfPlayed);

        if (c.CountOfInvite <= 0) {
            c.CountOfInvite = 250;
        }

        if (c.CountOfPlayed <= 0) {
            c.CountOfPlayed = 100;
        }

        c.LuckyWinnerReward = jObject1.getJSONObject(Parameters.data).optLong(Parameters.LuckyWinnerReward);
        if (jObject1.getJSONObject(Parameters.data).has(Parameters.DealModePotValue))
            c.dealModePotValue = jObject1.getJSONObject(Parameters.data).optLong(Parameters.DealModePotValue);

        c.LuckyWinnerRewardTimes = jObject1.getJSONObject(Parameters.data).optInt("BCCW");
        if (jObject1.getJSONObject(Parameters.data).has("MPODC"))
            c.MAX_DEADWOOD_POINT = jObject1.getJSONObject(Parameters.data).getInt("MPODC");
        c.MAINTENANCE_OFFER_MESSAGE = jObject1.getJSONObject(Parameters.data).getString("INFO_MSG_TEXT_KEY");

        c.IFR = jObject1.getJSONObject(Parameters.data).getLong("IFR");
        if (jObject1.getJSONObject(Parameters.data).has("IFRR"))
            c.IFRR = jObject1.getJSONObject(Parameters.data).getLong("IFRR");
        if (jObject1.getJSONObject(Parameters.data).has("MTIF"))
            c.MTIF = jObject1.getJSONObject(Parameters.data).getLong("MTIF");

        c.WEBSPOF = jObject1.getJSONObject(Parameters.data).getInt("WEBSPOF") == 1;
        c.WBSPURL = jObject1.getJSONObject(Parameters.data).getString("WBSPURL");
        c.WBSPIM = jObject1.getJSONObject(Parameters.data).getString("WBSPIM");
        c.WBSPIM1 = jObject1.getJSONObject(Parameters.data).getString("WBSPIM1");

        c.WEB3XOF = jObject1.getJSONObject(Parameters.data).getInt("WEB3XOF") == 1;
        c.WB3XURL = jObject1.getJSONObject(Parameters.data).getString("WB3XURL");
        c.WB3XIM = jObject1.getJSONObject(Parameters.data).getString("WB3XIM");
        c.WB3XIM1 = jObject1.getJSONObject(Parameters.data).getString("WB3XIM1");

        c.WEBMOREGAME = jObject1.getJSONObject(Parameters.data).getInt("WEBMOREGAME") == 1;
        c.WBMGAND = jObject1.getJSONObject(Parameters.data).getString("WBMGAND");
        c.WBMGIM = jObject1.getJSONObject(Parameters.data).getString("WBMGIM");
        c.WBMGIM1 = jObject1.getJSONObject(Parameters.data).getString("WBMGIM1");

        c.OFSB = jObject1.getJSONObject(Parameters.data).getInt("OFSB") == 1;
        c.BVRS = jObject1.getJSONObject(Parameters.data).getInt("BVRS");
        c.MBVR = jObject1.getJSONObject(Parameters.data).getInt("MBVR");

        if (jObject1.getJSONObject(Parameters.data).has("ISDEALBTN")) {
            c.isDealmodeEnable = jObject1.getJSONObject(Parameters.data).getBoolean("ISDEALBTN");
        }

        if (jObject1.getJSONObject(Parameters.data).has("ISBETBTN")) {
            c.isBetModeEnable = jObject1.getJSONObject(Parameters.data).getBoolean("ISBETBTN");
        }

        if (jObject1.getJSONObject(Parameters.data).has("ISQUICKBTN")) {
            c.isQuickModeEnable = jObject1.getJSONObject(Parameters.data).getBoolean("ISQUICKBTN");
        }

        if (jObject1.getJSONObject(Parameters.data).has("ISPOOLBTN")) {
            c.isPoolModeEnable = jObject1.getJSONObject(Parameters.data).getBoolean("ISPOOLBTN");
        }
    }
}
