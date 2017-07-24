package com.example.simplechat.mocou.data;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.simplechat.mocou.MainActivity;
import com.example.simplechat.mocou.common.TimeUtil;

/**
 * Created by Larry on 4/8/17.
 */

public class DialogDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mocou.db";
    public static final int DATABASE_VERSION = 5;

    public DialogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_DIALOG_TABLE = "CREATE TABLE " + DialogContract.DialogEntry.TABLE_NAME + " (" +
                DialogContract.DialogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DialogContract.DialogEntry.COLUMN_DIALOG_ID + " TEXT NOT NULL," +
                DialogContract.DialogEntry.COLUMN_ORIGINATOR + " TEXT NOT NULL," +
                DialogContract.DialogEntry.COLUMN_TO_USERNAME + " TEXT NOT NULL," +
                DialogContract.DialogEntry.COLUMN_TYPE + " TEXT NOT NULL," +
                DialogContract.DialogEntry.COLUMN_CONTENT + " TEXT NOT NULL," +
                DialogContract.DialogEntry.COLUMN_TIME + " TEXT NOT NULL," +
                DialogContract.DialogEntry.COLUMN_IS_RECEIVED + " INTEGER DEFAULT 0," +
                DialogContract.DialogEntry.COLUMN_IS_MINE + " INTEGER DEFAULT 0," +
                DialogContract.DialogEntry.COLUMN_IS_SENT + " INTEGER DEFAULT 0" +
                "); ";

        db.execSQL(SQL_CREATE_DIALOG_TABLE);

        final String SQL_CREATE_CHAT_TABLE = "CREATE TABLE " + ChatContract.ChatEntry.TABLE_NAME + " (" +
                ChatContract.ChatEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ChatContract.ChatEntry.COLUMN_ORIGINATOR + " TEXT NOT NULL," +
                ChatContract.ChatEntry.COLUMN_TO_USERNAME + " TEXT NOT NULL," +
                ChatContract.ChatEntry.COLUMN_TYPE+ " TEXT NOT NULL," +
                ChatContract.ChatEntry.COLUMN_LAST_CONTENT + " TEXT NOT NULL," +
                ChatContract.ChatEntry.COLUMN_TIME+ " TEXT NOT NULL" +
                "); ";

        db.execSQL(SQL_CREATE_CHAT_TABLE);

        final String SQL_CREATE_CONTACT_TABLE = "CREATE TABLE " + ContactContract.ContactEntry.TABLE_NAME + " (" +
                ContactContract.ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ContactContract.ContactEntry.COLUMN_ORIGINATOR + " TEXT NOT NULL," +
                ContactContract.ContactEntry.COLUMN_USERNAME + " TEXT NOT NULL" +
                "); ";

        db.execSQL(SQL_CREATE_CONTACT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DialogContract.DialogEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ChatContract.ChatEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ContactContract.ContactEntry.TABLE_NAME);
        onCreate(db);
    }

    public static long addDialog(SQLiteDatabase db, DialogEntity dialog) {
        ContentValues cv = new ContentValues();
        cv.put(DialogContract.DialogEntry.COLUMN_ORIGINATOR, dialog.getOriginator());
        cv.put(DialogContract.DialogEntry.COLUMN_TO_USERNAME, dialog.getToUsername());
        cv.put(DialogContract.DialogEntry.COLUMN_TYPE, dialog.getType());
        cv.put(DialogContract.DialogEntry.COLUMN_CONTENT, dialog.getContent());
        cv.put(DialogContract.DialogEntry.COLUMN_TIME, dialog.getTime());
        cv.put(DialogContract.DialogEntry.COLUMN_DIALOG_ID, dialog.getDialogId());
        cv.put(DialogContract.DialogEntry.COLUMN_IS_RECEIVED, dialog.isReceived());
        cv.put(DialogContract.DialogEntry.COLUMN_IS_MINE, dialog.isMine());
        cv.put(DialogContract.DialogEntry.COLUMN_IS_SENT, dialog.getSent());
        return db.insert(DialogContract.DialogEntry.TABLE_NAME, null, cv);
    }

    public static void updateDialog(SQLiteDatabase db, DialogEntity dialog) {
        String[] selectionArgs = {dialog.getDialogId()};
        Cursor cursor = db.query(
                DialogContract.DialogEntry.TABLE_NAME,
                null,
                DialogContract.DialogEntry.COLUMN_DIALOG_ID + " = ?",
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));

                ContentValues cv = new ContentValues();
                cv.put(DialogContract.DialogEntry.COLUMN_IS_SENT, dialog.getSent());

                String[] whereArgs = {id};
                db.update(DialogContract.DialogEntry.TABLE_NAME, cv, BaseColumns._ID + " = ?", whereArgs);
            }
        }
        cursor.close();
    }
}
