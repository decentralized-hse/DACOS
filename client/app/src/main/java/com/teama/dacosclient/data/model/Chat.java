package com.teama.dacosclient.data.model;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Chat extends BaseObservable {

    private static List<Chat> CHATS = new ArrayList<>();

    private static final MutableLiveData<List<Chat>> chatsData = new MutableLiveData<>(CHATS);

    @NonNull
    private String username;
    @NonNull
    private List<Message> messages = new ArrayList<>();



    private Chat(@NotNull String username) {
        this.username = username;
    }


    /**
     Creates new chat and inserts it into the static chat array.
     */
    public static Chat createChat(String username) {
        Chat chat = new Chat(username);
        CHATS.add(chat);
        chatsData.postValue(CHATS);
        return chat;
    }

    /**
     * Sets chat instance to the given one.
     * Should only be called for put the parsed Json data into Chats!
     * @param chats List<Chat>, parsed from a Json.
     */
    public static void setChat(List<Chat> chats) {
        CHATS = chats;
        chatsData.postValue(CHATS);
    }

    @NotNull
    public String getUsername() {
        return username;
    }


    /**
     * Is here for testing purposes only!
     * Should be called to fill CHATS with dummy data.
     */
    public static void generateDummyChats()
    {
        createChat("Sergey").addMessage(new Message("zdarova", false));
        createChat("Dima")
                .addMessage(
                        new Message("Very very very very very very " +
                                "very very very very very very very very very very long message",
                                false
                        )
                );
        createChat("Artemiy Fitisov").addMessage(new Message("privet", false));
        createChat("Vlad").addMessage(new Message("che kak", false));
        createChat("Anton").addMessage(new Message("sps", false));
        createChat("Boris");
        createChat("Ivan").addMessage(new Message("ku", false));
        createChat("Konstantin");
        createChat("Alexandr").addMessage(new Message("che kak", false));;
        createChat("Alexey").addMessage(new Message("здарова", false));;
        createChat("Natasha");
        createChat("Olya").addMessage(new Message("как жизнь?", false));;
        createChat("Masha").addMessage(new Message("хороший чат блин", false));;
        createChat("Dasha");
        createChat("Josh").addMessage(new Message("ыыы", false));;
        createChat("John").addMessage(new Message("!", false));;
        createChat("Grisha");
        createChat("Pavel").addMessage(new Message("когда стики завезут", false));;
        createChat("Oleg").addMessage(new Message("priv", false));;
    }


    @NonNull
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(@NonNull List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(@NonNull Message message) {
        messages.add(message);
        Chat.notifyDataUpdate();
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

    private static void notifyDataUpdate() {
        chatsData.postValue(CHATS);
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
}
