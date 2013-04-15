package com.fernandobarillas.SGS4GCarrierUnlocker;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.stericson.RootTools.*;
import com.stericson.RootTools.exceptions.*;
import com.stericson.RootTools.execution.*;

public class Shell {
	private static boolean ROOT_AVAILABLE = false;
	private static boolean BUSYBOX_INSTALLED = false;
	private static boolean CHECKED_ROOT = false;
	private static boolean CHECKED_BUSYBOX = false;
	private static int LAST_EXIT_CODE = 255;
	private static String LAST_COMMAND_OUTPUT = "";

	public String sendCommand(String commandString) {
		String result = "";

		if (checkRoot()) {
			CommandCapture command = new CommandCapture(0, commandString);
			try {
				RootTools.getShell(true).add(command).waitForFinish();
				result = command.toString();
				LAST_COMMAND_OUTPUT = result;
				LAST_EXIT_CODE = command.exitCode();
			} catch (InterruptedException e) {
				// TODO: Do something useful with the exceptions
				// e.printStackTrace();
			} catch (IOException e) {
				// e.printStackTrace();
			} catch (TimeoutException e) {
				// e.printStackTrace();
			} catch (RootDeniedException e) {
				// e.printStackTrace();
			}
		}
		return result;
	}

	public boolean checkRoot() {
		boolean result = true;

		// No need to do more shell calls if we already did the checks
		if (CHECKED_ROOT && !ROOT_AVAILABLE)
			// We already checked root and it wasn't available
			return false;

		if (!ROOT_AVAILABLE) {
			if (!RootTools.isRootAvailable()) {
				// TODO: Make a new dialog class that explains root access
				RootTools.log("Root access isn't available");
				result = false;
			} else if (!RootTools.isAccessGiven()) {
				RootTools.log("Root access denied by user");
				result = false;
			} else {
				ROOT_AVAILABLE = true;
			}
		}

		CHECKED_ROOT = true;
		return result;
	}

	public boolean checkBusybox() {
		boolean result = false;

		// No need to do more shell calls if we already did the checks
		if (CHECKED_BUSYBOX && !BUSYBOX_INSTALLED)
			// TODO: Open up a market instance for the busybox installer
			return false;
		
		if (BUSYBOX_INSTALLED
				|| (checkRoot() && RootTools.isBusyboxAvailable())) {
			result = true;
			BUSYBOX_INSTALLED = true;
		} else {
			// TODO: Make a new dialog class that offers busybox here
		}
		
		CHECKED_BUSYBOX = true;

		return result;
	}

	public int lastExitStatus() {
		return LAST_EXIT_CODE;
	}

	public String lastCommandOutput() {
		return LAST_COMMAND_OUTPUT;
	}

	public String toString() {
		return lastCommandOutput();
	}
}
