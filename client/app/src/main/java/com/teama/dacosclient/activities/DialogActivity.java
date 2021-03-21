package com.teama.dacosclient.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.teama.dacosclient.R;

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        Integer chatId = getIntent().getIntExtra("chat_id", 0);
        Log.d("dialog activity", "Current chat id: " + chatId);
    }
}