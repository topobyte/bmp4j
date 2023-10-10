package de.topobyte.bmp4j.codec;

public class FileHeader
{

	public String signature;
	public int fileSize;
	public int reserved;
	public int dataOffset;

	public FileHeader(String signature, int fileSize, int reserved,
			int dataOffset)
	{
		this.signature = signature;
		this.fileSize = fileSize;
		this.reserved = reserved;
		this.dataOffset = dataOffset;
	}

}
