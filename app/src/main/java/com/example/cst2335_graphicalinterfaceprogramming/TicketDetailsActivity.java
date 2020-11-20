package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TicketDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        JSONObject jsonObject = null;
        Bitmap image = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_details);
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
    }
}