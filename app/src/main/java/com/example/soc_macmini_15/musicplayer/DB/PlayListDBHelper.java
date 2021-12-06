package com.example.soc_macmini_15.musicplayer.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.soc_macmini_15.musicplayer.Model.PlayList;

import java.util.ArrayList;

public class PlayListDBHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "MusicApp"; // Database name
    private static int DB_VERSION = 1; // Database version
    private final Context mContext;
    private PlayListDBHelper dbHelper;
    public PlayListDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }
    private void open(){
        dbHelper=new PlayListDBHelper(mContext);
    }
    public ArrayList<PlayList> getAllPlaylists() {
        open();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql ="SELECT * FROM PlayList";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<PlayList> playLists = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                PlayList playList=new PlayList(cursor.getString(0),cursor.getInt(1));
                playLists.add(playList);
            }
        }
        db.close();
        return playLists;
    }
    public void deletePlayList(String name) {
        open();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("SongsList","playlistName=?",new String[]{name});
        db.delete("PlayList","name=?", new String[]{name});
        db.close();

    }
    public void addPlayList(String name){
        open();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("size",0);
        db.insert("PlayList", null, values);
        db.close();
    }
    public int checkPlaylistExist(String name){
        open();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM Playlist WHERE name='"+name+"'",null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    public void increaseSize(String name){
        open();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE PlayList SET size=size+1 WHERE name='"+name+"'");
        db.close();
    }
    public void decreaseSize(String name){
        open();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE PlayList SET size=size-1 WHERE name='"+name+"'");
        db.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE PlayList (name STRING PRIMARY KEY, size INT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS PlayList");
        onCreate(db);
    }
}
