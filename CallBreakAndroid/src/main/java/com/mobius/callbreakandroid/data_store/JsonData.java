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
            PreferenceManager.setUserReward(String.valueOf(SignupData.getJSONObject(Parameters.data).getJSONObject("userRewardData").getInt("userRewardPoint")));
            PreferenceManager.setUserLevel(SignupData.getJSONObject(Parameters.data).getJSONObject("userLoyaltyData").getInt("per"));

            PreferenceManager.setUserLevelPoint(SignupData.getJSONObject(Parameters.data).getJSONObject("userLoyaltyData").getInt("currentLevel"));
            Logger.print(TAG, "setSignUpData: ............ point :: " + PreferenceManager.getUserLevelPoint());
            PreferenceManager.setUserEmail(SignupData.getJSONObject(Parameters.data).getString(Parameters.User_Email));
//            PreferenceManager.set_FbId(SignupData.getJSONObject(Parameters.data).getString(Parameters.FB_Id));
            PreferenceManager.set_FB_accessToken(SignupData.getJSONObject(Parameters.data).getString(Parameters.FB_Token));
            PreferenceManager.setUserState(SignupData.getJSONObject(Parameters.data).getString(Parameters.state));
            PreferenceManager.set_UserLoginType(SignupData.getJSONObject(Parameters.data).optString(Parameters.User_LoginType));


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

            if (SignupData.getJSONObject(Parameters.data).has(Parameters.ChallangeButton))
                c.ChallangeOnOffFlag = SignupData.getJSONObject(Parameters.data).optInt(Parameters.ChallangeButton);

            if (SignupData.getJSONObject(Parameters.data).has(Parameters.FreeChip)) {
                c.FreeChip = SignupData.getJSONObject(Parameters.data).optInt(Parameters.FreeChip);
            }

            if (c.ChallangeOnOffFlag == 0) {
                PreferenceManager.setChallengePopup(false);
            } else {
                PreferenceManager.setChallengePopup(true);
            }

            if (SignupData.getJSONObject(Parameters.data).has(Parameters.ReferrerUid)) {
                c.Unique_Id = SignupData.getJSONObject(Parameters.data).optString(Parameters.ReferrerUid);
            }

            if (SignupData.getJSONObject(Parameters.data).has(Parameters.ReferrerCode)) {
                c.RFCode = SignupData.getJSONObject(Parameters.data).optString(Parameters.ReferrerCode);
            }

            if (SignupData.getJSONObject(Parameters.data).has(Parameters.rfl)) {
                c.RFLink = SignupData.getJSONObject(Parameters.data).optString(Parameters.rfl);
            }

            try {
                if (SignupData.getJSONObject(Parameters.data).has(Parameters.IR_OFFER_PLAN)) {
                    c.irOfferPlan = SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.IR_OFFER_PLAN);
                    if (c.irOfferPlan.length() > 0) {
                        c.irOfferWebLink = SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.IR_OFFER_PLAN).optString(Parameters.IR_OFFER_WEB_LINK);
                        c.irOfferDBImage = c.REMOTE_ASSET_BASE_URL + SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.IR_OFFER_PLAN).optString(Parameters.IR_OFFER_DB_IMAGE);
                    } else {
                        c.irOfferPlan = new JSONObject();
                        c.irOfferWebLink = "";
                        c.irOfferDBImage = "";
                    }
                    Logger.print(TAG, "IR_OFFER_PLAN --> " + c.irOfferPlan + "\n" + "IR_OFFER_WEB_LINK --> " + c.irOfferWebLink + "\n" + "IR_OFFER_DB_IMAGE --> " + c.irOfferDBImage);

                }
            } catch (Exception e) {
                c.irOfferPlan = new JSONObject();
                c.irOfferWebLink = "";
                c.irOfferDBImage = "";
                Logger.print(TAG, "EXCEPTION --> IR_OFFER_PLAN --> " + c.irOfferPlan + "\n" + "IR_OFFER_WEB_LINK --> " + c.irOfferWebLink + "\n" + "IR_OFFER_DB_IMAGE --> " + c.irOfferDBImage);
                e.printStackTrace();
            }

            c.dscl = SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.counters).optInt("dscl");
            c.dfhl = SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.counters).optInt("dfhl");

//            Logger.print(TAG, "SINGN UP CHIP>>>>>>>>" + Long.parseLong(SignupData.getJSONObject(Parameters.data).optString(Parameters.Chips)));

            c.Level = Integer.parseInt(SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.counters).optString(Parameters.LevelCompleted));

            c.inviteFriendsCounter = SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.counters).optInt(Parameters.inviteFriendCounter);

            c.inviteFriendsTotalChips = SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.counters).optInt(Parameters.inviteFriendTotalChips);

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

            if (SignupData.getJSONObject(Parameters.data).has(Parameters.DailyBonusFirstTime))
                c.isShowDailyRewardScreen = SignupData.getJSONObject(Parameters.data).optBoolean(Parameters.DailyBonusFirstTime);

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

//            c.practicGameFlage = SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.flags).optInt("sct") == 1;

            if (SignupData.getJSONObject(Parameters.data).has(Parameters.NotificationBonusFlag)) {
                c.isToShowDailyRewardsPopup = SignupData.getJSONObject(Parameters.data).optInt(Parameters.NotificationBonusFlag) == 1;
                if (c.isToShowDailyRewardsPopup) {
                    c.dailyRewardsAmount = SignupData.getJSONObject(Parameters.data).optLong(Parameters.NotificationBonusAmount);
                    c.dailyRewardsMessage = SignupData.getJSONObject(Parameters.data).optString("notimsg");
                }
            }

            if (SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.flags).has("_fti")) {
                c.isShowTutorial = SignupData.getJSONObject(Parameters.data).getJSONObject(Parameters.flags).optInt("_fti") == 0;
            }

            //}
            c.dailyRewardNotification = SignupData.getJSONObject(Parameters.data).optString(Parameters.CreatedDate);

            String lastFBPost = SignupData.getJSONObject(Parameters.data).getJSONObject("lasts").optString(Parameters.LastFBPosted);
            if (lastFBPost.length() > 0) {
                if (PreferenceManager.getlastFacebookPost().contentEquals(lastFBPost)) {
                    c.isFBPostEnable = false;
                } else {
                    PreferenceManager.setlastFacebookPost(lastFBPost);
                    c.isFBPostEnable = true;
                }
            } else {
                c.isFBPostEnable = true;
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

        c.GET_JUSPAY_CLIENT_AUTH_TOKEN = jObject1.getJSONObject(Parameters.data).has("JUS_PAY_KEY") ?
                jObject1.getJSONObject(Parameters.data).getString("JUS_PAY_KEY") : "";

        C.VIDEO_BONUS = jObject1.getJSONObject(Parameters.data).getInt(Parameters.VIDEO_BONUS);
        C.ADSPER = jObject1.getJSONObject(Parameters.data).getInt(Parameters.ADSPER);

        PreferenceManager.APP_ID = jObject1.getJSONObject(Parameters.data).getString(Parameters.FB_APP_ID);

        PreferenceManager.APP_NAMESPACE = jObject1.getJSONObject(Parameters.data).getString(Parameters.FB_APP_NAME);

        c.APP_PAGELINK = jObject1.getJSONObject(Parameters.data).getString(Parameters.FB_APP_PAGELINK);

        if (jObject1.getJSONObject(Parameters.data).has(Parameters.WatchVideoFound))
            c.isWatchVideoAvailable = jObject1.getJSONObject(Parameters.data).getBoolean(Parameters.WatchVideoFound);

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

        if (jObject1.getJSONObject(Parameters.data).has(Parameters.CBRWD))
            c.WATCH_VIDEO_REWARD = jObject1.getJSONObject(Parameters.data).getLong(Parameters.CBRWD);

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

        c.shareButoonFlowType = jObject1.getJSONObject(Parameters.data).optInt(Parameters.shareButoonFlowType, 0);

        c.DEAL_CARD_ANIMATION_TIME = jObject1.getJSONObject(Parameters.data).getInt(Parameters.DEAL_CARD_ANIMATION_TIME);

        c.COLLECT_BOOT_VALUE_TIME = jObject1.getJSONObject(Parameters.data).getInt(Parameters.COLLECT_BOOT_VALUE_TIME);

        c.SLOW_TABLE_TIMER = jObject1.getJSONObject(Parameters.data).getInt(Parameters.SLOW_TABLE_TIMER);

        c.REMOTE_ASSET_BASE_URL = jObject1.getJSONObject(Parameters.data).getString(Parameters.REMOTE_ASSET_BASE_URL).replace("https", "http");

        c.BASE_URL = jObject1.getJSONObject(Parameters.data).getString(Parameters.BASE_URL);

        if (jObject1.getJSONObject(Parameters.data).has("JUS_PAY_RETURN_URL")) {
            PreferenceManager.setUserReturnUrl(jObject1.getJSONObject(Parameters.data).getString("JUS_PAY_RETURN_URL"));
        }

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
        C.SPECIAL_OFFER = jObject1.getJSONObject(Parameters.data).getInt("SPECIAL_OFFER") == 1;
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
        if (jObject1.getJSONObject(Parameters.data).has("SPECIAL_OFFER_DISPLAY")) {
            C.CHIP_STORE = jObject1.getJSONObject(Parameters.data).getJSONObject("SPECIAL_OFFER_DISPLAY").optInt("chipstore") == 1;
            C.COIN_STORE = jObject1.getJSONObject(Parameters.data).getJSONObject("SPECIAL_OFFER_DISPLAY").optInt("coinstore") == 1;
            C.ONGAME_LOAD = jObject1.getJSONObject(Parameters.data).getJSONObject("SPECIAL_OFFER_DISPLAY").optInt("ongameload") == 1;
        }

        if (jObject1.getJSONObject(Parameters.data).has("PRIVAE_TABLE_COST")) {
            C.PRIVAE_TABLE_COST = jObject1.getJSONObject(Parameters.data).optLong("PRIVAE_TABLE_COST");
        }

    }
}
