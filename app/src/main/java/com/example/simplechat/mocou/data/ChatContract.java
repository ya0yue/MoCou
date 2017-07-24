package com.example.simplechat.mocou.data;

import android.provider.BaseColumns;

/**
 * Created by Larry on 4/8/17.
 */

public class ChatContract {

    public static final class ChatEntry implements BaseColumns {
        public static final String TABLE_NAME = "chat";
        public static final String COLUMN_ORIGINATOR = "originator";
        public static final String COLUMN_TO_USERNAME ="to_username";
        public static final String COLUMN_TYPE="type";
        public static final String COLUMN_LAST_CONTENT = "last_content";
        public static final String COLUMN_TIME = "time";

    }
}
