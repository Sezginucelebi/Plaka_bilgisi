const { app, BrowserWindow, ipcMain, dialog } = require('electron');
const path = require('path');
const fs = require('fs');
const admin = require('firebase-admin');
const axios = require('axios');
const QRCode = require('qrcode');

let db = null;
try {
    const keyPaths = [
        path.join(__dirname, 'serviceAccountKey.json'),
        path.join(__dirname, '..', 'serviceAccountKey.json')
    ];

    let keyPath = null;
    for (const p of keyPaths) {
        if (fs.existsSync(p)) {
            keyPath = p;
            break;
        }
    }

    if (keyPath) {
        const serviceAccount = require(keyPath);
        if (!admin.apps.length) admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });
        db = admin.firestore();
    } else {
        console.error("Critical: serviceAccountKey.json not found in dashboard-app or root!");
    }
} catch (e) { console.error("Firebase SDK Error:", e); }

const API_KEY = "AIzaSyAWh-HsV4kR5gyAHR6Jy3xrFXG2XG_Gto0";

function createWindow() {
    const win = new BrowserWindow({
        width: 1450,
        height: 950,
        title: "AuroNova Enterprise PTS",
        backgroundColor: '#0d1117',
        icon: path.join(__dirname, 'icon.png'),
        webPreferences: { nodeIntegration: true, contextIsolation: false }
    });
    win.loadFile('index.html');
    win.webContents.openDevTools();
    win.webContents.on('console-message', (event, level, message, line, sourceId) => {
        if(level >= 2) console.error(`[RENDERER ERROR] Line ${line}: ${message}`);
    });
}
app.whenReady().then(createWindow);

// --- LİVE VERİ TAKİBİ (onSnapshot) ---
let vehicleListener = null;
let terminalListener = null;
let staffListener = null;

ipcMain.on('start-live-updates', (event, ownerId) => {
    if(vehicleListener) vehicleListener();
    if(terminalListener) terminalListener();
    if(staffListener) staffListener();

    vehicleListener = db.collection('vehicles').where('ownerId', '==', ownerId).onSnapshot(snap => {
        event.sender.send('live-vehicles', snap.docs.map(doc => ({ id: doc.id, ...doc.data() })));
    });

    terminalListener = db.collection('terminals').where('ownerId', '==', ownerId).onSnapshot(snap => {
        event.sender.send('live-terminals', snap.docs.map(doc => ({ id: doc.id, ...doc.data() })));
    });

    // 👥 Personelleri de canlı takip et
    staffListener = db.collection('users').where('parentId', '==', ownerId).onSnapshot(snap => {
        event.sender.send('live-staff', snap.docs.map(doc => ({ id: doc.id, ...doc.data() })));
    });
});

ipcMain.on('stop-live-updates', () => {
    if(vehicleListener) vehicleListener();
    if(terminalListener) terminalListener();
    if(staffListener) staffListener();
});

// --- AUTH ---
ipcMain.handle('login-user', async (event, { email, password }) => {
    try {
        if (!db) return { success: false, error: "Veritabanı bağlantısı kurulamadı! (serviceAccountKey.json eksik)" };

        const url = `https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${API_KEY}`;
        const res = await axios.post(url, { email, password, returnSecureToken: true });

        if (res.status === 200) {
            const uid = res.data.localId;
            const userDoc = await db.collection('users').doc(uid).get();

            if (userDoc.exists) {
                const userData = userDoc.data();
                // 🔐 Yetkili Roller: sales, corporate, admin
                if (userData.role === 'sales' || userData.role === 'corporate' || userData.role === 'admin') {
                    return { success: true, user: { uid, ...userData } };
                } else {
                    return { success: false, error: "Bu panele erişim yetkiniz yok! (Rol: " + (userData.role || 'user') + ")" };
                }
            }
        }
        return { success: false, error: "Giriş bilgileri hatalı veya kullanıcı kaydı bulunamadı!" };
    } catch (e) {
        console.error("Login Error:", e);
        if (e.response && e.response.data && e.response.data.error) {
            const err = e.response.data.error.message;
            if (err === "INVALID_LOGIN_CREDENTIALS" || err === "INVALID_PASSWORD") return { success: false, error: "Hatalı şifre veya e-posta!" };
            if (err === "USER_NOT_FOUND") return { success: false, error: "Kullanıcı bulunamadı!" };
        }
        return { success: false, error: "Sunucu bağlantı hatası: " + (e.message || "Bilinmeyen hata") };
    }
});

// --- VERİ ÇEKME ---
ipcMain.handle('get-firms', async () => {
    if (!db) return [];
    // 🏢 Kurumsal (corporate) ve Yönetici (admin) rollerini getir, ancak sadece ana firmaları (parentId olmayanlar)
    const snap = await db.collection('users').where('role', 'in', ['corporate', 'admin']).get();
    return snap.docs.map(doc => ({ id: doc.id, ...doc.data() })).filter(x => !x.parentId);
});
ipcMain.handle('get-vehicles', async (event, ownerId) => {
    const snap = await db.collection('vehicles').where('ownerId', '==', ownerId).get();
    return snap.docs.map(doc => ({ id: doc.id, ...doc.data() }));
});
ipcMain.handle('get-staff', async (event, parentId) => {
    const snap = await db.collection('users').where('parentId', '==', parentId).get();
    return snap.docs.map(doc => ({ id: doc.id, ...doc.data() }));
});

ipcMain.handle('get-terminals', async (event, ownerId) => {
    const snap = await db.collection('terminals').where('ownerId', '==', ownerId).get();
    return snap.docs.map(doc => ({ id: doc.id, ...doc.data() }));
});

ipcMain.handle('create-pairing-code', async (event, firmId) => {
    try {
        const code = Math.random().toString(36).substring(2, 8).toUpperCase();
        await db.collection('pairings').doc(code).set({
            firmId: firmId,
            isUsed: false,
            createdAt: admin.firestore.FieldValue.serverTimestamp()
        });

        // 📷 QR Kod verisini Base64 olarak üret (External API bağımlılığını kaldır)
        const qrData = `AURONOVA|${code}|${firmId}`;
        const qrBase64 = await QRCode.toDataURL(qrData);

        return { success: true, code: code, qr: qrBase64 };
    } catch (e) {
        console.error("Pairing Code Error:", e);
        return { success: false, error: e.message };
    }
});

ipcMain.handle('delete-terminal', async (event, id) => {
    await db.collection('terminals').doc(id).delete();
    return { success: true };
});

// --- İŞLEMLER (FIXED) ---
ipcMain.handle('update-firm', async (event, { firmId, data }) => {
    try {
        if(data.email) await admin.auth().updateUser(firmId, { email: data.email });
        await db.collection('users').doc(firmId).update(data);
        return { success: true };
    } catch (e) { return { success: false, error: e.message }; }
});

ipcMain.handle('update-vehicle', async (event, { vehicleId, data }) => {
    await db.collection('vehicles').doc(vehicleId).update(data); // .doc olarak düzeltildi
    return { success: true };
});

ipcMain.handle('add-vehicle', async (event, { data }) => {
    try {
        const plate = (data.plate || "").toString().toUpperCase().trim();
        const ownerId = data.ownerId;

        // 🔍 Mükerrer Kontrolü
        const exists = await db.collection('vehicles')
            .where('plate', '==', plate)
            .where('ownerId', '==', ownerId)
            .get();

        if(!exists.empty) return { success: false, error: "Bu plaka zaten kayıtlı!" };

        await db.collection('vehicles').add({
            ...data,
            plate: plate,
            createdAt: admin.firestore.FieldValue.serverTimestamp()
        });
        return { success: true };
    } catch (e) { return { success: false, error: e.message }; }
});

ipcMain.handle('reset-staff-password', async (event, { uid, newPassword }) => {
    try { await admin.auth().updateUser(uid, { password: newPassword }); return { success: true }; }
    catch (e) { return { success: false, error: e.message }; }
});

ipcMain.handle('add-staff', async (event, { parentId, staffData, password }) => {
    try {
        const uRec = await admin.auth().createUser({ email: staffData.email, password });
        const data = {
            ...staffData,
            role: parentId ? 'user' : 'corporate',
            parentId: parentId || null,
            maxTerminals: 5, // Varsayılan limit
            enabledFields: ["plate", "ownerName", "block", "apartment", "phone"],
            createdAt: admin.firestore.FieldValue.serverTimestamp()
        };
        await db.collection('users').doc(uRec.uid).set(data);
        return { success: true };
    } catch (e) { return { success: false, error: e.message }; }
});

ipcMain.handle('delete-staff', async (event, id) => {
    try {
        // 1. Firmaya bağlı araçları sil
        const vehicles = await db.collection('vehicles').where('ownerId', '==', id).get();
        const batch = db.batch();
        vehicles.forEach(doc => batch.delete(doc.ref));

        // 2. Firmaya bağlı personelleri sil
        const staff = await db.collection('users').where('parentId', '==', id).get();
        for (const sDoc of staff.docs) {
            try { await admin.auth().deleteUser(sDoc.id); } catch(e) {}
            batch.delete(sDoc.ref);
        }

        // 3. Firmaya bağlı terminalleri sil
        const terminals = await db.collection('terminals').where('ownerId', '==', id).get();
        terminals.forEach(doc => batch.delete(doc.ref));

        // 4. Firmanın kendisini sil (Auth ve Firestore)
        try { await admin.auth().deleteUser(id); } catch(e) {}
        batch.delete(db.collection('users').doc(id));

        await batch.commit();
        return true;
    } catch(e) {
        console.error("Firma silme hatası:", e);
        return false;
    }
});
ipcMain.handle('delete-staff-single', async (event, id) => {
    try {
        await admin.auth().deleteUser(id);
        await db.collection('users').doc(id).delete();
        return true;
    } catch(e) { return false; }
});

ipcMain.handle('delete-vehicle', async (event, id) => { await db.collection('vehicles').doc(id).delete(); return true; });

// --- EXCEL & IMPORT ---
ipcMain.handle('import-excel', async (event, { firmId }) => {
    try {
        const userDoc = await db.collection('users').doc(firmId).get();
        const userData = userDoc.data();
        const isPremium = userData.isPremium || false;

        const { filePaths } = await dialog.showOpenDialog({ properties: ['openFile'], filters: [{ name: 'Excel', extensions: ['xlsx', 'xls'] }] });
        if(!filePaths || !filePaths[0]) return { success: false };

        const XLSX = require('xlsx');
        const workbook = XLSX.readFile(filePaths[0]);
        const sheet = workbook.Sheets[workbook.SheetNames[0]];
        const data = XLSX.utils.sheet_to_json(sheet);

        // 🛡️ LİMİT KONTROLÜ
        if (!isPremium) {
            const currentVehicles = await db.collection('vehicles').where('ownerId', '==', firmId).get();
            if (currentVehicles.size + data.length > 200) {
                return { success: false, error: `Standart paket limiti aşıldı! En fazla 200 araç kaydedebilirsiniz. Mevcut: ${currentVehicles.size}, Eklenen: ${data.length}` };
            }
        }

        // 🛡️ MÜKERRER KONTROLÜ (Mevcut plakaları çek)
        const currentVehiclesSnap = await db.collection('vehicles').where('ownerId', '==', firmId).get();
        const existingPlates = new Set(currentVehiclesSnap.docs.map(doc => doc.data().plate.toUpperCase()));

        const batch = db.batch();
        let addedCount = 0;
        let skippedCount = 0;

        data.forEach(item => {
            const plate = (item.PLAKA || item.plate || "").toString().toUpperCase().trim();
            if (plate && !existingPlates.has(plate)) {
                const ref = db.collection('vehicles').doc();
                batch.set(ref, {
                    plate: plate,
                    ownerName: item.SAHIP || item.ownerName || item.OWNERNAME || "",
                    block: item.BLOK || item.block || item.BLOCK || "",
                    apartment: item.DAIRE || item.apartment || item.APARTMENT || "",
                    floor: item.KAT || item.floor || item.FLOOR || "",
                    phone: item.TELEFON || item.phone || item.PHONE || "",
                    dahili: item.DAHILI || item.dahili || item.DAHİLİ || "",
                    brand: item.MARKA || item.brand || item.BRAND || "",
                    model: item.MODEL || item.model || "",
                    ownerId: firmId,
                    recordedBy: "Excel Import",
                    createdAt: admin.firestore.FieldValue.serverTimestamp()
                });
                existingPlates.add(plate);
                addedCount++;
            } else {
                skippedCount++;
            }
        });

        if(addedCount > 0) await batch.commit();
        return { success: true, count: addedCount, skipped: skippedCount };
    } catch (e) { console.error(e); return { success: false, error: e.message }; }
});

ipcMain.handle('export-to-excel', async (event, { collectionName, fileName, firmId }) => {
    try {
        let query = db.collection(collectionName);
        if(firmId) query = query.where('ownerId', '==', firmId);
        const snap = await query.get();
        const data = snap.docs.map(doc => {
            const raw = doc.data();
            // Veriyi Excel için temizle ve isimlendir
            return {
                "PLAKA": raw.plate || "",
                "ARAÇ SAHİBİ": raw.ownerName || "",
                "BLOK": raw.block || "",
                "KAT": raw.floor || "",
                "DAİRE": raw.apartment || "",
                "TELEFON": raw.phone || "",
                "DAHİLİ": raw.dahili || "", // 📞 Dahili Alanı Eklendi
                "MARKA": raw.brand || "",
                "MODEL": raw.model || "",
                "KAYIT TARİHİ": raw.createdAt ? raw.createdAt.toDate().toLocaleString('tr-TR') : ""
            };
        });

        if(!data.length) return { success: false, error: "Kayıtlı veri bulunamadı." };

        const { filePath } = await dialog.showSaveDialog({
            title: 'Excel Olarak Kaydet',
            defaultPath: path.join(app.getPath('downloads'), `${fileName}.xlsx`),
            filters: [{ name: 'Excel Workbook', extensions: ['xlsx'] }]
        });

        if(filePath) {
            const XLSX = require('xlsx');
            const worksheet = XLSX.utils.json_to_sheet(data);
            const workbook = XLSX.utils.book_new();
            XLSX.utils.book_append_sheet(workbook, worksheet, "Araç Listesi");
            XLSX.writeFile(workbook, filePath);
            return { success: true };
        }
    } catch (e) {
        console.error("Excel Export Error:", e);
        return { success: false, error: e.message };
    }
    return { success: false };
});

ipcMain.handle('get-global-stats', async () => {
    const f = await db.collection('users').where('role', '==', 'corporate').get();
    const v = await db.collection('vehicles').get();
    return { totalFirms: f.size, totalPlates: v.size };
});

ipcMain.handle('get-market-config', async () => {
    const doc = await db.collection('configs').doc('market').get();
    if(doc.exists) return doc.data();
    return {
        std: { price: "0 TL", features: "1 Terminal Desteği\n1 Kullanıcı Ekranı\n1 Cihaz Kaydı\n100 Araç Kaydı\nManuel Plaka Sorgulama" },
        sml: { price: "4 $ + KDV", features: "1 Terminal Desteği\n1 Kullanıcı Ekranı\n2 Cihaz Kaydı\n300 Araç Kaydı\nHatalı Park Bildirim\nExcel İçe/Dışa Aktarma\n1 GB Bulut Hafıza" },
        prm: { price: "10 $ + KDV", features: "1 Terminal Desteği\n2 Kullanıcı Ekranı\n5 Cihaz Kaydı\n5000 Araç Kaydı\nHatalı Park Bildirim Sism.\nExcel İçe/Dışa Aktarma\n10 GB Bulut Hafıza\nYAPAY ZEKA ANALİZİ" }
    };
});

ipcMain.handle('update-market-config', async (event, data) => {
    await db.collection('configs').doc('market').set(data);
    return { success: true };
});
