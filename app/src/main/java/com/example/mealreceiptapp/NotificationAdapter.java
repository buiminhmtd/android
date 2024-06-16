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
        // Get the data item for this position
        Notification notification = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_list_item, parent, false);
        }

        // Lookup view for data population
        TextView notificationText = convertView.findViewById(R.id.notification_text);
        TextView notificationDescription = convertView.findViewById(R.id.notification_description);

        // Populate the data into the template view using the data object
        notificationText.setText(notification.getTitle());
        notificationDescription.setText(notification.getDescription());

        // Return the completed view to render on screen
        return convertView;
    }
}

