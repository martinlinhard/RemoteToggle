package com.linhard.martin.remotemuteclient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface API {

    @POST("/toggle")
    Call<ResponseBody> toggle();

    @GET("/status")
    Call<StatusResponse> status();

}