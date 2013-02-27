package com.fernandobarillas.SGS4GCarrierUnlocker;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.stericson.RootTools.*;
import com.stericson.RootTools.exceptions.*;
import com.stericson.RootTools.execution.*;

public class Shell {
	private static boolean ROOT_AVAILABLE = false;
	private static boolean BUSYBOX_INSTALLED = false;
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

	private boolean checkRoot() {
		boolean result = true;

		if (!ROOT_AVAILABLE) {
			if (!RootTools.isRootAvailable()) {
				RootTools.log("Root access isn't available");
				result = false;
			} else if (!RootTools.isAccessGiven()) {
				RootTools.log("Root access denied by user");
				result = false;
			} else {
				ROOT_AVAILABLE = true;
			}
		}
		return result;
	}

	public boolean checkBusybox() {
		boolean result = false;

		if (BUSYBOX_INSTALLED
				|| (checkRoot() && RootTools.isBusyboxAvailable())) {
			result = true;
			BUSYBOX_INSTALLED = true;
			System.out.println("We've got root access and busybox found!!!");
		} else {
			System.out.println("Busybox not found :(");
		}

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
