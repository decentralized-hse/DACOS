package com.teama.dacosclient.data.model;

import androidx.annotation.NonNull;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;
import java.util.UUID;

public class Message implements IMessage {

    @NonNull
    private String text;
    @NonNull
    private boolean fromMe;
    @NonNull
    private Date createdAt;
    @NonNull
    private long id;
    @NonNull
    private transient int chatId;

    private static final transient IUser ME = new IUser() {
        @Override
        public String getId() {
            return "-1";
        }

        @Override
        public String getName() {
            return User.getInstance().getUsername();
        }

        @Override
        public String getAvatar() {
            return null;
        }
    };

    public Message(@NonNull String text, @NonNull Boolean fromMe, int chatId) {
        this.text = text;
        this.fromMe = fromMe;
        id = Chat.getChats().get(chatId).getMessages().size();
        this.chatId = chatId;
        createdAt = (new Date(System.currentTimeMillis()));
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @NonNull
    public String getText() {
        return text;
    }

    @Override
    public IUser getUser() {
        return fromMe ? ME : Chat.getChats().get(chatId);
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @NonNull
    public Boolean getFromMe() {
        return fromMe;
    }
}
