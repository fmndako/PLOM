# PLOM

Personal loan manager
=============================

Android app for managing personal loans, peer to peer lending and adashi (user contributions).

## Building the App

First, clone the repo:

`git clone git@github.com:fmndako/PLOM.git`

Next, you will need to have firebase credential to enable google sign in. Create one via the google developers site if you haven't already.

Open the file `app/src/main/res/values/google.xml` and enter the token for your project, as well as your
collection name.

Building the sample then depends on your build tools.

### Android Studio (Recommended)

(These instructions were tested with Android Studio version 4.1)

* Open Android Studio and select `File->Open...` or from the Android Launcher select `Import project (Eclipse ADT, Gradle, etc.)` and navigate to the root directory of your project.
* Select the directory or drill in and select the file `build.gradle` in the cloned repo.
* Click 'OK' to open the the project in Android Studio.
* A Gradle sync should start, but you can force a sync and build the 'app' module as needed.

### Gradle (command line)

* Build the APK: `./gradlew build`

### Eclipse

* Download the latest Android SDK from [Maven Central](http://repo1.maven.org/maven2/io/keen/keen-client-api-android)
  * Note: We publish both an AAR and a JAR; you may use whichever is more convenient based on your infrastructure and needs.


## Running the Sample App

Connect an Android device to your development machine.

### Android Studio

* Select `Run -> Run 'app'` (or `Debug 'app'`) from the menu bar
* Select the device you wish to run the app on and click 'OK'

