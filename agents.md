# ZScanner Agent Guidelines

## 1. Scope & Responsibilities
1. ZScanner is responsible for:
   - Communication with ZServer4MC.
   - Creating UI and displaying data received from ZServer4MC
   - Gathering user input and sending back to ZServer4MC        
2. ZScanner must remain a thin client. Business logic must reside on the server side (ZServer4MC). ZScanner do not have a direct access to ZWMS-DB.
3. Do not modify other repositories (e.g. ZServer4MC, ZIntServer, ZFront, ZWMS-DB) unless explicitly requested.

## 2. General Development Rules
1. Prefer simple, explicit Java implementations.
2. Do not introduce new dependencies without explaining why they are required.
3. Do not modify or refactor unrelated code.

## 3. Agent Task File Communication Standard
1. The repository uses `agent-tasks/` as a durable and auditable exchange directory for tasks and reports. Do not use `tmp/` as the primary task communication channel.
2. Before starting work, the agent MUST read the specified task file in `agent-tasks/` (e.g. `agent-tasks/NNN_krotki_opis.md`).
3. The agent MUST limit its work strictly to the scope described in the task.
4. Upon completing work, the agent MUST create a corresponding reply report file named `agent-tasks/NNN_gemini_reply.md`.
5. The reply report MUST be created regardless of outcome (whether `SUCCESS`, `PARTIAL`, `FAILED`, or `BLOCKED`).
6. The agent MUST NOT consider a task completed without saving the reply report file.
7. The agent MUST NOT perform Git commits unless explicitly instructed by the task specification.
8. The reply report MUST contain:
   - Execution status (`SUCCESS` / `PARTIAL` / `FAILED` / `BLOCKED`)
   - Podsumowanie (Summary)
   - Zmienione pliki (List of modified, added, and deleted files)
   - Wykonane polecenia (Executed commands)
   - Testy (Executed tests and results)
   - Problemy i ograniczenia (Issues and limitations)
   - Zalecane następne kroki (Recommended next steps)
9. The agent MUST NOT modify files in other repositories unless explicitly requested.
10. The agent MUST NOT delete or overwrite existing task reports.
11. The agent MUST NOT place passwords, API keys, tokens, or sensitive credentials in prompts, reports, or logs.

### Report Format Template

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
