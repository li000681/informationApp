package com.example.cst2335_graphicalinterfaceprogramming;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchListFragment extends Fragment {

    ListView listView;
    EditText etSearch;
    Button btnSearch;
    private ProgressBar pBar;
    private List<Album> albums;
    private ArrayAdapter<String> arrayAdapter;
    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static final String ALBUM_NUM = "ALBUM_NUM";
    private TheAudioDatabase parentActivity;
    static SearchListFragment fragment;

    public static SearchListFragment newInstance() {
        if(fragment == null) fragment = new SearchListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.search_fragment, container, false);

        listView = result.findViewById(R.id.lv_albums_tad);
        etSearch = result.findViewById(R.id.edt_search_tad);
        btnSearch = result.findViewById(R.id.btn_search_tad);
        pBar = result.findViewById(R.id.pb_tad);
        albums = new ArrayList<>(); // Albums list

        // Adapter for ListView
        arrayAdapter = new ArrayAdapter<String>(
                parentActivity,
                android.R.layout.simple_list_item_1,
                new ArrayList<>());

        listView.setAdapter(arrayAdapter);
        // ClickListener for each item in List
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // TODO:change frag
            String idAlbum = albums.get(position).getIdAlbum();
            parentActivity.changeFragment(TracksFragment.newInstance(), idAlbum);
        });

        // initialize retrofit, setup for server tad



        btnSearch.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) parentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            if (TextUtils.isEmpty(etSearch.getText().toString())) {
                Toast.makeText(parentActivity, R.string.empty_field_toast, Toast.LENGTH_SHORT).show();
            }
            else if (parentActivity.isNetworkOk()){
                arrayAdapter.clear();
                arrayAdapter.notifyDataSetChanged();
                pBar.setVisibility(View.VISIBLE);
                // Запрос к серверу
                // creating Call to server
                Call<Albums> call = parentActivity.searchCall.getData(String.valueOf(etSearch.getText()));
                call.enqueue(new Callback<Albums>() { // выполнить асинхронно
                    @Override
                    public void onResponse(Call<Albums> call, Response<Albums> response) {
                        albums = response.body().getAlbums(); // получение ответа и преобразование его в список альбомов
                        if (albums == null){
                            AlertDialog.Builder alert = new AlertDialog.Builder(parentActivity);
                            alert.setTitle(R.string.error)
                                    .setMessage(getString(R.string.cant_find_artist) + ": " + etSearch.getText().toString())
                                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                            alert.show();
                        }else {
                            ArrayList<String> albumNames = new ArrayList<>();// List of Albums names
                            for (Album album : albums)
                                albumNames.add(album.getStrAlbum()); // получение из списка альбомов имен альбомов

                            arrayAdapter.addAll(albumNames);
                            arrayAdapter.notifyDataSetChanged();

                            // save artist name to preferences
                            parentActivity.saveSharedPrefs(etSearch.getText().toString());
                        }
                        pBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<Albums> call, Throwable t) {
                        pBar.setVisibility(View.GONE);

                    }
                });
            } else {
                Snackbar.make(listView, R.string.no_network, BaseTransientBottomBar.LENGTH_SHORT).show();
            }

        });

        // if shared prefs is not null and contains artist name
        if (parentActivity.sharedPreferences != null){
            if (parentActivity.sharedPreferences.contains("SearchName")){
                // get artist name from SP
                etSearch.setText(parentActivity.sharedPreferences.getString("SearchName", ""));
                btnSearch.callOnClick();
            }
        }
        return result;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        parentActivity = (TheAudioDatabase) context;
        super.onAttach(context);
    }
}