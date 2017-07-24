package com.example.simplechat.mocou;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by YaoYue on 1/4/17.
 */

public class DetialContactActivity extends AppCompatActivity {
    private Button DeleteContactButton,BeginChatContactButton;
    private String DetailContactID;
    private EditText inputDetailContactID, inputDetailContactNickname,getInputDetialContactRemark;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ManageActivity.getInstance().addActivity(this);
        setContentView(R.layout.activity_contact_detial);

        DeleteContactButton = (Button) findViewById(R.id.contact_detial_delete);
        BeginChatContactButton = (Button) findViewById(R.id.contact_detial_chat);
        inputDetailContactID = (EditText) findViewById(R.id.contact_detial_id);
        inputDetailContactNickname = (EditText) findViewById(R.id.contact_detial_nickname);
        getInputDetialContactRemark = (EditText)findViewById(R.id.contact_detial_remark);

        DeleteContactButton.setOnClickListener(new ButtonListener());
        BeginChatContactButton.setOnClickListener(new ButtonListener());
    }

    public void onStart(){
        super.onStart();
        this.DetailContactID = this.getIntent().getStringExtra("account");
        inputDetailContactID.setText(DetailContactID);
    }

    protected class ButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.contact_detial_delete:
                    Toast.makeText(getApplicationContext(), "Delete new contact successfully!", Toast.LENGTH_LONG).show();
                    break;
                case R.id.contact_detial_chat:
                    Intent dialog_intent = new Intent(DetialContactActivity.this, DialogActivity.class);
                    dialog_intent.putExtra("account", DetailContactID);
                    startActivity(dialog_intent);
            }
        }
    }

}
