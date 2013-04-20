package com.fernandobarillas.SGS4GCarrierUnlocker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPreferences {
    public static final boolean KEY_PREFS_FIRST_RUN = true;
    private static final String APP_SHARED_PREFS = AppPreferences.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private Editor editor;

    public AppPreferences(Context context) {
        this.sharedPreferences = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public boolean getFirstRunValue() {
        return sharedPreferences.getBoolean("KEY_PREFS_FIRST_RUN", KEY_PREFS_FIRST_RUN);
    }

    public void setFirstRunValue(boolean bool) {
    	editor.putBoolean("KEY_PREFS_FIRST_RUN", bool);
    	editor.commit();
    }
}