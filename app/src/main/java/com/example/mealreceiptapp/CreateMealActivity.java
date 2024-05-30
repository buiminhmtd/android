package com.example.mealreceiptapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CreateMealActivity extends AppCompatActivity {

    private Uri selectedImageUri;
    private ImageButton themAnhButton;
    private LinearLayout dynamicIngredientsContainer;
    private DBHelper dbHelper;
    private List<Long> ingredientIDs;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        selectedImageUri = data.getData();
                        themAnhButton.setImageURI(selectedImageUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal);
        dbHelper = new DBHelper(this);
        ingredientIDs = new ArrayList<>();

        themAnhButton = findViewById(R.id.themAnh);
        Button tieptucButton = findViewById(R.id.buttonComplete);
        dynamicIngredientsContainer = findViewById(R.id.dynamic_ingredients_container);

        // Add initial row
        addNewIngredientRow();

        themAnhButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        tieptucButton.setOnClickListener(v -> {
            // Get meal information from UI
            EditText tenCongThucEditText = findViewById(R.id.tenCongThuc);
            EditText moTaEditText = findViewById(R.id.moTa);
            String mealName = tenCongThucEditText.getText().toString();
            String description = moTaEditText.getText().toString();

            // Redirect to CreateMeal2Activity
            Intent intent = new Intent(CreateMealActivity.this, CreateMeal2Activity.class);
            intent.putExtra("mealName", mealName);
            intent.putExtra("description", description);
            intent.putExtra("imageUri", (selectedImageUri != null) ? selectedImageUri.toString() : null);
            intent.putExtra("ingredientIDs", TextUtils.join(",", ingredientIDs));
            startActivity(intent);
        });
    }

    private void addNewIngredientRow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout newRow = (LinearLayout) inflater.inflate(R.layout.ingredient_row, dynamicIngredientsContainer, false);

        // Get the addRow button from the new row
        ImageButton addRowButton = newRow.findViewById(R.id.addRow);
        addRowButton.setOnClickListener(v -> addNewIngredientRow());

        dynamicIngredientsContainer.addView(newRow);
    }
}
