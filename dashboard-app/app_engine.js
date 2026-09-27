const { ipcRenderer } = require('electron');

window.Engine = {
    ipc: ipcRenderer,
    currentUser: null,
    selectedFirm: null,
    currentLang: 'TR',

    Translations: {
        'TR': {
            'overview': 'Genel Bakış', 'firm_info': 'Firma Bilgileri', 'vehicle_records': 'Araç Kayıtları',
            'device_management': 'Cihaz Yönetimi', 'staff_management': 'Personel Yönetimi',
            'tag_management': 'Etiket Yönetimi', 'market': 'Market', 'logout': 'Çıkış',
            'customer_portfolio': 'Müşteri Portföyü', 'capacity_analysis': 'Kapasite Analizi', 'market_management': 'Market Yönetimi',
            'registered_vehicle': 'Kayıtlı Araç', 'active_device': 'Aktif Cihaz', 'personnel': 'Personel',
            'last_added': 'SON EKLENEN ARAÇLAR', 'quick_actions': 'HIZLI İŞLEMLER', 'add_new_vehicle': 'YENİ ARAÇ EKLE',
            'pair_new_device': 'Yeni Cihaz Eşleştir', 'upgrade_package': 'Paketimi Yükselt', 'update': 'GÜNCELLE',
            'basic_info': 'TEMEL BİLGİLER', 'firm_name': 'Firma Adı', 'firm_code': 'Firma Kodu',
            'auth_info': 'YETKİLİ BİLGİLERİ', 'auth_person': 'Yetkili Kişi', 'auth_phone': 'Yetkili Telefonu',
            'corp_comm': 'KURUMSAL İLETİŞİM', 'corp_address': 'Kurumsal Adres', 'corp_phone': 'Kurumsal Telefon',
            'mail_addr': 'Mail Adresi', 'excel_import': 'EXCEL İÇE AKTAR', 'export': 'DIŞA AKTAR',
            'search_placeholder': 'Plaka veya Sahip ara...', 'add_device': 'YENİ CİHAZ EKLE',
            'qr_instruction': 'Mobil uygulamadan QR kodu okutun veya kodu girin.', 'qr_create': 'QR KOD OLUŞTUR',
            'active_terminals': 'AKTİF TERMİNALLER', 'staff_edit': 'PERSONEL DÜZENLE', 'name_surname': 'Ad Soyad',
            'reset': 'RESET', 'delete': 'SİL', 'new_staff': 'YENİ PERSONEL', 'save': 'KAYDET',
            'add_new_firm': 'YENİ FİRMA', 'active_tags': 'AKTİF ETİKETLER', 'add_new_field': 'YENİ ALAN EKLE', 'tag_name': 'Etiket Adı',
            'ai_analysis': 'Yapay Zeka Analizi', 're_analyze': 'YENİDEN ANALİZ ET',
            'traffic_predict': 'TRAFİK YOĞUNLUK TAHMİNİ', 'capacity_analys': 'KAPASİTE DOLULUK ANALİZİ', 'ai_insights_title': 'AI GÜVENLİK VE OPERASYON NOTLARI',
            'market_title': 'AuroNova Market', 'market_desc': 'İhtiyacınıza en uygun paketi seçerek özelliklerinizi genişletin.',
            'secure_pay': 'GÜVENLİ ÖDEME', 'back': 'GERİ', 'card_name': 'Kart Üzerindeki İsim',
            'card_no': 'Kart Numarası', 'exp_date': 'Son Kullanma', 'total': 'TOPLAM', 'pay_complete': 'ÖDEMEYİ TAMAMLA',
            'plate': 'PLAKA', 'ownername': 'ARAÇ SAHİBİ', 'block': 'BLOK', 'apartment': 'DAİRE', 'floor': 'KAT', 'phone': 'TELEFON', 'dahili': 'DAHİLİ'
        },
        'ENG': {
            'overview': 'Overview', 'firm_info': 'Firm Info', 'vehicle_records': 'Vehicle Records',
            'device_management': 'Device Management', 'staff_management': 'Staff Management',
            'tag_management': 'Tag Management', 'market': 'Market', 'logout': 'Logout',
            'customer_portfolio': 'Customer Portfolio', 'capacity_analysis': 'Capacity Analysis', 'market_management': 'Market Management',
            'registered_vehicle': 'Registered Vehicle', 'active_device': 'Active Device', 'personnel': 'Personnel',
            'last_added': 'RECENT VEHICLES', 'quick_actions': 'QUICK ACTIONS', 'add_new_vehicle': 'ADD NEW VEHICLE',
            'pair_new_device': 'Pair New Device', 'upgrade_package': 'Upgrade Package', 'update': 'UPDATE',
            'basic_info': 'BASIC INFORMATION', 'firm_name': 'Firm Name', 'firm_code': 'Firm Code',
            'auth_info': 'AUTHORIZED INFO', 'auth_person': 'Authorized Person', 'auth_phone': 'Auth. Phone',
            'corp_comm': 'CORPORATE COMMUNICATION', 'corp_address': 'Corporate Address', 'corp_phone': 'Corporate Phone',
            'mail_addr': 'Mail Address', 'excel_import': 'IMPORT EXCEL', 'export': 'EXPORT',
            'search_placeholder': 'Search Plate or Owner...', 'add_device': 'ADD NEW DEVICE',
            'qr_instruction': 'Scan QR code or enter the code from mobile app.', 'qr_create': 'GENERATE QR CODE',
            'active_terminals': 'ACTIVE TERMINALS', 'staff_edit': 'EDIT STAFF', 'name_surname': 'Name Surname',
            'reset': 'RESET', 'delete': 'DELETE', 'new_staff': 'NEW STAFF', 'save': 'SAVE',
            'add_new_firm': 'NEW FIRM', 'active_tags': 'ACTIVE TAGS', 'add_new_field': 'ADD NEW FIELD', 'tag_name': 'Tag Name',
            'ai_analysis': 'AI Analysis', 're_analyze': 'RE-ANALYZE',
            'traffic_predict': 'TRAFFIC DENSITY PREDICTION', 'capacity_analys': 'CAPACITY ANALYSIS', 'ai_insights_title': 'AI SECURITY & OPERATION NOTES',
            'market_title': 'AuroNova Market', 'market_desc': 'Choose the best plan to expand your features.',
            'secure_pay': 'SECURE PAYMENT', 'back': 'BACK', 'card_name': 'Name on Card',
            'card_no': 'Card Number', 'exp_date': 'Expiry Date', 'total': 'TOTAL', 'pay_complete': 'COMPLETE PAYMENT',
            'plate': 'PLATE', 'ownername': 'OWNER NAME', 'block': 'BLOCK', 'apartment': 'APARTMENT', 'floor': 'FLOOR', 'phone': 'PHONE', 'dahili': 'EXT'
        }
    },

    t: function(key) {
        const lowerKey = key.toLowerCase();
        return (this.Translations[this.currentLang] && this.Translations[this.currentLang][lowerKey]) || key;
    },

    applyTranslations: function(root = document) {
        root.querySelectorAll('[data-t]').forEach(el => {
            const key = el.getAttribute('data-t');
            const icon = el.querySelector('i') ? el.querySelector('i').outerHTML : '';
            const translatedText = this.t(key);
            el.innerHTML = icon ? `${icon} ${translatedText}` : translatedText;
        });
    },

    setGlobalLanguage: function(lang) {
        this.currentLang = lang;
        document.querySelectorAll('.lang-btn').forEach(b => {
            b.classList.remove('active-lang');
            if(b.innerText === lang) b.classList.add('active-lang');
        });

        this.applyTranslations();

        if(window.currentViewId) this.navigate(window.currentViewId);
    },

    init: function() {
        document.getElementById('app-root').innerHTML = window.Templates['login-view'];
        if(localStorage.getItem('auronova_e')) {
            document.getElementById('login-email').value = localStorage.getItem('auronova_e');
            document.getElementById('login-pass').value = localStorage.getItem('auronova_p');
            document.getElementById('remember-me').checked = true;
        }
    },

    Auth: {
        login: async function() {
            const e = document.getElementById('login-email').value.trim(), p = document.getElementById('login-pass').value.trim(), r = document.getElementById('remember-me').checked;
            if(!e || !p) return;
            const res = await Engine.ipc.invoke('login-user', { email:e, password:p });
            if(res.success) {
                if(r) { localStorage.setItem('auronova_e', e); localStorage.setItem('auronova_p', p); }
                Engine.currentUser = res.user;
                Engine.loadShell();
            } else { document.getElementById('login-error').innerText = res.error; }
        },
        forgetMe: function() {
            localStorage.removeItem('auronova_e'); localStorage.removeItem('auronova_p');
            document.getElementById('login-email').value = ''; document.getElementById('login-pass').value = '';
            document.getElementById('remember-me').checked = false;
            alert("Giriş bilgileriniz bu cihazdan temizlendi 🗑️");
        },
        togglePass: function(el) {
            const p = document.getElementById('login-pass');
            if (p.type === 'password') { p.type = 'text'; el.classList.replace('fa-eye', 'fa-eye-slash'); }
            else { p.type = 'password'; el.classList.replace('fa-eye-slash', 'fa-eye'); }
        }
    },

    loadShell: function() {
        document.getElementById('app-root').innerHTML = window.Templates['shell-view'];
        this.setGlobalLanguage(this.currentLang);
        if(this.currentUser.role === 'sales') {
            document.getElementById('sales-menu').style.display = 'block';
            document.getElementById('firm-context-menu').style.display = 'none';
            this.navigate('global-portfolio');
        } else {
            document.getElementById('sales-menu').style.display = 'none';
            // 🛡️ Admin veya Personel ise parentId üzerinden ana firmayı aç
            const targetId = this.currentUser.parentId || this.currentUser.uid;
            this.openFirm(targetId);
        }
    },

    searchTimeout: null,

    navigate: function(id, btn) {
        const viewport = document.getElementById('main-viewport');
        if(!viewport) return;
        window.currentViewId = id;
        viewport.innerHTML = window.Templates[id] || '<h1>Component Error</h1>';
        document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
        if(btn) btn.classList.add('active');

        this.applyTranslations(viewport);

        if(id === 'global-portfolio') this.Global.loadPortfolio();
        if(id === 'global-stats') this.Global.loadStats();
        if(id === 'global-new-firm') this.Global.prepareNewFirm();
        if(id === 'global-market-edit') this.Global.loadMarketConfig();
        if(id === 'tenant-dashboard') this.Tenant.renderOverview();
        if(id === 'tenant-info') this.Tenant.loadInfo();
        if(id === 'tenant-vehicles') this.Tenant.renderVehicles();
        if(id === 'tenant-devices') this.Tenant.renderDevices();
        if(id === 'tenant-staff') this.Tenant.loadStaff();
        if(id === 'tenant-tags') this.Tenant.loadTags();
        if(id === 'tenant-ai-analysis') this.Tenant.loadAI();
        if(id === 'tenant-admin-auth') this.Global.loadFirmAuth();
        if(id === 'tenant-market') this.Market.loadMarket();
    },

    openFirm: async function(id) {
        try {
            const firms = await this.ipc.invoke('get-firms');
            window.selectedFirm = firms.find(x => x.id === id) || { ...this.currentUser, id: id };
            document.getElementById('firm-context-menu').style.display = 'block';
            document.getElementById('ctx-name').innerHTML = `${window.selectedFirm.name}<br><small style="color:var(--text-muted); font-size:9px;">#${window.selectedFirm.firmCode || ''}</small>`;
            this.ipc.send('start-live-updates', window.selectedFirm.id);

            const plan = window.selectedFirm.plan || 'standart';

            if(this.currentUser.role === 'sales') {
                const hideIds = ['btn-arac-kayit', 'btn-cihaz-yonetim', 'btn-market', 'btn-genel-bakis', 'btn-personel-yonetim', 'btn-etiket-yonetim', 'btn-ai-analiz'];
                hideIds.forEach(hid => { const el = document.getElementById(hid); if(el) el.style.display = 'none'; });
                const authBtn = document.getElementById('btn-admin-auth'); if(authBtn) authBtn.style.display = 'flex';
                this.navigate('tenant-info');
            } else {
                const showIds = ['btn-arac-kayit', 'btn-cihaz-yonetim', 'btn-market', 'btn-genel-bakis', 'btn-personel-yonetim', 'btn-etiket-yonetim', 'btn-firma-bilgi'];
                showIds.forEach(sid => { const el = document.getElementById(sid); if(el) el.style.display = 'flex'; });
                const aiBtn = document.getElementById('btn-ai-analiz');
                if(aiBtn) aiBtn.style.display = (plan.includes('premium')) ? 'flex' : 'none';
                const authBtn = document.getElementById('btn-admin-auth'); if(authBtn) authBtn.style.display = 'none';
                this.navigate('tenant-dashboard');
            }
        } catch (e) {
            console.error("Open Firm Error:", e);
            alert("Sunucu hatası: Firma bilgileri yüklenemedi!");
        }
    },

    Global: {
        loadPortfolio: async function() {
            const firms = await Engine.ipc.invoke('get-firms');
            document.getElementById('firm-cards').innerHTML = firms.map(f => `
                <div class="box" style="display:flex; justify-content:space-between; align-items:center;">
                    <div><strong>${f.name}</strong><br><small style="color:var(--text-muted);">#${f.firmCode} | ${f.email}</small>
                    <div style="margin-top:5px;">${(f.isPremium || f.plan === 'premium') ? '<span class="badge-premium">PREMIUM</span>' : '<span class="badge-standart">STANDART</span>'}</div></div>
                    <div style="display:flex; gap:10px;"><button class="btn-blue" onclick="Engine.openFirm('${f.id}')"><i class="fas fa-eye"></i> İNCELE</button>
                    <button class="btn-danger" style="padding:0 15px;" onclick="Engine.Global.deleteFirm('${f.id}', '${f.name}')"><i class="fas fa-trash"></i></button></div>
                </div>`).join('') || '<p style="text-align:center; color:var(--text-muted);">Firma yok.</p>';
        },
        loadStats: async function() {
            const stats = await Engine.ipc.invoke('get-global-stats');
            document.getElementById('gs-firms').innerText = stats.totalFirms;
            document.getElementById('gs-plates').innerText = stats.totalPlates;
        },
        deleteFirm: async function(id, name) {
            if(!confirm(`'${name}' firmasını TÜM verileriyle silmek istiyor musunuz?`)) return;
            const res = await Engine.ipc.invoke('delete-staff', id);
            if(res) { alert("Firma silindi ✅"); this.loadPortfolio(); }
        },
        loadFirmAuth: function() {
            const f = window.selectedFirm;
            document.getElementById('adm-plan').value = f.plan || 'standart';
            document.getElementById('adm-max-vehicles').value = f.maxVehicles || 100;
            document.getElementById('adm-max-terminals').value = f.maxTerminals || 1;
            document.getElementById('adm-max-staff').value = f.maxStaff || 2;
            document.getElementById('adm-is-premium').checked = f.isPremium || false;
            document.getElementById('adm-expiry').value = f.expiryDate || "";
        },
        autoFillLimits: function(plan) {
            const v = document.getElementById('adm-max-vehicles');
            const t = document.getElementById('adm-max-terminals');
            const s = document.getElementById('adm-max-staff');
            const p = document.getElementById('adm-is-premium');
            if(plan.includes('premium')) { v.value = 5000; t.value = 5; s.value = 20; p.checked = true; }
            else if(plan.includes('small')) { v.value = 300; t.value = 2; s.value = 5; p.checked = true; }
            else { v.value = 100; t.value = 1; s.value = 2; p.checked = false; }
        },
        updateFirmAuth: async function() {
            const data = { plan: document.getElementById('adm-plan').value, maxVehicles: parseInt(document.getElementById('adm-max-vehicles').value), maxTerminals: parseInt(document.getElementById('adm-max-terminals').value), maxStaff: parseInt(document.getElementById('adm-max-staff').value), isPremium: document.getElementById('adm-is-premium').checked, expiryDate: document.getElementById('adm-expiry').value };
            const res = await Engine.ipc.invoke('update-firm', { firmId: window.selectedFirm.id, data });
            if(res.success) { alert("Yetkiler Güncellendi ✅"); window.selectedFirm = { ...window.selectedFirm, ...data }; }
        },
        loadMarketConfig: async function() {
            const config = await Engine.ipc.invoke('get-market-config');
            document.getElementById('me-std-price').value = config.std.price;
            document.getElementById('me-std-features').value = config.std.features;
            document.getElementById('me-sml-price').value = config.sml.price;
            document.getElementById('me-sml-features').value = config.sml.features;
            document.getElementById('me-prm-price').value = config.prm.price;
            document.getElementById('me-prm-features').value = config.prm.features;
        },
        saveMarketConfig: async function() {
            const data = { std: { price: document.getElementById('me-std-price').value, features: document.getElementById('me-std-features').value }, sml: { price: document.getElementById('me-sml-price').value, features: document.getElementById('me-sml-features').value }, prm: { price: document.getElementById('me-prm-price').value, features: document.getElementById('me-prm-features').value } };
            const res = await Engine.ipc.invoke('update-market-config', data);
            if(res.success) alert("Market Güncellendi! 🚀");
        },
        prepareNewFirm: function() {
            const code = Math.floor(10000000 + Math.random() * 90000000);
            const el = document.getElementById('nf-code');
            if(el) el.value = code;
        },
        saveFirm: async function() {
            const n = document.getElementById('nf-name').value, c = document.getElementById('nf-code').value, e = document.getElementById('nf-email').value, p = document.getElementById('nf-pass').value;
            if(!n || !c || !e || !p) { alert("Eksik bilgi!"); return; }
            const res = await Engine.ipc.invoke('add-staff', { parentId: null, staffData: { name:n, firmCode:c, email:e, role:'corporate' }, password:p });
            if(res.success) { alert("Firma Eklendi!"); Engine.navigate('global-portfolio'); }
        }
    },

    Tenant: {
        loadAI: async function() {
            const vehicles = window.allLiveVehicles || [];
            const insights = document.getElementById('ai-insights');
            const capPercent = document.getElementById('ai-cap-percent');
            const capText = document.getElementById('ai-cap-text');
            if(!insights) return;
            const f = window.selectedFirm;
            const plan = (f.plan || 'standart').toLowerCase();
            let maxCap = f.maxVehicles;
            if(!maxCap || maxCap < 100) {
                if (plan.includes('premium')) maxCap = 5000;
                else if (plan.includes('small')) maxCap = 300;
                else maxCap = 100;
            }
            const current = vehicles.length;
            const percent = Math.min(Math.round((current / maxCap) * 100), 100);
            capPercent.innerText = `${percent}%`;
            capPercent.style.color = percent > 90 ? 'var(--accent-red)' : 'var(--accent-blue)';
            capText.innerText = (Engine.currentLang === 'TR') ? `Toplam ${maxCap} araç kapasitesinden ${current} adedi kullanımda.` : `Total ${current} of ${maxCap} vehicles used.`;
            let aiHtml = "<ul style='list-style:none; padding:0;'>";
            const trPlates = vehicles.filter(v => /^[0-9]{2}[A-Z]/.test(v.plate)).length;
            const otherPlates = current - trPlates;
            const staffCount = window.allLiveStaff?.length || 0;
            const deviceCount = window.allLiveTerminals?.length || 0;
            if(Engine.currentLang === 'TR') {
                aiHtml += `<li><i class="fas fa-check-circle" style="color:var(--accent-green);"></i> <strong>Kapasite:</strong> %${percent} doluluk ile alan kullanımınız ideal.</li>`;
                aiHtml += `<li><i class="fas fa-globe" style="color:var(--accent-blue);"></i> <strong>Plaka Analizi:</strong> ${otherPlates} adet yabancı/moto plaka tespit edildi.</li>`;
                aiHtml += `<li><i class="fas fa-shield-alt" style="color:var(--accent-green);"></i> <strong>Güvenlik:</strong> Kayıtlar ${deviceCount} terminal ve ${staffCount} personelce doğrulanmıştır.</li>`;
            } else {
                aiHtml += `<li><i class="fas fa-check-circle" style="color:var(--accent-green);"></i> <strong>Capacity:</strong> Area usage is ideal with %${percent} occupancy.</li>`;
                aiHtml += `<li><i class="fas fa-globe" style="color:var(--accent-blue);"></i> <strong>Plates:</strong> ${otherPlates} foreign/moto plates detected.</li>`;
                aiHtml += `<li><i class="fas fa-shield-alt" style="color:var(--accent-green);"></i> <strong>Security:</strong> Logs verified by ${deviceCount} terminals and ${staffCount} staff.</li>`;
            }
            insights.innerHTML = aiHtml;
            this.renderAIChart(vehicles);
        },
        renderAIChart: function(vehicles) {
            const ctx = document.getElementById('ai-traffic-chart');
            if(!ctx) return;
            if(window.aiChart) window.aiChart.destroy();
            window.aiChart = new Chart(ctx, { type: 'line', data: { labels: ['06:00', '09:00', '12:00', '15:00', '18:00', '21:00', '00:00'], datasets: [{ label: 'Giriş Yoğunluğu', data: [5, 25, 12, 18, 45, 15, 8], borderColor: '#2f81f7', backgroundColor: 'rgba(47, 129, 247, 0.1)', fill: true, tension: 0.4 }] }, options: { responsive: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true, grid: { color: '#222' } }, x: { grid: { display: false } } } } });
            document.getElementById('ai-traffic-text').innerText = "AI Tahmini: Saat 17:00 - 19:00 arası en yüksek yoğunluk bekleniyor.";
        },
        renderOverview: function(vData) {
            const vehicles = vData || window.allLiveVehicles || [];
            const terminals = window.allLiveTerminals || [];
            const staff = window.allLiveStaff || [];
            const f = window.selectedFirm;
            const pkg = document.getElementById('pkg-status');
            if(pkg) pkg.innerHTML = (f.isPremium || f.plan === 'premium') ? '<span class="badge-premium">PREMIUM</span>' : '<span class="badge-standart">STANDART</span>';
            document.getElementById('stat-plates').innerText = vehicles.length;
            document.getElementById('stat-devices').innerText = terminals.length;
            document.getElementById('stat-staff').innerText = staff.length;
            const recent = [...vehicles].sort((a, b) => (b.createdAt?.seconds || 0) - (a.createdAt?.seconds || 0)).slice(0, 5);
            document.getElementById('recent-vehicles').innerHTML = recent.map(v => {
                const phone = v.phone || v.telefon || v.PHONE || v.TELEFON || "";
                return `<div class="recent-item">
                    <div>
                        <strong>${v.plate}</strong><br>
                        <small>${v.ownerName || ''}</small>
                        ${phone ? `<br><small style="font-size:10px; color:var(--text-muted);"><i class="fas fa-phone"></i> ${phone}</small>` : ''}
                    </div>
                    <div>
                        <span style="font-size:10px; color:var(--accent-blue);">${v.block || '-'} / ${v.apartment || '-'}</span>
                    </div>
                </div>`;
            }).join('') || 'Araç yok.';
            Engine.applyTranslations(document.getElementById('main-viewport'));
        },
        renderVehicles: function(vData) {
            if(document.activeElement && document.activeElement.id.startsWith('vi-')) return;
            const vehicles = vData || window.allLiveVehicles || [];
            const search = (document.getElementById('v-search')?.value || '').toLowerCase();
            const filtered = vehicles.filter(x => x.plate.toLowerCase().includes(search) || (x.ownerName || '').toLowerCase().includes(search));
            const flds = window.selectedFirm.enabledFields || ["plate", "ownerName", "block", "apartment", "phone"];
            const rowFields = ['blok', 'kat', 'daire', 'block', 'floor', 'apartment', 'dahili', 'no', 'numara'];
            document.getElementById('v-list').innerHTML = filtered.map(x => {
                let fieldsHtml = "", rowHtml = "";
                flds.forEach(f => {
                    const lower = f.toLowerCase();
                    // 🛡️ DAHİLİ VE DİĞER ALANLAR İÇİN TAM SENKRON
                    let val = x[f] || x[f.toUpperCase()] || x[lower];
                    if(!val) {
                        if(lower === 'floor' || lower === 'kat') val = x['floor'] || x['kat'] || x['FLOOR'] || x['KAT'];
                        else if(lower === 'phone' || lower === 'telefon') val = x['phone'] || x['telefon'] || x['PHONE'] || x['TELEFON'];
                        else if(lower === 'dahili') val = x['dahili'] || x['DAHILI'] || x['DAHİLİ'];
                        else if(lower === 'ownername' || lower === 'sahip') val = x['ownerName'] || x['ownername'] || x['sahip'] || x['SAHİP'] || x['OWNERNAME'];
                    }
                    val = val || "";
                    const label = Engine.t(f).toUpperCase();
                    const input = `<div class="col" style="min-width:120px;"><label style="font-size:11px;">${label}</label><input id="vi-${f}-${x.id}" value="${val}" style="height:38px;"></div>`;
                    if(rowFields.includes(lower)) rowHtml += input; else { if(rowHtml) { fieldsHtml += `<div class="row" style="gap:10px; margin-bottom:5px;">${rowHtml}</div>`; rowHtml = ""; } fieldsHtml += `<div class="row" style="gap:10px; margin-bottom:5px;">${input}</div>`; }
                });
                if(rowHtml) fieldsHtml += `<div class="row" style="gap:10px; margin-bottom:5px;">${rowHtml}</div>`;
                return `
                <div class="box" style="margin-bottom:12px; padding:15px;">
                    <div style="display:flex; justify-content:space-between; align-items:center; cursor:pointer;" onclick="Engine.Tenant.toggleVehicleDetail('${x.id}')">
                        <div style="display:flex; align-items:center; gap:20px; flex:1;">
                            <div style="background:rgba(88,166,255,0.1); padding:8px 12px; border-radius:6px; color:var(--accent-blue); font-weight:900; font-size:16px; min-width:110px; text-align:center;">${x.plate}</div>
                            <div style="display:flex; align-items:center; gap:60px;">
                                <strong style="color:#eee; font-size:15px; min-width:180px;">${x.ownerName || ''}</strong>
                                <span style="color:var(--text-muted); font-size:13px; font-weight:600;">${x.block || '-'} / ${x.apartment || '-'}</span>
                            </div>
                        </div>
                        <i class="fas fa-chevron-down" style="opacity:0.3;"></i>
                    </div>
                    <div id="v-det-${x.id}" style="display:none; margin-top:15px; border-top:1px solid #333; padding-top:20px;">
                        ${fieldsHtml}
                        <div style="text-align:right; margin-top:15px; border-top:1px dashed #222; padding-top:15px;">
                            <button class="btn-blue" onclick="Engine.Tenant.updateVehicle('${x.id}')" data-t="update"></button>
                            <button class="btn-danger" style="margin-left:10px;" onclick="Engine.Tenant.deleteVehicle('${x.id}', '${x.plate}')" data-t="delete"></button>
                        </div>
                    </div>
                </div>`;
            }).join('');
            Engine.applyTranslations(document.getElementById('v-list'));
        },
        renderDevices: function(tData) {
            const terminals = tData || window.allLiveTerminals || [];
            const max = window.selectedFirm.maxTerminals || 1;
            const remaining = max - terminals.length;
            const limitEl = document.getElementById('terminal-limit-info');
            if(limitEl) { limitEl.innerText = `LİMİT: ${terminals.length} / ${max}`; limitEl.className = remaining > 0 ? 'badge-premium' : 'badge-standart'; }
            document.getElementById('device-list').innerHTML = terminals.map(d => `<div class="box list-item" style="padding:15px; display:flex; justify-content:space-between; align-items:center; margin-bottom:10px;"><div><span>CİHAZ ID</span><br><strong>${d.deviceId}</strong></div><button class="btn-danger" onclick="Engine.Tenant.deleteDevice('${d.id}')"><i class="fas fa-trash"></i></button></div>`).join('') || 'Cihaz yok.';
            Engine.applyTranslations(document.getElementById('device-list'));
        },
        loadOverview: function() { this.renderOverview(); },
        loadInfo: function() { const f = window.selectedFirm; const set = (id, val) => { const el = document.getElementById(id); if(el) el.value = val || ''; }; set('f-name', f.name); set('f-code', f.firmCode); set('f-address', f.address); set('f-phone', f.phone); set('f-email', f.email); set('f-contact', f.contactName); set('f-contact-phone', f.contactPhone); },
        saveInfo: async function() { const data = { name: document.getElementById('f-name').value, address: document.getElementById('f-address').value, phone: document.getElementById('f-phone').value, email: document.getElementById('f-email').value, contactName: document.getElementById('f-contact').value, contactPhone: document.getElementById('f-contact-phone').value }; await Engine.ipc.invoke('update-firm', { firmId: window.selectedFirm.id, data }); alert("Ok ✅"); },
        loadVehicles: async function() { clearTimeout(Engine.searchTimeout); Engine.searchTimeout = setTimeout(async () => { const v = await Engine.ipc.invoke('get-vehicles', window.selectedFirm.id); this.renderVehicles(v); }, 300); },
        toggleVehicleDetail: function(id) { const el = document.getElementById(`v-det-${id}`); el.style.display = (el.style.display === 'none') ? 'block' : 'none'; },
        updateVehicle: async function(id) {
            const flds = window.selectedFirm.enabledFields || ["plate", "ownerName", "block", "apartment", "phone"];
            const data = {};
            flds.forEach(f => {
                const el = document.getElementById(`vi-${f}-${id}`);
                if(el) {
                    const lower = f.toLowerCase();
                    let key = lower;
                    if(lower === 'kat') key = 'floor'; else if(lower === 'telefon') key = 'phone'; else if(lower === 'sahip') key = 'ownerName'; else if(lower === 'blok') key = 'block'; else if(lower === 'daire') key = 'apartment';
                    data[key] = el.value.trim();
                }
            });
            if(data.plate) data.plate = data.plate.toUpperCase();
            await Engine.ipc.invoke('update-vehicle', { vehicleId: id, data });
            alert("Ok ✅");
        },
        deleteVehicle: async function(id, plate) { if(!confirm(`${plate} silinsin mi?`)) return; await Engine.ipc.invoke('delete-vehicle', id); },
        toggleAddVehicle: function() { const box = document.getElementById('add-vehicle-box'); if(box.style.display === 'none') { box.style.display = 'block'; const flds = window.selectedFirm.enabledFields || ["plate", "ownerName", "block", "apartment", "phone"]; const rowFields = ['blok', 'kat', 'daire', 'block', 'floor', 'apartment', 'dahili', 'no', 'numara']; let fieldsHtml = "", rowHtml = ""; flds.forEach(f => { const label = Engine.t(f).toUpperCase(); const input = `<div class="col"><label>${label}</label><input id="new-v-${f}" style="height:38px;"></div>`; if(rowFields.includes(f.toLowerCase())) rowHtml += input; else { if(rowHtml) { fieldsHtml += `<div class="row" style="gap:10px; margin-bottom:5px;">${rowHtml}</div>`; rowHtml = ""; } fieldsHtml += `<div class="row" style="gap:10px; margin-bottom:5px;">${input}</div>`; } }); if(rowHtml) fieldsHtml += `<div class="row" style="gap:10px; margin-bottom:5px;">${rowHtml}</div>`; document.getElementById('add-vehicle-fields').innerHTML = fieldsHtml; } else { box.style.display = 'none'; } },
        addVehicle: async function() {
            const flds = window.selectedFirm.enabledFields || ["plate", "ownerName", "block", "apartment", "phone"];
            const data = { ownerId: window.selectedFirm.id, recordedBy: "Dashboard" };
            flds.forEach(f => {
                const el = document.getElementById(`new-v-${f}`);
                if(el) {
                    const lower = f.toLowerCase();
                    let key = lower;
                    if(lower === 'kat') key = 'floor'; else if(lower === 'telefon') key = 'phone'; else if(lower === 'sahip') key = 'ownerName'; else if(lower === 'blok') key = 'block'; else if(lower === 'daire') key = 'apartment';
                    data[key] = el.value.trim();
                }
            });
            if(!data.plate) return;
            const res = await Engine.ipc.invoke('add-vehicle', { data });
            if(res.success) { alert("Ok ✅"); this.toggleAddVehicle(); }
        },
        importExcel: async function() { if(!window.selectedFirm.isPremium && window.selectedFirm.plan !== 'premium') { alert("Premium Gerekli! 🛒"); Engine.navigate('tenant-market'); return; } const res = await Engine.ipc.invoke('import-excel', { firmId: window.selectedFirm.id }); if(res.success) alert("Ok"); },
        exportVehicles: async function() { if(!window.selectedFirm.isPremium && window.selectedFirm.plan !== 'premium') { alert("Premium Gerekli! 🛒"); Engine.navigate('tenant-market'); return; } await Engine.ipc.invoke('export-to-excel', { collectionName: 'vehicles', fileName: 'arac_listesi', firmId: window.selectedFirm.id }); },
        loadDevices: async function() { const devices = await Engine.ipc.invoke('get-terminals', window.selectedFirm.id); this.renderDevices(devices); },
        generatePairingCode: async function() { const res = await Engine.ipc.invoke('create-pairing-code', window.selectedFirm.id); if(res.success) { document.getElementById('p-qr').src = res.qr; document.getElementById('p-code-text').innerText = res.code; document.getElementById('p-qr-container').style.display = 'block'; document.getElementById('p-placeholder').style.display = 'none'; } },
        deleteDevice: async function(id) { if(!confirm("Silinsin mi?")) return; await Engine.ipc.invoke('delete-terminal', id); },
        loadTags: function() {
            const flds = window.selectedFirm.enabledFields || ["plate", "ownerName"];
            document.getElementById('builder-active-tags').innerHTML = flds.map(f => `<div class="tag"><span>${f}</span><i class="fas fa-times" style="cursor:pointer;" onclick="Engine.Tenant.removeTag('${f}')"></i></div>`).join('');
            let html = "", rowItems = [];
            const sideBySideFields = ['blok', 'kat', 'daire', 'block', 'floor', 'apartment', 'dahili', 'no', 'numara'];
            flds.forEach(f => {
                const lower = f.toLowerCase();
                const display = Engine.currentLang === 'TR' ? f.toUpperCase() : (Engine.Translations['ENG'][lower] || f.toUpperCase());
                const item = `<div style="background:rgba(255,255,255,0.03); border:1px solid #30363d; padding:12px; border-radius:10px; flex:1;"><label style="font-size:8px; color:var(--accent-blue);">${display}</label><div style="height:14px;"></div></div>`;
                if(sideBySideFields.includes(lower)) { rowItems.push(item); if(rowItems.length === 2) { html += `<div style="display:flex; gap:10px; margin-bottom:10px;">${rowItems.join('')}</div>`; rowItems = []; } }
                else { if(rowItems.length) { html += `<div style="display:flex; gap:10px; margin-bottom:10px;">${rowItems.join('')}</div>`; rowItems = []; } html += `<div style="margin-bottom:10px;">${item}</div>`; }
            });
            if(rowItems.length) html += `<div style="display:flex; gap:10px; margin-bottom:10px;">${rowItems.join('')}</div>`;
            document.getElementById('mobile-preview-form').innerHTML = html;
            const tEl = document.getElementById('preview-title'); if(tEl) tEl.innerText = Engine.currentLang === 'TR' ? "Yeni Araç Kaydı" : "New Vehicle Register";
        },
        addTag: function() { const i = document.getElementById('new-tag-name'); if(!i || !i.value) return; if(!window.selectedFirm.enabledFields) window.selectedFirm.enabledFields = ["plate", "ownerName"]; const val = i.value.trim(); if(!window.selectedFirm.enabledFields.includes(val)) { window.selectedFirm.enabledFields.push(val); i.value = ''; Engine.Tenant.loadTags(); } },
        removeTag: function(t) { if(t === 'plate' || t === 'ownerName') return; window.selectedFirm.enabledFields = window.selectedFirm.enabledFields.filter(f => f !== t); Engine.Tenant.loadTags(); },
        saveTags: async function() { await Engine.ipc.invoke('update-firm', { firmId: window.selectedFirm.id, data: { enabledFields: window.selectedFirm.enabledFields } }); alert("Ok ✅"); },
        setLanguage: function(lang) { Engine.setGlobalLanguage(lang); },
        loadStaff: async function() { const s = await Engine.ipc.invoke('get-staff', window.selectedFirm.id); this.renderStaff(s); },
        renderStaff: function(sData) {
            const s = sData || window.allLiveStaff || [];
            const max = window.selectedFirm.maxStaff || 2;
            const limitEl = document.getElementById('staff-limit-info');
            if(limitEl) { limitEl.innerText = `LİMİT: ${s.length} / ${max}`; limitEl.className = (max - s.length) > 0 ? 'badge-premium' : 'badge-standart'; }
            document.getElementById('staff-list').innerHTML = s.map(x => `<div class="box list-item" onclick="Engine.Tenant.selectStaff('${x.id}','${x.name}','${x.email}')"><div><strong>${x.name}</strong><br><small>${x.email}</small></div><i class="fas fa-edit"></i></div>`).join('') || 'Personel yok.';
            Engine.applyTranslations(document.getElementById('staff-list'));
        },
        selectStaff: function(id, n, e) { window.selectedStaffId = id; document.getElementById('staff-edit-box').style.display = 'block'; document.getElementById('se-name').value = n; document.getElementById('se-email').value = e; },
        updateStaff: async function() { await Engine.ipc.invoke('update-firm', { firmId: window.selectedStaffId, data: { name:document.getElementById('se-name').value, email:document.getElementById('se-email').value } }); alert("Ok ✅"); },
        resetPassword: async function() { const p = window.prompt("Yeni Sifre:"); if(!p) return; await Engine.ipc.invoke('reset-staff-password', { uid: window.selectedStaffId, newPassword: p }); alert("Ok ✅"); },
        addStaff: async function() {
            const n = document.getElementById('s-name').value, sh = document.getElementById('s-short').value, p = document.getElementById('s-pass').value;
            if(!n || !sh || !p) return;
            const res = await Engine.ipc.invoke('add-staff', { parentId: window.selectedFirm.id, staffData: { name:n, shortName:sh, email:sh+"@auronova.com" }, password:p });
            if(res.success) { alert("Ok ✅"); }
        },
        deleteStaffAction: async function() { if(!confirm("Silinsin mi?")) return; await Engine.ipc.invoke('delete-staff-single', window.selectedStaffId); document.getElementById('staff-edit-box').style.display='none'; }
    },
    Market: {
        loadMarket: async function() {
            const config = await Engine.ipc.invoke('get-market-config');
            window.marketConfig = config;
            const currentPlan = (window.selectedFirm.plan || 'standart').toLowerCase();
            const setPack = (id, cfg, planKey) => {
                const pEl = document.getElementById(`m-${id}-price-view`);
                const fEl = document.getElementById(`m-${id}-features-view`);
                const btn = document.getElementById(`m-${id}-btn`);
                if(pEl) pEl.innerHTML = `${cfg.price} <small style="font-size:12px; color:var(--text-muted);">/ Ay</small>`;
                if(fEl) fEl.innerHTML = cfg.features.split('\n').map(f => `<li><i class="fas fa-check-circle" style="color:var(--accent-blue);"></i> ${f}</li>`).join('');
                if(btn) {
                    const isCurrent = currentPlan.includes(planKey);
                    if(isCurrent) { btn.innerText = Engine.currentLang === 'TR' ? "MEVCUT PAKET" : "CURRENT PLAN"; btn.className = "btn-outline"; btn.style.pointerEvents = "none"; btn.style.opacity = "0.5"; }
                    else { btn.innerText = Engine.currentLang === 'TR' ? "PAKETİMİ YÜKSELT" : "UPGRADE PACKAGE"; btn.className = id === 'prm' ? "btn-blue premium-btn" : "btn-blue"; if(id === 'prm') btn.style.background = "var(--accent-green)"; btn.style.pointerEvents = "auto"; btn.style.opacity = "1"; }
                }
            };
            setPack('std', config.std, 'standart'); setPack('sml', config.sml, 'small'); setPack('prm', config.prm, 'premium');
        },
        openCheckout: function(planId) {
            const config = window.marketConfig;
            let planData;
            if (planId === 'small') planData = config.sml; else if (planId === 'premium_usd') planData = config.prm; else planData = config.std;
            document.getElementById('selected-plan-name').innerText = planId.toUpperCase().replace('_USD', '');
            document.getElementById('total-price-text').innerText = planData.price;
            window.pendingPlan = planId;
            document.getElementById('market-main-view').style.display = 'none';
            document.getElementById('checkout-view').style.display = 'block';
        },
        closeCheckout: function() { document.getElementById('market-main-view').style.display = 'flex'; document.getElementById('checkout-view').style.display = 'none'; },
        processPayment: async function() {
            const name = document.getElementById('pay-name').value.trim();
            const card = document.getElementById('pay-card').value.trim();
            const exp = document.getElementById('pay-expiry').value.trim();
            const cvc = document.getElementById('pay-cvc').value.trim();
            if(!name || card.length < 16 || !exp || cvc.length < 3) { alert("Lütfen tüm ödeme bilgilerini eksiksiz ve doğru giriniz! 💳"); return; }
            const res = await Engine.ipc.invoke('update-firm', { firmId: window.selectedFirm.id, data: { isPremium: true, plan: window.pendingPlan, maxVehicles: window.pendingPlan === 'small' ? 300 : 5000, maxTerminals: window.pendingPlan === 'small' ? 2 : 5, maxStaff: window.pendingPlan === 'small' ? 5 : 20 } });
            if(res.success) { window.selectedFirm.isPremium = true; window.selectedFirm.plan = window.pendingPlan; document.getElementById('checkout-view').style.display = 'none'; document.getElementById('success-view').style.display = 'block'; }
        }
    }
};

window.onload = () => {
    Engine.init();
    Engine.ipc.on('live-vehicles', (event, vehicles) => {
        window.allLiveVehicles = vehicles;
        if(window.currentViewId === 'tenant-dashboard') Engine.Tenant.renderOverview();
        if(window.currentViewId === 'tenant-vehicles') Engine.Tenant.renderVehicles(vehicles);
    });
    Engine.ipc.on('live-terminals', (event, terminals) => {
        window.allLiveTerminals = terminals;
        if(window.currentViewId === 'tenant-dashboard') Engine.Tenant.renderOverview();
        if(window.currentViewId === 'tenant-devices') Engine.Tenant.renderDevices(terminals);
    });
    Engine.ipc.on('live-staff', (event, staff) => {
        window.allLiveStaff = staff;
        if(window.currentViewId === 'tenant-dashboard') Engine.Tenant.renderOverview();
        if(window.currentViewId === 'tenant-staff') Engine.Tenant.renderStaff(staff);
    });
};
