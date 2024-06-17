package com.example.mealreceiptapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    private Context mContext;
    private ArrayList<Notification> mNotifications;

    public NotificationAdapter(Context context, ArrayList<Notification> notifications) {
        super(context, 0, notifications);
        mContext = context;
        mNotifications = notifications;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.notification_list_item, parent, false);
            holder = new ViewHolder();
            holder.notificationText = convertView.findViewById(R.id.notification_text);
            holder.notificationDescription = convertView.findViewById(R.id.notification_description);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the data item for this position
        Notification notification = getItem(position);

        // Populate the data into the template view using the data object
        if (notification != null) {
            holder.notificationText.setText(notification.getTitle());
            holder.notificationDescription.setText(notification.getDescription());
        }

        // Return the completed view to render on screen
        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView notificationText;
        TextView notificationDescription;
    }
}
