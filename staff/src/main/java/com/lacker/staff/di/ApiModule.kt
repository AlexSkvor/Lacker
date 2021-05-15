package com.lacker.staff.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.lacker.staff.data.api.Api
import com.lacker.staff.data.api.FakeApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import com.lacker.staff.BuildConfig
import com.lacker.utils.api.ApiLogger
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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
        return FakeApi()
        //TODO return real api when it is ready
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .addConverterFactory(MoshiConverterFactory.create(json))
            .client(getClient(context))
            .build()
            .create(Api::class.java)
    }

    private fun getClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(ApiLogger.get())
            .addInterceptor(ChuckerInterceptor(context))
            .retryOnConnectionFailure(true)
            .connectTimeout(CONNECT_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .build()
    }

}