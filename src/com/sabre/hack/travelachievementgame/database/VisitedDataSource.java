package com.sabre.hack.travelachievementgame.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class VisitedDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] categoryColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_COUNT };
	private String[] placeColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_FB_ID,
			MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_COUNT };
	private String[] achievementColumns = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_NAME,
			MySQLiteHelper.COLUMN_FILENAME, MySQLiteHelper.COLUMN_CATEGORY, MySQLiteHelper.COLUMN_TYPE, MySQLiteHelper.COLUMN_COUNT };

	public VisitedDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Category createCategory(String category, int count) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, category);
		values.put(MySQLiteHelper.COLUMN_COUNT, count);
		long insertId = database.insert(MySQLiteHelper.TABLE_CATEGORIES, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CATEGORIES,
				categoryColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Category newCategory = cursorToCategory(cursor);
		cursor.close();
		return newCategory;
	}

	public void deleteCategory(Category category) {
		long id = category.getId();
		Log.d("Database", "Comment deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_CATEGORIES, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Category> getAllCategories() {
		List<Category> categories = new ArrayList<Category>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_CATEGORIES,
				categoryColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Category category = cursorToCategory(cursor);
			categories.add(category);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return categories;
	}

	private Category cursorToCategory(Cursor cursor) {
		if(cursor == null)
			return null;
		Category category = new Category();
		category.setId(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
		category.setName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NAME)));
		category.setCount(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COUNT)));
		return category;
	}
	
	public void incrementCategory(String name)
	{
		Category category = findCategory(name);
		if(category == null)
		{
			createCategory(name, 1);
		}
		else
		{
			ContentValues args = new ContentValues();
			args.put(MySQLiteHelper.COLUMN_COUNT, category.getCount()+1);
			database.update(MySQLiteHelper.TABLE_CATEGORIES, args , MySQLiteHelper.COLUMN_ID
				+ " = " + category.getId(), null);
		}
	}
	
	public Category findCategory(String name)
	{
		Category result = null;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CATEGORIES,
				categoryColumns, MySQLiteHelper.COLUMN_NAME + "='" + name + "'", null,
				null, null, MySQLiteHelper.COLUMN_NAME+" COLLATE NOCASE");
		if(cursor.moveToFirst())
			result = cursorToCategory(cursor);
		cursor.close();
		return result;
	}

	public Place createPlace(String place, int count, String facebook_id) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, place);
		values.put(MySQLiteHelper.COLUMN_COUNT, count);
		values.put(MySQLiteHelper.COLUMN_FB_ID, facebook_id);
		long insertId = database.insert(MySQLiteHelper.TABLE_PLACES, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_PLACES,
				placeColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Place newPlace = cursorToPlace(cursor);
		cursor.close();
		return newPlace;
	}

	public void deletePlace(Place place) {
		long id = place.getId();
		Log.d("Database", "Comment deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_PLACES, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Place> getAllPlaces() {
		List<Place> places = new ArrayList<Place>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_PLACES,
				placeColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Place place = cursorToPlace(cursor);
			places.add(place);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return places;
	}

	private Place cursorToPlace(Cursor cursor) {
		if(cursor == null)
			return null;
		Place place = new Place();
		place.setId(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
		place.setFb_id(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_FB_ID)));
		place.setName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NAME)));
		place.setCount(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COUNT)));
		return place;
	}
	
	public void incrementPlace(String name, String facebook_id)
	{
		Place place = findPlaceByFB_ID(facebook_id);
		if(place == null)
		{
			createPlace(name, 1, facebook_id);
		}
		else
		{
			ContentValues args = new ContentValues();
			args.put(MySQLiteHelper.COLUMN_COUNT, place.getCount()+1);
			database.update(MySQLiteHelper.TABLE_PLACES, args , MySQLiteHelper.COLUMN_FB_ID
				+ " = '" + facebook_id +"'", null);
		}
	}
	
	public Place findPlaceByFB_ID(String facebook_id)
	{
		Place result = null;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_PLACES,
				placeColumns, MySQLiteHelper.COLUMN_FB_ID + "='" + facebook_id + "'", null,
				null, null, null);
		if(cursor.moveToFirst())
			result = cursorToPlace(cursor);
		cursor.close();
		return result;
	}
	
	public Achievement createAchievement(String name, String filename, String category, int type, int count) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, name);
		values.put(MySQLiteHelper.COLUMN_FILENAME, filename);
		values.put(MySQLiteHelper.COLUMN_CATEGORY, category);
		values.put(MySQLiteHelper.COLUMN_TYPE, type);
		values.put(MySQLiteHelper.COLUMN_COUNT, count);
		long insertId = database.insert(MySQLiteHelper.TABLE_ACHIEVEMENTS, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ACHIEVEMENTS,
				achievementColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Achievement newAchievement = cursorToAchievement(cursor);
		cursor.close();
		return newAchievement;
	}

	public void deleteAchievement(Achievement achievement) {
		long id = achievement.getId();
		Log.d("Database", "Comment deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_ACHIEVEMENTS, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Achievement> getAllAchievements() {
		List<Achievement> achievements = new ArrayList<Achievement>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_ACHIEVEMENTS,
				achievementColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Achievement achievement = cursorToAchievement(cursor);
			achievements.add(achievement);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return achievements;
	}

	private Achievement cursorToAchievement(Cursor cursor) {
		if(cursor == null)
			return null;
		Achievement achievement = new Achievement();
		achievement.setId(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
		achievement.setName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NAME)));
		achievement.setCount(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COUNT)));
		return achievement;
	}
} 