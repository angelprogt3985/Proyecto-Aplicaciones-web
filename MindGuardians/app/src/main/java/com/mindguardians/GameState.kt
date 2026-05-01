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
import com.mindguardians.data.InventoryItemData
import com.mindguardians.data.GeminiRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.mindguardians.data.ShopItemData

data class Monster(val name: String, val type: String, val maxHp: Int, val isBoss: Boolean = false)
data class BattleMessage(val id: String, val text: String, val type: MessageType)

enum class MessageType { DAMAGE, REWARD, INFO }
// INVENTORY es nuevo aquí ↓
enum class Screen { COMBAT, DASHBOARD, SHOP, RANKING, ORACLE, INVENTORY, GUIDE }

val MONSTERS = listOf(
    Monster("Titán del Sedentarismo", "Gravedad Pesada", 100),
    Monster("Cometa de la Fatiga",    "Vacío Estelar",   120),
    Monster("Nebulosa del Estrés",    "Caos Cósmico",    150),
)

class GameViewModel : ViewModel() {

    private val repository       = FirebaseRepository()
    private val geminiRepository = GeminiRepository()

    var heroHp    by mutableIntStateOf(100)
    var heroLevel by mutableIntStateOf(1)
    var heroXp    by mutableIntStateOf(0)
    var heroGold  by mutableIntStateOf(0)
    // totalXp acumulado históricamente — nunca se resetea al subir de nivel
    var totalXp   by mutableIntStateOf(0)
    var heroMaxHP     by mutableIntStateOf(100)
    var heroName      by mutableStateOf(
        // Leer nombre inmediatamente de Firebase Auth (sin esperar Firestore)
        FirebaseAuth.getInstance().currentUser?.displayName
            ?.takeIf { it.isNotBlank() } ?: "Guerrero"
    )

    private val monsterQueue = mutableStateListOf<Monster>().also { it.addAll(MONSTERS) }

    var monsterHp     by mutableIntStateOf(MONSTERS[0].maxHp)

    var isBusy          by mutableStateOf(false)
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

    // IDs comprados (filtra la tienda)
    var purchasedIds = mutableStateListOf<String>()

    // Inventario completo con metadatos (pantalla Inventario)
    var inventoryItems = mutableStateListOf<InventoryItemData>()

    var ranking            = mutableStateListOf<Map<String, Any>>()
    var isPurchasing       by mutableStateOf(false)
    var isLoadingShop      by mutableStateOf(true)
    var isLoadingRanking   by mutableStateOf(true)
    var isLoadingInventory by mutableStateOf(false)
    var isLoadingCatalog   by mutableStateOf(true)

    // Catálogo viene del repositorio — fuente única de verdad
    var shopCatalog = mutableStateListOf<ShopItemData>()

    var bonusActive     by mutableStateOf(false)

    var reportText      by mutableStateOf("")

    var isReporting     by mutableStateOf(false)

    var isDefeat        by mutableStateOf(false)

    var isMonsterActing by mutableStateOf(false)


    init {
        loadUserThenInventory()
        loadShopCatalog()
        loadRanking()
        oracleMessages.add(
            Pair("oracle", "¡Salve, Guerrero Estelar! El cosmos observa tu jornada. ¿Qué hazañas de salud has realizado hoy?")
        )
    }

    // Suma bonusHp y bonusPower de todos los ítems actualmente en inventoryItems
    private fun computeStatBonuses(): Pair<Int, Int> {
        var hp    = 0
        var power = 0
        for (item in inventoryItems) {
            hp    += item.bonusHp
            power += item.bonusPower
        }
        return Pair(hp, power)
    }



    private fun loadUserThenInventory() {
        viewModelScope.launch {
            // 1. Cargar datos del héroe
            val data = repository.loadUserData()
            if (data != null) {
                heroLevel = (data["heroLevel"] as? Long)?.toInt() ?: 1
                heroXp    = (data["heroXp"]    as? Long)?.toInt() ?: 0
                heroGold  = (data["heroGold"]  as? Long)?.toInt() ?: 0
                totalXp   = (data["totalXp"]   as? Long)?.toInt() ?: 0
                val savedMaxHP = (data["heroMaxHP"] as? Long)?.toInt() ?: 100
                heroMaxHP = savedMaxHP
                heroHp    = ((data["heroHp"] as? Long)?.toInt() ?: savedMaxHP).coerceAtMost(heroMaxHP)
                val nameFromFirestore = (data["displayName"] as? String)?.takeIf { it.isNotBlank() }
                val nameFromAuth      = FirebaseAuth.getInstance().currentUser?.displayName?.takeIf { it.isNotBlank() }
                heroName = nameFromFirestore ?: nameFromAuth ?: "Guerrero"
            }
            isLoadingUser = false

            // 2. Cargar inventario solo DESPUÉS de tener heroMaxHP correcto de Firestore
            val ids   = repository.loadInventory()
            val items = repository.loadFullInventory()
            purchasedIds.addAll(ids)
            inventoryItems.addAll(items)

            // 3. Ahora sí aplicar bonus del equipo encima del heroMaxHP ya restaurado
            applyEquipmentBonuses()
            isLoadingShop = false
        }
    }

    // Recalcula heroMaxHP según el equipo y ajusta heroHp si excede el nuevo máximo
    private fun applyEquipmentBonuses() {
        val (bonusHp, _) = computeStatBonuses()
        val baseFromLevel = heroMaxHP - computeEquipmentHpBonus()
        heroMaxHP = baseFromLevel + bonusHp
        if (heroHp > heroMaxHP) heroHp = heroMaxHP
    }

    private fun computeEquipmentHpBonus(): Int = inventoryItems.sumOf { it.bonusHp }


    private fun loadShopCatalog() {
        viewModelScope.launch {
            val items = repository.loadShopCatalog()
            shopCatalog.clear()
            shopCatalog.addAll(items)
            isLoadingCatalog = false
        }
    }

    fun refreshFullInventory() {
        isLoadingInventory = true
        viewModelScope.launch {
            val items = repository.loadFullInventory()
            inventoryItems.clear()
            inventoryItems.addAll(items)
            applyEquipmentBonuses()
            isLoadingInventory = false
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
        bonusHp:    Int = 0,
        bonusPower: Int = 0,
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
                bonusHp    = bonusHp,
                bonusPower = bonusPower,
            )
            if (success) {
                heroGold -= price
                purchasedIds.add(itemId)
                val items = repository.loadFullInventory()
                inventoryItems.clear()
                inventoryItems.addAll(items)
                applyEquipmentBonuses()
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
                //oracleError = "Error: ${e.localizedMessage ?: "Error desconocido"}"
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
        if (isBusy || monsterHp <= 0 || isDefeat) return
        isBusy = true
        val (_, equipPower) = computeStatBonuses()
        val boostedDamage = damage + (damage * equipPower / 100)
        val finalDamage   = if (bonusActive) (boostedDamage * 1.5).toInt() else boostedDamage
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
                isBusy    = false
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
        isBusy          = false
        if (newHeroHp <= 0) {
            isDefeat = true
            addLog("¡Has caído en combate!", MessageType.DAMAGE)
            viewModelScope.launch {
                repository.saveBattle(currentMonster.type, "Derrota", 0, 0)
            }
        }
    }

    fun recoverAfterDefeat() {
        heroHp    = heroMaxHP
        isDefeat  = false
        monsterHp = currentMonster.maxHp
        viewModelScope.launch {
            repository.saveUserData(
                heroLevel = heroLevel,
                heroXp    = heroXp,
                heroGold  = heroGold,
                heroHp    = heroMaxHP,
                totalXp   = heroXp + (heroLevel - 1) * 100,
                heroMaxHP = heroMaxHP,
            )
        }
    }

    fun continueAfterVictory() {
        val gold = if (currentMonster.isBoss) 60 else 20 + (monsterQueue.size % MONSTERS.size) * 10
        val xp   = if (currentMonster.isBoss) 80 else 30 + (monsterQueue.size % MONSTERS.size) * 15
        heroGold += gold
        heroXp   += xp
        heroHp    = heroMaxHP
        totalXp  += xp
        if (heroXp >= 100) {
            heroLevel++;
            heroXp -= 100;
            heroMaxHP += 10}
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
                totalXp   = heroXp + (heroLevel - 1) * 100,
                heroMaxHP = heroMaxHP,
                heroHp    = heroMaxHP,
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