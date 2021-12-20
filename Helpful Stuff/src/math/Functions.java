package math;

import java.util.Arrays;

public class Functions {

	public static long ggT(long... x) {
		if (x.length == 0) {
			return 1;
		} else if (x.length == 1) {
			return x[0];
		} else {
			long[] y = x.clone();
			int a = max_index(y);
			int b = min_index(y);
			while (y[a] != y[b]) {
				y[a] -= y[b];
				a = max_index(y);
				b = min_index(y);
			}

			return y[b];
		}
	}

	//
	// Methoden für double
	public static double min(double... x) {
		double min = x.length > 0 ? x[0] : 0;
		for (int i = 1; i < x.length; i++) {
			if (x[i] < min)
				min = x[i];
		}
		return min;
	}

	public static int min_index(double... x) {
		int min = x.length > 0 ? 0 : -1;
		for (int i = 1; i < x.length; i++) {
			if (x[i] < x[min])
				min = i;
		}
		return min;
	}

	public static double max(double... x) {
		double max = x.length > 0 ? x[0] : 0;
		for (int i = 1; i < x.length; i++) {
			if (x[i] > max)
				max = x[i];
		}
		return max;
	}

	public static int max_index(double... x) {
		int max = x.length > 0 ? 0 : -1;
		for (int i = 1; i < x.length; i++) {
			if (x[i] > x[max])
				max = i;
		}
		return max;
	}

	//
	// Methoden für int
	public static long min(long... x) {
		long min = x.length > 0 ? x[0] : 0;
		for (int i = 1; i < x.length; i++) {
			if (x[i] < min)
				min = x[i];
		}
		return min;
	}

	public static int min_index(long... x) {
		int min = x.length > 0 ? 0 : -1;
		for (int i = 1; i < x.length; i++) {
			if (x[i] < x[min])
				min = i;
		}
		return min;
	}

	public static long max(long... x) {
		long max = x.length > 0 ? x[0] : 0;
		for (int i = 1; i < x.length; i++) {
			if (x[i] > max)
				max = x[i];
		}
		return max;
	}

	public static int max_index(long... x) {
		int max = x.length > 0 ? 0 : -1;
		for (int i = 1; i < x.length; i++) {
			if (x[i] > x[max])
				max = i;
		}
		return max;
	}
	
	public static int toDEX(int... arr) {
		int erg = 0;
		for(int i = 0; i < arr.length; i++) {
			erg += (arr[i] == 0 ? 0 : 1 )*Math.pow(2, i);
		}
		return erg;
	}
	
	public static int[] toBIN(int input, int size) {
		int[] bits = new int[size];
	    for (int i = bits.length-1; i >= 0; i--) {
	        bits[i] = (input & (1 << i)) != 0 ? 1 : 0;
	    }
	    return bits;
	}
	
	public static int log2(int N)
    {
  
        // calculate log2 N indirectly
        // using log() method
        int result = (int)(Math.log(N) / Math.log(2));
  
        return result;
    }
	
	public static double map(double x, double x0, double x1, double y0, double y1) {
		return ((x-x0)/(x1-x0)) * (y1-y0) + y0;
	}
	
	public interface Function{
		public abstract <In, Out> In run(Out o);
	}
	
	public interface Executable{
		public abstract void run();
	}

	public static void main(String[] args) {
		System.out.println(map(0,-1,3,0,1));
	}
}
