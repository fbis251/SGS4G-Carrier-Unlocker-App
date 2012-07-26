package com.fernandobarillas.SGS4GCarrierUnlocker;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;

public class AboutActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("myid", "Activity: AboutActivity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}

}
