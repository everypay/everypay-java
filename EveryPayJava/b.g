buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:0.12.+'
    }
}

task wrapper(type: Wrapper) {
gradleVersion = '1.12'
}

apply plugin: 'android'


dependencies {
    compile fileTree(dir: 'libs', include: '*.jar')
    compile project(':EveryPay:workspace:EveryPayJava')
    compile project(':android-sdk-linux:extras:android:support:v7:appcompat')
}


android {
    compileSdkVersion 19
    buildToolsVersion "19.1.0"


    defaultConfig {
        applicationId "com.everypay.client"
        minSdkVersion 10
        targetSdkVersion 19
    }

 signingConfigs {
        release {
            storeFile file("keys/everypay.keystore")
            storePassword System.console().readLine("\nKeystore password: ")
            keyAlias "MyReleaseKey"
            keyPassword System.console().readLIne("\nKey password: ")
        }
    }

buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }

    sourceSets {
        main {
            manifest.srcFile 'AndroidManifest.xml'
            java.srcDirs = ['src']
            resources.srcDirs = ['src']
            aidl.srcDirs = ['src']
            renderscript.srcDirs = ['src']
            res.srcDirs = ['res']
            assets.srcDirs = ['assets']
        }

        // Move the tests to tests/java, tests/res, etc...
        //instrumentTest.setRoot('tests')

        debug.setRoot('build-types/debug')
        release.setRoot('build-types/release')
    }
}


