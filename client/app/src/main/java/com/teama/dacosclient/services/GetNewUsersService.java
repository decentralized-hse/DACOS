package com.teama.dacosclient.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.teama.dacosclient.R;
import com.teama.dacosclient.activities.ChatsActivity;
import com.teama.dacosclient.activities.LoginActivity;
import com.teama.dacosclient.data.model.Chat;
import com.teama.dacosclient.data.model.Message;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

// TODO: untested
public class GetNewUsersService extends Service {
    public GetNewUsersService() {
    }

    private final Timer timer = new Timer();


    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.getContext());
                    queue.start();
                    String url = ChatsActivity.getActivityContext()
                            .getResources().getString(R.string.server_host)
                            + "get_users";
                    Gson gson = new Gson();
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                        Log.d("response users", response);
                        if (response.equals("error"))
                            return;
                        GetUserResponse[] responseList =
                                gson.fromJson(response, (Type) GetUserResponse[].class);
                        Set<String> usernameSet = Chat.getChats().stream().map(Chat::getUsername).collect(Collectors.toSet());
                        for (GetUserResponse object : responseList) {
                            if (!usernameSet.contains(object.getUsername()))
                                Chat.createChat(object.getUsername(),
                                        object.getPublic_key());
                        }
                    },
                            error -> {
                                Log.d("response blocks", "error");
                            });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                    // Not returning Result, because it will be processed in queue thread.
                } catch (Exception e) {
                    // No internet connection.
                }
            }
        }, 0, 60*1000);// 1 minute.
    }

    // Naming in Response.class is bad because of purpose of mapping JSON to this class.
    private class GetUserResponse {
        private String username;
        private byte[] public_key;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public byte[] getPublic_key() {
            return public_key;
        }

        public void setPublic_key(byte[] public_key) {
            this.public_key = public_key;
        }

        public GetUserResponse() {
        }
    }
}