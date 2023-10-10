package de.topobyte.bmp4j;

import java.awt.image.BufferedImage;

public class ImageDiffUtil
{

	public static int countEqualPixels(BufferedImage image1,
			BufferedImage image2)
	{
		int width = image1.getWidth();
		int height = image1.getHeight();

		int equal = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb1 = image1.getRGB(i, j);
				int rgb2 = image2.getRGB(i, j);
				if (rgb1 == rgb2) {
					equal += 1;
				}
			}
		}

		return equal;
	}

}
