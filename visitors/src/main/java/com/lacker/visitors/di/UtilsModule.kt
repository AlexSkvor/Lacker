package com.lacker.visitors.di

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import com.lacker.utils.extensions.DateAdapter
import com.lacker.utils.extensions.DateTimeAdapter
import com.lacker.utils.resources.ResourceProvider
import com.lacker.utils.resources.ResourceProviderImpl
import com.lacker.visitors.data.api.jsonadapters.DishTagJsonAdapter
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
        .add(DishTagJsonAdapter())
        .build()
}