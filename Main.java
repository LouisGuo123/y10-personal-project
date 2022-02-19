package louis_guo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;
import java.util.Scanner;

import louis_guo.io.NetworkReader;
import louis_guo.io.NetworkWriter;
import louis_guo.io.MNISTReader;
import louis_guo.network.Network;
import louis_guo.rendering.NetworkFrame;

public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		System.out.print("Getting MNIST data... ");
		
		InputStream training_label_reader_stream = Main.class.getResourceAsStream("/louis_guo/train-labels-idx1-ubyte");
		InputStream training_image_reader_stream = Main.class.getResourceAsStream("/louis_guo/train-images-idx3-ubyte");
		
		MNISTReader training_label_reader = new MNISTReader(training_label_reader_stream);
		MNISTReader training_image_reader = new MNISTReader(training_image_reader_stream);
		
		InputStream testing_label_reader_stream = Main.class.getResourceAsStream("/louis_guo/t10k-labels-idx1-ubyte");
		InputStream testing_image_reader_stream = Main.class.getResourceAsStream("/louis_guo/t10k-images-idx3-ubyte");
		
		MNISTReader testing_label_reader = new MNISTReader(testing_label_reader_stream);
		MNISTReader testing_image_reader = new MNISTReader(testing_image_reader_stream);
		
		System.out.println("done!");
		
		//  ----------------------------------------------------------------  //
		
		System.out.print("Reading network data... ");
		
		String network_url = System.getProperty("user.home") + "/java_network/network-byte-2h128";
		
		File network_tmp = new File(network_url);
		network_tmp.getParentFile().mkdirs();
		network_tmp.createNewFile();
		
		NetworkReader network_reader = new NetworkReader(network_url);
		
		int start_index;
		long cycles;
		long seed;
		
		Random rand = new Random(ThreadLocalRandom.current().nextLong());
		
		Network network;
		
		int magic = network_reader.readInt();
		if(magic != 1053) {
			System.out.print("no valid network found... ");
			
			network = new Network(new int[] {784, 128, 128, 10});
			start_index = 0;
			cycles = 0;
			
			seed = rand.nextLong();
		}
		else {
			System.out.print("network found... ");
			
			int layers = network_reader.readInt();
			int[] node_counts = network_reader.readNodeCounts(layers);
			double[][][] weights = network_reader.readWeights(layers, node_counts);
			double[][] biases = network_reader.readBiases(layers, node_counts);
			
			start_index = network_reader.readInt();
			cycles = network_reader.readLong();
			seed = network_reader.readLong();
			
			network = new Network(node_counts, weights, biases);
		}
		
		System.out.println("done!");
		
		//  ----------------------------------------------------------------  //
		
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Network setting: ");
		String setting = sc.nextLine();
		
		if(setting.equals("training")) {
			System.out.print("Reading MNIST training data... ");
			
			double[][] training_labels = training_label_reader.readLabels();
			double[][] training_images = training_image_reader.readImages();
			
			System.out.println("done!");
			
			//  ----------------------------------------------------------------  //
			
			System.out.println("Initializing other variables... ");
			
			int training_size = training_images.length;
			
			System.out.print("Number of iterations (multiplier of training size): ");
			int iterations = (int)(training_size * Double.parseDouble(sc.nextLine()));
			int training_index = start_index;
			
			System.out.print("Learning rate (exponent of divisor): ");
			double learning_rate = 0.005 / Math.pow(2, Double.parseDouble(sc.nextLine()));
			
			System.out.print("Batch size: ");
			int batch_size = Integer.parseInt(sc.nextLine());
			
			Helper.shuffle(training_labels, seed);
			Helper.shuffle(training_images, seed);
			
			network.learning_rate = learning_rate;
			
			long start_time = System.nanoTime();
			
			System.out.println("Done!");
			
			//  ----------------------------------------------------------------  //
			
			System.out.println("Training from index " + start_index + " for " + iterations + " iterations...");
			
			for(int i = 0; i < iterations; i++, training_index = (training_index + 1) % training_size) {
				if((i + 1) % training_size == 0) {
					long end_time = System.nanoTime();
					
					System.out.println((i + 1) + " runs done in " + (double)(end_time - start_time) / 1000000000.0 + " seconds!");
				}
				
				network.backpropogate(training_images[training_index], training_labels[training_index], batch_size == 1);
				
				if(((i + 1) % batch_size == 0) && batch_size != 1) {
					network.batch_update();
				}
				
				if(training_index == training_size - 1) {
					cycles++;
					
					seed = rand.nextLong();
					
					Helper.shuffle(training_labels, seed);
					Helper.shuffle(training_images, seed);
				}
			}
			
			System.out.println("Done!");
			
			//  ----------------------------------------------------------------  //
			
			System.out.println("Calculating Average Cost... ");
			
			double average_cost = 0.0;
			int correct = 0;
			for(int i = 0; i < training_size; i++) {
				double cost = network.calculateCost(training_images[i], training_labels[i]);
				
				cost /= (double)training_size;
				
				average_cost += cost;
				
				double max_value_expected = training_labels[i][0];
				int max_j_expected = 0;
				
				double max_value = network.getOutput()[0];
				int max_j = 0;
				for(int j = 1; j < network.getOutput().length; j++) {
					if(network.getOutput()[j] > max_value) {
						max_value = network.getOutput()[j];
						max_j = j;
					}
					
					if(training_labels[i][j] > max_value_expected) {
						max_value_expected = training_labels[i][j];
						max_j_expected = j;
					}
				}
				
				if(max_j == max_j_expected) {
					correct++;
				}
			}
			
			System.out.println("Average cost of " + average_cost + ", with " + (double)correct * 100 / (double)training_size + "% correct.");
			
			System.out.println("Ending at index " + training_index + ".");
			
			System.out.print("Do you want to keep changes? ");
			String input = sc.nextLine();
			if(input.equalsIgnoreCase("yes")) {
				System.out.print("Writing network... ");
				
				NetworkWriter network_writer = new NetworkWriter(network_url);
				network_writer.writeNetwork(1053, network.weights, network.biases, training_index, cycles, seed);
				network_writer.flush();
				
				System.out.println("done!");
			}
		}
		
		else if(setting.equals("testing")) {
			System.out.print("Reading MNIST testing data... ");
			
			double[][] testing_labels = testing_label_reader.readLabels();
			double[][] testing_images = testing_image_reader.readImages();
			
			System.out.println("done!");
			
			System.out.print("Initializing other variables... ");
			
			int testing_size = testing_images.length;
			
			int[] wrong = new int[network.getOutput().length];
			
			System.out.println("done!");
			
			System.out.println("Calculating Average Cost... ");
			
			double average_cost = 0.0;
			int correct = 0;
			
			for(int i = 0; i < testing_size; i++) {
				double cost = network.calculateCost(testing_images[i], testing_labels[i]);
				cost /= (double)testing_size;
				
				average_cost += cost;
				
				double max_value_expected = testing_labels[i][0];
				int max_j_expected = 0;
				
				double max_value = network.getOutput()[0];
				int max_j = 0;
				
				for(int j = 1; j < network.getOutput().length; j++) {
					if(network.getOutput()[j] > max_value) {
						max_value = network.getOutput()[j];
						max_j = j;
					}
					
					if(testing_labels[i][j] > max_value_expected) {
						max_value_expected = testing_labels[i][j];
						max_j_expected = j;
					}
				}
				
				if(max_j == max_j_expected) {
					correct++;
				}
				else {
					wrong[max_j_expected]++;
				}
			}
			
			System.out.println("Average cost of " + average_cost + ", with " + (double)correct * 100 / (double)testing_size + "% correct.");
			
			@SuppressWarnings("unused")
			NetworkFrame frame = new NetworkFrame(testing_images, testing_labels, network);
		}
		
		else {
			System.out.println("Invalid setting.");
		}
		
		sc.close();
	}
}
