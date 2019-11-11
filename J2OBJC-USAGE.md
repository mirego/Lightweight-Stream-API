# Utiliser lightweight-stream-api dans un core

## Dépendance pour le Core/android

- Ajouter la dépendance dans votre `build.gradle` 

```
dependencies {
	...
	compile 'com.mirego.annimon:stream:1.2.16'
	...
}
```

## Dépendance pour iOS

- Ajouter la dépendance dans votre `*core.podspec.template`

```
s.dependency 'lightweight-stream-api', '1.2.16'
```

## Header mapping pour iOS

Certains fichiers `.h` générés pour `stream-api` entrent en conflit avec des header de `jre-emul`


### Si votre projet n'a pas de header mapping:
- Créer un fichier texte appelé `header-mapping.j2objc` à la racine du core avec les entrées suivantes:

### Si le votre projet a déjà du header mapping: 

- Ajouter les entrées suivantes au fichier de header mapping (normalement nommé `header-mapping.j2objc') dans le Core du project

```
com.annimon.stream.Optional=CASOptional.h
com.annimon.stream.function.Function=CASFunction.h
com.annimon.stream.Objects=CASObjects.h
com.annimon.stream.function.Predicate=CASPredicate.h
com.annimon.stream.function.Supplier=CASSupplier.h
com.annimon.stream.Stream=CASStream.h
```

- Ajouter l'option `--header-mapping` au plugin j2objc 

```
j2objc {
	...
	options += ' --header-mapping header-mapping.j2objc'
	...
}
```
