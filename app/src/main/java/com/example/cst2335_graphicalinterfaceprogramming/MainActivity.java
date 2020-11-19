package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button TicketMasterButton = findViewById(R.id.button1);
        Intent nextPage = new Intent(MainActivity.this, TicketMasterActivity.class);
        TicketMasterButton.setOnClickListener(click->
        {
            startActivity(nextPage);
        });

        Button TheAudioDbButton = findViewById(R.id.button4);
        Intent audioDb = new Intent(this, TheAudioDatabase.class);
        TheAudioDbButton.setOnClickListener(click->
        {
            startActivity(audioDb);
        });
      
        /** Button ReceipeSearchButton will lead to the recipe search page.*/
        Button ReceipeSearchButton = findViewById(R.id.button2);
        Intent nextPage1 = new Intent(MainActivity.this, RecipeSearch.class);
        ReceipeSearchButton.setOnClickListener(click->
        {
            startActivity(nextPage1);
        });
    }
}