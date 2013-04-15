package com.fernandobarillas.SGS4GCarrierUnlocker;

import android.os.Environment;
import android.util.Log;

public class EfsTools {
	static Shell SHELL;
	static String STORAGE_PATH_ROOT = Environment.getExternalStorageDirectory()
			.getPath();
	// TODO: Change BACKUP_PATH to user defined location if available, making
	// sure that any existing backups get moved to the new location
	static String BACKUP_PATH = STORAGE_PATH_ROOT;
	static String BACKUP_FILENAME = "efs_backup.tar";
	static String BACKUP_TAR = BACKUP_PATH + "/" + BACKUP_FILENAME;
	static String BACKUP_TAR_MD5 = BACKUP_TAR + ".md5";

	public EfsTools() {
		Log.i("EfsTools", "Instantiated");
	}

	public boolean doEfsBackup() {
		Log.i("EfsTools", "doEfsBackup()");
		boolean result = false;

		if (!createShell()) {
			return false;
		}

		// TODO: Expand backup to something like cm9's updater.sh to save
		// backups that were already made.
		Log.i("EfsTools", "Attempting backup");

		SHELL.sendCommand("busybox mkdir -p " + BACKUP_PATH + " && cd / && "
				+ "busybox tar cf " + BACKUP_TAR + " /efs && cd " + BACKUP_PATH
				+ " && busybox md5sum " + BACKUP_FILENAME + " > "
				+ BACKUP_TAR_MD5);

		if (SHELL.lastExitStatus() == 0)
			result = true;

		Log.i("EfsTools", "Backup result = " + result);
		return result;
	}

	public boolean doEfsRestore() {
		Log.i("EfsTools", "doEfsRestore()");
		boolean result = false;

		if (!createShell())
			return result;

		Log.i("EfsTools", "Sending restore command");
		SHELL.sendCommand("cd " + BACKUP_PATH + " && md5sum -c "
				+ BACKUP_TAR_MD5 + " && cd / && tar xf " + BACKUP_TAR);

		if (SHELL.lastExitStatus() == 0)
			result = true;

		Log.i("EfsTools", "Restore result = " + result);

		return result;
	}

	public boolean isBackupAvailable() {
		Log.i("EfsTools", "isBackupAvailable()");
		// TODO: Check if backup exists
		return false;
	}

	private boolean createShell() {
		Log.i("EfsTools", "createShell()");
		if (SHELL == null) {
			SHELL = new Shell();

			if (!SHELL.checkRoot()) {
				Log.i("EfsTools", "createShell() Root unvailable");
				return false;
			}

			if (!SHELL.checkBusybox()) {
				// TODO: Send some kind of warning to user here
				Log.i("EfsTools", "createShell() Busybox unvailable");
				return false;
			}
		}

		return true;
	}
}
