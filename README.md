# Obfustring

This plugin obfuscates your strings

### Setup
You have to apply the Obfustring plugin to the project.

DSL
```kotlin
plugins {
  id("io.github.c0nnor263.obfustring") version "1.0.2"
}

```

Legacy
```groovy
buildscript {
  repositories {
    maven {
      url = uri("https://plugins.gradle.org/m2/")
    }
  }
  dependencies {
    classpath("io.github.c0nnor263:plugin:1.0.2")
  }
}

plugins{
    id 'com.android.application'
    id 'io.github.c0nnor263.obfustring'
}
```

Annotate classes with strings that need to be obfuscated with: 
```kotlin
@Obfustring
```

### Example:

```kotlin
val TAG = "FirstFragmentTAG"

@Obfustring
class FirstFragment : Fragment(R.layout.fragment_first) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
    }
}
```

### Output:
```kotlin

val TAG = ObfustringEncoder().vigenere("JufqfYqmyydizHNV")

@Obfustring
class FirstFragment : Fragment(R.layout.fragment_first) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, ObfustringEncoder().vigenere("mzPgqjHdwmszj"))
    }
}

```

### License
    Copyright 2022 Boichuk Oleh

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
