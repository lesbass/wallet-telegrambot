package com.lesbass.wallet.service

import com.github.kotlintelegrambot.entities.Message

class TextService(private val commandService: CommandService) {
    fun processText(message: Message): String {
        return "Hey! Dovresti usare i comandi! Per la lista completa dei comandi. \n\n" + commandService.getComandi(message)
    }
}