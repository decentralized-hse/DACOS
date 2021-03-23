package com.teama.dacosclient.services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teama.dacosclient.R;
import com.teama.dacosclient.activities.ChatsActivity;
import com.teama.dacosclient.activities.LoginActivity;
import com.teama.dacosclient.data.LoginRepository;
import com.teama.dacosclient.data.model.Chat;
import com.teama.dacosclient.data.model.Message;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LoadMessagesService extends Service
{

    private Timer timer = new Timer();
    Type responseType = new TypeToken<List<List<String>>>() {}.getType();


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
                            + "read_message?block_number=" + Chat.getCurrentBlock();
                    Gson gson = new Gson();
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("response blocks", response);
                            if (response.equals("error"))
                                return;
                            List<List<String>> responseList = gson.fromJson(response, responseType);
                            Chat.setCurrentBlock(Chat.getCurrentBlock() + responseList.size());
                            for (List<String> list : responseList)
                                for (String message : list)
                                    Message.parseMessage(message);
                        }
                    },
                            error -> {
                                Log.d("response blocks", "error");
                    });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                    // Not returning Result, because it will be processed in queue thread.
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.getContext(),
                            "Error logging in", Toast.LENGTH_LONG).show();
                }
            }
        }, 0, 10*1000);// 10 Seconds.
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

}