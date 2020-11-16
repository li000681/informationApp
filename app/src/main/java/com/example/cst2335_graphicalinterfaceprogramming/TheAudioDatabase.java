package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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
    private static ArtistSearch searchCall;
    private Retrofit retrofit;
    List<Album> albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_audio_database);
        listView = findViewById(R.id.lv_albums_tad);
        etSearch = findViewById(R.id.edt_search_tad);
        btnSearch = findViewById(R.id.btn_search_tad);
        albums = new ArrayList<>();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.theaudiodb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        searchCall = retrofit.create(ArtistSearch.class);

        btnSearch.setOnClickListener(v -> {
            Call<List<Album>> call = searchCall.getData(String.valueOf(etSearch.getText()));
            call.enqueue(new Callback<List<Album>>() {
                @Override
                public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                    albums = response.body();
                }

                @Override
                public void onFailure(Call<List<Album>> call, Throwable t) {

                }
            });
        });


    }
}