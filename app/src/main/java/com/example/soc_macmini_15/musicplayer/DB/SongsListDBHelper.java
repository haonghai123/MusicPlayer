package com.example.soc_macmini_15.musicplayer.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.soc_macmini_15.musicplayer.Model.Song;

import java.util.ArrayList;

public class SongsListDBHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "MusicApp"; // Database name
    private static int DB_VERSION = 1; // Database version
    private final Context mContext;
    private SongsListDBHelper dbHelper;
    public SongsListDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }
    private void open(){
        dbHelper=new SongsListDBHelper(mContext);
    }
    public ArrayList<Song> getAllSongsList(String name) {
        open();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql ="SELECT * FROM SongsList WHERE playlistName='"+name+"'";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Song> playLists = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                boolean value = cursor.getInt(3) > 0;
                Song songsList=new Song(cursor.getString(0)
                        , cursor.getString(1)
                        , cursor.getString(2)
                        , value);
                playLists.add(songsList);
            }
        }
        db.close();
        return playLists;
    }
    public void deleteSongsList(String name, String PlayListName) {
        open();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("SongsList","title=? and playlistName=?", new String[]{name,PlayListName});
        db.close();
    }
    public ArrayList<Song> getSearchedSongsToAdd(String name) {
        open();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql ="SELECT * FROM Song WHERE title NOT IN (SELECT title FROM SongsList WHERE playlistName='"+name+"')";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Song> playLists = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                boolean value = cursor.getInt(3) > 0;
                Song songsList=new Song(cursor.getString(0)
                        , cursor.getString(1)
                        , cursor.getString(2)
                        , value);
                playLists.add(songsList);
            }
        }
        db.close();
        return playLists;
    }
    public void addSongsList(Song song, String name){
        open();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", song.getTitle());
        values.put("subTitle", song.getSubTitle());
        values.put("path", song.getPath());
        values.put("isFav", song.isFav());
        values.put("playlistName",name);
        db.insert("SongsList", null, values);
        db.close();
    }
    public int getSizeOfPlaylist(String name){
        open();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT COUNT(*) FROM SongsList WHERE playlistName='"+name+"'",null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
