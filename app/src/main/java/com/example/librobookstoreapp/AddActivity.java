package com.example.librobookstoreapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Currency;

public class AddActivity extends AppCompatActivity {

    EditText title_input, author_input, pages_input, price_input;
    Button add_button;
    ImageView open_gallery_button;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        title_input = findViewById(R.id.title_input);
        author_input = findViewById(R.id.author_input);
        pages_input = findViewById(R.id.pages_input);
        price_input = findViewById(R.id.price_input);
        add_button = findViewById(R.id.add_button);
        open_gallery_button = findViewById(R.id.open_gallery);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper DB = new DatabaseHelper(AddActivity.this);

                if(title_input.getText().toString().trim().equals("") ||
                        author_input.getText().toString().trim().equals("") ||
                        pages_input.getText().toString().trim().equals("") ||
                        price_input.getText().toString().trim().equals("")) {
                    Toast.makeText(AddActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Enable the drawing cache for the ImageView
                open_gallery_button.setDrawingCacheEnabled(true);
                // Get the bitmap from the ImageView's drawing cache
                Bitmap bitmap = open_gallery_button.getDrawingCache();
                // Display the bitmap from the ImageView's drawing cache
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if(bitmap == null && bitmap.isRecycled()) {
                    Toast.makeText(AddActivity.this, "Please provide a valid image", Toast.LENGTH_SHORT).show();
                    return;
                }
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();

                DB.addBook(title_input.getText().toString().trim(),
                        author_input.getText().toString().trim(),
                        Integer.parseInt(pages_input.getText().toString().trim()),
                        BigDecimal.valueOf(Long.parseLong(price_input.getText().toString().trim())),
                        image);
                // Disable the drawing cache for the ImageView
                open_gallery_button.setDrawingCacheEnabled(false);

                Toast.makeText(AddActivity.this, "Book added successfully", Toast.LENGTH_SHORT).show();
            }
        });
        open_gallery_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    // This method is called when the user returns from the gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            open_gallery_button.setImageURI(data.getData());
        }
    }
}