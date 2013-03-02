package com.fernandobarillas.SGS4GCarrierUnlocker;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.os.Bundle;
import android.util.Log;

public class SettingsActivity extends SherlockPreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("SettingsActivity", "Instantiated");
		super.onCreate(savedInstanceState);
		
		Log.i("SettingsActivity", "Loading preferences from XML");
		addPreferencesFromResource(R.xml.preferences);
	}
}
