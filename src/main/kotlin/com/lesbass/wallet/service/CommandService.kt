package com.lesbass.wallet.service

import CategoriesRequest
import WalletApiGateway
import com.github.kotlintelegrambot.entities.Message

class CommandService(private val walletApiGateway: WalletApiGateway) {

    private val commands = mapOf<String, (message: Message) -> String>(
        "categorie" to ::getCategorie,
        "comandi" to ::getComandi
    )

    fun processCommand(message: Message): String {
        val words = message.text?.split(" ")
        val command = words?.first()?.substring(1)
        return if (commands.containsKey(command)) {
            commands[command]!!.invoke(message)
        } else {
            "Comando non trovato"
        }
    }

    private fun getCategorie(message: Message): String {
        val userName = message.from?.username!!
        val categories = walletApiGateway.getCategories(CategoriesRequest(userName))
        return categories.joinToString("\n") { (it.icon?.emoji + " " + it.label).trim() }
    }

    fun getComandi(message: Message): String {
        return listOf("Ecco un elenco dei comandi disponibili:")
            .union(commands.map { """/${it.key} """ })
            .joinToString("\n")
    }
}