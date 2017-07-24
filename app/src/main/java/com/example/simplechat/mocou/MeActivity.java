package com.example.simplechat.mocou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by YaoYue on 29/3/17.
 */

public class MeActivity extends AppCompatActivity {
    ImageButton naviChatButton,naviContatsButton,naviMeButton;
    Button logoutButton;
    TextView showUserID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ManageActivity.getInstance().addActivity(this);
        setContentView(R.layout.activity_me);

    }

    protected void onStart(){
        super.onStart();
        logoutButton = (Button)findViewById(R.id.me_logout);
        naviChatButton = (ImageButton) findViewById(R.id.navibutton_chat);
        naviContatsButton = (ImageButton) findViewById(R.id.navibutton_contact);
        naviMeButton = (ImageButton) findViewById(R.id.navibutton_me);
        showUserID = (TextView)findViewById(R.id.me_id);

        naviChatButton.setOnClickListener(new MeActivity.ButtonListener());
        naviContatsButton.setOnClickListener(new MeActivity.ButtonListener());
        naviMeButton.setOnClickListener(new MeActivity.ButtonListener());
        logoutButton.setOnClickListener(new MeActivity.ButtonListener());
        showUserID.setText(MainActivity.loginID_text);
    }

    public class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.navibutton_chat:
                    Intent chat_intent = new Intent(MeActivity.this,ChatActivity.class);
                    startActivity(chat_intent);
                    break;
                case R.id.navibutton_contact:
                    Intent contact_intent = new Intent(MeActivity.this,ContactActivity.class);
                    startActivity(contact_intent);
                    break;
                case R.id.navibutton_me:
                    break;
                case R.id.me_logout:
                        MainActivity.loginStatus=false;

                        SharedPreferences sharedPref = MeActivity.this.getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean(getString(R.string.is_login), MainActivity.loginStatus);
                        editor.commit();

                        MainActivity.loginID_text=null;
                        Intent login_intent = new Intent(MeActivity.this,MainActivity.class);
                        startActivity(login_intent);
                    break;
            }
        }
    }
}
