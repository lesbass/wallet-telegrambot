import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.jackson.objectBody
import com.natpryce.konfig.*

data class CheckUserRequest(val userName: String){}

class WalletApiGateway{

    private val config = EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("defaults.properties")

    private fun buildApiUrl(endpoint: String): String {
        val walletApiBaseUrl = config[Key("WALLET_BASE_URL", stringType)]
        return walletApiBaseUrl + endpoint
    }

    private fun getBearer(): String{
        return """Bearer ${config[Key("WALLET_API_KEY", stringType)]}"""
    }

    fun isAuthorized(checkUserRequest: CheckUserRequest): Boolean{
        val (_, _, result) = buildApiUrl("/check-user")
            .httpPost()
            .objectBody(checkUserRequest)
            .appendHeader("Authorization", getBearer())
            .responseString()

        return result is com.github.kittinunf.result.Result.Success
    }
}