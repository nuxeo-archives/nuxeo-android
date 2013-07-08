# Nuxeo Android projects

## Initialize Maven repository with Android artifacts (with Maven 3)

  git clone https://github.com/mosabua/maven-android-sdk-deployer.git
  mvn install -P4.1

## How to build

  export ANDROID_HOME=/path/to/your/AndroidSDK/containing/platforms/

Using maven 2.2.1 or later, from root folder:

    $ mvn clean install

This will build the sub-modules `nuxeo-automation-thin-client`, `nuxeo-android`, `nuxeo-android-connector`, `nuxeo-automation-sample` and `nuxeo-automation-sample-test`

The produced APKs will be in `nuxeo-android/target/`, `nuxeo-automation-sample/target` and `nuxeo-automation-sample-test/target`

You can work from Eclipse, using Android Development Tools (ADT) but **do not use maven-eclipse-plugin** which badly defines:

 * JRE instead of Android SDK
 * `target/generated-sources/` instead of `src/`

Copy `.project.ok` to `.project` and `.classpath.ok` to `.classpath`, then import the projects into Eclipse.

You can still use maven-eclipse-plugin for the `nuxeo-automation-thin-client` and `nuxeo-android-connector` modules.

    $ mvn eclipse:eclipse -pl nuxeo-automation-thin-client,nuxeo-android-connector

The Maven build uses [maven-android-plugin](http://code.google.com/p/maven-android-plugin/).

Here's an overview of its main targets:

 * `android:deploy`

    Deploys the built apk file, or another specified apk, to a connected device.
    Automatically performed when running `mvn integration-test` (or `mvn install`) on a project with instrumentation tests.

 * `android:deploy-dependencies`

    Deploys all directly declared dependencies of `<type>apk</type>` in this project's pom.
    Usually used in a project with instrumentation tests, to deploy the apk to test onto the device before running the deploying and running the instrumentation tests apk.
    Automatically performed when running mvn integration-test (or mvn install) on a project with instrumentation tests.

 * `android:dex`

    Converts compiled Java classes to the Android dex format.

 * `android:emulator-start`

    EmulatorStartMojo can start the Android Emulator with a specified Android Virtual Device (avd).

 * `android:emulator-stop`

    EmulatorStartMojo can stop the Android Emulator with a specified Android Virtual Device (avd).

 * `android:zipalign`

    ZipalignMojo can run the zipalign command against the apk.

## Modules description

 * Nuxeo Automation Thin Client (`nuxeo-automation-thin-client`)

    It is a fork from [nuxeo-automation-client](http://hg.nuxeo.org/nuxeo/nuxeo-features/file/5.4/nuxeo-automation/nuxeo-automation-client/)
    making it usable with Google Android SDK 2.2 and reducing its dependencies.
    This module only manage ReadOnly access to nuxeo and is depracted : use nuxeo-android-connector.

 * Nuxeo Android simple client (`nuxeo-android`)

    The Nuxeo Android application based on Nuxeo Automation Thin Client and Droid4Me library.
    This module is deprecated since it relies on the deprecated automation-client.

 * Nuxeo Android connector (`nuxeo-android-connector`)

    New connector for using Nuxeo Platform services from Android. It's an alternative library of nuxeo-automation-thin-client that provide more infrastructure and support for Read/Write operation as well as off-line management.

 * Nuxeo Automation sample (`nuxeo-automation-sample`)

    Sample use of the `org.nuxeo.android:nuxeo-android-connector` library.

 * Nuxeo Automation sample test (`nuxeo-automation-sample-test`)

    Test application for Nuxeo Automation sample.

### Nuxeo Android Simple Client

This Android application aims to show application abilities to work with a Nuxeo server using [Nuxeo Automation REST API](http://doc.nuxeo.com/display/NXDOC/Content+Automation).

It is based on [droid4me framework](http://code.google.com/p/droid4me/) provided by [Smart&Soft](www.smartnsoft.com).

WARNING: This code is in beta state (it was the result of two days sprint at Nuxeo).
It's a proof of concept and need some work to provide more useful features.

### Nuxeo Automation sample

Sample use of `org.nuxeo.android:nuxeo-android-connector` library.

## About Nuxeo

Nuxeo provides a modular, extensible Java-based [open source software platform for enterprise content management] [1] and packaged applications for [document management] [2], [digital asset management] [3] and [case management] [4]. Designed by developers for developers, the Nuxeo platform offers a modern architecture, a powerful plug-in model and extensive packaging capabilities for building content applications.

[1]: http://www.nuxeo.com/en/products/ep
[2]: http://www.nuxeo.com/en/products/document-management
[3]: http://www.nuxeo.com/en/products/dam
[4]: http://www.nuxeo.com/en/products/case-management

More information on: <http://www.nuxeo.com/>
