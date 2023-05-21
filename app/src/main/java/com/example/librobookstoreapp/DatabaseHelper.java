package com.example.librobookstoreapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "BookLibrary.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE1 = "my_library";
    private static final String TABLE2 = "Users";
    private static final String TABLE3 = "Purchases";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "book_title";
    private static final String COLUMN_AUTHOR = "book_author";
    private static final String COLUMN_PAGES = "book_pages";
    private static final String COLUMN_PRICE = "book_price";
    private static final String COLUMN_IMAGE = "book_image";

    DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String Table1 = "CREATE TABLE " + TABLE1 + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_PAGES + " INTEGER, " +
                COLUMN_PRICE + " MONEY, " +
                COLUMN_IMAGE + " BLOB)";

        String Table2 = "CREATE TABLE " + TABLE2 + " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "username TEXT, " +
                "password TEXT)";

        String Table3 = "CREATE TABLE " + TABLE3 + " (purchaseId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "dateOfPurchase DATETIME2, " +
                "userId INTEGER, " +
                "bookId INTEGER, " +
                "FOREIGN KEY (userId) REFERENCES " + TABLE2 + "('id'), " +
                "FOREIGN KEY (bookId) REFERENCES " + TABLE1 + "('_id')" +
                ")";

        db.execSQL(Table1);
        db.execSQL(Table2);
        db.execSQL(Table3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE3);
        onCreate(db);
    }

    public void addBook(String title, String author, int pages , BigDecimal price, byte[] image){
        // SQLiteOpenHelper library function
        SQLiteDatabase db = this.getWritableDatabase();
        // Transfer all the data from the app to the table
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_AUTHOR, author);
        cv.put(COLUMN_PAGES, pages);
        cv.put(COLUMN_PRICE, String.valueOf(price));
        cv.put(COLUMN_IMAGE, image);
        long result = db.insert(TABLE1, null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed to insert the book record into Database", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean addUser(String username, String password){
        // SQLiteOpenHelper library function
        SQLiteDatabase db = this.getWritableDatabase();
        // Read all the data from the app to the table
        ContentValues cv = new ContentValues();

        cv.put("username", username);
        cv.put("password", password);
        long result = db.insert(TABLE2, null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed to insert the login record into Database", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public Boolean addPurchase(String userId, String bookId){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        // SQLiteOpenHelper library function
        SQLiteDatabase db = this.getWritableDatabase();
        // Read all the data from the app to the table
        ContentValues cv = new ContentValues();

        cv.put("dateOfPurchase", formattedDate);
        cv.put("userId", userId);
        cv.put("bookId", bookId);
        long result = db.insert(TABLE3, null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed to insert the purchase record into Database", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
            return true;
        }
    }


    public Boolean checkUsername(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE username = ?",
                new String[] {username});
        if(cursor.getCount() > 0){
            return true;
        }else{
            return false;
        }
    }

    public Boolean checkUsernamePassword(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE username = ? AND password = ?",
                new String[] {username, password});
        if(cursor.getCount() > 0){
            return true;
        }else{
            return false;
        }
    }

    public Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE1;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    void updateData(String row_id, String title, String author, String pages, String price, byte[] image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_AUTHOR, author);
        cv.put(COLUMN_PAGES, pages);
        cv.put(COLUMN_PRICE, price);
        cv.put(COLUMN_IMAGE, image);

        long result = db.update(TABLE1, cv, "_id=?", new String[]{row_id});
        if (result == -1){
            Toast.makeText(context, "Failed to update the record", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Record Updated Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE1, "_id=?", new String[]{row_id});
        if (result == -1){
            Toast.makeText(context, "Failed to delete record", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Record Deleted Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE1);
    }

    public byte[] getImage(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE1 + " WHERE _id = ?",
                new String[] {String.valueOf(id)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            byte[] image = cursor.getBlob(5);
            return image;
        }else{
            return null;
        }
    }
}
