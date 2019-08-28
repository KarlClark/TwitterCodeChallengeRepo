package com.twitter.challenge.data;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

// Interface used by Retrofit to define its's api calls.

public interface WeatherDataEndpointApi {

    @GET("{id}.json")
    Call<WeatherData> getWeatherData(@Path("id") String id);
}
