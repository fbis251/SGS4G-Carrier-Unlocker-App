package com.fernandobarillas.SGS4GCarrierUnlocker;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UnlockCodeActivity extends Activity implements Runnable {
	String pleaseWait;
	String outputFile;
	String nvDataFile;
	String nvDataTempFile;
	String unlockCodeFound;
	String unlockCodeNotFound;
	String unlockCodeSaved;
	String unlockCodeNotSaved;
	String unlockCode;
	UnlockCode unlockCodeObject;
	static String resultText;
	static TextView textView;
	static ProgressDialog processDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("UnlockCodeActivity", "Instantiated");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unlock_code);

		// Initialize variables
		pleaseWait = getString(R.string.please_wait);
		outputFile = getString(R.string.output_file);
		nvDataFile = getString(R.string.nv_data_file);
		nvDataTempFile = getString(R.string.nv_data_temp_file);
		unlockCodeFound = getString(R.string.unlock_code_found);
		unlockCodeNotFound = getString(R.string.unlock_code_not_found);
		unlockCodeSaved = getString(R.string.unlock_code_saved);
		unlockCodeNotSaved = getString(R.string.unlock_code_not_saved);

		// Set the resultText view for use by other methods
		textView = (TextView) this.findViewById(R.id.unlock_code_result_view);

		// Add a complete path to external storage
		outputFile = UnlockCode.STORAGE_PATH_ROOT + outputFile;
		unlockCodeObject = new UnlockCode(nvDataFile, outputFile,
				nvDataTempFile);

		// This is our main button! Basically only here so the user will need to
		// do something before the superuser request
		final Button unlockButton = (Button) findViewById(R.id.unlock_button);
		unlockButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// We don't want users repeatedly tapping the button
				unlockButton.setEnabled(false);

				// Tell the user to chill while we do our thing
				processDialog = new ProgressDialog(UnlockCodeActivity.this);
				processDialog.setMessage(pleaseWait);
				processDialog.setCancelable(false);
				processDialog.show();

				// We start the unlocking process in another thread so the UI
				// can update while it waits for the result
				Thread thread = new Thread(UnlockCodeActivity.this);
				thread.start();
			}
		});
	}

	// Process Dialog
	// run and handler modified from
	// http://www.helloandroid.com/tutorials/using-threads-and-progressdialog
	public void run() {
		unlockCode = unlockCodeObject.getUnlockCode();

		if (unlockCode != "") {
			resultText = unlockCodeFound + "\n" + unlockCode;
			// Try to write the unlock code to the SD card
			if (unlockCodeObject.saveUnlockCodeToSDCard(unlockCode)) {
				resultText += "\n\n" + unlockCodeSaved + "\n" + outputFile;
			} else {
				resultText += "\n\n" + unlockCodeNotSaved;
			}
		} else {
			resultText = unlockCodeNotFound;
		}

		// Tell the thread handler that we're done
		handler.sendEmptyMessage(0);
	}

	private static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// We don't need the dialog anymore
			processDialog.dismiss();
			textView.setText(resultText);
		}
	};

}
