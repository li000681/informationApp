package com.example.cst2335_graphicalinterfaceprogramming;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TracksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TracksFragment extends Fragment {

    private TheAudioDatabase parentActivity;
    private ListView listView;
    ArrayAdapter arrayAdapter;
    static TracksFragment fragment;
    private ImageView imageView;

    // TODO: Rename and change types and number of parameters
    public static TracksFragment newInstance() {
        if (fragment == null) fragment = new TracksFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_tracks, container, false);
        listView = result.findViewById(R.id.list_view_tracks);
        imageView = result.findViewById(R.id.iv_album_img_ft);

        arrayAdapter = new ArrayAdapter<String>(
                parentActivity,
                android.R.layout.simple_list_item_1,
                new ArrayList<>());
        listView.setAdapter(arrayAdapter);

        Call<AnswerTracks> callTracks = parentActivity.searchCall.getTracks(getArguments().getString(parentActivity.ID_ALBUM));
        callTracks.enqueue(new Callback<AnswerTracks>() {
            @Override
            public void onResponse(Call<AnswerTracks> call, Response<AnswerTracks> response) {
                List<Track> tracks = response.body().getTrack();
                List<String> trackNames = new ArrayList<>();
                for (Track t : tracks) trackNames.add(t.getStrTrack());
                arrayAdapter.addAll(trackNames);
                arrayAdapter.notifyDataSetChanged();
                ArtistSearch artistSearch = parentActivity.retrofit.create(ArtistSearch.class);
                Call<Albums> callAlbums = artistSearch.getData(tracks.get(0).getStrArtist());
                callAlbums.enqueue(new Callback<Albums>() {
                    @Override
                    public void onResponse(Call<Albums> call, Response<Albums> response) {
                        DownloadImageTask task = new DownloadImageTask(imageView);
                        List<Album> albums = response.body().getAlbums();
                        String url = null;
                        for (Album album : albums) {
                            if (getArguments().getString(parentActivity.ID_ALBUM).equals(album.getIdAlbum())){
                                url = (String) album.getStrAlbumThumb();
                                break;
                            }
                        }
                        if (!TextUtils.isEmpty(url)) {
                            task.execute(url);
                        }

                    }

                    @Override
                    public void onFailure(Call<Albums> call, Throwable t) {
                        System.out.println(t);
                    }
                });


            }

            @Override
            public void onFailure(Call<AnswerTracks> call, Throwable t) {

            }
        });


        return result;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        parentActivity = (TheAudioDatabase) context;
        super.onAttach(context);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;

        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);

        }
    }

}