package com.teama.dacosclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.teama.dacosclient.data.model.User;

public class ChatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        TextView editText = (TextView) findViewById(R.id.textView2);
        editText.setText(User.getInstance().getUsername() + " " + User.getInstance().getPassword(), TextView.BufferType.EDITABLE);

    }
}