package louis_guo.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import louis_guo.io.ByteWriter;

public class NetworkWriter extends ByteWriter {
	public NetworkWriter(String url) throws FileNotFoundException {
		super(url);
	}
	
	private void writeWeights(double[][][] weights) throws IOException {
		for(int i = 0; i < weights.length; i++) {
			for(int j = 0; j < weights[i].length; j++) {
				for(int k = 0; k < weights[i][j].length; k++) {
					writeDouble(weights[i][j][k]);
				}
			}
		}
	}
	
	private void writeBiases(double[][] biases) throws IOException {
		for(int i = 0; i < biases.length; i++) {
			for(int j = 0; j < biases[i].length; j++) {
				writeDouble(biases[i][j]);
			}
		}
	}
	
	public void writeNetwork(int magic, double[][][] weights, double[][] biases, int index, long cycles, long seed) throws IOException {
		writeInt(magic);
		
		writeInt(biases.length);
		for(int i = 0; i < biases.length; i++) {
			writeInt(biases[i].length);
		}
		
		writeWeights(weights);
		writeBiases(biases);
		
		writeInt(index);
		writeLong(cycles);
		writeLong(seed);
	}
}
