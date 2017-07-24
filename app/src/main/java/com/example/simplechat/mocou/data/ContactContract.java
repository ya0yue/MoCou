package com.example.simplechat.mocou.data;

import android.provider.BaseColumns;

/**
 * Created by Larry on 4/7/17.
 */

public class ContactContract {

    public static final class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "contact";
        public static final String COLUMN_ORIGINATOR = "originator";
        public static final String COLUMN_USERNAME = "username";

    }
}
