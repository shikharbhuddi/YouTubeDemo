package com.codebrat.youtubedemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codebrat.youtubedemo.R;
import com.codebrat.youtubedemo.utils.BasicUtils;

public class SplashActivity extends AppCompatActivity {
	private static String TAG = SplashActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		final LinearLayout splashLayout = (LinearLayout) findViewById(R.id.activity_splash);
		final ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
		mProgressBar.setVisibility(View.VISIBLE);

		TextView tv = (TextView) findViewById(R.id.splash_text);

		Typeface face = Typeface.createFromAsset(getAssets(),
			"fonts/Cabin-Regular.ttf");
		tv.setTypeface(face);


		new Thread(new Runnable() {
			@Override
			public void run() {
				if(!isNetworkAvailable() & !isOnline()){
					Snackbar snackbar = Snackbar
						.make(splashLayout, "Internet Connection not available!", Snackbar.LENGTH_LONG);
					snackbar.getView().setBackgroundColor(ContextCompat.getColor(SplashActivity.this,
						R.color.colorPrimary));
					snackbar.show();
					mProgressBar.setVisibility(View.GONE);
				} else {
					BasicUtils.setContext(getApplicationContext());
					Intent intent = new Intent(SplashActivity.this, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					finish();
				}
			}
		}).start();
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager
			= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	public Boolean isOnline() {
		try {
			Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
			int returnVal = p1.waitFor();
			boolean reachable = (returnVal==0);
			return reachable;
		} catch (Exception e) {
			BasicUtils.makeToastShort(e.toString());
		}
		return false;
	}
}
