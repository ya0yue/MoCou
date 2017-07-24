package com.example.simplechat.mocou;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ListView;

import com.example.simplechat.mocou.data.ContactAdapter;
import com.example.simplechat.mocou.data.ContactContract;
import com.example.simplechat.mocou.data.ContactDbHelper;
import com.example.simplechat.mocou.data.ContactEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YaoYue on 29/3/17.
 */

public class ContactActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    ImageButton naviChatButton, naviContatsButton, naviMeButton;
    Button addContatButton;
    private String contactID, contactNickname, clickedItemName, clickedItemID;
    ListView contactListView;
    public List<ContactEntity> contactEntityList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ManageActivity.getInstance().addActivity(this);
        setContentView(R.layout.activity_contact);

        naviChatButton = (ImageButton) findViewById(R.id.navibutton_chat);
        naviContatsButton = (ImageButton) findViewById(R.id.navibutton_contact);
        naviMeButton = (ImageButton) findViewById(R.id.navibutton_me);
        addContatButton = (Button) findViewById(R.id.contact_add);
        contactListView = (ListView) findViewById(R.id.contact_list);

        naviChatButton.setOnClickListener(new ContactActivity.ButtonListener());
        naviContatsButton.setOnClickListener(new ContactActivity.ButtonListener());
        naviMeButton.setOnClickListener(new ContactActivity.ButtonListener());
        addContatButton.setOnClickListener(new ContactActivity.ButtonListener());

        updateContactView();

        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                ContactEntity temp = (ContactEntity) contactListView.getItemAtPosition(position);
                clickedItemID = temp.username;
                Intent dialog_intent = new Intent(ContactActivity.this, DetialContactActivity.class);
                dialog_intent.putExtra("account", clickedItemID);
                startActivity(dialog_intent);
            }
        });
    }

    private Cursor getContacts(){
        String[] selectionArgs={MainActivity.loginID_text};
        return mDb.query(
                ContactContract.ContactEntry.TABLE_NAME,
                null,
                ContactContract.ContactEntry.COLUMN_ORIGINATOR + " = ?",
                selectionArgs,
                null,
                null,
                ContactContract.ContactEntry.COLUMN_USERNAME
        );
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
        updateContactView();
    }

    protected void updateContactView() {
        this.contactEntityList.clear();
        //retrieve data from database
        ContactDbHelper dbHelper = new ContactDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        Cursor cursor = getContacts();
        if(cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_USERNAME));
                    ContactEntity entity=new ContactEntity(MainActivity.loginID_text, name);
                    this.contactEntityList.add(entity);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        ContactAdapter adapter=new ContactAdapter(this, this.contactEntityList);
        this.contactListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.navibutton_chat:
                    Intent chat_intent = new Intent(ContactActivity.this, ChatActivity.class);
                    startActivity(chat_intent);
                    break;
                case R.id.navibutton_contact:
                    break;
                case R.id.navibutton_me:
                    Intent me_intent = new Intent(ContactActivity.this, MeActivity.class);
                    startActivity(me_intent);
                    break;
                case R.id.contact_add:
                    Intent addContact_intent = new Intent(ContactActivity.this, AddContactActivity.class);
                    startActivity(addContact_intent);
                    break;
            }
        }
    }
}
