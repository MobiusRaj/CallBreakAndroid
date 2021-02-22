package com.mobius.callbreakandroid.data_store;

import com.mobius.callbreakandroid.utility_base.C;
import com.mobius.callbreakandroid.utility_base.Logger;
import com.mobius.callbreakandroid.utility_base.Parameters;

import org.json.JSONObject;

public class User_Info {
    private static final String TAG = "User_Info";
    
    String UserName, _id, ProfilePicture;
    
    long Chips;
    boolean FlagFacebookLike, FlagIsRated;
    
    C c = C.getInstance();
    
    public User_Info(JSONObject data) {
        
        try {
            Logger.print(TAG, "c.ProfilePicture => " + c.ProfilePicture);
            setProfilePicture(data.getString(Parameters.ProfilePicture));
            c.ProfilePicture = getProfilePicture();
            setUserName(data.getString(Parameters.User_Name));
            setChips(data.getLong(Parameters.Chips));
            c.RefernceInvited = data.getJSONObject(Parameters.counters).optInt(Parameters.rifc);
//            setFlagFacebookLike((data.getJSONObject(Parameters.flags).getString(Parameters.FlagFacebookLike).equals("1")));
            setFlagIsRated((data.getJSONObject(Parameters.flags).getString(Parameters.FlagRate).equals("1")));
            set_id(data.getString(Parameters._id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean isFlagIsRated() {
        return FlagIsRated;
    }
    
    public void setFlagIsRated(boolean flagIsRated) {
        FlagIsRated = flagIsRated;
    }
    
    
    public String getProfilePicture() {
        return ProfilePicture;
    }
    
    public void setProfilePicture(String profilePicture) {
        ProfilePicture = profilePicture;
    }
    
    public String getUserName() {
        return UserName;
    }
    
    public void setUserName(String userName) {
        UserName = userName;
    }
    
    public String get_id() {
        return _id;
    }
    
    public void set_id(String _id) {
        this._id = _id;
    }
    
    public long getChips() {
        return Chips;
    }
    
    public void setChips(long chips) {
        Chips = chips;
    }
    
    public boolean isFlagFacebookLike() {
        return FlagFacebookLike;
    }
    
    public void setFlagFacebookLike(boolean flagFacebookLike) {
        FlagFacebookLike = flagFacebookLike;
    }
    
}
