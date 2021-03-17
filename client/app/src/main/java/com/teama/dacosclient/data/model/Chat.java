package com.teama.dacosclient.data.model;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    public static final List<Chat> CHATS = new ArrayList<>();

    private String username;

    private Chat(String username) {
        this.username = username;
    }

    static {
        createChat("Sergey");
        createChat("Dima");
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

    public String getUsername() {
        return username;
    }
}
