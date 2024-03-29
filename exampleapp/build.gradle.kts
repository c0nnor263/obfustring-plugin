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
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
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
                "proguard-rules.pro"
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
    implementation(libs.bundles.android.example.app)
}