package de.topobyte.bmp4j;

import java.awt.image.BufferedImage;

import org.junit.Assert;

public class TestUtils
{

	static void compare(BufferedImage imageExpect, BufferedImage image)
	{
		Assert.assertEquals(imageExpect.getWidth(), image.getWidth());
		Assert.assertEquals(imageExpect.getHeight(), image.getHeight());

		int total = imageExpect.getWidth() * imageExpect.getHeight();
		int equal = ImageDiffUtil.countEqualPixels(imageExpect, image);

		if (equal != total) {
			System.out.println("images are not equal");
			double equalityRate = equal / (double) total;
			System.out.println(
					String.format("equality rate: %.3f", equalityRate));
		}
		Assert.assertEquals("All pixels should be equal", total, equal);
	}

}
