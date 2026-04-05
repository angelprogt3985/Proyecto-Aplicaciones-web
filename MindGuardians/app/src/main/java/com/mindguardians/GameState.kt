package com.mindguardians

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mindguardians.data.FirebaseRepository
import com.mindguardians.data.GeminiRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Monster(val name: String, val type: String, val maxHp: Int)
data class BattleMessage(val id: String, val text: String, val type: MessageType)

enum class MessageType { DAMAGE, REWARD, INFO }
enum class Screen { COMBAT, DASHBOARD, SHOP, RANKING, ORACLE }

val MONSTERS = listOf(
    Monster("Titán del Sedentarismo", "Gravedad Pesada", 100),
    Monster("Cometa de la Fatiga",    "Vacío Estelar",   120),
    Monster("Nebulosa del Estrés",    "Caos Cósmico",    150),
)

class GameViewModel : ViewModel() {

    private val repository       = FirebaseRepository()
    private val geminiRepository = GeminiRepository()

    var heroHp        by mutableIntStateOf(100)
    var heroLevel     by mutableIntStateOf(1)
    var heroXp        by mutableIntStateOf(0)
    var heroGold      by mutableIntStateOf(0)
    var heroName      by mutableStateOf(
        // Leer nombre inmediatamente de Firebase Auth (sin esperar Firestore)
        FirebaseAuth.getInstance().currentUser?.displayName
            ?.takeIf { it.isNotBlank() } ?: "Guerrero"
    )

    var monsterIndex  by mutableIntStateOf(0)
    var monsterHp     by mutableIntStateOf(MONSTERS[0].maxHp)
    var isAttacking   by mutableStateOf(false)
    var isVictory     by mutableStateOf(false)
    var menuOpen      by mutableStateOf(false)
    var currentScreen by mutableStateOf(Screen.COMBAT)
    var isLoadingUser by mutableStateOf(true)

    var oracleMessages  = mutableStateListOf<Pair<String, String>>()
    var isOracleLoading by mutableStateOf(false)
    var oracleError     by mutableStateOf<String?>(null)

    val battleLog = mutableStateListOf<BattleMessage>()

    val currentMonster get() = MONSTERS[monsterIndex]

    init {
        loadUser()
        oracleMessages.add(
            Pair("oracle", "¡Salve, Guerrero Estelar! El cosmos observa tu jornada. ¿Qué hazañas de salud has realizado hoy?")
        )
    }

    private fun loadUser() {
        viewModelScope.launch {
            val data = repository.loadUserData()
            if (data != null) {
                heroLevel = (data["heroLevel"] as? Long)?.toInt() ?: 1
                heroXp    = (data["heroXp"]    as? Long)?.toInt() ?: 0
                heroGold  = (data["heroGold"]  as? Long)?.toInt() ?: 0
                heroHp    = (data["heroHp"]    as? Long)?.toInt() ?: 100
                // Prioridad: Firestore > Firebase Auth > fallback
                val nameFromFirestore = (data["displayName"] as? String)?.takeIf { it.isNotBlank() }
                val nameFromAuth = FirebaseAuth.getInstance().currentUser?.displayName?.takeIf { it.isNotBlank() }
                heroName = nameFromFirestore ?: nameFromAuth ?: "Guerrero"
            }
            isLoadingUser = false
        }
    }

    fun consultOracle(userMessage: String) {
        if (userMessage.isBlank()) return
        oracleMessages.add(Pair("user", userMessage))
        isOracleLoading = true
        oracleError     = null
        viewModelScope.launch {
            val reply = try {
                geminiRepository.consultOracle(userMessage)
            } catch (e: Exception) {
                oracleError = "Error al contactar al Oráculo. Intenta de nuevo."
                null
            }
            if (reply != null) oracleMessages.add(Pair("oracle", reply))
            isOracleLoading = false
        }
    }

    fun addLog(text: String, type: MessageType = MessageType.INFO) {
        battleLog.add(BattleMessage(System.currentTimeMillis().toString() + Math.random(), text, type))
        if (battleLog.size > 20) battleLog.removeAt(0)
    }

    fun attack(damage: Int, actionName: String) {
        if (monsterHp <= 0) return
        isAttacking = true
        val newHp = maxOf(0, monsterHp - damage)
        monsterHp = newHp
        addLog("¡$actionName! -$damage HP al enemigo", MessageType.DAMAGE)
        viewModelScope.launch {
            delay(400)
            isAttacking = false
            if (newHp <= 0) {
                isVictory = true
                addLog("¡Enemigo derrotado!", MessageType.REWARD)
            }
        }
    }

    fun continueAfterVictory() {
        val gold = 20 + monsterIndex * 10
        val xp   = 30 + monsterIndex * 15
        heroGold += gold
        heroXp   += xp
        if (heroXp >= 100) { heroLevel++; heroXp -= 100 }
        isVictory    = false
        monsterIndex = (monsterIndex + 1) % MONSTERS.size
        monsterHp    = MONSTERS[monsterIndex].maxHp

        viewModelScope.launch {
            repository.saveBattle("Combate", "Victoria", gold, xp)
            repository.saveUserData(
                heroLevel = heroLevel,
                heroXp    = heroXp,
                heroGold  = heroGold,
                heroHp    = heroHp,
                totalXp   = heroXp + (heroLevel - 1) * 100,
            )
        }
    }

    fun goldReward() = 20 + monsterIndex * 10
    fun xpReward()   = 30 + monsterIndex * 15
}