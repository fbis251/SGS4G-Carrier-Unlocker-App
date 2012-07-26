package com.fernandobarillas.SGS4GCarrierUnlocker;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("myid", "Activity: SettingsActivity");
		super.onCreate(savedInstanceState);
		
		Log.i("myid", "Loading preferences from XML");
		addPreferencesFromResource(R.xml.preferences);
	}
}
