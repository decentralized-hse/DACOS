package com.teama.dacosclient.services;

import android.util.Log;

import com.google.gson.Gson;
import com.teama.dacosclient.R;
import com.teama.dacosclient.activities.ChatsActivity;
import com.teama.dacosclient.data.model.Chat;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.stream.Collectors;

public class GetNewUsersService extends GetSomethingFromServerService{
    @Override
    public void execute(String response) {
        Gson gson = new Gson();
        GetUserResponse[] responseList =
                gson.fromJson(response, (Type) GetUserResponse[].class);
        Set<String> usernameSet = Chat.getChats().stream().map(Chat::getUsername)
                .collect(Collectors.toSet());
        Log.d("Usernames", response);
        for (GetUserResponse object : responseList) {
            if (!usernameSet.contains(object.getUsername()))
                Chat.createChat(object.getUsername(),
                        object.getPublic_key());
        }
    }

    @Override
    public String getUrl() {
        return ChatsActivity.getActivityContext()
                .getResources().getString(R.string.server_host) + "get_users";
    }

    @Override
    public Integer getRepeatTime() {
        return 3;
    }

    class GetUserResponse {
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
