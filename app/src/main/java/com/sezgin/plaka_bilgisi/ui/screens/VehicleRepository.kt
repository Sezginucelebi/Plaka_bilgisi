package com.sezgin.plaka_bilgisi.ui.screens

import com.google.firebase.firestore.FirebaseFirestore
import com.sezgin.plaka_bilgisi.model.Vehicle

fun addVehicleToFirestore(
    vehicle: Vehicle,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    db.collection("vehicles")
        .whereEqualTo("plate", vehicle.plate)
        .get()
        .addOnSuccessListener { result ->
            if (result.isEmpty) {
                val vehicleMap = mapOf(
                    "plate" to vehicle.plate,
                    "brand" to vehicle.brand,
                    "model" to vehicle.model,
                    "ownerId" to vehicle.ownerId,
                    "ownerName" to vehicle.ownerName
                )

                db.collection("vehicles")
                    .add(vehicleMap)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { exception -> onFailure(exception) }
            } else {
                onFailure(Exception("Bu plaka zaten kayıtlı"))
            }
        }
        .addOnFailureListener { exception -> onFailure(exception) }
}
