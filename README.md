# Dare or Double Dare

An offline-first Android party and icebreaker game. Players roll dice to move
around a 40-space board split into four escalating zones (Tease, Tempt, Heat,
After Dark), drawing prompts and dares from an 11-category deck as they go.

## Features

- **Board & dice** — 40-space board, dual dice (category + intensity/movement),
  zone-capped intensity so early turns can't jump straight to the spiciest cards
- **11 card categories, 250+ prompts** — icebreakers, confessions, roleplay,
  sensation, power & control, and more, stored locally in Room
- **Party Mode** (2–8 players) and **Intimacy Mode** (2 players, private
  comfort ceiling, no group-scope cards)
- **Consent-first design**
  - Cards are shown to everyone involved before anyone commits
  - Traffic-light safety system (🟢/🟡/🔴), always one tap away
  - Uncomfortable dares can be swapped or skipped with zero penalty, and are
    muted from future draws per player
- **Hearts** — an uncapped, anonymous reaction players give to standout
  moments (long-press, not a tap) — not a score, not competitive
- **Highlight reel** — end-of-game recap of the most-loved moments instead of
  a leaderboard
- **Custom dares** — players can add their own prompts, stored alongside the
  built-in deck
- **Local-only storage** — everything lives in Room/SQLite on-device; nothing
  is uploaded

## Status

In active development. See `/docs/roadmap.md` (or your issue tracker) for
what's built vs. in progress.

## Tech stack

- Kotlin, Jetpack Compose
- Room (SQLite) for persistence
- MVVM (ViewModel + Repository pattern)

## Setup

1. Clone the repo
2. Open in Android Studio (Giraffe or newer recommended)
3. Sync Gradle, then run on an emulator or device (min SDK: fill in)

## Content note

This app contains adult-oriented content in its Heat / After Dark / Intimacy
categories. An 18+ confirmation gate is required before first use.

## Privacy

All data — profiles, custom dares, game history — is stored locally on the
device. Nothing is sent to a server. `allowBackup` is disabled to prevent
this data from being included in cloud backups.

## License

(fill in — MIT, proprietary, etc.)
