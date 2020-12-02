package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    /**
     * The method is the entry of execute,it equivalent to main method
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar tBar = (Toolbar)findViewById(R.id.hometoolbar);
        //This loads the toolbar, which calls onCreateOptionsMenu below:
        setSupportActionBar(tBar);
        DrawerLayout drawer = findViewById(R.id.homedrawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.homenav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
    @Override
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homepagemenu, menu);
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
                startActivity(new Intent(this, TicketMasterActivity.class));
                break;
            case R.id.item2:
                startActivity(new Intent(this,RecipeSearchToolBar.class));
                break;
            case R.id.item3:
                startActivity(new Intent(this,Covid19Activity.class));
                break;
            case R.id.item4:
                startActivity(new Intent(this, TheAudioDatabase.class));
                break;
        }
        return true;
    }
    @Override
    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    public boolean onNavigationItemSelected( MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.item1:
                startActivity(new Intent(this, TicketMasterActivity.class));
                break;
            case R.id.item2:
                startActivity(new Intent(this,RecipeSearchToolBar.class));
                break;
            case R.id.item3:
                startActivity(new Intent(this,Covid19Activity.class));
                break;
            case R.id.item4:
                startActivity(new Intent(this,TheAudioDatabase.class));
                break;

        }

        DrawerLayout drawerLayout = findViewById(R.id.homedrawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}