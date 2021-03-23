package com.teama.dacosclient.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.teama.dacosclient.R;
import com.teama.dacosclient.data.model.Chat;
import com.teama.dacosclient.data.model.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogActivity extends AppCompatActivity {

    private MessagesListAdapter<Message> adapter;
    private int chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        chatId = getIntent().getIntExtra("chat_id", 0);
        Log.d("dialog activity", "Current chat id: " + chatId);

        ImageLoader imageLoader = (imageView, url, payload) -> {
            // Empty image loader is required for MessagesListAdapter to work.
        };
        adapter = new MessagesListAdapter<>("-1", imageLoader);
        // TODO: in case of any bugs check setMessageInputListener() - it hasn't been tested.
        setMessageInputListener();
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
        Chat.saveChatsInJson();
        super.onStop();
    }

    private void setMessageInputListener() {
        MessageInput messageInput = findViewById(R.id.message_input);
        messageInput.setInputListener(input -> {
            String inp = input.toString().trim();
            if (!inp.isEmpty()) {
                Message addedMessage = Chat.getChats().get(chatId).addMessage(inp, true,
                        new Date(System.currentTimeMillis()));

                try {
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.getContext());
                    queue.start();
                    String url = ChatsActivity.getActivityContext()
                            .getResources().getString(R.string.server_host) + "write_msg";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            response -> Log.d("Server", "message sended: " + response),
                            error -> {
                        adapter.delete(addedMessage);
                        Toast.makeText(this, "Error sending message",
                                Toast.LENGTH_LONG).show();
                    }) {
                        @Override
                        protected Map<String, String> getParams() {

                            Map<String, String> params = new HashMap<String, String>();
                            params.put("message",
                                    Message.encodeMessage(addedMessage, Chat.getChats().get(chatId)));
                            Log.d("sending ", params.toString());

                            return params;
                        }

                    };
                    queue.add(stringRequest);
                    // Not returning Result, because it will be processed in queue thread.
                } catch (Exception e) {
                    Toast.makeText(this,
                            "Error sending message", Toast.LENGTH_LONG).show();
                }
                return true;
            }
            return false;
        });
    }
}