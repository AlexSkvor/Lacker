package com.lacker.visitors.data.api.fake

import com.lacker.visitors.data.api.Api
import com.lacker.visitors.data.dto.auth.LoginResponse
import com.lacker.visitors.data.dto.auth.UserFromServer
import com.lacker.visitors.data.dto.common.DateTimeResponse
import com.lacker.visitors.data.storage.files.FilesManager
import com.lacker.visitors.data.dto.auth.GoogleAuthData
import com.lacker.visitors.data.dto.common.DateTimeDto
import com.lacker.visitors.data.dto.menu.*
import com.lacker.visitors.data.dto.order.CurrentOrderResponse
import com.lacker.visitors.data.dto.order.Order
import com.lacker.visitors.data.dto.order.SubOrder
import com.lacker.visitors.data.dto.restaurants.TablesOfRestaurantResponse
import com.squareup.moshi.Moshi
import kotlinx.coroutines.delay
import java.time.OffsetDateTime
import java.util.*
import kotlin.random.Random

class FakeApi(
    private val filesManager: FilesManager,
    private val json: Moshi
) : Api {

    override suspend fun signInWithGoogle(request: GoogleAuthData): LoginResponse {
        delay(Random.nextLong(200, 5000))
        possiblyThrow()
        return LoginResponse(
            user = UserFromServer(
                id = UUID.randomUUID().toString(),
                email = "Email",
                name = "Name",
                surname = "Surname",
                token = "Fake Token hahaha",
                fullPhotoUrl = "https://i.ytimg.com/vi/Yh5whB-37HY/hqdefault_live.jpg"
            )
        )
    }

    override suspend fun getRestaurantMenu(restaurantId: String): MenuResponse {
        delay(Random.nextLong(200, 5000))
        possiblyThrow()
        return getMenu(restaurantId)
    }

    private suspend fun getMenu(restaurantId: String): MenuResponse {
        if (restaurantId !in restaurantIds) throw Exception()

        val menu = menus[restaurantId] ?: readOrGenerateMenu(restaurantId).menu
        val updated = possiblyUpdateMenu(menu)

        if (updated.timeStamp != menus[restaurantId]?.timeStamp) {
            menus[restaurantId] = updated
        }

        return MenuResponse(updated)
    }

    override suspend fun getRestaurantMenuTimestamp(restaurantId: String): DateTimeResponse {
        delay(Random.nextLong(100, 200))
        possiblyThrow()
        return DateTimeResponse(DateTimeDto(getMenu(restaurantId).menu.timeStamp))
    }

    override suspend fun getTablesOfRestaurant(restaurantId: String): TablesOfRestaurantResponse {
        if (restaurantId !in restaurantIds) throw Exception()
        return TablesOfRestaurantResponse(emptyList())
    }

    override suspend fun callStaff(restaurantId: String, type: String, tableId: String) {
        delay(Random.nextLong(100, 2000))
        possiblyThrow()
    }

    private var currentOrder = Order(status = "", subOrders = listOf())

    override suspend fun getCurrentOrder(
        restaurantId: String,
        tableId: String
    ): CurrentOrderResponse {
        delay(Random.nextLong(100, 5000))
        possiblyThrow()
        return CurrentOrderResponse(currentOrder)
    }

    override suspend fun addToCurrentOrder(
        restaurantId: String,
        tableId: String,
        subOrder: SubOrder
    ): CurrentOrderResponse {
        delay(Random.nextLong(100, 5000))
        possiblyThrow()
        currentOrder = currentOrder.copy(subOrders = listOf(subOrder) + currentOrder.subOrders)
        return CurrentOrderResponse(currentOrder)
    }

    private fun possiblyThrow(always: Boolean = false) {
        if (Random.nextInt(0, 100) > 90 || always) throw Exception()
    }

    private val menus: MutableMap<String, Menu> = mutableMapOf()

    private val restaurantIds = listOf(
        "55fef8db-8ccd-460b-85d2-84fd0cc59f21",
        "ce28ea33-4faf-446e-b21d-16e5e99491b9",
        "c9be96ae-aa4d-482c-acb5-dc5257c5af29"
    )

    private val pictures = listOf(
        "https://img.freepik.com/free-photo/flat-lay-japanese-dumplings-assortment_23-2148809859.jpg",
        "https://img.freepik.com/free-photo/plate-with-cooked-beef-with-sliced-colorful-pepper-covered-with-sauce_181624-26132.jpg?size=626&ext=jpg",
        "https://img.freepik.com/free-photo/green-lentil-soup-in-a-white-bowl-on-the-wooden-board_114579-31548.jpg",
        "https://img.freepik.com/free-photo/penne-pasta-with-meatballs-in-tomato-sauce-in-a-white-bowl_2829-7663.jpg",
        "https://img.freepik.com/free-photo/banquet-table-with-snacks_144627-18361.jpg",
        "https://img.freepik.com/free-photo/spaghetti-with-mixed-ingredients-in-a-white-plate-with-cutlery-set-aside_114579-15115.jpg",
        "https://img.freepik.com/free-photo/four-kinds-of-cream-soups-made-of-tomatoes-mushrooms-seafood-and-basil_181624-25273.jpg",
        "https://img.freepik.com/free-photo/top-view-tasty-meat-sauce-soup-with-greens-and-sliced-potatoes-on-black_140725-41254.jpg",
        "https://img.freepik.com/free-photo/russian-apetizer-blinchik-crepes-inside-white-plate_114579-2887.jpg",
        "https://img.freepik.com/free-photo/a-large-laid-table-of-different-dishes-for-the-whole-family-on-a-day-off_127425-26.jpg"
    )

    private val descriptions = listOf(
        "Описание 1. Прекрасное блюдо для компании.",
        "Описание 2. (Длинное-предлинное) Выбирая данное блюдо для себя и своей возлюбленной Вы гарантируете минимум четыре часа тотальнейшей обжираловки. Каждый ломтик будет словно самое вкусное, что Вы пробовали в жизни, и Вы будете задыхаться от натуги, пытаясь запихнуть его в себя. Как бы Вы ни старались, Вы не сможете съесть все, будь Вы хоть полнейший обжора. Вы скажете \"У меня живот болит\" и будете правы. Мы вынудим Вас съесть всё, или отрежем Вам уши и ноги и выдавим глаза - ведь именно части тел тех, кто не смог съесть это блюдо в прошлый раз придают нашему волшебному блюду такую пикантность. Во истину, Вы вступите в схватку с самой смерть, пытаесть съесть всё. И Вы проиграете, без шансов! Но, поверьте, это будет оооочень вкусная смерть!",
        "Описание 3. (С переносами) Далее следует перенос на 2 строки\n\nА вот тут уже описание, которое также может быть не в одну строчку! Например,\nРаз\nДва\nТри\n\tИ с табчиком",
        "Описание 4. В сущности, такое же, как и первое, но чуточку по длиннее: Прекрасное блюдо для компании.",
    )

    private suspend fun readOrGenerateMenu(restaurantId: String): MenuResponse {

        val saved = filesManager.getFileTextOrNull(restaurantId, FilesManager.FileType.Menu)

        val menu = if (saved != null) json.adapter(Menu::class.java).fromJson(saved) else null

        return menu?.let { MenuResponse(it) } ?: generateMenu()
    }

    private fun generateMenu(): MenuResponse {
        val timeStamp = OffsetDateTime.now()
        val itemsSize = Random.nextInt(5, 100)

        val items = List(itemsSize) {
            MenuItem(
                id = UUID.randomUUID().toString(),
                name = "Блюдо номер $it",
                photoFullUrl = pictures[it % pictures.size],
                shortDescription = descriptions.random(),
                portions = List(Random.nextInt(1, 3)) { n ->
                    Portion(
                        id = UUID.randomUUID().toString(),
                        price = Random.nextInt(10, 10000),
                        portionName = "Portion $n",
                        weight = n
                    )
                })
        }

        return MenuResponse(Menu(timeStamp, items))
    }

    private fun possiblyUpdateMenu(menu: Menu): Menu {
        if (Random.nextInt(0, 100) < 90) return menu

        val updatedItem = menu.items.random().let { it.copy(name = "Updated! ${it.name}") }

        return menu.copy(
            timeStamp = OffsetDateTime.now(),
            items = menu.items.map { if (it.id == updatedItem.id) updatedItem else it }
        )
    }
}