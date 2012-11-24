package com.fernandobarillas.SGS4GCarrierUnlocker;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity {
	String tabUnlockCode;
	String tabHexUnlock;
	String tabEfsTools;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("MainActivity", "Activity: MainActivity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Initialize variables
		tabUnlockCode = getString(R.string.tab_unlock_code);
		tabHexUnlock = getString(R.string.tab_hex_unlock);
		tabEfsTools = getString(R.string.tab_efs_tools);

		TabHost tabHost = getTabHost();

		// Unlock Code tab
		TabSpec tabUnlockCodeSpec = tabHost.newTabSpec(tabUnlockCode);
		tabUnlockCodeSpec.setIndicator(tabUnlockCode, getResources()
				.getDrawable(R.drawable.key));
		Intent unlockCodeIntent = new Intent(this, UnlockCodeActivity.class);
		tabUnlockCodeSpec.setContent(unlockCodeIntent);

		// Hex Unlock tab
		TabSpec hexUnlockSpec = tabHost.newTabSpec(tabHexUnlock);
		hexUnlockSpec.setIndicator(tabHexUnlock,
				getResources().getDrawable(R.drawable.magnifying_glass));
		Intent hexUnlockIntent = new Intent(this, HexUnlockActivity.class);
		hexUnlockSpec.setContent(hexUnlockIntent);

		// EFS Tools tab
		TabSpec efsToolsSpec = tabHost.newTabSpec(tabEfsTools);
		efsToolsSpec.setIndicator(tabEfsTools,
				getResources().getDrawable(R.drawable.magnifying_glass));
		Intent efsToolsIntent = new Intent(this, EfsToolsActivity.class);
		efsToolsSpec.setContent(efsToolsIntent);

		// Now we add all the tabs
		tabHost.addTab(tabUnlockCodeSpec);
		tabHost.addTab(hexUnlockSpec);
		tabHost.addTab(efsToolsSpec);
	}

	// Parse menu.xml
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menu, menu);
		return true;
	}

	// Take care of menu events
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.about:
			Intent aboutIntent = new Intent(this, AboutActivity.class);
			startActivity(aboutIntent);
			return true;
		case R.id.exit:
			terminate();
			return true;
		case R.id.settings:
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void terminate() {
		super.onDestroy();
		this.finish();
		Log.i("MainActivity", "Application successfully terminated!");
		System.exit(0);
	}
}
