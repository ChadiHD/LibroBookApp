package com.example.librobookstoreapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookListFragment extends Fragment {

    RecyclerView horizontal_rv, horizontal_rv_notitle, vertical_rv;
    ArrayList<String> book_id ,book_title, book_author, book_pages;
    ArrayList<byte[]> book_image;
    HorizontalRvAdapter customAdapter;
    HorizontalRvNoTitleAdapter customAdapter2;
    VerticalRvAdapter customAdapter3;
    DatabaseHelper DB;
    Context context;
    public BookListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        horizontal_rv = view.findViewById(R.id.horizontal_rv);
        horizontal_rv_notitle = view.findViewById(R.id.horizontal_rv_notitle);
        vertical_rv = view.findViewById(R.id.vertical_rv);

        DB = new DatabaseHelper(getActivity());
        book_id = new ArrayList<>();
        book_title = new ArrayList<>();
        book_author = new ArrayList<>();
        book_pages = new ArrayList<>();
        book_image = new ArrayList<>();

        Cursor cursor = DB.readAllData();
        while (cursor.moveToNext()){
            book_id.add(cursor.getString(0));
            book_title.add(cursor.getString(1));
            book_author.add(cursor.getString(2));
            book_pages.add(cursor.getString(3));
            book_image.add(cursor.getBlob(5));
        }


        customAdapter = new HorizontalRvAdapter(getActivity(), book_id, book_title, book_author, book_pages, book_image);
        horizontal_rv.setAdapter(customAdapter);
        horizontal_rv.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));

        customAdapter2 = new HorizontalRvNoTitleAdapter(getActivity(), book_image);
        horizontal_rv_notitle.setAdapter(customAdapter2);
        horizontal_rv_notitle.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));

        customAdapter3 = new VerticalRvAdapter(getActivity(), book_title, book_author, book_image);
        vertical_rv.setAdapter(customAdapter3);
        vertical_rv.setLayoutManager(new GridLayoutManager(getActivity(), 2,
                GridLayoutManager.VERTICAL, false));

        return view;
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    class HorizontalRvAdapter extends RecyclerView.Adapter<HorizontalRvAdapter.MyHolder> {
        HorizontalRvAdapter(Context _context,
                            ArrayList _book_id,
                            ArrayList _book_title,
                            ArrayList _book_author,
                            ArrayList _book_pages,
                            ArrayList _book_image){
            context = _context;
            book_id = _book_id;
            book_title = _book_title;
            book_author = _book_author;
            book_pages = _book_pages;
            book_image = _book_image;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.horizontal_rv, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            holder.book_title_txt.setText(String.valueOf(book_title.get(position)));
            holder.book_author_txt.setText(String.valueOf(book_author.get(position)));
            holder.book_pages_txt.setText(String.valueOf(book_pages.get(position)));
            if(book_image != null && book_image.size() > position) {
                // Convert byte[] to Bitmap
                byte[] bookImage = book_image.get(position);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bookImage, 0, bookImage.length);
                holder.book_image_view.setImageBitmap(bitmap);
            }
            holder.mainLayout.setOnClickListener(v -> {
                String bookId = book_id.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("bookId", bookId);
                Fragment fragment = new BookOrderFragment();
                fragment.setArguments(bundle);
                loadFragment(fragment);
            });
        }

        @Override
        public int getItemCount() {
            return book_title.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView book_title_txt, book_author_txt, book_pages_txt;
            ImageView book_image_view;
            CardView mainLayout;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                book_title_txt = itemView.findViewById(R.id.book_title_txt);
                book_author_txt = itemView.findViewById(R.id.book_author_txt);
                book_pages_txt = itemView.findViewById(R.id.book_pages_txt);
                book_image_view = itemView.findViewById(R.id.book_image_view);
                mainLayout = itemView.findViewById(R.id.mainLayout);
            }
        }
    }

    class HorizontalRvNoTitleAdapter extends RecyclerView.Adapter<HorizontalRvNoTitleAdapter.MyHolder> {
        HorizontalRvNoTitleAdapter(Context _context,
                                   ArrayList _book_image){
            context = _context;
            book_image = _book_image;
        }

        @NonNull
        @Override
        public HorizontalRvNoTitleAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.horizontal_rv_no_title, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            if(book_image != null && book_image.size() > position) {
                // Convert byte[] to Bitmap
                byte[] bookImage = book_image.get(position);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bookImage, 0, bookImage.length);
                holder.book_image_view.setImageBitmap(bitmap);
                holder.mainLayout.setOnClickListener(v -> {
                    String bookId = book_id.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("bookId", bookId);
                    Fragment fragment = new BookOrderFragment();
                    fragment.setArguments(bundle);
                    loadFragment(fragment);
                });
            }
        }

        @Override
        public int getItemCount() {
            return book_title.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            ImageView book_image_view;
            CardView mainLayout;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                book_image_view = itemView.findViewById(R.id.book_image_view);
                mainLayout = itemView.findViewById(R.id.mainLayout);
            }
        }
    }

    class VerticalRvAdapter extends RecyclerView.Adapter<VerticalRvAdapter.MyHolder> {
        VerticalRvAdapter(Context _context,
                          ArrayList _book_title,
                          ArrayList _book_author,
                          ArrayList _book_image){
            context = _context;
            book_title = _book_title;
            book_author = _book_author;
            book_image = _book_image;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.vertical_rv, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VerticalRvAdapter.MyHolder holder, int position) {
            holder.book_title_txt.setText(String.valueOf(book_title.get(position)));
            holder.book_author_txt.setText(String.valueOf(book_author.get(position)));
            if(book_image != null && book_image.size() > position) {
                // Convert byte[] to Bitmap
                byte[] bookImage = book_image.get(position);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bookImage, 0, bookImage.length);
                holder.book_image_view.setImageBitmap(bitmap);
            }
            holder.mainLayout.setOnClickListener(v -> {
                String bookId = book_id.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("bookId", bookId);
                Fragment fragment = new BookOrderFragment();
                fragment.setArguments(bundle);
                loadFragment(fragment);
            });
        }

        @Override
        public int getItemCount() {
            return book_title.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView book_title_txt, book_author_txt;
            ImageView book_image_view;
            CardView mainLayout;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                book_title_txt = itemView.findViewById(R.id.book_title_txt);
                book_author_txt = itemView.findViewById(R.id.book_author_txt);
                book_image_view = itemView.findViewById(R.id.book_image_view);
                mainLayout = itemView.findViewById(R.id.mainLayout);
            }
        }

    }
}