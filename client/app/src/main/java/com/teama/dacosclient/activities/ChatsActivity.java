package com.teama.dacosclient.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.goterl.lazycode.lazysodium.LazySodiumAndroid;
import com.goterl.lazycode.lazysodium.SodiumAndroid;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.SecretBox;
import com.goterl.lazycode.lazysodium.utils.Key;
import com.goterl.lazycode.lazysodium.utils.KeyPair;
import com.teama.dacosclient.data.model.User;
import com.teama.dacosclient.fragments.ChatFragment;
import com.teama.dacosclient.R;
import com.teama.dacosclient.adapters.ChatRecyclerViewAdapter;
import com.teama.dacosclient.data.model.Chat;
import com.teama.dacosclient.services.GetNewUsersService;
import com.teama.dacosclient.services.LoadMessagesService;

import java.lang.reflect.Type;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

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

        Intent loadMessagesService = new Intent(context, LoadMessagesService.class);
        Intent getUsersService = new Intent(context, GetNewUsersService.class);
        // Contingently this is test 1
        {
            // TODO: test if serialization works on current keygen or not.
            KeyPairGenerator kpg = null;
            KeyPair kp = null;
            LazySodiumAndroid sodium = null;
            try {
                kpg = KeyPairGenerator.getInstance("RSA");
                sodium = new LazySodiumAndroid(new SodiumAndroid());
                kp = sodium.cryptoBoxKeypair();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String encodedMessage = sodium.cryptoBoxSealEasy("text", kp.getPublicKey());
                Log.d("encodedMessage", encodedMessage);
                String decodedMessage = sodium.cryptoBoxSealOpenEasy(encodedMessage, kp);
                Log.d("decodedMessage", decodedMessage);
            } catch (SodiumException e) {
                e.printStackTrace();
            }
        }
        {
            KeyPair kp = null;
            LazySodiumAndroid sodium = null;
            try {
                sodium = new LazySodiumAndroid(new SodiumAndroid());
                kp = sodium.cryptoBoxKeypair();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String encodedMessage = sodium.cryptoBoxSealEasy("text",
                        Key.fromBytes(User.getInstance().getPublicKey()));
                Log.d("encoded from this user", encodedMessage);
                String decodedMessage = sodium.cryptoBoxSealOpenEasy(encodedMessage,
                        new KeyPair(Key.fromBytes(User.getInstance().getPublicKey()),
                                Key.fromBytes(User.getInstance().getPrivateKey())));
                Log.d("decoded from this user", decodedMessage);
            } catch (SodiumException e) {
                e.printStackTrace();
            }
        }
        context.startService(loadMessagesService);
        context.startService(getUsersService);
        Chat.generateDummyChats();

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
    protected void onStop() {
        Chat.saveChatsInJson();
        super.onStop();
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
}