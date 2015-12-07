package com.dt.android.activity;

import com.dt.android.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;

public class BaseMainActivity extends BaseActivity {


	protected boolean showConfirm=true;

	@Override
	public void finish() {
		if(showConfirm){
			this.showDialog(EXIT_CONFIRM_DIALOG);
		}else{
			super.finish();
		}
//		super.finish();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id==EXIT_CONFIRM_DIALOG){
			return creatExitDialog();
		}
		return super.onCreateDialog(id);
	}

	private Dialog creatExitDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
				.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage(R.string.exit_confirm)
				.setCancelable(false)
				.setPositiveButton("ÍË³ö",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								BaseMainActivity.super.finish();
							}
						}).setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}
	
	
}
