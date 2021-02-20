package com.mobius.callbreakandroid.data_store;

import com.mobius.callbreakandroid.utility_base.C;
import com.mobius.callbreakandroid.utility_base.Logger;
import com.mobius.callbreakandroid.utility_base.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Table_Info {
    private static final String TAG = "Table_Info";
    
    private String GameType, SubType, GameUserType, TableName,
            TableStatus, TableId, CurrentTheme,
            catId, HukamCard;
    
    private int ActivePlayers, FlagIsPrivate,
            Timer,
            pileCounter;
    private double BootValue;
    private long MaxPotValue, PotValue;
    private JSONArray player_info = new JSONArray();
    private JSONArray UnChangedplayer_info = new JSONArray();
    
    private JSONArray player_score1 = new JSONArray();
    
    private JSONArray ThrownCard = new JSONArray();
    private JSONObject OriginalJSON = new JSONObject();
    
    private int RoundPoint = 0;
    
    public Table_Info(JSONObject data) {
        
        Logger.print(TAG, "TABLE_INFO DATA --> " + data.toString());
        try {
            setOriginalJSON(data);
            setBootValue(data.optDouble(Parameters.BootValue));
            setCatId(data.optString("catId"));
            setCurrentTheme(data.optString(Parameters.CurrentTheme));
            setFlagIsPrivate(data.optInt(Parameters.FlagIsPrivate));
            setGameType(data.optString(Parameters.Game_Type));
            setSubType(data.optString(Parameters.Sub_Type));
            setGameUserType(data.optString(Parameters.GameUserType));
            setMaxPotValue(data.optLong(Parameters.MaxPotValue));
            setPotValue(data.optLong(Parameters.PotValue));
            setTableName(data.optString(Parameters.TableName));
            setTableStatus(data.optString(Parameters.TableStatus));
            setTimer(data.getInt(Parameters.Timer));
            setTableId(data.optString(Parameters._id));
            C.getInstance().TableId = getTableId();
            setPlayer_info(data.optJSONArray(Parameters.PlayersInfo));
            setActivePlayers(data.optInt(Parameters.ActivePlayers));
            setUnChangedplayer_info(data.optJSONArray(Parameters.PlayersInfo));
            setPileCounter(data.optInt(Parameters.pilecounter));
            setThrownCard(data.optJSONArray(Parameters.thc));
            setRoundPoint(data.optInt(Parameters.roundpnt));
            setHukamCard(data.getJSONArray("hukam").optString(0));
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }
    
    public String getHukamCard() {
        return HukamCard;
    }
    
    public void setHukamCard(String hukamCard) {
        this.HukamCard = hukamCard;
    }
    
    public int getPileCounter() {
        return pileCounter;
    }
    
    public void setPileCounter(int pileCounter) {
        this.pileCounter = pileCounter;
    }
    
    public String getCatId() {
        return catId;
    }
    
    public void setCatId(String catId) {
        this.catId = catId;
    }
    
    public JSONObject getOriginalJSON() {
        return OriginalJSON;
    }
    
    public void setOriginalJSON(JSONObject originalJSON) {
        OriginalJSON = originalJSON;
    }
    
    
    public boolean hasJoinedTable() throws JSONException {
        for (int i = 0; i < getPlayer_info().length(); i++) {
            if (getPlayer_info().getJSONObject(i).length() > 0
                    && getPlayer_info().getJSONObject(i)
                    .getJSONObject(Parameters.User_Info)
                    .getString(Parameters._id)
                    .equals(PreferenceManager.get_id())) {
                return true;
            }
        }
        return false;
    }
    
    public int UserSeatIndex(String UserId) throws JSONException {
        for (int i = 0; i < getPlayer_info().length(); i++) {
            if (getPlayer_info().getJSONObject(i).length() > 0
                    && getPlayer_info().getJSONObject(i)
                    .getJSONObject(Parameters.User_Info)
                    .getString(Parameters._id).equals(UserId)) {
                return getPlayer_info().getJSONObject(i).getInt(
                        Parameters.SeatIndex);
            }
        }
        return -1;
    }
    
    
    public int GetSeatIndex(int value) throws JSONException {
        if (getPlayer_info().getJSONObject(value).length() > 0) {
            return getPlayer_info().getJSONObject(value).getInt(
                    Parameters.SeatIndex);
        }
        return -1;
    }
    
    public boolean isSeatEmpty(int SeatIndex) throws JSONException {
        return getPlayer_info().getJSONObject(SeatIndex).length() <= 0;
    }
    
    public String getCurrentTheme() {
        if (CurrentTheme == null || CurrentTheme.length() <= 0) {
            return "0";
        }
        return CurrentTheme;
    }
    
    public void setCurrentTheme(String currentTheme) {
        CurrentTheme = currentTheme;
    }
    
    public int getTimer() {
        return Timer;
    }
    
    public void setTimer(int timer) {
        Timer = timer;
    }
    
    public JSONArray getUnChangedplayer_info() {
        return UnChangedplayer_info;
    }
    
    public void setUnChangedplayer_info(JSONArray unChangedplayer_info) {
        UnChangedplayer_info = unChangedplayer_info;
    }
    
    public String getTableId() {
        return TableId;
    }
    
    public void setTableId(String tableId) {
        TableId = tableId;
    }
    
    
    public String getGameType() {
        if (GameType == null) {
            return "";
        }
        return GameType;
    }
    
    public void setGameType(String gameType) {
        GameType = gameType;
    }
    
   public String getSubType() {
        if (SubType == null) {
            return "";
        }
        return SubType;
    }

    public void setSubType(String subType) {
        SubType = subType;
    }

    public String getGameUserType() {
        return GameUserType;
    }
    
    public void setGameUserType(String gameUserType) {
        GameUserType = gameUserType;
    }
    
    public String getTableName() {
        return TableName;
    }
    
    public void setTableName(String tableName) {
        TableName = tableName;
    }
    
    public String getTableStatus() {
        return TableStatus;
    }
    
    public void setTableStatus(String tableStatus) {
        TableStatus = tableStatus;
    }
    
    
    public int getActivePlayers() {
        return ActivePlayers;
    }
    
    public void setActivePlayers(int activePlayers) {
        ActivePlayers = activePlayers;
    }
    
    public double getBootValue() {
        return BootValue;
    }
    
    public void setBootValue(double bootValue) {
        BootValue = bootValue;
    }
    
    public int getRoundPoint() {
        return RoundPoint;
    }
    
    public void setRoundPoint(int roundPoint) {
        RoundPoint = roundPoint;
    }
    
    public int getFlagIsPrivate() {
        return FlagIsPrivate;
    }
    
    public void setFlagIsPrivate(int flagIsPrivate) {
        FlagIsPrivate = flagIsPrivate;
    }
    
    public long getMaxPotValue() {
        return MaxPotValue;
    }
    
    public void setMaxPotValue(long maxPotValue) {
        MaxPotValue = maxPotValue;
    }
    
    public long getPotValue() {
        return PotValue;
    }
    
    public void setPotValue(long potValue) {
        PotValue = potValue;
    }
    
    public JSONArray getPlayer_info() {
        return player_info;
    }
    
    public void setPlayer_info(JSONArray player_info) {
        this.player_info = player_info;
    }
    
    public JSONArray getThrownCard() {
        return ThrownCard;
    }
    
    public void setThrownCard(JSONArray thrownCard) {
        ThrownCard = thrownCard;
    }
    
    @Override
    public String toString() {
        return "Table_Info{" +
                "GameType='" + GameType + '\'' +
                ", GameUserType='" + GameUserType + '\'' +
                ", TableName='" + TableName + '\'' +
                ", TableStatus='" + TableStatus + '\'' +
                ", TableId='" + TableId + '\'' +
                ", CurrentTheme='" + CurrentTheme + '\'' +
                ", catId='" + catId + '\'' +
                ", HukamCard='" + HukamCard + '\'' +
                ", ActivePlayers=" + ActivePlayers +
                ", BootValue=" + BootValue +
                ", FlagIsPrivate=" + FlagIsPrivate +
                ", Timer=" + Timer +
                ", pileCounter=" + pileCounter +
                ", MaxPotValue=" + MaxPotValue +
                ", PotValue=" + PotValue +
                ", player_info=" + player_info +
                ", UnChangedplayer_info=" + UnChangedplayer_info +
                ", player_score1=" + player_score1 +
                ", ThrownCard=" + ThrownCard +
                ", OriginalJSON=" + OriginalJSON +
                ", RoundPoint=" + RoundPoint +
                '}';
    }
}
