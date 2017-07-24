package com.example.simplechat.mocou;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.simplechat.mocou.common.TimeUtil;
import com.example.simplechat.mocou.data.ChatContract;
import com.example.simplechat.mocou.data.ChatDbHelper;
import com.example.simplechat.mocou.data.ChatEntity;
import com.example.simplechat.mocou.data.DialogAdapter;
import com.example.simplechat.mocou.data.DialogContract;
import com.example.simplechat.mocou.data.DialogDbHelper;
import com.example.simplechat.mocou.data.DialogEntity;
import com.example.simplechat.mocou.sync.SocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

/**
 * Created by YaoYue on 30/3/17.
 */

public class DialogActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;

    private SQLiteDatabase mDb;
    private ListView dialogListView;
    private List<DialogEntity> dialogEntityList = new ArrayList<>();
    private String toUsername;
    protected TextView showDialogToAccount;
    protected ImageButton diaOpenFile, diaSend;
    protected Button diaBack;
    protected EditText preSentText;

    private Socket mSocket;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SocketService.NEW_MESSAGE.equals(intent.getAction())) {
                //refresh list view
                updateDialogView();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(1);
        ManageActivity.getInstance().addActivity(this);
        setContentView(R.layout.activity_dialog);

        this.toUsername = this.getIntent().getStringExtra("account");
        this.dialogListView = (ListView) this.findViewById(R.id.dialog_list);

        DialogDbHelper dbHelper = new DialogDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();

        IntentFilter intentFilter = new IntentFilter(SocketService.NEW_MESSAGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);

        showDialogToAccount = (TextView) findViewById(R.id.dialog_dialogToName);
        showDialogToAccount.setText(toUsername);
        diaBack = (Button) findViewById(R.id.dialog_back);
        diaOpenFile = (ImageButton) findViewById(R.id.dialog_openfile);
        diaSend = (ImageButton) findViewById(R.id.dialog_send);
        diaBack.setOnClickListener(new ButtonListener());
        diaOpenFile.setOnClickListener(new ButtonListener());
        diaSend.setOnClickListener(new ButtonListener());

        updateDialogView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = selectedImageUri.toString();

                Bitmap bm = null;
                try {
                    bm = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(selectedImagePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String type = "image";
                String dialogId = MainActivity.loginID_text + "-" + toUsername + "-" + System.currentTimeMillis();
                String fileName = dialogId + ".png";
                String content = saveToInternalStorage(bm, fileName);

                DialogEntity dialog = new DialogEntity(
                        dialogId,
                        MainActivity.loginID_text,
                        toUsername,
                        type,
                        content,
                        TimeUtil.geTime(),
                        false,
                        true,
                        false
                );
                if(SocketService.is_connected){
                    dialog.setSent(true);
                    String encodedStr = encodeImage(bm);
                    sendImage(dialog, encodedStr);
                }
                DialogDbHelper.addDialog(mDb, dialog); // add to local database
                updateDialogView(); // update to view
                updateChat(dialog);
            }
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String fileName) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, fileName);
        System.out.println("my path=================="+mypath);

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
        return directory.getAbsolutePath()+"/"+fileName;
    }

    private class ButtonListener implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_back:
                    finish();
                    break;
                case R.id.dialog_openfile:
                    Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    imageIntent.setType("image/*");
                    startActivityForResult(Intent.createChooser(imageIntent, "Select File"), SELECT_PICTURE);

                    break;
                case R.id.dialog_send:
                    preSentText = (EditText) findViewById(R.id.dialog_content);
                    String type = "text";
                    String content = preSentText.getText().toString();
                    String dialogId = MainActivity.loginID_text + "-" + toUsername + "-" + System.currentTimeMillis();
                    DialogEntity dialog = new DialogEntity(
                            dialogId,
                            MainActivity.loginID_text,
                            toUsername,
                            type,
                            content,
                            TimeUtil.geTime(),
                            false,
                            true,
                            false
                    );
                    if(SocketService.is_connected){
                        dialog.setSent(true);
                        attemptSend(dialog);
                    }
                    DialogDbHelper.addDialog(mDb, dialog); // add to local database
                    updateDialogView(); // update to view
                    preSentText.setText("");
                    updateChat(dialog);
                    break;
            }
        }
    }

    public void updateDialogView() {
        this.dialogEntityList.clear();
        //retrieve data from database

        String[] selectionArgs = {toUsername};
        Cursor cursor = getDialogs(selectionArgs);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String dialogId = cursor.getString(cursor.getColumnIndex(DialogContract.DialogEntry.COLUMN_DIALOG_ID));
                    String type = cursor.getString(cursor.getColumnIndex(DialogContract.DialogEntry.COLUMN_TYPE));
                    String content = cursor.getString(cursor.getColumnIndex(DialogContract.DialogEntry.COLUMN_CONTENT));
                    String time = cursor.getString(cursor.getColumnIndex(DialogContract.DialogEntry.COLUMN_TIME));
                    boolean isReceived = cursor.getInt(cursor.getColumnIndex(DialogContract.DialogEntry.COLUMN_IS_RECEIVED)) > 0;
                    boolean isMine = cursor.getInt(cursor.getColumnIndex(DialogContract.DialogEntry.COLUMN_IS_MINE)) > 0;
                    boolean isSent = cursor.getInt(cursor.getColumnIndex(DialogContract.DialogEntry.COLUMN_IS_SENT)) > 0;
                    DialogEntity entity = new DialogEntity(
                            dialogId,
                            MainActivity.loginID_text,
                            toUsername,
                            type,
                            content,
                            time,
                            isReceived,
                            isMine,
                            isSent
                    );
                    this.dialogEntityList.add(entity);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();

        DialogAdapter adapter = new DialogAdapter(this, this.dialogEntityList);
        this.dialogListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private Cursor getDialogs(String[] selectionArgs) {
        return mDb.query(
                DialogContract.DialogEntry.TABLE_NAME,
                null,
                DialogContract.DialogEntry.COLUMN_TO_USERNAME + " = ?",
                selectionArgs,
                null,
                null,
                DialogContract.DialogEntry.COLUMN_TIME
        );
    }

    private void attemptSend(DialogEntity entity) {
        /*
        if (!mSocket.connected() || !NetworkUtil.isNetworkConnected(this)) {
            // change the isReceived to false
            ContentValues cv =new ContentValues();

            mDb.update(DialogContract.DialogEntry.TABLE_NAME, );
            return;
        }
        */

        // Sending an object
        JSONObject obj = new JSONObject();
        try {
            obj.put("to_user", entity.getToUsername());
            obj.put("from_user", MainActivity.loginID_text);
            obj.put("type", "text");
            obj.put("content", entity.getContent());
            obj.put("date", entity.getTime());
            obj.put("msg_id", entity.getDialogId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // perform the sending message attempt.
        mSocket.emit("new message", obj);
    }

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
        String content="";
        if(dialog.getType().equals("text")){
            content = dialog.getContent();
        }else if(dialog.getType().equals("image")){
            content="[image]";
        }

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
                String time = TimeUtil.geTime();

                ContentValues cv = new ContentValues();
                cv.put(ChatContract.ChatEntry.COLUMN_LAST_CONTENT, content);
                cv.put(ChatContract.ChatEntry.COLUMN_TIME, time);

                String[] whereArgs = {id};
                mDb.update(ChatContract.ChatEntry.TABLE_NAME, cv, BaseColumns._ID + " = ?", whereArgs);
            }
        } else {
            String toUsername = dialog.getToUsername();
            String time = TimeUtil.geTime();
            ChatEntity chat = new ChatEntity(MainActivity.loginID_text, toUsername,dialog.getType(), content, time);
            ChatDbHelper.addChat(mDb, chat);
        }
        cursor.close();
    }

    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] b = baos.toByteArray();
        //Base64.de
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private void sendImage(DialogEntity dialog, String encodedImageStr){
        JSONObject obj = new JSONObject();
        try {
            obj.put("to_user", dialog.getToUsername());
            obj.put("from_user", MainActivity.loginID_text);
            obj.put("type", "image");  //send image
            obj.put("content", encodedImageStr);
            obj.put("date", dialog.getTime());
            obj.put("msg_id", dialog.getDialogId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // perform the sending message attempt.
        mSocket.emit("new message", obj);
    }

    public void sendOfflineMsg(DialogEntity dialog){
        System.out.println("click offline button");
        if(!dialog.getSent()){
            if(SocketService.is_connected){
                dialog.setSent(true);
                attemptSend(dialog);
                //update db
                DialogDbHelper.updateDialog(mDb,dialog);
                updateDialogView();
            }
        }
    }
}
