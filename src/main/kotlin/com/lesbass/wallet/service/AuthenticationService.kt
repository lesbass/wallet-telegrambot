package com.lesbass.wallet.service

class AuthenticationService {

    val authorizedUsers = listOf("lesbass")

    fun authorize(userName: String?): Boolean {
        return authorizedUsers.contains(userName)
    }
}