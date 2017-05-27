package com.codebrat.youtubedemo.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.codebrat.youtubedemo.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shikhar on 27-05-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "model_database";

	// Events table name
	private static final String TABLE_MODELS = "models";

	// Events Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_VIDEO_ID = "video_id";
	private static final String KEY_TIME = "time";
	private static final String KEY_COMMENT = "comment";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_MODELS_TABLE = "CREATE TABLE " + TABLE_MODELS + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_VIDEO_ID + " TEXT, "
			+ KEY_TIME + " INTEGER, "
			+ KEY_COMMENT + " TEXT"
			+ ")";
		db.execSQL(CREATE_MODELS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MODELS);
		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing event details in database
	 * */
	public long addModel(Model model) {
		long rowId;
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_VIDEO_ID, model.getVideoId());
		values.put(KEY_TIME, model.getTime());
		values.put(KEY_COMMENT, model.getComment());

		// Inserting Row
		try {
			rowId = db.insertOrThrow(TABLE_MODELS, null, values);
		} catch (SQLiteConstraintException sce){
			return -1;
		}
		db.close(); // Closing database connection
		return rowId;
	}

	/**
	 * Getting events data from database
	 * */
	public ArrayList<Model> getAllModels(){
		ArrayList<Model> modelArrayList = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_MODELS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		if(cursor.moveToFirst()){
			do {
				Model model = new Model();
				model.setVideoId(cursor.getString(cursor.getColumnIndex(KEY_VIDEO_ID)));
				model.setTime(cursor.getInt(cursor.getColumnIndex(KEY_TIME)));
				model.setComment(cursor.getString(cursor.getColumnIndex(KEY_COMMENT)));

				modelArrayList.add(model);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		// return event
		return modelArrayList;
	}

	/**
	 * Getting event table status
	 * return true if rows are there in table
	 * */
	public int getModelsRowCount() {
		String countQuery = "SELECT * FROM " + TABLE_MODELS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();

		cursor.close();
		db.close();

		// return row count
		return rowCount;
	}

	/**
	 * Delete events tables
	 * */
	public void resetModelsTables(){
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_MODELS, null, null);
		db.close();
	}
}


