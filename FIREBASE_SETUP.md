# Configurare Firebase pentru EcoStep

## Pași de configurare

### 1. Creează un proiect Firebase

1. Mergi la [Firebase Console](https://console.firebase.google.com/)
2. Click pe "Add project"
3. Introdu numele proiectului (ex: "EcoStep")
4. Urmează pașii de configurare

### 2. Adaugă aplicația Android

1. În Firebase Console, click pe iconița Android
2. Introdu:
   - **Package name**: `com.example.ecostep` (sau package-ul tău)
   - **App nickname**: EcoStep (opțional)
   - **Debug signing certificate SHA-1**: (opțional pentru development)

### 3. Descarcă `google-services.json`

1. Descarcă fișierul `google-services.json`
2. Copiază-l în `app/` folder (la același nivel cu `build.gradle.kts`)

### 4. Activează serviciile Firebase

#### Firestore Database
1. În Firebase Console, mergi la **Firestore Database**
2. Click pe "Create database"
3. Alege "Start in test mode" (pentru development)
4. Selectează o locație pentru database

#### Authentication
1. În Firebase Console, mergi la **Authentication**
2. Click pe "Get started"
3. Activează **Anonymous** sign-in method (pentru login cu QR)

### 5. Structura Firestore

Aplicația folosește următoarea structură:

```
users/
  {userId}/
    - id: string
    - email: string
    - displayName: string
    - qrCode: string
    - createdAt: timestamp
    dailyLogs/
      {logId}/
        - id: number
        - date: string
        - transportType: string
        - transportDistanceKm: number
        - meatPortions: number
        - veggiesPortions: number
        - dairyPortions: number
        - junkFoodPortions: number
        - electricityKwh: number
        - waterLiters: number
        - wasteBags: number
        - recycledToday: boolean
        - steps: number
        - ecoScore: number
```

### 6. Reguli de securitate Firestore (pentru producție)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users pot citi/scrie doar propriile date
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
      
      // Daily logs per user
      match /dailyLogs/{logId} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
    }
  }
}
```

### 7. Verificare

După configurare, verifică că:
- ✅ `google-services.json` este în folderul `app/`
- ✅ Plugin-ul Google Services este adăugat în `build.gradle.kts`
- ✅ Firestore este activat
- ✅ Authentication (Anonymous) este activat

## Funcționalități implementate

### Login cu QR Code
- Fiecare user are un QR code unic
- QR code-ul conține userId-ul
- Scanarea QR code-ului autentifică user-ul

### Grafice personalizate
- Graficele afișează doar datele user-ului autentificat
- Datele sunt sincronizate cu Firestore
- Actualizări în timp real

### AI pentru detectare mâncare
- Folosește ML Kit Image Labeling
- Detectează automat categorii: carne, legume, fructe, lactate, amestec
- Calculează medii/generalizări pentru porții

## Note importante

⚠️ **Pentru producție:**
- Configurează regulile de securitate Firestore
- Folosește autentificare mai sigură (Email/Password sau OAuth)
- Activează Cloud Functions pentru validări suplimentare
- Configurează backup-uri automate

⚠️ **Development:**
- Test mode în Firestore permite acces complet (doar pentru development)
- Anonymous auth este suficient pentru testare
- QR codes pot fi generate local pentru testare

