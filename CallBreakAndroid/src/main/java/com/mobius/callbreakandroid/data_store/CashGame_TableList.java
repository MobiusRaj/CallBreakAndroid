package com.mobius.callbreakandroid.data_store;

import org.json.JSONArray;
import org.json.JSONObject;

public class CashGame_TableList {

    private String id;
    private int activePlayers;
    private int ap;
    private String bv;
    private String catId;
    private String gt;
    private String subType;
    private boolean isPlay;
    private int minEntry;
    private int ms;
    private JSONArray loyaltyLevelArray;
    private String sunType;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setActivePlayers(int activePlayers) {
        this.activePlayers = activePlayers;
    }

    public int getActivePlayers() {
        return this.activePlayers;
    }

    public void setAp(int ap) {
        this.ap = ap;
    }

    public int getAp() {
        return this.ap;
    }

    public void setBv(String bv) {
        this.bv = bv;
    }

    public String getBv() {
        return this.bv;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatId() {
        return this.catId;
    }

    public void setGt(String gt) {
        this.gt = gt;
    }

    public String getGt() {
        return this.gt;
    }

    public void setsubType(String subType) {
        this.subType = subType;
    }

    public String getsubType() {
        return this.subType;
    }

    public void setIsPlay(boolean isPlay) {
        this.isPlay = isPlay;
    }

    public boolean isIsPlay() {
        return this.isPlay;
    }

    public void setMinEntry(int minEntry) {
        this.minEntry = minEntry;
    }

    public int getMinEntry() {
        return this.minEntry;
    }

    public void setMs(int ms) {
        this.ms = ms;
    }

    public int getMs() {
        return this.ms;
    }

    public void setSunType(String sunType) {
        this.sunType = sunType;
    }

    public String getSunType() {
        return this.sunType;
    }

    public JSONArray getLoyaltyLevelArray() {
        return loyaltyLevelArray;
    }

    public void setLoyaltyLevelArray(JSONArray loyaltyLevelArray) {
        this.loyaltyLevelArray = loyaltyLevelArray;
    }

    public CashGame_TableList(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        id = jsonObject.optString("_id");
        activePlayers = jsonObject.optInt("activePlayers");
        ap = jsonObject.optInt("ap");
        bv = jsonObject.optString("bv");
        catId = jsonObject.optString("catId");
        gt = jsonObject.optString("gt");
        subType = jsonObject.optString("subType");
//        isPlay = jsonObject.optBoolean("isPlay");
        minEntry = jsonObject.optInt("minEntry");
        ms = jsonObject.optInt("ms");
        ms = jsonObject.optInt("ms");
        loyaltyLevelArray = jsonObject.optJSONArray("loyaltyLevel");
//        sunType = jsonObject.optString("sunType");
    }

}

