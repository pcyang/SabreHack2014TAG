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
	public static final String COLUMN_RECEIVED = "received";

	private static final String DATABASE_NAME = "visited.db";
	private static final int DATABASE_VERSION = 10;

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
			+ COLUMN_COUNT+ " integer, "
			+ COLUMN_RECEIVED +" integer)";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_CATEGORIES);
		database.execSQL(CREATE_TABLE_PLACES);
		database.execSQL(CREATE_TABLE_ACHIEVEMENTS);
		database.execSQL(LOAD_ACHIEVEMENTS);

	}

	private static final String LOAD_ACHIEVEMENTS = 
			"INSERT INTO 'achievements' SELECT null AS '_id', 'just listening' AS 'name',  'music1' AS 'filename',  'CONCERT VENUE' AS 'category',  2 AS 'type',  1 AS 'count', 0 AS 'received' "
			+" UNION SELECT null, 'number 1 fan',  'music2',  'CONCERT VENUE',  2,  10, 0"
			+" UNION SELECT null, 'band member',  'music3',  'CONCERT VENUE',  2,  25, 0"
			+" UNION SELECT null, 'non native species',  'national1',  'NATIONAL-PARKS',  2,  1, 0"
			+" UNION SELECT null, 'restoration',  'national2',  'NATIONAL-PARKS',  2,  5, 0"
			+" UNION SELECT null, 'park ranger',  'national3',  'NATIONAL-PARKS',  2,  15, 0"
			+" UNION SELECT null, 'fledgling',  'outdoor1',  'OUTDOORS',  2,  1, 0"
			+" UNION SELECT null, 'nature explorer',  'outdoor2',  'OUTDOORS',  2,  10, 0"
			+" UNION SELECT null, 'back to the wild',  'outdoor3',  'OUTDOORS',  2,  25, 0"
			+" UNION SELECT null, 'a little crush',  'romantic1',  'ROMANTIC',  2,  1, 0"
			+" UNION SELECT null, 'what is love?',  'romantic2',  'ROMANTIC',  2,  5, 0"
			+" UNION SELECT null, 'destined to be',  'romantic3',  'ROMANTIC',  2,  15, 0"
			+" UNION SELECT null, 'treat yourself',  'shopping1',  'SHOPPING',  2,  1, 0"
			+" UNION SELECT null, 'shopping spree',  'shopping2',  'SHOPPING',  2,  10, 0"
			+" UNION SELECT null, 'shopaholic',  'shopping3',  'SHOPPING',  2,  25, 0"
			+" UNION SELECT null, 'in the stands',  'sports1',  'SPORTS/RECREATION/ACTIVITIES',  2,  1, 0"
			+" UNION SELECT null, 'junior varisity',  'sports2',  'SPORTS/RECREATION/ACTIVITIES',  2,  10, 0"
			+" UNION SELECT null, 'goal!',  'sports3',  'SPORTS/RECREATION/ACTIVITIES',  2,  25, 0"
			+" UNION SELECT null, 'ups and downs',  'theme1',  'THEME-PARK',  2,  1, 0"
			+" UNION SELECT null, 'amused?',  'theme2',  'THEME-PARK',  2,  5, 0"
			+" UNION SELECT null, 'thrill seeker',  'theme3',  'THEME-PARK',  2,  15, 0"
			+" UNION SELECT null, 'tourist',  'travel1',  'TRANSPORTATION',  2,  1, 0"
			+" UNION SELECT null, 'frequent flier',  'travel2',  'TRANSPORTATION',  2,  10, 0"
			+" UNION SELECT null, 'around the world',  'travel3',  'TRANSPORTATION',  2,  25, 0"
;

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