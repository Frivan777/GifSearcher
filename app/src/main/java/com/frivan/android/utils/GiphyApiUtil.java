package com.frivan.android.utils;


import com.frivan.android.interfaces.GiphyApi;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GiphyApiUtil {
    private static final String BASE_URL = "http://api.giphy.com/";

    public static GiphyApi getApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GiphyApi giphyApi = retrofit.create(GiphyApi.class);

        return giphyApi;
    }

}
