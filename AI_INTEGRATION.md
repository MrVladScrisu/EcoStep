# Integrare AI în EcoStep

## Prezentare Generală

EcoStep folosește AI (Artificial Intelligence) pentru a automatiza detectarea și clasificarea activităților ecologice din imagini. Această funcționalitate este în curs de dezvoltare și este marcată cu placeholder-uri în cod.

## Funcționalități AI Planificate

### 1. Detectare Tip Vehicul (Transport)
**Locație în cod:** `DailyLogScreen.kt` - secțiunea Transport

**Ce face:**
- Analizează o poză cu un vehicul
- Detectează automat tipul de vehicul (mașină, bicicletă, autobuz, etc.)
- Estimează distanța parcursă bazându-se pe contextul imaginii

**Tehnologie propusă:**
- **ML Kit Object Detection** (Google) - pentru detectarea obiectelor
- **TensorFlow Lite** - model custom antrenat pentru recunoaștere vehicule
- **CameraX** - pentru capturarea imaginilor

**Implementare:**
```kotlin
// TODO: Integrare AI pentru detectarea tipului de vehicul din poză
// Exemplu: folosind ML Kit sau TensorFlow Lite pentru recunoaștere imagini
val transportImagePicker = rememberLauncherForActivityResult(...) { uri ->
    transportPhoto = uri
    // Aici se va apela serviciul AI
    // detectVehicleType(uri) -> TransportOption
    // estimateDistance(uri) -> Double
}
```

### 2. Detectare Tip Mâncare și Estimare Porții (Alimentație)
**Locație în cod:** `DailyLogScreen.kt` - secțiunea Alimentație

**Ce face:**
- Analizează o poză cu mâncare
- Identifică tipurile de mâncare (carne, legume, lactate, fast-food)
- Estimează numărul de porții pentru fiecare categorie

**Tehnologie propusă:**
- **ML Kit Food Recognition** - pentru recunoașterea mâncării
- **TensorFlow Lite** - model custom pentru clasificare mâncare
- **Google Cloud Vision API** - pentru analiza avansată (opțional)

**Implementare:**
```kotlin
// TODO: Integrare AI pentru detectarea tipului de mâncare și estimarea porțiilor
// Exemplu: folosind ML Kit Food Recognition sau custom model
val foodImagePicker = rememberLauncherForActivityResult(...) { uri ->
    foodPhoto = uri
    // Aici se va apela serviciul AI
    // detectFoodTypes(uri) -> Map<FoodType, Portions>
    // updateFormData(detectedFood)
}
```

## Status Actual

### ✅ Implementat
- UI pentru încărcarea imaginilor
- Placeholder-uri pentru integrarea AI
- Structură de date pregătită pentru rezultatele AI

### 🚧 În Dezvoltare
- Integrarea efectivă a ML Kit sau TensorFlow Lite
- Antrenarea modelelor de machine learning
- Optimizarea pentru performanță pe dispozitive mobile

### 📋 Planificat
- Cache pentru rezultatele AI
- Feedback loop pentru îmbunătățirea acurateței
- Integrare cu servicii cloud pentru analiză avansată

## Cum Funcționează (Când va fi implementat)

1. **Utilizatorul încarcă o poză:**
   - Alege o imagine din galerie sau face o poză
   - Imaginea este trimisă către serviciul AI

2. **AI-ul procesează imaginea:**
   - Extrage caracteristici din imagine
   - Aplică modelul de machine learning
   - Generează predicții

3. **Rezultatele sunt returnate:**
   - Formularul este completat automat
   - Utilizatorul poate corecta dacă este necesar

## Tehnologii Recomandate

### Pentru Dispozitive Mobile (On-Device)
- **ML Kit** (Google) - ușor de integrat, funcționează offline
- **TensorFlow Lite** - mai flexibil, necesită antrenare proprie
- **Core ML** (iOS) - dacă se dezvoltă versiune iOS

### Pentru Cloud (Opțional)
- **Google Cloud Vision API** - pentru analiză avansată
- **AWS Rekognition** - alternativă cloud
- **Azure Computer Vision** - altă opțiune

## Exemple de Utilizare

### Detectare Vehicul
```
Input: Poză cu o mașină
Output: 
  - Tip: "car"
  - Distanță estimată: 25.5 km (bazat pe context)
  - Confidență: 0.92
```

### Detectare Mâncare
```
Input: Poză cu o masă
Output:
  - Carne: 2 porții (confidență: 0.85)
  - Legume: 3 porții (confidență: 0.90)
  - Lactate: 1 porție (confidență: 0.75)
```

## Note Importante

⚠️ **Privirea la date:** Toate procesările AI se fac local pe dispozitiv sau cu permisiunea explicită a utilizatorului pentru cloud.

⚠️ **Performanță:** Modelele AI vor fi optimizate pentru a funcționa eficient pe dispozitive mobile cu resurse limitate.

⚠️ **Acuratețe:** Rezultatele AI sunt estimări și utilizatorul poate întotdeauna să le corecteze manual.

## Resurse pentru Dezvoltare

- [ML Kit Documentation](https://developers.google.com/ml-kit)
- [TensorFlow Lite](https://www.tensorflow.org/lite)
- [Android CameraX](https://developer.android.com/training/camerax)

