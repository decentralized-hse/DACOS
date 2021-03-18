package com.teama.dacosclient.data.model;

import androidx.annotation.NonNull;

public class Message {

    @NonNull
    private String text;
    @NonNull
    private Boolean fromMe;

    public Message(@NonNull String text, @NonNull Boolean fromMe) {
        this.text = text;
        this.fromMe = fromMe;
    }

    @NonNull
    public String getText() {
        return text;
    }

    @NonNull
    public Boolean getFromMe() {
        return fromMe;
    }
}
