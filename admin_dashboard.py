import streamlit as st
import firebase_admin
from firebase_admin import credentials, firestore, auth
import pandas as pd

# 🚀 Firebase Başlatma
if not firebase_admin._apps:
    cred = credentials.Certificate("serviceAccountKey.json")
    firebase_admin.initialize_app(cred)

db = firestore.client()

st.set_page_config(page_title="Atlas Yazılım - Sales Dashboard", layout="wide")

st.title("🛡️ Atlas Plaka Takip - Sales & System Admin")
st.sidebar.header("Menü")
menu = st.sidebar.radio("İşlem Seçin", ["📊 Genel Bakış", "🏢 Kurumsal Firma Yönetimi", "🚗 Tüm Plaka Kayıtları", "👥 Kullanıcı Listesi"])

# --- GENEL BAKIŞ ---
if menu == "📊 Genel Bakış":
    st.subheader("Sistem İstatistikleri")

    users_ref = db.collection("users").stream()
    vehicles_ref = db.collection("vehicles").stream()

    users_list = [doc.to_dict() for doc in users_ref]
    vehicles_list = [doc.to_dict() for doc in vehicles_ref]

    col1, col2, col3 = st.columns(3)
    col1.metric("Toplam Kullanıcı", len(users_list))
    col2.metric("Toplam Araç Kaydı", len(vehicles_list))

    corporate_count = len([u for u in users_list if u.get("role") == "corporate"])
    col3.metric("Kurumsal Firma Sayısı", corporate_count)

    st.write("### Son Kayıtlar")
    if vehicles_list:
        df_v = pd.DataFrame(vehicles_list)
        st.table(df_v.tail(5))

# --- KURUMSAL FİRMA YÖNETİMİ ---
elif menu == "🏢 Kurumsal Firma Yönetimi":
    st.subheader("Yeni Kurumsal Firma Oluştur")

    with st.form("new_corporate_form"):
        comp_name = st.text_input("Firma/Yönetici Adı")
        comp_email = st.text_input("Firma E-postası")
        comp_pass = st.text_input("Şifre (Min 6 Karakter)", type="password")

        submitted = st.form_submit_button("Firma Hesabı Aç")
        if submitted:
            try:
                # 1. Firebase Auth'a ekle
                user = auth.create_user(email=comp_email, password=comp_pass)

                # 2. Firestore'a ekle
                db.collection("users").document(user.uid).set({
                    "name": comp_name,
                    "email": comp_email,
                    "role": "corporate",
                    "isAdmin": True,
                    "enabledFields": ["plate", "ownerName", "block", "apartment"]
                })
                st.success(f"{comp_name} başarıyla kurumsal yönetici olarak kaydedildi!")
            except Exception as e:
                st.error(f"Hata: {e}")

# --- TÜM PLAKA KAYITLARI ---
elif menu == "🚗 Tüm Plaka Kayıtları":
    st.subheader("Sistemdeki Tüm Araçlar")
    vehicles_ref = db.collection("vehicles").stream()
    vehicles_list = [doc.to_dict() for doc in vehicles_ref]

    if vehicles_list:
        df = pd.DataFrame(vehicles_list)
        st.dataframe(df, use_container_width=True)

        csv = df.to_csv(index=False).encode('utf-8')
        st.download_button("Listeyi Excel (CSV) Olarak İndir", csv, "plaka_listesi.csv", "text/csv")
    else:
        st.info("Henüz araç kaydı bulunmuyor.")

# --- KULLANICI LİSTESİ ---
elif menu == "👥 Kullanıcı Listesi":
    st.subheader("Tüm Kayıtlı Kullanıcılar")
    users_ref = db.collection("users").stream()
    users_data = []
    for doc in users_ref:
        d = doc.to_dict()
        d["ID"] = doc.id
        users_data.append(d)

    df_u = pd.DataFrame(users_data)
    st.dataframe(df_u, use_container_width=True)
