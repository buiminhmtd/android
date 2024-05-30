package com.example.mealreceiptapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class CreateMealActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private ImageView foodThumbnail;
    private Button buttonComplete;
    private ImageButton themAnh;
    private RelativeLayout ingredientsContainer;
    private LinearLayout dynamicIngredientsContainer;
    private EditText tenCongThuc;
    private EditText moTa;
    private Uri selectedImageUri;

    private List<String> ingredientNames = new ArrayList<>();
    private List<String> ingredientQuantities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal);

        initializeViews();
        setupButtonClickListeners();
    }

    private void initializeViews() {
        foodThumbnail = findViewById(R.id.food_thumbnail);
        buttonComplete = findViewById(R.id.buttonComplete);
        themAnh = findViewById(R.id.themAnh);
        ingredientsContainer = findViewById(R.id.ingredients);
        dynamicIngredientsContainer = findViewById(R.id.dynamic_ingredients_container);
        tenCongThuc = findViewById(R.id.tenCongThuc);
        moTa = findViewById(R.id.moTa);

        // Initialize with one row
        addIngredientRow();
    }

    private void setupButtonClickListeners() {
        themAnh.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(CreateMealActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CreateMealActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                openImagePicker();
            }
        });

        buttonComplete.setOnClickListener(view -> {
            String ten = tenCongThuc.getText().toString();
            String moTaText = moTa.getText().toString();

            ingredientNames.clear();
            ingredientQuantities.clear();

            for (int i = 0; i < dynamicIngredientsContainer.getChildCount(); i++) {
                View ingredientRow = dynamicIngredientsContainer.getChildAt(i);
                EditText nameEditText = ingredientRow.findViewById(R.id.tenNguyenLieu);
                EditText quantityEditText = ingredientRow.findViewById(R.id.soLuong);

                ingredientNames.add(nameEditText.getText().toString());
                ingredientQuantities.add(quantityEditText.getText().toString());
            }

            Intent intent = new Intent(CreateMealActivity.this, CreateMeal2Activity.class);
            intent.putExtra("ten", ten);
            intent.putExtra("moTa", moTaText);
            if (selectedImageUri != null) {
                intent.putExtra("imageUri", selectedImageUri.toString());
            }
            intent.putStringArrayListExtra("ingredientNames", (ArrayList<String>) ingredientNames);
            intent.putStringArrayListExtra("ingredientQuantities", (ArrayList<String>) ingredientQuantities);
            startActivity(intent);
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            themAnh.setVisibility(View.GONE);
            foodThumbnail.setVisibility(View.VISIBLE);
            foodThumbnail.setImageURI(selectedImageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void addIngredientRow() {
        View ingredientRow = getLayoutInflater().inflate(R.layout.ingredient_row, null);

        ImageButton addButton = ingredientRow.findViewById(R.id.addRow);
        addButton.setOnClickListener(v -> addIngredientRow());

        dynamicIngredientsContainer.addView(ingredientRow);
    }
}
