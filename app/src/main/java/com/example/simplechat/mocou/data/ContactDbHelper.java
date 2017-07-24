package com.example.simplechat.mocou.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.simplechat.mocou.data.ContactContract.*;

/**
 * Created by Larry on 4/7/17.
 */

public class ContactDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mocou.db";
    //private static final int DATABASE_VERSION = 5;

    public ContactDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DialogDbHelper.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_CONTACT_TABLE = "CREATE TABLE " + ContactEntry.TABLE_NAME + " (" +
                ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ContactEntry.COLUMN_ORIGINATOR + " TEXT NOT NULL," +
                ContactEntry.COLUMN_USERNAME + " TEXT NOT NULL" +
                //WaitlistEntry.COLUMN_PARTY_SIZE + " INTEGER NOT NULL, " +
                //WaitlistEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_CONTACT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ContactEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
