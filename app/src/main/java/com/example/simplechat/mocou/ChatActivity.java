package com.example.simplechat.mocou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

//import com.example.simplechat.mocou..MoreActivity;
import com.example.simplechat.mocou.common.TimeUtil;
import com.example.simplechat.mocou.data.ChatAdapter;
import com.example.simplechat.mocou.data.ChatContract;
import com.example.simplechat.mocou.data.ChatEntity;
import com.example.simplechat.mocou.data.DialogContract;
import com.example.simplechat.mocou.data.DialogDbHelper;
import com.example.simplechat.mocou.data.DialogEntity;
import com.example.simplechat.mocou.sync.SocketService;


import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    ListView chatListView;
    ImageButton naviChatButton, naviContatsButton, naviMeButton;
    public List<ChatEntity> chatEntityList = new ArrayList<>();
    String clickedItemName;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SocketService.NEW_MESSAGE.equals(intent.getAction())) {
                //refresh list view
                updateChatView();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ManageActivity.getInstance().addActivity(this);
        setContentView(R.layout.activity_chat);

        naviChatButton = (ImageButton) findViewById(R.id.navibutton_chat);
        naviContatsButton = (ImageButton) findViewById(R.id.navibutton_contact);
        naviMeButton = (ImageButton) findViewById(R.id.navibutton_me);

        naviChatButton.setOnClickListener(new ButtonListener());
        naviContatsButton.setOnClickListener(new ButtonListener());
        naviMeButton.setOnClickListener(new ButtonListener());

        DialogDbHelper dbHelper = new DialogDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        //click a line to begin chat dialog
        this.chatListView = (ListView) this.findViewById(R.id.chat_list);
        chatListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                ChatEntity temp = (ChatEntity) ChatActivity.this.chatListView.getItemAtPosition(position);
                clickedItemName = temp.getChatToName();
                //Toast.makeText(getApplicationContext(), "Position:"+position+"/id:"+id+"/Name:"+clickedItemName, Toast.LENGTH_LONG).show();
                Intent dialog_intent = new Intent(ChatActivity.this, DialogActivity.class);
                dialog_intent.putExtra("account", temp.getChatToName());
                startActivity(dialog_intent);
            }
        });

        IntentFilter intentFilter = new IntentFilter(SocketService.NEW_MESSAGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);

        updateChatView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateChatView();
    }

    protected void updateChatView() {
        this.chatEntityList.clear();
        //retrieve data from database
        Cursor cursor = getChats();
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String toUsername = cursor.getString(cursor.getColumnIndex(DialogContract.DialogEntry.COLUMN_TO_USERNAME));
                    String type = cursor.getString(cursor.getColumnIndex(ChatContract.ChatEntry.COLUMN_TYPE));
                    String content = cursor.getString(cursor.getColumnIndex(ChatContract.ChatEntry.COLUMN_LAST_CONTENT));
                    String time = cursor.getString(cursor.getColumnIndex(ChatContract.ChatEntry.COLUMN_TIME));
                    ChatEntity entity = new ChatEntity(MainActivity.loginID_text, toUsername, type,content, time);
                    this.chatEntityList.add(entity);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();

        ChatAdapter adapter= new ChatAdapter(this, this.chatEntityList);
        this.chatListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private Cursor getChats() {
        String[] selectionArgs = {MainActivity.loginID_text};
        return mDb.query(
                ChatContract.ChatEntry.TABLE_NAME,
                null,
                ChatContract.ChatEntry.COLUMN_ORIGINATOR+ " = ?",
                selectionArgs,
                null,
                null,
                ChatContract.ChatEntry.COLUMN_TIME
        );
    }


    private class ButtonListener implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.navibutton_chat:
                    break;
                case R.id.navibutton_contact:
                    Intent contact_intent = new Intent(ChatActivity.this, ContactActivity.class);
                    startActivity(contact_intent);
                    break;
                case R.id.navibutton_me:
                    Intent me_intent = new Intent(ChatActivity.this, MeActivity.class);
                    startActivity(me_intent);
                    break;
            }
        }
    }

}
