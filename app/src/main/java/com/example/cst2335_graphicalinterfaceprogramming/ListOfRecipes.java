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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
/**
 * Class shows a list of favorite recipes, press the recipe to check the details and  allows user to delete a recipe from the list
 * @author Jianchuan Li
 * @version 1.0
 */
public class ListOfRecipes extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public final static String ITEM_TITLE = "TITLE";
    public final static String ITEM_URL = "URL";
    public final static String ITEM_INGREDIENTS = "INGREDIENTS";
    public final static String ITEM_ID = "_id";
    ArrayList<Recipes> elements = new ArrayList<>();
    private ListOfRecipes.MyListAdapter  myAdapter;
    private SQLiteDatabase db;
    //boolean isTablet;
    ListView recipeList;
   // RecipeSearchDetailsFragment dFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_recipes);
        //isTablet = findViewById(R.id.frameLayout) != null;
        recipeList = (ListView) findViewById(R.id.recipeList);
        loadDataFromDatabase();
        recipeList.setAdapter(myAdapter = new ListOfRecipes.MyListAdapter());
  //      isTablet= findViewById(R.id.fragmentLocation) != null;
        /**get a database connection*/



/** When long press the recipe, there is an AlertDialog showing the recipe title, URL and ingredients.*/
        recipeList.setOnItemLongClickListener((parent, view, pos, id) -> {

            showMessage(pos);

            return true;
        });
        recipeList.setOnItemClickListener((list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_TITLE, elements.get(position).getTitle() );

            dataToPass.putLong(ITEM_ID, id);
            dataToPass.putString(ITEM_URL, elements.get(position).getHref());
            dataToPass.putString(ITEM_INGREDIENTS, elements.get(position).getIngredients());
            Intent nextActivity = new Intent(ListOfRecipes.this, RecipeFavoriteDetail.class);
            nextActivity.putExtras(dataToPass); //send data to next activity
            startActivity(nextActivity);


        });
        Toolbar tBar = (Toolbar)findViewById(R.id.recipeSearchtoolbar1);

        /**This loads the toolbar, which calls onCreateOptionsMenu below*/
        setSupportActionBar(tBar);
        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.recipeSearchdrawer_layout1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.recipeSearchopen, R.string.recipeSearchclose);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setResult(500);

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
                Intent nextPage = new Intent(this, ListOfRecipes.class);
                startActivity(nextPage);
                break;



            case R.id.recipeSearchHelp:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.RecipeHelp) + ": ")
                        .setMessage(getResources().getString(R.string.WelcomeRecipeSearch)+getResources().getString(R.string.editRecipe))
                        .setNeutralButton(getResources().getString(R.string.recipeAlertNB), (click, b) -> { })
                        .create().show();
                break;
            case R.id.recipeSearchLogin:
                Intent nextPage1 = new Intent(this, MainActivity.class);
                startActivity(nextPage1);
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

            case R.id.navigation_home:
                Intent nextPage1 = new Intent(this, MainActivity.class);
                startActivity(nextPage1);
                break;
            case R.id.navigation_recipe:
                Intent nextPage2 = new Intent(this, TicketMasterActivity.class);
                startActivity(nextPage2);
                break;
            case R.id.navigation_covid:
                Intent nextPage3 = new Intent(this,  Covid19Activity.class);
                startActivity(nextPage3);
                break;
            case R.id.navigation_audio:
                Intent nextPage4 = new Intent(this,  TheAudioDatabase.class);
                startActivity(nextPage4);
                break;
        }



        DrawerLayout drawerLayout = findViewById(R.id.recipeSearchdrawer_layout1);
        drawerLayout.closeDrawer(GravityCompat.START);


        return false;
    }



         /**
         * The inner class is an adapter for ListView
         */
        private class MyListAdapter extends BaseAdapter {
            /**
             * Method counts how many recipes are in a list
             * @return number of recipes in a list
             */
            @Override
            public int getCount() {
                return elements.size();
            }
            /**
             * Method gets a recipe from a list
             * @param position position of a recipe in a list
             * @return recipe
             */
            @Override
            public Object getItem(int position) {
                return elements.get(position);
            }
            /**
             * Method gets a event's id from a database
             * @param position position of a event in a list
             * @return id of a event
             */
            @Override
            public long getItemId(int position) {
                return elements.get(position).getId();
            }
            /**
             * Method returns a view for a recipe
             * @param position position of a event in a list
             * @param convertView recycled view
             * @param parent view that can contain other views
             * @return view of a event (row to the ListView)
             */
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
    /** showMessage would show the details of recipes when one of the recipes is long clicked */
    protected void showMessage(int position)
    {
        Recipes selectedRecipe = elements.get(position);

        View contact_view = getLayoutInflater().inflate(R.layout.recipe_search_favorite_edit, null);

        TextView rowTitle = contact_view.findViewById(R.id.recipeTitle);
        TextView rowURL = contact_view.findViewById(R.id.recipeURL);
        TextView rowIngredients = contact_view.findViewById(R.id.recipeIngredients);

        //set the fields for the alert dialog
        rowTitle.setText(selectedRecipe.getTitle());
        rowURL.setText(selectedRecipe.getHref());
        rowIngredients.setText(selectedRecipe.getIngredients());


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(selectedRecipe.getTitle())
                .setMessage(getResources().getString(R.string.recipeURL)+ selectedRecipe.getHref()+"\n"+getResources().getString(R.string.recipeIngredients)+selectedRecipe.getIngredients())
                .setView(contact_view) //add the 3 edit texts showing the contact information
                .setNegativeButton(getResources().getString(R.string.recipeSearchDelete), (click, b) -> {
                    deleteMessage(selectedRecipe); //remove the contact from database
                    elements.remove(position);
//                    if(isTablet){
//                        getSupportFragmentManager().beginTransaction().remove(dFragment).commit();finish();}//remove the contact from contact list
                    myAdapter.notifyDataSetChanged(); //there is one less item so update the list
                })
                .setNeutralButton(getResources().getString(R.string.recipeAlertNB), (click, b) -> { })
                .create().show();
    }
    protected void updateMessage(Recipes c)
    {

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

    /**
     * Method loads a list of favorite recipes from database
     */
    protected  void loadDataFromDatabase()
    {
        /**get a database connection*/
        RecipeSearchMyOpener dbOpener = new RecipeSearchMyOpener(this);
        db = dbOpener.getWritableDatabase();
        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {RecipeSearchMyOpener.COL_ID, RecipeSearchMyOpener.COL_TITLE, RecipeSearchMyOpener.COL_URL, RecipeSearchMyOpener.COL_INGREDIENTS};
        //query all the results from the database:
        Cursor results = db.query(false, RecipeSearchMyOpener.TABLE_NAME, columns, null, null, null, null, null, null);


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
        Toast toast;
        if(elements.size() > 0) {
            toast = Toast.makeText(this.getApplicationContext(), elements.size()+" "+getResources().getString(R.string.recipeLoad), Toast.LENGTH_LONG);
        }
        else {
            toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.recipeNoFound), Toast.LENGTH_LONG);
        }
        toast.show();
        }
        //At this point, the contactsList array has loaded every row from the cursor.
    }
