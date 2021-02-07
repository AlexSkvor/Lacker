package com.lacker.visitors.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import com.lacker.visitors.features.session.menu.MenuMachine
import com.lacker.visitors.features.scan.ScanMachine
import com.lacker.visitors.features.session.basket.BasketMachine
import com.lacker.visitors.features.session.favourite.FavouriteMachine
import com.lacker.visitors.features.session.order.OrderMachine
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
    @ViewModelKey(BasketMachine::class)
    internal abstract fun bindBasketMachine(viewModel: BasketMachine): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavouriteMachine::class)
    internal abstract fun bindFavouriteMachine(viewModel: FavouriteMachine): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrderMachine::class)
    internal abstract fun bindOrderMachine(viewModel: OrderMachine): ViewModel
}