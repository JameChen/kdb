package com.hyphenate.easeui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jame on 2017/6/6.
 */

public class PreferenceManager {
    public static final String PREFERENCE_NAME = "saveInfo";
    private static SharedPreferences mSharedPreferences;
    private static PreferenceManager mPreferencemManager;
    private static SharedPreferences.Editor editor;
    private static String SHARED_KEY_TO_NICK_NAME= "SHARED_KEY_TO_NICK_NAME";

    @SuppressLint("CommitPrefEdits")
    private PreferenceManager(Context cxt) {
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static synchronized void init(Context cxt){
        if(mPreferencemManager == null){
            mPreferencemManager = new PreferenceManager(cxt);
        }
    }

    /**
     * get instance of PreferenceManager
     *
     * @param
     * @return
     */
    public synchronized static PreferenceManager getInstance() {
        if (mPreferencemManager == null) {
            throw new RuntimeException("please init first!");
        }

        return mPreferencemManager;
    }
    public void setToNickName(String id,String nickname){
        editor.putString(SHARED_KEY_TO_NICK_NAME+id, nickname);
        editor.apply();
    }
    public String getToNickName(String id) {
        return mSharedPreferences.getString(SHARED_KEY_TO_NICK_NAME+id, "");
    }

}
