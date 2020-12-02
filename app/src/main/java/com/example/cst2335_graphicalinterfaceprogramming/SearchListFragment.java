package com.example.cst2335_graphicalinterfaceprogramming;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.cst2335_graphicalinterfaceprogramming.TheAudioDatabase.ARTIST_NAME;


public class SearchListFragment extends Fragment {

	private ListView listView;
	private EditText etSearch;
	private Button btnSearch;
	private ProgressBar pBar;
	private List<String> albums;
	private ArrayAdapter<String> arrayAdapter;
	private String artistName;

	private TheAudioDatabase parentActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
		// if shared prefs is not null and contains artist name
		if (parentActivity.sharedPreferences != null) {
			if (parentActivity.sharedPreferences.contains(ARTIST_NAME)) {
				// get artist name from SP
				artistName = parentActivity.sharedPreferences.getString(ARTIST_NAME, "");
			}
		}
        super.onCreate(savedInstanceState);
    }

	@Override
	public void onResume() {
    	if (!TextUtils.isEmpty(etSearch.getText())){
    		btnSearch.callOnClick();
		}
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View result = inflater.inflate(R.layout.search_fragment, container, false);

		listView = result.findViewById(R.id.lv_albums_tad);
		etSearch = result.findViewById(R.id.edt_search_tad);
		btnSearch = result.findViewById(R.id.btn_search_tad);
		pBar = result.findViewById(R.id.pb_tracks_frag);
		albums = new ArrayList<>(); // Albums list
		// remove focus when open fragment
		parentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Adapter for ListView
		arrayAdapter = new ArrayAdapter<String>(
				parentActivity,
				android.R.layout.simple_list_item_1,
				new ArrayList<>());

		listView.setAdapter(arrayAdapter);

		// ClickListener for each item in List
		listView.setOnItemClickListener((parent, view, position, id) -> {
			try {
				JSONObject jsonObject = new JSONObject(albums.get(position));
				String idAlbum = jsonObject.getString("idAlbum");
				String urlImage = jsonObject.getString("strAlbumThumb");
				String artistName = jsonObject.getString("strArtist");

				if (parentActivity.orientationLand) {
					parentActivity.searchAlbum(idAlbum, urlImage, artistName);
				} else {
					parentActivity.changeFragment(parentActivity.tracksFragment, idAlbum, urlImage, artistName);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		});


		btnSearch.setOnClickListener(v -> {
			// Hide keyboard when Search pressing
			InputMethodManager imm = (InputMethodManager) parentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			if (TextUtils.isEmpty(etSearch.getText().toString())) {
				Toast.makeText(parentActivity, R.string.empty_field_toast, Toast.LENGTH_SHORT).show();
			} else if (parentActivity.isNetworkOk()) {
				arrayAdapter.clear();
				arrayAdapter.notifyDataSetChanged();
				pBar.setVisibility(View.VISIBLE);

				GetAlbumsAsync getAlbumsAsync = new GetAlbumsAsync();
				getAlbumsAsync.execute(etSearch.getText().toString().trim());

			} else {
				Snackbar.make(listView, R.string.no_network, BaseTransientBottomBar.LENGTH_SHORT).show();
			}

		});


		if (artistName != null) {
            etSearch.setText(artistName);
        }
		return result;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		parentActivity = (TheAudioDatabase) context;
		super.onAttach(context);
	}

	@Override
	public void onDetach() {
    	parentActivity = null;
		super.onDetach();
	}




	/**
	 * AsyncTask
	 */


	private class GetAlbumsAsync extends AsyncTask<String, Integer, String> {
		protected String doInBackground(String... params) {
			String result = "";
			try {
				String artistName = params[0].replace(" ", "%20");
				String urlSite = "https://www.theaudiodb.com/api/v1/json/1/searchalbum.php?s=";
				URL url = new URL(urlSite + artistName);
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
					JSONArray dataArrayAlbums = jObject.getJSONArray("album");
					List<String> albumNames = new ArrayList<>();
					albums.clear();
					if (dataArrayAlbums != null) {
						for (int i = 0; i < dataArrayAlbums.length(); i++) {
							albums.add(dataArrayAlbums.getString(i));
							JSONObject jsonObjectAlbum = dataArrayAlbums.getJSONObject(i);
							albumNames.add(jsonObjectAlbum.getString("strAlbum"));
						}
					}

					((ArrayAdapter) listView.getAdapter()).clear();
					((ArrayAdapter) listView.getAdapter()).addAll(albumNames);
					pBar.setVisibility(View.GONE);
					((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
                    parentActivity.saveSharedPrefs(etSearch.getText().toString());

				} else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(parentActivity);
                            alert.setTitle(R.string.error)
                                    .setMessage(getString(R.string.cant_find_artist) + ": " + etSearch.getText().toString())
                                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                            alert.show();
                }
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
		}
	}
}
