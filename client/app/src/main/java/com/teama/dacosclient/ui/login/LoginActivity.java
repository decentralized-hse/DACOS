package com.teama.dacosclient.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.teama.dacosclient.ChatsActivity;
import com.teama.dacosclient.R;
import com.teama.dacosclient.data.LoginRepository;
import com.teama.dacosclient.data.Result;
import com.teama.dacosclient.data.model.User;

public class LoginActivity extends AppCompatActivity {

    private static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = this;
        LoginRepository loginRepository = LoginRepository.getInstance();
        if (loginRepository.isLoggedIn())
        {
            Intent intent = new Intent(this, ChatsActivity.class);
            finish();
            this.startActivity(intent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);

                Result<User> result = loginRepository
                        .login(usernameEditText.getText().toString(),
                                passwordEditText.getText().toString());
                loadingProgressBar.setVisibility(View.INVISIBLE);
            }

        });
    }

    public static Context getContext(){
        return context;
    }
}