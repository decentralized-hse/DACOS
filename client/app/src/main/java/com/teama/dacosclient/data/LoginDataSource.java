package com.teama.dacosclient.data;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.teama.dacosclient.activities.ChatsActivity;
import com.teama.dacosclient.R;
import com.teama.dacosclient.data.model.User;
import com.teama.dacosclient.activities.LoginActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public void login(String username, String password) {
    User.setInstance(username, password);
    User user = User.getInstance();
        try {
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.getContext());
            queue.start();
            String url = LoginActivity.getContext()
                    .getResources().getString(R.string.server_host) + "register";
            Gson gson = new Gson();
            String publicKey = gson.toJson(user.getPublicKey());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                            Intent intent = new Intent(LoginActivity.getContext(), ChatsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ((Activity)LoginActivity.getContext()).finish();
                            LoginRepository.getInstance().saveUserInJson(user);
                            LoginActivity.getContext().startActivity(intent);

                    },
                    error -> {

                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Toast.makeText(LoginActivity.getContext(), errorString,Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", user.getUsername());
                    params.put("password", user.getPassword());
                    params.put("publicKey", publicKey);
                    Log.i("sending ", params.toString());

                    return params;
                }

            };
            // Add the realibility on the connection.
            stringRequest.setRetryPolicy
                    (new DefaultRetryPolicy(10000, 1, 1.0f));
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
            // Not returning Result, because it will be processed in queue thread.
        } catch (Exception e) {
            Toast.makeText(LoginActivity.getContext(),
                    "Error logging in", Toast.LENGTH_LONG).show();
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}