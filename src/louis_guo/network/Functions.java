package louis_guo.network;

public class Functions {
	public static double sigmoid(double value) {
		return 1 / (1 + Math.exp(-value));
	}
	
	public static double sigmoid_prime(double value) {
		return sigmoid(value) * (1 - sigmoid(value));
	}
}
