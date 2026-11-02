# MP Tube 1.0 – Created by Marek Petr

**Samostatné nativní aplikace** pro Android a iPhone. Nejde o stránku v Brave ani PWA. Projekt obsahuje dva odlišné klienty, které zobrazují web YouTube ve vestavěném prohlížeči a umí blokovat síťové požadavky na omezený seznam známých reklamních a měřicích domén.

## Co je hotové
- Mobilní YouTube v samostatném prohlížeči, vyhledávání a zadávání URL
- Domů / zpět / vpřed / obnovit
- Tmavé rozhraní a autorství **Created by Marek Petr**
- Přepínač filtru, zapamatování nastavení i po zavření
- Android: podpora celoobrazovkového HTML5 videa
- iPhone: filtr přes WebKit Content Rule Lists

## Co není ověřeno

Žádná APK ani IPA **zatím není sestavena a otestována na telefonu**. YouTube může reklamy načítat ze stejných domén jako přehrávač, takže **neexistuje záruka, že zmizí reklamy před videem nebo uvnitř videa**. Může se nepodařit přihlášení ke Google účtu v rámci WebView/WKWebView; přehrávání při zamknutém telefonu není zajištěno. Nejde o plnohodnotný Brave Shields. Neobsahuje YouTube API ani stahování chráněného obsahu.

## Vytvoření APK – Windows

1. Nainstaluj Android Studio (software potřebuje internet pro stažení SDK a Gradle).
2. Zvol **Open** a otevři složku `android` (ne celý ZIP).
3. Po synchronizaci zvol **Build → Build Bundle(s) / APK(s) → Build APK(s)** (názvy menu se mohou v jednotlivých verzích lišit).
4. Soubor vznikne v `android/app/build/outputs/apk/debug/app-debug.apk`.
5. Přenes APK do telefonu, povol instalaci z daného zdroje a nainstaluj.

Na Windows zde nemám Android SDK, takže APK nemohu sestavit přímo. Aplikace je navržena pro Android 8.0 a vyšší.

## Vytvoření aplikace pro iPhone

Potřebuješ Mac, Xcode, Apple účet pro podpis a `xcodegen` (`brew install xcodegen`). Ve složce `ios` spusť `xcodegen generate`, otevři vzniklý `MPTube.xcodeproj`, nastav Signing Team a spusť na iPhonu. Pro veřejnou distribuci platí další pravidla Apple.

## Poznámka k reklamám

Filtr je úmyslně konzervativní, aby co nejméně narušoval přehrávání. Záměrné obcházení reklam na YouTube může být v rozporu s podmínkami služby. Filtr lze v aplikaci vypnout.

**Verze zdrojů:** 1.0 • **Vyrobil:** Marek Petr
