# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\UK2016\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.safesoft.proapp.distribute.postData.** { *; }

-keep class org.firebirdsql.** { *; }
-dontwarn org.firebirdsql.**

# Keep @Subscribe methods AND their declaring classes (names preserved).
# This is required so AndroidXSafeSubscriberIndex can identify app classes
# by their package prefix "com.safesoft.proapp.distribute" and let EventBus
# scan them by reflection, while skipping obfuscated AndroidX/framework
# classes that would trigger NoClassDefFoundError on API < 31.
-keepclasseswithmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}

-dontwarn java.applet.Applet
-dontwarn org.apache.harmony.kernel.vm.VM
-dontwarn org.apache.harmony.security.x509.DNParser
-dontwarn org.apache.log4j.xml.DOMConfigurator

# Fix crash NoClassDefFoundError: android.app.PictureInPictureUiState on API < 31.
# EventBus 3.3.1's SubscriberMethodFinder calls Method.getParameterTypes() on all
# methods in the subscriber class hierarchy (including ComponentActivity).
# ComponentActivity.onPictureInPictureUiStateChanged(PictureInPictureUiState)
# references a class only available from API 31, so getParameterTypes() throws
# NoClassDefFoundError on API 30 and below. The app does not use
# Picture-in-Picture, so this framework callback is safe to strip.
-assumenosideeffects class androidx.activity.ComponentActivity {
    public void onPictureInPictureUiStateChanged(android.app.PictureInPictureUiState);
}

# ---------------------------------------------------------------------------
# WorkManager + Room (R8 / minification).
# WorkManager creates its Room database implementation
# (androidx.work.impl.WorkDatabase_Impl) through reflection
# (Class.getDeclaredConstructor()). R8 full mode may remove the no-argument
# constructor, which then throws:
#   NoSuchMethodException: androidx.work.impl.WorkDatabase_Impl.<init> []
# at startup (androidx.startup.InitializationProvider).
# Keep the generated Room database implementations and their constructors.
# ---------------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase {
    <init>();
}
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>();
}
-keep class androidx.work.impl.WorkDatabase_Impl { *; }
-keep class androidx.work.impl.WorkDatabase { *; }
