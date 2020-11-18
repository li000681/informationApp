package com.example.cst2335_graphicalinterfaceprogramming;


import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        TextView c=findViewById(R.id.country);
        TextView p=findViewById(R.id.province);
        TextView ca=findViewById(R.id.cases);
        TextView d=findViewById(R.id.date);
        c.setText(getIntent().getStringExtra("country"));
        p.setText(getIntent().getStringExtra("province"));
        ca.setText(getIntent().getStringExtra("cases"));
        d.setText(getIntent().getStringExtra("date"));

    }
}


