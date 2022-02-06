package louis_guo;

import java.util.Random;

public class Helper {
	public static void setArray(double[] arr1, double[] arr2) {
		for(int i = 0; i < arr1.length; i++) {
			arr1[i] = arr2[i];
		}
	}
	
	public static void shuffle(Object[] arr, long seed) {
		Random rand = new Random(seed);
		
		for(int i = arr.length - 1; i > 0; i--) {
			int index = rand.nextInt(i + 1);
			
			Object temp = arr[index];
			arr[index] = arr[i];
			arr[i] = temp;
		}
	}
	
	public static int clamp(int clamp_1, int clamp_2, int value) {
		int min = Math.min(clamp_1, clamp_2);
		int max = Math.max(clamp_1, clamp_2);
		
		return Math.min(Math.max(value, min), max);
	}
	
	public static double clamp(double clamp_1, double clamp_2, double value) {
		double min = Math.min(clamp_1, clamp_2);
		double max = Math.max(clamp_1, clamp_2);
		
		return Math.min(Math.max(value, min), max);
	}
}
