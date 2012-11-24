package com.fernandobarillas.SGS4GCarrierUnlocker;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;

public class EfsToolsActivity extends Activity {
	EfsTools efsTools;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("EfsToolsActivity", "Instantiated");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_efs_tools);
	}
}
