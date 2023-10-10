package de.topobyte.bmp4j;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

import de.topobyte.bmp4j.codec.BMPDecoder;
import de.topobyte.system.utils.SystemPaths;

public class TestConversion
{

	@Test
	public void testRgb1() throws IOException
	{
		test("rgb1");
	}

	@Test
	public void testRgb2() throws IOException
	{
		test("rgb2");
	}

	@Test
	public void testRgb1_4bit() throws IOException
	{
		test("rgb1-4bit");
	}

	@Test
	public void testRgb2_4bit() throws IOException
	{
		test("rgb2-4bit");
	}

	@Test
	public void testColors() throws IOException
	{
		test("colors");
	}

	private void test(String filename) throws IOException
	{
		Path dirTestdata = SystemPaths.CWD.resolve("testdata");
		Path dir = dirTestdata.resolve("bmp");
		Path convert = dirTestdata.resolve("png-convert");
		Path output = dirTestdata.resolve("png-test");
		Files.createDirectories(output);

		Path file = dir.resolve(filename + ".bmp");

		convert(file, convert, output);
	}

	private static void convert(Path file, Path convert, Path output)
			throws IOException
	{
		BufferedImage image = BMPDecoder.read(file);
		String filename = file.getFileName().toString();
		String pngName = filename.substring(0, filename.length() - 4) + ".png";
		Path pngFileConvert = convert.resolve(pngName);
		Path pngFileCreate = output.resolve(pngName);

		ImageIO.write(image, "PNG", pngFileCreate.toFile());

		BufferedImage imageCompare = ImageIO.read(pngFileConvert.toFile());

		compare(imageCompare, image);
	}

	private static void compare(BufferedImage imageExpect, BufferedImage image)
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
