package com.example.cst2335_graphicalinterfaceprogramming;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

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
public class TracksFragment extends Fragment {

	private TheAudioDatabase parentActivity;
	private ListView listView;
	TextView songs;
	ProgressBar pBar;
	private ArrayAdapter arrayAdapter;
	private ImageView imageView;
	private String albumID;
	private String urlImage;
	private String artistName;

	private List<String> tracks;


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null){
            if (bundle.containsKey(ALBUM_ID)) {
                albumID = bundle.getString(ALBUM_ID);
            }
            if (bundle.containsKey(URL_IMAGE)) {
                urlImage = bundle.getString(URL_IMAGE);
            }
			if (bundle.containsKey(ARTIST_NAME)) {
				artistName = bundle.getString(ARTIST_NAME);
			}
		}
	}

	/**
	 *  ON CRATE VIEW *
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View result = inflater.inflate(R.layout.fragment_album_detail, container, false);

		songs = result.findViewById(R.id.tv_songs_tracks_frag);
		listView = result.findViewById(R.id.list_view_tracks);
		imageView = result.findViewById(R.id.iv_album_img_ft);
		pBar = result.findViewById(R.id.pb_tracks_frag);
		arrayAdapter = new ArrayAdapter<String>(
				parentActivity,
				android.R.layout.simple_list_item_1,
				new ArrayList<>());

		listView.setAdapter(arrayAdapter);

		listView.setOnItemClickListener((adapterView, view, i, l) -> {
			try {
				JSONObject jsonObject = new JSONObject(tracks.get(i));
				String trackName = jsonObject.getString("strTrack");
				String url = "http://www.google.com/search?q=" + artistName+"+"+trackName;
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivity(intent);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		});

		listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
			PopupMenu popupMenu = new PopupMenu(parentActivity, view, Gravity.CENTER_VERTICAL);
			popupMenu.getMenu().add(R.string.add_to_fav);
			popupMenu.setOnMenuItemClickListener(menuItem -> {
				try {
					JSONObject jsonObject = new JSONObject(tracks.get(i));
					String trackName = jsonObject.getString("strTrack");
					parentActivity.dbHelper.insertData(
							artistName,
							trackName
					);
					parentActivity.updateFavList();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return true;
			});
			popupMenu.show();
			return true;
		});

		if (albumID != null) {
			GetTracksAsync asynkTask = new GetTracksAsync();
			asynkTask.execute(albumID);
			pBar.setVisibility(View.VISIBLE);
		}

		if (urlImage != null) {
			GetImageAsync asynkTask = new GetImageAsync();
			asynkTask.execute(urlImage);
		}

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


	public void searchAlbum(String idAlbum, String urlImage, String artistName) {
		this.albumID = idAlbum;
		this.artistName = artistName;
		this.urlImage = urlImage;

		GetImageAsync getImageAsync = new GetImageAsync();
		getImageAsync.execute(this.urlImage);

		GetTracksAsync getTracksAsync = new GetTracksAsync();
		getTracksAsync.execute(albumID);
	}


	/**
     *  ASYNC TASKS for info and image downloading
     *
     */

	private class GetImageAsync extends AsyncTask<String, Void, Bitmap> {
		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap albumIcon = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				albumIcon = BitmapFactory.decodeStream(in);

			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}

			return albumIcon;
		}

		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(result);
		}
	}


	private class GetTracksAsync extends AsyncTask<String, Integer, String> {
		protected String doInBackground(String... params) {
			String result = "";
			try {
				String albumID = params[0];
				String urlSite = "https://theaudiodb.com/api/v1/json/1/track.php?m=";
				//create a URL object of what server to contact:
				URL url = new URL(urlSite + albumID);
				//open the connection
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				conn.connect();
				//wait for data:
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(
							conn.getInputStream(), "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					result = sb.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}


		@Override
		public void onPostExecute(String s) {
			try {
				if (s != "") {
					JSONObject jObject = new JSONObject(s);
					JSONArray dataArrayTracks = jObject.getJSONArray("track");
					List<String> trackNames = new ArrayList<>();
					tracks = new ArrayList<>();

					if (dataArrayTracks != null) {
						for (int i = 0; i < dataArrayTracks.length(); i++) {
							tracks.add(dataArrayTracks.getString(i));
							JSONObject jsonObjectAlbum = dataArrayTracks.getJSONObject(i);
							trackNames.add(jsonObjectAlbum.getString("strTrack"));
						}
					}

					((ArrayAdapter) listView.getAdapter()).clear();
					((ArrayAdapter) listView.getAdapter()).addAll(trackNames);
					((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();

				}
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
			pBar.setVisibility(View.GONE);
			songs.setVisibility(View.VISIBLE);
		}
	}
}