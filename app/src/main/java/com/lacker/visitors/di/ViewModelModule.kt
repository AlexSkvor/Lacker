package com.lacker.visitors.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import com.lacker.visitors.features.auth.AuthMachine
import com.lacker.visitors.features.auth.main.MainAuthMachine
import com.lacker.visitors.features.scan.ScanMachine
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
    @ViewModelKey(MainAuthMachine::class)
    internal abstract fun bindMainAuthMachine(viewModel: MainAuthMachine): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ScanMachine::class)
    internal abstract fun bindScanMachine(viewModel: ScanMachine): ViewModel
}