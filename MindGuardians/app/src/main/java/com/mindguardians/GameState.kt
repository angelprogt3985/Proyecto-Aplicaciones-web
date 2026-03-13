package com.mindguardians

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── Modelos ──────────────────────────────────────────────────────────────────

data class Monster(
    val name: String,
    val type: String,
    val maxHp: Int,
)

data class BattleMessage(
    val id: String,
    val text: String,
    val type: MessageType,
)

enum class MessageType { DAMAGE, REWARD, INFO }

enum class Screen { COMBAT, DASHBOARD, SHOP, RANKING, ORACLE }

val MONSTERS = listOf(
    Monster("Titán del Sedentarismo", "Gravedad Pesada", 100),
    Monster("Cometa de la Fatiga",    "Vacío Estelar",   120),
    Monster("Nebulosa del Estrés",    "Caos Cósmico",    150),
)

// ── ViewModel ────────────────────────────────────────────────────────────────

class GameViewModel : ViewModel() {

    var heroHp          by mutableIntStateOf(100)
    var heroLevel       by mutableIntStateOf(1)
    var heroXp          by mutableIntStateOf(0)
    var heroGold        by mutableIntStateOf(0)

    var monsterIndex    by mutableIntStateOf(0)
    var monsterHp       by mutableIntStateOf(MONSTERS[0].maxHp)
    var isAttacking     by mutableStateOf(false)
    var isVictory       by mutableStateOf(false)
    var menuOpen        by mutableStateOf(false)
    var currentScreen   by mutableStateOf(Screen.COMBAT)

    val battleLog = mutableStateListOf<BattleMessage>()

    val currentMonster get() = MONSTERS[monsterIndex]

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
        if (heroXp >= 100) {
            heroLevel++
            heroXp -= 100
        }
        isVictory    = false
        monsterIndex = (monsterIndex + 1) % MONSTERS.size
        monsterHp    = MONSTERS[monsterIndex].maxHp
    }

    fun goldReward() = 20 + monsterIndex * 10
    fun xpReward()   = 30 + monsterIndex * 15
}
