NAWAB RIDE V3 - ANDROID PROJECT
This is Android Studio source code, NOT an APK.
To build: open this folder in Android Studio, allow Gradle sync and Android SDK 35 install, then Build > Build APK(s).
APK will appear at app/build/outputs/apk/debug/app-debug.apk.
Offline storage: WebView localStorage; backup using JSON regularly.
Note: File downloads from inside WebView may require additional native DownloadManager integration; the existing CSV/JSON export buttons are designed for browsers and may not download in this APK wrapper.
