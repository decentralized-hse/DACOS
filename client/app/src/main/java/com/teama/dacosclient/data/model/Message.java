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
            Log.d("message", "parseMessage: " + encodedMessage );
            decodedMessage = sodium.cryptoBoxSealOpenEasy(encodedMessage,
                    new KeyPair(
                            Key.fromBytes(User.getInstance().getPublicKey()),
                            Key.fromBytes(User.getInstance().getPrivateKey())
                    )
            );
        } catch (SodiumException | NegativeArraySizeException e) {
            Log.e("LazySodium", "Error in decoding message" + e.getMessage());
            return;
        }

        String[] message = decodedMessage.split(Pattern.quote("∫"));
        if (message.length != 3)
            return;
        int userId = Chat.getIdFromNickname(message[0]);
        if (userId == -10 || userId == -1)
            return;
        // TODO: Checking if user id == -1 - message is self-directed, now it is prohibited,
        //  maybe should be allowed.
        Date date;
        try {
            date = new Date(Long.parseLong(message[2]));
        } catch (Exception e) {
            return;
        }
        // TODO: It crashes because of order of downloading new messages and new users.
        Chat.getChats().get(userId).addMessage(message[1], false, date);
    }

    public static String encodeMessage(Message message, Chat chat) {
        String fullMessage = User.getInstance().getUsername() + '∫' +
                message.getText() + '∫' +
                System.currentTimeMillis();
        try {
            // TODO: this throws exception in some cases - should work this out.
            return sodium.cryptoBoxSealEasy(fullMessage, Key.fromBytes(chat.getPublicKey()));
        } catch (SodiumException e) {
            // Shouldn't be called - all info should be proper.
            Log.e("message text", fullMessage);
            Log.e("public key", Key.fromBytes(chat.getPublicKey()).toString());
            Log.e("encoding message", "Error message: " + e.getMessage());
            return "";
        }
    }
}
