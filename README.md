# ARMIN DECK X — Android starter

A native Java Android project with a dark/neon dashboard, installed-app launcher list, local profile settings, local tournament records, and GitHub Actions debug APK workflow.

## Build on GitHub using a phone
1. Open the repository in GitHub in your browser.
2. Upload the *contents* of this project folder to the repository root (keep `.github/workflows/build-apk.yml` at that exact path).
3. Commit to `main` (or `master`).
4. Open **Actions** → **Build ARMIN DECK X APK** → choose **Run workflow** if it did not start automatically.
5. Open the successful run, scroll to **Artifacts**, and download `ARMIN-DECK-X-debug-apk`.
6. Extract the downloaded artifact ZIP on your phone, tap `app-debug.apk`, and allow installation from that browser/file manager if Android asks.

## Toolchain
- Android Gradle Plugin 8.6.1
- Gradle 8.7 recommended
- JDK 17
- compileSdk 35 / build-tools 35.0.0
- minSdk 23

## Important status
This is an initial implementation scaffold, not a verified release. A successful build has not been run in this environment. Tournament storage and first-round pairing are implemented, but full multi-round winner propagation, rankings, editing, and backup/restore are not complete yet. Do not treat it as meeting every item in the master prompt until these are implemented and tested.

## Android package visibility
The manifest declares launcher-intent queries rather than `QUERY_ALL_PACKAGES`. Android may still filter some apps, and Play distribution policies can restrict broad app inventory. This app does not request storage, network, or account permissions.

## Debug vs release
The workflow builds a debug-signed APK suitable for personal testing. For a public release, configure a release signing key as encrypted GitHub Actions secrets and never commit the keystore or passwords. A release build has not been configured here.
