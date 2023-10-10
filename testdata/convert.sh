#!/bin/bash

set -e

rm -rf png bmp png-convert

mkdir -p png
mkdir -p bmp
mkdir -p png-convert

# exclude date and time as text in PNG metadata to produce the same files
# each time we run this:
png() {
    convert -define png:exclude-chunks=date,time "$@"
}

inkscape -C -o png/rgb1.png svg/rgb1.svg
inkscape -C -o png/rgb2.png svg/rgb2.svg
inkscape -C -o png/colors.png svg/colors.svg

convert png/rgb1.png bmp/rgb1.bmp
convert png/rgb1.png -depth 4 -type palette bmp/rgb1-4bit.bmp

convert png/rgb2.png bmp/rgb2.bmp
convert png/rgb2.png -depth 4 -type palette bmp/rgb2-4bit.bmp

convert png/colors.png bmp/colors.bmp
# Don't do this yet. The result will be run-length encoded which we cannot
# parse yet.
#convert png/colors.png -depth 8 -type palette bmp/colors-8bit.bmp

png bmp/rgb1.bmp png-convert/rgb1.png
png bmp/rgb1-4bit.bmp png-convert/rgb1-4bit.png

png bmp/rgb2.bmp png-convert/rgb2.png
png bmp/rgb2-4bit.bmp png-convert/rgb2-4bit.png

png bmp/colors.bmp png-convert/colors.png
#png bmp/colors-8bit.bmp png-convert/colors-8bit.png
