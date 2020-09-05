package com.alexskvor.appblank.di

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import com.alexskvor.appblank.data.api.jsonadapters.UserRoleJsonAdapter
import com.alexskvor.utils.extensions.DateAdapter
import com.alexskvor.utils.extensions.DateTimeAdapter
import com.alexskvor.utils.resources.ResourceProvider
import com.alexskvor.utils.resources.ResourceProviderImpl
import javax.inject.Singleton

@Module
class UtilsModule {

    @Singleton
    @Provides
    fun bindResourceProvider(context: Context): ResourceProvider = ResourceProviderImpl(context)

    @Singleton
    @Provides
    fun provideJson(): Moshi = Moshi.Builder()
        .add(DateTimeAdapter())
        .add(DateAdapter())
        .add(UserRoleJsonAdapter())
        .build()
}