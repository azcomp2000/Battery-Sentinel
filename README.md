# Battery Sentinel (Project: Battery Helper)

![Android](https://img.shields.io/badge/Platform-Android-brightgreen.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

[**English**](#english) | [**Русский**](#русский)

---

## English

**Battery Sentinel** is a lightweight Android tool for deep battery monitoring without Root privileges. 

### 🛠 Key Features
- **Real-time mAh Tracking:** Accurate calculation of current capacity and consumption in milliampere-hours (mAh) using system counters.
- **No-Root Analytics:** Collects app usage and battery drain statistics without system modification.
- **CRT Aesthetic UI:** Retro-terminal style interface with CRT scanline effects and beveled design.
- **Smart Refresh:** Automatic data updates every 30 seconds with intelligent pausing when the screen is off.

### 🏗 Tech Stack
- **UI:** Jetpack Compose
- **Database:** Room (SQLite)
- **Concurrency:** Kotlin Coroutines & Flow
- **Architecture:** MVVM (Model-View-ViewModel)

### 🚀 Build
To build the project:
1. Open the project in **Android Studio (Ladybug or newer)**.
2. Wait for Gradle sync to complete.
3. Build via: `Build > Build Bundle(s) / APK(s) > Build APK(s)`.

---

## Русский

**Battery Sentinel** — это легковесный инструмент для глубокого мониторинга состояния аккумулятора Android без использования Root-прав. 

### 🛠 Ключевые особенности
- **Отслеживание mAh в реальном времени:** Точный расчет текущей емкости и потребления в миллиампер-часах (mAh) на основе системных счетчиков.
- **Аналитика без Root:** Сбор статистики использования приложений и разряда батареи без вмешательства в систему.
- **CRT интерфейс:** Ретро-стиль терминала с эффектами сканирования ЭЛТ и «выпуклым» дизайном (Beveled).
- **Умное обновление:** Автоматическое обновление данных каждые 30 секунд с остановкой процесса при выключенном экране.

### 🏗 Технологический стек
- **UI:** Jetpack Compose
- **БД:** Room (SQLite)
- **Многопоточность:** Kotlin Coroutines & Flow
- **Архитектура:** MVVM (Model-View-ViewModel)

### 🚀 Сборка
Для сборки проекта:
1. Откройте проект в **Android Studio (Ladybug или новее)**.
2. Дождитесь завершения синхронизации Gradle.
3. Соберите через: `Build > Build Bundle(s) / APK(s) > Build APK(s)`.

---
*Created by DarkMByte as part of the Sovereign Engineering Initiative.*
