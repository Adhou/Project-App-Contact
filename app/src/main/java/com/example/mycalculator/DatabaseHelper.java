package com.example.mycalculator;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

 public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "contacts.db";
    private static final String TABLE_NAME = "contacts";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "NAME";
    private static final String COL_3 = "EMAIL";
    private static final String COL_4 = "PHONE_HOME";
    private static final String COL_5 = "PHONE_OFFICE";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, EMAIL TEXT, PHONE_HOME TEXT, PHONE_OFFICE TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
     public int insertData(String name, String email, String homePhone, String officePhone) {
         SQLiteDatabase db = this.getWritableDatabase();
         //same phone number exists
         Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE PHONE_HOME = ? OR PHONE_OFFICE = ?", new String[]{homePhone, officePhone});
         if (cursor.getCount() > 0) {
             cursor.close();
             return -1;  // phone number already exists
         }
         cursor.close();
         ContentValues contentValues = new ContentValues();
         contentValues.put("NAME", name);
         contentValues.put("EMAIL", email);
         contentValues.put("PHONE_HOME", homePhone);
         contentValues.put("PHONE_OFFICE", officePhone);

         long result = db.insert(TABLE_NAME, null, contentValues);

         if (result == -1) {
             return 0;  //if there was an error inserting
         } else {
             return 1;  //inserted successfully
         }
     }
//update
public int updateData(String name, String email, String homePhone, String officePhone) {
         SQLiteDatabase db = this.getWritableDatabase();
         ContentValues contentValues = new ContentValues();
         contentValues.put("NAME", name);
         contentValues.put("EMAIL", email);
         contentValues.put("PHONE_HOME", homePhone);
         contentValues.put("PHONE_OFFICE", officePhone);

         //Update contacts where the home phone or office phone matches the given phone number
         int result = db.update(TABLE_NAME, contentValues, "PHONE_HOME = ? OR PHONE_OFFICE = ?", new String[] {homePhone, officePhone});
         if (result > 0) {
             return 1;  //updated successfully
         } else {
             return 0;  // an error updating
         }
     }
//show all data
     public List<String> getAllContacts() {
        List<String> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COL_2));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COL_3));
                @SuppressLint("Range") String phoneHome = cursor.getString(cursor.getColumnIndex(COL_4));
                @SuppressLint("Range") String phoneOffice = cursor.getString(cursor.getColumnIndex(COL_5));
                contacts.add("Name: " + name + "\nEmail: " + email + "\nHome Phone: " + phoneHome + "\nOffice Phone: " + phoneOffice);
            } while (cursor.moveToNext());
        } else {
            contacts.add("No Contact Saved");
        }
        cursor.close();
        db.close();
        return contacts;
    }
//delete
     public int deleteData(String homePhone, String officePhone) {
         SQLiteDatabase db = this.getWritableDatabase();
         // Delete where the home phone or office phone matches the given phone number
         int result = db.delete(TABLE_NAME, "PHONE_HOME = ? OR PHONE_OFFICE = ?", new String[] {homePhone, officePhone});
         return result;
     }

 }
