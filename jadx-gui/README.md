Need to take action "Deobfuscate" to get the `R.java`

### Error with Gradle build fro newly exported project 
> AndroidStudio automatically assign gradle version to default "8.9.0".

1. Update gradle version seems take effect.
2. Then I need to update java version in gradle build to 1.8

### Unable to make field private final java.lang.String java.io.File.path accessible: module java.base does not "opens java.io"

Add `gradle.properties` file to define some extra configuration for gradle build.

### Failed to fetch namespace: Affected Modules: app
Add name space to `android` block at `build.gradle` file.
