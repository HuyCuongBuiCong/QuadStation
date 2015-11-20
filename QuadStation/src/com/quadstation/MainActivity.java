package com.quadstation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.Window;
import com.quadstation.map.MapWaypointsActivity;
import com.quadstation.pid.PIDActivity;
import com.quadstation.app.App;
import com.quadstation.communication.BT_New;
import com.quadstation.control.ControlActivity;
import com.quadstation.dashboard.CompassAndLevelViewActivity;
import com.quadstation.graph.GraphsActivity;

public class MainActivity extends SherlockActivity {
	Button buttonBT, buttonDashBoard, buttonGraph, buttonMap, buttonPID,
			buttonControl;
	TextView textViewBTdevice;
	private boolean killme = false;
	private App app;
	TextView TVInfo;
	ActionBarSherlock actionBar;
	Context context;
	private static final int REQUEST_CONNECT_DEVICE_MULTIWII = 1;
	private static final int REQUEST_CONNECT_DEVICE_FRSKY = 2;

	private final Handler mHandler = new Handler();
	private final Handler mHandler1 = new Handler() {
		// BinaryFileAccess file = new
		// BinaryFileAccess("/MultiWiiLogs/dump1.txt", true);

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BT_New.MESSAGE_STATE_CHANGE:
				Log.i("ccc", "MESSAGE_STATE_CHANGE: " + msg.arg1);

				switch (msg.arg1) {
				case BT_New.STATE_CONNECTED:
					setStatus("Connected");
					break;
				case BT_New.STATE_CONNECTING:
					setStatus(getString(R.string.Connecting));
					break;
				case BT_New.STATE_NONE:
					break;
				}

				break;
			case BT_New.MESSAGE_WRITE:
				break;
			case BT_New.MESSAGE_READ:
				// byte[] readBuf = (byte[]) msg.obj;
				// String readMessage = new String(readBuf, 0, msg.arg1);
				// //file.WriteBytes(readBuf);

				break;
			case BT_New.MESSAGE_DEVICE_NAME:
				String deviceName = msg.getData().getString(BT_New.DEVICE_NAME);
				setStatus(getString(R.string.Connected) + "->" + deviceName);
				Log.d("ccc", "Device Name=" + deviceName);
				break;
			case BT_New.MESSAGE_TOAST:
				Log.i("ccc",
						"MESSAGE_TOAST:"
								+ msg.getData().getString(BT_New.TOAST));
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(BT_New.TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private final void setStatus(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		app = (App) getApplication();
		context = this;
		buttonBT = (Button) findViewById(R.id.buttonSelectBTDevice);
		buttonDashBoard = (Button) findViewById(R.id.buttonDashBoardCompassAndLevel);
		buttonGraph = (Button) findViewById(R.id.buttonGpaph);
		buttonMap = (Button) findViewById(R.id.buttonMap);
		buttonPID = (Button) findViewById(R.id.buttonPID);
		buttonControl = (Button) findViewById(R.id.buttonControl);
		textViewBTdevice = (TextView) findViewById(R.id.textViewMacAddress);
		// app.commMW.SetHandler(mHandler1);
		// requestWindowFeature(Window.FEATURE_PROGRESS);
		// getSupportActionBar().setDisplayShowTitleEnabled(false);
		// app.AppStartCounter++;
		// app.SaveSettings(true);

	}

	@Override
	protected void onResume() {
		super.onResume();
		buttonBT.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent serverIntent = null;
				serverIntent = new Intent(context, DeviceListActivity.class);
				startActivityForResult(serverIntent,
						REQUEST_CONNECT_DEVICE_MULTIWII);

			}
		});

		buttonDashBoard.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent dashBoardIntent = new Intent(context,
						CompassAndLevelViewActivity.class);
				startActivity(dashBoardIntent);
			}
		});

		buttonGraph.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent graphIntent = new Intent(context, GraphsActivity.class);
				startActivity(graphIntent);
			}
		});

		buttonMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				check();
			}
		});

		buttonPID.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent PIDIntent = new Intent(context, PIDActivity.class);
				startActivity(PIDIntent);
			}
		});

		buttonControl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent ControlIntent = new Intent(context,
						ControlActivity.class);
				startActivity(ControlIntent);
			}
		});
		// app.ForceLanguage();
		// if (app.commMW.Connected) {
		//
		// try {
		// mHandler.removeCallbacksAndMessages(null);
		// } catch (Exception e) {
		// }
		// mHandler.postDelayed(update, 100);
		// }

	}

	protected void check() {
		if (app.checkGooglePlayServicesAvailability(this)) {
			killme = true;
			mHandler.removeCallbacksAndMessages(null);
			startActivity(new Intent(getApplicationContext(),
					MapWaypointsActivity.class).putExtra("WAYPOINT", false));
		}
	}

	public void onPause() {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		super.onPause();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		// menu.findItem(R.id.menu_connect_frsky).setVisible(app.FrskySupport);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		if (item.getItemId() == R.id.menu_exit) {
			EXIT(false);
			return true;
		}

		if (item.getItemId() == R.id.menu_connect) {
			Connect();
			mHandler.postDelayed(update, 100);
			return true;
		}
		if (item.getItemId() == R.id.menu_connect_frsky) {
			return true;
		}
		if (item.getItemId() == R.id.menu_disconnect) {
			Close();
			return true;
		}
		return false;
	}

	void EXIT(boolean restart) {
		// try {
		// stopService(new Intent(getApplicationContext(),
		// MOCK_GPS_Service.class));
		// } catch (Exception e) {
		// }

		if (app.DisableBTonExit
				&& app.ConfigHasBeenChange_DisplayRestartInfo == false) {
			app.commMW.Disable();
			app.commFrsky.Disable();
		}
		app.mw.CloseLoggingFile();
		app.notifications.Cancel(99);

		if (restart) {
			if (app.commMW.Connected)
				app.commMW.Disable();
			app.RestartApp();
		}
		Close();
		System.exit(0);
	}

	public void Close() {
		try {
			mHandler.removeCallbacksAndMessages(null);
			if (app.commMW.Connected)
				app.commMW.Close();
			if (app.commFrsky.Connected)
				app.commFrsky.Close();
		}

		catch (Exception e) {

		}

	}

	public void Connect() {
		if (app.CommunicationTypeMW == App.COMMUNICATION_TYPE_BT
				|| app.CommunicationTypeMW == App.COMMUNICATION_TYPE_BT_NEW) {
			if (!app.MacAddress.equals("")) {
				app.commMW.Connect(app.MacAddress, app.SerialPortBaudRateMW);
			} else {
				Toast.makeText(
						getApplicationContext(),
						"Wrong MAC address. Go to Config and select correct device",
						Toast.LENGTH_LONG).show();
			}
			try {
				mHandler.removeCallbacksAndMessages(null);
			} catch (Exception e) {
			}
		} else {
			app.commMW.Connect(app.MacAddress, app.SerialPortBaudRateMW);
		}
	}

	private Runnable update = new Runnable() {
		@Override
		public void run() {
			app.mw.ProcessSerialData(app.loggingON);
			app.Frequentjobs();
			app.mw.SendRequest(app.MainRequestMethod);
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);
			if (app.D)
				Log.d(app.TAG, "loop " + this.getClass().getName());
		}

	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_MULTIWII:
			if (resultCode == Activity.RESULT_OK) {
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				app.MacAddress = address;
				textViewBTdevice.setText("MAC:" + app.MacAddress);
			}
			break;

		case REQUEST_CONNECT_DEVICE_FRSKY:
			if (resultCode == Activity.RESULT_OK) {
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				app.MacAddressFrsky = address;
				textViewBTdevice.setText("MAC:" + app.MacAddressFrsky);
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
}
