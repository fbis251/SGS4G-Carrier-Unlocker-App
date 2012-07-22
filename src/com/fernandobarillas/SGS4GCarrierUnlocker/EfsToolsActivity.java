package com.fernandobarillas.SGS4GCarrierUnlocker;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;

public class EfsToolsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_efs_tools);
		Log.i("myid", "Activity: EfsToolsActivity");
	}
}
