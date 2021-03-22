package com.teama.dacosclient.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.teama.dacosclient.R;
import com.teama.dacosclient.data.model.Chat;
import com.teama.dacosclient.data.model.Message;

import java.util.ArrayList;
import java.util.List;

public class DialogActivity extends AppCompatActivity {

    private MessagesListAdapter<Message> adapter;
    private int chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        chatId = getIntent().getIntExtra("chat_id", 0);
        Log.d("dialog activity", "Current chat id: " + chatId);

        MessageInput messageInput = findViewById(R.id.message_input);
        ImageLoader imageLoader = (imageView, url, payload) -> {
            // Empty image loader is required for MessagesListAdapter to work.
        };
        adapter = new MessagesListAdapter<>("-1", imageLoader);

        messageInput.setInputListener(input -> {
            String inp = input.toString().trim();
            if (!inp.isEmpty()) {
                Chat.getChats().get(chatId).addMessage(inp, true);
                return true;
            }
            return false;
        });

        Chat.observeChatsData(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                List<Message> updatedMessages = Chat.getChats().get(chatId).getMessages();
                if (adapter.getItemCount() - 1 != updatedMessages.size()) {
                    List<Message> toAdd = updatedMessages
                            .subList(adapter.getItemCount() - 1, updatedMessages.size());
                    for (Message message : toAdd)
                        adapter.addToStart(message, true);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        MessagesList messagesList = findViewById(R.id.messages_list);

        messagesList.setAdapter(adapter);
        adapter.addToEnd(new ArrayList<>(Chat.getChats().get(chatId).getMessages()), true);
        super.onStart();
    }

    @Override
    protected void onStop() {
        Chat.removeChatsDataObserver(this);
        // TODO: probably not the best solution, but saving onstop of chats activity doesn't detect
        //  (obviously) current changes in dialog activity.
        SharedPreferences sharedPreferences = LoginActivity.getContext()
                .getSharedPreferences("dacos", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Chat.getChats());
        editor.putString("chats", json);
        Log.d("json", "saved : " + json);
        editor.apply();
        super.onStop();
    }
}