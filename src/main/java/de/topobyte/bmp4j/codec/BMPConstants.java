package de.topobyte.bmp4j.codec;

/**
 * Provides constants used with BMP format.
 * 
 * @author Ian McDonagh
 */
public class BMPConstants
{

	private BMPConstants()
	{
	}

	public static final int HEADER_LENGTH_3 = 40;
	public static final int HEADER_LENGTH_5 = 124;

	/**
	 * The signature for the BMP format header "BM".
	 */
	public static final String FILE_HEADER = "BM";

	/**
	 * Specifies no compression.
	 * 
	 * @see InfoHeader#getCompression() InfoHeader
	 */
	public static final int BI_RGB = 0; // no compression

	/**
	 * Specifies 8-bit RLE compression.
	 * 
	 * @see InfoHeader#getCompression() InfoHeader
	 */
	public static final int BI_RLE8 = 1; // 8bit RLE compression

	/**
	 * Specifies 4-bit RLE compression.
	 * 
	 * @see InfoHeader#getCompression() InfoHeader
	 */
	public static final int BI_RLE4 = 2; // 4bit RLE compression

	/**
	 * Specifies 16-bit or 32-bit "bit field" compression.
	 * 
	 * @see InfoHeader#getCompression() InfoHeader
	 */
	public static final int BI_BITFIELDS = 3; // 16bit or 32bit "bit field"
												// compression.

	/**
	 * Specifies JPEG compression.
	 * 
	 * @see InfoHeader#getCompression() InfoHeader
	 */
	public static final int BI_JPEG = 4; // _JPEG compression

	/**
	 * Specifies PNG compression.
	 * 
	 * @see InfoHeader#getCompression() InfoHeader
	 */
	public static final int BI_PNG = 5; // PNG compression
}
