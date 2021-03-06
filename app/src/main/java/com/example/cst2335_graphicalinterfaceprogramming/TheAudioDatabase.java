package com.example.cst2335_graphicalinterfaceprogramming;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * The class is the main responsible the main page function
 *  @author Gulmira Kanalinova
 *  @version 1.0
 *  December 5, 2020
 */

/**
 * Main class for the The Audio Database api
 * contains navigation menu with favorites list
 */
public class TheAudioDatabase extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * The variables is used to store data
     */
    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static final String ALBUM_ID = "ALBUM_NUM";
    public static final String URL_IMAGE = "URL_IMAGE";
    /**
     * Fragments used in this activity
     */
    private SearchListFragment searchListFragment;
    private FavoriteTracksFragment favoriteTracksFragment;
    TracksFragment tracksFragment;
    /**
     * DB classes
     */
    SharedPreferences sharedPreferences;
    TheAudioDbHelper dbHelper;
    /**
     * orientation and tablet
     */
    boolean orientationLand;
    boolean isTablet;

    NavigationView navigationView;
    DrawerLayout drawer;

    List<Track> favoriteTracks;

    @Override
    /**
     *The method is the entry of execute,it equivalent to main method
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tad_main);
        dbHelper = new TheAudioDbHelper(this);
        Toolbar tBar = findViewById(R.id.tad_toolbar); // get reference of Toolbar
        setSupportActionBar(tBar); // setting/replace toolbar as ActionBar
        checkOrientation();

        drawer = findViewById(R.id.tad_drawer); //displays App???s Navigation option from the left edge of the screen
        navigationView = findViewById(R.id.tad_nav_view);
        updateFavList();
        navigationView.setNavigationItemSelectedListener(this);

        // This is a hamburger icon in toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, tBar, R.string.tad_nav_open, R.string.tad_nav_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        searchListFragment = new SearchListFragment();
        tracksFragment = new TracksFragment();
        favoriteTracksFragment = new FavoriteTracksFragment();
        sharedPreferences = getSharedPreferences("audioDb", MODE_PRIVATE);

        // Allows to fragments locate horizontally when screen is turned. For this  was created
        // layout_land with activity_the_audio_database.xml file
        if (orientationLand || isTablet) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_location, searchListFragment) //Add the fragment in FrameLayout
                    .commit(); //actually load the fragment.
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_location2, tracksFragment) //Add the fragment in FrameLayout
                    .commit(); //actually load the fragment.
        } else {
            changeFragment(searchListFragment);
        }
    }

    /**
     * update Favorite tracks in Navigation menu
     */
    void updateFavList() {
        favoriteTracks = dbHelper.getTracklist();
        navigationView.getMenu().clear();
        for (Track t : favoriteTracks){
            navigationView.getMenu().add(t.toString());
        }
    }

    /**
     * replace fragment with param
     * @param fragment
     */
    void changeFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.fragment_location, fragment) //Add the fragment in FrameLayout
                .commit(); //actually load the fragment.
    }

    /**
     * replace fragment and search specific album (used to replace details fragment
     * @param fragment
     * @param idAlbum
     * @param urlImage
     * @param artistName
     */
    void changeFragment(Fragment fragment, String idAlbum, String urlImage, String artistName) {
        changeFragment(fragment);
        tracksFragment.searchAlbum(idAlbum, urlImage, artistName);
    }

    /**
     * search album in details album fragment
     * @param idAlbum
     * @param urlImage
     * @param artistName
     */
    public void searchAlbum(String idAlbum, String urlImage, String artistName) {
        if (tracksFragment != null) {
            tracksFragment.searchAlbum(idAlbum, urlImage, artistName);
        }
    }

    /**
     * override onBackPressed to control fragments change
     */
    @Override
    public void onBackPressed() {
        if (orientationLand || isTablet) {
            super.onBackPressed();
        } else if (tracksFragment.isVisible() || favoriteTracksFragment.isVisible()){
            changeFragment(searchListFragment);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * @return network state
     */
    boolean isNetworkOk() {
        return ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo() != null &&
                ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                        .getActiveNetworkInfo().isConnected();
    }

    @Override
    /**
     Overriden onCreateOptionsMenu for adding a menu to Activity
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.help_tad);//this adds the menu item dynamically
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    /**
     * This hook is called whenever an item in your options menu is selected.
     */
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // what to do when menu item is selected
        if (item.getItemId() == 0) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Instruction")
                    .setMessage(R.string.Instruction)
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Save string in SharedPreferences
      */
    void saveSharedPrefs(String savedString) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ARTIST_NAME, savedString);
            editor.commit();
        }
    }

    /**
    * checking orientation an tablet mode
     */
    private void checkOrientation(){
        isTablet = findViewById(R.id.fragment_location2) != null;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == 2){
            orientationLand = true;
        } else orientationLand = false;
    }

    /**
     * on Click Favorites in Navigation menu
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            String artistAndTrack = item.getTitle().toString().replace(" ", "%20");
            String url = "http://www.google.com/search?q=" + artistAndTrack ;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        return false;
    }

    /**
     * Go to organize Favorites Fragment
     * @param view
     */
	public void organizeFavorites(View view) {
        if(orientationLand || isTablet){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_location, favoriteTracksFragment) //Add the fragment in FrameLayout
                    .addToBackStack("")
                    .commit(); //actually load the fragment.
            drawer.closeDrawers();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .addToBackStack("")
                    .replace(R.id.fragment_location, favoriteTracksFragment) //Add the fragment in FrameLayout
                    .commit(); //actually load the fragment.
            drawer.closeDrawers();
        }
	}
}