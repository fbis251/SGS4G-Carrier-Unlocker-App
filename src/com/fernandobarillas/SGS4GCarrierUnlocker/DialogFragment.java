package com.fernandobarillas.SGS4GCarrierUnlocker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class DialogFragment extends SherlockDialogFragment {
	private AppPreferences appPreferences;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		appPreferences = new AppPreferences(getActivity());

		builder.setTitle(R.string.dialog_agreement_title)
				.setMessage(R.string.dialog_agreement_text)
				.setPositiveButton(R.string.dialog_agreement_accept,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Log.i("DialogFragment", "Dialog accepted");
								appPreferences.setFirstRunValue(false);
							}
						})
				.setNegativeButton(R.string.dialog_agreement_decline,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Log.i("DialogFragment", "Dialog declined");
								System.exit(0);
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}
}