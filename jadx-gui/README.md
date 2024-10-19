Need to take action "Deobfuscate" to get the `R.java`

### Error with Gradle build fro newly exported project 
> AndroidStudio automatically assign gradle version to default "8.9.0".

1. Update gradle version seems take effect.
2. Then I need to update java version in gradle build to 1.8

### Unable to make field private final java.lang.String java.io.File.path accessible: module java.base does not "opens java.io"

Add `gradle.properties` file to define some extra configuration for gradle build.

### Failed to fetch namespace: Affected Modules: app
Add name space to `android` block at `build.gradle` file.

### Fix compiler error by Jadx
1. Copy some error code from real Jadx-gui. Some code broken due to exportation.
2. Fix some error code manually: constant value instead of raw value
3. Note todo for all error that cannot fix by jadx decompiler: missing variable, `UnsupportedOperationException`

### Remove the compiled code and redundant resources | Tecnicallyy remove all then if failed compiler ---> revert it back
1. all text with pattern
```sh
<string name="abc_.*>\n

<dimen name="abc_.*>\n

<integer name="abc_.*>\n

<bool name="abc_.*>\n

<color name="abc_.*>\n
```

2. remove all abc_ file using `remove_abc_files.sh`
3. Remove all style variants in `res/values/styles.xml` and all styles in `res/values/styles.xml` except the one referenced in `AndroidManifest.xml`
3. Remove publics.xml
4. Technically remove every resources which seem unrelated

### unresolved a libarary (TransportMediator)

deprecated lib/module ---> dowgrade compileSdk level
> From 30 to 24. Don't know why

### Remove dependencies
Use the library, especially the support android lib --> base on compile or target sdk level

# Remove R class ---> actrrually copy R.class to gen directory