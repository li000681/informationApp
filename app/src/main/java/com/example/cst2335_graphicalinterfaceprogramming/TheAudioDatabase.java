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


public class TheAudioDatabase extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static final String ALBUM_ID = "ALBUM_NUM";
    public static final String URL_IMAGE = "URL_IMAGE";
    private SearchListFragment searchListFragment;
    private FavoriteTracksFragment favoriteTracksFragment;
    TracksFragment tracksFragment;
    SharedPreferences sharedPreferences;
    boolean orientationLand;
    TheAudioDbHelper dbHelper;
    NavigationView navigationView;
    DrawerLayout drawer;
    List<Track> favoriteTracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tad_main);
        Toolbar tBar = findViewById(R.id.tad_toolbar);
        setSupportActionBar(tBar);
        checkOrientation();
        dbHelper = new TheAudioDbHelper(this);

        drawer = findViewById(R.id.tad_drawer);
        navigationView = findViewById(R.id.tad_nav_view);
        updateFavList();
        navigationView.setNavigationItemSelectedListener(this);

        // Hamburger icon in toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, tBar, R.string.tad_nav_open, R.string.tad_nav_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        searchListFragment = new SearchListFragment();
        tracksFragment = new TracksFragment();
        favoriteTracksFragment = new FavoriteTracksFragment();
        sharedPreferences = getSharedPreferences("audioDb", MODE_PRIVATE);

        if (orientationLand) {
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

    // update Favorite tracks in Navigation list
    void updateFavList() {
        favoriteTracks = dbHelper.getTracklist();
        navigationView.getMenu().clear();
        for (Track t : favoriteTracks){
            navigationView.getMenu().add(t.toString());
        }
    }

    void changeFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.fragment_location, fragment) //Add the fragment in FrameLayout
                .commit(); //actually load the fragment.
    }
    void changeFragment(Fragment fragment, String idAlbum, String urlImage, String artistName) {
        changeFragment(fragment);
        tracksFragment.searchAlbum(idAlbum, urlImage, artistName);
    }
    public void searchAlbum(String idAlbum, String urlImage, String artistName) {
        if (tracksFragment != null) {
            tracksFragment.searchAlbum(idAlbum, urlImage, artistName);
        }
    }

    @Override
    public void onBackPressed() {
        if (orientationLand) {
            super.onBackPressed();
        } else if (tracksFragment.isVisible()){
            changeFragment(searchListFragment);
        } else {
            super.onBackPressed();
        }
    }

    boolean isNetworkOk() {
        return ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo() != null &&
                ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                        .getActiveNetworkInfo().isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.help_tad);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 0) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Instruction")
                    .setMessage(R.string.Instruction)
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    // Save string in SP
    void saveSharedPrefs(String savedString) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ARTIST_NAME, savedString);
            editor.commit();
        }
    }

    private void checkOrientation(){
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == 2){
            orientationLand = true;
        } else orientationLand = false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            String artistAndTrack = item.getTitle().toString().replace(" ", "%20");
            String url = "http://www.google.com/search?q=" + artistAndTrack ;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        return false;
    }

	public void organizeFavorites(View view) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .addToBackStack("")
                .replace(R.id.fragment_location, favoriteTracksFragment) //Add the fragment in FrameLayout
                .commit(); //actually load the fragment.
        drawer.closeDrawers();
	}
}