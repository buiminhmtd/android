package com.example.mealreceiptapp;

import android.content.Context;
import android.content.SharedPreferences;

public class NotificationUtils {

    public static void addNotification(Context context, String title, String message) {
        SharedPreferences notificationsPrefs = context.getSharedPreferences("notifications", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = notificationsPrefs.edit();

        int notificationCount = notificationsPrefs.getInt("notification_count", 0);
        editor.putString("notification_title_" + notificationCount, title);
        editor.putString("notification_message_" + notificationCount, message);
        editor.putInt("notification_count", notificationCount + 1);

        editor.apply();
    }
}
