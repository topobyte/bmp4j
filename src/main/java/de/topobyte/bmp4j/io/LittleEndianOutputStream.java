package de.topobyte.bmp4j.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Writes little-endian data to a target <tt>OutputStream</tt> by reversing byte
 * ordering.
 * 
 * @author Ian McDonagh
 */
public class LittleEndianOutputStream extends DataOutputStream
{

	/**
	 * Creates a new instance of <tt>LittleEndianOutputStream</tt>, which will
	 * write to the specified target.
	 * 
	 * @param out
	 *            the target <tt>OutputStream</tt>
	 */
	public LittleEndianOutputStream(OutputStream out)
	{
		super(out);
	}

	/**
	 * Writes a little-endian <tt>short</tt> value
	 * 
	 * @param value
	 *            the source value to convert
	 * @throws IOException
	 *             if an error occurs
	 */
	public void writeShortLE(short value) throws IOException
	{
		value = EndianUtils.swapShort(value);
		super.writeShort(value);
	}

	/**
	 * Writes a little-endian <tt>int</tt> value
	 * 
	 * @param value
	 *            the source value to convert
	 * @throws IOException
	 *             if an error occurs
	 */
	public void writeIntLE(int value) throws IOException
	{
		value = EndianUtils.swapInteger(value);
		super.writeInt(value);
	}

	/**
	 * Writes a little-endian <tt>float</tt> value
	 * 
	 * @param value
	 *            the source value to convert
	 * @throws IOException
	 *             if an error occurs
	 */
	public void writeFloatLE(float value) throws IOException
	{
		value = EndianUtils.swapFloat(value);
		super.writeFloat(value);
	}

	/**
	 * Writes a little-endian <tt>long</tt> value
	 * 
	 * @param value
	 *            the source value to convert
	 * @throws IOException
	 *             if an error occurs
	 */
	public void writeLongLE(long value) throws IOException
	{
		value = EndianUtils.swapLong(value);
		super.writeLong(value);
	}

	/**
	 * Writes a little-endian <tt>double</tt> value
	 * 
	 * @param value
	 *            the source value to convert
	 * @throws IOException
	 *             if an error occurs
	 */
	public void writeDoubleLE(double value) throws IOException
	{
		value = EndianUtils.swapDouble(value);
		super.writeDouble(value);
	}

	/**
	 * @since 0.6
	 */
	public void writeUnsignedInt(long value) throws IOException
	{
		int i1 = (int) (value >> 24);
		int i2 = (int) ((value >> 16) & 0xFF);
		int i3 = (int) ((value >> 8) & 0xFF);
		int i4 = (int) (value & 0xFF);

		write(i1);
		write(i2);
		write(i3);
		write(i4);
	}

	/**
	 * @since 0.6
	 */
	public void writeUnsignedIntLE(long value) throws IOException
	{
		int i1 = (int) (value >> 24);
		int i2 = (int) ((value >> 16) & 0xFF);
		int i3 = (int) ((value >> 8) & 0xFF);
		int i4 = (int) (value & 0xFF);

		write(i4);
		write(i3);
		write(i2);
		write(i1);
	}
}
