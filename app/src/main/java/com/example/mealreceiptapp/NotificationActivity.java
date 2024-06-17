package com.example.mealreceiptapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private ListView notificationListView;
    private ArrayList<Notification> notifications;
    private NotificationAdapter adapter;
    private ImageButton Home;
    private ImageButton Profile;
    private ImageButton Create;
    private ImageButton Bookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Initialize notifications list
        notifications = new ArrayList<>();

        // Initialize ListView
        notificationListView = findViewById(R.id.notification_listview);

        // Initialize Adapter
        adapter = new NotificationAdapter(this, notifications);
        notificationListView.setAdapter(adapter);

        // Load notifications from SharedPreferences
        loadNotifications();

        // Initialize Home and Profile buttons
        Home = findViewById(R.id.union_ek5);
        Profile = findViewById(R.id.union_ek1);
        Create = findViewById(R.id.union_ek4);
        Bookmark = findViewById(R.id.union_ek3);

        // Set click listener for the "Home" button
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start HomeActivity
                Intent homeIntent = new Intent(NotificationActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            }
        });

        // Set click listener for the "Profile" button
        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ProfileActivity
                Intent profileIntent = new Intent(NotificationActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });
        // Set click listener for the "Create" button
        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CreateMealActivity
                Intent createIntent = new Intent(NotificationActivity.this, CreateMealActivity.class);
                startActivity(createIntent);
            }
        });

        // Set click listener for the "Bookmark" button
        Bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start SavedMealsActivity
                Intent bookmarkIntent = new Intent(NotificationActivity.this, SavedMealsActivity.class);
                startActivity(bookmarkIntent);
            }
        });
    }

    private void loadNotifications() {
        // Initialize SharedPreferences
        SharedPreferences notificationsPrefs = getSharedPreferences("notifications", MODE_PRIVATE);
        // Retrieve notification count
        int notificationCount = notificationsPrefs.getInt("notification_count", 0);

        for (int i = 0; i < notificationCount; i++) {
            String notificationTitle = notificationsPrefs.getString("notification_title_" + i, "");
            String notificationMessage = notificationsPrefs.getString("notification_message_" + i, "");
            if (!notificationTitle.isEmpty() && !notificationMessage.isEmpty()) {
                notifications.add(new Notification(notificationTitle, notificationMessage));
            }
        }

        // Notify adapter that data set changed
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
