package com.mindguardians.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

// ── Modelo compartido de ítem de tienda ──────────────────────────────────────
data class ShopItemData(
    val id:    String,
    val emoji: String,
    val name:  String,
    val stat:  String,
    val price: Int,
    val bonusHp:    Int = 0,
    val bonusPower: Int = 0,
)

// ── Modelo de ítem de inventario (con fecha de compra) ───────────────────────
data class InventoryItemData(
    val docId:       String,
    val id:          String,
    val emoji:       String,
    val name:        String,
    val stat:        String,
    val price:       Int,
    val bonusHp:     Int,
    val bonusPower:  Int,
    val purchasedAt: com.google.firebase.Timestamp?,
)


class FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    // Carga el catalogo de la tienda desde firebase
    suspend fun loadShopCatalog(): List<ShopItemData> {
        val snapshot = db.collection("shop_catalog").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val id    = doc.id
            val name  = doc.getString("name")  ?: return@mapNotNull null
            val stat  = doc.getString("stat")  ?: doc.getString("description") ?: ""
            val emoji = doc.getString("emoji") ?: ""
            val price = (doc.getLong("price")  ?: 0).toInt()
            val bonusHp    = (doc.getLong("bonusHp")    ?: 0).toInt()
            val bonusPower = (doc.getLong("bonusPower") ?: 0).toInt()
            ShopItemData(id, emoji, name, stat, price, bonusHp, bonusPower)
        }
    }

    suspend fun isDisplayNameTaken(displayName: String): Boolean {
        val snapshot = db.collection("users")
            .whereEqualTo("displayName", displayName)
            .limit(1)
            .get()
            .await()
        return !snapshot.isEmpty
    }

    suspend fun createUserProfile(displayName: String) {
        val uid   = currentUserId ?: return
        val email = auth.currentUser?.email ?: ""

        val exists = db.collection("users").document(uid).get().await().exists()
        if (exists) return

        val data = mapOf(
            "displayName" to displayName,
            "email"       to email,
            "heroLevel"   to 1,
            "heroXp"      to 0,
            "heroGold"    to 0,
            "heroHp"      to 100,
            "totalXp"     to 0,
            "heroMaxHP"   to 100,
        )
        db.collection("users").document(uid).set(data).await()
    }

    suspend fun loadUserData(): Map<String, Any>? {
        val uid = currentUserId ?: return null
        val snapshot = db.collection("users").document(uid).get().await()
        return if (snapshot.exists()) snapshot.data else null
    }

    suspend fun saveUserData(
        heroLevel:   Int,
        heroXp:      Int,
        heroGold:    Int,
        heroHp:      Int,
        totalXp:     Int,
        heroMaxHP:   Int,
        displayName: String? = null,
        heroClass:   String? = null,
    ) {
        val uid = currentUserId ?: return
        val data = mutableMapOf<String, Any>(
            "heroLevel" to heroLevel,
            "heroXp"    to heroXp,
            "heroGold"  to heroGold,
            "heroHp"    to heroHp,
            "totalXp"   to totalXp,
            "heroMaxHP" to heroMaxHP,
        )
        // Solo incluir si se pasaron explícitamente
        if (displayName != null) data["displayName"] = displayName
        if (heroClass   != null) data["heroClass"]   = heroClass
        db.collection("users").document(uid)
            .set(data, SetOptions.merge())
            .await()
    }

    suspend fun saveBattle(
        habitType:  String,
        result:     String,
        goldEarned: Int,
        xpEarned:   Int,
    ) {
        val uid = currentUserId ?: return
        val battle = mapOf(
            "date"       to com.google.firebase.Timestamp.now(),
            "habitType"  to habitType,
            "result"     to result,
            "goldEarned" to goldEarned,
            "xpEarned"   to xpEarned,
        )
        db.collection("users").document(uid)
            .collection("battles")
            .add(battle)
            .await()
    }

    suspend fun loadRanking(): List<Map<String, Any>> {
        val snapshot = db.collection("users")
            .orderBy("totalXp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.data }
    }

    suspend fun loadBattles(): List<BattleRecord> {
        val uid = currentUserId ?: return emptyList()
        val snapshot = db.collection("users").document(uid)
            .collection("battles")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            val ts = doc.getTimestamp("date") ?: return@mapNotNull null
            val cal = java.util.Calendar.getInstance().apply { time = ts.toDate() }
            val dateStr = "%04d-%02d-%02d".format(
                cal.get(java.util.Calendar.YEAR),
                cal.get(java.util.Calendar.MONTH) + 1,
                cal.get(java.util.Calendar.DAY_OF_MONTH),
            )
            BattleRecord(
                id         = doc.id,
                date       = dateStr,
                habitType  = doc.getString("habitType")          ?: "Combate",
                result     = doc.getString("result")             ?: "Victoria",
                goldEarned = (doc.getLong("goldEarned") ?: 0).toInt(),
                xpEarned   = (doc.getLong("xpEarned")   ?: 0).toInt(),
            )
        }
    }

    // ── Solo IDs (para filtrar ítems ya comprados en la tienda) ──────────────
    suspend fun loadInventory(): List<String> {
        val uid = currentUserId ?: return emptyList()
        val snapshot = db.collection("users").document(uid)
            .collection("inventory")
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.getString("id") }
    }

    // ── Inventario completo con metadatos (pantalla Inventario) ───────────────
    suspend fun loadFullInventory(): List<InventoryItemData> {
        val uid = currentUserId ?: return emptyList()
        val snapshot = db.collection("users").document(uid)
            .collection("inventory")
            .orderBy("purchasedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            val id    = doc.getString("id")    ?: return@mapNotNull null
            val name  = doc.getString("name")  ?: return@mapNotNull null
            val stat  = doc.getString("stat")  ?: return@mapNotNull null
            val emoji = doc.getString("emoji") ?: "📦"
            val price = (doc.getLong("price") ?: 0).toInt()
            val bonusHp    = (doc.getLong("bonusHp")    ?: 0).toInt()
            val bonusPower = (doc.getLong("bonusPower") ?: 0).toInt()
            val ts    = doc.getTimestamp("purchasedAt")
            InventoryItemData(doc.id, id, emoji, name, stat, price, bonusHp, bonusPower, ts)
        }
    }

    suspend fun spendGold(
        amount:   Int,
        itemId:   String,
        itemName: String,
        itemStat: String,
        emoji:    String,
        bonusHp:    Int = 0,
        bonusPower: Int = 0,
    ): Boolean {
        val uid = currentUserId ?: return false
        val userRef  = db.collection("users").document(uid)
        val snapshot = userRef.get().await()
        if (!snapshot.exists()) return false

        val currentGold = (snapshot.getLong("heroGold") ?: 0).toInt()
        if (currentGold < amount) return false

        userRef.update("heroGold", currentGold - amount).await()

        db.collection("users").document(uid)
            .collection("inventory")
            .add(mapOf(
                "id"          to itemId,
                "name"        to itemName,
                "stat"        to itemStat,
                "emoji"       to emoji,
                "price"       to amount,
                "bonusHp"     to bonusHp,
                "bonusPower"  to bonusPower,
                "purchasedAt" to com.google.firebase.Timestamp.now(),
            ))
            .await()

        return true
    }
}