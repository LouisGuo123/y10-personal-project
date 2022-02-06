package louis_guo.io;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;

import louis_guo.io.ByteReader;

public class MNISTReader extends ByteReader {
	public MNISTReader(String url) throws FileNotFoundException {
		super(url);
	}
	
	public MNISTReader(InputStream in_stream) {
		super(in_stream);
	}
	
	public double[][] readLabels() throws IOException {
		int magic = readInt();
		if(magic != 2049) {
			return new double[][] {};
		}
		
		int length = readInt();
		double[][] output = new double[length][10];
		
		for(int i = 0; i < length; i++) {
			byte label = read();
			output[i][label] = 1.0;
		}
		
		return output;
	}
	
	public double[][] readImages() throws IOException {
		int magic = readInt();
		if(magic != 2051) {
			return new double[][] {};
		}
		
		int length = readInt();
		int rows = readInt();
		int columns = readInt();
		
		double[][] output = new double[length][rows * columns];
		
		for(int i = 0; i < length; i++) {
			for(int j = 0; j < rows * columns; j++) {
				byte value = read();
				output[i][j] = (double)Byte.toUnsignedInt(value) / 255.0;
			}
		}
		
		return output;
	}
}
