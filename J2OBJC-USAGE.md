# Utiliser lightweight-stream-api dans un core

## Dépendance pour le Core/android

- Ajouter la dépendance dans votre `build.gradle`

```groovy
dependencies {
    ...
    compile 'com.mirego.annimon:stream:1.2.18'
    ...
}
```

## Dépendance pour iOS

- Ajouter la dépendance dans votre `*core.podspec.template`

```ruby
s.dependency 'lightweight-stream-api', '1.2.18'
```

## Header mapping pour iOS

Les fichiers `.h` générés pour `lightweight-stream-api` entrent en conflit avec l'implémentaion de `Stream` de `jre-emul` (Utilisable seulement si votre App Android est API Level 24 et plus)

Un fichier de header mappings est livré à même le `.jar` dans les META-INF. Il s'appelle `lightweight-stream-api-header-mapping.j2objc`

Pour être en mesure d'utiliser ce fichier lors de la transpilation de votre projet, ajouter ceci à votre build.gradle de core:

```groovy
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

### Si votre projet n'a pas de header mapping

Ajouter l'option `--header-mapping` au plugin j2objc

```groovy
j2objc {
    ...
    options += ' --header-mapping dependencies-header-mapping.j2objc'
    ...
}
```

### Si votre projet a déjà du header mapping

- Localiser l'option `--header-mapping` dans la configuration de votre plugin j2objc et y ajouter le 2e fichier:

```groovy
... --header-mapping header-mapping.j2objc,dependencies-header-mapping.j2objc ...
```
