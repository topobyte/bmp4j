/*
 * Decodes a BMP image from an <tt>InputStream</tt> to a <tt>BufferedImage</tt>
 *  
 * @author Ian McDonagh
 */

package de.topobyte.bmp4j.codec;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import de.topobyte.bmp4j.io.CountingInputStream;
import de.topobyte.bmp4j.io.LittleEndianInputStream;

/**
 * Decodes images in BMP format.
 * 
 * @author Ian McDonagh
 */
public class BMPDecoder
{

	private BufferedImage img;
	private FileHeader fileHeader;
	private InfoHeader infoHeader;

	/**
	 * Creates a new instance of BMPDecoder and reads the BMP data from the
	 * source.
	 * 
	 * @param in
	 *            the source <tt>InputStream</tt> from which to read the BMP
	 *            data
	 * @throws IOException
	 *             if an error occurs
	 */
	public BMPDecoder(InputStream in) throws IOException
	{
		LittleEndianInputStream lis = new LittleEndianInputStream(
				new CountingInputStream(in));

		/* header [14] */

		fileHeader = readHeader(lis);

		/* info header [40] */

		infoHeader = readInfoHeader(lis);

		/* Color table and Raster data */

		img = read(infoHeader, lis);
	}

	/**
	 * Retrieves a bit from the lowest order byte of the given integer.
	 * 
	 * @param bits
	 *            the source integer, treated as an unsigned byte
	 * @param index
	 *            the index of the bit to retrieve, which must be in the range
	 *            <tt>0..7</tt>.
	 * @return the bit at the specified index, which will be either <tt>0</tt>
	 *         or <tt>1</tt>.
	 */
	private static int getBit(int bits, int index)
	{
		return (bits >> (7 - index)) & 1;
	}

	/**
	 * Retrieves a nibble (4 bits) from the lowest order byte of the given
	 * integer.
	 * 
	 * @param nibbles
	 *            the source integer, treated as an unsigned byte
	 * @param index
	 *            the index of the nibble to retrieve, which must be in the
	 *            range <tt>0..1</tt>.
	 * @return the nibble at the specified index, as an unsigned byte.
	 */
	private static int getNibble(int nibbles, int index)
	{
		return (nibbles >> (4 * (1 - index))) & 0xF;
	}

	public FileHeader getFileHeader()
	{
		return fileHeader;
	}

	/**
	 * The <tt>InfoHeader</tt> structure, which provides information about the
	 * BMP data.
	 * 
	 * @return the <tt>InfoHeader</tt> structure that was read from the source
	 *         data when this <tt>BMPDecoder</tt> was created.
	 */
	public InfoHeader getInfoHeader()
	{
		return infoHeader;
	}

	/**
	 * The decoded image read from the source input.
	 * 
	 * @return the <tt>BufferedImage</tt> representing the BMP image.
	 */
	public BufferedImage getBufferedImage()
	{
		return img;
	}

	private static void getColorTable(ColorEntry[] colorTable, byte[] ar,
			byte[] ag, byte[] ab)
	{
		for (int i = 0; i < colorTable.length; i++) {
			ar[i] = (byte) colorTable[i].bRed;
			ag[i] = (byte) colorTable[i].bGreen;
			ab[i] = (byte) colorTable[i].bBlue;
		}
	}

	public static FileHeader readHeader(LittleEndianInputStream lis)
			throws IOException
	{
		// signature "BM" [2]
		byte[] bsignature = new byte[2];
		lis.read(bsignature);
		String signature = new String(bsignature, "UTF-8");

		if (!signature.equals("BM")) {
			throw new IOException(
					"Invalid signature '" + signature + "' for BMP format");
		}

		// file size [4]
		int fileSize = lis.readIntLE();

		// reserved = 0 [4]
		int reserved = lis.readIntLE();

		// DataOffset [4] file offset to raster data
		int dataOffset = lis.readIntLE();

		return new FileHeader(signature, fileSize, reserved, dataOffset);
	}

	/**
	 * Reads the BMP info header structure from the given <tt>InputStream</tt>.
	 * 
	 * @param lis
	 *            the <tt>InputStream</tt> to read
	 * @return the <tt>InfoHeader</tt> structure
	 * @throws IOException
	 *             if an error occurred
	 */
	public static InfoHeader readInfoHeader(LittleEndianInputStream lis)
			throws IOException
	{
		int iSize = lis.readIntLE();
		return readInfoHeader(lis, iSize);
	}

	public static InfoHeader readInfoHeader(LittleEndianInputStream lis,
			int infoSize) throws IOException
	{
		if (infoSize == BMPConstants.HEADER_LENGTH_3) {
			return new InfoHeader3(lis);
		} else if (infoSize == BMPConstants.HEADER_LENGTH_5) {
			return new InfoHeader5(lis);
		}
		throw new IOException(
				String.format("Invalid header size %d", infoSize));
	}

	/**
	 * Reads the BMP data from the given <tt>InputStream</tt> using the
	 * information contained in the <tt>InfoHeader</tt>.
	 * 
	 * @param lis
	 *            the source input
	 * @param infoHeader
	 *            an <tt>InfoHeader</tt> that was read by a call to
	 *            {@link #readInfoHeader(LittleEndianInputStream)
	 *            readInfoHeader()}.
	 * @return the decoded image read from the source input
	 * @throws IOException
	 *             if an error occurs
	 */
	public static BufferedImage read(InfoHeader infoHeader,
			LittleEndianInputStream lis) throws IOException
	{
		BufferedImage img = null;

		/* Color table (palette) */

		ColorEntry[] colorTable = null;

		// color table is only present for 1, 4 or 8 bit (indexed) images
		if (infoHeader.getBitCount() <= 8) {
			colorTable = readColorTable(infoHeader, lis);
		}

		img = read(infoHeader, lis, colorTable);

		return img;
	}

	/**
	 * Reads the BMP data from the given <tt>InputStream</tt> using the
	 * information contained in the <tt>InfoHeader</tt>.
	 * 
	 * @param colorTable
	 *            <tt>ColorEntry</tt> array containing palette
	 * @param infoHeader
	 *            an <tt>InfoHeader</tt> that was read by a call to
	 *            {@link #readInfoHeader(LittleEndianInputStream)
	 *            readInfoHeader()}.
	 * @param lis
	 *            the source input
	 * @return the decoded image read from the source input
	 * @throws IOException
	 *             if any error occurs
	 */
	public static BufferedImage read(InfoHeader infoHeader,
			LittleEndianInputStream lis, ColorEntry[] colorTable)
			throws IOException
	{
		BufferedImage img = null;

		// 1-bit (monochrome) uncompressed
		if (infoHeader.getBitCount() == 1
				&& infoHeader.getCompression() == BMPConstants.BI_RGB) {

			img = read1(infoHeader, lis, colorTable);

		}
		// 4-bit uncompressed
		else if (infoHeader.getBitCount() == 4
				&& infoHeader.getCompression() == BMPConstants.BI_RGB) {

			img = read4(infoHeader, lis, colorTable);

		}
		// 8-bit uncompressed
		else if (infoHeader.getBitCount() == 8
				&& infoHeader.getCompression() == BMPConstants.BI_RGB) {

			img = read8(infoHeader, lis, colorTable);

		}
		// 24-bit uncompressed
		else if (infoHeader.getBitCount() == 24
				&& infoHeader.getCompression() == BMPConstants.BI_RGB) {

			img = read24(infoHeader, lis);

		}
		// 32bit uncompressed
		else if (infoHeader.getBitCount() == 32
				&& infoHeader.getCompression() == BMPConstants.BI_RGB) {

			img = read32(infoHeader, lis);
		}
		// 32bit uncompressed
		else if (infoHeader.getBitCount() == 32
				&& infoHeader.getCompression() == BMPConstants.BI_BITFIELDS) {

			img = read32(infoHeader, lis);
		} else {
			throw new IOException("Unrecognized bitmap format: bit count="
					+ infoHeader.getBitCount() + ", compression="
					+ infoHeader.getCompression());
		}

		return img;
	}

	/**
	 * Reads the <tt>ColorEntry</tt> table from the given <tt>InputStream</tt>
	 * using the information contained in the given <tt>infoHeader</tt>.
	 * 
	 * @param infoHeader
	 *            the <tt>InfoHeader</tt> structure, which was read using
	 *            {@link #readInfoHeader(LittleEndianInputStream)
	 *            readInfoHeader()}
	 * @param lis
	 *            the <tt>InputStream</tt> to read
	 * @throws IOException
	 *             if an error occurs
	 * @return the decoded image read from the source input
	 */
	public static ColorEntry[] readColorTable(InfoHeader infoHeader,
			LittleEndianInputStream lis) throws IOException
	{
		ColorEntry[] colorTable = new ColorEntry[infoHeader.getNumColors()];
		for (int i = 0; i < infoHeader.getNumColors(); i++) {
			ColorEntry ce = new ColorEntry(lis);
			colorTable[i] = ce;
		}
		return colorTable;
	}

	/**
	 * Reads 1-bit uncompressed bitmap raster data, which may be monochrome
	 * depending on the palette entries in <tt>colorTable</tt>.
	 * 
	 * @param infoHeader
	 *            the <tt>InfoHeader</tt> structure, which was read using
	 *            {@link #readInfoHeader(LittleEndianInputStream)
	 *            readInfoHeader()}
	 * @param lis
	 *            the source input
	 * @param colorTable
	 *            <tt>ColorEntry</tt> array specifying the palette, which must
	 *            not be <tt>null</tt>.
	 * @throws IOException
	 *             if an error occurs
	 * @return the decoded image read from the source input
	 */
	public static BufferedImage read1(InfoHeader infoHeader,
			LittleEndianInputStream lis, ColorEntry[] colorTable)
			throws IOException
	{
		// 1 bit per pixel or 8 pixels per byte
		// each pixel specifies the palette index

		byte[] ar = new byte[colorTable.length];
		byte[] ag = new byte[colorTable.length];
		byte[] ab = new byte[colorTable.length];

		getColorTable(colorTable, ar, ag, ab);

		IndexColorModel icm = new IndexColorModel(1, 2, ar, ag, ab);

		// Create indexed image
		BufferedImage img = new BufferedImage(infoHeader.getWidth(),
				infoHeader.getHeight(), BufferedImage.TYPE_BYTE_BINARY, icm);
		// We'll use the raster to set samples instead of RGB values.
		// The SampleModel of an indexed image interprets samples as
		// the index of the colour for a pixel, which is perfect for use here.
		WritableRaster raster = img.getRaster();

		// padding

		int dataBitsPerLine = infoHeader.getWidth();
		int bitsPerLine = dataBitsPerLine;
		if (bitsPerLine % 32 != 0) {
			bitsPerLine = (bitsPerLine / 32 + 1) * 32;
		}
		int padBits = bitsPerLine - dataBitsPerLine;
		int padBytes = padBits / 8;

		int bytesPerLine = bitsPerLine / 8;
		int[] line = new int[bytesPerLine];

		for (int y = infoHeader.getHeight() - 1; y >= 0; y--) {
			for (int i = 0; i < bytesPerLine; i++) {
				line[i] = lis.readUnsignedByte();
			}

			for (int x = 0; x < infoHeader.getWidth(); x++) {
				int i = x / 8;
				int v = line[i];
				int b = x % 8;
				int index = getBit(v, b);
				// int rgb = c[index];
				// img.setRGB(x, y, rgb);
				// set the sample (colour index) for the pixel
				raster.setSample(x, y, 0, index);
			}
		}

		return img;
	}

	/**
	 * Reads 4-bit uncompressed bitmap raster data, which is interpreted based
	 * on the colours specified in the palette.
	 * 
	 * @param infoHeader
	 *            the <tt>InfoHeader</tt> structure, which was read using
	 *            {@link #readInfoHeader(LittleEndianInputStream)
	 *            readInfoHeader()}
	 * @param lis
	 *            the source input
	 * @param colorTable
	 *            <tt>ColorEntry</tt> array specifying the palette, which must
	 *            not be <tt>null</tt>.
	 * @throws IOException
	 *             if an error occurs
	 * @return the decoded image read from the source input
	 */
	public static BufferedImage read4(InfoHeader infoHeader,
			LittleEndianInputStream lis, ColorEntry[] colorTable)
			throws IOException
	{
		// 2 pixels per byte or 4 bits per pixel.
		// Colour for each pixel specified by the color index in the pallette.

		byte[] ar = new byte[colorTable.length];
		byte[] ag = new byte[colorTable.length];
		byte[] ab = new byte[colorTable.length];

		getColorTable(colorTable, ar, ag, ab);

		IndexColorModel icm = new IndexColorModel(4, infoHeader.getNumColors(),
				ar, ag, ab);

		BufferedImage img = new BufferedImage(infoHeader.getWidth(),
				infoHeader.getHeight(), BufferedImage.TYPE_BYTE_BINARY, icm);

		WritableRaster raster = img.getRaster();

		// padding
		int bitsPerLine = infoHeader.getWidth() * 4;
		if (bitsPerLine % 32 != 0) {
			bitsPerLine = (bitsPerLine / 32 + 1) * 32;
		}
		int bytesPerLine = bitsPerLine / 8;

		int[] line = new int[bytesPerLine];

		for (int y = infoHeader.getHeight() - 1; y >= 0; y--) {
			// scan line
			for (int i = 0; i < bytesPerLine; i++) {
				int b = lis.readUnsignedByte();
				line[i] = b;
			}

			// get pixels
			for (int x = 0; x < infoHeader.getWidth(); x++) {
				// get byte index for line
				int b = x / 2; // 2 pixels per byte
				int i = x % 2;
				int n = line[b];
				int index = getNibble(n, i);
				raster.setSample(x, y, 0, index);
			}
		}

		return img;
	}

	/**
	 * Reads 8-bit uncompressed bitmap raster data, which is interpreted based
	 * on the colours specified in the palette.
	 * 
	 * @param infoHeader
	 *            the <tt>InfoHeader</tt> structure, which was read using
	 *            {@link #readInfoHeader(LittleEndianInputStream)
	 *            readInfoHeader()}
	 * @param lis
	 *            the source input
	 * @param colorTable
	 *            <tt>ColorEntry</tt> array specifying the palette, which must
	 *            not be <tt>null</tt>.
	 * @throws IOException
	 *             if an error occurs
	 * @return the decoded image read from the source input
	 */
	public static BufferedImage read8(InfoHeader infoHeader,
			LittleEndianInputStream lis, ColorEntry[] colorTable)
			throws IOException
	{
		// 1 byte per pixel
		// color index 1 (index of color in palette)
		// lines padded to nearest 32bits
		// no alpha

		byte[] ar = new byte[colorTable.length];
		byte[] ag = new byte[colorTable.length];
		byte[] ab = new byte[colorTable.length];

		getColorTable(colorTable, ar, ag, ab);

		IndexColorModel icm = new IndexColorModel(8, infoHeader.getNumColors(),
				ar, ag, ab);

		BufferedImage img = new BufferedImage(infoHeader.getWidth(),
				infoHeader.getHeight(), BufferedImage.TYPE_BYTE_INDEXED, icm);

		WritableRaster raster = img.getRaster();

		/*
		 * //create color pallette int[] c = new int[infoHeader.iNumColors]; for
		 * (int i = 0; i < c.length; i++) { int r = colorTable[i].bRed; int g =
		 * colorTable[i].bGreen; int b = colorTable[i].bBlue; c[i] = (r << 16) |
		 * (g << 8) | (b); }
		 */

		// padding
		int dataPerLine = infoHeader.getWidth();
		int bytesPerLine = dataPerLine;
		if (bytesPerLine % 4 != 0) {
			bytesPerLine = (bytesPerLine / 4 + 1) * 4;
		}
		int padBytesPerLine = bytesPerLine - dataPerLine;

		for (int y = infoHeader.getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < infoHeader.getWidth(); x++) {
				int b = lis.readUnsignedByte();
				// int clr = c[b];
				// img.setRGB(x, y, clr);
				// set sample (colour index) for pixel
				raster.setSample(x, y, 0, b);
			}

			lis.skip(padBytesPerLine);
		}

		return img;
	}

	/**
	 * Reads 24-bit uncompressed bitmap raster data.
	 * 
	 * @param lis
	 *            the source input
	 * @param infoHeader
	 *            the <tt>InfoHeader</tt> structure, which was read using
	 *            {@link #readInfoHeader(LittleEndianInputStream)
	 *            readInfoHeader()}
	 * @throws IOException
	 *             if an error occurs
	 * @return the decoded image read from the source input
	 */
	public static BufferedImage read24(InfoHeader infoHeader,
			LittleEndianInputStream lis) throws IOException
	{
		// 3 bytes per pixel
		// blue 1
		// green 1
		// red 1
		// lines padded to nearest 32 bits
		// no alpha

		BufferedImage img = new BufferedImage(infoHeader.getWidth(),
				infoHeader.getHeight(), BufferedImage.TYPE_INT_RGB);

		WritableRaster raster = img.getRaster();

		// padding to nearest 32 bits
		int dataPerLine = infoHeader.getWidth() * 3;
		int bytesPerLine = dataPerLine;
		if (bytesPerLine % 4 != 0) {
			bytesPerLine = (bytesPerLine / 4 + 1) * 4;
		}
		int padBytesPerLine = bytesPerLine - dataPerLine;

		for (int y = infoHeader.getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < infoHeader.getWidth(); x++) {
				int b = lis.readUnsignedByte();
				int g = lis.readUnsignedByte();
				int r = lis.readUnsignedByte();

				// int c = 0x00000000 | (r << 16) | (g << 8) | (b);
				// System.out.println(x + ","+y+"="+Integer.toHexString(c));
				// img.setRGB(x, y, c);
				raster.setSample(x, y, 0, r);
				raster.setSample(x, y, 1, g);
				raster.setSample(x, y, 2, b);
			}
			lis.skip(padBytesPerLine);
		}

		return img;
	}

	/**
	 * Reads 32-bit uncompressed bitmap raster data, with transparency.
	 * 
	 * @param lis
	 *            the source input
	 * @param infoHeader
	 *            the <tt>InfoHeader</tt> structure, which was read using
	 *            {@link #readInfoHeader(LittleEndianInputStream)
	 *            readInfoHeader()}
	 * @throws IOException
	 *             if an error occurs
	 * @return the decoded image read from the source input
	 */
	public static BufferedImage read32(InfoHeader infoHeader,
			LittleEndianInputStream lis) throws IOException
	{
		// 4 bytes per pixel
		// blue 1
		// green 1
		// red 1
		// alpha 1
		// No padding since each pixel = 32 bits

		BufferedImage img = new BufferedImage(infoHeader.getWidth(),
				infoHeader.getHeight(), BufferedImage.TYPE_INT_ARGB);

		WritableRaster rgb = img.getRaster();
		WritableRaster alpha = img.getAlphaRaster();

		for (int y = infoHeader.getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < infoHeader.getWidth(); x++) {
				int b = lis.readUnsignedByte();
				int g = lis.readUnsignedByte();
				int r = lis.readUnsignedByte();
				int a = lis.readUnsignedByte();
				rgb.setSample(x, y, 0, r);
				rgb.setSample(x, y, 1, g);
				rgb.setSample(x, y, 2, b);
				alpha.setSample(x, y, 0, a);
			}
		}

		return img;
	}

	/**
	 * Reads and decodes BMP data from the source file.
	 * 
	 * @param file
	 *            the source file
	 * @throws IOException
	 *             if an error occurs
	 * @return the decoded image read from the source file
	 */
	public static BufferedImage read(File file) throws IOException
	{
		FileInputStream fin = new FileInputStream(file);
		try {
			return read(new BufferedInputStream(fin));
		} finally {
			try {
				fin.close();
			} catch (IOException ex) {
			}
		}
	}

	/**
	 * Reads and decodes BMP data from the source file.
	 * 
	 * @param file
	 *            the source file
	 * @throws IOException
	 *             if an error occurs
	 * @return the decoded image read from the source file
	 */
	public static BufferedImage read(Path file) throws IOException
	{
		InputStream fin = Files.newInputStream(file);
		try {
			return read(new BufferedInputStream(fin));
		} finally {
			try {
				fin.close();
			} catch (IOException ex) {
			}
		}
	}

	/**
	 * Reads and decodes BMP data from the source input.
	 * 
	 * @param in
	 *            the source input
	 * @throws IOException
	 *             if an error occurs
	 * @return the decoded image read from the source file
	 */
	public static BufferedImage read(InputStream in) throws IOException
	{
		BMPDecoder d = new BMPDecoder(in);
		return d.getBufferedImage();
	}

	/**
	 * Reads and decodes BMP data from the source file, together with metadata.
	 * 
	 * @param file
	 *            the source file
	 * @throws IOException
	 *             if an error occurs
	 * @return the decoded image read from the source file
	 */
	public static BMPImage readExt(File file) throws IOException
	{
		FileInputStream fin = new FileInputStream(file);
		try {
			return readExt(new BufferedInputStream(fin));
		} finally {
			try {
				fin.close();
			} catch (IOException ex) {
			}
		}
	}

	/**
	 * Reads and decodes BMP data from the source input, together with metadata.
	 * 
	 * @param in
	 *            the source input
	 * @throws IOException
	 *             if an error occurs
	 * @return the decoded image read from the source file
	 */
	public static BMPImage readExt(InputStream in) throws IOException
	{
		BMPDecoder d = new BMPDecoder(in);
		BMPImage ret = new BMPImage(d.getBufferedImage(), d.getInfoHeader());
		return ret;
	}
}
