package com.lacker.visitors.di

import dagger.Component
import com.lacker.visitors.MainActivity
import com.lacker.visitors.data.storage.user.UserStorage
import com.lacker.visitors.features.auth.bottomdialog.AuthBottomSheetDialogFragment
import com.lacker.visitors.features.profile.ProfileFragment
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NavigationModule::class,
        ViewModelModule::class,
        UtilsModule::class,
        DataModule::class,
        ApiModule::class
    ]
)
interface AppComponent {
    fun inject(appActivity: MainActivity)
    fun inject(authFragment: AuthBottomSheetDialogFragment)
    fun inject(profileFragment: ProfileFragment)

    fun getUserStorage(): UserStorage
}