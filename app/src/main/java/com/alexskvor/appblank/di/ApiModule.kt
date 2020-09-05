package com.alexskvor.appblank.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.alexskvor.appblank.BuildConfig
import com.alexskvor.appblank.data.api.Api
import com.alexskvor.appblank.data.api.ApiLogger
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class ApiModule {

    private companion object {
        const val CONNECT_TIMEOUT_SECONDS = 60
        const val READ_TIMEOUT_SECONDS = 60
        const val WRITE_TIMEOUT_SECONDS = 60
    }

    @Provides
    @Singleton
    fun provideApi(context: Context, json: Moshi): Api {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .addConverterFactory(MoshiConverterFactory.create(json))
            .client(getClient(context, json))
            .build()
            .create(Api::class.java)
    }

    private fun getClient(context: Context, json: Moshi): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(ApiLogger.get(json))
            .addInterceptor(ChuckerInterceptor(context))
            .retryOnConnectionFailure(true)
            .connectTimeout(CONNECT_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .build()
    }

}