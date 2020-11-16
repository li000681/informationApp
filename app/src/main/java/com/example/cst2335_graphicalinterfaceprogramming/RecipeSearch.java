package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class RecipeSearch extends AppCompatActivity {
/** prefs is used to store the recipe and ingredients input by user last time.*/
    SharedPreferences prefs = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search);
        prefs = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        /** savedString is used to store the recipe */
        String savedString = prefs.getString("Recipe", "");
        /** et is the editText where users input the recipe*/
        EditText et = findViewById(R.id.editRecipe);
        /** set the text to be the recipe input by user last time.*/
        et.setText(savedString);
        /** When users input, there is a toast indicating that only one recipe can be accepted.*/
        et.setOnClickListener(v -> Toast.makeText(RecipeSearch.this, getResources().getString(R.string.Recipe_toast_message), Toast.LENGTH_LONG).show());
        /** savedString1 is used to store the ingredients */
        String savedString1 = prefs.getString("Ingredients", "");
        /** et1 is the editText where users input the ingredients*/
        EditText et1 = findViewById(R.id.editIngredients);
        /** set the text to be the ingredients input by user last time.*/
        et1.setText(savedString1);
        /** When users input, there is a snackBar indicating that ingredients should be separated by comma.*/
        et1.setOnClickListener(v ->Snackbar.make(et1,getResources().getString(R.string.Ingredients_snackBar_message),Snackbar.LENGTH_LONG));
        /** help button is for help information which would show in AlertDialog.*/
        Button help = findViewById(R.id.recipeHelpButton);
        help.setOnClickListener(v ->{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.RecipeHelp) + ": ")
                    .setMessage(getResources().getString(R.string.WelcomeRecipeSearch)+getResources().getString(R.string.editRecipe))
                    .setNeutralButton(getResources().getString(R.string.recipeAlertNB), (click, b) -> { })
                    .create().show();});
        /** search button is for search information which would show in next activity.*/
        Button search = findViewById(R.id.button);
        search.setOnClickListener(bt -> {
            /** the recipe and ingredients would be storage in files in the cellphone.*/
            saveSharedPrefs(et.getText().toString());
            saveSharedPrefs1(et1.getText().toString());
            Intent goToProfile = new Intent(RecipeSearch.this, ListOfRecipes.class);
            goToProfile.putExtra("Recipe",et.getText().toString());
            goToProfile.putExtra("Ingredients",et1.getText().toString());
            startActivity(goToProfile);

        });






    }

    @Override
    protected void onPause () {


        super.onPause();
       // Log.e(ACTIVITY_NAME, "In function: onPause");
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

}

