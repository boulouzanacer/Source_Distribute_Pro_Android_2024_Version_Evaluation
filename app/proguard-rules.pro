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

-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}

-dontwarn java.applet.Applet
-dontwarn org.apache.harmony.kernel.vm.VM
-dontwarn org.apache.harmony.security.x509.DNParser
-dontwarn org.apache.log4j.xml.DOMConfigurator
