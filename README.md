# Battery Sentinel (Project: Battery Helper)

![Android](https://img.shields.io/badge/Platform-Android-brightgreen.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

**Battery Sentinel** — это легковесное Android-приложение для глубокого мониторинга состояния аккумулятора без использования Root-прав. 

## 🛠 Ключевые особенности
- **Real-time mAh Tracking:** Расчет текущей емкости и потребления в миллиампер-часах (mAh) на основе системных счетчиков.
- **No-Root Analytics:** Сбор статистики использования приложений и разряда батареи без вмешательства в систему.
- **CRT Aesthetic UI:** Интерфейс в стиле ретро-терминалов с эффектом электронно-лучевой трубки и скошенными гранями (Beveled Design).
- **Smart Refresh:** Автоматическое обновление данных каждые 30 секунд с интеллектуальной остановкой при выключенном экране.

## 🏗 Технологический стек
- **UI:** Jetpack Compose
- **Database:** Room (SQLite)
- **Concurrency:** Kotlin Coroutines & Flow
- **Architecture:** MVVM (Model-View-ViewModel)

## 🚀 Сборка
Для сборки проекта выполните следующие шаги:
1. Откройте проект в **Android Studio (Ladybug или новее)**.
2. Дождитесь завершения синхронизации Gradle.
3. Запустите сборку через меню: `Build > Build Bundle(s) / APK(s) > Build APK(s)`.

Или через терминал (если у вас установлен Gradle):
```bash
gradle assembleDebug
```

## 📜 Лицензия
MIT License. Свободно для использования и модификации.

---
*Created by DarkMByte as part of the Sovereign Engineering Initiative.*
