package com.fernandobarillas.SGS4GCarrierUnlocker;

import android.os.Environment;

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
		SHELL = new Shell();
		if (!SHELL.checkBusybox()) {
			// TODO: Send some kind of warning to user here
		}
	}

	public boolean doEfsBackup() {
		boolean result = false;

		// TODO: Expand backup to something like cm9's updater.sh to save
		// backups that were already made.
		SHELL.sendCommand("cd / && " + "busybox tar cvf " + BACKUP_TAR
				+ " /efs && cd " + BACKUP_PATH + " && busybox md5sum "
				+ BACKUP_FILENAME + " > " + BACKUP_TAR_MD5);

		if (SHELL.lastExitStatus() == 0)
			result = true;

		System.out.println("efs BACKUP result = " + result);
		return result;
	}

	public boolean doEfsRestore() {
		boolean result = false;

		SHELL.sendCommand("cd " + BACKUP_PATH + " && md5sum -c "
				+ BACKUP_TAR_MD5 + " && cd / && tar xvf " + BACKUP_TAR);
		if (SHELL.lastExitStatus() == 0)
			result = true;
		System.out.println("efs RESTORE result = " + result);

		return result;
	}

	public boolean isBackupAvailable() {
		// TODO: Check if backup exists
		return false;
	}
}
