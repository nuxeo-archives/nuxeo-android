# Nuxeo Android Simple Client

This Android application aims to show application abilities to work with a Nuxeo server using Nuxeo Automation REST API.
http://doc.nuxeo.com/display/NXDOC/Content+Automation
It is based on droid4me framework provided by Smart&Soft (www.smartnsoft.com).
http://code.google.com/p/droid4me/

## Current state

WARNING: This code is in alpha state (result of 2 days sprint at Nuxeo).
It's a proof of concept and need some work to provide useful features.

## How to build

export ANDROID_HOME=/path/to/your/AndroidSDK/containing/platforms/

Using maven 2.2.1 or later, from root folder:

    $ mvn clean install

This will build the two sub-modules `nuxeo-automation-thin-client` and `nuxeo-android`.

The produced APK will be in `nuxeo-android/target/`

You can work from Eclipse, using Android Development Tools (ADT) but do not use maven-eclipse-plugin which badly defines:
 * JRE instead of Android SDK
 * target/generated-sources/ instead of src/
You will have to manually configure the Eclipse projects:
 * set Android SDK for compilation for both modules
 * make `nuxeo-android` depends on `nuxeo-automation-thin-client`
 * make `nuxeo-android` depends on `libs/com.smartnsoft.droid4me.jar`

Maven build uses maven-android-plugin (http://maven-android-plugin-m2site.googlecode.com/svn/plugin-info.html).
Here's an overview of its main targets:
 * android:deploy
        Deploys the built apk file, or another specified apk, to a connected device.
        Automatically performed when running mvn integration-test (or mvn install) on a project with instrumentation tests.
 * android:deploy-dependencies
        Deploys all directly declared dependencies of <type>apk</type> in this project's pom.
        Usually used in a project with instrumentation tests, to deploy the apk to test onto the device before running the deploying and running the instrumentation tests apk.
        Automatically performed when running mvn integration-test (or mvn install) on a project with instrumentation tests.
 * android:dex
        Converts compiled Java classes to the Android dex format.
 * android:emulator-start
        EmulatorStartMojo can start the Android Emulator with a specified Android Virtual Device (avd).
 * android:emulator-stop
        EmulatorStartMojo can stop the Android Emulator with a specified Android Virtual Device (avd).
 * android:zipalign
        ZipalignMojo can run the zipalign command against the apk.

## Modules description
 * Nuxeo Automation Thin Client
 It is a fork from nuxeo-automation-client (http://hg.nuxeo.org/nuxeo/nuxeo-features/file/5.4/nuxeo-automation/nuxeo-automation-client/)
 making it usable with Google Android SDK 2.2 and to reduce its dependencies. At the end, it would be nicer to have nuxeo-automation-client depends on such a light module.
 * Nuxeo Android simple client
 The Nuxeo Android application based on Nuxeo Automation Thin Client and Droid4Me library.

## About Nuxeo

Nuxeo provides a modular, extensible Java-based [open source software platform for enterprise content management](http://www.nuxeo.com/en/products/ep) and packaged applications for [document management](http://www.nuxeo.com/en/products/document-management), [digital asset management](http://www.nuxeo.com/en/products/dam) and [case management](http://www.nuxeo.com/en/products/case-management). Designed by developers for developers, the Nuxeo platform offers a modern architecture, a powerful plug-in model and extensive packaging capabilities for building content applications.

More information on: <http://www.nuxeo.com/>
