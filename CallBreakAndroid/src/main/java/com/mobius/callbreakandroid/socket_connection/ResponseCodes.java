package com.mobius.callbreakandroid.socket_connection;

public class ResponseCodes {

    public static final int StartLoader = 70;
    public static final int FinishLoader = 71;

    public static final int SignupResp = 1003;
    public static final int ProfileResp = 1004;

    public static final int GetTableInfoResp = 1006;
    public static final int JoinTableResp = 1007;
    public static final int NotificationResp = 1008;
    public static final int LeaveTableResp = 1009;
    public static final int CollectBootValueResp = 1010;
    public static final int StartDealingResp = 1011;
    public static final int UserTurnStartedResp = 1012;
    public static final int RoundTimerStartResp = 1013;
    public static final int TurnTimeoutResp = 1014;
    public static final int TurnTakenResp = 1015;
    public static final int SeeMyCardResp = 1016;
    public static final int GetGameConfigResp = 1026;

    public static final int ExiststanceInPrevTableResp = 1060;
    public static final int UpdateChipsResp = 1063;
    public static final int MMNResp = 1070;
    public static final int MaintainaceTimeRemaining = 1071;
    public static final int ThrowCardOnDeckResp = 1086;
    public static final int SwitchTableResp = 3482;
    public static final int ChangeNameResp = 3541;
    public final static int STORE_USER_CARD = 20071;
    public final static int Boot_ValuesResp = 20073;

    public static final int WinnerDeclaredResp = 20075;
    public final static int LeaveWitoutDropChipsCutResp = 20077;
    public final static int DealNumberResp = 30104;
    public final static int ScoreBoardResp = 30106;
//    public final static int OtherUserDeclaredResp = 30108;
    public final static int Finish_Playscreen = 147852;
    public final static int UniqueIdSearchResp = 147853;

    public static final int SocketConnectionResp = 110;
    public static final int NewUserRegisterResp = 101;
    public static final int CheckMobileNumberResp = 107;
    public static final int CheckReferelOrCouponCodeResp = 108;
    public static final int NewUserLoginResp = 109;
    public static final int CheckUserNameResp = 111;
    public static final int OnlineUserCounterResp = 115;
    public static final int JoinTablesResp = 130;
    public static final int LockButtonClickEventResp = 147;
    public static final int CPPRes = 133;
    public static final int SuspendeUserResp = 134;
    public static final int GetPlayingtablecategoryListResp = 127;
    public static final int UserClickFinishButtonResp = 135;
    public static final int UserUpdateRewardPointResp = 136;
    public static final int UserWalletValueResp = 145;

    //callbreak events
    public static final int SelectBidResp = 336;
    public static final int CardMoveResp = 337;
    public static final int BidWinnerResp = 338;
    public static final int RSDResp = 339;
    public static final int GetRoundDetailsResp = 342;
    public static final int FinishCBScoreBoard = 340;
    public static final int CallBreakWinnerDeclaredResp = 341;

    public static final int DisplayCashWithdrawPopupResp = 343;
    public static final int UpdatedUserGameBonusResp = 344;
    public static final int UpdateUserLoyaltyLevelInfoResp = 345;
}
