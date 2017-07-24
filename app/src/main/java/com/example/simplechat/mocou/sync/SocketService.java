package com.example.simplechat.mocou.sync;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.widget.Toast;

import com.example.simplechat.mocou.ChatApplication;
import com.example.simplechat.mocou.DialogActivity;
import com.example.simplechat.mocou.MainActivity;
import com.example.simplechat.mocou.R;
import com.example.simplechat.mocou.common.TimeUtil;
import com.example.simplechat.mocou.data.ChatContract;
import com.example.simplechat.mocou.data.ChatDbHelper;
import com.example.simplechat.mocou.data.ChatEntity;
import com.example.simplechat.mocou.data.DialogContract;
import com.example.simplechat.mocou.data.DialogDbHelper;
import com.example.simplechat.mocou.data.DialogEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketService extends Service {

    public static String NEW_MESSAGE = "sync.newMessage";
    public static String CHECK_CONNECTION = "sync.checkConnection";

    public static boolean is_connected=false;

    private SQLiteDatabase mDb;
    private Socket mSocket;

    private LoopThread loopThread;
    /*private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SocketService.CHECK_CONNECTION.equals(intent.getAction())) {
                if(!mSocket.connected()){
                    System.out.println("socket is not connected");
                    is_connected=false;
                    mSocket.connect();
                    if(MainActivity.loginStatus
                            && MainActivity.loginID_text != null
                            && !MainActivity.loginID_text.equals("")){
                        mSocket.emit("add user", MainActivity.loginID_text);
                    }
                }else{
                    System.out.println("socket is connected");
                    is_connected=true;
                }
            }
        }
    };*/

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
        mSocket.connect();

        DialogDbHelper dbHelper = new DialogDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        this.loopThread=new LoopThread();
        this.loopThread.start();

        /*
        IntentFilter intentFilter = new IntentFilter(SocketService.CHECK_CONNECTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + 3 * 1000; // 3 seconds
        Intent i = new Intent(SocketService.this, SocketService.class);
        i.setAction(SocketService.CHECK_CONNECTION);//自定义的执行定义任务的Action
        PendingIntent pendingIntent = PendingIntent.getService(SocketService.this,0,i,PendingIntent.FLAG_CANCEL_CURRENT);
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);*/
    }

    @Override
    public void onDestroy() {
        mSocket.disconnect();
        mSocket.off();
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            String toUser;
            String fromUser;
            String type;
            String content;
            String date;
            String msgId;
            try {
                JSONObject data = (JSONObject) args[0];
                toUser = data.getString("to_user");
                fromUser = data.getString("from_user");
                type = data.getString("type");
                content = data.getString("content");
                date = data.getString("date");
                msgId = data.getString("msg_id");
            } catch (JSONException e) {
                // Log.e(TAG, e.getMessage());
                e.printStackTrace();
                return;
            }

            DialogEntity dialog = new DialogEntity(msgId, toUser, fromUser, type, content, date, true, false, true);
            if (type.equals("text")) {
                //addMessage(fromUser, content);
                DialogDbHelper.addDialog(mDb, dialog);
                updateChat(dialog);

            } else if (type.equals("image")) {
                //decode base64 and store image to storage
                Bitmap bm = decodeImage(dialog.getContent());
                String newContent = saveToInternalStorage(bm, dialog.getDialogId() + ".png");
                dialog.setContent(newContent);
                DialogDbHelper.addDialog(mDb, dialog);
                updateChat(dialog);
            }
            Intent localIntent = new Intent(NEW_MESSAGE);
            LocalBroadcastManager.getInstance(SocketService.this).sendBroadcast(localIntent);

            //return checkout event
            mSocket.emit("checkout", msgId);
            sendNotification(dialog);
        }
    };

    private void updateChat(DialogEntity dialog) {
        String[] selectionArgs = {dialog.getToUsername()};
        Cursor cursor = mDb.query(
                ChatContract.ChatEntry.TABLE_NAME,
                null,
                ChatContract.ChatEntry.COLUMN_TO_USERNAME + " = ?",
                selectionArgs,
                null,
                null,
                null
        );
        String content = "";
        if (dialog.getType().equals("text")) {
            content = dialog.getContent();
        } else if (dialog.getType().equals("image")) {
            content = "[image]";
        }

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
                String time = TimeUtil.geTime();

                ContentValues cv = new ContentValues();
                cv.put(ChatContract.ChatEntry.COLUMN_TYPE, dialog.getType());
                cv.put(ChatContract.ChatEntry.COLUMN_LAST_CONTENT, content);
                cv.put(ChatContract.ChatEntry.COLUMN_TIME, time);

                String[] whereArgs = {id};
                mDb.update(ChatContract.ChatEntry.TABLE_NAME, cv, BaseColumns._ID + " = ?", whereArgs);
            }
        } else {
            String toUsername = dialog.getToUsername();
            String time = TimeUtil.geTime();
            ChatEntity chat = new ChatEntity(MainActivity.loginID_text, toUsername, dialog.getType(), content, time);
            ChatDbHelper.addChat(mDb, chat);
        }
        cursor.close();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //Toast.makeText(getApplicationContext(),R.string.connect, Toast.LENGTH_LONG).show();
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //Toast.makeText(getApplicationContext(),R.string.disconnect, Toast.LENGTH_LONG).show();
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //Toast.makeText(getApplicationContext(),R.string.error_connect, Toast.LENGTH_LONG).show();
        }
    };

    private Bitmap decodeImage(String data) {
        byte[] b = Base64.decode(data, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bmp;
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String fileName) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, fileName);
        System.out.println("my path==================" + mypath);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath() + "/" + fileName;
    }

    private void sendNotification(DialogEntity dialog) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        if (dialog.getType().equals("text")) {
            mBuilder
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle("New Message")
                    .setContentText(dialog.getContent()).
                    setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL);
        } else if (dialog.getType().equals("image")) {
            mBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle("New Message")
                    .setContentText("You have an image.")
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL);
        }

// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, DialogActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(DialogActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    private class LoopThread extends Thread{
        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!mSocket.connected()){
                    System.out.println("socket is not connected");
                    is_connected=false;
                    mSocket.connect();
                    if(MainActivity.loginStatus
                            && MainActivity.loginID_text != null
                            && !MainActivity.loginID_text.equals("")){
                        mSocket.emit("add user", MainActivity.loginID_text);
                    }
                }else{
                    System.out.println("socket is connected");
                    is_connected=true;
                }
            }
        }
    }
}
