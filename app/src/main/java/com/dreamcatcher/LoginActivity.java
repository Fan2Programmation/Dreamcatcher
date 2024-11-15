package com.dreamcatcher;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {

    // 10.0.2.2 is a build-in ip address inside the emulator that represents the device that
    // is running the emulator (hence my own computer, where my http server is running)
    private static final String BASE_URL = "http://10.0.2.2:3000";
    EditText usernameInput;
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> onButtonClick("login"));
        registerButton.setOnClickListener(v -> onButtonClick("register"));
    }

    private void onButtonClick(String type) {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in the blanks", Toast.LENGTH_SHORT).show();
        } else {
            if(type.equals("login")) loginUser(username, password);
            else if (type.equals("register")) registerUser(username, password);
        }
    }

    private void loginUser(String username, String password) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder().url(BASE_URL + "/users").build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseData = "";
                    if(response.body() != null) {
                        // .string() enables us to get the body of the HTTP answer
                        responseData = response.body().string();
                    }

                    if (responseData.contains(username) && responseData.contains(password)) {
                        runOnUiThread(() -> Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show());
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Wrong credentials", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Request failed", Toast.LENGTH_SHORT).show());
                }

                response.close();

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Connexion problem", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void registerUser(String username, String password) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                String json = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

                RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

                Request request = new Request.Builder()
                        .url(BASE_URL + "/register")  // La route pour enregistrer l'utilisateur
                        .post(body)  // Méthode POST pour l'enregistrement
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Registration problem", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

}

