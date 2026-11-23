package com.example.project_farhangadoca_mobilee.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseClient {
    private const val BASE_URL = "https://pkjfrsevjtquceigpepb.supabase.co/rest/v1/"
    private const val API_KEY = "sb_publishable_Wi4ND2pfJQSx0oVTtbd3xg_aCWxrsH7"

    private val headerInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val method = originalRequest.method

        val requestBuilder = originalRequest.newBuilder()
            .header("apikey", API_KEY)

        if (method.equals("GET", ignoreCase = true)) {
            requestBuilder.header("Accept-Profile", "mobile")
        } else if (method.equals("POST", ignoreCase = true) ||
                   method.equals("PATCH", ignoreCase = true) ||
                   method.equals("DELETE", ignoreCase = true)) {
            requestBuilder.header("Content-Profile", "mobile")
            requestBuilder.header("Content-Type", "application/json")
            requestBuilder.header("Prefer", "return=representation")
        }

        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    val apiService: SupabaseApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApiService::class.java)
    }
}
