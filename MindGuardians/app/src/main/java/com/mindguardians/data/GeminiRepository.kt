package com.mindguardians.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

private const val CLOUD_FUNCTION_URL = "https://consult-oracle-ajesprbufa-uc.a.run.app/"

private data class OracleRequest(
    val message: String
)

private data class OracleResponse(
    val reply: String?
)

private interface OracleApi {
    @POST(".")
    suspend fun consult(@Body request: OracleRequest): OracleResponse
}

class GeminiRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val api: OracleApi = Retrofit.Builder()
        .baseUrl(CLOUD_FUNCTION_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OracleApi::class.java)

    suspend fun consultOracle(userMessage: String): String {
        val response = api.consult(OracleRequest(message = userMessage))
        return response.reply ?: "El cosmos guarda silencio por ahora. Intenta de nuevo."
    }
}