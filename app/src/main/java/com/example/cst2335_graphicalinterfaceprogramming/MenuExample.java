package com.example.cst2335_graphicalinterfaceprogramming;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.cst2335_graphicalinterfaceprogramming.R;
import com.google.android.material.navigation.NavigationView;

public class MenuExample extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_example);

        //This gets the toolbar from the layout:
        Toolbar tBar = (Toolbar)findViewById(R.id.toolbar);

        //This loads the toolbar, which calls onCreateOptionsMenu below:
        setSupportActionBar(tBar);

        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);


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
            case R.id.search_item:
                Intent nextToolbarPage4 = new Intent(MenuExample.this, TicketMasterActivity.class);
                startActivity( nextToolbarPage4 );
                break;
            case R.id.favorite_item:
                Intent nextPage5 = new Intent(MenuExample.this, FavoriteTicketActivity.class);
                startActivity(nextPage5);
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int d=item.getItemId();
        if (d==R.id.navigation_search){
            Intent nextToolbarPage4 = new Intent(MenuExample.this, TicketMasterActivity.class);
            startActivity( nextToolbarPage4 );
        }
        else if (d==R.id.navigation_favorite){
            Intent nextPage5 = new Intent(MenuExample.this, FavoriteTicketActivity.class);
            startActivity(nextPage5);
        }
        return false;
    }
}