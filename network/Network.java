package louis_guo.network;

import java.util.concurrent.ThreadLocalRandom;

public class Network {
	public double[][] nodes;
	public double[][][] weights;
	public double[][] biases;
	
	private double[][] activations;
	private double[][] errors;
	
	private double[][][] weights_delta;
	private double[][] biases_delta;
	
	private int batch_size = 0;
	
	public double learning_rate = 0.005;
	
	public boolean print_debug = false;
	
	public Network(int[] node_counts) {
		int layers = node_counts.length;
		
		nodes = new double[layers][];
		weights = new double[layers - 1][][];
		biases = new double[layers][];
		
		activations = new double[layers][];
		errors = new double[layers][];
		
		weights_delta = new double[layers - 1][][];
		biases_delta = new double[layers][];
		
		for(int i = 0; i < layers; i++) {
			nodes[i] = new double[node_counts[i]];
			biases[i] = new double[node_counts[i]];
			
			activations[i] = new double[node_counts[i]];
			errors[i] = new double[node_counts[i]];
			
			biases_delta[i] = new double[node_counts[i]];
			
			for(int j = 0; j < node_counts[i]; j++) {
				biases[i][j] = 0;
			}
		}
		
		for(int i = 0; i < layers - 1; i++) {
			weights[i] = new double[node_counts[i]][];
			
			weights_delta[i] = new double[node_counts[i]][];
			
			for(int j = 0; j < node_counts[i]; j++) {
				weights[i][j] = new double[node_counts[i + 1]];
				
				weights_delta[i][j] = new double[node_counts[i + 1]];
				
				for(int k = 0; k < node_counts[i + 1]; k++) {
					weights[i][j][k] = ThreadLocalRandom.current().nextGaussian() * Math.sqrt(2.0 / weights[i].length);
				}
			}
		}
	}
	
	public Network(int[] node_counts, double[][][] weights, double[][] biases) {
		int layers = node_counts.length;
		
		nodes = new double[layers][];
		this.weights = weights;
		this.biases = biases;
		
		activations = new double[layers][];
		errors = new double[layers][];
		
		weights_delta = new double[layers - 1][][];
		biases_delta = new double[layers][];
		
		for(int i = 0; i < layers; i++) {
			nodes[i] = new double[node_counts[i]];
			
			activations[i] = new double[node_counts[i]];
			errors[i] = new double[node_counts[i]];
			
			biases_delta[i] = new double[node_counts[i]];
		}
		
		for(int i = 0; i < layers - 1; i++) {
			weights_delta[i] = new double[node_counts[i]][];
			
			for(int j = 0; j < node_counts[i]; j++) {
				weights_delta[i][j] = new double[node_counts[i + 1]];
			}
		}
	}
	
	/* Node Functions */
	public double activation_function(double input) {
		return Math.max(input, 0);
	}
	
	public double activation_function_prime(double input) {
		return input > 0 ? 1 : 0;
	}
	
	public double output_function(double input) {
		return Functions.sigmoid(input);
	}
	
	public double output_function_prime(double input) {
		return Functions.sigmoid_prime(input);
	}
	
	public double cost_function(double input, double expected) {
		if(input == 0) {
			if(expected == 0) {
				return 0;
			}
			else {
				return Double.MAX_VALUE;
			}
		}
		if(input == 1) {
			if(expected == 1) {
				return 0;
			}
			else {
				return Double.MAX_VALUE;
			}
		}
		return -(expected * Math.log(input) + (1 - expected) * Math.log(1 - input));
	}
	
	public double cost_function_prime(double input, double expected) {
		if(input == 0) {
			if(expected == 0) {
				return 1;
			}
			else {
				return Double.MIN_VALUE;
			}
		}
		if(input == 1) {
			if(expected == 1) {
				return -1;
			}
			else {
				return Double.MAX_VALUE;
			}
		}
		
		return -(expected / input) + ((1 - expected) / (1 - input));
	}
	
	/* Get Functions */
	public double getNode(int layer, int index) {
		return nodes[layer][index];
	}
	
	public double getWeight(int layer, int start, int end) {
		return weights[layer][start][end];
	}
	
	public double getBias(int layer, int index) {
		return biases[layer][index];
	}
	
	private double getActivation(int layer, int index) {
		return activations[layer][index];
	}
	
	private double getError(int layer, int index) {
		return errors[layer][index];
	}
	
	private double getWeightDelta(int layer, int start, int end) {
		return weights_delta[layer][start][end];
	}
	
	private double getBiasDelta(int layer, int index) {
		return biases_delta[layer][index];
	}
	
	public int getLayers() {
		return nodes.length;
	}
	
	public int getLayerSize(int layer) {
		return nodes[layer].length;
	}
	
	public double[] getInput() {
		return nodes[0];
	}
	
	public double[] getOutput() {
		return nodes[getLayers() - 1];
	}
	
	public int[] getNodeCounts() {
		int[] output = new int[getLayers()];
		
		for(int i = 0; i < getLayers(); i++) {
			output[i] = getLayerSize(i);
		}
		
		return output;
	}
	
	/* Set Functions */
	public void setNode(int layer, int index, double value) {
		nodes[layer][index] = value;
	}
	
	public void setWeight(int layer, int start, int end, double value) {
		weights[layer][start][end] = value;
	}
	
	public void setBias(int layer, int index, double value) {
		biases[layer][index] = value;
	}
	
	private void setActivation(int layer, int index, double value) {
		activations[layer][index] = value;
	}
	
	private void setError(int layer, int index, double value) {
		errors[layer][index] = value;
	}
	
	private void setWeightDelta(int layer, int start, int end, double value) {
		weights_delta[layer][start][end] = value;
	}
	
	private void setBiasDelta(int layer, int index, double value) {
		biases_delta[layer][index] = value;
	}
	
	public void setInput(double[] input) {
		for(int i = 0; i < getLayerSize(0); i++) {
			getInput()[i] = input[i];
		}
	}
	
	/* Calculation Functions */
	public double calculateActivation(int layer, int index) {
		double activation = 0;
		for(int i = 0; i < getLayerSize(layer - 1); i++) {
			activation += getNode(layer - 1, i) * getWeight(layer - 1, i, index);
		}
		
		activation += getBias(layer, index);
		
		return activation;
	}
	
	public void feedforward() {
		for(int i = 1; i < getLayers() - 1; i++) {
			for(int j = 0; j < getLayerSize(i); j++) {
				double result = calculateActivation(i, j);
				setActivation(i, j, result);
				setNode(i, j, activation_function(result));
			}
		}
		
		for(int i = 0; i < getOutput().length; i++) {
			double result = calculateActivation(getLayers() - 1, i);
			setActivation(getLayers() - 1, i, result);
			setNode(getLayers() - 1, i, output_function(result));
		}
	}
	
	public void feedforward(double[] input) {
		setInput(input);
		feedforward();
	}
	
	private double calculateCost(double output, double expected) {
		return cost_function(output, expected);
	}
	
	public double calculateCost(double[] expected) {
		double output = 0;
		for(int i = 0; i < getOutput().length; i++) {
			output += calculateCost(getOutput()[i], expected[i]);
		}
		
		output /= getOutput().length;
		return output;
	}
	
	public double calculateCost(double[] input, double[] expected) {
		setInput(input);
		feedforward();
		return calculateCost(expected);
	}
	
	public void calculateNodeError(int layer, int index, double[] expected) {
		double output = 0.0;
		
		if(layer == getLayers() - 1) {
			output = cost_function_prime(getNode(layer, index), expected[index]) * output_function_prime(getActivation(layer, index));
			
			if(print_debug) {
				System.out.println(getNode(layer, index));
				System.out.println(expected[index]);
			}
			
			setError(layer, index, output);
			return;
		}
		
		for(int i = 0; i < getLayerSize(layer + 1); i++) {
			output += getError(layer + 1, i) * getWeight(layer, index, i);
		}
		
		output *= activation_function_prime(getActivation(layer, index));
		
		setError(layer, index, output);
	}
	
	public void backpropogate(double[] expected, boolean isDirect) {
		if(isDirect) {
			for(int i = getLayers() - 1; i > 0; i--) {
				for(int j = 0; j < getLayerSize(i); j++) {
					calculateNodeError(i, j, expected);
				}
			}
			
			for(int i = 0; i < getLayers() - 1; i++) {
				for(int j = 0; j < getLayerSize(i); j++) {
					for(int k = 0; k < getLayerSize(i + 1); k++) {
						double delta = getNode(i, j) * getError(i + 1, k) * learning_rate;
						setWeight(i, j, k, getWeight(i, j ,k) - delta);
					}
				}
			}
			
			for(int i = 0; i < getLayers(); i++) {
				for(int j = 0; j < getLayerSize(i); j++) {
					double delta = getError(i, j) * learning_rate;
					setBias(i, j, getBias(i, j) - delta);
				}
			}
		}
		
		else {
			for(int i = getLayers() - 1; i > 0; i--) {
				for(int j = 0; j < getLayerSize(i); j++) {
					calculateNodeError(i, j, expected);
				}
			}
			
			for(int i = 0; i < getLayers() - 1; i++) {
				for(int j = 0; j < getLayerSize(i); j++) {
					for(int k = 0; k < getLayerSize(i + 1); k++) {
						double delta = getNode(i, j) * getError(i + 1, k) * learning_rate;
						setWeightDelta(i, j, k, getWeightDelta(i, j ,k) - delta);
					}
				}
			}
			
			for(int i = 0; i < getLayers(); i++) {
				for(int j = 0; j < getLayerSize(i); j++) {
					double delta = getError(i, j) * learning_rate;
					setBiasDelta(i, j, getBiasDelta(i, j) - delta);
				}
			}
			
			batch_size++;
		}
	}
	
	public void backpropogate(double[] input, double[] expected, boolean isDirect) {
		setInput(input);
		feedforward();
		backpropogate(expected, isDirect);
	}
	
	public void batch_update() {
		for(int i = 0; i < getLayers() - 1; i++) {
			for(int j = 0; j < getLayerSize(i); j++) {
				for(int k = 0; k < getLayerSize(i + 1); k++) {
					setWeight(i, j, k, getWeight(i, j, k) + (getWeightDelta(i, j, k) / (double)batch_size));
					setWeightDelta(i, j, k, 0.0);
				}
			}
		}
		
		for(int i = 0; i < getLayers(); i++) {
			for(int j = 0; j < getLayerSize(i); j++) {
				setBias(i, j, getBias(i, j) + (getBiasDelta(i, j) / (double)batch_size));
				setBiasDelta(i, j, 0.0);
			}
		}
		
		batch_size = 0;
	}
}
