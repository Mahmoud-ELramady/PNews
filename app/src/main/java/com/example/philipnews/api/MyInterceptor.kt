package com.example.philipnews.api

import com.example.philipnews.utils.Constants.API_KEY
import okhttp3.Interceptor
import okhttp3.Response

class MyInterceptor:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url=chain.request()
            .url
            .newBuilder()
            .addQueryParameter("apiKey",API_KEY)
            .build()

      val  request=chain.request()
            .newBuilder()
            .url(url)
            .build()


        return chain.proceed(request)
    }
}