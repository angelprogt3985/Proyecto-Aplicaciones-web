package com.mindguardians.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private const val CLOUD_FUNCTION_URL = "https://us-central1-mindguardians-b07d3.cloudfunctions.net/consult_oracle"

private data class OracleRequest(
    val message: String
)

private data class OracleResponse(
    val reply: String?
)

private interface OracleApi {
    @POST("consult_oracle")
    suspend fun consult(@Body request: OracleRequest): OracleResponse
}

class GeminiRepository {

    private val api: OracleApi = Retrofit.Builder()
        .baseUrl(CLOUD_FUNCTION_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OracleApi::class.java)

    suspend fun consultOracle(userMessage: String): String {
        val response = api.consult(OracleRequest(message = userMessage))
        return response.reply ?: "El cosmos guarda silencio por ahora. Intenta de nuevo."
    }
}