package de.topobyte.bmp4j.codec;

import java.io.IOException;

import de.topobyte.bmp4j.io.LittleEndianOutputStream;

public interface InfoHeader
{

	public int getBmpVersion();

	public int getSize();

	public int getWidth();

	public int getHeight();

	/**
	 * The compression type, which should be one of the following:
	 * <ul>
	 * <li>{@link BMPConstants#BI_RGB BI_RGB} - no compression</li>
	 * <li>{@link BMPConstants#BI_RLE8 BI_RLE8} - 8-bit RLE compression</li>
	 * <li>{@link BMPConstants#BI_RLE4 BI_RLE4} - 4-bit RLE compression</li>
	 * <li>{@link BMPConstants#BI_BITFIELDS BI_BITFIELDS} - bit field
	 * compression</li>
	 * </ul>
	 */
	public int getCompression();

	public short getBitCount();

	public int getNumColors();

	public void write(LittleEndianOutputStream out) throws IOException;

}
