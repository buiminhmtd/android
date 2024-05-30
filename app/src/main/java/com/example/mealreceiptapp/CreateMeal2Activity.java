package com.example.mealreceiptapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CreateMeal2Activity extends AppCompatActivity {

    private ImageView foodThumbnailStep;
    private LinearLayout stepsContainer;
    private DBHelper dbHelper;
    private Uri imageUri;
    private List<EditText> stepEditTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal2);
        dbHelper = new DBHelper(this);

        foodThumbnailStep = findViewById(R.id.food_thumbnail_step);
        stepsContainer = findViewById(R.id.steps_container);
        Button completeButton = findViewById(R.id.buttonComplete);
        ImageButton addStepRowButton = findViewById(R.id.addStepRow);

        stepEditTexts = new ArrayList<>();

        // Add initial step EditTexts
        stepEditTexts.add(findViewById(R.id.editTextTextMultiLine));
        stepEditTexts.add(findViewById(R.id.editTextTextMultiLine2));
        stepEditTexts.add(findViewById(R.id.editTextTextMultiLine3));

        Intent intent = getIntent();
        String imageUriString = intent.getStringExtra("imageUri");
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
            foodThumbnailStep.setImageURI(imageUri);
        }

        completeButton.setOnClickListener(v -> {
            StringBuilder stepsBuilder = new StringBuilder();
            for (EditText stepEditText : stepEditTexts) {
                String stepText = stepEditText.getText().toString();
                if (!stepText.isEmpty()) {
                    stepsBuilder.append(stepText).append("\n");
                }
            }
            String steps = stepsBuilder.toString();

            String mealName = intent.getStringExtra("ten");
            String description = intent.getStringExtra("moTa");
            List<String> ingredientNames = intent.getStringArrayListExtra("ingredientNames");
            List<String> ingredientQuantities = intent.getStringArrayListExtra("ingredientQuantities");

            byte[] imageBytes = null;
            if (imageUri != null) {
                imageBytes = uriToByteArray(imageUri);
            }

            // Insert meal into database
            long mealID = dbHelper.insertMeal(mealName, imageBytes, description, steps);

            // Insert ingredients into meal_ingredient table
            if (mealID != -1 && ingredientNames != null && ingredientQuantities != null) {
                for (int i = 0; i < ingredientNames.size(); i++) {
                    String ingredientName = ingredientNames.get(i);
                    String ingredientQuantity = ingredientQuantities.get(i);
                    long ingredientID = dbHelper.insertIngredient(ingredientName, ingredientQuantity);
                    dbHelper.insertMealIngredient(mealID, ingredientID);
                }
                Toast.makeText(CreateMeal2Activity.this, "Meal added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CreateMeal2Activity.this, "Failed to add meal!", Toast.LENGTH_SHORT).show();
            }
        });

        addStepRowButton.setOnClickListener(v -> addNewStepRow());
    }

    private void addNewStepRow() {
        int stepNumber = stepEditTexts.size() + 1;
        EditText newStepEditText = new EditText(this);
        newStepEditText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newStepEditText.setHint("Bước " + stepNumber);
        newStepEditText.setInputType(android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        newStepEditText.setGravity(Gravity.START | Gravity.TOP);
        newStepEditText.setMinLines(3);
        stepsContainer.addView(newStepEditText);
        stepEditTexts.add(newStepEditText);
    }

    private byte[] uriToByteArray(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream inputStream = resolver.openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (inputStream != null) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                return byteArrayOutputStream.toByteArray();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
