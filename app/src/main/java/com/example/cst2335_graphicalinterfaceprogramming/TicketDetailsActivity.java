package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TicketDetailsActivity extends AppCompatActivity {
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        JSONObject jsonObject = null;
        Bitmap image = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_details);
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();
        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("JSONSTRING"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ImageView imageView=(ImageView)findViewById(R.id.imageView);
        FileInputStream fis = null;
        try {
            fis = openFileInput(jsonObject.getString("imgName"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("FILE", "The icon file exists!");
        image = BitmapFactory.decodeStream(fis);
        imageView.setImageBitmap(image);

        Button favoriteButton = findViewById(R.id.favorite_button);
        favoriteButton.setOnClickListener( click -> {
            ContentValues newRowValues = new ContentValues();
            //Log.d("sssssssss", getIntent().getStringExtra("JSONSTRING"));
            newRowValues.put(MyOpener.COL_MESSAGE, getIntent().getStringExtra("JSONSTRING"));
            db.insert(MyOpener.TABLE_NAME, null, newRowValues);
        });
    }

}