package com.sezgin.plaka_bilgisi.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sezgin.plaka_bilgisi.model.Vehicle

fun addVehicleToFirestore(
    vehicle: Vehicle,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    
    val vehicleMap = hashMapOf(
        "plate" to vehicle.plate,
        "ownerName" to vehicle.ownerName,
        "block" to vehicle.block,
        "apartment" to vehicle.apartment,
        "floor" to vehicle.floor,
        "phone" to vehicle.phone,
        "ownerId" to vehicle.ownerId,
        "recordedBy" to vehicle.recordedBy,
        "brand" to vehicle.brand,
        "model" to vehicle.model
    )

    db.collection("vehicles")
        .whereEqualTo("plate", vehicle.plate)
        .whereEqualTo("ownerId", vehicle.ownerId) // Sadece aynı firma içinde mükerrer kontrolü
        .get()
        .addOnSuccessListener { result ->
            if (result.isEmpty) {
                db.collection("vehicles").add(vehicleMap)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            } else {
                onFailure(Exception("Bu plaka bu firmada zaten kayıtlı!"))
            }
        }
        .addOnFailureListener { onFailure(it) }
}

fun updateVehicleInFirestore(
    vehicleId: String,
    vehicle: Vehicle,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val vehicleMap = hashMapOf(
        "plate" to vehicle.plate,
        "ownerName" to vehicle.ownerName,
        "block" to vehicle.block,
        "apartment" to vehicle.apartment,
        "floor" to vehicle.floor,
        "phone" to vehicle.phone,
        "brand" to vehicle.brand,
        "model" to vehicle.model
    )

    db.collection("vehicles").document(vehicleId)
        .update(vehicleMap as Map<String, Any>)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
}

fun getVehiclesForUser(userId: String, onSuccess: (List<Vehicle>) -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("vehicles")
        .whereEqualTo("ownerId", userId)
        .get()
        .addOnSuccessListener { result ->
            val vehicles = result.mapNotNull { doc -> 
                doc.toObject(Vehicle::class.java).apply { id = doc.id } 
            }
            onSuccess(vehicles)
        }
        .addOnFailureListener { onFailure(it) }
}

fun getAllVehicles(onSuccess: (List<Vehicle>) -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("vehicles").get()
        .addOnSuccessListener { result ->
            val vehicles = result.mapNotNull { doc -> 
                doc.toObject(Vehicle::class.java).apply { id = doc.id } 
            }
            onSuccess(vehicles)
        }
        .addOnFailureListener { onFailure(it) }
}

fun searchVehicleByPlate(plate: String, onSuccess: (Vehicle?) -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("vehicles")
        .whereEqualTo("plate", plate.uppercase().trim())
        .get()
        .addOnSuccessListener { result ->
            if (!result.isEmpty) {
                val doc = result.documents[0]
                onSuccess(doc.toObject(Vehicle::class.java)?.apply { id = doc.id })
            } else { onSuccess(null) }
        }
        .addOnFailureListener { exception -> onFailure(exception) }
}

fun checkIfAdmin(userId: String, onResult: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users").document(userId).get()
        .addOnSuccessListener { document ->
            val role = document.getString("role")
            onResult(role == "admin")
        }
        .addOnFailureListener { onResult(false) }
}

fun getUserName(userId: String, onResult: (String) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users").document(userId).get()
        .addOnSuccessListener { document ->
            val name = document.getString("name") ?: "Bilinmeyen Kullanıcı"
            onResult(name)
        }
        .addOnFailureListener { onResult("Hata: Alınamadı") }
}

// Cihaz ve Terminal Yönetimi
fun checkTerminalLimit(userId: String, deviceId: String, onResult: (Boolean, String?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users").document(userId).get()
        .addOnSuccessListener { userDoc ->
            val isPremium = userDoc.getBoolean("isPremium") ?: false
            val maxTerminals = if (isPremium) 100 else 1 // Ücretli ise 100, değilse 1 hak
            
            db.collection("terminals")
                .whereEqualTo("ownerId", userId)
                .get()
                .addOnSuccessListener { terminals ->
                    val registeredDevices = terminals.documents.mapNotNull { it.getString("deviceId") }
                    
                    if (registeredDevices.contains(deviceId)) {
                        onResult(true, null) // Cihaz zaten kayıtlı
                    } else if (registeredDevices.size < maxTerminals) {
                        // Yeni cihaz kaydedilebilir
                        val newTerminal = hashMapOf(
                            "ownerId" to userId,
                            "deviceId" to deviceId,
                            "registerDate" to com.google.firebase.Timestamp.now()
                        )
                        db.collection("terminals").add(newTerminal)
                            .addOnSuccessListener { onResult(true, null) }
                            .addOnFailureListener { onResult(false, "Cihaz kaydı başarısız.") }
                    } else {
                        onResult(false, "Terminal sınırına ulaşıldı. Lütfen Premium hesaba geçin.")
                    }
                }
        }
        .addOnFailureListener { onResult(false, "Kullanıcı bilgisi alınamadı.") }
}

// Admin için kullanıcı profili ve yönetimi
data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "user",
    val isPremium: Boolean = false,
    val enabledFields: List<String> = emptyList()
)

fun getAllUsers(onSuccess: (List<UserProfile>) -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users").get()
        .addOnSuccessListener { result ->
            val users = result.mapNotNull { doc ->
                val profile = doc.toObject(UserProfile::class.java)
                profile.copy(uid = doc.id)
            }
            onSuccess(users)
        }
        .addOnFailureListener { onFailure(it) }
}

fun updateUserRole(userId: String, newRole: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users").document(userId)
        .update("role", newRole)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
}

fun updatePremiumStatus(userId: String, isPremium: Boolean, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users").document(userId)
        .update("isPremium", isPremium)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
}
