# Spiritland – aplikacja mobilna (Android) + REST API (.NET) + PostgreSQL

Projekt składa się z:
- **Backend**: ASP.NET Core (C#) + JWT + Entity Framework Core
- **Baza danych**: PostgreSQL
- **Aplikacja mobilna**: Android Studio (Kotlin + Jetpack Compose)
- **Komunikacja**: REST API (Retrofit)

Aplikacja umożliwia m.in. logowanie, przeglądanie duchów/scenariuszy/przeciwników, tworzenie rozgrywek oraz losowanie elementów gry za pomocą sensora telefonu.

---

## ✨ Funkcjonalności

### 🔐 Autoryzacja (JWT)
- Rejestracja użytkownika
- Logowanie użytkownika
- Utrzymanie sesji (token zapisywany w `SharedPreferences`)
- Pobieranie profilu użytkownika `/me`
- Wylogowanie
<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/d67074cd-f4bc-4c28-b60c-2b9dccbf92cb" />
<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/d60391cf-3245-44dc-9dca-3f282d686d1f" />

### 👻 Duchy
- Lista duchów (nazwa + obrazek)
- Szczegóły ducha
- Lista aspektów w szczegółach (duże i czytelne obrazki)
<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/c5ae58e6-4f35-4ba8-806e-3d70e14b26e0" />
<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/56b2be0f-5af3-47b8-ba19-9cb7c4543591" />

### 📜 Scenariusze
- Lista scenariuszy (nazwa + obrazek)
- Szczegóły scenariusza
<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/6abe3abd-d852-4376-a94b-56b57fd44678" />
<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/55922b42-be35-4edb-9aad-e77d12947243" />

### ⚔️ Przeciwnicy
- Lista przeciwników (nazwa + obrazek)
- Szczegóły przeciwnika
<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/baa72b14-0a0d-4329-b367-6fae67f2ee80" />

### 🎮 Gry
- Tworzenie nowej gry
- Dodawanie graczy do rozgrywki (wybór z listy użytkowników, duch, aspekt)
- Lista moich rozgrywek
- Szczegóły gry (wyświetlanie nazw zamiast ID)
- Usuwanie gry
<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/a5f87ce0-c349-44f7-85a7-ca4c826237ea" />
<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/6be8fefe-25c2-4ae8-ba6f-5755fb8f6030" />

### 🎲 Losowanie (sensor)
- Oddzielny ekran losowania (wybór: duch/scenariusz/przeciwnik)
- Losowanie po **potrząśnięciu telefonem (akcelerometr)**
- Animacja kości podczas losowania
- Historia ostatnich losowań
<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/c014e91f-3f8b-4887-bd00-d6620b9cc597" />

---

## 🧩 Technologie

### Android
- Kotlin
- Jetpack Compose
- Navigation Compose
- Retrofit + OkHttp
- Coil
- SensorManager (akcelerometr)

### Backend
- ASP.NET Core Web API
- Entity Framework Core (Npgsql)
- JWT Authentication
- Swagger

### DB
- PostgreSQL (pgAdmin 4)
