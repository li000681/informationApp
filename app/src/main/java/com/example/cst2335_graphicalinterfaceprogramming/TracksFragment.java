package com.example.cst2335_graphicalinterfaceprogramming;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    // TODO: Rename and change types and number of parameters
    public static TracksFragment newInstance() {
        if (fragment == null) fragment = new TracksFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.fragment_tracks, container, false);
        listView = result.findViewById(R.id.list_view_tracks);

        arrayAdapter = new ArrayAdapter<String>(
                parentActivity,
                android.R.layout.simple_list_item_1,
                new ArrayList<>());
        listView.setAdapter(arrayAdapter);

        Call<AnswerTracks> call = parentActivity.searchCall.getTracks(getArguments().getString(parentActivity.ID_ALBUM));
        call.enqueue(new Callback<AnswerTracks>() {
            @Override
            public void onResponse(Call<AnswerTracks> call, Response<AnswerTracks> response) {
                List<Track> tracks = response.body().getTrack();
                List<String> trackNames = new ArrayList<>();
                for (Track t: tracks) trackNames.add(t.getStrTrack());
                arrayAdapter.addAll(trackNames);
                arrayAdapter.notifyDataSetChanged();
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
}