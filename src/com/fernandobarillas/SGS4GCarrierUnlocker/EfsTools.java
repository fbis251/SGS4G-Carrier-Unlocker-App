package com.fernandobarillas.SGS4GCarrierUnlocker;

public class EfsTools {
	static Shell SHELL;

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
		SHELL.sendCommand("cd / && busybox tar cvf /sdcard/test/backup.tar /efs && cd /sdcard/test && busybox md5sum backup.tar > backup.tar.md5");
		// System.out.println("RESULT = " + SHELL.lastExitStatus() + "\n" +
		// SHELL);
		if (SHELL.lastExitStatus() == 0)
			result = true;

		System.out.println("efs BACKUP result = " + result);
		return result;
	}

	public boolean doEfsRestore() {
		boolean result = false;

		SHELL.sendCommand("cd /sdcard/test && md5sum -c backup.tar.md5 && cd / && tar xvf /sdcard/test/backup.tar");
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
