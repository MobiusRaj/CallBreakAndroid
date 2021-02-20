package com.mobius.callbreakandroid.utility_base;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.mobius.callbreakandroid.R;
import com.mobius.callbreakandroid.data_store.PreferenceManager;

import java.util.HashMap;

public class Music_Manager {
    
    public static Music_Manager musicManager;
    private static AudioManager am;
    private static float vol = 0.99f;
    private static KeyguardManager myKM;
    private static int Card_Dealing, UserTurn, /*Winner, Timeout, MagicCollect, MagicFull, CardPickDiscard, */button_click;
    private static SoundPool spool;
    
    private HashMap<Integer, Boolean> soundLoaded;
    
    public Music_Manager(Activity mContext) {
        
        try {
            if (spool != null) {
                spool.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (soundLoaded != null) {
            soundLoaded.clear();
            soundLoaded = null;
        }
        soundLoaded = new HashMap<>();
        
        am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        myKM = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        
        spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        
        spool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            soundLoaded.put(sampleId, status == 0);
        });
        
        Card_Dealing = spool.load(mContext, R.raw.carddealing, 1);
        UserTurn = spool.load(mContext, R.raw.userturn, 1);
        /*Winner = spool.load(mContext, R.raw.winner, 1);
        Timeout = spool.load(mContext, R.raw.timeout, 1);
        CardPickDiscard = spool.load(mContext, R.raw.cardpickdiscard, 1);
        MagicCollect = spool.load(mContext, R.raw.magic_collect, 1);
        MagicFull = spool.load(mContext, R.raw.magic_full, 1);*/
        button_click = spool.load(mContext, R.raw.button_click, 1);
    }
    
    public static boolean isMute() {
        return am != null && am.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
    }
    
    public static Music_Manager getInstance(Activity context) {
        if (musicManager == null) {
            musicManager = new Music_Manager(context);
        }
        return musicManager;
    }
    
    public void buttonClick() {
        if (canPlaySound() && soundLoaded.containsKey(button_click) && soundLoaded.get(button_click))
            spool.play(button_click, vol, vol, 1, 0, 1);
    }
    
    public void play_CardDealing() {
        if (canPlaySound() && soundLoaded.containsKey(Card_Dealing) && soundLoaded.get(Card_Dealing))
            spool.play(Card_Dealing, vol, vol, 1, 0, 1);
    }
    
    public void play_Notification() {
        if (canPlaySound() && soundLoaded.containsKey(UserTurn) && soundLoaded.get(UserTurn))
            spool.play(UserTurn, vol, vol, 1, 0, 1);
    }

    
    public boolean isCallActive() {
        return am != null && am.getMode() == AudioManager.MODE_IN_CALL;
    }
    
    private boolean canPlaySound() {
        return !isMute() &&
                myKM != null &&
                PreferenceManager.getSound() &&
                !myKM.inKeyguardRestrictedInputMode() &&
                am.getMode() != AudioManager.MODE_IN_CALL &&
                am.getRingerMode() != AudioManager.RINGER_MODE_SILENT &&
                am.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE;
    }
}
