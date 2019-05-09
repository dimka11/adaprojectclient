package com.ds.da.accelerationdata;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class AppConfig {

    private static String BASE_URL = "http://192.168.1.3:8080/";

    static Retrofit getRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .build();
        BASE_URL = (String) GetSharedPreferences.invoke("serverUploadURL"); //rewrite base_url from settings

        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
