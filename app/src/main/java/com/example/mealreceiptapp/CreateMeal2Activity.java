    package com.example.mealreceiptapp;

    import android.content.ContentResolver;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.net.Uri;
    import android.os.Bundle;
    import android.view.Gravity;
    import android.view.View;
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
        private ImageButton Home;
        private ImageButton Notification;
        private ImageButton Profile;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_meal2);
            dbHelper = new DBHelper(this);

            foodThumbnailStep = findViewById(R.id.food_thumbnail_step);
            stepsContainer = findViewById(R.id.steps_container);
            Home = findViewById(R.id.union_ek5);
            Notification = findViewById(R.id.union_ek2);
            Profile = findViewById(R.id.union_ek1);
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
                    updateNotifications();
                } else {
                    Toast.makeText(CreateMeal2Activity.this, "Failed to add meal!", Toast.LENGTH_SHORT).show();
                }
                completeRecipe();
            });



            addStepRowButton.setOnClickListener(v -> addNewStepRow());

            // Set click listener for the "Home" button
            Home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start HomeActivity
                    Intent homeIntent = new Intent(CreateMeal2Activity.this, HomeActivity.class);
                    startActivity(homeIntent);
                }
            });

            // Set click listener for the "Profile" button
            Profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start ProfileActivity
                    Intent profileIntent = new Intent(CreateMeal2Activity.this, ProfileActivity.class);
                    startActivity(profileIntent);
                }
            });

            // Set click listener for the "Notification" button
            Notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start NotificationActivity
                    Intent notificationIntent = new Intent(CreateMeal2Activity.this, NotificationActivity.class);
                    startActivity(notificationIntent);
                }
            });
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

        private void updateNotifications() {
            // Initialize SharedPreferences
            SharedPreferences notificationsPrefs = getSharedPreferences("notifications", MODE_PRIVATE);
            // Create Editor
            SharedPreferences.Editor editor = notificationsPrefs.edit();
            // Update notification message
            editor.putString("notification_message", "Bạn vừa tạo 1 công thức mới");
            // Apply changes
            editor.apply();
        }
        private void completeRecipe() {
            // Logic để hoàn tất công thức
            String newRecipeTitle = "Công thức mới";
            String newRecipeMessage = "Bạn đã thêm một công thức mới!";

            // Thêm thông báo mới
            NotificationUtils.addNotification(this, newRecipeTitle, newRecipeMessage);

            // Điều hướng đến NotificationActivity để hiển thị thông báo
            Intent notificationIntent = new Intent(CreateMeal2Activity.this, NotificationActivity.class);
            startActivity(notificationIntent);

            // Hoặc bạn có thể ở lại trong CreateMeal2Activity và cập nhật giao diện tại chỗ nếu cần thiết
        }
    }
