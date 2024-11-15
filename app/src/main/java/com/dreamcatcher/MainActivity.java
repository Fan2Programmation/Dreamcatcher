package com.dreamcatcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> openUserProfile());

        Button feedButton = findViewById(R.id.feedButton);
        feedButton.setOnClickListener(v -> openFriendActivityFeed());

        Button newDreamButton = findViewById(R.id.newDreamButton);
        newDreamButton.setOnClickListener(v -> openDreamEntry());

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> openDreamSearch());
    }

    private void openUserProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    private void openFriendActivityFeed() {
        startActivity(new Intent(this, FeedActivity.class));
    }

    private void openDreamEntry() {
        startActivity(new Intent(this, DreamEntryActivity.class));
    }

    private void openDreamSearch() {
        startActivity(new Intent(this, DreamSearchActivity.class));
    }
}
