package com.example.cst2335_graphicalinterfaceprogramming;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ArtistSearch {
    @GET("/api/v1/json/1/searchalbum.php")
    Call<Albums> getData(@Query("s") String artistName);

}

