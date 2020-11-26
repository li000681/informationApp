package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class RecipeSearchToolBar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    /** prefs is used to store the recipe and ingredients input by user last time.*/

    SharedPreferences prefs = null;
    private SQLiteDatabase db;

    RecipeSearchDetailsFragment dFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search_tool_bar);
        prefs = getSharedPreferences("FileName", Context.MODE_PRIVATE);

        /** savedString is used to store the recipe */
        String savedString = prefs.getString("Recipe", "");
        /** et is the editText where users input the recipe*/
        EditText et = findViewById(R.id.editRecipe);
        /** set the text to be the recipe input by user last time.*/
        et.setText(savedString);
        /** When users input, there is a toast indicating that only one recipe can be accepted.*/
        et.setOnClickListener(v -> Toast.makeText(this, getResources().getString(R.string.Recipe_toast_message), Toast.LENGTH_LONG).show());
        /** savedString1 is used to store the ingredients */
        String savedString1 = prefs.getString("Ingredients", "");
        /** et1 is the editText where users input the ingredients*/
        EditText et1 = findViewById(R.id.editIngredients);
        /** set the text to be the ingredients input by user last time.*/
        et1.setText(savedString1);
        /** When users input, there is a snackBar indicating that ingredients should be separated by comma.*/
        et1.setOnClickListener(v -> Snackbar.make(et1,getResources().getString(R.string.Ingredients_snackBar_message),Snackbar.LENGTH_LONG).show());
        /** help button is for help information which would show in AlertDialog.*/
//        Button help = findViewById(R.id.recipeHelpButton);
//        help.setOnClickListener(v ->{
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(getResources().getString(R.string.RecipeHelp) + ": ")
//                    .setMessage(getResources().getString(R.string.WelcomeRecipeSearch)+getResources().getString(R.string.editRecipe))
//                    .setNeutralButton(getResources().getString(R.string.recipeAlertNB), (click, b) -> { })
//                    .create().show();});
        /** search button is for search information which would show in next activity.*/
        Button search = findViewById(R.id.button);
        search.setOnClickListener(bt -> {
            /** the recipe and ingredients would be storage in files in the cellphone.*/
            saveSharedPrefs(et.getText().toString());
            saveSharedPrefs1(et1.getText().toString());
            Intent goToProfile = new Intent(this, ListOfRecipes.class);
            goToProfile.putExtra("Recipe",et.getText().toString());
            goToProfile.putExtra("Ingredients",et1.getText().toString());
            startActivity(goToProfile);});

            //This gets the toolbar from the layout:
        Toolbar tBar = (Toolbar)findViewById(R.id.recipeSearchtoolbar);

        /**This loads the toolbar, which calls onCreateOptionsMenu below*/
        setSupportActionBar(tBar);
        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.recipeSearchdrawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.recipeSearchopen, R.string.recipeSearchclose);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setResult(500);

    }

    /** saveSharedPrefs is used to store the recipe input by user last time.*/
    private void saveSharedPrefs(String stringToSave){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Recipe", stringToSave);
        editor.commit();
    }
    /** saveSharedPrefs1 is used to store the ingredients input by user last time.*/
    private void saveSharedPrefs1 (String stringToSave){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Ingredients", stringToSave);
        editor.commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_search_menu, menu);


	    /* slide 15 material:
	    MenuItem searchItem = menu.findItem(R.id.search_item);
        SearchView sView = (SearchView)searchItem.getActionView();
        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }  });

	    */

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.recipeSearchFavorite:
                message = "You clicked recipe_search_favorite.png item";
                break;
            case R.id.recipeSearchHelp:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.RecipeHelp) + ": ")
                        .setMessage(getResources().getString(R.string.WelcomeRecipeSearch)+getResources().getString(R.string.editRecipe))
                        .setNeutralButton(getResources().getString(R.string.recipeAlertNB), (click, b) -> { })
                        .create().show();
                break;
            case R.id.recipeSearchLogin:
                message = "You clicked on the overflow menu";
                break;

        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
    }
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {

        String message = null;

        switch(item.getItemId())
        {
            case R.id.recipeSearchFavorite:
                message = "You clicked recipe_search_favorite.png item";
                break;
            case R.id.recipeSearchHelp:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.RecipeHelp) + ": ")
                        .setMessage(getResources().getString(R.string.WelcomeRecipeSearch)+getResources().getString(R.string.editRecipe))
                        .setNeutralButton(getResources().getString(R.string.recipeAlertNB), (click, b) -> { })
                        .create().show();
                break;
            case R.id.recipeSearchLogin:
                finish();
                break;

        }

        DrawerLayout drawerLayout = findViewById(R.id.recipeSearchdrawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        //Toast.makeText(this, "NavigationDrawer: " + message, Toast.LENGTH_LONG).show();
        return false;
    }

}
