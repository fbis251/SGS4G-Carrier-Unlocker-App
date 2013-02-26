package com.fernandobarillas.SGS4GCarrierUnlocker;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.stericson.RootTools.*;
import com.stericson.RootTools.exceptions.*;
import com.stericson.RootTools.execution.*;

public class Shell {
	private static boolean ROOT_AVAILABLE = false;

	public String sendCommand(String commandString) {
		String result = "";
		
		if (checkRoot()) {
			CommandCapture command = new CommandCapture(0, commandString);
			try {
				RootTools.getShell(true).add(command).waitForFinish();
				result = command.toString();
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
}
