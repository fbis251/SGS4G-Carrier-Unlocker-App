package com.fernandobarillas.SGS4GCarrierUnlocker;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HexUnlockActivity extends SherlockFragment {
	static TextView textView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO: Add some kind of progress dialog for lock/unlock procedure

		final HexUnlock hexUnlock = new HexUnlock();
		String lockStatus = hexUnlock.getLockStatus();

		Log.i("HexUnlockActivity", "Instantiated");
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.activity_hex_unlock, container,
				false);

		textView = (TextView) view.findViewById(R.id.hex_unlock_result_view);
		textView.setText(lockStatus);

		final Button hexUnlockButton = (Button) view
				.findViewById(R.id.hex_unlock_button);
		final Button hexLockButton = (Button) view
				.findViewById(R.id.hex_lock_button);

		// Disabled by default until we determine the lock status
		hexLockButton.setEnabled(false);
		hexUnlockButton.setEnabled(false);

		if (lockStatus == "unlocked") {
			hexUnlockButton.setEnabled(false);
		} else if (lockStatus == "locked") {
			hexLockButton.setEnabled(false);
		}

		hexUnlockButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO: Make sure you warn the user if a backup isn't detected
				// We don't want users repeatedly tapping the button
				hexUnlockButton.setEnabled(false);
				hexUnlock.doHexEdit("unlocked");
				textView.setText(hexUnlock.getLockStatus());
				hexLockButton.setEnabled(true);
			}
		});

		hexLockButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// We don't want users repeatedly tapping the button
				hexLockButton.setEnabled(false);
				hexUnlock.doHexEdit("locked");
				textView.setText(hexUnlock.getLockStatus());
				hexUnlockButton.setEnabled(true);
			}
		});

		return view;
	}
}
