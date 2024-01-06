/*
 * Copyright 2024 Oleh Boichuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("io.github.c0nnor263.obfustring-plugin")
}

android {
    compileSdk = ObfustringData.exampleapp.compileSdk
    namespace = ObfustringData.exampleapp.namespace

    defaultConfig {
        applicationId = namespace
        minSdk = ObfustringData.exampleapp.minSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
            testProguardFile("proguard-test-rules.pro")
        }
    }
    testBuildType = "release"
    compileOptions {
        sourceCompatibility = ObfustringData.exampleapp.sourceCompatibility
        targetCompatibility = ObfustringData.exampleapp.targetCompatibility
    }
    kotlin {
        jvmToolchain(ObfustringData.exampleapp.jvmTarget)
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.21")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// obfustring {
//    android.namespace?.let {
//        key = it
//    }
//    loggingEnabled = true
//    mode = ObfustringMode.DEFAULT
//    stringConcatStrategy = StringConcatStrategy.INDY
// }
