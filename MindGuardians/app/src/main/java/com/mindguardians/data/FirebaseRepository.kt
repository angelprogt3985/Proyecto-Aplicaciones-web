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
}