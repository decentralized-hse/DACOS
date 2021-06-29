package com.teama.dacosclient.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teama.dacosclient.R;
import com.teama.dacosclient.activities.ChatsActivity;
import com.teama.dacosclient.data.model.Chat;
import com.teama.dacosclient.data.model.Message;

import java.util.List;

public class GetLoadMessagesService extends  GetSomethingFromServerService{
    @Override
    public void execute(String response) {
        Gson gson = new Gson();
        List<List<String>> responseList = gson.fromJson(response,
                new TypeToken<List<List<String>>>() {}.getType());
        Chat.setCurrentBlock(Chat.getCurrentBlock() + responseList.size());
        for (List<String> list : responseList)
            for (String message : list)
                Message.parseMessage(message);
    }

    @Override
    public String getUrl() {
        return ChatsActivity.getActivityContext()
                .getResources().getString(R.string.server_host)
                + "read_message?block_number=" + Chat.getCurrentBlock();
    }

    @Override
    public Integer getRepeatTime() {
        return 10;
    }
}
