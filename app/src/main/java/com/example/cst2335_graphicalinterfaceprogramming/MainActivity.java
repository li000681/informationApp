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

        Button covid=findViewById(R.id.button3);
        covid.setOnClickListener(clk->
                startActivity(new Intent(MainActivity.this, Covid19Activity.class)));
    }
}