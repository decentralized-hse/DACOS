package com.teama.dacosclient.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teama.dacosclient.data.model.Message;
import com.teama.dacosclient.fragments.ChatFragment;
import com.teama.dacosclient.R;
import com.teama.dacosclient.adapters.ChatRecyclerViewAdapter;
import com.teama.dacosclient.data.model.Chat;
import com.teama.dacosclient.services.GetLoadMessagesService;
import com.teama.dacosclient.services.GetNewUsersService;
import com.teama.dacosclient.services.GetSomethingFromServerService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatsActivity extends AppCompatActivity
        implements ChatRecyclerViewAdapter.OnChatListener {


    private ChatRecyclerViewAdapter currentChatFragmentAdapter;
    private static ChatsActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        loadSavedChatInstance();
        setContentView(R.layout.activity_chats);
        String serverUrl = getActivityContext()
                .getResources().getString(R.string.server_host);
        context.startService(new Intent(this, GetLoadMessagesService.class));
        context.startService(new Intent(this, GetNewUsersService.class));
        //Chat.generateDummyChats();

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
        Chat.setCurrentBlock(sharedPreferences.getInt("current_block", 0));
        Log.i("", "loadSavedChatInstance: " + json);
        Type type = new TypeToken<ArrayList<Chat>>() {
        }.getType();
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
    protected void onPause() {
        Chat.saveChatsInJson();
        super.onPause();
    }

    @Override
    public void onChatClick(int chatId) {
        Intent intent = new Intent(this, DialogActivity.class);
        intent.putExtra("chat_id", chatId);
        startActivity(intent);
    }

    public static ChatsActivity getActivityContext() {
        return context;
    }

    // TODO: needs to be completed after creating servers structure on client side.
    private Intent getNewServersService(String serviceUrl) {
        return new Intent(context, new GetSomethingFromServerService() {
            @Override
            public void execute(String response) {
                return;
            }

            @Override
            public String getUrl() {
                return serviceUrl + "get_servers";
            }

            @Override
            public Integer getRepeatTime() {
                return 60;
            }
        }.getClass());
    }
}