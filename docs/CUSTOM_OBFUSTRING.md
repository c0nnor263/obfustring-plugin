# Custom Obfustring implementation

### 1. Setup the buildSrc project

- In the root of your project, create a new directory called `buildSrc`. This directory will contain your custom
  obfustring implementation

- Inside the `buildSrc` directory, create a new file called `build.gradle.kts` with the following content:

```kotlin
plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    // Obfustring core dependency
    implementation("io.github.c0nnor263:obfustring-core:$obfustringVersion")
}
```

> [!NOTE]
> If you want to know more about setup the `buildSrc` project, you can read the official documentation [here](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html#sec:build_sources)

### 2. Creating the obfustring implementation

- In the `buildSrc` directory, create the following folder structure:
  `src/main/kotlin/obfustring`
- Inside the `obfustring` directory, create a new Kotlin file with the following content:

```kotlin
package obfustring

import io.github.c0nnor263.obfustringcore.CommonObfustring

object CustomObfustring : CommonObfustring {
    /**
     * Process the string
     *
     * @param key the key for obfuscation
     * @param stringValue the string value to be processed
     * @param mode the mode of obfuscation. [ObfustringCryptoMode.ENCRYPT] or [ObfustringCryptoMode.DECRYPT]
     * @return the processed string
     */
    override fun process(
        key: String,
        stringValue: String,
        mode: Int
    ): String {
        // Your custom obfustring implementation here
        return // Processed obfuscated string
    }
}
```

> [!WARNING]
> - The custom implementation must be an object that implements the `CommonObfustring` interface. This ensures it can be used wherever obfustring logic is required in your project.
> - Make sure you handle edge cases, such as null or empty strings, in your custom obfuscation logic.

### 3. Apply the custom obfustring implementation

- In the `build.gradle.kts` file of your app module, add the following code to apply the custom obfustring
  implementation:

```kotlin

import obfustring.CustomObfustring

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("io.github.c0nnor263.obfustring-plugin")
}

android {
    // ...
}

obfustring {
    customObfustring = CustomObfustring
}
```

### That's it! You have successfully implemented and applied your custom obfustring logic to your project.

Just sync your project and try to build release APK/AAB to see your custom obfustring logic in action.
