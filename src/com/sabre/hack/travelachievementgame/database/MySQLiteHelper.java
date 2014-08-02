package com.sabre.hack.travelachievementgame.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_CATEGORIES = "categories";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_NAME = "name";
  public static final String COLUMN_COUNT = "count";
  
  public static final String TABLE_PLACES = "places";
  public static final String COLUMN_FB_ID = "fb_id";
  
  public static final String TABLE_ACHIEVEMENTS = "achievements";
  public static final String COLUMN_FILENAME = "filename";
  public static final String COLUMN_CATEGORY = "category";
  public static final String COLUMN_TYPE = "type";

  private static final String DATABASE_NAME = "visited.db";
  private static final int DATABASE_VERSION = 6;

  // Database creation sql statement
  private static final String CREATE_TABLE_CATEGORIES = "create table "
      + TABLE_CATEGORIES
      + "("
      + COLUMN_ID + " integer primary key autoincrement, "
      + COLUMN_NAME + " text not null, "
      + COLUMN_COUNT+ " integer)";
  private static final String CREATE_TABLE_PLACES = "create table " + TABLE_PLACES + "("
	      + COLUMN_ID + " integer primary key autoincrement, "
	      + COLUMN_NAME + " text not null, "
	      + COLUMN_FB_ID + " integer not null, "
	      + COLUMN_COUNT+ " integer)";
  private static final String CREATE_TABLE_ACHIEVEMENTS = "create table " + TABLE_ACHIEVEMENTS + "("
	      + COLUMN_ID + " integer primary key autoincrement, "
	      + COLUMN_NAME + " text not null, "
	      + COLUMN_FILENAME + " text, "
	      + COLUMN_CATEGORY + " text not null, "
	      + COLUMN_TYPE + " integer, "
	      + COLUMN_COUNT+ " integer)";

  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(CREATE_TABLE_CATEGORIES);
    database.execSQL(CREATE_TABLE_PLACES);
    database.execSQL(CREATE_TABLE_ACHIEVEMENTS);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(MySQLiteHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENTS);
    onCreate(db);
  }

} 