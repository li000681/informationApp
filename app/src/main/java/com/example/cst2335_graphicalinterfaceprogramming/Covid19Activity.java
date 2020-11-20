package com.example.cst2335_graphicalinterfaceprogramming;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

/**
 * The class is the main responsible the main page funtion
 *  @author June Li
 * @version 1.0
 */
public class Covid19Activity extends AppCompatActivity {
    SharedPreferences prefs=null;
    @Override
    /**
     * The method is the entry of execute,it equivalent to main method
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid19);
        //get data from saved file
        prefs=getSharedPreferences("favoriteRecord", Context.MODE_PRIVATE);
        String savedString1 = prefs.getString("country", "");
        String savedString2= prefs.getString("fromDate","");
        String savedString3= prefs.getString("endDate","");

        EditText country1 = findViewById(R.id.country);
        EditText fromDate1 = findViewById(R.id.fromDate);
        EditText endDate1 = findViewById(R.id.endDate);

        country1.setText(savedString1);
        fromDate1.setText(savedString2);
        endDate1.setText(savedString3);

        Button searchButton = findViewById(R.id.entrySearch);
        searchButton.setOnClickListener(clk -> {
            Intent searchIntent = new Intent(this, SearchActivity.class);
            String country=country1.getText().toString();
            String fromDate=fromDate1.getText().toString();
            String endDate=endDate1.getText().toString();

            searchIntent.putExtra("country", country);
            searchIntent.putExtra("fromDate", fromDate);
            searchIntent.putExtra("endDate", endDate);

            saveSharedPrefs(country,fromDate,endDate);
            startActivity(searchIntent);
        });
        Button favoriteButton = findViewById(R.id.favorite);
        favoriteButton.setOnClickListener(clk ->
                startActivity(new Intent(this, FavoriteActivity.class)));
    }
    private void saveSharedPrefs(String s1,String s2, String s3) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("country", s1);
        editor.putString("fromDate", s2);
        editor.putString("endDate", s3);
        editor.commit();
    }
}