package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
        TextView textView1=(TextView)findViewById(R.id.starting_date);
        TextView textView2=(TextView)findViewById(R.id.min);
        TextView textView3=(TextView)findViewById(R.id.max);
        TextView textView4=(TextView)findViewById(R.id.URL);
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
        try {
            textView1.setText("Starting date: "+jsonObject.getString("localDate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            textView2.setText("Min price: $"+jsonObject.getString("min"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            textView3.setText("Max price: $"+jsonObject.getString("max"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            textView4.setText("URL: "+jsonObject.getString("url"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button favoriteButton = findViewById(R.id.favorite_button);
        Button help2 = findViewById(R.id.help_button);
        if(getIntent().hasExtra("FAVORITE")){
            favoriteButton.setVisibility(View.INVISIBLE);
            help2.setVisibility(View.INVISIBLE);
        }
        else{
            favoriteButton.setOnClickListener( click -> {
                ContentValues newRowValues = new ContentValues();
                //Log.d("sssssssss", getIntent().getStringExtra("JSONSTRING"));
                newRowValues.put(MyOpener.COL_MESSAGE, getIntent().getStringExtra("JSONSTRING"));
                db.insert(MyOpener.TABLE_NAME, null, newRowValues);
            });

            help2.setOnClickListener(v ->{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.TicketFavoriteHelp) + ": ")
                        .setMessage(getResources().getString(R.string.TicketFavoriteSaveAs))
                        .setNeutralButton(getResources().getString(R.string.ticketAlertNB1), (click, b) -> { })
                        .create().show();});

        }

    }

}