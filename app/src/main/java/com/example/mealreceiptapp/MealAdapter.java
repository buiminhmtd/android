package com.example.mealreceiptapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private Context context;
    private List<Map<String, Object>> mealList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public MealAdapter(Context context, List<Map<String, Object>> mealList) {
        this.context = context;
        this.mealList = mealList;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meal_item, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Map<String, Object> meal = mealList.get(position);
        holder.mealName.setText((String) meal.get("mealName"));
        holder.description.setText((String) meal.get("description"));

        // Convert byte array to Bitmap
        byte[] mealImage = (byte[]) meal.get("mealImage");
        if (mealImage != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(mealImage, 0, mealImage.length);
            holder.mealImage.setImageBitmap(bitmap);
        } else {
            holder.mealImage.setImageResource(R.drawable.placeholder_image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {

        TextView mealName, description;
        ImageView mealImage;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealName = itemView.findViewById(R.id.mealName);
            description = itemView.findViewById(R.id.description);
            mealImage = itemView.findViewById(R.id.mealImage);
        }
    }
}
