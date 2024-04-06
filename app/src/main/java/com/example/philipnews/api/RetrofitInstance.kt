package com.example.philipnews.api

import com.example.philipnews.utils.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private val logging by lazy {
        HttpLoggingInterceptor()
            .apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
    }

    private val client =OkHttpClient.Builder()
        .apply {
            addInterceptor(MyInterceptor())
            addInterceptor(logging)
            connectTimeout(10,TimeUnit.SECONDS)
        }.build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val api:NewsApi by lazy {
        retrofit.create(NewsApi::class.java)
    }

}