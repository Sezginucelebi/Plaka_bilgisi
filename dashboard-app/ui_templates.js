window.Templates = {
    'login-view': `
        <div style="width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; background: #000000;">
            <div style="width: 420px; text-align: center; padding: 50px; background: #080808; border: 2px solid #333333; border-radius: 30px; box-shadow: 0 15px 40px rgba(0,0,0,0.6);">
                <div style="margin-bottom: 45px;">
                    <h1 style="color: #fff; margin: 0; font-size: 34px; font-weight: 900; line-height: 1;">AuroNova</h1>
                    <h2 style="color: #fff; margin: 0; font-size: 30px; font-weight: 800;">Software</h2>
                </div>
                <div style="display: flex; align-items: center; gap: 15px; margin-bottom: 20px;">
                    <div style="width: 35px; text-align: center;"><i class="far fa-user" style="font-size: 28px; color: #fff;"></i></div>
                    <input type="text" id="login-email" style="flex: 1; background: #4a4a4a; border: none; border-radius: 12px; padding: 16px; color: #fff; font-size: 16px; margin: 0;">
                </div>
                <div style="display: flex; align-items: center; gap: 15px; margin-bottom: 15px;">
                    <div style="width: 35px; text-align: center;"><i class="fas fa-lock" style="font-size: 28px; color: #fff;"></i></div>
                    <div style="flex: 1; position: relative; display: flex; align-items: center;">
                        <input type="password" id="login-pass" style="width: 100%; background: #4a4a4a; border: none; border-radius: 12px; padding: 16px; color: #fff; font-size: 16px; margin: 0;">
                        <i class="far fa-eye" style="position: absolute; right: 15px; color: #adb5bd; font-size: 16px; cursor: pointer;" onclick="Engine.Auth.togglePass(this)"></i>
                    </div>
                </div>
                <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 35px; padding-left: 50px;">
                    <input type="checkbox" id="remember-me" style="width: 22px; height: 22px; accent-color: #fff; cursor: pointer; border: 1.5px solid #fff;">
                    <label for="remember-me" style="color: #fff; font-size: 14px; font-weight: 700; margin: 0; text-transform: none;">Beni Hatırla</label>
                </div>
                <button style="width: 200px; height: 45px; background: linear-gradient(180deg, #c4d9ff 0%, #e5bfff 100%); border: 1.5px solid #b8a0ff; border-radius: 30px; color: #000; font-size: 18px; font-weight: 800; cursor: pointer; box-shadow: 0 4px 15px rgba(255,255,255,0.1); margin-bottom: 15px;" onclick="Engine.Auth.login()">GİRİŞ</button>
                <p id="login-error" style="color: #f85149; font-size: 13px; font-weight: 700; min-height: 20px; margin: 0;"></p>
            </div>
        </div>`,

    'shell-view': `
        <nav id="sidebar">
            <div class="nav-header">
                <h1 style="color:var(--accent-blue);">AuroNova</h1>
                <div style="display:flex; gap:5px; margin-top:10px;">
                    <button class="lang-btn active-lang" style="flex:1;" onclick="Engine.setGlobalLanguage('TR')">TR</button>
                    <button class="lang-btn" style="flex:1;" onclick="Engine.setGlobalLanguage('ENG')">ENG</button>
                </div>
            </div>
            <div id="sales-menu" style="display:none;">
                <button class="nav-btn" data-t="customer_portfolio" onclick="Engine.navigate('global-portfolio', this)"><i class="fas fa-building"></i> Müşteri Portföyü</button>
                <button class="nav-btn" data-t="capacity_analysis" onclick="Engine.navigate('global-stats', this)"><i class="fas fa-chart-line"></i> Kapasite Analizi</button>
                <button class="nav-btn" data-t="market_management" onclick="Engine.navigate('global-market-edit', this)"><i class="fas fa-store-alt"></i> Market Yönetimi</button>
            </div>
            <div id="firm-context-menu" style="display:none; border-top: 1px solid #333; margin-top:10px;">
                <div id="ctx-name" style="padding:15px; font-weight:900; font-size:11px; color:#fff; text-transform:uppercase;">-</div>
                <button id="btn-admin-auth" class="nav-btn" style="color:var(--accent-green); font-weight:800;" onclick="Engine.navigate('tenant-admin-auth', this)"><i class="fas fa-shield-alt"></i> YETKİ & PAKET (SALES)</button>
                <button id="btn-genel-bakis" class="nav-btn" data-t="overview" onclick="Engine.navigate('tenant-dashboard', this)"><i class="fas fa-th-large"></i> Genel Bakış</button>
                <button id="btn-firma-bilgi" class="nav-btn" data-t="firm_info" onclick="Engine.navigate('tenant-info', this)"><i class="fas fa-info-circle"></i> Firma Bilgileri</button>
                <button id="btn-arac-kayit" class="nav-btn" data-t="vehicle_records" onclick="Engine.navigate('tenant-vehicles', this)"><i class="fas fa-car"></i> Araç Kayıtları</button>
                <button id="btn-cihaz-yonetim" class="nav-btn" data-t="device_management" onclick="Engine.navigate('tenant-devices', this)"><i class="fas fa-mobile-alt"></i> Cihaz Yönetimi</button>
                <button id="btn-personel-yonetim" class="nav-btn" data-t="staff_management" onclick="Engine.navigate('tenant-staff', this)"><i class="fas fa-users"></i> Personel Yönetimi</button>
                <button id="btn-etiket-yonetim" class="nav-btn" data-t="tag_management" onclick="Engine.navigate('tenant-tags', this)"><i class="fas fa-tags"></i> Etiket Yönetimi</button>
                <button id="btn-ai-analiz" class="nav-btn" data-t="ai_analysis" style="color:var(--accent-blue); display:none;" onclick="Engine.navigate('tenant-ai-analysis', this)"><i class="fas fa-brain"></i> Yapay Zeka Analizi</button>
                <button id="btn-market" class="nav-btn" data-t="market" onclick="Engine.navigate('tenant-market', this)"><i class="fas fa-shopping-cart"></i> Paketler & Market</button>
            </div>
            <button class="nav-btn" style="margin-top:auto; color:var(--accent-red);" data-t="logout" onclick="location.reload()">Çıkış</button>
        </nav>
        <main id="main-viewport"></main>`,

    'tenant-dashboard': `
        <div class="container">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:30px;">
                <div><h2 style="margin:0;" data-t="overview"></h2><div id="pkg-status"></div></div>
                <button class="btn-blue" onclick="Engine.navigate('tenant-vehicles')"><i class="fas fa-plus"></i> <span data-t="add_new_vehicle"></span></button>
            </div>
            <div class="row">
                <div class="stat-card"><i class="fas fa-car"></i><div class="stat-val" id="stat-plates">0</div><div class="stat-label" data-t="registered_vehicle"></div></div>
                <div class="stat-card"><i class="fas fa-mobile-alt"></i><div class="stat-val" id="stat-devices">0</div><div class="stat-label" data-t="active_device"></div></div>
                <div class="stat-card"><i class="fas fa-users"></i><div class="stat-val" id="stat-staff">0</div><div class="stat-label" data-t="personnel"></div></div>
            </div>
            <div class="row" style="margin-top:20px;">
                <div style="flex:1.5;"><div class="box"><div class="box-title" data-t="last_added"></div><div id="recent-vehicles">Yükleniyor...</div></div></div>
                <div style="flex:1;"><div class="box"><div class="box-title" data-t="quick_actions"></div><button class="btn-outline" style="width:100%; margin-bottom:10px; justify-content:flex-start;" onclick="Engine.navigate('tenant-devices')"><i class="fas fa-qrcode"></i> <span data-t="pair_new_device"></span></button><button class="btn-outline" style="width:100%; margin-bottom:10px; justify-content:flex-start;" onclick="Engine.navigate('tenant-market')"><i class="fas fa-crown"></i> <span data-t="upgrade_package"></span></button></div></div>
            </div>
        </div>`,

    'tenant-admin-auth': `
        <div class="container" style="max-width:800px;">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:30px;">
                <h2>Yetkilendirme & Paket Yönetimi (Sales)</h2>
                <div class="badge-premium" style="background:var(--accent-green); padding:8px 15px;">SALES ADMIN MODE</div>
            </div>
            <div class="box">
                <div class="box-title">PAKET VE LİMİT AYARLARI</div>
                <div class="row">
                    <div class="col">
                        <label>Aktif Paket Seçimi</label>
                        <select id="adm-plan" onchange="Engine.Global.autoFillLimits(this.value)">
                            <option value="standart">Standart Paket (Ücretsiz)</option>
                            <option value="small">Small Plan</option>
                            <option value="premium_usd">Premium Plan </option>
                        </select>
                    </div>
                    <div class="col">
                        <label>Abonelik Bitiş Tarihi (Geçici Yetki)</label>
                        <input type="date" id="adm-expiry">
                    </div>
                </div>

                <div class="row" style="margin-top:10px;">
                    <div class="col">
                        <label>Araç Kayıt Limiti</label>
                        <input type="number" id="adm-max-vehicles">
                    </div>
                    <div class="col">
                        <label>Cihaz (Terminal) Limiti</label>
                        <input type="number" id="adm-max-terminals">
                    </div>
                    <div class="col">
                        <label>Personel Limiti</label>
                        <input type="number" id="adm-max-staff">
                    </div>
                </div>

                <div class="box-title" style="margin-top:30px;">ÖZEL YETKİLER</div>
                <div style="display:flex; gap:20px; align-items:center; background:var(--bg-primary); padding:20px; border-radius:12px; border:1px solid #333;">
                    <input type="checkbox" id="adm-is-premium" style="width:22px; height:22px; accent-color:var(--accent-blue); margin:0;">
                    <div>
                        <label for="adm-is-premium" style="margin:0; cursor:pointer; color:#fff; font-size:14px;">Premium Özellikleri Kilidini Aç</label>
                        <small style="display:block; color:var(--text-muted); font-size:11px;">(Excel İçe/Dışa Aktarma, AI Analiz ve SMS sistemini anında etkinleştirir)</small>
                    </div>
                </div>

                <div style="margin-top:40px; text-align:right; display:flex; gap:15px; justify-content:flex-end;">
                    <button class="btn-outline" onclick="Engine.navigate('tenant-info')">İPTAL</button>
                    <button class="btn-blue" style="height:50px; padding:0 40px; font-size:16px;" onclick="Engine.Global.updateFirmAuth()">
                        <i class="fas fa-save"></i> YETKİLERİ GÜNCELLE VE KAYDET
                    </button>
                </div>
            </div>
        </div>`,

    'tenant-info': `
        <div class="container" style="max-width:900px;">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:30px;"><h2 data-t="firm_info"></h2><button class="btn-blue" onclick="Engine.Tenant.saveInfo()"><i class="fas fa-save"></i> <span data-t="update"></span></button></div>
            <div class="box">
                <div class="box-title" data-t="basic_info"></div>
                <div style="display:flex; gap:20px; margin-bottom:10px;"><div style="flex:1.5;"><label data-t="firm_name"></label><input id="f-name"></div><div style="flex:1;"><label data-t="firm_code"></label><input id="f-code" readonly></div></div>
                <div class="box-title" data-t="auth_info" style="margin-top:20px;"></div>
                <div style="display:flex; gap:20px; margin-bottom:10px;"><div style="flex:1;"><label data-t="auth_person"></label><input id="f-contact"></div><div style="flex:1;"><label data-t="auth_phone"></label><input id="f-contact-phone"></div></div>
                <div class="box-title" data-t="corp_comm" style="margin-top:20px;"></div>
                <div style="display:flex; gap:20px;"><div style="flex:1.5;"><label data-t="corp_address"></label><input id="f-address"></div><div style="flex:1;"><label data-t="corp_phone"></label><input id="f-phone"></div><div style="flex:1.2;"><label data-t="mail_addr"></label><input id="f-email"></div></div>
            </div>
        </div>`,

    'tenant-vehicles': `
        <div class="container">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:25px;">
                <h2 data-t="vehicle_records"></h2>
                <div style="display:flex; gap:12px;">
                    <button class="btn-outline" onclick="Engine.Tenant.importExcel()"><i class="fas fa-file-import"></i> <span data-t="excel_import"></span></button>
                    <button class="btn-outline" onclick="Engine.Tenant.exportVehicles()"><i class="fas fa-file-export"></i> <span data-t="export"></span></button>
                    <button class="btn-blue" onclick="Engine.Tenant.toggleAddVehicle()"><i class="fas fa-plus"></i> <span data-t="add_new_vehicle"></span></button>
                </div>
            </div>
            <div id="add-vehicle-box" class="box" style="display:none; border-top:4px solid var(--accent-blue);"><div class="box-title" data-t="add_new_vehicle"></div><div id="add-vehicle-fields"></div><div style="text-align:right; margin-top:20px;"><button class="btn-outline" onclick="Engine.Tenant.toggleAddVehicle()" data-t="back"></button><button class="btn-blue" style="margin-left:10px;" onclick="Engine.Tenant.addVehicle()" data-t="save"></button></div></div>
            <div class="box" style="padding:20px; margin-bottom:20px;"><input type="text" id="v-search" data-t="search_placeholder" onkeyup="Engine.Tenant.loadVehicles()" style="margin-bottom:0;"></div>
            <div id="v-list"></div>
        </div>`,

    'tenant-devices': `
        <div class="container"><div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;"><h2 data-t="device_management"></h2><div id="terminal-limit-info" class="badge-standart"></div></div>
            <div style="display:flex; gap:30px;"><div style="flex:1;"><div class="box" style="text-align:center;"><div class="box-title" data-t="add_device"></div><p style="font-size:12px; color:var(--text-muted);" data-t="qr_instruction"></p>
            <div id="p-qr-container" style="display:none; background:white; padding:20px; border-radius:15px; margin:20px auto; width:220px;"><img id="p-qr" src="" style="width:180px; height:180px;"><div id="p-code-text" style="color:#000; font-size:22px; font-weight:900; margin-top:10px;"></div></div>
            <div id="p-placeholder" style="height:250px; border:2px dashed #333; border-radius:15px; display:flex; align-items:center; justify-content:center; margin-bottom:20px;"><i class="fas fa-qrcode fa-3x" style="opacity:0.2;"></i></div>
            <button id="btn-generate-pairing" class="btn-blue" style="width:100%;" onclick="Engine.Tenant.generatePairingCode()"><i class="fas fa-sync"></i> <span data-t="qr_create"></span></button></div></div>
            <div style="flex:1.5;"><div class="box"><div class="box-title" data-t="active_terminals"></div><div id="device-list"></div></div></div></div>
        </div>`,

    'tenant-staff': `<div class="container"><div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:30px;"><h2 data-t="staff_management"></h2><div id="staff-limit-info" class="badge-standart"></div></div><div class="row"><div style="flex:1.2;"><div class="box"><div class="box-title" data-t="personnel"></div><div id="staff-list"></div></div></div><div style="flex:1;"><div id="staff-edit-box" class="box" style="display:none; border-top:4px solid var(--accent-blue);"><div class="box-title" data-t="staff_edit"></div><label data-t="name_surname"></label><input id="se-name"> <label>Email</label><input id="se-email"><div class="row"><button class="btn-blue" onclick="Engine.Tenant.updateStaff()" data-t="update"></button><button class="btn-blue" style="background:#58a6ff;" onclick="Engine.Tenant.resetPassword()" data-t="reset"></button></div><button class="btn-danger" style="width:100%; margin-top:10px;" onclick="Engine.Tenant.deleteStaffAction()" data-t="delete"></button></div><div class="box"><div class="box-title" data-t="new_staff"></div><label data-t="name_surname"></label><input id="s-name"> <label>Nick</label><input id="s-short"> <label>Pass</label><input type="password" id="s-pass"><button id="btn-add-staff" class="btn-blue" style="width:100%;" onclick="Engine.Tenant.addStaff()" data-t="save"></button></div></div></div></div>`,

    'tenant-tags': `
        <div class="container">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:30px;"><h2 data-t="tag_management"></h2><div style="display:flex; gap:10px;"><button class="lang-btn active-lang" id="lang-tr" onclick="Engine.Tenant.setLanguage('TR')">TR</button><button class="lang-btn" id="lang-eng" onclick="Engine.Tenant.setLanguage('ENG')">ENG</button></div></div>
            <div class="row" style="align-items: flex-start; gap: 40px;">
                <div style="flex:1;"><div class="box"><div class="box-title" data-t="active_tags"></div><div id="builder-active-tags" style="display:flex; flex-wrap:wrap; gap:10px; margin-bottom:30px;"></div><div class="box-title" data-t="add_new_field"></div><div style="display:flex; gap:10px; align-items:flex-end;"><div style="flex:1;"><label data-t="tag_name"></label><input id="new-tag-name" style="margin-bottom:0;"></div><button class="btn-blue" onclick="Engine.Tenant.addTag()"><i class="fas fa-plus"></i></button></div><div style="margin-top:30px; text-align:right;"><button class="btn-blue" style="background:var(--accent-green); height:50px;" onclick="Engine.Tenant.saveTags()"><i class="fas fa-save"></i> <span data-t="save"></span></button></div></div></div>
                <div style="flex: 1; display: flex; justify-content: center;"><div style="width: 320px; height: 620px; background: #000; border: 12px solid #222; border-radius: 45px; position: relative; box-shadow: 0 30px 60px rgba(0,0,0,0.5); overflow: hidden;"><div style="position: absolute; top: 15px; left: 50%; transform: translateX(-50%); width: 60px; height: 5px; background: #333; border-radius: 10px;"></div><div style="width: 100%; height: 100%; background: #0d1117; padding: 40px 20px;"><div style="text-align: center; margin-bottom: 25px;"><div id="preview-title" style="color: #fff; font-size: 14px; font-weight: 800;"></div></div><div id="mobile-preview-form"></div></div></div></div>
            </div>
        </div>`,

    'tenant-ai-analysis': `
        <div class="container">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:30px;">
                <div><h2 style="margin:0;" data-t="ai_analysis"></h2><span class="badge-premium">AI ENGINE ACTIVE</span></div>
                <button class="btn-blue" onclick="Engine.Tenant.loadAI()"><i class="fas fa-sync"></i> <span data-t="re_analyze"></span></button>
            </div>
            <div class="row">
                <div class="box col"><div class="box-title" data-t="traffic_predict"></div><canvas id="ai-traffic-chart" style="max-height:250px;"></canvas><p style="font-size:12px; color:var(--text-muted); margin-top:15px;" id="ai-traffic-text"></p></div>
                <div class="box col"><div class="box-title" data-t="capacity_analys"></div><div style="text-align:center; padding:20px;"><div style="font-size:48px; font-weight:900; color:var(--accent-blue);" id="ai-cap-percent">0%</div><div style="font-size:14px; color:#fff; margin-top:10px;" id="ai-cap-text"></div></div></div>
            </div>
            <div class="box"><div class="box-title" data-t="ai_insights_title"></div><div id="ai-insights" style="line-height:1.8; font-size:14px;"></div></div>
        </div>`,

    'tenant-market': `
        <div class="container">
            <h2 style="text-align:center;" data-t="market_title"></h2>
            <p style="text-align:center; color:var(--text-muted); margin-bottom:40px;" data-t="market_desc"></p>
            <div id="market-main-view" style="display:flex; gap:20px; justify-content:center; flex-wrap:wrap;">
                <div class="box" style="flex:1; min-width:280px; max-width:300px; border-top:4px solid var(--text-muted); text-align:center;"><div style="font-size:16px; font-weight:900; margin-bottom:10px;">STANDART</div><div style="font-size:28px; font-weight:900; margin-bottom:20px;" id="m-std-price-view">0 TL</div><ul style="list-style:none; padding:0; text-align:left; margin-bottom:30px; font-size:13px;" id="m-std-features-view"></ul><button id="m-std-btn" class="btn-outline" style="width:100%;"></button></div>
                <div class="box" style="flex:1; min-width:280px; max-width:300px; border-top:4px solid var(--accent-blue); text-align:center; background:rgba(47, 129, 247, 0.02);"><div style="font-size:16px; font-weight:900; margin-bottom:10px; color:var(--accent-blue);">SMALL</div><div style="font-size:28px; font-weight:900; margin-bottom:20px;" id="m-sml-price-view">4 $</div><ul style="list-style:none; padding:0; text-align:left; margin-bottom:30px; font-size:13px;" id="m-sml-features-view"></ul><button id="m-sml-btn" class="btn-blue" style="width:100%;" onclick="Engine.Market.openCheckout('small', 4)"></button></div>
                <div class="box" style="flex:1; min-width:280px; max-width:300px; border-top:4px solid var(--accent-green); text-align:center;"><div style="font-size:16px; font-weight:900; margin-bottom:10px; color:var(--accent-green);">PREMIUM</div><div style="font-size:28px; font-weight:900; margin-bottom:20px;" id="m-prm-price-view">10 $</div><ul style="list-style:none; padding:0; text-align:left; margin-bottom:30px; font-size:13px;" id="m-prm-features-view"></ul><button id="m-prm-btn" class="btn-blue" style="width:100%; background:var(--accent-green);" onclick="Engine.Market.openCheckout('premium_usd', 10)"></button></div>
            </div>
            <div id="checkout-view" style="display:none; max-width:500px; margin: 0 auto;" class="box">
                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:25px;"><div class="box-title" data-t="secure_pay"></div><button class="btn-outline" onclick="Engine.Market.closeCheckout()" data-t="back"></button></div>
                <div id="checkout-plan-info" style="background:var(--bg-primary); padding:15px; border-radius:10px; margin-bottom:20px;"><div style="font-weight:900; font-size:16px;" id="selected-plan-name"></div></div>

                <div style="margin-bottom:15px;"><label data-t="card_name"></label><input type="text" id="pay-name"></div>
                <div style="margin-bottom:15px;"><label data-t="card_no"></label><input type="text" id="pay-card" maxlength="19"></div>

                <div class="row">
                    <div class="col"><label data-t="exp_date"></label><input type="text" id="pay-expiry" maxlength="5"></div>
                    <div class="col"><label>CVC</label><input type="password" id="pay-cvc" maxlength="3"></div>
                </div>

                <div style="background:var(--bg-primary); padding:15px; border-radius:10px; margin-bottom:25px;"><div style="display:flex; justify-content:space-between; font-weight:900; color:var(--accent-blue); font-size:18px;"><span data-t="total"></span><span id="total-price-text"></span></div></div>
                <button class="btn-blue" style="width:100%; height:50px;" onclick="Engine.Market.processPayment()"><i class="fas fa-lock"></i> <span data-t="pay_complete"></span></button>
            </div>
            <div id="success-view" style="display:none; text-align:center;" class="box"><i class="fas fa-check-circle fa-5x" style="color:var(--accent-green); margin-bottom:20px;"></i><h2>Aboneliğiniz Aktif Edildi!</h2><button class="btn-blue" onclick="location.reload()">OK</button></div>
        </div>`,

    'global-stats': `
        <div class="container"><h2 data-t="capacity_analysis"></h2><div class="row"><div class="stat-card"><i class="fas fa-building"></i><div><div class="stat-val" id="gs-firms">0</div><div class="stat-label" data-t="customer_portfolio"></div></div></div><div class="stat-card"><i class="fas fa-car-side"></i><div><div class="stat-val" id="gs-plates">0</div><div class="stat-label" data-t="vehicle_records"></div></div></div></div></div>`,

    'global-portfolio': `<div style="width:100%;"><div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;"><h2 data-t="customer_portfolio"></h2><button onclick="Engine.navigate('global-new-firm')">+ <span data-t="add_new_firm"></span></button></div><div id="firm-cards" style="display:grid; grid-template-columns:1fr; gap:10px;"></div></div>`,

    'global-new-firm': `<div class="container" style="max-width:600px;"><h2 data-t="add_new_firm"></h2><div class="box"><label data-t="firm_name"></label><input id="nf-name"><label data-t="firm_code"></label><input id="nf-code" readonly style="opacity:0.7; background:rgba(0,0,0,0.2);"><label>Email</label><input id="nf-email"><label>Pass</label><input type="password" id="nf-pass"><div style="margin-top:10px; text-align:right;"><button class="btn-blue" onclick="Engine.Global.saveFirm()" data-t="save"></button></div></div></div>`,

    'global-market-edit': `
        <div class="container">
            <h2 data-t="market_management"></h2>
            <div class="row">
                <div class="box col"><div class="box-title">STANDART</div><label>Price</label><input id="me-std-price"><label>Features</label><textarea id="me-std-features" style="width:100%; height:200px; background:var(--bg-primary); color:#fff; padding:10px;"></textarea></div>
                <div class="box col"><div class="box-title">SMALL</div><label>Price</label><input id="me-sml-price"><label>Features</label><textarea id="me-sml-features" style="width:100%; height:200px; background:var(--bg-primary); color:#fff; padding:10px;"></textarea></div>
                <div class="box col"><div class="box-title">PREMIUM</div><label>Price</label><input id="me-prm-price"><label>Features</label><textarea id="me-prm-features" style="width:100%; height:200px; background:var(--bg-primary); color:#fff; padding:10px;"></textarea></div>
            </div>
            <div style="text-align:right; margin-top:20px;"><button class="btn-blue" onclick="Engine.Global.saveMarketConfig()" data-t="save"></button></div>
        </div>`
};
