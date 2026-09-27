package com.sezgin.plaka_bilgisi.repository

import com.sezgin.plaka_bilgisi.model.Vehicle

/**
 * AuroNova PTS - MongoDB Altyapı Hazırlık Modülü
 * DİKKAT: Bu altyapı şu anda PASİF durumdadır (IS_MONGO_ENABLED = false).
 * Uygulama varsayılan olarak Firebase/Firestore kullanmaya devam eder.
 */
@Suppress("UNUSED_PARAMETER", "Unused")
object MongoDBRepository {

    const val IS_MONGO_ENABLED: Boolean = false // Devre dışı
    const val MONGO_BASE_URL: String = "http://10.0.2.2:3000/api/mongo"

    fun syncVehicleToMongo(vehicle: Vehicle, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        if (!IS_MONGO_ENABLED) {
            return
        }
    }

    fun getVehiclesFromMongo(ownerId: String, onSuccess: (List<Vehicle>) -> Unit, onFailure: (Exception) -> Unit) {
        if (!IS_MONGO_ENABLED) {
            return
        }
    }
}
