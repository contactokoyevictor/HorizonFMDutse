# Horizon FM Dutse

Android companion app for **Horizon FM 100.5 Dutse** (Radio Nigeria) — live listening and station engagement for audiences in Jigawa State and beyond.

I built this as a native Android client focused on reliable live radio streaming, station content, and social presence in one place.

## What it does

| Area | Details |
|------|---------|
| **Live radio** | Streams Horizon FM over the network with play / pause controls |
| **Background playback** | `MediaPlayerBackgroundService` keeps audio playing while the user navigates the app |
| **Audio focus** | Handles audio focus changes and “becoming noisy” (e.g. headphones unplugged) |
| **Connectivity** | `ConnectivityReceiver` reacts to network changes before / during streaming |
| **Station home** | Home and programme-oriented UI for the station experience |
| **Content & social** | In-app WebViews for blog, news, Facebook, Twitter, LinkedIn, and YouTube |
| **Feedback** | Listener feedback form that sends mail via SMTP |
| **Onboarding** | Branded splash screen into a bottom-navigation shell |

## Architecture

```
app/src/main/java/ng/horizonfm/horizonfm/
├── HorizonFMSplashScreen.java      Launcher / splash
├── MainActivity.java               Bottom navigation host
├── LiveFragment.java               Live stream UI
├── HomeFragment.java / BlogFragment / NewsFragment / MoreFragment
├── FacebookFragment / TwitterFragment / LinkedInFragment / YoutubeFragment
├── FeedbackFragment.java + GmailSender.java
├── MediaPlayerBackgroundService.java
└── ConnectivityReceiver.java
```

- **UI** — Support Library fragments + bottom navigation (`appcompat`, `design`, `constraint-layout`)
- **Playback** — Android `MediaPlayer` in a started `Service`, bound from the UI for control
- **Web content** — `WebView` clients with custom offline / HTTP error pages under `assets/web/`
- **Build** — Gradle Android plugin, `applicationId` `ng.horizonfm.horizonfm`, minSdk 16 / targetSdk 28

## Stack

- **Java** Android application
- **Android Support Library** 28.x
- **Gradle** / Android Studio project layout
- **JavaMail** (bundled jars) for feedback email
- Live stream via an Icecast / shoutcast-style HTTP audio endpoint

## Notes

This is an earlier mobile portfolio project for a public Radio Nigeria FM station. Stream URLs and SMTP credentials belong in local configuration — they are not documented here, and secrets should not be committed.
