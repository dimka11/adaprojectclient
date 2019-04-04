package com.ds.da.wakelockfgservicetest

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


object NetworkService {
    private lateinit var mInstance: NetworkService
    private val BASE_URL = "https://jsonplaceholder.typicode.com"
    private lateinit var mRetrofit: Retrofit

    private fun constr(): NetworkService {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)

        mRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()
        return this
    }

    fun getInstance(): NetworkService {
        if (!::mInstance.isInitialized) {
            mInstance = constr()
        }
        return mInstance
    }

    fun getJSONApi(): JSONPlaceHolderApi {
        return mRetrofit.create(JSONPlaceHolderApi::class.java)
    }
}