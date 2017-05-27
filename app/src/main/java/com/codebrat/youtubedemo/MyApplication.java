package com.codebrat.youtubedemo;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Shikhar on 26-05-2017.
 */

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Fabric.with(this, new Crashlytics());
	}
}
