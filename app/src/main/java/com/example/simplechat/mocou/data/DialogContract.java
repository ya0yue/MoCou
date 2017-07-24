package com.example.simplechat.mocou.data;

import android.provider.BaseColumns;

/**
 * Created by Larry on 4/8/17.
 */

public class DialogContract {

    public static final class DialogEntry implements BaseColumns {
        public static final String TABLE_NAME = "dialog";
        public static final String COLUMN_ORIGINATOR = "originator";
        public static final String COLUMN_TO_USERNAME ="to_username";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_DIALOG_ID = "dialog_id";
        public static final String COLUMN_IS_RECEIVED = "is_received";
        public static final String COLUMN_IS_MINE = "is_mine";
        public static final String COLUMN_IS_SENT = "is_sent";
    }
}
