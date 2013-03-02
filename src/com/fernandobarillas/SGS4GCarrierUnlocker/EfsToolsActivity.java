package com.fernandobarillas.SGS4GCarrierUnlocker;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EfsToolsActivity extends SherlockFragment {
	static EfsTools EFS_TOOLS;
	static TextView textView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("EfsToolsActivity", "Instantiated");
		View view = inflater.inflate(R.layout.activity_efs_tools, container,
				false);

		textView = (TextView) view.findViewById(R.id.efs_tools_result_view);
		textView.setText("");

		final Button efsBackupButton = (Button) view
				.findViewById(R.id.efs_backup_button);
		final Button efsRestoreButton = (Button) view
				.findViewById(R.id.efs_restore_button);
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

		return view;
	}
}
