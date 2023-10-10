## Software versions

The files in this directory have been converted with Inkscape and ImageMagick
in the following versions:

* Inkscape 1.2.2 (1:1.2.2+202305151915+b0a8486541)
* ImageMagick 6.9.10-23 Q16 x86_64 20190101

## Test data structure

The sources of the test data are the SVG files in `svg`.
The script `convert.sh` can be used to generate, in this order:

* A set of corresponding png files from the svg sources using Inkscape,
  results are in `png`.
* A set of bmp images derived from the png images, using ImageMagick's
  `convert` utility. Results are in `bmp`.
* A set of png images derived from the bmp images, also using `convert`.
  These can be used to validate that our parser understands the bmp files
  corretly by comparing the image raster read from these png files to the
  image raster loaded from the bmp files.
