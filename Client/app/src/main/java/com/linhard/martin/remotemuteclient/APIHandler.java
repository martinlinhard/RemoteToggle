package com.linhard.martin.remotemuteclient;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIHandler {

    private API api;


    public APIHandler(String baseURL) {
        this.rebuild(baseURL);
    }

    public void rebuild(String baseURL) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://" + baseURL + ":8080")
                .build();
        this.api = retrofit.create(API.class);
    }

    public void fetchStatus(Callback<StatusResponse> cb) {
        Call<StatusResponse> call = this.api.status();
        call.enqueue(cb);
    }

    public void toggle(Callback<ResponseBody> cb) {
        Call<ResponseBody> call = this.api.toggle();
        call.enqueue(cb);
    }
}
