package com.teama.dacosclient.data.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.stfalcon.chatkit.commons.models.IUser;
import com.teama.dacosclient.activities.LoginActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Chat extends BaseObservable implements IUser {

    private static List<Chat> CHATS = new ArrayList<>();

    private static final MutableLiveData<List<Chat>> chatsData = new MutableLiveData<>(CHATS);

    private static int currentBlock = 0;

    private final byte[] publicKey;

    // TODO: observer on chatsData to see if there is chat added and it should be updated in HashMap.
    private static HashMap<String, Integer> nicknameToId;

    @NonNull
    private String username;
    @NonNull
    private List<Message> messages = new ArrayList<>();
    // Id is used for differentiating between similar chats,
    // and in adapter to contain chat position in the global list.
    @NonNull
    private Integer id;


    private Chat(@NotNull String username, byte[] publicKey) {
        this.username = username;
        this.id = CHATS.size();
        this.publicKey = publicKey;
    }


    /**
     * Creates new chat and inserts it into the static chat array.
     */
    public static Chat createChat(String username, byte[] publicKey) {
        Chat chat = new Chat(username, publicKey);
        CHATS.add(chat);
        chatsData.setValue(CHATS);
        return chat;
    }

    /**
     * Sets chat instance to the given one.
     * Should only be called for put the parsed Json data into Chats!
     *
     * @param chats List<Chat>, parsed from a Json.
     */
    public static void setChat(List<Chat> chats) {
        CHATS = chats;
        nicknameToId = new HashMap<>();
        chatsData.setValue(CHATS);
        for (int i = 0; i < chats.size(); i++)
            nicknameToId.put(chats.get(i).username, i);
        nicknameToId.put(User.getInstance().getUsername(), -1);
    }

    public static int getCurrentBlock() {
        return currentBlock;
    }

    public static void setCurrentBlock(int currentBlock) {
        Chat.currentBlock = currentBlock;
    }

    @NotNull
    public String getUsername() {
        return username;
    }


    /**
     * Is here for testing purposes only!
     * Should be called to fill CHATS with dummy data.
     */
    public static void generateDummyChats() {
        CHATS = new ArrayList<>();
        chatsData.setValue(CHATS);
        byte[] dummyPublicKey = new byte[0];
        Chat sergey = createChat("Sergey", dummyPublicKey);
        sergey.addMessage("zdarova", false, new Date(System.currentTimeMillis()));
        sergey.addMessage("priv", true, new Date(System.currentTimeMillis()));
        sergey.addMessage("!", false, new Date(System.currentTimeMillis()));
        sergey.addMessage("!!", false, new Date(System.currentTimeMillis()));
        sergey.addMessage("!!!", true, new Date(System.currentTimeMillis()));
        sergey.addMessage("?", true, new Date(System.currentTimeMillis()));
        sergey.addMessage(":)", false, new Date(System.currentTimeMillis()));
        sergey.addMessage(":/", true, new Date(System.currentTimeMillis()));
        sergey.addMessage("пока!", false , new Date(System.currentTimeMillis()));
        createChat("Dima", dummyPublicKey)
                .addMessage("Very very very very very very " +
                                "very very very very very very very very very very long message",
                        true,
                        new Date(System.currentTimeMillis())

                );
        createChat("Artemiy Fitisov", dummyPublicKey).addMessage("privet", false, new Date(System.currentTimeMillis()));
        createChat("Vlad", dummyPublicKey).addMessage("che kak", false, new Date(System.currentTimeMillis()));
        createChat("Anton", dummyPublicKey).addMessage("sps", false, new Date(System.currentTimeMillis()));
        createChat("Boris", dummyPublicKey);
        createChat("Ivan", dummyPublicKey).addMessage("ku", false, new Date(System.currentTimeMillis()));
        createChat("Konstantin", dummyPublicKey);
        createChat("Alexandr", dummyPublicKey).addMessage("che kak", false, new Date(System.currentTimeMillis()));
        createChat("Alexey", dummyPublicKey).addMessage("здарова", false, new Date(System.currentTimeMillis()));
        createChat("Natasha", dummyPublicKey);
        createChat("Olya", dummyPublicKey).addMessage("как жизнь?", false, new Date(System.currentTimeMillis()));
        createChat("Masha", dummyPublicKey).addMessage("хороший чат блин", false, new Date(System.currentTimeMillis()));
        createChat("Dasha", dummyPublicKey);
        createChat("Josh", dummyPublicKey).addMessage("ыыы", false, new Date(System.currentTimeMillis()));
        createChat("John", dummyPublicKey).addMessage("!", false, new Date(System.currentTimeMillis()));
        createChat("Grisha", dummyPublicKey);
        createChat("Pavel", dummyPublicKey).addMessage("когда стики завезут", false, new Date(System.currentTimeMillis()));
        createChat("Oleg", dummyPublicKey).addMessage("priv", false, new Date(System.currentTimeMillis()));
    }


    @NonNull
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(@NonNull List<Message> messages) {
        this.messages = messages;
    }

    public Message addMessage(String text, Boolean fromMe, Date date) {
        Message addedMessage = new Message(text, fromMe, id, date);
        messages.add(addedMessage);
        Chat.notifyDataUpdate();
        return addedMessage;
    }

    public Message getLastMessage() {
        if (!messages.isEmpty())
            return messages.get(messages.size() - 1);
        return null;
    }

    public static List<Chat> getChats() {
        return chatsData.getValue();
    }

    public static void observeChatsData(LifecycleOwner lifecycleOwner, Observer observer) {
        chatsData.observe(lifecycleOwner, observer);
    }

    public  static void removeChatsDataObserver(LifecycleOwner lifecycleOwner) {
        chatsData.removeObservers(lifecycleOwner);
    }

    private static void notifyDataUpdate() {
        chatsData.setValue(CHATS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return username.equals(chat.username) &&
                messages.equals(chat.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, messages);
    }

    @NonNull
    public Integer getNumericId() {
        return id;
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public String getAvatar() {
        return null;
    }

    public static Integer getIdFromNickname(String nickname) {
        if (nicknameToId.containsKey(nickname))
            return nicknameToId.get(nickname);
        return -10;
    }

    public static void saveChatsInJson() {
        SharedPreferences sharedPreferences = LoginActivity.getContext()
                .getSharedPreferences("dacos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Chat.getChats());
        editor.putString("chats", json);
        editor.putInt("current_block", currentBlock);
        Log.d("json", "saved : " + json);
        editor.apply();
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void deleteMessage(Message message) {
        messages.remove(message);
        notifyDataUpdate();
    }
}
