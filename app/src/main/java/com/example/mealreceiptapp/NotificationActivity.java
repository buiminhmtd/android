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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Initialize notifications list
        notifications = new ArrayList<>();

        // Load notifications from SharedPreferences
        loadNotifications();

        // Initialize ListView and Adapter
        notificationListView = findViewById(R.id.notification_listview);
        adapter = new NotificationAdapter(this, notifications);
        notificationListView.setAdapter(adapter);

        Home = findViewById(R.id.union_ek5);
        Profile = findViewById(R.id.union_ek1);

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

    }

    private void loadNotifications() {
        // Initialize SharedPreferences
        SharedPreferences notificationsPrefs = getSharedPreferences("notifications", MODE_PRIVATE);
        // Retrieve notification message
        String notificationMessage = notificationsPrefs.getString("notification_message", "");

        // Add notification to list if message is not empty
        if (!notificationMessage.isEmpty()) {
            notifications.add(new Notification("Thông báo", notificationMessage));
            adapter.notifyDataSetChanged(); // Notify adapter that data set changed
        }
    }
}
