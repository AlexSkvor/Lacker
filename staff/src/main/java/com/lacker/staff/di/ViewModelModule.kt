package com.lacker.staff.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lacker.staff.features.auth.AuthMachine
import com.lacker.staff.features.orders.OrdersMachine
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass


@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(AuthMachine::class)
    internal abstract fun bindAuthMachine(viewModel: AuthMachine): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrdersMachine::class)
    internal abstract fun bindOrdersMachine(viewModel: OrdersMachine): ViewModel
}