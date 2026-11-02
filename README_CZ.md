# MP-TUBE 1.1 — Created by Marek Petr

Aktualizace existujícího Android projektu `cz.marekpetr.mptube`.
Projekt je určen k sestavení pomocí původního Codemagic workflow.

## Co obsahuje
- Zlatá animovaná úvodní obrazovka při každém spuštění: **Created by Marek Petr**
- Vestavěný webový přehrávač mobilního YouTube
- Vyhledávání, domů, zpět, vpřed, obnovení stránky
- Přehrávání HTML5 videa na celou obrazovku
- Volitelný jednoduchý filtr známých reklamních domén (bez záruky odstranění reklam)
- Uložení nastavení filtru v telefonu
- Tmavé rozhraní se zlatým označením autora

## Důležité omezení
Toto je **WebView aplikace**, nikoliv plnohodnotný klon NewPipe.
Neobsahuje nezávislý extraktor YouTube streamů, stahování videí,
přehrávání na pozadí ani spolehlivé odstranění reklam.
YouTube může omezit přehrávání nebo přihlašování ve WebView.

## Sestavení
Nahraj soubory ZIP do kořene původního repozitáře MP-TUBE,
přepiš stejnojmenné soubory a spusť existující workflow
`mp-tube-android` v Codemagic. Výstup: `app-debug.apk`.

**Poznámka:** APK nebylo v tomto prostředí sestaveno ani otestováno.
