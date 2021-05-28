package com.lacker.staff.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lacker.staff.features.auth.AuthMachine
import com.lacker.staff.features.menu.MenuMachine
import com.lacker.staff.features.order.OrderDetailsMachine
import com.lacker.staff.features.orders.OrdersListMachine
import com.lacker.staff.features.tasks.TasksMachine
import com.lacker.staff.features.profile.ProfileMachine
import com.lacker.staff.features.suborder.SuborderMachine
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
    @ViewModelKey(TasksMachine::class)
    internal abstract fun bindTasksMachine(viewModel: TasksMachine): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SuborderMachine::class)
    internal abstract fun bindSuborderMachine(viewModel: SuborderMachine): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MenuMachine::class)
    internal abstract fun bindMenuMachine(viewModel: MenuMachine): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrdersListMachine::class)
    internal abstract fun bindOrdersListMachine(viewModel: OrdersListMachine): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrderDetailsMachine::class)
    internal abstract fun bindOrderDetailsMachine(viewModel: OrderDetailsMachine): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileMachine::class)
    internal abstract fun bindProfileMachine(viewModel: ProfileMachine): ViewModel
}