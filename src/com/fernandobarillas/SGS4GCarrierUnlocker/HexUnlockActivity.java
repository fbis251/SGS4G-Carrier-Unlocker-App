package com.fernandobarillas.SGS4GCarrierUnlocker;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HexUnlockActivity extends Activity {
	static TextView textView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("HexUnlockActivity", "Instantiated");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hex_unlock);
		final HexUnlock hexUnlock = new HexUnlock();
		String lockStatus = hexUnlock.getLockStatus();

		textView = (TextView) this.findViewById(R.id.hex_unlock_result_view);
		textView.setText(lockStatus);

		final Button hexUnlockButton = (Button) findViewById(R.id.hex_unlock_button);
		final Button hexLockButton = (Button) findViewById(R.id.hex_lock_button);

		if (lockStatus == "unlocked") {
			hexUnlockButton.setEnabled(false);
		} else {
			hexLockButton.setEnabled(false);
		}

		hexUnlockButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
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
	}

}
