package com.teama.dacosclient.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.teama.dacosclient.R;
import com.teama.dacosclient.activities.ChatsActivity;
import com.teama.dacosclient.activities.LoginActivity;
import com.teama.dacosclient.data.model.Chat;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public abstract class GetSomethingFromServerService extends Service {

    private final Timer timer = new Timer();

    // Repeat time in seconds.
    private final int repeatTime;
    private final String url;

    public GetSomethingFromServerService() {
        repeatTime = getRepeatTime();
        url = getUrl();
    }

    public abstract void execute(String response);

    public abstract String getUrl();

    /**
     * @return Repeat time in seconds.
     */
    public abstract Integer getRepeatTime();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.getContext());
                    queue.start();
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                        if (response.equals("error"))
                            return;
                        execute(response);
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
        }, 0, repeatTime * 1000); // 10 seconds.
    }
}
