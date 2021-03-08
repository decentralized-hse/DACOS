package com.teama.dacosclient.data.model;

import com.teama.dacosclient.data.LoginDataSource;
import com.teama.dacosclient.data.LoginRepository;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class User {

    private String username;
    private String password;
    private String rsaN;
    private String rsaE;
    private String gInBigPower;

    private static User instance;


    private User(String username, String password) {
        this.username = username;
        this.password = password;
        this.rsaN = "1";
        this.rsaE = "2";
        this.gInBigPower = "3";
    }

    public static User getInstance() {
        return instance;
    }

    public static void generateInstance(String username, String password) {
        if (instance == null)
            instance = new User(username, password);

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRsaN() {
        return rsaN;
    }

    public String getRsaE() {
        return rsaE;
    }

    public String getGInBigPower() {
        return gInBigPower;
    }
}