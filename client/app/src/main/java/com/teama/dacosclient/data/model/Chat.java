package com.teama.dacosclient.data.model;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.stfalcon.chatkit.commons.models.IUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Chat extends BaseObservable implements IUser {

    private static List<Chat> CHATS = new ArrayList<>();

    private static final MutableLiveData<List<Chat>> chatsData = new MutableLiveData<>(CHATS);

    @NonNull
    private String username;
    @NonNull
    private List<Message> messages = new ArrayList<>();
    // Id is used for differentiating between similar chats,
    // and in adapter to contain chat position in the global list.
    @NonNull
    private Integer id;


    private Chat(@NotNull String username) {
        this.username = username;
        this.id = CHATS.size();
    }


    /**
     * Creates new chat and inserts it into the static chat array.
     */
    public static Chat createChat(String username) {
        Chat chat = new Chat(username);
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
        chatsData.setValue(CHATS);
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
        Chat sergey = createChat("Sergey");
        sergey.addMessage("zdarova", false);
        sergey.addMessage("priv", true);
        sergey.addMessage("!", false);
        sergey.addMessage("!!", false);
        sergey.addMessage("!!!", true);
        sergey.addMessage("?", true);
        sergey.addMessage(":)", false);
        sergey.addMessage(":/", true);
        sergey.addMessage("пока!", false);
        createChat("Dima")
                .addMessage("Very very very very very very " +
                                "very very very very very very very very very very long message",
                        true

                );
        createChat("Artemiy Fitisov").addMessage("privet", false);
        createChat("Vlad").addMessage("che kak", false);
        createChat("Anton").addMessage("sps", false);
        createChat("Boris");
        createChat("Ivan").addMessage("ku", false);
        createChat("Konstantin");
        createChat("Alexandr").addMessage("che kak", false);
        createChat("Alexey").addMessage("здарова", false);
        createChat("Natasha");
        createChat("Olya").addMessage("как жизнь?", false);
        createChat("Masha").addMessage("хороший чат блин", false);
        createChat("Dasha");
        createChat("Josh").addMessage("ыыы", false);
        createChat("John").addMessage("!", false);
        createChat("Grisha");
        createChat("Pavel").addMessage("когда стики завезут", false);
        createChat("Oleg").addMessage("priv", false);
    }


    @NonNull
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(@NonNull List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(String text, Boolean fromMe) {
        messages.add(new Message(text, fromMe, id));
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
}
