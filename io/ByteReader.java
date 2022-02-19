package louis_guo.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteReader {
	private BufferedInputStream stream;
	
	public ByteReader(String url) throws FileNotFoundException {
		stream = new BufferedInputStream(new FileInputStream(url));
	}
	
	public ByteReader(InputStream in_stream) {
		stream = new BufferedInputStream(in_stream);
	}
	
	public byte read() throws IOException {
		return (byte)stream.read();
	}
	
	public int readInt() throws IOException {
		byte[] wrapping = new byte[4];
		for(int i = 0; i < 4; i++) {
			wrapping[i] = read();
		}
		return ByteBuffer.wrap(wrapping).getInt();
	}
	
	public float readFloat() throws IOException {
		byte[] wrapping = new byte[4];
		for(int i = 0; i < 4; i++) {
			wrapping[i] = read();
		}
		return ByteBuffer.wrap(wrapping).getFloat();
	}
	
	public long readLong() throws IOException {
		byte[] wrapping = new byte[8];
		for(int i = 0; i < 8; i++) {
			wrapping[i] = read();
		}
		return ByteBuffer.wrap(wrapping).getLong();
	}
	
	public double readDouble() throws IOException {
		byte[] wrapping = new byte[8];
		for(int i = 0; i < 8; i++) {
			wrapping[i] = read();
		}
		return ByteBuffer.wrap(wrapping).getDouble();
	}
	
	public void close() throws IOException {
		stream.close();
	}
}
