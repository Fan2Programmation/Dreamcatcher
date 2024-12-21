package com.dreamcatcher;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DreamEntryActivity extends AppCompatActivity {

    private static final int SPEECH_REQUEST_CODE = 100;
    private static final int RECORD_AUDIO_PERMISSION_CODE = 101;

    private EditText dreamInputField;
    private Button dictateButton;
    private Button submitButton;

    private final OkHttpClient httpClient = new OkHttpClient();
    private static final String CREATE_DREAM_URL = "http://your-api-url/dreams/create";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dreamentry);

        dreamInputField = findViewById(R.id.dream_input);
        dictateButton = findViewById(R.id.dictate_button);
        submitButton = findViewById(R.id.submit_button);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkRecordAudioPermission();
        }

        dictateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSpeechToText();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDream();
            }
        });
    }

    private void checkRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Microphone permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void launchSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Describe your dream...");

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Speech-to-Text is not supported on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String spokenText = results.get(0);
                dreamInputField.setText(spokenText);
            }
        }
    }

    private void submitDream() {
        String dreamContent = dreamInputField.getText().toString().trim();

        if (dreamContent.isEmpty()) {
            Toast.makeText(this, "Please enter your dream before submitting.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject dreamPayload = new JSONObject();
        try {
            dreamPayload.put("content", dreamContent);
        } catch (JSONException e) {
            Log.e("DreamEntryActivity", "JSON Exception: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(dreamPayload.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(CREATE_DREAM_URL)
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DreamEntryActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(DreamEntryActivity.this, "Dream submitted successfully!", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(DreamEntryActivity.this, "Failed to submit dream. Please try again.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
