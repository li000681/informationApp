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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * The class is the main responsible the main page funtion
 *  @author Jianchuan Li
 * @version 1.0
 */
public class RecipeSearchToolBar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    /** prefs is used to store the recipe and ingredients input by user last time.*/

    SharedPreferences prefs = null;
    private SQLiteDatabase db;
    ArrayList<Recipes> elements = new ArrayList<>();
    RecipeSearchDetailsFragment dFragment;
    ListOfRecipes lr= new ListOfRecipes();
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
                    if(et.getText().toString().trim().equals("")||et1.getText().toString().trim().equals("")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                        alertDialogBuilder.setTitle(getResources().getString(R.string.RecipeInputError))
                                .setMessage(getResources().getString(R.string.RecipeErrorMessage))
                                .setNeutralButton("OK", (click, args) -> {
                                })
                                .create().show();
                    }else{
            saveSharedPrefs(et.getText().toString());
            saveSharedPrefs1(et1.getText().toString());
            Intent goToProfile = new Intent(this, ListOfRecipes.class);
            goToProfile.putExtra("Recipe",et.getText().toString());
            goToProfile.putExtra("Ingredients",et1.getText().toString());
            startActivity(goToProfile);}});
        ListView favoriteList = (ListView)findViewById(R.id.listfavorite);
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

    /**
     * Initialize the contents of the Activity's standard options menu.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_search_menu, menu);




        return true;
    }
    /**
     * This method is called whenever an item in your options menu is selected.
     *
     * @param item The menu item that was selected. Help would show the help file, go to login would lead to main page,
     * favorate leads to saved recipes/
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.recipeSearchFavorite:
                lr.elements.clear();
                loadDataFromDatabase();
                lr.recipeList.setAdapter(lr.myAdapter);

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

        return true;
    }
    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item like in ToolBar Items.
     * @return true to display the item as the selected item
     */
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


        return false;
    }
    private void loadDataFromDatabase()
    {
        //get a database connection:
        RecipeSearchMyOpener dbOpener = new RecipeSearchMyOpener(this);
        //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer

        db = dbOpener.getWritableDatabase();
        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {RecipeSearchMyOpener.COL_ID, RecipeSearchMyOpener.COL_TITLE, RecipeSearchMyOpener.COL_URL, RecipeSearchMyOpener.COL_INGREDIENTS};
        //query all the results from the database:
        Cursor results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);


        //Now the results object has rows of results that match the query.
        //find the column indices:
        int titleIndex = results.getColumnIndex(RecipeSearchMyOpener.COL_TITLE);
        int urlIndex = results.getColumnIndex(RecipeSearchMyOpener.COL_URL);
        int ingredientsIndex=results.getColumnIndex(RecipeSearchMyOpener.COL_INGREDIENTS);
        int idColIndex = results.getColumnIndex(RecipeSearchMyOpener.COL_ID);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String title = results.getString(titleIndex);
            String url = results.getString(urlIndex);
            String ingredients = results.getString(ingredientsIndex);
            long id = results.getLong(idColIndex);
            // Log.i(String.valueOf(id),msg+sendButtonIsClicked);
            //add the new Contact to the array list:
            elements.add(new Recipes(id, title, url,ingredients));
        }

        //At this point, the contactsList array has loaded every row from the cursor.
    }
    private class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return elements.size();
        }

        @Override
        public Object getItem(int position) {
            return elements.get(position);
        }

        @Override
        public long getItemId(int position) {
            return elements.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.recipe_list_view, parent, false);

/** In listView, only the title of recipe would be shown*/
            TextView tView = newView.findViewById(R.id.recipeTitle);
            tView.setText(elements.get(position).getTitle());

            return newView;

        }

    }
    protected void showMessage(int position)
    {
        Recipes selectedRecipe = elements.get(position);

        //View contact_view = getLayoutInflater().inflate(R.layout.message_edit, null);
        //get the TextViews
//        EditText rowName = contact_view.findViewById(R.id.row_name);
//        EditText rowEmail = contact_view.findViewById(R.id.row_email);
//        TextView rowId = contact_view.findViewById(R.id.row_id);

        //set the fields for the alert dialog
//        rowName.setText(selectedMessage.getMsg());
//        rowEmail.setText(selectedMessage.booleanToString(selectedMessage.getSendButtonIsClicked()));
//        rowId.setText("id:" + selectedMessage.getId());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(selectedRecipe.getTitle())
                .setMessage(getResources().getString(R.string.recipeURL)+ selectedRecipe.getHref()+"\n"+getResources().getString(R.string.recipeIngredients)+selectedRecipe.getIngredients())
//                .setView(contact_view) //add the 3 edit texts showing the contact information
//                .setPositiveButton(getResources().getString(R.string.alertUB), (click, b) -> {
//                    selectedMessage.update(rowName.getText().toString(), selectedMessage.stringToBoolean(rowEmail.getText().toString()));
//                    updateMessage(selectedMessage);
//                    myAdapter.notifyDataSetChanged(); //the recipe_search_help.png and name have changed so rebuild the list
//                })
//                .setNegativeButton(getResources().getString(R.string.alertPB), (click, b) -> {
//                    deleteMessage(selectedMessage); //remove the contact from database
//                    elements.remove(position);
//                    if(isTablet){
//                        getSupportFragmentManager().beginTransaction().remove(dFragment).commit();}//remove the contact from contact list
//                    myAdapter.notifyDataSetChanged(); //there is one less item so update the list
//                })
                .setNeutralButton(getResources().getString(R.string.recipeAlertNB), (click, b) -> { })
                .create().show();
    }
    protected void updateMessage(Recipes c)
    {
        //get a database connection:
        RecipeSearchMyOpener dbOpener = new RecipeSearchMyOpener(this);
        //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer

        db = dbOpener.getWritableDatabase();
        //Create a ContentValues object to represent a database row:
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(RecipeSearchMyOpener.COL_TITLE, c.getTitle());
        updatedValues.put(RecipeSearchMyOpener.COL_URL, c.getHref());
        updatedValues.put(RecipeSearchMyOpener.COL_INGREDIENTS, c.getIngredients());
        updatedValues.put(RecipeSearchMyOpener.COL_ID, c.getId());

        //now call the update function:
        db.update(RecipeSearchMyOpener.TABLE_NAME, updatedValues, RecipeSearchMyOpener.COL_ID + "= ?", new String[] {Long.toString(c.getId())});
    }

    protected void deleteMessage(Recipes c)
    {
        db.delete(RecipeSearchMyOpener.TABLE_NAME, RecipeSearchMyOpener.COL_ID + "= ?", new String[] {Long.toString(c.getId())});
    }
}
