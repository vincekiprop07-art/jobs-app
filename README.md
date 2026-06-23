# Jobs App

An Android app (Kotlin) with two sections, reachable from the bottom navigation bar:

1. **Jobs** — live job listings pulled from RemoteOK's free public API (no key required).
2. **CV modifier** — paste a CV and rewrite it into clean, ATS-friendly, globally neutral wording, powered by the Anthropic API.

## How to open and run

1. Install [Android Studio](https://developer.android.com/studio) (latest stable).
2. Open this folder (`JobsApp/`) as a project — `File > Open`, select the folder containing `build.gradle` and `settings.gradle`.
3. Let Android Studio sync Gradle. If prompted to create a Gradle wrapper, accept it.
4. Run on an emulator or a physical device (`Run > Run 'app'`).

Minimum SDK 24 (Android 7.0+), target/compile SDK 34.

## Jobs section

Uses [RemoteOK's](https://remoteok.com/api) public JSON feed via Retrofit. No signup needed. If you want a different or larger job pool, swap `RemoteOkApi` / `RetrofitClient` for another provider (e.g. Adzuna, Jooble, or a paid job board API) — the `Job` model and `JobsAdapter` will need matching field updates.

## CV modifier section

On first use, tap **API key** in the CV modifier screen and paste an Anthropic API key (get one at console.anthropic.com). The key is stored in local `SharedPreferences` only — it never leaves the device except to call the Anthropic API directly.

**Important before shipping this to real users:** storing and using an API key directly on-device works fine for personal use or testing, but it is not secure for a public release — anyone could extract the key from the app and run up your bill. For production, route the CV rewrite request through your own backend server (which holds the real API key) instead of calling Anthropic directly from the app. The `CvRewriteClient.kt` file is written so swapping the URL/auth for your own backend endpoint is a small change.

## Project structure

```
app/src/main/java/com/example/jobsapp/
  MainActivity.kt                 — bottom nav host
  model/Job.kt                    — job data model
  network/RemoteOkApi.kt          — jobs API interface
  network/RetrofitClient.kt       — Retrofit/OkHttp setup
  ui/jobs/JobsFragment.kt         — jobs list screen
  ui/jobs/JobsAdapter.kt          — RecyclerView adapter
  ui/jobs/JobsViewModel.kt        — jobs loading logic
  ui/cv/CvModifierFragment.kt     — CV modifier screen
  ui/cv/CvRewriteClient.kt        — Anthropic API call + JSON parsing
```

## Next steps you might want

- Swap the placeholder app icon/branding for your own.
- Add job search/filtering (RemoteOK supports tag-based filtering via URL params).
- Add a "save job" / favorites feature with local storage (Room database).
- Move the CV rewrite call behind your own backend (see security note above).
