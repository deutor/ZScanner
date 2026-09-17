# Zadanie 001: wysyłanie requestId/commandId do ZServer4MC

## Zakres repozytoriów

Zadanie jest częścią skoordynowanej zmiany z:

- `ZServer4MC/agent-tasks/015_request_id_command_id.md`,
- `ZWMS-DB/agent-tasks/003_app_command_idempotency.md`.

Możesz modyfikować wyłącznie repozytorium `ZScanner`. ZScanner pozostaje cienkim klientem: nie dodawaj tu walidacji stanu magazynu, deduplikacji biznesowej ani dostępu do bazy.

## Cel

ZScanner ma wysyłać identyfikator logicznej akcji użytkownika oraz identyfikator każdej próby transportowej:

- `commandId` — stały dla jednej akcji użytkownika i zachowywany przy ponowieniu tej samej akcji,
- `requestId` — nowy dla każdej próby HTTP, do korelacji logów.

## Wymagania

1. Rozszerz `MainRequestData` o pola JSON `commandId` i `requestId` oraz odpowiednie metody dostępu.
2. Zaktualizuj centralny mechanizm wysyłania żądań w `MainActivity`, tak aby wszystkie ścieżki (`prepare`, `go`, `back`, akcje z pickera i menu) miały prawidłowy `requestId`.
3. Dla `go` i `back` utwórz `commandId` przy rozpoczęciu logicznej akcji i zachowaj go do czasu otrzymania odpowiedzi kończącej tę akcję. Jeśli transport wymaga ponowienia, ponowienie musi użyć tego samego `commandId`, ale nowego `requestId`.
4. Nie generuj nowego `commandId` dla każdego automatycznego retry tego samego żądania.
5. Zabezpiecz interfejs przed przypadkowym równoległym wysłaniem tej samej akcji, ale nie próbuj rozstrzygać po stronie klienta, czy operacja magazynowa jest dopuszczalna — to pozostaje odpowiedzialnością ZServer4MC.
6. Dla `prepare` nie wymagaj `commandId`, jeżeli serwer traktuje tę akcję jako read-only; nadal wysyłaj `requestId`.
7. Zaktualizuj odbiór odpowiedzi tak, aby tolerował i przechowywał zwracane `requestId`, `commandId` oraz ewentualną informację o replay. Nie uzależniaj działania UI od pól opcjonalnych.
8. Zachowaj kompatybilność z istniejącym JSON i nie zmieniaj logiki ekranów ani parametrów workflow.
9. Dodaj testy jednostkowe dla serializacji oraz generowania i ponawiania identyfikatorów. Jeśli obecna struktura na to pozwala, dodaj test ścieżki `go`/`back` bez uruchamiania bazy.
10. Zaktualizuj dokumentację protokołu klient-serwer, jeśli istnieje.

## Raport

Utwórz `ZScanner/agent-tasks/001_gemini_reply.md` zgodnie z formatem repozytorium. Nie wykonuj commita ani push.
