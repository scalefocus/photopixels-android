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


# Keep all classes from the javax.naming package
-keep class javax.naming.** { *; }

# Keep all classes from the org.ietf.jgss package
-keep class org.ietf.jgss.** { *; }

# Keep the JWT class and its public members
-keep public class com.auth0.android.jwt.JWT { *; }

# Exclude internal (package-private) classes
-keep public class com.auth0.android.jwt.** { *; }

# Keep all public classes and methods from AppAuth
-keep public class com.auth0.android.auth.** { *; }

# Exclude internal (package-private) classes
-keep public class com.auth0.android.auth.** { *; }

