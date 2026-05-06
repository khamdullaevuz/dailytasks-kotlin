# DailyTasks

Kundalik ishlarni (vazifalarni) **rejalashtirish** va **monitoring** (bajarilganini belgilash, statistika) qilish uchun Android mobil ilova.

## Imkoniyatlar

- **Bugun**: bugungi vazifalar, muddati o‘tganlar, rejasiz vazifalar
- **Reja**: rejalashtirilgan (kelajakdagi) vazifalar ro‘yxati
- **Monitoring/Statistika**: so‘nggi 7 kun bo‘yicha bajarilgan va rejalashtirilgan vazifalar soni
- **Vazifa qo‘shish/tahrirlash**: nom, tavsif, sana (DatePicker)

## Texnologiyalar

- Kotlin + Jetpack Compose (Material 3)
- Navigation Compose
- Room (lokal baza)
- MVVM (ViewModel + StateFlow)

## Ishga tushirish

Terminalda (macOS/zsh):

```zsh
cd /Users/elbek/AndroidStudioProjects/DailyTasks
./gradlew :app:assembleDebug
```

Unit testlar:

```zsh
./gradlew :app:testDebugUnitTest
```

> Eslatma: loyiha `minSdk=24` bo‘lgani uchun `java.time` ishlashi uchun core library desugaring yoqilgan.

# dailytasks-kotlin
