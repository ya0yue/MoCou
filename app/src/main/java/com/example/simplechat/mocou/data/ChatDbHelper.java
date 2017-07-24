package com.example.simplechat.mocou.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Larry on 4/8/17.
 */

public class ChatDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mocou.db";
    //private static final int DATABASE_VERSION = 5;

    public ChatDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DialogDbHelper.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CHAT_TABLE = "CREATE TABLE " + ChatContract.ChatEntry.TABLE_NAME + " (" +
                ChatContract.ChatEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ChatContract.ChatEntry.COLUMN_ORIGINATOR + " TEXT NOT NULL," +
                ChatContract.ChatEntry.COLUMN_TO_USERNAME + " TEXT NOT NULL," +
                ChatContract.ChatEntry.COLUMN_TYPE+ " TEXT NOT NULL," +
                ChatContract.ChatEntry.COLUMN_LAST_CONTENT + " TEXT NOT NULL," +
                ChatContract.ChatEntry.COLUMN_TIME+ " TEXT NOT NULL" +
                "); ";

        db.execSQL(SQL_CREATE_CHAT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ChatContract.ChatEntry.TABLE_NAME);
        onCreate(db);
    }

    public static long addChat(SQLiteDatabase db, ChatEntity chat) {
        ContentValues cv = new ContentValues();
        cv.put(DialogContract.DialogEntry.COLUMN_ORIGINATOR, chat.getOriginator());
        cv.put(ChatContract.ChatEntry.COLUMN_TO_USERNAME, chat.getChatToName());
        cv.put(ChatContract.ChatEntry.COLUMN_TYPE, chat.getType());
        cv.put(ChatContract.ChatEntry.COLUMN_LAST_CONTENT, chat.getchatContent());
        cv.put(ChatContract.ChatEntry.COLUMN_TIME, chat.getTime());
        return db.insert(ChatContract.ChatEntry.TABLE_NAME, null, cv);
    }
}
