package com.teama.dacosclient.data;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teama.dacosclient.data.model.User;
import com.teama.dacosclient.ui.login.LoginActivity;

import java.lang.reflect.Type;

import static android.content.Context.MODE_PRIVATE;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private User user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
        SharedPreferences sharedPreferences = LoginActivity.getContext()
                .getSharedPreferences("dacos", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user", null);
        Type type = new TypeToken<User>() {}.getType();
        user = gson.fromJson(json, type);
        User.setInstance(user);
    }

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository(new LoginDataSource());
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    // TODO: probably not working well, is called, when there is error in connecting to server.
    private void setLoggedInUser(User user) {
        this.user = user;
        SharedPreferences sharedPreferences = LoginActivity.getContext()
                .getSharedPreferences("dacos", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(User.getInstance());
        editor.putString("user", json);
        editor.apply();
    }

    public void login(String username, String password) {
        dataSource.login(username, password);
    }

    public void saveUserInJson(User user) {
        setLoggedInUser(user);
    }
}