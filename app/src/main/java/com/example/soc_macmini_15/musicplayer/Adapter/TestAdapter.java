package com.example.soc_macmini_15.musicplayer.Adapter;

import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.soc_macmini_15.musicplayer.DB.DataBaseHelper;
import com.example.soc_macmini_15.musicplayer.Model.PlayList;
import com.example.soc_macmini_15.musicplayer.Model.Song;
import com.example.soc_macmini_15.musicplayer.Model.SongsList;

public class TestAdapter {

    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public TestAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }
    public TestAdapter createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public TestAdapter open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    public ArrayList<Song> getAllFavorites() {
        open();
        String sql ="SELECT * FROM Song WHERE isFav=true";
        Cursor cursor = mDb.rawQuery(sql, null);
        ArrayList<Song> favSongs = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                boolean value = cursor.getInt(3) > 0;
                Song song = new Song(cursor.getString(0)
                        , cursor.getString(1)
                        , cursor.getString(2)
                        , value);
                favSongs.add(song);
            }
        }
        close();
        return favSongs;
    }
    public ArrayList<Song> getAllSongs() {
        open();
        String sql ="SELECT * FROM Song";
        Cursor cursor = mDb.rawQuery(sql, null);
        ArrayList<Song> songs = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                boolean value = cursor.getInt(3) > 0;
                Song song = new Song(cursor.getString(0)
                        , cursor.getString(1)
                        , cursor.getString(2)
                        , value);
                songs.add(song);
            }
        }
        close();
        return songs;
    }
    public boolean updateFav(String title,String subtitle,String path,boolean isFav){

        try{
            open();
            mDb = mDbHelper.getWritableDatabase();
            Song song=new Song(title,subtitle,path,isFav);
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("subTitle", subtitle);
            values.put("path", path);
            values.put("isFav", isFav);
            mDb.update("Song",values,"title=?",new String[]{title});
            return true;
        }catch (Exception e){
            return false;
        }
        finally {
            close();
        }

    }


}
