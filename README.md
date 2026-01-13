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


### 👻 Duchy
- Lista duchów (nazwa + obrazek)
- Szczegóły ducha
- Lista aspektów w szczegółach (duże i czytelne obrazki)

### 📜 Scenariusze
- Lista scenariuszy (nazwa + obrazek)
- Szczegóły scenariusza

### ⚔️ Przeciwnicy
- Lista przeciwników (nazwa + obrazek)
- Szczegóły przeciwnika

### 🎮 Gry
- Tworzenie nowej gry
- Dodawanie graczy do rozgrywki (wybór z listy użytkowników, duch, aspekt)
- Lista moich rozgrywek
- Szczegóły gry (wyświetlanie nazw zamiast ID)
- Usuwanie gry

### 🎲 Losowanie (sensor)
- Oddzielny ekran losowania (wybór: duch/scenariusz/przeciwnik)
- Losowanie po **potrząśnięciu telefonem (akcelerometr)**
- Animacja kości podczas losowania
- Historia ostatnich losowań

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
