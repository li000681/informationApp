package com.example.cst2335_graphicalinterfaceprogramming;


import androidx.annotation.NonNull;
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
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The class is the main responsible the main page funtion
 *  @author June Li
 * @version 1.0
 */
public class Covid19Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    SharedPreferences prefs=null;
    ArrayList<String> dateList= new ArrayList<>();
    private SQLiteDatabase db;
    SavedAdapter savedAdapter=new SavedAdapter();
    ListView savedView;
    CovidDetailsFragment dFragment = new CovidDetailsFragment();


    @Override
    /**
     * The method is the entry of execute,it equivalent to main method
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid19);

        Toolbar tBar = (Toolbar)findViewById(R.id.toolbar);
        //This loads the toolbar, which calls onCreateOptionsMenu below:
        setSupportActionBar(tBar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        boolean isTablet = findViewById(R.id.fragment) != null;
        //get data from saved file
        prefs=getSharedPreferences("favoriteRecord", Context.MODE_PRIVATE);
        String savedString1 = prefs.getString("country", "");
        String savedString2= prefs.getString("date","");

        EditText country1 = findViewById(R.id.country);
        EditText date1 = findViewById(R.id.date);
        country1.setText(savedString1);
        date1.setText(savedString2);
        savedView=findViewById(R.id.savedData);
        savedView.setOnItemLongClickListener((p, b, pos, id)->{
            String selectedRecord = dateList.get(pos);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you want to delete it?")
                    //What is the message:
                    .setMessage("The country is: " + selectedRecord.substring(11) + "\nThe date is: "+selectedRecord.substring(0,10))
                    .setPositiveButton("Yes", (click, arg) -> {
                        dateList.remove(pos);
                        deleteRecord(selectedRecord);
                        getSupportFragmentManager().beginTransaction().remove(dFragment).commit();
                        savedAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("No", (click, arg) -> {
                    })
                    .setView(getLayoutInflater().inflate(R.layout.row_layout, null))
                    .create().show();
            return true;
        });
        savedView.setOnItemClickListener((list, view, position, id) -> {
            //get the detailed list data
            String s=dateList.get(position);
            String [] columns1 = {CovidOpener.COL_PROVINCE,CovidOpener.COL_CASE};
            Cursor detailResults=db.query(true,CovidOpener.TABLE_NAME,columns1, CovidOpener.COL_DATE + "= ? and "+CovidOpener.COL_COUNTRY+" =?",
                    new String[]{s.substring(0,10),s.substring(11)},null,null,null,null);
            int provinceColIndex = detailResults.getColumnIndex(CovidOpener.COL_PROVINCE);
            int caseColIndex = detailResults.getColumnIndex(CovidOpener.COL_CASE);
            ArrayList<String> detailList= new ArrayList<>();
            while(detailResults.moveToNext()) {
                String s1 = detailResults.getString(provinceColIndex);
                String s2 = detailResults.getString(caseColIndex);
                detailList.add(s1 + ":" + s2);
            }
            //create a bundle to transfer data
            Bundle dataToPass = new Bundle();
            dataToPass.putSerializable("ARRAYLIST", (Serializable) detailList);
            if (isTablet) {
                // DetailsFragment dFragment = new DetailsFragment(); //add a DetailFragment
                dFragment.setArguments(dataToPass);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, dFragment).commit();
            }
            //for Phone:
            else {
                Intent nextActivity = new Intent(this, CovidEmptyActivity.class);
                //nextActivity.putExtras(dataToPass);
                nextActivity.putExtra("BUNDLE", dataToPass);
                startActivity(nextActivity);
            }
        });

        Button searchButton = findViewById(R.id.entrySearch);
        searchButton.setOnClickListener(clk -> {
            String country=country1.getText().toString();
            String date=date1.getText().toString();
            if(country.trim().equals("")||date.trim().equals("")||!date.matches("\\d{4}[-.]\\d{1,2}[-.]\\d{1,2}")){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.CovidErrorTitle))
                .setMessage(getResources().getString(R.string.CovidErrorMsg))
                .setNeutralButton("OK",(click,args)->{})
                .create().show();
            }else {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                searchIntent.putExtra("country", country);
                searchIntent.putExtra("date", date);

                saveSharedPrefs(country, date);
                startActivity(searchIntent);
            }
        });



    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     *
     * <p>This is only called once, the first time the options menu is
     * displayed.
     * <p>The default implementation populates the menu with standard system
     * menu items.
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.covidmenu, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.item1:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("How to use:")
                        .setMessage(getResources().getString(R.string.WelcomeCovidSearch))
                        .setNeutralButton("OK", (click, b) -> { })
                        .setView(getLayoutInflater().inflate(R.layout.alert_layout, null))
                        .create().show();
                break;
            case R.id.item2:
                loadSavedDataFromDatabase();
                savedView.setAdapter(savedAdapter);
                break;
        }
        return true;
    }
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menu1:
                startActivity(new Intent(this,TicketMasterActivity.class));
                break;
            case R.id.menu2:
                startActivity(new Intent(this,RecipeSearchToolBar.class));
                break;
            case R.id.menu3:
                startActivity(new Intent(this,TheAudioDatabase.class));
                break;
            case R.id.menu4:
                finish();
                break;

        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    /**
     * This method is called when click search button to save the input content to a file.
     * @param s1,s2 these two parameters are the content to save.
     */
    private void saveSharedPrefs(String s1,String s2) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("country", s1);
        editor.putString("date",s2);
        editor.commit();
    }
    /**
     * This method is called to load the saved data from database.
     */
    private void loadSavedDataFromDatabase() {
        //get a database connection:
        CovidOpener covidOpener = new CovidOpener(this);
        db = covidOpener.getWritableDatabase();
        String [] columns = { CovidOpener.COL_DATE,CovidOpener.COL_COUNTRY};
        Cursor results = db.query(true, CovidOpener.TABLE_NAME, columns, null, null, null, null,CovidOpener.COL_DATE , null);
        //find the column index:
        int dateColIndex = results.getColumnIndex(CovidOpener.COL_DATE);
        int countryColIndex = results.getColumnIndex(CovidOpener.COL_COUNTRY);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext()){
            String  s1 = results.getString(dateColIndex);
            String s2=results.getString(countryColIndex);
            dateList.add(s1+" "+s2);
        }
    }
    /**
     * This method is called to delete the records from the saved data in database.
     */
    protected void deleteRecord(String s) {
        db.delete(CovidOpener.TABLE_NAME, CovidOpener.COL_DATE + "= ? and "+CovidOpener.COL_COUNTRY+" =?",
                new String[]{s.substring(0,10),s.substring(11)});
    }

    protected class SavedAdapter extends BaseAdapter {
        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return dateList.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public String getItem(int position){
            return dateList.get(position);
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param old         The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int position, View old, ViewGroup parent) {
            String sr = getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.activity_search_result, parent, false);
            if (sr != null) {
                TextView savedView = view.findViewById(R.id.searchResult);
                savedView.setText(sr);
            }
            return view;
        }
        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}