package de.topobyte.bmp4j.codec;

import java.awt.image.BufferedImage;

/**
 * Contains a decoded BMP image, as well as information about the source encoded
 * image.
 * 
 * @author Ian McDonagh
 */
public class BMPImage
{

	protected InfoHeader infoHeader;
	protected BufferedImage image;

	/**
	 * Creates a new instance of BMPImage
	 * 
	 * @param image
	 *            the decoded image
	 * @param infoHeader
	 *            the InfoHeader structure providing information about the
	 *            source encoded image
	 */
	public BMPImage(BufferedImage image, InfoHeader infoHeader)
	{
		this.image = image;
		this.infoHeader = infoHeader;
	}

	/**
	 * The InfoHeader structure representing the encoded BMP image.
	 */
	public InfoHeader getInfoHeader()
	{
		return infoHeader;
	}

	/**
	 * Sets the InfoHeader structure used for encoding the BMP image.
	 */
	public void setInfoHeader(InfoHeader infoHeader)
	{
		this.infoHeader = infoHeader;
	}

	/**
	 * The decoded BMP image.
	 */
	public BufferedImage getImage()
	{
		return image;
	}

	/**
	 * Sets the image to be encoded.
	 */
	public void setImage(BufferedImage image)
	{
		this.image = image;
	}

	/**
	 * The width of the BMP image in pixels.
	 * 
	 * @return the width of the BMP image, or <tt>-1</tt> if unknown
	 */
	public int getWidth()
	{
		return infoHeader == null ? -1 : infoHeader.getWidth();
	}

	/**
	 * The height of the BMP image in pixels.
	 * 
	 * @return the height of the BMP image, or <tt>-1</tt> if unknown.
	 */
	public int getHeight()
	{
		return infoHeader == null ? -1 : infoHeader.getHeight();
	}

	/**
	 * The color depth of the BMP image (bits per pixel).
	 * 
	 * @return the color depth, or <tt>-1</tt> if unknown.
	 */
	public int getColorDepth()
	{
		return infoHeader == null ? -1 : infoHeader.getBitCount();
	}

	/**
	 * The number of possible colors for the BMP image.
	 * 
	 * @return the number of colors, or <tt>-1</tt> if unknown.
	 */
	public int getColorCount()
	{
		int bpp = infoHeader.getBitCount() == 32 ? 24
				: infoHeader.getBitCount();
		return bpp == -1 ? -1 : (int) (1 << bpp);
	}

	/**
	 * Specifies whether this BMP image is indexed, that is, the encoded bitmap
	 * uses a color table. If <tt>getColorDepth()</tt> returns <tt>-1</tt>,
	 * the return value has no meaning.
	 * 
	 * @return <tt>true</tt> if indexed, <tt>false</tt> if not.
	 */
	public boolean isIndexed()
	{
		return infoHeader == null ? false : infoHeader.getBitCount() <= 8;
	}

}
