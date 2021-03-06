/*  MultiWii EZ-GUI
    Copyright (C) <2012>  Bartosz Szczygiel (eziosoft)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.quadstation.app;

import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.quadstation.helper.Sensors;
import com.quadstation.helper.Sensors.Listener;
import com.quadstation.MainActivity;
import com.quadstation.R;
import com.quadstation.communication.BT;
import com.quadstation.communication.BT_New;
import com.quadstation.communication.Communication;
import com.quadstation.helper.Notifications;
import com.quadstation.mw.QuadProtocol;
import com.quadstation.mw.QuadrotorData;

;

public class App extends Application implements Sensors.Listener {

	public static final int PROTOCOL_220 = 220;
	public static final int PROTOCOL_230 = 230;
	public static final int PROTOCOL_NAV = 231;

	// debug
	public boolean D = true; // debug
	public String TAG = "EZGUI";
	public String MapAPIKeyDebug = "0AxI9Dd4w6Y_4upkSvwAfQDK1f8fXpsnCx07vyg";
	public String MapAPIKeyPublic = "0AxI9Dd4w6Y-ERQuGVB0WKB4x4iZe3uD9HVpWYQ";
	// end debug/////////////////

	private static String REFRESHRATE = "REFRESHRATE";
	public int RefreshRate = 100; // this means wait 100ms after everything is
									// done

	public Communication commMW;
	public QuadrotorData mw;

	public boolean FollowMeEnable = false;
	public boolean FollowMeBlinkFlag = false;

	public boolean FollowHeading = false;
	public boolean FollowHeadingBlinkFlag = false;

	// public FrskyProtocol frskyProtocol;

	private SharedPreferences prefs;
	private Editor editor;
	public Sensors sensors;
	// variables used in FrequentJobs
	private boolean[] oldActiveModes;

	int timer2Freq = 8000;// bip when low battery
	private long timer3 = 0;
	int timer3Freq = 1000; // timer every 1sek
	private long timer4 = 0;
	int timer4Freq = 5000; // timer every 5sek

	public boolean loggingON = false;
	// ----settings-----

	private static final String COPYFRSKYTOMW = "COPYFRSKYTOMW";
	public boolean CopyFrskyToMW;

	private static final String COMMUNICATION_TYPE_MW = "CommunicationTypeMW2";
	public static final int COMMUNICATION_TYPE_BT = 0;
	public static final int COMMUNICATION_TYPE_SERIAL_FTDI = 1;
	public static final int COMMUNICATION_TYPE_SERIAL_OTHERCHIPS = 2;
	public static final int COMMUNICATION_TYPE_BT_NEW = 4;

	public int CommunicationTypeMW = COMMUNICATION_TYPE_BT;

	public static final String SERIAL_PORT_BAUD_RATE_MW = "SerialPortBaudRateMW1";
	public int SerialPortBaudRateMW = 115200;

	public static final String SERIAL_PORT_BAUD_RATE_FRSKY = "SerialPortBaudRateFrSky1";
	public int SerialPortBaudRateFrSky = 9600;

	public int CommunicationTypeFrSky = COMMUNICATION_TYPE_BT;

	private static final String RADIOMODE = "RadioMode";
	public int RadioMode;

	private static final String PROTOCOL = "PROTOCOL4";
	public int Protocol;

	private static final String MAGMODE = "MAGMODE";
	public int MagMode;

	private static final String TEXTTOSPEACH = "TEXTTOSPEACH1";
	public boolean TextToSpeach = true;

	private static final String MACADDERSS = "MACADDERSS";
	public String MacAddress = "";

	private static final String MACADDERSSFRSKY = "MACADDERSSFRSKY";
	public String MacAddressFrsky = "";

	private static final String CONNECTONSTART = "CONNECTONSTART";
	public boolean ConnectOnStart = false;

	// private static final String ADVANCEDFUNCTIONS = "ADVANCEDFUNCTIONS";
	public boolean AdvancedFunctions = false;

	private static final String DISABLEBTONEXIT = "DISABLEBTONEXIT";
	public boolean DisableBTonExit = true;

	private static final String FORCELANGUAGE = "FORCELANGUAGE";
	public String ForceLanguage = "";

	private static final String PERIODICSPEAKING = "PERIODICSPEAKING";
	public int PeriodicSpeaking = 20000; // in ms

	private static final String VOLTAGEALARM = "VOLTAGEALARM";
	public float VoltageAlarm = 0;

	// private static final String USEOFFLINEMAPS = "USEOFFLINEMAPS";
	// public boolean UseOfflineMaps = false;

	private static final String APPSTARTCOUNTER = "APPSTARTCOUNTER";
	public int AppStartCounter = 0;

	private static final String DONATEBUTTONPRESSED = "DONATEBUTTONPRESSED";
	public int DonateButtonPressed = 0;

	private static final String REVERSEROLL = "REVERSEROLL";
	public boolean ReverseRoll = false;

	private static final String MAPZOOMLEVEL = "MAPZOOMLEVEL1";
	public float MapZoomLevel = 9;

	private static final String MAPCENTERPERIOD = "MAPCENTERPERIOD";
	public int MapCenterPeriod = 3;

	// private static final String MAINREQUESTMETHOD = "MAINREQUESTMETHOD1";
	public int MainRequestMethod = 2;

	private static final String FRSKY_SUPPORT = "FRSKY_SUPPORT";
	public boolean FrskySupport = false;

	private static final String NO_DATA_RECEIVED_WARNING = "NO_DATA_RECEIVED_WARNING";
	public boolean NoDataReceievedWarning = true;

	public boolean VarioSound = false;

	// graphs
	public final String ACCROLL = "ACC ROLL";
	public final String ACCPITCH = "ACC PITCH";
	public final String ACCZ = "ACC Z";

	public final String GYROROLL = "GYRO ROLL";
	public final String GYROPITCH = "GYRO PITCH";
	public final String GYROYAW = "GYRO YAW";

	public final String MAGROLL = "MAG ROLL";
	public final String MAGPITCH = "MAG PITCH";
	public final String MAGYAW = "MAG YAW";

	public final String ALT = "ALT";
	public final String HEAD = "HEAD";

	public final String DEBUG1 = "DEBUG1";
	public final String DEBUG2 = "DEBUG2";
	public final String DEBUG3 = "DEBUG3";
	public final String DEBUG4 = "DEBUG4";

	private final static String GRAPHSTOSHOW = "GRAPHSTOSHOW";
	public String GraphsToShow = ACCROLL + ";" + ACCZ + ";" + ALT + ";" + GYROPITCH;

	// graphs end

	public Notifications notifications;

	private int tempLastI2CErrorCount = 0;

	public boolean ConfigHasBeenChange_DisplayRestartInfo = false;

	@Override
	public void onCreate() {

		Log.d("aaa", "APP ON CREATE");
		super.onCreate();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		editor = prefs.edit();
		Init();
		notifications = new Notifications(getApplicationContext());
		sensors = new Sensors(getApplicationContext());
		sensors.registerListener(this);
		sensors.start();

	}

	public void Init() {
		ReadSettings();
		ForceLanguage();

		if (CommunicationTypeMW == COMMUNICATION_TYPE_BT) {
			commMW = new BT(getApplicationContext());
			CommunicationTypeFrSky = COMMUNICATION_TYPE_BT;
		}

		if (CommunicationTypeMW == COMMUNICATION_TYPE_BT_NEW) {
			commMW = new BT_New(getApplicationContext());
			CommunicationTypeFrSky = COMMUNICATION_TYPE_BT_NEW;
		}

		// ////////////

		SelectProtocol();

	}

	public void SelectProtocol() {
		if (Protocol == PROTOCOL_230) {
			mw = new QuadProtocol(commMW);

			Log.d("protoocol", "230");
		}
		oldActiveModes = new boolean[20];// not the best method

	}

	public void ReadSettings() {
		RadioMode = prefs.getInt(RADIOMODE, 2);
		Protocol = prefs.getInt(PROTOCOL, 230);
		MagMode = prefs.getInt(MAGMODE, 1);
		TextToSpeach = prefs.getBoolean(TEXTTOSPEACH, true);
		MacAddress = prefs.getString(MACADDERSS, "");
		MacAddressFrsky = prefs.getString(MACADDERSSFRSKY, "");
		ConnectOnStart = prefs.getBoolean(CONNECTONSTART, false);

		DisableBTonExit = prefs.getBoolean(DISABLEBTONEXIT, true);
		ForceLanguage = prefs.getString(FORCELANGUAGE, "");
		VoltageAlarm = prefs.getFloat(VOLTAGEALARM, 9.9f);
		GraphsToShow = prefs.getString(GRAPHSTOSHOW, GraphsToShow);
		// UseOfflineMaps = prefs.getBoolean(USEOFFLINEMAPS, false);
		RefreshRate = prefs.getInt(REFRESHRATE, 100);
		CopyFrskyToMW = prefs.getBoolean(COPYFRSKYTOMW, false);
		AppStartCounter = prefs.getInt(APPSTARTCOUNTER, 0);
		DonateButtonPressed = prefs.getInt(DONATEBUTTONPRESSED, 0);
		ReverseRoll = prefs.getBoolean(REVERSEROLL, false);
		MapZoomLevel = prefs.getFloat(MAPZOOMLEVEL, 9);
		MapCenterPeriod = prefs.getInt(MAPCENTERPERIOD, 3);
		CommunicationTypeMW = prefs.getInt(COMMUNICATION_TYPE_MW, COMMUNICATION_TYPE_BT_NEW);
		// CommunicationTypeFrSky = prefs.getInt(COMMUNICATION_TYPE_FRSKY,
		// COMMUNICATION_TYPE_BT);
		SerialPortBaudRateMW = prefs.getInt(SERIAL_PORT_BAUD_RATE_MW, 115200);
		SerialPortBaudRateFrSky = prefs.getInt(SERIAL_PORT_BAUD_RATE_FRSKY, 9600);
		// MainRequestMethod = prefs.getInt(MAINREQUESTMETHOD, 2);
		FrskySupport = prefs.getBoolean(FRSKY_SUPPORT, false);
		NoDataReceievedWarning = prefs.getBoolean(NO_DATA_RECEIVED_WARNING, true);

	}

	public void SaveSettings(boolean quiet) {
		editor.putInt(RADIOMODE, RadioMode);
		editor.putInt(PROTOCOL, Protocol);
		editor.putInt(MAGMODE, MagMode);
		editor.putBoolean(TEXTTOSPEACH, TextToSpeach);
		editor.putString(MACADDERSS, MacAddress);
		editor.putString(MACADDERSSFRSKY, MacAddressFrsky);
		editor.putBoolean(CONNECTONSTART, ConnectOnStart);
		editor.putBoolean(DISABLEBTONEXIT, DisableBTonExit);
		editor.putString(FORCELANGUAGE, ForceLanguage);
		editor.putInt(PERIODICSPEAKING, PeriodicSpeaking);
		editor.putFloat(VOLTAGEALARM, VoltageAlarm);
		editor.putString(GRAPHSTOSHOW, GraphsToShow);
		editor.putInt(REFRESHRATE, RefreshRate);
		editor.putBoolean(COPYFRSKYTOMW, CopyFrskyToMW);
		editor.putInt(APPSTARTCOUNTER, AppStartCounter);
		editor.putInt(DONATEBUTTONPRESSED, DonateButtonPressed);
		editor.putBoolean(REVERSEROLL, ReverseRoll);
		editor.putFloat(MAPZOOMLEVEL, MapZoomLevel);
		editor.putInt(MAPCENTERPERIOD, MapCenterPeriod);
		editor.putInt(COMMUNICATION_TYPE_MW, CommunicationTypeMW);
		editor.putInt(SERIAL_PORT_BAUD_RATE_MW, SerialPortBaudRateMW);
		editor.putBoolean(FRSKY_SUPPORT, FrskySupport);
		editor.putBoolean(NO_DATA_RECEIVED_WARNING, NoDataReceievedWarning);
		editor.commit();

		if (!quiet) {
			Toast.makeText(getApplicationContext(), getString(R.string.Settingssaved), Toast.LENGTH_LONG).show();
			// Say(getString(R.string.Settingssaved));
		}
	}

	@Override
	public void onTerminate() {
		sensors.stop();
		mw.CloseLoggingFile();
		super.onTerminate();

	}

	public void Frequentjobs() {

		// rssi

		// Say battery level every xx seconds

		// ===================timer every 1sek===============================
		if (timer3 < System.currentTimeMillis()) {
			timer3 = System.currentTimeMillis() + timer3Freq;

			// Notifications
			if (mw.i2cError != tempLastI2CErrorCount) {
				notifications.displayNotification(getString(R.string.Warning),
						"I2C Error=" + String.valueOf(mw.i2cError), true, 1, false);
				tempLastI2CErrorCount = mw.i2cError;
			}

			if (mw.DataFlow < 0 && NoDataReceievedWarning) {
				notifications.displayNotification(getString(R.string.Warning),
						getString(R.string.NoDataRecieved) + " " + String.valueOf(mw.DataFlow), true, 1, false);
			}

			// Checkboxes speaking; ON OFF
			for (int i = 0; i < mw.CHECKBOXITEMS; i++) {
				if (mw.ActiveModes[i] != oldActiveModes[i]) {
					if (mw.ActiveModes[i]) {
					} else {
					}

					if (mw.BoxNames[i].equals("ARM")) {
						mw.ZeroConnection();
					}

				}
				oldActiveModes[i] = mw.ActiveModes[i];
			}
		}
		// --------------------END timer every 1sek---------------------------

		// ===================timer every 5sek===============================
		if (timer4 < System.currentTimeMillis()) {
			timer4 = System.currentTimeMillis() + timer4Freq;
			// update Home position
			if (commMW.Connected && Protocol < 231) {
				mw.SendRequestMSP_WP(0);

			}

			if (mw.version > 0) {
				if (mw.version > Protocol) {

				}
			}

		}
		// --------------------END timer every 5sek---------------------------
		if (FollowHeading) {
			mw.SendRequestMSP_SET_HEAD((int) sensors.Heading);
			FollowHeadingBlinkFlag = !FollowHeadingBlinkFlag;
		}

	}

	public void ForceLanguage() {
		if (!ForceLanguage.equals("")) {
			String languageToLoad = ForceLanguage;
			Locale locale = new Locale(languageToLoad);
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config, null);
		}
	}

	public void OpenInfoOnClick(View v) {
		{
			Log.d("aaa", "OpenInfoOnClick " + v.getTag().toString());
			final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(v.getTag().toString()));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

	private void showOkDialogWithText(Context context, String messageText) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(messageText);
		builder.setCancelable(true);
		builder.setPositiveButton("OK", null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public boolean checkGooglePlayServicesAvailability(Activity activity) {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		if (resultCode != ConnectionResult.SUCCESS) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity, 69);
			if (dialog != null) {
				dialog.show();
				return false;
			} else {
				showOkDialogWithText(activity,
						"Something went wrong. Please make sure that you have the Play Store installed and that you are connected to the internet.");
				return false;
			}

		}

		Log.d("GooglePlayServicesUtil Check", "Result is: " + resultCode);
		return true;
	}

	public void RestartApp() {
		Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
		int mPendingIntentId = 123456;
		PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,
				mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 200, mPendingIntent);
		System.exit(0);
	}

	@Override
	public void onSensorsStateChangeMagAcc() {

	}

	@Override
	public void onSensorsStateGPSLocationChange() {

	}

	@Override
	public void onSensorsStateGPSStatusChange() {

	}

}
