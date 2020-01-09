# Breaking Changes

## V1.2.15
* Stream class was mapped to CASStream.

Add this to your project `header-mapping.j2objc`.
```
com.annimon.stream.Stream=CASStream.h
```

## V1.2.17 ⚠️⚠️⚠️ Broken version, do not use ⚠️⚠️⚠️
### BREAKING CHANGE
Header directory structure is now maintained, removing the need for `header-mapping.j2objc` entries

Remove all entries starting by `com.animon.stream.*` from your project mapping file

## V1.2.18
### BREAKING CHANGE
Simply keeping the directory structure was not enough since cocoapods does not maintain the hierarchy for standard pods (only for local pods)

The library now bundles an header mapping file in it's META-INF folder. It needs to be extracted and added to the options passed to the j2objc plugin.

`--header-mapping header-mapping.j2objc` -> `--header-mapping header-mapping.j2objc,build/dependencies-header-mapping.j2objc`

To extract the header mapping files from lightweight-stream-api add this to your build script:

```
task extractStreamHeaderMapping {
    doLast {
        def generatedHeader = new File(project.buildDir, 'dependencies-header-mapping.j2objc')
        project.configurations.findByName('compileClasspath')?.filter {
            it.name.endsWith('.jar')
        }?.each { jar ->
            def zipFile = new ZipFile(jar)
            def prefixesEntry = zipFile.getEntry("META-INF/lightweight-stream-api-header-mapping.j2objc")
            if (prefixesEntry) {
                generatedHeader << "${zipFile.getInputStream(prefixesEntry)}"
            }
        }
    }
}

tasks['j2objc'].dependsOn extractStreamHeaderMapping
```
