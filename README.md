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

## Mock REST API

Loyihada Retrofit mock API ishlatiladi. `Bugun` yoki `Reja` ekranidagi menyudan **"Ma'lumotlarni yangilash (mock API)"** ni bosib, ma'lumotlarni API dan Room bazaga yuklashingiz mumkin.

Mock API tashqi servis talab qilmaydi: so'rovlar `MockApiInterceptor` orqali statik JSON bilan javob qaytaradi.
