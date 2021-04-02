package com.teama.dacosclient.data.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.LazySodiumAndroid;
import com.goterl.lazycode.lazysodium.SodiumAndroid;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.Box;
import com.goterl.lazycode.lazysodium.utils.Key;
import com.goterl.lazycode.lazysodium.utils.KeyPair;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;


import java.util.Date;
import java.util.regex.Pattern;

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
    private int chatId;
    private static final LazySodium sodium = new LazySodiumAndroid(new SodiumAndroid());

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

    public Message(@NonNull String text, @NonNull Boolean fromMe, int chatId, Date createdAt) {
        this.text = text;
        this.fromMe = fromMe;
        id = Chat.getChats().get(chatId).getMessages().size();
        this.chatId = chatId;
        this.createdAt = createdAt;
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

    public static void parseMessage(String encodedMessage) {
        String decodedMessage;
        try {
            decodedMessage = sodium.cryptoBoxSealOpenEasy(encodedMessage,
                    new KeyPair(
                            Key.fromBytes(User.getInstance().getPublicKey()),
                            Key.fromBytes(User.getInstance().getPrivateKey())
                    )
            );
        } catch (SodiumException e) {
            Log.e("LazySodium", "Error in decoding message" + e.getMessage());
            return;
        }

        String[] message = decodedMessage.split("ʃ");
        if (message.length != 3)
            return;
        int userId = Chat.getIdFromNickname(message[0]);
        if (userId == -10 || userId == -1)
            return;
        Date date;
        try {
            date = new Date(Long.parseLong(message[2]));
        } catch (Exception e) {
            return;
        }
        Chat.getChats().get(userId).addMessage(message[1], false, date);
    }

    public static String encodeMessage(Message message, Chat chat) {
        String fullMessage = chat.getName() + '∫' +
                message.getText() + '∫' +
                System.currentTimeMillis();
        try {
            return sodium.cryptoBoxSealEasy(message.getText(), Key.fromBytes(chat.getPublicKey()));
        } catch (SodiumException e) {
            // Shouldn't be called - all info should be proper.
            Log.e("encoding message", "Error message: " + e.getMessage());
            return null;
        }
    }
}
