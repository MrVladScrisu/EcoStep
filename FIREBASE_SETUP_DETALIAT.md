# 🔥 Ghid Pas cu Pas - Configurare Firebase pentru EcoStep

## 📋 Ce vei avea nevoie:
- Cont Google (Gmail)
- Proiectul EcoStep deschis în Android Studio
- 10-15 minute timp

---

## PASUL 1: Creează Cont Firebase (dacă nu ai)

### 1.1. Deschide browser-ul
- Deschide Chrome, Firefox sau orice browser
- Mergi la: https://console.firebase.google.com/

### 1.2. Login cu Google
- Click pe butonul "Sign in" (sau "Conectează-te")
- Introdu email-ul tău Gmail și parola
- Dacă nu ai cont Google, creează unul la https://accounts.google.com/signup

### 1.3. Acceptă termenii
- Citește și acceptă termenii și condițiile Firebase
- Click pe "Accept" sau "Acceptă"

---

## PASUL 2: Creează Proiect Firebase

### 2.1. Click pe "Add project" sau "Adaugă proiect"
- În pagina principală Firebase Console
- Vei vedea un buton mare "Add project" (sau "Adaugă proiect")
- Click pe el

### 2.2. Introdu numele proiectului
- **Nume proiect**: `EcoStep` (sau orice nume vrei)
- **Project ID**: va fi generat automat (ex: `ecostep-12345`)
- Click pe "Continue" sau "Continuă"

### 2.3. Configurează Google Analytics (OPȚIONAL)
- Firebase va întreba dacă vrei Google Analytics
- Pentru început, poți să-l **dezactivezi** (bifează "Not now" sau "Nu acum")
- Sau poți să-l activezi dacă vrei (recomandat pentru viitor)
- Click pe "Create project" sau "Creează proiect"

### 2.4. Așteaptă crearea proiectului
- Va dura 30-60 secunde
- Vei vedea un mesaj "Your new project is ready" sau "Proiectul tău este gata"
- Click pe "Continue" sau "Continuă"

---

## PASUL 3: Adaugă Aplicația Android

### 3.1. Click pe iconița Android
- În pagina principală a proiectului Firebase
- Vei vedea 3 iconițe: </> (web), 📱 (Android), 🍎 (iOS)
- Click pe iconița **Android** (telefonul verde)

### 3.2. Completează formularul

#### 3.2.1. Android package name
- **Ce să introduci**: `com.example.ecostep`
- **Unde să găsești asta**:
  1. Deschide Android Studio
  2. Deschide fișierul `app/build.gradle.kts`
  3. Caută linia: `applicationId = "com.example.ecostep"`
  4. Copiază exact ce scrie acolo (fără ghilimele)
  5. Lipește-l în câmpul "Android package name"

#### 3.2.2. App nickname (opțional)
- Poți să introduci: `EcoStep` sau să lași gol
- Nu este obligatoriu

#### 3.2.3. Debug signing certificate SHA-1 (OPȚIONAL - poți să-l lași gol)
- Pentru development, nu este necesar
- Poți să-l adaugi mai târziu dacă vrei
- **Lăsează gol pentru moment**

### 3.3. Click pe "Register app" sau "Înregistrează aplicația"

---

## PASUL 4: Descarcă google-services.json

### 4.1. Vei vedea un buton de download
- După ce ai înregistrat aplicația
- Vei vedea un buton mare: **"Download google-services.json"**
- Click pe el

### 4.2. Fișierul se va descărca
- Va apărea în folderul "Downloads" (Descărcări)
- Numele fișierului: `google-services.json`

### 4.3. Copiază fișierul în proiect
- **Deschide Android Studio**
- În partea stângă, vezi structura proiectului (Project view)
- Găsește folderul `app` (nu `app/src`, doar `app`)
- **Trage fișierul `google-services.json` în folderul `app`**
  - Sau: Click dreapta pe folderul `app` → Paste → Selectează `google-services.json`

### 4.4. Verifică că e în locul corect
- Structura trebuie să fie:
  ```
  EcoStep/
    app/
      google-services.json  ← AICI trebuie să fie!
      build.gradle.kts
      src/
  ```

### 4.5. Click pe "Next" în Firebase Console
- După ce ai copiat fișierul
- Click pe "Next" în browser

---

## PASUL 5: Configurează build.gradle.kts (Firebase face asta automat)

### 5.1. Firebase îți va arăta instrucțiuni
- Vei vedea 2 blocuri de cod
- **NU le copia manual!** Firebase le va adăuga automat

### 5.2. Verifică în Android Studio
- Deschide `build.gradle.kts` (cel din root, nu din app)
- Ar trebui să vezi deja:
  ```kotlin
  plugins {
      alias(libs.plugins.google.services) apply false
  }
  ```
- Dacă nu este, am adăugat deja în codul nostru

### 5.3. Verifică în app/build.gradle.kts
- Deschide `app/build.gradle.kts`
- Ar trebui să vezi deja:
  ```kotlin
  plugins {
      alias(libs.plugins.google.services)
  }
  ```
- Dacă nu este, am adăugat deja în codul nostru

### 5.4. Click pe "Next" în Firebase Console

---

## PASUL 6: Activează Firestore Database

### 6.1. Mergi la Firestore
- În meniul din stânga Firebase Console
- Click pe **"Firestore Database"** (sau "Bază de date Firestore")
- Dacă nu vezi meniul, click pe iconița cu 3 linii (☰) în stânga sus

### 6.2. Click pe "Create database"
- Vei vedea un buton mare "Create database"
- Click pe el

### 6.3. Alege modul de securitate
- Vei vedea 2 opțiuni:
  - **"Start in production mode"** - pentru producție (necesită reguli)
  - **"Start in test mode"** - pentru development (permite tot)
- **Alege "Start in test mode"** (pentru început)
- Click pe "Next"

### 6.4. Selectează locația
- Alege o locație apropiată de tine
- Exemple: `europe-west1` (Belgia), `europe-west3` (Frankfurt)
- Sau `us-central1` (Iowa, SUA) dacă nu găsești una europeană
- Click pe "Enable" sau "Activează"

### 6.5. Așteaptă crearea
- Va dura 1-2 minute
- Vei vedea "Cloud Firestore is being set up" sau "Firestore se configurează"

---

## PASUL 7: Activează Authentication

### 7.1. Mergi la Authentication
- În meniul din stânga
- Click pe **"Authentication"** (sau "Autentificare")

### 7.2. Click pe "Get started" sau "Începe"
- Vei vedea un buton mare
- Click pe el

### 7.3. Activează Anonymous sign-in
- Vei vedea o listă cu metode de autentificare
- Găsește **"Anonymous"** (sau "Anonim")
- Click pe el
- Va apărea un switch (comutator)
- **Activează switch-ul** (trebuie să fie albastru/verde)
- Click pe "Save" sau "Salvează"

---

## PASUL 8: Verifică Configurarea în Android Studio

### 8.1. Sync Project
- În Android Studio, sus în toolbar
- Click pe butonul **"Sync Project with Gradle Files"** (iconița cu elefant)
- Sau: File → Sync Project with Gradle Files
- Așteaptă să se termine (va dura 1-2 minute)

### 8.2. Verifică dacă apare eroare
- Dacă vezi erori roșii, verifică:
  - ✅ `google-services.json` este în folderul `app/`
  - ✅ Plugin-ul Google Services este în `build.gradle.kts`
  - ✅ Dependențele Firebase sunt în `app/build.gradle.kts`

### 8.3. Build Project
- Click pe: Build → Make Project
- Sau apasă: `Ctrl + F9` (Windows) sau `Cmd + F9` (Mac)
- Ar trebui să compileze fără erori

---

## PASUL 9: Testează Conexiunea

### 9.1. Rulează aplicația
- Click pe butonul verde "Run" (▶️)
- Sau apasă: `Shift + F10` (Windows) sau `Ctrl + R` (Mac)
- Alege un emulator sau telefon conectat

### 9.2. Verifică în Firebase Console
- Mergi la Firestore Database
- Dacă aplicația creează date, le vei vedea acolo
- Structura va fi: `users/{userId}/dailyLogs/{logId}`

---

## ✅ Verificare Finală - Checklist

Bifează fiecare item când e gata:

- [ ] Am cont Firebase
- [ ] Am creat proiectul "EcoStep"
- [ ] Am adăugat aplicația Android
- [ ] Am descărcat `google-services.json`
- [ ] Am copiat `google-services.json` în folderul `app/`
- [ ] Am activat Firestore Database (test mode)
- [ ] Am activat Authentication (Anonymous)
- [ ] Am făcut Sync în Android Studio
- [ ] Proiectul compilează fără erori
- [ ] Aplicația rulează pe telefon/emulator

---

## 🆘 Probleme Comune și Soluții

### Problema 1: "google-services.json not found"
**Soluție**: 
- Verifică că fișierul este exact în folderul `app/`
- Nu în `app/src/`, nu în root, ci în `app/`

### Problema 2: "Plugin with id 'com.google.gms.google-services' not found"
**Soluție**:
- Verifică că în `build.gradle.kts` (root) ai:
  ```kotlin
  plugins {
      alias(libs.plugins.google.services) apply false
  }
  ```

### Problema 3: "Firestore permission denied"
**Soluție**:
- Verifică că Firestore este în "test mode"
- Sau adaugă reguli de securitate (vezi FIREBASE_SETUP.md)

### Problema 4: "Authentication failed"
**Soluție**:
- Verifică că Anonymous sign-in este activat
- Verifică că ai internet pe telefon/emulator

---

## 📸 Screenshots - Unde să cauți

### În Firebase Console:
1. **Proiect**: Click pe numele proiectului în stânga sus
2. **Meniu**: Iconița cu 3 linii (☰) în stânga sus
3. **Firestore**: În meniu → Firestore Database
4. **Authentication**: În meniu → Authentication

### În Android Studio:
1. **Project View**: Click pe "Project" în partea stângă sus
2. **Folder app**: Expand `EcoStep` → `app`
3. **google-services.json**: Ar trebui să-l vezi acolo

---

## 🎉 Gata!

Dacă ai bifat toate itemele din checklist, Firebase este configurat corect!

Acum poți:
- ✅ Folosi login cu QR code
- ✅ Salva date în Firestore
- ✅ Vedea grafice personalizate per user
- ✅ Folosi AI pentru detectare mâncare

**Succes! 🚀**

