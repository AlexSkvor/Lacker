package com.lacker.staff.features.auth

import com.lacker.staff.R
import com.lacker.staff.data.api.ApiCallResult
import com.lacker.staff.data.api.NetworkManager
import com.lacker.staff.data.dto.auth.AuthRequest
import com.lacker.staff.data.dto.auth.toDomain
import com.lacker.staff.data.dto.restaurant.RestaurantDto
import com.lacker.staff.data.dto.restaurant.RestaurantsInfoRequest
import com.lacker.staff.data.storage.restaurants.SignedBeforeRestaurantsStorage
import com.lacker.staff.data.storage.user.UserStorage
import javax.inject.Inject
import com.lacker.staff.features.auth.SignInMachine.Wish
import com.lacker.staff.features.auth.SignInMachine.State
import com.lacker.staff.features.auth.SignInMachine.Result
import com.lacker.staff.navigation.Screens
import com.lacker.utils.extensions.isValidEmail
import com.lacker.utils.extensions.onNull
import com.lacker.utils.resources.ResourceProvider
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class SignInMachine @Inject constructor(
    private val oldStorage: SignedBeforeRestaurantsStorage,
    private val net: NetworkManager,
    private val router: Router,
    private val resourceProvider: ResourceProvider,
    private val userStorage: UserStorage
) : Machine<Wish, Result, State>() {

    sealed class Wish { // TODO autoselect last restaurant!
        object SignIn : Wish()
        object Start : Wish()

        data class Restaurant(val restaurant: RestaurantDto?) : Wish()
        data class Email(val text: String) : Wish()
        data class Password(val text: String) : Wish()
    }

    sealed class Result {
        sealed class SignIn : Result() {
            object Success : Result.SignIn()
            data class Error(val text: String) : Result.SignIn()
        }

        sealed class RestaurantsInfo : Result() {
            data class Success(val info: List<RestaurantDto>) : Result.RestaurantsInfo()
            data class Error(val text: String) : Result.RestaurantsInfo()
        }
    }

    data class State(
        val loading: Boolean = false,
        val email: String = "",
        val password: String = "",
        val restaurants: List<RestaurantDto>? = null,
        val selectedRestaurant: RestaurantDto? = null,
        val errorTextEmail: String? = null,
        val errorTextPassword: String? = null,
        val errorTextRestaurant: String? = null
    )

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        is Wish.Email -> oldState.copy(
            email = wish.text, errorTextEmail = null
        )
        is Wish.Password -> oldState.copy(
            password = wish.text, errorTextPassword = null
        )
        Wish.SignIn -> checkStateAndSignInIfPossible(oldState)
        Wish.Start -> oldState.also {
            pushResult { loadRestaurantInfo(*oldStorage.restaurantCodes.toTypedArray()) }
        }
        is Wish.Restaurant -> oldState.copy(
            selectedRestaurant = wish.restaurant,
            errorTextRestaurant = null,
            email = if (oldState.email.isNotBlank() || wish.restaurant == null) oldState.email
            else oldStorage.getEmail(wish.restaurant.id).orEmpty()
        )
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        is Result.RestaurantsInfo.Success -> {
            val newState = oldState.copy(
                restaurants = res.info + oldState.restaurants.orEmpty()
            )
            if (newState.email != oldState.email) newState.copy(errorTextEmail = null)
            else newState
        }
        is Result.RestaurantsInfo.Error -> oldState.also { sendMessage(res.text) }
        Result.SignIn.Success -> oldState.also { router.backTo(Screens.OrdersScreen) }
        is Result.SignIn.Error -> oldState.copy(loading = false).also { sendMessage(res.text) }
    }

    private fun checkStateAndSignInIfPossible(oldState: State): State {
        if (oldState.loading) return oldState

        val emailError = when {
            oldState.email.isBlank() -> resourceProvider.getString(R.string.emailEmptyError)
            !isValidEmail(oldState.email) -> resourceProvider.getString(R.string.emailIsNotValid)
            else -> null
        }

        val passwordError = if (oldState.password.isNotBlank()) null
        else resourceProvider.getString(R.string.passwordEmptyError)

        val restaurantError = if (oldState.selectedRestaurant != null) null
        else resourceProvider.getString(R.string.noSelectedRestaurant)

        if (emailError != null || passwordError != null || restaurantError != null)
            return oldState.copy(
                errorTextEmail = emailError,
                errorTextPassword = passwordError,
                errorTextRestaurant = restaurantError
            )

        requireNotNull(oldState.selectedRestaurant)
        pushResult { auth(oldState.selectedRestaurant, oldState.email, oldState.password) }
        return oldState.copy(loading = true)
    }

    private suspend fun loadRestaurantInfo(vararg code: String): Result.RestaurantsInfo {
        val codesList = code.asList()
        if (codesList.isEmpty()) return Result.RestaurantsInfo.Success(emptyList())

        val request = RestaurantsInfoRequest(codesList)
        return when (val res = net.callResult { getRestaurantsInfo(request) }) {
            is ApiCallResult.Result -> Result.RestaurantsInfo.Success(res.value)
            is ApiCallResult.ErrorOccurred -> Result.RestaurantsInfo.Error(res.text)
        }
    }

    private suspend fun auth(
        restaurant: RestaurantDto,
        email: String,
        password: String
    ): Result.SignIn {
        val request = AuthRequest(restaurant.id, email, password)

        return when (val res = net.callResult { signIn(request) }) {
            is ApiCallResult.Result -> {
                oldStorage.restaurantCodes = oldStorage.restaurantCodes + restaurant.code // TODO in bottom dialog, not here
                oldStorage.addEmail(restaurant.id, email)
                userStorage.user = res.value.toDomain()
                Result.SignIn.Success
            }
            is ApiCallResult.ErrorOccurred -> Result.SignIn.Error(res.text)
        }
    }

    private var lastClickTime: Long? = null
    override fun onBackPressed() {
        val now = System.currentTimeMillis()
        if (lastClickTime == null || now - lastClickTime.onNull(0) > 2000) {
            lastClickTime = now
            sendMessage(resourceProvider.getString(R.string.clickAgainToCloseApp))
        } else router.exit()
    }
}