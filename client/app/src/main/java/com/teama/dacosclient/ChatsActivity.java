package com.teama.dacosclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teama.dacosclient.data.model.Chat;
import com.teama.dacosclient.data.model.Message;
import com.teama.dacosclient.data.model.User;
import com.teama.dacosclient.ui.login.LoginActivity;

import org.libsodium.jni.NaCl;
import org.libsodium.jni.Sodium;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class ChatsActivity extends AppCompatActivity {


    private ChatRecyclerViewAdapter currentChatFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSavedChatInstance();
        setContentView(R.layout.activity_chats);
        // Not sure if creating and saving fragment here is a great solution, may lead to potential
        // unexpected crashes. In case of problems, check:
        // https://stackoverflow.com/questions/44782827/passing-changing-variables-to-recyclerview-adapter
        ChatFragment fragment = new ChatFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    // Not sure if this line is not obsolete:
                    // https://developer.android.com/guide/fragments/transactions#reordering
                    .setReorderingAllowed(true)
                    .add(R.id.chats_container, fragment, "CHAT")
                    .commit();
        }
        currentChatFragmentAdapter = fragment.getAdapter();
    }

    private void loadSavedChatInstance() {
        SharedPreferences sharedPreferences = LoginActivity.getContext()
                .getSharedPreferences("dacos", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(
                "chats",
                gson.toJson(new ArrayList<Chat>())); // DefaultValue.
        Type type = new TypeToken<ArrayList<Chat>>() {}.getType();
        Chat.setChat(gson.fromJson(json, type));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentChatFragmentAdapter.setQuery(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onStop() {
        SharedPreferences sharedPreferences = LoginActivity.getContext()
                .getSharedPreferences("dacos", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Chat.getChats());
        editor.putString("chats", json);
        editor.apply();
        super.onStop();
    }
}