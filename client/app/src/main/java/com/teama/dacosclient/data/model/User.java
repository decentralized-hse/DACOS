package com.teama.dacosclient.data.model;

import android.security.keystore.KeyProperties;
import android.util.Log;

import com.teama.dacosclient.data.LoginDataSource;
import com.teama.dacosclient.data.LoginRepository;

import org.libsodium.jni.NaCl;
import org.libsodium.jni.Sodium;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class User {

    private String username;
    private String password;
    private byte[] privateKey;
    private byte[] publicKey;

    // TODO: fix serialization problem on RSA keys:
    //  https://stackoverflow.com/questions/40921562/how-to-serialize-deserialize-a-flexiprovider-keypair-using-gson
    //  or change crypto to elliptic curve


    private static User instance;

    private User(String username, String password) {
        this.username = username;
        this.password = password;
        Sodium sodium = NaCl.sodium();
        publicKey = new byte[Sodium.crypto_box_publickeybytes()];
        privateKey = new byte[Sodium.crypto_box_secretkeybytes()];
        Sodium.crypto_box_keypair(publicKey, privateKey);
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