package com.dt.android.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.dt.android.R;
import com.dt.android.db.DBAdapter;
import com.dt.android.domainobject.AppData;
import com.dt.android.utils.AsyncTaskEx;
import com.dt.android.view.listener.BackButtonClickListener;
import com.dt.android.view.listener.SelectCityBtnClickListener;

public class BaseActivity extends FragmentActivity implements OnCancelListener {

	protected boolean btnInited = false;
	protected DBAdapter dbadapter;

	protected boolean leftBtnIsLocation = true;
	protected boolean rightBtnIsCarType = true;

	protected final static int SELECT_CAR_TYPE_DIALOG_ID = 1000;
	protected final static int CLEAR_STARED=1001;
	protected final static int PROGRESS_DIALOG=1002;
	protected final static int NO_NETWORK=1003;
	protected static final int EXIT_CONFIRM_DIALOG = 1004;
	public static final int CAR_TYPE_CAR = 0;
	public static final int CAR_TYPE_BUS = 1;
	public static final int CAR_TYPE_TRUCK = 2;

	private Button rightBtn;
	
	protected boolean dialogCanceled =false;
	
	protected AsyncTaskEx task;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		if (AppData.getInstance().isNightMode()) {
//			this.setTheme(R.style.myTheme_night);
//		} else {
//			this.setTheme(R.style.myTheme);
//		}
		super.onCreate(savedInstanceState);
		// Log.d("BaseAc","is night mode:"+AppData.getInstance().isNightMode());
		dbadapter = new DBAdapter(this);
		dbadapter.open();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		dbadapter.close();
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		if (!btnInited) {
			Button btn = (Button) findViewById(R.id.leftbtn);

			if (this.leftBtnIsLocation) {
				btn.setOnClickListener(new SelectCityBtnClickListener(this));
			} else {
				btn.setOnClickListener(new BackButtonClickListener(this));
			}
			if (this.rightBtnIsCarType) {
				rightBtn = (Button) findViewById(R.id.rightbtn);				
				rightBtn.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						showDialog(SELECT_CAR_TYPE_DIALOG_ID);

					}
				});
			}
			btnInited = true;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (this.leftBtnIsLocation) {
			Button btn = (Button) findViewById(R.id.leftbtn);
			btn.setText(AppData.getInstance().getCurrentLocation().getName());
			btn.setHorizontallyScrolling(true);  
			btn.setFocusable(true);  
			
		}
		if(this.rightBtnIsCarType){
			rightBtn.setText(getResources()
					.getStringArray(R.array.car_type)[AppData.getInstance()
					.getCarType()]);
		}

	}
	
	public void showProgressDialog(int id){
		this.dialogCanceled=false;
		this.showDialog(id);
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SELECT_CAR_TYPE_DIALOG_ID:
			return createCarTypeDialog();
		case CLEAR_STARED:
			return createStaredDialog();
		case PROGRESS_DIALOG:
			return createProgressDialog();
		case NO_NETWORK:
			return createNoNetworkDialog();
		}
		return super.onCreateDialog(id);
	}
	private Dialog createNoNetworkDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("网络错误")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage("无网络连接，请打开网络")
				.setCancelable(false)
				.setPositiveButton("设置",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						enableNetworkSettings();
					}
				}).setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}
	private Dialog createProgressDialog() {
		ProgressDialog d= ProgressDialog.show(this, "",getProgressMessage(), true);
		d.setCancelable(true);
		d.setContentView(R.layout.progress_dialog);
		d.setOnCancelListener(this);		
		return d;
	}
	private void enableNetworkSettings() {
		Intent settingsIntent = new Intent(
				Settings.ACTION_WIRELESS_SETTINGS);
		startActivityForResult(settingsIntent, 1);
		startActivity(settingsIntent);
	}

	protected void doClear(){
		
	};
	protected String getProgressMessage(){
		return "";
	}
	protected String [] getClearMessage(){
		return new String[]{"清空收藏","确认要清空我的收藏？"};
	};	
	
	private Dialog createStaredDialog() {
		String []message=this.getClearMessage();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(message[0])
				.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage(message[1])
				.setCancelable(false)
				.setPositiveButton("清空",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								doClear();
							}
						}).setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

	private Dialog createCarTypeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.select_car_type);
		builder.setSingleChoiceItems(R.array.car_type_options, AppData.getInstance()
				.getCarType(), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				AppData.getInstance().setCarType(which);
				dbadapter.getAppConfDAO().updateAppConf("car.type", which + "");

				rightBtn.setText(getResources()
						.getStringArray(R.array.car_type)[which]);
				removeDialog(SELECT_CAR_TYPE_DIALOG_ID);
			}

		});

		return builder.create();
	}

	public void updateUIForSelectedItem() {

	}

	public void showToast(int message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	public void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	protected String formatTime(int i) {
		return format(i / 60) + ":" + format(i % 60);
	}

	private String format(int i) {
		if (i < 10) {
			return "0" + i;
		} else {
			return String.valueOf(i);
		}
	}

	public void onCancel(DialogInterface dialog) {
		dialogCanceled=true;	
		if(task!=null)
		{
			task.cancel(true);
		}
	}

	public AsyncTaskEx getExcuteTask(){
		return task;
	}
	public boolean isConnected(){
		ConnectivityManager cm =
	        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();
	}


	
}
