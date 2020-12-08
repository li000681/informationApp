package com.example.cst2335_graphicalinterfaceprogramming;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
/**
 * The class is used to show the recipe detail according to user's choice of specific recipe
 *  @author Jianchuan Li
 * @version 1.0
 */
public class RecipeFavoriteDetail extends AppCompatActivity {
    /**
     * Method used to retrieve required data(title, URL,ingredients)
     * from previous activity and set value to TextView
     *
     * @param savedInstanceState reference to a Bundle object that is passed into the onCreate method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_favorite_detail);

        Intent intent = getIntent();

        String title = intent.getStringExtra(ListOfRecipes.ITEM_TITLE);
        String url = intent.getStringExtra(ListOfRecipes.ITEM_URL) + "\n";
        String ingredients = intent.getStringExtra(ListOfRecipes.ITEM_INGREDIENTS);


        //show the title
        TextView message = (TextView) findViewById(R.id.recipeTitle);
        message.setText(getResources().getString(R.string.inputRecipe) + title);

        //show the URL:
        TextView linkView = (TextView) findViewById(R.id.recipeURL);
        linkView.setText(getResources().getString(R.string.recipeURL) + url);

        //show the ingredients
        TextView ingredientsView = (TextView) findViewById(R.id.recipeIngredients);
        ingredientsView.setText(getResources().getString(R.string.recipeIngredients) + ingredients);

        Button urlButton = findViewById(R.id.recipeURL);
        urlButton.setOnClickListener(click->{
            Intent nextActivity = new Intent(Intent.ACTION_VIEW);
            nextActivity.setData(Uri.parse(url));
            startActivity(nextActivity);
        });
        Button hide=findViewById(R.id.finish);
        hide.setOnClickListener(click->{

            finish();
        });
    }
}