package com.example.mealreceiptapp;

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

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private List<Map<String, Object>> ingredients;

    public IngredientAdapter(List<Map<String, Object>> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_row2, parent, false);
        return new IngredientViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Map<String, Object> ingredient = ingredients.get(position);
        String ingredientName = (String) ingredient.get("ingredientName");
        byte[] ingredientImageBytes = (byte[]) ingredient.get("ingredientImage");

        holder.ingredientNameTextView.setText(ingredientName);
        if (ingredientImageBytes != null) {
            Bitmap ingredientImageBitmap = BitmapFactory.decodeByteArray(ingredientImageBytes, 0, ingredientImageBytes.length);
            holder.ingredientImageView.setImageBitmap(ingredientImageBitmap);
        } else {
            holder.ingredientImageView.setImageResource(R.drawable.placeholder_image);
        }
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        public ImageView ingredientImageView;
        public TextView ingredientNameTextView;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientImageView = itemView.findViewById(R.id.ingredient_image);
            ingredientNameTextView = itemView.findViewById(R.id.ingredient_name);
        }
    }
}
