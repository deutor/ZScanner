# Katalog Wymiany Zadań (agent-tasks)

## Przeznaczenie
Katalog `agent-tasks/` służy do trwałej i audytowalnej wymiany zadań oraz raportów wykonania z agentami AI w repozytorium `ZScanner`.
Katalog `tmp/` nie jest podstawowym kanałem komunikacji (może służyć jedynie do tymczasowych logów i artefaktów testów).

## Schemat Nazewnictwa i Zasady Numeracji
Pliki w katalogu mają format:
- `NNN_krotki_opis.md` - opis zadania zleconego agentowi
- `NNN_gemini_reply.md` - raport z wykonania zadania przez agenta

Gdzie:
- `NNN` to trzycyfrowy numer lokalny dla repozytorium (np. `001`, `002`).
- Przykład: `001_przygotowanie_srodowiska.md` -> `001_gemini_reply.md`.

Zasady:
- Numeracja jest lokalna dla repozytorium ZScanner.
- Istniejących plików nie wolno usuwać ani nadpisywać.
- Domyślnie zadanie dotyczy wyłącznie repozytorium ZScanner. Zadania dotyczące wielu repozytoriów muszą wskazywać wszystkie objęte repozytoria i być koordynowane jawnie.

## Obowiązek Utworzenia Raportu i Format
Po wykonaniu pracy (lub w przypadku niepowodzenia/blokady) agent ma obowiązek zapisać raport `NNN_gemini_reply.md`. Zadanie nie jest uznawane za zakończone bez zapisania tego raportu.

Format raportu:
```markdown
# Raport wykonania zadania NNN

## Status

SUCCESS / PARTIAL / FAILED / BLOCKED

## Podsumowanie

## Zmienione pliki

## Wykonane polecenia

## Testy

## Problemy i ograniczenia

## Zalecane następne kroki
```

## Bezpieczeństwo i Zakaz Sekretów
W plikach zadań, raportach i logach w katalogu `agent-tasks/` kategorycznie zabrania się umieszczania haseł, tokenów, kluczy API ani innych danych poufnych.
Katalog `agent-tasks/` jest śledzony przez system kontroli wersji Git (nie jest dodany do `.gitignore`).

## Praca wyłącznie w bieżącym repozytorium
Agent wykonujący zadanie z tego katalogu pracuje wyłącznie w repozytorium `ZScanner` i nie modyfikuje sąsiednich repozytoriów bez wyraźnego polecenia.
