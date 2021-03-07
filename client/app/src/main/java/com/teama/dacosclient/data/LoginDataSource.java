package com.teama.dacosclient.data;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.teama.dacosclient.data.model.LoggedInUser;
import com.teama.dacosclient.ui.login.LoginActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.GetContext());
            String url = "http://10.0.2.2:8000/register";
            JSONObject json = new JSONObject();
            Map<String, String> jsonParams = new HashMap<String, String>();
            json.put("username","ded");
            json.put("password","password");
            json.put("public_rsa_n","23");
            json.put("public_rsa_e","32");
            json.put("g_in_big_power","44");
            final String jsonRequest = json.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {

                            Log.d("onResponse", response);

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
                                Log.i("log error", errorString);
                            }
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username","ded");
                    params.put("password","password");
                    params.put("public_rsa_n","23");
                    params.put("public_rsa_e","32");
                    params.put("g_in_big_power","44");
                    Log.i("sending ", params.toString());

                    return params;
                }

            };


            // Add the realibility on the connection.
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
// Add the request to the RequestQueue.
            queue.add(stringRequest);
            queue.wait();
            LoggedInUser fakeUser = new LoggedInUser(java.util.UUID.randomUUID().toString(), "start");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}