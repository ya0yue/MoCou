package com.example.simplechat.mocou;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.Intent;
import android.app.AlertDialog;
import android.view.KeyEvent;

import com.example.simplechat.mocou.sync.SocketService;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity {

    private Socket mSocket;
    Button loginButton;
    public static boolean loginStatus=false;
    public static String loginID_text;
    public static String loginPW_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ManageActivity.getInstance().addActivity(this);
        setContentView(R.layout.login);

        Intent serviceIntent=new Intent(MainActivity.this, SocketService.class);
        startService(serviceIntent);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.on("login", onLogin);

        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        loginStatus=sharedPref.getBoolean(getString(R.string.is_login), false);
        if(loginStatus){
            loginID_text=sharedPref.getString(getString(R.string.login_username), "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loginStatus) {
            mSocket.emit("add user", loginID_text);
            Intent chat_intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(chat_intent);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!loginStatus) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                new AlertDialog.Builder(this)
                        .setTitle("系统提示")
                        .setMessage("确定要退出吗")
                        .setPositiveButton("确定", listener)
                        .setNegativeButton("取消", listener)
                        .show();
            }
        }
        return false;
    }

    private void attemptLogin() {
        EditText loginID = (EditText) findViewById(R.id.login_ID);
        EditText loginPW = (EditText) findViewById(R.id.login_PW);
        loginID_text = loginID.getText().toString();
        loginPW_text = loginPW.getText().toString();
        if (loginPW_text.equals("test")) {
            // perform the user login attempt.
            mSocket.emit("add user", loginID_text);
            System.out.println("fire add user");
        }
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    ManageActivity.getInstance().exit();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            loginStatus = true;

            SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(
                    getString(R.string.preference_file_key),Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.is_login), loginStatus);
            editor.putString(getString(R.string.login_username), loginID_text);
            editor.apply();

            Intent mainIntent = new Intent(MainActivity.this, ChatActivity.class);
            //mainIntent.putExtra("username", loginID_text);
            startActivity(mainIntent);
        }
    };

}


