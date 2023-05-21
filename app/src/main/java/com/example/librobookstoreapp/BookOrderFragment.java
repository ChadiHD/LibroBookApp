package com.example.librobookstoreapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookOrderFragment extends Fragment {
    TextView book_title_txt, book_author_txt, book_pages_txt, book_price_txt;
    String id, title, author, pages, price;
    ImageView book_image_view;
    byte[] image;
    Button order_button;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    String userId;
    DatabaseHelper db;

    public BookOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_order, container, false);

        book_title_txt = view.findViewById(R.id.book_title_txt);
        book_author_txt = view.findViewById(R.id.book_author_txt);
        book_pages_txt = view.findViewById(R.id.book_pages_txt);
        book_price_txt = view.findViewById(R.id.book_price_txt);
        book_image_view = view.findViewById(R.id.book_image_view);
        order_button = view.findViewById(R.id.order_button);
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        db = new DatabaseHelper(getActivity());

        getAndSetIntentData();

        order_button.setOnClickListener(view1 -> {
            if(fUser == null){
                Toast.makeText(getActivity(), "Please login to order", Toast.LENGTH_SHORT).show();
                return;
            }
            userId = fUser.getUid();
            Boolean insert = db.addPurchase(userId, id);
            if(insert){
                Intent intent = new Intent(getActivity(), FinalActivity.class);  // Redirecting to FinalActivity
                startActivity(intent);
            }else{
                Toast.makeText(getActivity(), "Order failed", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
}

    void getAndSetIntentData(){
        Bundle bundle = getArguments();
        assert bundle != null;
        if(bundle.getString("bookId") != null){    // If the data is not null
            // Getting The data from Fragment bundle
            id = bundle.getString("bookId");
            Cursor cursor = db.readAllData();
            if(cursor.getCount() == 0){
                Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
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
            // Setting data using the data from the bundle
            book_title_txt.setText(title);
            book_author_txt.setText(author);
            book_pages_txt.setText(pages);
            book_price_txt.setText(price);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            book_image_view.setImageBitmap(bitmap);

        }else{
            Toast.makeText(getActivity(), "No data was retrieved", Toast.LENGTH_SHORT).show();
        }
    }
}