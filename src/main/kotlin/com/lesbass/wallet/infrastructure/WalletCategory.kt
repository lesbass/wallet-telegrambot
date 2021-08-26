package com.lesbass.wallet.infrastructure

data class WalletCategory(val icon: Icon?, val id: String, val label: String, val top: Boolean){
    fun getLabelWithEmoji() = (icon?.emoji + " " + label).trim()
}
data class Icon(val type: String, val emoji: String?, val file: File?)
data class File(val url: String)