package com.frivan.android.interfaces;

import com.frivan.android.models.Gif;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Для работы с Giphy Api
 */

public interface GiphyApi {

    @GET("/v1/gifs/search")
    Observable<Gif> getSearchGifs(@Query("q") String query, @Query("api_key") String apiKey);

    @GET("/v1/gifs/trending")
    Observable<Gif> getTrendingGifs(@Query("api_key") String apiKey);
}
