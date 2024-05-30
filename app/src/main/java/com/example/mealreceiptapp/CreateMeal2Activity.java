package com.example.mealreceiptapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CreateMeal2Activity extends AppCompatActivity {

    private ImageView foodThumbnailStep;
    private EditText step1EditText;
    private EditText step2EditText;
    private EditText step3EditText;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal2);
        dbHelper = new DBHelper(this);

        foodThumbnailStep = findViewById(R.id.food_thumbnail_step);
        step1EditText = findViewById(R.id.editTextTextMultiLine);
        step2EditText = findViewById(R.id.editTextTextMultiLine2);
        step3EditText = findViewById(R.id.editTextTextMultiLine3);
        Button completeButton = findViewById(R.id.buttonComplete);

        Intent intent = getIntent();
        String imageUriString = intent.getStringExtra("imageUri");
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            foodThumbnailStep.setImageURI(imageUri);
        }

        completeButton.setOnClickListener(v -> {
            // Get step information from UI
            String step1 = step1EditText.getText().toString();
            String step2 = step2EditText.getText().toString();
            String step3 = step3EditText.getText().toString();

            // Concatenate steps
            StringBuilder stepsBuilder = new StringBuilder();
            if (!step1.isEmpty()) {
                stepsBuilder.append(step1).append("\n");
            }
            if (!step2.isEmpty()) {
                stepsBuilder.append(step2).append("\n");
            }
            if (!step3.isEmpty()) {
                stepsBuilder.append(step3).append("\n");
            }
            String steps = stepsBuilder.toString();

            // Get meal attributes from intent
            String mealName = intent.getStringExtra("mealName");
            String description = intent.getStringExtra("description");
            String ingredientIDsString = intent.getStringExtra("ingredientIDs");

            // Convert URI to byte array
            byte[] mealImage = uriToByteArray(Uri.parse(imageUriString));

            // Insert meal into database
            long mealID = dbHelper.insertMeal(mealName, mealImage, description, steps);

            // Insert ingredients into meal_ingredient table
            if (mealID != -1) {
                if (ingredientIDsString != null && !ingredientIDsString.isEmpty()) {
                    String[] ingredientIDs = ingredientIDsString.split(",");
                    for (String ingredientID : ingredientIDs) {
                        long ingredientIDLong = Long.parseLong(ingredientID);
                        dbHelper.insertMealIngredient(mealID, ingredientIDLong);
                    }
                }
                // Meal successfully added
                // Redirect to next activity or perform any other action
            } else {
                // Failed to add meal
                // Handle the failure scenario
            }
        });
    }

    // Convert URI to byte array
    private byte[] uriToByteArray(Uri uri) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }
}
