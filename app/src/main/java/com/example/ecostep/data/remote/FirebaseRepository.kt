package com.example.ecostep.data.remote

import com.example.ecostep.data.local.DailyLog
import com.example.ecostep.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

interface FirebaseRepository {
    suspend fun getCurrentUser(): User?
    suspend fun createUser(user: User): Result<User>
    suspend fun getUserByQrCode(qrCode: String): User?
    suspend fun saveDailyLog(userId: String, log: DailyLog): Result<Unit>
    fun getDailyLogs(userId: String): Flow<List<DailyLog>>
    suspend fun updateDailyLog(userId: String, log: DailyLog): Result<Unit>
    suspend fun deleteDailyLog(userId: String, logId: Long): Result<Unit>
    suspend fun getDailyLogById(userId: String, logId: Long): DailyLog?
}

class FirebaseRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : FirebaseRepository {

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return try {
            val doc = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()
            
            doc.toObject(User::class.java)?.copy(id = firebaseUser.uid)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createUser(user: User): Result<User> {
        return try {
            val userRef = firestore.collection("users").document(user.id)
            userRef.set(user).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserByQrCode(qrCode: String): User? {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("qrCode", qrCode)
                .limit(1)
                .get()
                .await()
            
            if (querySnapshot.isEmpty) return null
            
            val doc = querySnapshot.documents.first()
            doc.toObject(User::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveDailyLog(userId: String, log: DailyLog): Result<Unit> {
        return try {
            val logMap = mapOf(
                "id" to log.id,
                "date" to log.date,
                "transportType" to log.transportType,
                "transportDistanceKm" to log.transportDistanceKm,
                "meatPortions" to log.meatPortions,
                "veggiesPortions" to log.veggiesPortions,
                "dairyPortions" to log.dairyPortions,
                "junkFoodPortions" to log.junkFoodPortions,
                "electricityKwh" to log.electricityKwh,
                "waterLiters" to log.waterLiters,
                "wasteBags" to log.wasteBags,
                "recycledToday" to log.recycledToday,
                "steps" to log.steps,
                "ecoScore" to log.ecoScore
            )
            
            firestore.collection("users")
                .document(userId)
                .collection("dailyLogs")
                .document(log.id.toString())
                .set(logMap)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getDailyLogs(userId: String): Flow<List<DailyLog>> = callbackFlow {
        val listenerRegistration = firestore.collection("users")
            .document(userId)
            .collection("dailyLogs")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                
                val logs = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        DailyLog(
                            id = (doc.get("id") as? Number)?.toLong() ?: 0L,
                            date = doc.getString("date") ?: "",
                            transportType = doc.getString("transportType") ?: "",
                            transportDistanceKm = (doc.get("transportDistanceKm") as? Number)?.toDouble() ?: 0.0,
                            meatPortions = (doc.get("meatPortions") as? Number)?.toInt() ?: 0,
                            veggiesPortions = (doc.get("veggiesPortions") as? Number)?.toInt() ?: 0,
                            dairyPortions = (doc.get("dairyPortions") as? Number)?.toInt() ?: 0,
                            junkFoodPortions = (doc.get("junkFoodPortions") as? Number)?.toInt() ?: 0,
                            electricityKwh = (doc.get("electricityKwh") as? Number)?.toDouble() ?: 0.0,
                            waterLiters = (doc.get("waterLiters") as? Number)?.toDouble() ?: 0.0,
                            wasteBags = (doc.get("wasteBags") as? Number)?.toInt() ?: 0,
                            recycledToday = doc.getBoolean("recycledToday") ?: false,
                            steps = (doc.get("steps") as? Number)?.toInt() ?: 0,
                            ecoScore = (doc.get("ecoScore") as? Number)?.toDouble() ?: 0.0
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(logs)
            }
        
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun updateDailyLog(userId: String, log: DailyLog): Result<Unit> {
        return try {
            val logMap = mapOf(
                "id" to log.id,
                "date" to log.date,
                "transportType" to log.transportType,
                "transportDistanceKm" to log.transportDistanceKm,
                "meatPortions" to log.meatPortions,
                "veggiesPortions" to log.veggiesPortions,
                "dairyPortions" to log.dairyPortions,
                "junkFoodPortions" to log.junkFoodPortions,
                "electricityKwh" to log.electricityKwh,
                "waterLiters" to log.waterLiters,
                "wasteBags" to log.wasteBags,
                "recycledToday" to log.recycledToday,
                "steps" to log.steps,
                "ecoScore" to log.ecoScore
            )
            
            firestore.collection("users")
                .document(userId)
                .collection("dailyLogs")
                .document(log.id.toString())
                .update(logMap as Map<String, Any>)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDailyLog(userId: String, logId: Long): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("dailyLogs")
                .document(logId.toString())
                .delete()
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDailyLogById(userId: String, logId: Long): DailyLog? {
        return try {
            val doc = firestore.collection("users")
                .document(userId)
                .collection("dailyLogs")
                .document(logId.toString())
                .get()
                .await()
            
            if (!doc.exists()) return null
            
            DailyLog(
                id = (doc.get("id") as? Number)?.toLong() ?: 0L,
                date = doc.getString("date") ?: "",
                transportType = doc.getString("transportType") ?: "",
                transportDistanceKm = (doc.get("transportDistanceKm") as? Number)?.toDouble() ?: 0.0,
                meatPortions = (doc.get("meatPortions") as? Number)?.toInt() ?: 0,
                veggiesPortions = (doc.get("veggiesPortions") as? Number)?.toInt() ?: 0,
                dairyPortions = (doc.get("dairyPortions") as? Number)?.toInt() ?: 0,
                junkFoodPortions = (doc.get("junkFoodPortions") as? Number)?.toInt() ?: 0,
                electricityKwh = (doc.get("electricityKwh") as? Number)?.toDouble() ?: 0.0,
                waterLiters = (doc.get("waterLiters") as? Number)?.toDouble() ?: 0.0,
                wasteBags = (doc.get("wasteBags") as? Number)?.toInt() ?: 0,
                recycledToday = doc.getBoolean("recycledToday") ?: false,
                steps = (doc.get("steps") as? Number)?.toInt() ?: 0,
                ecoScore = (doc.get("ecoScore") as? Number)?.toDouble() ?: 0.0
            )
        } catch (e: Exception) {
            null
        }
    }
}

