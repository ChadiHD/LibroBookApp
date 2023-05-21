package com.example.librobookstoreapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.util.Set;

public class UpdateActivity extends AppCompatActivity {

    EditText title_input, author_input, pages_input, price_input;
    Button update_button, delete_button;
    String id, title, author, pages, price;
    ImageView open_gallery_button;
    byte[] image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        title_input = findViewById(R.id.title_input2);
        author_input = findViewById(R.id.author_input2);
        pages_input = findViewById(R.id.pages_input2);
        price_input = findViewById(R.id.price_input2);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);
        open_gallery_button = findViewById(R.id.open_gallery2);

        getAndSetIntentData();

        //Set actionbar title after getAndSetIntentData method
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("Edit" + title);
        }

        update_button.setOnClickListener(view -> {
            DatabaseHelper DB = new DatabaseHelper(UpdateActivity.this);

            title = title_input.getText().toString().trim();
            author = author_input.getText().toString().trim();
            pages = pages_input.getText().toString().trim();
            price = price_input.getText().toString().trim();
            image = convertImageViewToByte(open_gallery_button);
            DB.updateData(id, title, author, pages, price, image);
            Toast.makeText(UpdateActivity.this, "Book updated successfully", Toast.LENGTH_SHORT).show();
        });

        delete_button.setOnClickListener(view -> confirmDialog());

        open_gallery_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("id")){
            DatabaseHelper DB = new DatabaseHelper(UpdateActivity.this);
            // Getting The data from Intent
            id = getIntent().getStringExtra("id");
            Cursor cursor = DB.readAllData();
            if(cursor.getCount() == 0){
                Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
            }else{
                while(cursor.moveToNext()){
                    if(cursor.getString(0).equals(id)){
                        title = cursor.getString(1);
                        author = cursor.getString(2);
                        pages = cursor.getString(3);
                        price = cursor.getString(4);
                        image = cursor.getBlob(5);
                    }
                }
            }

            // Setting data using Intent
            title_input.setText(title);
            author_input.setText(author);
            pages_input.setText(pages);
            price_input.setText(price);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            open_gallery_button.setImageBitmap(bitmap);

        }else{
            Toast.makeText(this, "No data was retrieved", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + title + " ?");
        builder.setMessage("Are you sure you want to delete " + title + " ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper DB = new DatabaseHelper(UpdateActivity.this);
                DB.deleteOneRow(id);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.create().show();
    }

    byte[] convertImageViewToByte(ImageView imageView){
        // Enable the drawing cache for the ImageView
        imageView.setDrawingCacheEnabled(true);
        // Get the bitmap from the ImageView's drawing cache
        Bitmap bitmap = imageView.getDrawingCache();
        // Create a byte array output stream
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress the bitmap
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();
        // Disable the drawing cache for the ImageView
        imageView.setDrawingCacheEnabled(false);
        return image;
    }
}