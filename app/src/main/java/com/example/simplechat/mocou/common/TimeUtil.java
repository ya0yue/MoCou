package com.example.simplechat.mocou.common;

/**
 * Created by YaoYue on 30/3/17.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String geTimeNoS() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
        String time = df.format(date);
        return time;
    }

    public static String geTime() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss");
        String time = df.format(date);
        return time;
    }
}
