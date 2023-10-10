package de.topobyte.bmp4j;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.junit.Test;

import de.topobyte.bmp4j.codec.BMPDecoder;
import de.topobyte.system.utils.SystemPaths;

public class TestDecodeBmps
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

	/**
	 * Convert file from BMP to PNG (png-test) using our library and compare
	 * result to files created by convert (png-convert).
	 */
	private void test(String filename) throws IOException
	{
		Path dirTestdata = SystemPaths.CWD.resolve("testdata");
		Path dir = dirTestdata.resolve("bmp");
		Path convert = dirTestdata.resolve("png-convert");
		Path output = dirTestdata.resolve("png-test");
		Files.createDirectories(output);

		Path input = dir.resolve(filename + ".bmp");

		BufferedImage bmpImage = BMPDecoder.read(input);

		Path pngFile = output.resolve(filename + ".png");
		ImageIO.write(bmpImage, "PNG", pngFile.toFile());

		Path pngFileConvert = convert.resolve(filename + ".png");
		BufferedImage imageConvert = ImageIO.read(pngFileConvert.toFile());

		TestUtils.compare(imageConvert, bmpImage);
	}

}
