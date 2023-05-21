package com.example.librobookstoreapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BookManagerFragment extends Fragment {
    RecyclerView recyclerView;
    FloatingActionButton add_button, edit_button, delete_all_button;
    ImageView empty_imageview;
    TextView no_data;
    DatabaseHelper DB;
    ArrayList<String> book_id, book_title, book_author, book_pages, book_price;
    CustomAdapter customAdapter;
    ArrayList<byte[]> book_image;
    View view;
    Animation rotateOpen, rotateClose, fromBottom, toBottom;
    Boolean clicked = false;

    public BookManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_book_manager, container, false);

        recyclerView = view.findViewById(R.id.recycleView);
        add_button = view.findViewById(R.id.add_button);
        edit_button = view.findViewById(R.id.edit_button);
        delete_all_button = view.findViewById(R.id.delete_all_button);
        empty_imageview = view.findViewById(R.id.empty_imageView);
        no_data = view.findViewById(R.id.no_data);
        rotateOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.to_bottom_anim);

        add_button.setOnClickListener(v -> {
            onAddButtonClicked();
        });
        edit_button.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddActivity.class);
            startActivity(intent);
        });
        delete_all_button.setOnClickListener(v -> {
            confirmDialog();
        });

        DB = new DatabaseHelper(getActivity());
        book_id = new ArrayList<>();
        book_title = new ArrayList<>();
        book_author = new ArrayList<>();
        book_pages = new ArrayList<>();
        book_price = new ArrayList<>();
        book_image = new ArrayList<>(); // initialize to empty byte array

        storeDataInArrayList();

        customAdapter = new CustomAdapter(getActivity(), getActivity(), book_id, book_title, book_author,book_pages, book_price, book_image);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    private void onAddButtonClicked(){
        setVisibility(clicked);
        setAnimation(clicked);

        clicked = !clicked;
    }

    private void setVisibility(Boolean clicked){
        if(!clicked){
            edit_button.setVisibility(View.VISIBLE);
            delete_all_button.setVisibility(View.VISIBLE);
        }else{
            edit_button.setVisibility(View.INVISIBLE);
            delete_all_button.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(Boolean clicked){
        if(!clicked) {
            edit_button.startAnimation(fromBottom);
            delete_all_button.startAnimation(fromBottom);
            add_button.startAnimation(rotateOpen);
        }else{
            edit_button.startAnimation(toBottom);
            delete_all_button.startAnimation(toBottom);
            add_button.startAnimation(rotateClose);
        }
    }

    public void storeDataInArrayList(){
        Cursor cursor = DB.readAllData();
        if(cursor.getCount() == 0){
            empty_imageview.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        }else{
            while (cursor.moveToNext()){
                book_id.add(cursor.getString(0));
                book_title.add(cursor.getString(1));
                book_author.add(cursor.getString(2));
                book_pages.add(cursor.getString(3));
                book_price.add(cursor.getString(4));
                book_image.add(cursor.getBlob(5));
            }

            empty_imageview.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete All?");
        builder.setMessage("Are you sure you want to delete all data?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper DB = new DatabaseHelper(getActivity());
                DB.deleteAllData();
                // Refresh activity back to Main activity
                loadFragment(new BookManagerFragment());
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

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}