package com.quadstation.dashboard;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.quadstation.R;
import com.quadstation.app.App;

public class CompassAndLevelViewActivity extends Activity {
	private boolean killme = false;
	Handler mHandler = new Handler();
	CompassView compass;
	PitchRollView PRVp;
	PitchRollView PRVr;
	ProgressBar M1;
	ProgressBar M2;
	ProgressBar M3;
	ProgressBar M4;
	TextView TVM1;
	TextView TVM2;
	TextView TVM3;
	TextView TVM4;
	App app;
	//
	int CurentPosition = 0;
	int NextLimit = 5000;
	boolean pause = false;
	private Runnable update = new Runnable() {

		@Override
		public void run() {
			app.mw.ProcessSerialData(app.loggingON);
			PRVp.SetAngle(app.mw.angy);
			PRVr.SetAngle(app.mw.angx);

			compass.SetHeading(-app.mw.head);
			compass.SetText("");
			Log.d("Run", "Enter ");

			if (app.mw.mot[0] >= 1000)
				M1.setProgress((int) app.mw.mot[0] - 1000);
			if (app.mw.mot[1] >= 1000)
				M2.setProgress((int) app.mw.mot[1] - 1000);
			if (app.mw.mot[2] >= 1000)
				M3.setProgress((int) app.mw.mot[2] - 1000);
			if (app.mw.mot[3] >= 1000)
				M4.setProgress((int) app.mw.mot[3] - 1000);
			if (app.mw.mot[4] >= 1000)

			TVM1.setText(Integer.toString((int) app.mw.mot[0]));
			TVM2.setText(Integer.toString((int) app.mw.mot[1]));
			TVM3.setText(Integer.toString((int) app.mw.mot[2]));
			TVM4.setText(Integer.toString((int) app.mw.mot[3]));
			// baro.setText(String.format("%.2f", app.mw.alt));
			// BattVoltageTV.setText(String
			// .valueOf((float) (app.mw.bytevbat / 10.0)));
			// PowerSumTV.setText(String.valueOf(app.mw.pMeterSum));

			app.Frequentjobs();

			app.mw.SendRequest(app.MainRequestMethod);
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);

			if (app.D)
				Log.d(app.TAG, "loop " + this.getClass().getName());

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compass_and_level_view);
		compass = new CompassView(this);
		app = (App) getApplication();
		compass = (CompassView) findViewById(R.id.Mag);
		PRVp = (PitchRollView) findViewById(R.id.PitchView_Pitch);
		PRVr = (PitchRollView) findViewById(R.id.PitchView_Roll);
		M1 = (ProgressBar) findViewById(R.id.ProgressBar1);
		M2 = (ProgressBar) findViewById(R.id.ProgressBar2);
		M3 = (ProgressBar) findViewById(R.id.ProgressBar3);
		M4 = (ProgressBar) findViewById(R.id.ProgressBar4);
		TVM1 = (TextView) findViewById(R.id.textViewM1);
		TVM2 = (TextView) findViewById(R.id.textViewM2);
		TVM3 = (TextView) findViewById(R.id.textViewM3);
		TVM4 = (TextView) findViewById(R.id.textViewM4);
		M1.setMax(1000);
		M2.setMax(1000);
		M3.setMax(1000);
		M4.setMax(1000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d("Test", "OnStar");
	}

	@Override
	protected void onResume() {
		super.onResume();
		mHandler.postDelayed(update, 100);
		Log.d("Test", "OnResume");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
