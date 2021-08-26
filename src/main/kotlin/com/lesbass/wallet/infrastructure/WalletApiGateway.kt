import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.jackson.objectBody
import com.github.kittinunf.fuel.jackson.responseObject
import com.lesbass.wallet.infrastructure.WalletCategory
import com.natpryce.konfig.*

data class CheckUserRequest(val userName: String)
data class CategoriesRequest(val userName: String)

class WalletApiGateway {

    private val objectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val config = EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("defaults.properties")

    private fun buildApiUrl(endpoint: String): String {
        val walletApiBaseUrl = config[Key("WALLET_BASE_URL", stringType)]
        return walletApiBaseUrl + endpoint
    }

    private fun getBearer(): String {
        return """Bearer ${config[Key("WALLET_API_KEY", stringType)]}"""
    }

    fun isAuthorized(checkUserRequest: CheckUserRequest): Boolean {
        val (_, _, result) = buildApiUrl("/check-user")
            .httpPost()
            .appendHeader("Authorization", getBearer())
            .objectBody(checkUserRequest)
            .responseString()

        return result is com.github.kittinunf.result.Result.Success
    }

    fun getCategories(categoriesRequest: CategoriesRequest): List<WalletCategory> {
        val (_, _, result) = buildApiUrl("/category")
            .httpPost()
            .appendHeader("Authorization", getBearer())
            .objectBody(categoriesRequest)
            .responseObject<List<WalletCategory>>(mapper = objectMapper)

        return result.fold(
            success = { it },
            failure = { error -> throw Exception(error.message ?: "Errore generico") }
        )
    }
}