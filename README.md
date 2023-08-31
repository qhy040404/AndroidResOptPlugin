# AndroidResOptPlugin

Optimize an Android app's resources using `aapt`.

---

## Installation

**Only available for Android projects**

<details open>
<summary>Kotlin</summary>

> root `build.gradle.kts`

```kotlin
plugins {
    id("com.qhy04.gradle.android.res_opt") version "1.0.0" apply false
}
```

> module `build.gradle.kts`

```kotlin
plugins {
    id("com.qhy04.gradle.android.res_opt")
}
```

</details>


<details>
<summary>Groovy</summary>

> root `build.gradle`

```groovy
plugins {
    id 'com.qhy04.gradle.android.res_opt' version "1.0.0" apply false
}
```

> module `build.gradle`

```groovy
plugins {
    id 'com.qhy04.gradle.android.res_opt'
}
```

</details>