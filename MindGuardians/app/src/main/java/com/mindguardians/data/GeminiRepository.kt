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

    suspend fun narrateMonsterAttack(monsterName: String, heroAction: String, damage: Int): String {
        val prompt = """Eres el Narrador épico de MindGuardians, un RPG de bienestar cósmico.
            El héroe usó "$heroAction" contra "$monsterName".
            El monstruo contraataca infligiendo $damage puntos de daño.
            Narra el contraataque en UNA frase épica (máximo 20 palabras). Sin comillas. Solo la frase."""
        val response = api.consult(OracleRequest(message = prompt))
        return response.reply ?: "$monsterName contraataca con fuerza oscura."
    }

    suspend fun narrateHeroAttack(
        monsterName: String,
        monsterType: String,
        actionName: String,
        damage: Int,
        bonusActive: Boolean,
    ): String {
        val bonusText = if (bonusActive) "El ataque lleva un bono de poder activo." else ""
        val prompt = """Eres el Narrador épico de MindGuardians, un RPG de bienestar cósmico.
            El héroe usa "$actionName" contra "$monsterName" (tipo: $monsterType) causando $damage de daño. $bonusText
            Narra el ataque en UNA frase épica que refleje si fue muy efectivo o apenas dañino según el tipo del enemigo (máximo 20 palabras). Sin comillas. Solo la frase."""
        val response = api.consult(OracleRequest(message = prompt))
        return response.reply ?: "¡$actionName golpea a $monsterName! -$damage HP."
    }

    suspend fun validateDeed(deed: String): String {
        val prompt = """Eres el Narrador épico de MindGuardians, un RPG de bienestar cósmico.
            El héroe reporta esta hazaña de salud: "$deed"
            Responde con UNA frase épica corta (máximo 20 palabras) que celebre la hazaña y mencione que otorga poder adicional al próximo ataque. Sin comillas. Solo la frase."""
        val response = api.consult(OracleRequest(message = prompt))
        return response.reply ?: "¡Tu hazaña fortalece tu próximo golpe!"
    }

    suspend fun generateBoss(weakness: String): Pair<String, String> {
        val prompt = """Eres el creador de enemigos de MindGuardians, un RPG de bienestar cósmico.
            El héroe reporta esta debilidad: "$weakness"
            Crea un jefe final basado en esa debilidad. Responde EXACTAMENTE en este formato sin nada más:
            NOMBRE: [nombre épico del jefe en español, máximo 4 palabras]
            TIPO: [tipo o poder del jefe en español, máximo 3 palabras]"""
        val response = api.consult(OracleRequest(message = prompt))
        val reply = response.reply ?: "NOMBRE: Señor del Caos\nTIPO: Energía Oscura"
        val name = reply.lines().firstOrNull { it.startsWith("NOMBRE:") }
            ?.removePrefix("NOMBRE:")?.trim() ?: "Jefe del Abismo"
        val type = reply.lines().firstOrNull { it.startsWith("TIPO:") }
            ?.removePrefix("TIPO:")?.trim() ?: "Fuerza Cósmica"
        return Pair(name, type)
    }
}