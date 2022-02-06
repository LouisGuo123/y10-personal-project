package louis_guo.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import louis_guo.io.ByteReader;

public class NetworkReader extends ByteReader {
	public NetworkReader(String url) throws FileNotFoundException {
		super(url);
	}
	
	public int[] readNodeCounts(int layers) throws IOException {
		int[] output = new int[layers];
		
		for(int i = 0; i < layers; i++) {
			output[i] = readInt();
		}
		
		return output;
	}
	
	public double[][][] readWeights(int layers, int[] node_counts) throws IOException {
		double[][][] output = new double[layers - 1][][];
		
		for(int i = 0; i < layers - 1; i++) {
			output[i] = new double[node_counts[i]][];
			
			for(int j = 0; j < node_counts[i]; j++) {
				output[i][j] = new double[node_counts[i + 1]];
				
				for(int k = 0; k < node_counts[i + 1]; k++) {
					output[i][j][k] = readDouble();
				}
			}
		}
		
		return output;
	}
	
	public double[][] readBiases(int layers, int[] node_counts) throws IOException {
		double[][] output = new double[layers][];
		
		for(int i = 0; i < layers; i++) {
			output[i] = new double[node_counts[i]];
			
			for(int j = 0; j < node_counts[i]; j++) {
				output[i][j] = readDouble();
			}
		}
		
		return output;
	}
}
