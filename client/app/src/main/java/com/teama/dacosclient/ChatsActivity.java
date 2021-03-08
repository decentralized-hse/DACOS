package com.teama.dacosclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        String count = getIntent().getStringExtra("username");

        TextView editText = (TextView) findViewById(R.id.textView2);
        editText.setText(count, TextView.BufferType.EDITABLE);
        Toast.makeText(getApplicationContext(), count, Toast.LENGTH_LONG).show();

    }
}