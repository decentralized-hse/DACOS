package com.teama.dacosclient.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.teama.dacosclient.R;
import com.teama.dacosclient.data.model.Chat;
import com.teama.dacosclient.data.model.Message;

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        int chatId = getIntent().getIntExtra("chat_id", 0);
        Log.d("dialog activity", "Current chat id: " + chatId);
        MessagesList messagesList = findViewById(R.id.messages_list);
        ImageLoader imageLoader = (imageView, url, payload) -> {

        };
        MessagesListAdapter<Message> adapter = new MessagesListAdapter<>("-1", imageLoader);
        messagesList.setAdapter(adapter);
        adapter.addToEnd(Chat.getChats().get(chatId).getMessages(), true);
    }
}