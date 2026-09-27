/**
 * AuroNova PTS - MongoDB Entegrasyon Modülü
 * DİKKAT: Bu modül devre dışıdır (isMongoEnabled = false).
 * Devreye almak için 'isMongoEnabled = true' yapıp 'MONGO_URI' tanımını güncelleyiniz.
 */

const isMongoEnabled = false; // Devre dışı (Firebase aktif)
const MONGO_URI = process.env.MONGO_URI || "mongodb://localhost:27017/plaka_bilgisi";

let dbInstance = null;

async function initMongoDB() {
    if (!isMongoEnabled) {
        console.log("[MongoDB] Devre dışı. Sistem Firebase üzerinden çalışıyor.");
        return null;
    }

    try {
        const { MongoClient } = require('mongodb');
        const client = new MongoClient(MONGO_URI);
        await client.connect();
        dbInstance = client.db();
        console.log("[MongoDB] Bağlantı başarılı ✅");
        return dbInstance;
    } catch (error) {
        console.error("[MongoDB] Bağlantı hatası ❌:", error.message);
        return null;
    }
}

// MongoDB Veri Modelleri & Koleksiyon Şemaları
const MongoSchemas = {
    vehicles: {
        plate: String,      // Plaka (örn: 34EBY457)
        ownerName: String,  // Araç Sahibi
        brand: String,      // Marka
        block: String,      // Blok
        apartment: String,  // Daire
        floor: String,      // Kat
        phone: String,      // Dahili / Telefon
        ownerId: String,    // Bağlı Olduğu Firma ID
        recordedBy: String, // Kaydı Yapan Kullanıcı/Terminal
        createdAt: Date     // Kayıt Tarihi
    },
    users: {
        uid: String,
        name: String,
        email: String,
        role: String,       // admin, corporate, user
        firmCode: String,
        isPremium: Boolean,
        enabledFields: Array,
        parentId: String
    },
    terminals: {
        ownerId: String,
        deviceId: String,
        registerDate: Date
    }
};

module.exports = {
    isMongoEnabled,
    initMongoDB,
    MongoSchemas
};
