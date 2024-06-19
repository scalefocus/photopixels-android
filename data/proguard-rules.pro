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

#Google Sign in
-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}
-dontwarn autovalue.shaded.com.squareup.javapoet.**
# Keep all classes from the javax.naming package
-keep class javax.naming.** { *; }

# Keep all classes from the org.ietf.jgss package
-keep class org.ietf.jgss.** { *; }


# gRPC libraries -> Used for Google Photos
-keep class io.grpc.** { *; }
-dontwarn io.grpc.**

-keep class com.google.common.util.concurrent.** { *; }
-dontwarn com.google.common.util.concurrent.**

-keep class io.perfmark.** { *; }
-dontwarn io.perfmark.**

-keep class io.opencensus.** { *; }
-dontwarn io.opencensus.**

-keep class io.netty.** { *; }
-dontwarn io.netty.**

-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

-keep class io.netty.internal.tcnative.** { *; }

-keep class javax.lang.model.** { *; }

-keep class javax.naming.** { *; }

-keep class org.apache.log4j.** { *; }
-keep class org.apache.logging.log4j.** { *; }

-keep class org.eclipse.jetty.alpn.** { *; }
-keep class org.eclipse.jetty.npn.** { *; }

-keep class org.ietf.jgss.** { *; }

-keep class reactor.blockhound.integration.** { *; }


# Google Photos Library Client
-keep public class com.google.photos.library.v1.PhotosLibrarySettings {
  public *;
}

-keep public class com.google.photos.library.v1.PhotosLibraryClient {
  public *;
}

-keep public class com.google.api.gax.core.FixedCredentialsProvider {
  public *;
}

-keep class com.google.photos.types.proto.** { *; }
-dontwarn com.google.photos.types.proto.**

-keep class com.google.protobuf.** { *; }
-dontwarn com.google.protobuf.**

# Keep specific public classes within Google Photos Library
-keep public class com.google.photos.library.** {
  public * getId();
  public * setId();
  public * getName();
  public * getMediaItems();
  public * createMediaItem();
  public * public*;
}

# Keep specific annotations used by Google Photos Library
-keep @com.google.photos.library.** class * { *; }

# Keep Google Photos Library Client classes
-keep class com.google.photos.library.client.** { *; }

# Keep necessary Google API Client classes
-keep class com.google.api.client.http.** { *; }
-keep class com.google.api.client.json.** { *; }
-keep class com.google.api.client.util.** { *; }

# Keep Google OAuth2 Client classes
-keep class com.google.auth.oauth2.** { *; }

# Keep the Google Photos Library Client annotations
-keep @com.google.photos.library.v1.PhotosLibraryClient class * { *; }
-keep @com.google.photos.library.v1.PhotosLibrarySettings class * { *; }

