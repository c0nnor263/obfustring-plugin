# Obfustring

This is a Android Gradle plugin that obfuscates strings in Kotlin classes

## Setup

You have to apply the Obfustring plugin to the project.

> [!WARNING]
> Starting from JDK 9, string concatenation uses [invokedynamic instruction](https://www.baeldung.com/java-string-concatenation-invoke-dynamic)
> by default.
> For this reason, the Obfustring plugin must use StringConcatFactory strategies, which use the old
> method of string concatenation via StringBuilder.
>
> If you are developing a big project, you may encounter significant performance issues compared to
> the optimized invokedynamic instruction

##### build.gradle(Project)

```kotlin
buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("io.github.c0nnor263:obfustring-plugin:$version")
    }
}
```

##### build.gradle(Module)

```kotlin
plugins {
    id("com.android.application")
    id("io.github.c0nnor263.obfustring-plugin")
}

obfustring {
    /**
     * Key used to obfuscate strings
     */
    key = "exampleKey"

    /**
     * Configure obfustring mode using [ObfustringMode]
     */
    mode = ObfustringMode.DEFAULT

    /**
     * Enable logging
     */
    loggingEnabled = true

    /**
     * Set JVM argument -Xstring-concat using [StringConcatStrategy]
     */
    stringConcatStrategy = StringConcatStrategy.INDY
}
```

> [!TIP]
> For more information check source code of `ObfustringExtension`

## Usage

Annotation `@ObfustringThis` is used to mark classes and fields that should be obfuscated

```kotlin
@ObfustringThis
class MyApplication
```

> [!NOTE]
> If you want to obfuscate all classes you can edit the plugin configuration and
> set `mode = Obfustring.FORCE`

## Example:

```kotlin

@ObfustringThis
class MyApplication : Application() {
    companion object {
        private const val TAG = "MyApplication"
        val username = "user#${Random.nextInt()}"
        val onCreateMsg = "Hello world and $username!"
    }

    override fun onCreate() {
        super.onCreate()
        val userChecker = UserChecker()
        val isValidUserMsg =
            if (userChecker.isValidName(username)) {
                onCreateMsg
            } else {
                "$username is not valid user name"
            }

        Log.i(
            TAG,
            "Application onCreate: $isValidUserMsg",
        )
    }
}

@Suppress("DEPRECATION")
@ObfustringThis
class UserChecker {
    companion object {
        @Deprecated("This is a deprecated list")
        private val forbiddenNames = listOf("admin", "root", "user")
    }

    fun isValidName(name: String): Boolean {
        return when {
            name.isBlank() -> false
            name.isEmpty() -> false
            forbiddenNames.contains(name) -> false
            else -> true
        }.also { result ->
            Log.i(
                "TAG",
                "\tisValidName: $name is $result\n" +
                        "\tAll forbidden names: $forbiddenNames",
            )
        }
    }
}
```

### Output:

```java
public final class MyApplication extends Application {
    public static final C0501a Companion = new Object();
    private static final String TAG = "MyApplication";
    private static final String onCreateMsg;
    private static final String username;

    static {
        String m398a = AbstractC0520b.m398a("wgqs#");
        AbstractC0700e.f1984a.getClass();
        String str = m398a + AbstractC0700e.f1985b.mo0a().nextInt();
        username = str;
        onCreateMsg = AbstractC0520b.m398a("Dsxmq kbszl bra ") + str + AbstractC0520b.m398a("!");
    }

    @Override // android.app.Application
    public void onCreate() {
        String str;
        super.onCreate();
        UserChecker userChecker = new UserChecker();
        String str2 = username;
        if (userChecker.isValidName(str2)) {
            str = onCreateMsg;
        } else {
            str = str2 + AbstractC0520b.m398a(" kg zpv jnmwl vwbr zpxi");
        }
        Log.i(AbstractC0520b.m398a("ImGqrzvdobjsk"), AbstractC0520b.m398a("Wdbmkqnuwwo skWdtlxe: ") + str);
    }
}

public final class UserChecker {
    public static final C0502b Companion = new Object();
    private static final List<String> forbiddenNames;

    static {
        List<String> asList = Arrays.asList(AbstractC0520b.m398a("cryjp"), AbstractC0520b.m398a("tcau"), AbstractC0520b.m398a("wgqs"));
        AbstractC0577a.m287k(asList, "asList(...)");
        forbiddenNames = asList;
    }

    public final boolean isValidName(String str) {
        AbstractC0577a.m286l(str, AbstractC0520b.m398a("poyf"));
        boolean z = false;
        if (!AbstractC0175d.m958Z(str) && str.length() != 0 && !forbiddenNames.contains(str)) {
            z = true;
        }
        String m398a = AbstractC0520b.m398a("PIM");
        String m398a2 = AbstractC0520b.m398a("\tkgBbnwqIouf: ");
        String m398a3 = AbstractC0520b.m398a(" kg ");
        String m398a4 = AbstractC0520b.m398a("\n\tWzx gqfojrlfr kaytd: ");
        List<String> list = forbiddenNames;
        Log.i(m398a, m398a2 + str + m398a3 + z + m398a4 + list);
        return z;
    }
}
```

### Feedback

If you have any questions or suggestions, please feel free
to [open an issue](https://github.com/c0nnor263/obfustring-plugin/issues/new).
I will be happy to make your using of the plugin more comfortable and enjoyable.

### License

    Copyright 2024 Boichuk Oleh

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
