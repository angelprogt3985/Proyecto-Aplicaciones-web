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

data class Monster(val name: String, val type: String, val maxHp: Int, val isBoss: Boolean = false)
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

    private val monsterQueue = mutableStateListOf<Monster>().also { it.addAll(MONSTERS) }

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

    val currentMonster get() = monsterQueue.firstOrNull() ?: MONSTERS[0]

    var purchasedIds     = mutableStateListOf<String>()

    var ranking          = mutableStateListOf<Map<String, Any>>()

    var isPurchasing     by mutableStateOf(false)

    var isLoadingShop    by mutableStateOf(true)

    var isLoadingRanking by mutableStateOf(true)

    var bonusActive     by mutableStateOf(false)

    var reportText      by mutableStateOf("")

    var isReporting     by mutableStateOf(false)

    var isDefeat        by mutableStateOf(false)

    var isMonsterActing by mutableStateOf(false)


    init {
        loadUser()
        loadInventory()
        loadRanking()
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

    private fun loadInventory() {
        viewModelScope.launch {
            val ids = repository.loadInventory()
            purchasedIds.addAll(ids)
            isLoadingShop = false
        }
    }

    private fun loadRanking() {
        viewModelScope.launch {
            val data = repository.loadRanking()
            ranking.clear()
            ranking.addAll(data)
            isLoadingRanking = false
        }
    }

    fun purchaseItem(
        itemId:   String,
        itemName: String,
        itemStat: String,
        emoji:    String,
        price:    Int,
    ) {
        if (isPurchasing) return
        if (heroGold < price) return
        if (purchasedIds.contains(itemId)) return

        isPurchasing = true
        viewModelScope.launch {
            val success = repository.spendGold(
                amount   = price,
                itemId   = itemId,
                itemName = itemName,
                itemStat = itemStat,
                emoji    = emoji,
            )
            if (success) {
                heroGold -= price
                purchasedIds.add(itemId)
            }
            isPurchasing = false
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
        if (monsterHp <= 0 || isMonsterActing) return
        val finalDamage = if (bonusActive) (damage * 1.5).toInt() else damage
        bonusActive = false
        isAttacking = true
        val newMonsterHp = maxOf(0, monsterHp - finalDamage)
        monsterHp = newMonsterHp
        viewModelScope.launch {
            val narration = try {
                geminiRepository.narrateHeroAttack(
                    monsterName  = currentMonster.name,
                    monsterType  = currentMonster.type,
                    actionName   = actionName,
                    damage       = finalDamage,
                    bonusActive  = finalDamage > damage,
                )
            } catch (e: Exception) {
                "¡$actionName! -$finalDamage HP al enemigo."
            }
            addLog(narration, MessageType.DAMAGE)
            delay(400)
            isAttacking = false
            if (newMonsterHp <= 0) {
                isVictory = true
                addLog("¡${currentMonster.name} derrotado!", MessageType.REWARD)
                return@launch
            }
            monsterCounterattack(actionName)
        }
    }

    private suspend fun monsterCounterattack(heroAction: String) {
        val monsterDamage = (currentMonster.maxHp / 10) + if (currentMonster.isBoss) 10 else 0 + (2..8).random()
        isMonsterActing = true
        val narration = try {
            geminiRepository.narrateMonsterAttack(currentMonster.name, heroAction, monsterDamage)
        } catch (e: Exception) {
            "${currentMonster.name} contraataca. -$monsterDamage HP."
        }
        val newHeroHp = maxOf(0, heroHp - monsterDamage)
        heroHp = newHeroHp
        addLog(narration, MessageType.INFO)
        isMonsterActing = false
        if (newHeroHp <= 0) {
            isDefeat = true
            addLog("¡Has caído en combate!", MessageType.DAMAGE)
            viewModelScope.launch {
                repository.saveBattle(currentMonster.type, "Derrota", 0, 0)
            }
        }
    }

    fun recoverAfterDefeat() {
        heroHp    = 100
        isDefeat  = false
        monsterHp = currentMonster.maxHp
        viewModelScope.launch {
            repository.saveUserData(
                heroLevel = heroLevel,
                heroXp    = heroXp,
                heroGold  = heroGold,
                heroHp    = heroHp,
                totalXp   = heroXp + (heroLevel - 1) * 100,
            )
        }
    }

    fun continueAfterVictory() {
        val gold = if (currentMonster.isBoss) 60 else 20 + (monsterQueue.size % MONSTERS.size) * 10
        val xp   = if (currentMonster.isBoss) 80 else 30 + (monsterQueue.size % MONSTERS.size) * 15
        heroGold += gold
        heroXp   += xp
        if (heroXp >= 100) { heroLevel++; heroXp -= 100 }
        isVictory = false

        viewModelScope.launch {
            repository.saveBattle(currentMonster.type, "Victoria", gold, xp)
            // Sacar el monstruo derrotado y pasar al siguiente; si la cola queda vacía, recargar base
            if (monsterQueue.isNotEmpty()) monsterQueue.removeAt(0)
            if (monsterQueue.isEmpty()) monsterQueue.addAll(MONSTERS)
            monsterHp = currentMonster.maxHp
            repository.saveUserData(
                heroLevel = heroLevel,
                heroXp    = heroXp,
                heroGold  = heroGold,
                heroHp    = heroHp,
                totalXp   = heroXp + (heroLevel - 1) * 100,
            )
        }
    }

    fun goldReward() = if (currentMonster.isBoss) 60 else 20 + (monsterQueue.size % MONSTERS.size) * 10
    fun xpReward()   = if (currentMonster.isBoss) 80 else 30 + (monsterQueue.size % MONSTERS.size) * 15

    fun reportDeed(deed: String) {
        if (deed.isBlank() || isReporting) return
        isReporting = true
        viewModelScope.launch {
            val narration = try {
                geminiRepository.validateDeed(deed)
            } catch (e: Exception) {
                "¡Tu hazaña fortalece tu próximo golpe!"
            }
            bonusActive = true
            addLog(narration, MessageType.REWARD)
            reportText  = ""
            isReporting = false
        }
    }

    fun reportWeakness(weakness: String) {
        if (weakness.isBlank() || isReporting) return
        isReporting = true
        viewModelScope.launch {
            val (bossName, bossType) = try {
                geminiRepository.generateBoss(weakness)
            } catch (e: Exception) {
                Pair("Señor del Caos", "Fuerza Oscura")
            }
            val boss = Monster(bossName, bossType, 180, isBoss = true)
            if (monsterQueue.size > 1) monsterQueue.add(1, boss) else monsterQueue.add(boss)
            addLog("¡$bossName acecha en el horizonte!", MessageType.INFO)
            reportText  = ""
            isReporting = false
        }
    }
}