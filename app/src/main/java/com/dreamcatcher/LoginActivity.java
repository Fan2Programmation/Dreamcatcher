package com.dreamcatcher;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {

    // the address of our spring boot api (enables us to communicate with our database by
    // using an HTTP interface because psql isn't supported directly with android development)
    // 10.0.2.2 is the address of the computer on which the simulation is running (hence my
    // own computer where the api is running)
    private static final String BASE_URL = "http://10.0.2.2:8080/passerelle/v1/users";
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
            if (type.equals("login")) loginUser(username, password);
            else if (type.equals("register")) registerUser(username, password);
        }
    }

    private void loginUser(String username, String password) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                // this is the json we send to the api
                String json = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
                RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

                // creating the request
                Request request = new Request.Builder()
                        .url(BASE_URL + "/login")
                        .post(body)
                        .build();

                // executing the request
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show());
                } else if (response.code() == 401) {
                    runOnUiThread(() -> Toast.makeText(this, "Wrong credentials", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show());
                }

                response.close();

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Connection problem", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void registerUser(String username, String password) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                // this is the json we send to the api
                String json = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
                RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

                // creating the request
                Request request = new Request.Builder()
                        .url(BASE_URL + "/register")
                        .post(body)
                        .build();

                // executing the request
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show());
                } else if (response.code() == 400) {
                    runOnUiThread(() -> Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show());
                }

                response.close();

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Registration problem", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
