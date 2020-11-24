package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TheAudioDatabase extends AppCompatActivity {

    static final String ID_ALBUM = "ID_ALBUM";
    ArtistSearch searchCall;
    private Retrofit retrofit;
    private List<Album> albums;
    private ArrayAdapter<String> arrayAdapter;
    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static final String ALBUM_NUM = "ALBUM_NUM";
    SearchListFragment  searchListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_audio_database);

        // initialize retrofit, setup for server tad
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.theaudiodb.com/") // set servername
                .addConverterFactory(GsonConverterFactory.create()) // set JSON converter
                .build();

        // creating object of interface ArtistSearch
        searchCall = retrofit.create(ArtistSearch.class);
        searchListFragment = SearchListFragment.newInstance();
        changeFragment(searchListFragment, null);
    }

    void changeFragment(Fragment fragment, String idAlbum){
        if (idAlbum != null) {
            Bundle bundle = new Bundle();
            bundle.putString(ID_ALBUM, idAlbum);
            fragment.setArguments(bundle); //pass it a bundle for information
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_location, fragment) //Add the fragment in FrameLayout
                .commit(); //actually load the fragment.
    }

    boolean isNetworkOk(){
        return ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo() != null &&
                ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                        .getActiveNetworkInfo().isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("instruction");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("instruction")) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Instruction")
                    .setMessage("Input name and press \"Search\"")
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
            alert.show();
        }
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (TracksFragment.newInstance().isVisible()){
            changeFragment(SearchListFragment.newInstance(), null);
        } else super.onBackPressed();
    }
}