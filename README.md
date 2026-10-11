# LOUD Soundboard (Android starter project)

A small original Android soundboard app with a dark, Discord-inspired layout:
- Import audio from the phone using Android's audio picker
- Play and stop imported sounds
- Open the Discord app / Discord web app

## Important limitation
This app plays sound locally on the phone. Android does not generally let a regular app inject arbitrary audio directly into Discord's microphone/voice-chat stream. For everyone in a Discord voice channel to hear a sound, you will need a Discord voice bot hosted separately (or a supported audio-routing setup). This starter project does not include a bot or server.

## Build an APK
1. Install Android Studio: https://developer.android.com/studio
2. Extract this ZIP.
3. In Android Studio choose **Open** and select the `LOUDSoundboardProject` folder.
4. Let Gradle sync finish. Use JDK 17 if Android Studio asks for a Gradle JDK.
5. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. Android Studio will show a link to the generated APK.

## Test
Install the APK on your Android phone, open LOUD, tap **ADD YOUR AUDIO**, select an audio file, then tap **PLAY**.

This is an original starter app, not a cracked copy of any existing APK.
