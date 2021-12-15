package math;

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
	
	public interface Function{
		public abstract <In, Out> In run(Out o);
	}
	
	public interface Executable{
		public abstract void run();
	}

	public static void main(String[] args) {
		System.out.println("123456hallo Welt!789".replaceAll("[^\\d]", ""));
	}
}
