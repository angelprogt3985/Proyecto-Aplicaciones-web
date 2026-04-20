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
)

// ── Modelo de ítem de inventario (con fecha de compra) ───────────────────────
data class InventoryItemData(
    val docId:       String,
    val id:          String,
    val emoji:       String,
    val name:        String,
    val stat:        String,
    val price:       Int,
    val purchasedAt: com.google.firebase.Timestamp?,
)


class FirebaseRepository {

    // ── Catálogo canónico — FUENTE ÚNICA DE VERDAD para app y web ────────────
    val catalogItems: List<ShopItemData> = listOf(
        ShopItemData("shop_01", "🗡️", "Espada del Amanecer", "+10% Daño Agua",  120),
        ShopItemData("shop_02", "🛡️", "Escudo Estelar",      "+15 HP Máx.",     120),
        ShopItemData("shop_03", "🪖", "Casco de Claridad",   "+20% Daño Mente", 180),
        ShopItemData("shop_04", "👟", "Botas del Cosmos",    "+15% Postura",    150),
        ShopItemData("shop_05", "💎", "Amuleto Galáctico",   "+5% Todo daño",   250),
        ShopItemData("shop_06", "🔮", "Orbe del Oráculo",    "+2x bonif. IA",   300),
    )

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    val currentUserId: String?
        get() = auth.currentUser?.uid

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
        heroLevel: Int,
        heroXp:    Int,
        heroGold:  Int,
        heroHp:    Int,
        totalXp:   Int,
        heroMaxHP: Int,
    ) {
        val uid = currentUserId ?: return
        val data = mapOf(
            "heroLevel" to heroLevel,
            "heroXp"    to heroXp,
            "heroGold"  to heroGold,
            "heroHp"    to heroHp,
            "totalXp"   to totalXp,
            "heroMaxHP" to heroMaxHP,
        )
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
            val ts    = doc.getTimestamp("purchasedAt")
            InventoryItemData(doc.id, id, emoji, name, stat, price, ts)
        }
    }

    suspend fun spendGold(
        amount:   Int,
        itemId:   String,
        itemName: String,
        itemStat: String,
        emoji:    String,
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
                "purchasedAt" to com.google.firebase.Timestamp.now(),
            ))
            .await()

        return true
    }
}