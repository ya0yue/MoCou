package com.example.simplechat.mocou;

import android.app.Activity;
import android.app.Application;
import android.support.v7.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by YaoYue on 29/3/17.
 */

public class ManageActivity extends AppCompatActivity {

    private List<Activity> mList = new LinkedList<Activity>();

    private static ManageActivity instance;

    public static ManageActivity getInstance() {

        if (null == instance) {
            instance = new ManageActivity();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Activity activity) {

        mList.add(activity);

    }

    public void exit() {

        try {
            for (Activity activity:mList) {
                activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }

    }

    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}