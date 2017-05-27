package com.codebrat.youtubedemo.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Shikhar on 26-05-2017.
 */

public class BasicUtils {
	private static Context mContext;

	public static void setContext(Context context){
		mContext = context;
	}
	public static void makeToastShort(String text) {
		Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
	}
}
