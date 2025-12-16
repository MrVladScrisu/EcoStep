# 🔐 Ghid Configurare Autentificare Biometrică

## Problema: "Nu s-a găsit Activity-ul" sau "context invalid"

### Soluție 1: Configurare pe Telefon

**IMPORTANT**: Pentru ca autentificarea cu amprentă să funcționeze, trebuie să ai amprentă configurată pe telefon!

#### Pași pentru configurare amprentă:

1. **Deschide Setări** pe telefon
2. **Mergi la Securitate** (sau "Security & privacy")
3. **Căută "Fingerprint" sau "Amprentă"**
4. **Adaugă o amprentă nouă**:
   - Urmează pașii de pe ecran
   - Plasează degetul pe senzor de mai multe ori
   - Confirmă când este gata

5. **Testează amprenta**:
   - Încearcă să deblochezi telefonul cu amprenta
   - Dacă merge, atunci va merge și în aplicație

---

### Soluție 2: Activare Firebase Anonymous Authentication

Pentru ca autentificarea biometrică să funcționeze, trebuie să activezi **Anonymous Authentication** în Firebase:

#### Pași:

1. **Mergi la [Firebase Console](https://console.firebase.google.com/)**
2. **Selectează proiectul tău** (EcoStep)
3. **Click pe "Authentication"** din meniul stânga
4. **Click pe tab-ul "Sign-in method"**
5. **Găsește "Anonymous"** în lista de provideri
6. **Click pe "Anonymous"**
7. **Activează toggle-ul "Enable"**
8. **Click "Save"**

---

### Soluție 3: Testare pe Emulator

Dacă testezi pe **emulator Android Studio**:

1. **Deschide emulatorul**
2. **Click pe "..." (Extended controls)** din toolbar-ul emulatorului
3. **Mergi la "Fingerprint"**
4. **Click "Touch the sensor"** pentru a simula o amprentă

---

## Alternative de Login

Dacă autentificarea biometrică nu funcționează, aplicația oferă 3 opțiuni:

### 1. **Login cu Amprentă** (recomandat)
- Cel mai rapid și sigur
- Necesită amprentă configurată pe telefon

### 2. **Login cu Email/Parolă**
- Pentru utilizare pe mai multe dispozitive
- Fiecare cont are date separate
- Necesită activarea Email/Password în Firebase (vezi FIREBASE_AUTH_SETUP.md)

### 3. **Demo (fără autentificare)**
- Pentru testare rapidă
- Datele sunt salvate local
- Nu necesită configurare

---

## Verificare Funcționalitate

### Când deschizi aplicația:

1. **Dacă ai amprentă configurată**:
   - Vei vedea butonul "Deblochează cu Amprentă"
   - Click pe buton → dialog de amprentă
   - Plasează degetul → intri în aplicație

2. **Dacă NU ai amprentă configurată**:
   - Vei vedea mesajul: "Nu ai configurat amprentă/Face ID. Configurează în Setări."
   - Folosește "Login cu Email/Parolă" sau "Demo"

---

## Mesaje de Eroare Comune

### "Dispozitivul nu are senzor biometric"
- **Cauză**: Telefonul nu are senzor de amprentă
- **Soluție**: Folosește "Login cu Email/Parolă"

### "Nu ai configurat amprentă/Face ID"
- **Cauză**: Nu ai adăugat nicio amprentă în Setări
- **Soluție**: Configurează amprentă în Setări > Securitate

### "Eroare Firebase: ..."
- **Cauză**: Anonymous Authentication nu este activat în Firebase
- **Soluție**: Activează Anonymous în Firebase Console (vezi pașii de mai sus)

### "Folosește butonul 'Login cu Email/Parolă'"
- **Cauză**: Context-ul Activity nu este disponibil
- **Soluție**: Folosește alternativa Email/Parolă

---

## Testare Completă

### Pe Telefon Real:

1. ✅ Configurează amprentă în Setări
2. ✅ Activează Anonymous Auth în Firebase
3. ✅ Instalează aplicația: `.\gradlew.bat installDebug`
4. ✅ Deschide aplicația
5. ✅ Click "Deblochează cu Amprentă"
6. ✅ Plasează degetul pe senzor
7. ✅ Intri în aplicație

### Pe Emulator:

1. ✅ Pornește emulatorul
2. ✅ Configurează "Fingerprint" în Extended Controls
3. ✅ Instalează aplicația
4. ✅ Click "Deblochează cu Amprentă"
5. ✅ Click "Touch the sensor" în Extended Controls
6. ✅ Intri în aplicație

---

## Notă Importantă despre Date

- **Fiecare cont are date SEPARATE**
- **Login biometric** = cont anonim unic pentru dispozitivul tău
- **Login email** = cont persistent pe orice dispozitiv
- **Demo** = cont anonim temporar

**Nu vei mai avea problema cu log-uri partajate între conturi!** ✅

---

## Dacă tot nu merge

1. **Verifică că ai activat Anonymous Auth în Firebase**
2. **Verifică că ai amprentă configurată pe telefon**
3. **Încearcă să dezinstalezi și reinstalezi aplicația**
4. **Folosește alternativa "Login cu Email/Parolă"**
5. **Verifică că telefonul are Android 6.0+ (API 23+)**

---

## Contact

Dacă problema persistă, trimite-mi:
- Versiunea Android (ex: Android 13)
- Modelul telefonului (ex: Samsung Galaxy S21)
- Mesajul exact de eroare
- Screenshot-uri dacă este posibil

