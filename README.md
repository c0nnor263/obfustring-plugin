# Obfustring

This plugin obfuscates your strings

### Setup
You have to apply the Obfustring plugin to the project.

##### build.gradle(Project)
```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'io.github.c0nnor263:plugin:1.5.7'
    }
}

plugins{
    id 'com.android.application'
    id 'io.github.c0nnor263.obfustring-plugin'
}
```

##### build.gradle(Module)
```groovy
obfustring{
    packageKey = "com.conboi.myapplication" // comDconboinmyapplicatio
}

android{
    dependencies {
        implementation("io.github.c0nnor263:obfustring-core:1.5.7")
    }
}
```


Annotate classes with strings that need to be obfuscated with:
```kotlin
@ObfustringThis
```

### Example:

```kotlin

@ObfustringThis
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        "HELLO"
        Log.d("TAG", "onCreate: \n \" binding root ${binding.root} binding def $binding def ")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val sydney = LatLng(-34.0, 151.0)
        "HI"
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney 5"))
    }
}
```

### Output:
```kotlin

@ObfustringThis
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ObfStr("comDconboinmyapplication").v("LWFPG")
//@ | Log.d("TAG", "onCreate: \n \" binding root ${binding.root} binding def $binding def ")
        Log.d(ObfStr("comDconboinmyapplication").v("PIM"), ObfStr("comDconboinmyapplication").v("mzWpqnsq: \n \" twpdtyv jmoa ¦${binding.root}¦ tuabube pre ¦$binding¦ pwt "))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val sydney = LatLng(-34.0, 151.0)
        ObfStr("comDconboinmyapplication").v("LA")
        mMap.addMarker(MarkerOptions().position(sydney).title(ObfStr("comDconboinmyapplication").v("Qmfiqe hz Qmfnpj 5")))
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
