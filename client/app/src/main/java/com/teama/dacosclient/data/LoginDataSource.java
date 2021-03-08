package com.teama.dacosclient.data;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.teama.dacosclient.ChatsActivity;
import com.teama.dacosclient.data.model.User;
import com.teama.dacosclient.ui.login.LoginActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    String username;

    public String getUsername() {
        return username;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    public Result<User> login(String username, String password) {
    User.setInstance(username, password);
    User user = User.getInstance();
        try {
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.getContext());
            queue.start();
            String url = "http://10.0.2.2:8000/register";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                                Log.e("nice","+");
                                Intent intent = new Intent(LoginActivity.getContext(), ChatsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                LoginActivity.getContext().startActivity(intent);

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            NetworkResponse response = error.networkResponse;
                            String errorMsg = "";
                            if(response != null && response.data != null){
                                String errorString = new String(response.data);
                                Toast.makeText(LoginActivity.getContext(), errorString,Toast.LENGTH_LONG).show();
                            }
                            Log.e("bad","-");
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", user.getUsername());
                    params.put("password", user.getPassword());
                    params.put("public_rsa_n", user.getPublicRsaN());
                    params.put("public_rsa_e", user.getRsaE());
                    params.put("g_in_big_power", user.getGInBigPower());
                    Log.i("sending ", params.toString());

                    return params;
                }

            };
            // Add the realibility on the connection.
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}