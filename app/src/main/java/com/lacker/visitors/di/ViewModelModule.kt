package com.lacker.visitors.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lacker.visitors.features.history.HistoryMachine
import com.lacker.visitors.features.news.NewsMachine
import com.lacker.visitors.features.profile.ProfileMachine
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import com.lacker.visitors.features.session.menu.MenuMachine
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
    @ViewModelKey(ScanMachine::class)
    internal abstract fun bindScanMachine(viewModel: ScanMachine): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MenuMachine::class)
    internal abstract fun bindMenuMachine(viewModel: MenuMachine): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileMachine::class)
    internal abstract fun bindProfileMachine(viewModel: ProfileMachine): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HistoryMachine::class)
    internal abstract fun bindHistoryMachine(viewModel: HistoryMachine): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewsMachine::class)
    internal abstract fun bindNewsMachine(viewModel: NewsMachine): ViewModel
}