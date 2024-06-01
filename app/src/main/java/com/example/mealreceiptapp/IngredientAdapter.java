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

    private List<Map<String, Object>> ingredientList;

    public IngredientAdapter(List<Map<String, Object>> ingredientList) {
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_row2, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Map<String, Object> ingredient = ingredientList.get(position);
        holder.ingredientName.setText((String) ingredient.get("ingredientName"));
        byte[] ingredientImageBytes = (byte[]) ingredient.get("ingredientImage");
        if (ingredientImageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(ingredientImageBytes, 0, ingredientImageBytes.length);
            holder.ingredientImage.setImageBitmap(bitmap);
        } else {
            holder.ingredientImage.setImageResource(R.drawable.placeholder_image); // Use a placeholder image if no image is found
        }
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientName;
        ImageView ingredientImage;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.ingredientName);
            ingredientImage = itemView.findViewById(R.id.ingredientImage);
        }
    }
}
