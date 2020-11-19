package com.example.cst2335_graphicalinterfaceprogramming;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

/**
 * The class is the main responsible the main page funtion
 *  @author June Li
 * @version 1.0
 */
public class Covid19Activity extends AppCompatActivity {
    @Override
    /**
     * The method is the entry of execute,it equivalent to main method
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid19);
        EditText country1 = findViewById(R.id.country);
        EditText fromDate1 = findViewById(R.id.fromDate);
        EditText endDate1 = findViewById(R.id.endDate);
        Button searchButton = findViewById(R.id.entrySearch);
        searchButton.setOnClickListener(clk -> {
            Intent searchIntent = new Intent(this, SearchActivity.class);
            searchIntent.putExtra("country", country1.getText().toString());
            searchIntent.putExtra("fromDate", fromDate1.getText().toString());
            searchIntent.putExtra("endDate", endDate1.getText().toString());
            startActivity(searchIntent);
        });
        Button favoriteButton = findViewById(R.id.favorite);
        favoriteButton.setOnClickListener(clk ->
                startActivity(new Intent(this, FavoriteActivity.class)));
    }
}