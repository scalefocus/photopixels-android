# PhotoPixels for Android

## Releases
 The latest release is available in the *Releases* section.

 **TODO: add link to Releases section / latest release **

## Build Instructions

If you want to build your own version of PhotoPixels, keep in mind the instructions that follow. 

### Application Signing

There is a Gradle convention plugin (**AndroidApplicationSigningConventionPlugin.kt**) that has all
logic related to signing the application. The plugin looks for the **keystore.properties** file in
the project root directory. This file is and should be kept out of Git via **.gitignore**. If
**keystore.properties** exists, then the application will be signed as defined by the
configuration inside **AndroidApplicationSigningConventionPlugin.kt**. If the 
**keystore.properties** file does not exist, then the application will not be signed.

This is how **keystore.properties** looks like:

```
keystorePath=/path/to/keystore/file
keystorePassword=some_good_password
keyAlias=alias_of_key_from_keystore
keyPassword=some_good_password
```

### Enabling Google Photos API

To enable retrieval of personal photos from the Google Photos services, the PhotoPixels app will
need data from *Google Cloud Console*, more specifically, an **Android client ID**, 
a **Web client ID**, and **Web client secret**. 

Once you retrieve these, place them a file called **photopixels.properties** in the root directory
of the project. This is how **photopixels.properties** should look like:

```
# Google Console Android Client ID
GOOGLE_OAUTH_ANDROID_CLIENT_ID="android_client_id_from_google_cloud_console"

# Google Console Android Web ID
GOOGLE_OAUTH_WEB_CLIENT_ID="web_client_id_from_google_cloud_console"
GOOGLE_OAUTH_WEB_CLIENT_SECRET="web_client_secret_from_google_cloud_console"
```

#### Obtaining Properties from *Google Cloud Console* 

**TODO: add instructions/links describing how to obtain properties for photopixels.properties 
file.**

## Firebase Services Configuration

This project already includes the necessary dependencies for the following Firebase services:
* Firebase Crashlytics
* Firebase Performance
* Firebase App Distribution

There is a Gradle conventional plugin called **AndroidFirebaseConventionPlugin.kt**. You may modify
its contents as you see fit for your current project.

To be able to use any of the Firebase services, you need to provide the **google-services.json**
file downloaded from the Firebase Console. Place this file into the **app* module (**app/**
directory) of the project. Once Gradle finds this file, it enables the fore-mentioned Firebase
services. Without this file, all Firebase services will remain disabled.

There may be necessary to provide additional configuration to be able to use the Firebase services.

### Additional Configuration for Firebase App Distribution

Place the following properties in the **local.properties** file in the root of the project:

```
# region Firebase App Distribution properties
# --------------------------------------------------------------------------------------------------
# The path to your service account private key JSON file.
PHOTOPIXELS_FIREBASE_APPDISTRIBUTION_CREDENTIALS_FILE=/path/to/firebase/app/distribution/credentials/file

# The tester groups you want to distribute builds to.
PHOTOPIXELS_FIREBASE_APPDISTRIBUTION_GROUPS=release-group1, release-group2, release-group3

# Specifies your app's file type. Can be set to "AAB" or "APK".
PHOTOPIXELS_FIREBASE_APPDISTRIBUTION_ARTIFACT_TYPE=APK

# Build types: (can be project-specific, especially with product flavors): [release, debug]
PHOTOPIXELS_FIREBASE_APPDISTRIBUTION_BUILD_TYPES=release, debug
# --------------------------------------------------------------------------------------------------
# endregion
```

NOTE: When application product flavors are used, **AndroidFirebaseConventionPlugin.kt** will have to
be revised and modified to support them.
