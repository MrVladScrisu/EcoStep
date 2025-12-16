# 🔥 Configurare Firebase Authentication

## ⚠️ IMPORTANT: Trebuie să activezi Email/Password în Firebase!

---

## 📋 Pași Rapizi (2 minute):

### 1. Deschide Firebase Console
```
https://console.firebase.google.com/
```

### 2. Selectează Proiectul "EcoStep"
- Click pe numele proiectului

### 3. Mergi la Authentication
- În meniul din stânga
- Click pe "Authentication" (sau "Autentificare")

### 4. Activează Email/Password
```
1. Click pe tab-ul "Sign-in method" (sau "Metode de conectare")
2. Găsește "Email/Password" în listă
3. Click pe el
4. Activează switch-ul pentru "Email/Password" (primul)
5. NU activa "Email link" (al doilea) - lasă-l dezactivat
6. Click "Save" sau "Salvează"
```

### 5. Verifică
```
✅ "Email/Password" trebuie să aibă status "Enabled" (Activat)
✅ Ar trebui să vezi o bifă verde
```

---

## 🎯 Pași Detaliați (cu Screenshots Virtuale):

### Pasul 1: Firebase Console
```
┌─────────────────────────────────────┐
│  Firebase Console                   │
│  ┌───────────────────────────────┐ │
│  │ 🔥 EcoStep                    │ │
│  │ Project Overview              │ │
│  └───────────────────────────────┘ │
│                                     │
│  Build ▼                           │
│    Authentication  ← CLICK AICI    │
│    Firestore Database              │
│    Storage                         │
└─────────────────────────────────────┘
```

### Pasul 2: Authentication Page
```
┌─────────────────────────────────────┐
│  Authentication                     │
│  ┌─────────────────────────────┐  │
│  │ Users │ Sign-in method ← TAB│  │
│  └─────────────────────────────┘  │
│                                     │
│  Sign-in providers:                │
│                                     │
│  ┌─────────────────────────────┐  │
│  │ Email/Password    Disabled  │  │
│  │                   ← CLICK   │  │
│  └─────────────────────────────┘  │
│                                     │
│  ┌─────────────────────────────┐  │
│  │ Anonymous         Enabled   │  │
│  └─────────────────────────────┘  │
└─────────────────────────────────────┘
```

### Pasul 3: Activează Email/Password
```
┌─────────────────────────────────────┐
│  Email/Password                     │
│                                     │
│  ┌─────────────────────────────┐  │
│  │ Email/Password              │  │
│  │ ○ → ● (Switch ON)           │  │
│  │                             │  │
│  │ Email link (passwordless)   │  │
│  │ ○ (lasă OFF)                │  │
│  └─────────────────────────────┘  │
│                                     │
│  [Cancel]  [Save] ← CLICK          │
└─────────────────────────────────────┘
```

### Pasul 4: Verificare
```
┌─────────────────────────────────────┐
│  Sign-in providers:                │
│                                     │
│  ┌─────────────────────────────┐  │
│  │ ✅ Email/Password  Enabled  │  │
│  │    (Status: Active)         │  │
│  └─────────────────────────────┘  │
│                                     │
│  ┌─────────────────────────────┐  │
│  │ ✅ Anonymous      Enabled   │  │
│  └─────────────────────────────┘  │
└─────────────────────────────────────┘
```

---

## ❓ Probleme Comune

### Problema 1: "Nu găsesc Authentication"
**Soluție**:
- Verifică că ești în proiectul corect (EcoStep)
- Caută în meniul din stânga sub "Build"
- Dacă nu vezi, scroll down în meniu

### Problema 2: "Email/Password nu apare în listă"
**Soluție**:
- Verifică tab-ul "Sign-in method" (nu "Users")
- Scroll down în listă
- Ar trebui să fie printre primele opțiuni

### Problema 3: "Nu pot activa Email/Password"
**Soluție**:
- Verifică că ai permisiuni de admin pe proiect
- Încearcă să reîncarci pagina (F5)
- Logout și login din nou în Firebase Console

---

## 🧪 Testare După Activare

### Test 1: Înregistrare
```
1. Rulează aplicația
2. Click "Login cu Email/Parolă"
3. Click "Nu am cont. Înregistrare"
4. Introduci: test@gmail.com / parola123
5. Click "Înregistrează-te"
6. ✅ Ar trebui să funcționeze!
```

### Test 2: Login
```
1. Logout din aplicație
2. Click "Login cu Email/Parolă"
3. Introduci: test@gmail.com / parola123
4. Click "Login"
5. ✅ Ar trebui să intri!
```

### Test 3: Verificare în Firebase
```
1. Firebase Console → Authentication → Users
2. ✅ Ar trebui să vezi utilizatorul creat
3. Email: test@gmail.com
4. Provider: Password
```

---

## 📝 Checklist

- [ ] Am deschis Firebase Console
- [ ] Am selectat proiectul EcoStep
- [ ] Am mers la Authentication
- [ ] Am deschis tab-ul "Sign-in method"
- [ ] Am găsit "Email/Password"
- [ ] Am activat switch-ul
- [ ] Am salvat (Save)
- [ ] Status este "Enabled" ✅
- [ ] Am testat înregistrarea în aplicație
- [ ] Funcționează! 🎉

---

**Gata! Acum poți folosi Email/Password în aplicație! 🚀**

