package com.lesbass.wallet

import CategoriesRequest
import WalletApiGateway
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.lesbass.wallet.infrastructure.WalletCategory
import com.lesbass.wallet.service.AuthenticationService
import com.natpryce.konfig.*

val walletApiGateway = WalletApiGateway()
val authenticationService = AuthenticationService(walletApiGateway)

fun main() {
    val config = EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("defaults.properties")
    val apiKey = config[Key("API_KEY", stringType)]
    println("Telegram Bot started! ")
    println(
        "apiKey: ${
            if (apiKey.isNotEmpty()) {
                "✔"
            } else {
                "❌"
            }
        }"
    )

    val catPrefix = "category-"

    val bot = bot {
        token = apiKey
        dispatch {

            command("add") {
                if (!authenticationService.authorize(message.from?.username)) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = """L'utente ${message.from?.username} non è autorizzato ad accedere a questo Bot."""
                    )

                } else {
                    fun buildCategoryButton(category: WalletCategory): InlineKeyboardButton.CallbackData =
                        InlineKeyboardButton.CallbackData(
                            text = category.getLabelWithEmoji(),
                            callbackData = catPrefix.plus(category.id)
                        )

                    val categories = walletApiGateway.getCategories(CategoriesRequest(message.from?.username!!))
                    val mainCategoriesButton = InlineKeyboardMarkup.create(
                        categories
                            .filter { it.top }
                            .map { buildCategoryButton(it) }
                            .chunked(1)
                    )
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Per prima cosa, seleziona la categoria! Ecco quelle principali:",
                        replyMarkup = mainCategoriesButton
                    )
                    val otherCategoriesButton = InlineKeyboardMarkup.create(
                        categories
                            .filter { !it.top }
                            .map { buildCategoryButton(it) }
                            .chunked(3)
                    )
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "E qui ci sono le altre:",
                        replyMarkup = otherCategoriesButton
                    )
                }
            }

            callbackQuery {
                val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery
                if (callbackQuery.data.startsWith(catPrefix)) {
                    val catId = callbackQuery.data.substring(catPrefix.length)
                    val request = CategoriesRequest(callbackQuery.from.username!!)
                    val category = walletApiGateway.getCategories(request)
                        .find { it.id == catId }
                    bot.sendMessage(ChatId.fromId(chatId), "Categoria selezionata: " + category?.getLabelWithEmoji())
                    bot.sendMessage(ChatId.fromId(chatId), "Quale è l'importo?")
                }
            }

            text("ping") {
                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Pong")
            }

            telegramError {
                println(error.getErrorMessage())
            }
        }
    }
    println("Polling...")
    bot.startPolling()
}