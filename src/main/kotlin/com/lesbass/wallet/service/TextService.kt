package com.lesbass.wallet.service

class TextService(private val commandService: CommandService) {
    fun processText(text: String): String {
        return "Hey! Dovresti usare i comandi! Per la lista completa dei comandi. \n\n" + commandService.getComandi()
    }
}