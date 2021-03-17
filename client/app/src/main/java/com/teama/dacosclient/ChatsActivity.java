package com.teama.dacosclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ChatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    // Не уверен, нужна ли эта строчка:
                    // https://developer.android.com/guide/fragments/transactions#reordering
                    .setReorderingAllowed(true)
                    .add(R.id.chats_container, ChatFragment.class, null)
                    .commit();
        }
    }
}