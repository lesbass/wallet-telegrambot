package com.lesbass.wallet.service

class CommandService {

    val commands = mapOf<String, () -> String>(
        "categorie" to ::getCategorie,
        "comandi" to ::getComandi
    )

    fun processCommand(text: String): String {
        val words = text.split(" ")
        val command = words.first().substring(1)
        return if (commands.containsKey(command)) {
            commands[command]!!.invoke()
        } else {
            "Comando non trovato"
        }
    }

    private fun getCategorie(): String {
        return "Elenco categorie"
    }

    fun getComandi(): String {
        return listOf("Ecco un elenco dei comandi disponibili:")
            .union(commands.map { """/${it.key} """ })
            .joinToString("\n")
    }
}