# Breaking Changes

## V1.2.15
* Stream class was mapped to CASStream.

Add this to your project `header-mapping.j2objc`.
```
com.annimon.stream.Stream=CASStream.h
```

## V1.2.17
### BREAKING CHANGE
Header directory structure is now maintained, removing the need for `header-mapping.j2objc` entries

Remove all entries starting by `com.animon.stream.*` from your project mapping file
