package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TheAudioDatabase extends AppCompatActivity {
    ListView listView;
    EditText etSearch;
    Button btnSearch;
    private ProgressBar pBar;
    private ArtistSearch searchCall;
    private Retrofit retrofit;
    private List<Album> albums;
    private ArrayAdapter<String> arrayAdapter;
    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static final String ALBUM_NUM = "ALBUM_NUM";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_audio_database);
        listView = findViewById(R.id.lv_albums_tad);
        etSearch = findViewById(R.id.edt_search_tad);
        btnSearch = findViewById(R.id.btn_search_tad);
        pBar = findViewById(R.id.pb_tad);
        albums = new ArrayList<>(); // Albums list

        // Adapter for ListView
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                new ArrayList<>());

        listView.setAdapter(arrayAdapter);
        // ClickListener for each item in List
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, AlbumDetailsInfo.class);
            intent.putExtra(ALBUM_NUM, position);
            intent.putExtra(ARTIST_NAME, etSearch.getText().toString());
            startActivity(intent);
        });

        // initialize retrofit, setup for server tad
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.theaudiodb.com/") // set servername
                .addConverterFactory(GsonConverterFactory.create()) // set JSON converter
                .build();

        // creating object of interface ArtistSearch
        searchCall = retrofit.create(ArtistSearch.class);

        btnSearch.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etSearch.getText().toString())) {
                Toast.makeText(this, "Input artist name", Toast.LENGTH_SHORT).show();
            }
            else if (((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
            .getActiveNetworkInfo() != null &&
                    ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                            .getActiveNetworkInfo().isConnected()){
                arrayAdapter.clear();
                arrayAdapter.notifyDataSetChanged();
                pBar.setVisibility(View.VISIBLE);
                // Запрос к серверу
                // creating Call to server
                Call<Albums> call = searchCall.getData(String.valueOf(etSearch.getText()));
                call.enqueue(new Callback<Albums>() { // выполнить асинхронно
                    @Override
                    public void onResponse(Call<Albums> call, Response<Albums> response) {
                        albums = response.body().getAlbums(); // получение ответа и преобразование его в список альбомов
                        if (albums == null){
                            AlertDialog.Builder alert = new AlertDialog.Builder(TheAudioDatabase.this);
                            alert.setTitle("Error")
                                    .setMessage("Can't find artist: " + etSearch.getText().toString())
                                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                            alert.show();
                        }else {
                            ArrayList<String> albumNames = new ArrayList<>();// List of Albums names
                            for (Album album : albums)
                                albumNames.add(album.getStrAlbum()); // получение из списка альбомов имен альбомов

                            arrayAdapter.addAll(albumNames);
                            arrayAdapter.notifyDataSetChanged();
                        }
                        pBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<Albums> call, Throwable t) {
                        pBar.setVisibility(View.GONE);

                    }
                });
            } else {
                Snackbar.make(listView, "No Internet Connection", BaseTransientBottomBar.LENGTH_SHORT).show();
            }

        });


    }
}