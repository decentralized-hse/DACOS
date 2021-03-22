package com.teama.dacosclient.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.teama.dacosclient.R;
import com.teama.dacosclient.data.model.Chat;
import com.teama.dacosclient.data.model.Message;

import java.util.List;

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        int chatId = getIntent().getIntExtra("chat_id", 0);
        Log.d("dialog activity", "Current chat id: " + chatId);
        MessagesList messagesList = findViewById(R.id.messages_list);
        ImageLoader imageLoader = (imageView, url, payload) -> {
            // Empty image loader is required for MessagesListAdapter to work.
        };
        MessageInput messageInput = findViewById(R.id.message_input);
        messageInput.setInputListener(input -> {
            String inp = input.toString().trim();
            if (!inp.isEmpty()) {
                Chat.getChats().get(chatId).addMessage(inp, true);
                return true;
            }
            return false;
        });
        MessagesListAdapter<Message> adapter = new MessagesListAdapter<>("-1", imageLoader);
        messagesList.setAdapter(adapter);
        adapter.addToEnd(Chat.getChats().get(chatId).getMessages(), true);
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
    protected void onStop() {
        Chat.removeChatsDataObserver(this);
        super.onStop();
    }
}