package de.topobyte.bmp4j;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.junit.Test;

import de.topobyte.bmp4j.codec.BMPEncoder;
import de.topobyte.system.utils.SystemPaths;

public class TestEncodePngsAsBmp
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
	public void testColors() throws IOException
	{
		test("colors");
	}

	/**
	 * Convert file from PNG to BMP (bmp-test) using our library, read back in
	 * using ImageIO and compare result to original file.
	 */
	private void test(String filename) throws IOException
	{
		Path dirTestdata = SystemPaths.CWD.resolve("testdata");
		Path dirInput = dirTestdata.resolve("png");
		Path dirOutput = dirTestdata.resolve("bmp-test");
		Files.createDirectories(dirOutput);

		Path input = dirInput.resolve(filename + ".png");
		BufferedImage pngImage = ImageIO.read(input.toFile());

		Path bmpFile = dirOutput.resolve(filename + ".bmp");
		BMPEncoder.write(pngImage, bmpFile.toFile());
		BufferedImage bmpImage = ImageIO.read(bmpFile.toFile());

		TestUtils.compare(pngImage, bmpImage);
	}

}
