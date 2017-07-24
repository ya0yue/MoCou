package com.example.simplechat.mocou;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

import com.example.simplechat.mocou.AddContactActivity;
import com.example.simplechat.mocou.data.ContactContract;
import com.example.simplechat.mocou.data.ContactDbHelper;

/**
 * Created by YaoYue on 1/4/17.
 */

public class AddContactActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    Button addContactButton;
    TextView mUsernameTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ManageActivity.getInstance().addActivity(this);
        setContentView(R.layout.activity_contact_add);

        addContactButton = (Button) findViewById(R.id.contact_add_add);
        addContactButton.setOnClickListener(new ButtonListener());
    }

    protected class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.contact_add_add:
                    mUsernameTextView=(TextView) findViewById(R.id.contact_add_id);
                    String username=mUsernameTextView.getText().toString();
                    addContact(username);
                    Toast.makeText(getApplicationContext(), "Add new contact successfully!", Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        }
    }

    private long addContact(String username) {
        ContactDbHelper dbHelper = new ContactDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ContactContract.ContactEntry.COLUMN_ORIGINATOR, MainActivity.loginID_text);
        cv.put(ContactContract.ContactEntry.COLUMN_USERNAME, username);
        return mDb.insert(ContactContract.ContactEntry.TABLE_NAME, null, cv);
    }

}
