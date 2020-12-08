package com.example.cst2335_graphicalinterfaceprogramming;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
/**
 * The class is the main responsible the main page function
 *  @author Gulmira Kanalinova
 *  @version 1.0
 *  December 5, 2020
 */

class TheAudioDbHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "TADDB";
	private static final String DB_TABLE = "Tracks";



	//columns
	static final String ID = "_id";
	static final String ARTIST = "Artist";
	static final String TRACK_NAME = "Track";
	private static final int VERSION = 1;

	private static final String CREATE_TABLE = "CREATE TABLE "+DB_TABLE+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+ ARTIST +" TEXT,"+TRACK_NAME+" TEXT);";

	public TheAudioDbHelper(@Nullable Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int v1, int v2) {
	sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
	onCreate(sqLiteDatabase);
	}

	public Cursor getCursor(){
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "Select * from "+DB_TABLE;
		Cursor cursor = db.rawQuery(query, null);
		return cursor;
	}

	public boolean insertData(String artist, String track) {
		if (!trackExists(artist, track)) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues contentValues = new ContentValues();
			contentValues.put(ARTIST, artist);
			contentValues.put(TRACK_NAME, track);
			long result = db.insert(DB_TABLE, null, contentValues);
			return result != -1; // error if -1
		}
		return false;
	}

	protected void delete(String artist, String track)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(DB_TABLE, ARTIST + "= ?" + " AND " + TRACK_NAME + "= ?", new String[] {artist, track});
	}

	/**
	 * returns true if track exists in DB
	 * @param artist
	 * @param track
	 * @return
	 */
	private boolean trackExists(String artist, String track){
		try
		{
			SQLiteDatabase db=this.getReadableDatabase();
			String sql = "SELECT * FROM " + DB_TABLE + " WHERE "+ ARTIST + "='" + artist + "' and " + TRACK_NAME + " = '" + track + "'";
			Cursor cursor=db.rawQuery(sql,null);
			if (cursor.moveToFirst())
			{
				db.close();
				return true;//record Exists
			}
			db.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * get favorite tracks list
	 * @return
	 */
	public List<Track> getTracklist(){
		List<Track> tracks = new ArrayList<>();
		Cursor cursor = getCursor();
		cursor.moveToFirst();
		while (cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex(ID));
			String artistName = cursor.getString(cursor.getColumnIndex(ARTIST));
			String track = cursor.getString(cursor.getColumnIndex(TRACK_NAME));
			tracks.add(new Track(id, artistName, track));
		}
		cursor.close();
		return tracks;
	}

}
