package com.mindguardians.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    // ── Verifica si el displayName ya está en uso por otro usuario ────────────
    suspend fun isDisplayNameTaken(displayName: String): Boolean {
        val snapshot = db.collection("users")
            .whereEqualTo("displayName", displayName)
            .limit(1)
            .get()
            .await()
        return !snapshot.isEmpty
    }

    // ── Crear perfil al registrarse (solo si no existe aún) ───────────────────
    suspend fun createUserProfile(displayName: String) {
        val uid   = currentUserId ?: return
        val email = auth.currentUser?.email ?: ""

        val exists = db.collection("users").document(uid).get().await().exists()
        if (exists) return // ya tiene perfil, no sobreescribir

        val data = mapOf(
            "displayName" to displayName,
            "email"       to email,
            "heroLevel"   to 1,
            "heroXp"      to 0,
            "heroGold"    to 0,
            "heroHp"      to 100,
            "totalXp"     to 0,
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
    ) {
        val uid = currentUserId ?: return
        val data = mapOf(
            "heroLevel" to heroLevel,
            "heroXp"    to heroXp,
            "heroGold"  to heroGold,
            "heroHp"    to heroHp,
            "totalXp"   to totalXp,
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

    suspend fun loadInventory(): List<String> {
        val uid = currentUserId ?: return emptyList()
        val snapshot = db.collection("users").document(uid)
            .collection("inventory")
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.getString("id") }
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
                "id"       to itemId,
                "name"     to itemName,
                "stat"     to itemStat,
                "emoji"    to emoji,
                "price"    to amount,
                "purchasedAt" to com.google.firebase.Timestamp.now(),
            ))
            .await()

        return true
    }
}