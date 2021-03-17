package com.teama.dacosclient.data.model;

import android.security.keystore.KeyProperties;
import android.util.Log;

import com.teama.dacosclient.data.LoginDataSource;
import com.teama.dacosclient.data.LoginRepository;

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
    private String publicRsaN;
    private String rsaE;
    private String gInBigPower;
    private String privateRSAN;
    private RSAPrivateKey rsaPrivateKey;
    private RSAPublicKey rsaPublicKey;

    private static User instance;


    private User(String username, String password) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA);
            generator.initialize(2048);
            KeyPair keyPair = generator.genKeyPair();
            RSAPublicKey publicKey = (java.security.interfaces.RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (java.security.interfaces.RSAPrivateKey) keyPair.getPrivate();
            this.username = username;
            this.password = password;
            this.publicRsaN = publicKey.getModulus().toString();
            this.privateRSAN = privateKey.getModulus().toString();
            this.rsaE = publicKey.getPublicExponent().toString();
            this.gInBigPower = "3";
            this.rsaPrivateKey = privateKey;
            this.rsaPublicKey = publicKey;
        } catch (NoSuchAlgorithmException e) {
            Log.e("Critical user errer", "couldn't find corresponding algorithm");
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

    public String getPublicRsaN() {
        return publicRsaN;
    }

    public String getRsaE() {
        return rsaE;
    }

    public String getGInBigPower() {
        return gInBigPower;
    }
}