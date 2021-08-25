package com.lesbass.wallet.service

import CheckUserRequest
import WalletApiGateway

class AuthenticationService(private val walletApiGateway: WalletApiGateway) {

    fun authorize(userName: String?): Boolean {
        return !userName.isNullOrEmpty() && walletApiGateway.isAuthorized(CheckUserRequest(userName))
    }
}