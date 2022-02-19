package louis_guo.io;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteWriter {
	BufferedOutputStream stream;
	
	public ByteWriter(String url) throws FileNotFoundException {
		stream = new BufferedOutputStream(new FileOutputStream(url));
	}
	
	public ByteWriter(OutputStream out) {
		stream = new BufferedOutputStream(out);
	}
	
	public void write(byte b) throws IOException {
		stream.write(b);
	}
	
	public void write(byte[] b) throws IOException {
		stream.write(b);
	}
	
	public void writeInt(int n) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(n);
		write(buffer.array());
	}
	
	public void writeFloat(float f) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putFloat(f);
		write(buffer.array());
	}
	
	public void writeLong(long l) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(l);
		write(buffer.array());
	}
	
	public void writeDouble(double d) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putDouble(d);
		write(buffer.array());
	}
	
	public void flush() throws IOException {
		stream.flush();
	}
	
	public void close() throws IOException {
		stream.close();
	}
}
