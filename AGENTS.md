# Tyzenhaus

This file provides guidance to AI coding agents when working with code in this repository.

## What This Is

Tyzenhaus is a shared-expenses tracking Telegram bot.
It runs in group chats, records who paid for what, and simplifies multi-currency debts.
It is deployed on Fly.io with a PostgreSQL database.

## Repository Layout

This is a Gradle multi-project build with a separate Node/Yarn frontend:

- `:i18n` — localized string bundles (`.properties` files + Crowdin)
- `:entity` — Kotlin data models with `kotlinx.serialization`
- `:repository` / `:repository:postgresql` — repository interfaces + PostgreSQL implementation with Liquibase migrations
- `:core` — business logic: debt simplification, Telegram update processing pipeline
- `:launcher:fly` — Ktor application entry point (webhook, Mini App API, metrics)
- `mini-app/` — React/TypeScript Telegram Mini App (built separately with Yarn)
- `policies/` — privacy policy built from Asciidoctor source
- `buildSrc/` — shared Gradle build logic and version constants

## Architecture

### Telegram Update Flow

Telegram sends webhook POSTs to Ktor → `UpdateProcessor` dispatches to an `UpdateProcessingPipeline` — an ordered chain of handlers registered in the Koin DI module.
Each handler decides whether to handle or pass through the update.
Handlers cover `/start`, `/lang`, `/help`, multi-step expense entry dialogs, and more.
`DialogState` persisted in Postgres tracks where a user is in a multi-step flow.

### Debt Calculation

`DebtsCalculator` (`:core`) simplifies a list of transactions into the minimum number of transfers, handling multiple currencies.
It operates purely on in-memory data from the `Balance` entity.

### Data Layer

All persistence goes through interfaces in `:repository`; the only implementation is `:repository:postgresql`.
Liquibase changelogs live in `repository/postgresql/src/main/resources/db/changelog/`.
The `Balance` column is stored as JSONB (a nested map of currency → user → amount).

### Ktor Application (launcher/fly)

Entry point is `Application.kt` in `:launcher:fly`.
Koin modules are split by concern: `json`, `db`, `services`, `telegram`, `pipeline`, `config`.
The app exposes:
- Port **5000**: Telegram webhook + Mini App API + static Mini App files
- Port **5001**: management/metrics (Prometheus via Micrometer)

### Mini App

Two Webpack entry points (`expense.tsx`, `history.tsx`) produce separate HTML pages served as static files by Ktor.
The Mini App communicates with the backend via the Mini App API routes.
i18next handles localization mirroring the bot's i18n strings.

## Key Configuration

- **Versions**: `gradle/libs.versions.toml` (libraries) + `buildSrc/src/main/kotlin/Versions.kt` (build tooling)
- **Detekt rules**: `detekt.yml` at the repo root
- **Application config**: `launcher/fly/src/main/resources/application.conf` — reads `DATABASE_URL` and `TELEGRAM_TOKEN` from environment
- **Fly.io**: `launcher/fly/fly.toml` — primary region Frankfurt, health check on TCP:5000
- **Translations**: `crowdin.yml` — Crowdin syncs `i18n/src/main/resources/i18n*.properties`

## CI/CD

- **`default.yml`**: runs on all branches except `master` — lint, unit tests, DB tests, Mini App build, coverage upload to CodeCov
- **`master.yml`**: deploys to Fly.io, pushes Docker image to GHCR (`ghcr.io/madhead/tyzenhaus`), sets Telegram webhook
- **`backup.yml`**: daily at 03:00 UTC — dumps DB tables to a Telegram chat via WireGuard tunnel
