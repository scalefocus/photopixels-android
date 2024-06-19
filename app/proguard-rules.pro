# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

##---------------Begin: proguard configuration for Pusher Java Client  ----------
-dontwarn org.slf4j.impl.StaticLoggerBinder
##---------------End: proguard configuration for Pusher Java Client  -----

-dontwarn org.apache.http.conn.ssl.DefaultHostnameVerifier
-dontwarn autovalue.shaded.com.squareup.javapoet.**
-dontwarn com.google.auto.value.processor.**
-dontwarn com.google.auto.value.extension.toprettystring.processor.**
-dontwarn org.apache.http.impl.auth.GGSSchemeBase
-dontwarn org.apache.http.auth.KerberosCredentials
-dontwarn org.apache.http.impl.auth.KerberosScheme
-dontwarn org.apache.http.**

## Proguard rules for Firebase Performance
-keep class com.google.protobuf.** { *; }
-keep class com.google.firebase.perf.** { *; }
-keepnames class com.google.firebase.perf.** { *; }

## FIXME Test without this rule after Compose 1.7.0 is released (now it is 1.6.8)
## Fix crash in release builds due to Google's f*ck up on Compose and Lifecycle Runtime libraries
-keep class androidx.compose.ui.platform.AndroidCompositionLocals_androidKt { *; }
