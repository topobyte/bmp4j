package de.topobyte.bmp4j.codec;

import java.io.IOException;

import de.topobyte.bmp4j.io.LittleEndianInputStream;
import de.topobyte.bmp4j.io.LittleEndianOutputStream;

/**
 * Represents a bitmap <tt>InfoHeader</tt> structure, which provides header
 * information.
 * 
 * @author Ian McDonagh
 */
public class InfoHeader5 implements InfoHeader
{

	/**
	 * The size of this <tt>InfoHeader</tt> structure in bytes.
	 */
	public int iSize;
	/**
	 * The width in pixels of the bitmap represented by this
	 * <tt>InfoHeader</tt>.
	 */
	public int iWidth;
	/**
	 * The height in pixels of the bitmap represented by this
	 * <tt>InfoHeader</tt>.
	 */
	public int iHeight;
	/**
	 * The number of planes, which should always be <tt>1</tt>.
	 */
	public short sPlanes;
	/**
	 * The bit count, which represents the colour depth (bits per pixel). This
	 * should be either <tt>1</tt>, <tt>4</tt>, <tt>8</tt>, <tt>24</tt> or
	 * <tt>32</tt>.
	 */
	public short sBitCount;
	/**
	 * The compression type, which should be one of the following:
	 * <ul>
	 * <li>{@link BMPConstants#BI_RGB BI_RGB} - no compression</li>
	 * <li>{@link BMPConstants#BI_RLE8 BI_RLE8} - 8-bit RLE compression</li>
	 * <li>{@link BMPConstants#BI_RLE4 BI_RLE4} - 4-bit RLE compression</li>
	 * </ul>
	 */
	public int iCompression;
	/**
	 * The compressed size of the image in bytes, or <tt>0</tt> if
	 * <tt>iCompression</tt> is <tt>0</tt>.
	 */
	public int iImageSize;
	/**
	 * Horizontal resolution in pixels/m.
	 */
	public int iXpixelsPerM;
	/**
	 * Vertical resolution in pixels/m.
	 */
	public int iYpixelsPerM;
	/**
	 * Number of colours actually used in the bitmap.
	 */
	public int iColorsUsed;
	/**
	 * Number of important colours (<tt>0</tt> = all).
	 */
	public int iColorsImportant;

	/**
	 * Calculated number of colours, based on the colour depth specified by
	 * {@link #sBitCount sBitCount}.
	 */
	public int iNumColors;

	public int iRedMask;
	public int iGreenMask;
	public int iBlueMask;
	public int iAlphaMask;
	public int iColorSpaceType;
	public int iRedX;
	public int iRedY;
	public int iRedZ;
	public int iGreenX;
	public int iGreenY;
	public int iGreenZ;
	public int iBlueX;
	public int iBlueY;
	public int iBlueZ;
	public int iGammaRed;
	public int iGammaGreen;
	public int iGammaBlue;
	public int iIntent;
	public int iProfileData;
	public int iProfileSize;
	public int iReserved;

	/**
	 * Creates an <tt>InfoHeader</tt> structure from the source input.
	 * 
	 * @param in
	 *            the source input
	 * @throws IOException
	 *             if an error occurs
	 */
	public InfoHeader5(LittleEndianInputStream in) throws IOException
	{
		init(in, BMPConstants.HEADER_LENGTH_5);
	}

	protected void init(LittleEndianInputStream in, int infoSize)
			throws IOException
	{
		this.iSize = infoSize;

		// Width
		iWidth = in.readIntLE();
		// Height
		iHeight = in.readIntLE();
		// Planes (=1)
		sPlanes = in.readShortLE();
		// Bit count
		sBitCount = in.readShortLE();

		// calculate NumColors
		iNumColors = (int) Math.pow(2, sBitCount);

		// Compression
		iCompression = in.readIntLE();
		// Image size - compressed size of image or 0 if Compression = 0
		iImageSize = in.readIntLE();
		// horizontal resolution pixels/meter
		iXpixelsPerM = in.readIntLE();
		// vertical resolution pixels/meter
		iYpixelsPerM = in.readIntLE();
		// Colors used - number of colors actually used
		iColorsUsed = in.readIntLE();
		// Colors important - number of important colors 0 = all
		iColorsImportant = in.readIntLE();

		iRedMask = in.readIntLE();
		iGreenMask = in.readIntLE();
		iBlueMask = in.readIntLE();
		iAlphaMask = in.readIntLE();

		iColorSpaceType = in.readIntLE();

		iRedX = in.readIntLE();
		iRedY = in.readIntLE();
		iRedZ = in.readIntLE();
		iGreenX = in.readIntLE();
		iGreenY = in.readIntLE();
		iGreenZ = in.readIntLE();
		iBlueX = in.readIntLE();
		iBlueY = in.readIntLE();
		iBlueZ = in.readIntLE();

		iGammaRed = in.readIntLE();
		iGammaGreen = in.readIntLE();
		iGammaBlue = in.readIntLE();

		iIntent = in.readIntLE();
		iProfileData = in.readIntLE();
		iProfileSize = in.readIntLE();
		iReserved = in.readIntLE();
	}

	/**
	 * Creates an <tt>InfoHeader</tt> with default values.
	 */
	public InfoHeader5()
	{
		// Size of InfoHeader structure = 124
		iSize = BMPConstants.HEADER_LENGTH_5;
		// Width
		iWidth = 0;
		// Height
		iHeight = 0;
		// Planes (=1)
		sPlanes = 1;
		// Bit count
		sBitCount = 0;

		// calculate NumColors
		iNumColors = 0;

		// Compression
		iCompression = BMPConstants.BI_BITFIELDS;
		// Image size - compressed size of image or 0 if Compression = 0
		iImageSize = 0;
		// horizontal resolution pixels/meter
		iXpixelsPerM = 0;
		// vertical resolution pixels/meter
		iYpixelsPerM = 0;
		// Colors used - number of colors actually used
		iColorsUsed = 0;
		// Colors important - number of important colors 0 = all
		iColorsImportant = 0;

		iRedMask = 0x00FF0000;
		iGreenMask = 0x0000FF00;
		iBlueMask = 0x000000FF;
		iAlphaMask = 0xFF000000;
		iColorSpaceType = 0x73524742;
		iRedX = 0;
		iRedY = 0;
		iRedZ = 0;
		iGreenX = 0;
		iGreenY = 0;
		iGreenZ = 0;
		iBlueX = 0;
		iBlueY = 0;
		iBlueZ = 0;
		iGammaRed = 0;
		iGammaGreen = 0;
		iGammaBlue = 0;
		iIntent = 4;
		iProfileData = 0;
		iProfileSize = 0;
		iReserved = 0;
	}

	/**
	 * Creates a copy of the source <tt>InfoHeader</tt>.
	 * 
	 * @param source
	 *            the source to copy
	 */
	public InfoHeader5(InfoHeader5 source)
	{
		iSize = source.iSize;
		iWidth = source.iWidth;
		iHeight = source.iHeight;
		sPlanes = source.sPlanes;
		sBitCount = source.sBitCount;
		iCompression = source.iCompression;
		iImageSize = source.iImageSize;
		iXpixelsPerM = source.iXpixelsPerM;
		iYpixelsPerM = source.iYpixelsPerM;
		iColorsUsed = source.iColorsUsed;
		iColorsImportant = source.iColorsImportant;
		iNumColors = source.iNumColors;
		iRedMask = source.iRedMask;
		iBlueMask = source.iBlueMask;
		iGreenMask = source.iGreenMask;
		iAlphaMask = source.iAlphaMask;
		iColorSpaceType = source.iColorSpaceType;
		iRedX = source.iRedX;
		iRedY = source.iRedY;
		iRedZ = source.iRedZ;
		iGreenX = source.iGreenX;
		iGreenY = source.iGreenY;
		iGreenZ = source.iGreenZ;
		iBlueX = source.iBlueX;
		iBlueY = source.iBlueY;
		iBlueZ = source.iBlueZ;
		iGammaRed = source.iGammaRed;
		iGammaGreen = source.iGammaGreen;
		iGammaBlue = source.iGammaBlue;
		iIntent = source.iIntent;
		iProfileData = source.iProfileData;
		iProfileSize = source.iProfileSize;
		iReserved = source.iReserved;
	}

	/**
	 * Writes the <tt>InfoHeader</tt> structure to output
	 * 
	 * @param out
	 *            the output to which the structure will be written
	 * @throws IOException
	 *             if an error occurs
	 */
	@Override
	public void write(LittleEndianOutputStream out) throws IOException
	{
		// Size of InfoHeader structure = 124
		out.writeIntLE(iSize);
		// Width
		out.writeIntLE(iWidth);
		// Height
		out.writeIntLE(iHeight);
		// Planes (=1)
		out.writeShortLE(sPlanes);
		// Bit count
		out.writeShortLE(sBitCount);

		// caculate NumColors
		// iNumColors = (int) Math.pow(2, sBitCount);

		// Compression
		out.writeIntLE(iCompression);
		// Image size - compressed size of image or 0 if Compression = 0
		out.writeIntLE(iImageSize);
		// horizontal resolution pixels/meter
		out.writeIntLE(iXpixelsPerM);
		// vertical resolution pixels/meter
		out.writeIntLE(iYpixelsPerM);
		// Colors used - number of colors actually used
		out.writeIntLE(iColorsUsed);
		// Colors important - number of important colors 0 = all
		out.writeIntLE(iColorsImportant);

		out.writeUnsignedIntLE(iRedMask);
		out.writeUnsignedIntLE(iGreenMask);
		out.writeUnsignedIntLE(iBlueMask);
		out.writeUnsignedIntLE(iAlphaMask);
		out.writeIntLE(iColorSpaceType);
		out.writeIntLE(iRedX);
		out.writeIntLE(iRedY);
		out.writeIntLE(iRedZ);
		out.writeIntLE(iGreenX);
		out.writeIntLE(iGreenY);
		out.writeIntLE(iGreenZ);
		out.writeIntLE(iBlueX);
		out.writeIntLE(iBlueY);
		out.writeIntLE(iBlueZ);
		out.writeIntLE(iGammaRed);
		out.writeIntLE(iGammaGreen);
		out.writeIntLE(iGammaBlue);
		out.writeIntLE(iIntent);
		out.writeIntLE(iProfileData);
		out.writeIntLE(iProfileSize);
		out.writeIntLE(iReserved);
	}

	@Override
	public int getBmpVersion()
	{
		return 5;
	}

	@Override
	public int getSize()
	{
		return BMPConstants.HEADER_LENGTH_5;
	}

	@Override
	public int getWidth()
	{
		return iWidth;
	}

	@Override
	public int getHeight()
	{
		return iHeight;
	}

	@Override
	public int getCompression()
	{
		return iCompression;
	}

	@Override
	public short getBitCount()
	{
		return sBitCount;
	}

	@Override
	public int getNumColors()
	{
		return iNumColors;
	}

}
