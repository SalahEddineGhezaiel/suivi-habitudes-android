# Suivi d'Habitudes

Android app (Java) to track daily habits: create habits by category, check them off each day, and view statistics. The backend is a small PHP + MySQL REST-style API.

## Features
- Register / login
- Add, edit, delete habits (with category)
- Daily check-off and dashboard progress
- Statistics per category, reset statistics / clear habits
- Change password (settings)

## Stack
- Android: Java, Retrofit 2 + Gson, Material Components (minSdk 27, targetSdk 36)
- Backend: PHP (`PHP/`), MySQL via `mysqli`

## Setup

### 1. Backend
1. Install XAMPP or WAMP (Apache + MySQL).
2. Copy the `PHP/` folder to `htdocs/suivi_habitudes/` so endpoints are served at `http://localhost/suivi_habitudes/login.php`, etc.
3. Copy `db_config.example.php` to `db_config.php` and set your MySQL host, user, password and port.
4. Create the `suivi_habitudes` database and its tables (users, habits, check-ins). *The SQL schema is not included yet.*

### 2. Android app
1. Open the project in Android Studio and let Gradle sync.
2. `BASE_URL` in `app/src/main/java/com/example/suivi_habitudes/api/ApiClient.java` defaults to `http://10.0.2.2/suivi_habitudes/` (the emulator's alias for your computer's localhost). On a physical device, use your computer's LAN IP.
3. Run on an emulator or device.

> The app uses plain HTTP for local development only. Use HTTPS for any real deployment.

## Project structure
- `app/src/main/java/.../activities` – screens
- `app/src/main/java/.../adapters` – RecyclerView adapters
- `app/src/main/java/.../api` – Retrofit client and service
- `app/src/main/java/.../models` – API data models
- `PHP/` – backend endpoints
