package com.teama.dacosclient.data.model;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    public static final List<Chat> CHATS = new ArrayList<>();

    @NonNull
    private String username;
    @NonNull
    private List<Message> messages = new ArrayList<>();

    private Chat(@NotNull String username) {
        this.username = username;
    }

    static {
        createChat("Sergey").addMessage(new Message("sosi", false));
        createChat("Dima")
                .addMessage(
                        new Message("Very very very very very very " +
                                "very very very very very very very very very very long message",
                                false
                        )
                );
        createChat("Artemiy Fitisov");
        createChat("Vlad");
        createChat("Anton");
        createChat("Boris");
        createChat("Ivan");
        createChat("Konstantin");
        createChat("Alexandr");
        createChat("Alexey");
    }

    /**
     Creates new chat and inserts it into the static chat array.
     */
    public static Chat createChat(String username)
    {
        Chat chat = new Chat(username);
        CHATS.add(chat);
        return chat;
    }

    @NotNull
    public String getUsername() {
        return username;
    }

    @NonNull
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(@NonNull List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(@NonNull Message message)
    {
        messages.add(message);
    }

    public Message getLastMessage()
    {
        if (!messages.isEmpty())
            return messages.get(messages.size() - 1);
        return null;
    }
}
