package com.lacker.staff.features.auth

import com.lacker.staff.R
import com.lacker.staff.data.api.ApiCallResult
import com.lacker.staff.data.api.NetworkManager
import com.lacker.staff.data.dto.auth.AuthRequest
import com.lacker.staff.data.dto.auth.toDomain
import com.lacker.staff.data.dto.restaurant.RestaurantDto
import com.lacker.staff.data.storage.restaurants.SignedBeforeRestaurantsStorage
import com.lacker.staff.data.storage.user.UserStorage
import javax.inject.Inject
import com.lacker.staff.features.auth.AuthMachine.Wish
import com.lacker.staff.features.auth.AuthMachine.State
import com.lacker.staff.features.auth.AuthMachine.Result
import com.lacker.staff.navigation.Screens
import com.lacker.utils.extensions.isValidEmail
import com.lacker.utils.extensions.onNull
import com.lacker.utils.resources.ResourceProvider
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class AuthMachine @Inject constructor(
    private val oldStorage: SignedBeforeRestaurantsStorage,
    private val net: NetworkManager,
    private val router: Router,
    private val resourceProvider: ResourceProvider,
    private val userStorage: UserStorage
) : Machine<Wish, Result, State>() {

    sealed class Wish {
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

        sealed class Restaurants : Result() {
            data class Success(val restaurants: List<RestaurantDto>) : Result.Restaurants()
            data class Error(val text: String) : Result.Restaurants()
        }
    }

    data class State(
        val loading: Boolean = false,
        val restaurantsLoading: Boolean = false,
        val email: String = "",
        val password: String = "",
        val restaurants: List<RestaurantDto>? = null,
        val selectedRestaurant: RestaurantDto? = null,
        val errorTextEmail: String? = null,
        val errorTextPassword: String? = null
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
        Wish.Start -> oldState.copy(restaurantsLoading = true)
            .also { pushResult { loadRestaurants() } }
        is Wish.Restaurant -> oldState.setRestaurant(wish.restaurant)
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        is Result.Restaurants.Success -> {
            val newState = oldState.copy(restaurants = res.restaurants, restaurantsLoading = false)
            if (res.restaurants.size != 1) newState // TODO autoselect last restaurant!
            else newState.setRestaurant(res.restaurants.first())
        }
        is Result.Restaurants.Error -> oldState.copy(restaurantsLoading = false)
            .also { sendMessage(res.text) }
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

        restaurantError?.let { sendMessage(restaurantError) }

        if (emailError != null || passwordError != null || restaurantError != null)
            return oldState.copy(
                errorTextEmail = emailError,
                errorTextPassword = passwordError
            )

        requireNotNull(oldState.selectedRestaurant)
        pushResult { auth(oldState.selectedRestaurant, oldState.email, oldState.password) }
        return oldState.copy(loading = true)
    }

    private fun State.setRestaurant(restaurant: RestaurantDto?): State {
        return copy(
            selectedRestaurant = restaurant,
            email = if (email.isNotBlank() || restaurant == null) email
            else oldStorage.getEmail(restaurant.id).orEmpty()
        )
    }

    private suspend fun loadRestaurants(): Result.Restaurants {
        return when (val res = net.callResult { getRestaurants() }) {
            is ApiCallResult.Result -> Result.Restaurants.Success(res.value)
            is ApiCallResult.ErrorOccurred -> Result.Restaurants.Error(res.text)
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