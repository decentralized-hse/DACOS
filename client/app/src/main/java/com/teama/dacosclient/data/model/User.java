package com.teama.dacosclient.data.model;

import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.LazySodiumAndroid;
import com.goterl.lazycode.lazysodium.SodiumAndroid;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class User {

    private String username;
    private String password;
    // TODO: change format of containing keys to String (hex from byte[]) - REQUIRES CHANGES ON SERV    ER.
    private byte[] privateKey;
    private byte[] publicKey;

    private static User instance;

    private User(String username, String password) {
        this.username = username;
        this.password = password;
        LazySodium sodium = new LazySodiumAndroid(new SodiumAndroid());
        try {
            KeyPair kp = sodium.cryptoBoxKeypair();
            publicKey = kp.getPublicKey().getAsBytes();
            privateKey = kp.getSecretKey().getAsBytes();
        } catch (SodiumException e) {
            // Shouldn't be called in any case.
            e.printStackTrace();
        }
    }

    public static User getInstance() {
        return instance;
    }

    public static void setInstance(String username, String password) {
        instance = new User(username, password);
    }

    public static void setInstance(User user) {
        instance = user;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }
}