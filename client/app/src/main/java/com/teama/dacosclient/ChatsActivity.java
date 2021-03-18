package com.teama.dacosclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.teama.dacosclient.data.model.Chat;
import com.teama.dacosclient.data.model.Message;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chats);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    // Not sure if this line is not obsolete:
                    // https://developer.android.com/guide/fragments/transactions#reordering
                    .setReorderingAllowed(true)
                    .add(R.id.chats_container, ChatFragment.class, null)
                    .commit();
        }
    }
}