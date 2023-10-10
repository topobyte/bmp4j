package de.topobyte.bmp4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.topobyte.bmp4j.codec.BMPDecoder;
import de.topobyte.bmp4j.codec.FileHeader;
import de.topobyte.bmp4j.codec.InfoHeader;
import de.topobyte.bmp4j.io.CountingInputStream;
import de.topobyte.bmp4j.io.LittleEndianInputStream;
import de.topobyte.system.utils.SystemPaths;

public class TestHeaders
{

	@Test
	public void testRgb1() throws IOException
	{
		test("rgb1", 124, 32, 3);
	}

	@Test
	public void testRgb2() throws IOException
	{
		test("rgb2", 124, 32, 3);
	}

	@Test
	public void testRgb1_4bit() throws IOException
	{
		test("rgb1-4bit", 124, 4, 0);
	}

	@Test
	public void testRgb2_4bit() throws IOException
	{
		test("rgb2-4bit", 124, 4, 0);
	}

	@Test
	public void testColors() throws IOException
	{
		test("colors", 124, 32, 3);
	}

	static Map<Integer, String> compressionNames = new HashMap<>();
	static {
		compressionNames.put(0, "BI_RGB");
		compressionNames.put(1, "BI_RLE8");
		compressionNames.put(2, "BI_RLE4");
		compressionNames.put(3, "BI_BITFIELDS");
	}

	private void test(String filename, int expectedHeaderSize, int expectedBits,
			int expectedCompression) throws IOException
	{
		Path dirTestdata = SystemPaths.CWD.resolve("testdata");
		Path dir = dirTestdata.resolve("bmp");

		Path file = dir.resolve(filename + ".bmp");
		System.out.println(file);

		InputStream in = Files.newInputStream(file);
		LittleEndianInputStream lis = new LittleEndianInputStream(
				new CountingInputStream(in));
		FileHeader header = BMPDecoder.readHeader(lis);
		System.out.println(
				"size, offset: " + header.fileSize + ", " + header.dataOffset);
		InfoHeader info = BMPDecoder.readInfoHeader(lis);
		System.out.println("header size: " + info.getSize());
		System.out.println("bits: " + info.getBitCount());
		System.out.println(
				"compression: " + compressionNames.get(info.getCompression()));

		Assert.assertEquals(expectedHeaderSize, info.getSize());
		Assert.assertEquals(expectedBits, info.getBitCount());
		Assert.assertEquals(expectedCompression, info.getCompression());
	}

}
