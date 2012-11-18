package com.fernandobarillas.SGS4GCarrierUnlocker;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.support.v4.app.NavUtils;

public class HexUnlockActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		Log.i("myid", "Activity: HexUnlockActivity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hex_unlock);
		System.out.println(HexUnlock.doHexUnlock());
	}

}
