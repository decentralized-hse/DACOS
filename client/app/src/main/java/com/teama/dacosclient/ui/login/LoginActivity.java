package com.teama.dacosclient.ui.login;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.teama.dacosclient.R;
import com.teama.dacosclient.data.LoginRepository;
import com.teama.dacosclient.data.Result;
import com.teama.dacosclient.data.model.User;

public class LoginActivity extends AppCompatActivity {

    private static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.getContext(), "error", Toast.LENGTH_LONG).show();
                loadingProgressBar.setVisibility(View.VISIBLE);

                Result<User> result = LoginRepository.getInstance()
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