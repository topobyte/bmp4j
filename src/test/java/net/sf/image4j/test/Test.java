/*
 * Test.java
 *
 * Created on January 19, 2007, 11:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.image4j.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.sf.image4j.codec.bmp.BMPDecoder;
import net.sf.image4j.codec.bmp.BMPEncoder;
import net.sf.image4j.codec.ico.ICODecoder;
import net.sf.image4j.codec.ico.ICOEncoder;

/**
 *
 * @author Ian McDonagh
 */
public class Test
{

	public static void main(String[] args)
	{

		// input and output file names

		if (args.length < 2) {
			System.out.println("Usage:\n\tTest <inputfile> <outputfile>");
			System.exit(1);
		}

		String strInFile = args[0];
		String strOutFile = args[1];

		InputStream in = null;
		try {
			List<BufferedImage> images;

			/***** decode ICO and save images as BMP and PNG ****/

			if (strInFile.startsWith("http:")) {
				in = new URL(strInFile).openStream();
			} else {
				in = new FileInputStream(strInFile);
			}

			if (!strInFile.endsWith(".ico")) {
				images = new ArrayList<>(1);
				images.add(ImageIO.read(in));

				System.out.println("Read image " + strInFile + "...OK");
			} else {
				System.out.println("Decoding ICO file '" + strInFile + "'.");

				// load and decode ICO file

				images = ICODecoder.read(in);
				System.out.println("ICO decoding...OK");

				// display summary of decoded images

				System.out.println("  image count = " + images.size());
				System.out.println("  image summary:");
				for (int i = 0; i < images.size(); i++) {
					BufferedImage img = images.get(i);
					int bpp = img.getColorModel().getPixelSize();
					int width = img.getWidth();
					int height = img.getHeight();
					System.out.println("    # " + i + ": size=" + width + "x"
							+ height + "; colour depth=" + bpp + " bpp");
				}

				// save images as separate BMP and PNG files

				System.out.println("  saving separate images:");

				String format = "png";

				for (int j = 0; j < images.size(); j++) {
					BufferedImage img = images.get(j);
					String name = strOutFile + "-" + j;
					File bmpFile = new File(name + ".bmp");
					File pngFile = new File(name + ".png");

					// write BMP
					System.out.println("    writing '" + name + ".bmp'");
					BMPEncoder.write(img, bmpFile);

					// write PNG
					System.out.println("    writing '" + name + ".png'");
					ImageIO.write(img, format, pngFile);
				}

				System.out.println("BMP encoding...OK");

				/***** reload BMP images *****/

				System.out.println("  reloading BMP files:");

				List<BufferedImage> images2 = new ArrayList<>(images.size());

				for (int k = 0; k < images.size(); k++) {
					String name = strOutFile + "-" + k + ".bmp";
					File file = new File(name);

					// read BMP
					System.out.println("    reading '" + name + "'");
					BufferedImage image = BMPDecoder.read(file);
					images2.add(image);
				}

				System.out.println("BMP decoding...OK");

			}

			/***** encode images and save as ICO *****/

			System.out.println("Encoding ICO file '" + strOutFile + "'.");

			File outFile = new File(strOutFile);

			ICOEncoder.write(images, outFile);

			System.out.println("ICO encoding...OK");

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
			}
		}

	}

	private static void usage()
	{

	}

}
