package com.fernandobarillas.SGS4GCarrierUnlocker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;

public class EfsToolsActivity extends Activity {
	static EfsTools EFS_TOOLS;
	static TextView textView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("EfsToolsActivity", "Instantiated");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_efs_tools);

		textView = (TextView) this.findViewById(R.id.efs_tools_result_view);
		textView.setText("");

		final Button efsBackupButton = (Button) findViewById(R.id.efs_backup_button);
		final Button efsRestoreButton = (Button) findViewById(R.id.efs_restore_button);
		EFS_TOOLS = new EfsTools();

		efsBackupButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String text = "";
				// TODO: Disable backup/restore buttons if busybox is not
				// available

				// We don't want users repeatedly tapping the button
				efsBackupButton.setEnabled(false);
				if (EFS_TOOLS.doEfsBackup()) {
					text = "Backup successful";
				} else {
					text = "Backup failed";
				}

				textView.setText(text);
				efsBackupButton.setEnabled(true);
			}
		});

		efsRestoreButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String text = "";

				// We don't want users repeatedly tapping the button
				efsRestoreButton.setEnabled(false);
				if (EFS_TOOLS.doEfsRestore()) {
					text = "Restore successful";
				} else {
					text = "Restore failed";
				}
				textView.setText(text);
				efsRestoreButton.setEnabled(true);
			}
		});
	}
}
