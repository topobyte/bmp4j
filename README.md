# bmp4j

## Overview

The bmp4j library allows you to read and write BMP image files in 100% pure Java.

Currently the following formats are supported:

* BMP (Microsoft bitmap format - uncompressed; 1, 4, 8, 24 and 32 bit)

## Purpose

This project aims to provide:

* an open source library for handling BMP image files in Java
* with a commercial-friendly license
* using only Java code, ie. without using any JNI hacks
* with no dependencies on third-party libraries (where possible)

## License

The bmp4j library is licensed under the GNU LGPL v2.1 so you are free to use it in your Free Software and Open Source projects, as well as commercial projects, under the terms of the LGPL v2.1.

## History

This library has been forked from [imcdonagh/image4j](https://github.com/imcdonagh/image4j), refactored
and then reduced in scope to only focus on BMP images.

The original author states:

This project began after I spent hours searching for a library that would meet the above criteria and found that there is probably no such thing - or at least, not any more. The only support I could find for the ICO format was read-only, so doing cool stuff - like generating favicons on the fly - using only Java code was not possible.

## QuickStart

It is possible to decode/encode BMP files with just one line of code!

The bmp4j library consists of only Java code and has no dependencies on third-party libraries, so you just add it to the classpath and you're good to go.

Although the code is compatible with Java 1.5.0 or later, it should be relatively simple to port the code to an older version, eg. 1.4.2.

### Decode


```java
BufferedImage image = BMPDecoder.read(new File("input.bmp"));
```

### Encode

```java
BMPEncoder.write(image, new File("output.bmp"));
```

## Credits

* The [File Formats page at DaubNET](https://www.daubnet.com/en/file-formats) for information on various image formats.
* GIMP, which I use for editing images

## Disclaimer

To my knowledge, there are no patents on either the BMP or ICO formats.
