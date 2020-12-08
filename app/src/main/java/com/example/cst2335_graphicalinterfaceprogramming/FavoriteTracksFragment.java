package com.example.cst2335_graphicalinterfaceprogramming;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.cst2335_graphicalinterfaceprogramming.TheAudioDatabase.ALBUM_ID;
import static com.example.cst2335_graphicalinterfaceprogramming.TheAudioDatabase.ARTIST_NAME;
import static com.example.cst2335_graphicalinterfaceprogramming.TheAudioDatabase.URL_IMAGE;

/**
 * The class is the main responsible the main page function
 *  @author Gulmira Kanalinova
 *  @version 1.0
 *  December 5, 2020
 */
public class FavoriteTracksFragment extends Fragment {

	private TheAudioDatabase parentActivity;
	private ListView listView;
	ProgressBar pBar;
	private ArrayAdapter arrayAdapter;

	private List<Track> tracks;


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracks = new ArrayList<>();
		if (parentActivity != null && parentActivity.dbHelper != null) {
			tracks = parentActivity.dbHelper.getTracklist();
		}
	}

	/**
	 *  ON CRATE VIEW *
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View result = inflater.inflate(R.layout.fragment_favorite_tracks, container, false);

		listView = result.findViewById(R.id.list_view_favorite_tracks);

		arrayAdapter = new ArrayAdapter<String>(
				parentActivity,
				android.R.layout.simple_list_item_1,
				new ArrayList<>());

		listView.setAdapter(arrayAdapter);
		arrayAdapter.addAll(tracks);
		arrayAdapter.notifyDataSetChanged();

		listView.setOnItemClickListener((adapterView, view, i, l) -> {
			PopupMenu popupMenu = new PopupMenu(parentActivity, view);
			popupMenu.getMenu().add("Remove?");
			popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem menuItem) {
					parentActivity.dbHelper.delete(
							tracks.get(i).artistName,
							tracks.get(i).trackName
							);
					tracks = parentActivity.dbHelper.getTracklist();
					arrayAdapter.clear();
					arrayAdapter.addAll(tracks);

					parentActivity.updateFavList();
					return false;
				}
			});
			popupMenu.show();
		});


		return result;
	}



	// We need this to have reference to Activity *
	@Override // On Attach to activity
	public void onAttach(@NonNull Context context) {
		parentActivity = (TheAudioDatabase) context;
		super.onAttach(context);
	}

    @Override
    public void onDetach() { // on Detach from Activity
	    // To avoid memory leaks
	    parentActivity = null;
        super.onDetach();
    }
}