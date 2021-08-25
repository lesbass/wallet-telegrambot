package com.lesbass.wallet

import WalletApiGateway
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.lesbass.wallet.service.AuthenticationService
import com.lesbass.wallet.service.CommandService
import com.lesbass.wallet.service.TextService
import com.natpryce.konfig.*

val walletApiGateway = WalletApiGateway()
val authenticationService = AuthenticationService(walletApiGateway)
val commandService = CommandService()
val textService = TextService(commandService)

fun processMessage(message: Message): String =
    if (!authenticationService.authorize(message.from?.username)) {
        """L'utente ${message.from?.username} non è autorizzato ad accedere a questo Bot."""
    } else {
        (message.text ?: "").let {
            if (it.startsWith("/")) {
                commandService.processCommand(it)
            } else {
                textService.processText(it)
            }
        }
    }

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

    val bot = bot {
        token = apiKey
        dispatch {
            text {
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id), text = processMessage(message)
                )
            }
        }
    }
    println("Polling...")
    bot.startPolling()
}