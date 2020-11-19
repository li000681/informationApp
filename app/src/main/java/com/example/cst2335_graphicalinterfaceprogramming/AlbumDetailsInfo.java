package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlbumDetailsInfo extends AppCompatActivity {

    private ArtistSearch searchCall;
    private Retrofit retrofit;
    private List<Album> albums;

    TextView albumName;
    TextView artistName;
    TextView year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details_info);
        albumName = findViewById(R.id.tv_album_name_aad);
        artistName = findViewById(R.id.tv_artist_name_aad);
        year = findViewById(R.id.tv_jear_aad);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.theaudiodb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // creating object of interface ArtistSearch
        searchCall = retrofit.create(ArtistSearch.class); // using interface for calls

        String artistNameFromIntent = getIntent().getStringExtra(TheAudioDatabase.ARTIST_NAME);

        // creating Call to server
        Call<Albums> call = searchCall.getData(artistNameFromIntent);
        call.enqueue(new Callback<Albums>() { // выполнить асинхронно
            @Override
            public void onResponse(Call<Albums> call,
                                   // Responce from server
                                   Response<Albums> response) {
                albums = response.body().getAlbums(); // получение ответа и преобразование его в список альбомов
                Album album = albums.get(getIntent().getIntExtra(TheAudioDatabase.ALBUM_NUM,0));
                artistName.setText(album.getStrArtist());
                albumName.setText(album.getStrAlbum());
                year.setText(album.getIntYearReleased());
            }

            @Override
            public void onFailure(Call<Albums> call, Throwable t) {
                System.out.println(t);
            }
        });

    }
}