# fs-pgn
The FS Ryan PGN library is a pure Kotlin library for parsing and generating PGN (Portable Game Notation) files.

Because it uses Kotlin Multiplatform, binaries targeting many different architectures are supported.

## Getting started
```bash
chmod +x initialize.sh
./initialize.sh
```

If you opened this in IntelliJ IDEA before running the above, then the gradle project almost certainly did not get set up correctly. So rerun a gradle sync after running the initizlize.sh script.

## Building for various platforms

in the [gradle.properties](gradle.properties) file, you can set the following properties to build for various platforms:
fsryan.android=<true|false>
fsryan.ios=<true|false>
fsryan.js=<true|false>
fsryan.jvm=<true|false>