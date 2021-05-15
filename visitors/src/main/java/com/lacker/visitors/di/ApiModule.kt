package com.lacker.visitors.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.lacker.utils.api.ApiLogger
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.lacker.visitors.BuildConfig
import com.lacker.visitors.data.api.Api
import com.lacker.visitors.data.api.fake.FakeApi
import com.lacker.visitors.data.storage.files.FilesManager
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
    fun provideApi(context: Context, json: Moshi, filesManager: FilesManager): Api {
        return FakeApi(filesManager, json)
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