import java.util.Arrays;

import com.aparapi.Kernel;
import com.aparapi.Range;

public class Test {

	public static float[] addArrays_CPU(float[] a1, float[] a2) {
		float[] erg = new float[a1.length];
		for (int i = 0; i < a1.length; i++) {
			erg[i] = (int)Math.cos(Math.asin(a1[i]) + Math.acos(a2[i]))*a1[i]*a2[i]*a1[i]*a1[i]*a1[i]*a1[i]*a1[i]*a1[i]*a1[i];
		}
		return erg;
	}

	public static float[] addArrays_GPU(float[] a1, float[] a2) {
		final float[] erg = new float[a1.length], A1 = a1, A2 = a2;
		assert (a1.length == a2.length);
		
		Kernel k = new Kernel() {

			public void run() {
				int i = getGlobalId();
				erg[i] = (float)Math.cos(Math.asin(A1[i]) + Math.acos(A2[i]))*A1[i]*A2[i]*A1[i]*A1[i]*A1[i]*A1[i]*A1[i]*A1[i]*A1[i];
			}
		};
		k.setExplicit(true);
		k.put(a1);
		k.put(a2);
		k.execute(Range.create(erg.length));
		System.out.println(k.getExecutionMode());
		k.get(erg);
		k.dispose();
		return erg;
	}

	public static void main(String[] args) {
		int N = 10000000;
		float[] a1 = new float[N], a2 = new float[N];
		for (int i = 0; i < N; i++) {
			a1[i] =(float)Math.random() * 100;
			a2[i] = (float)Math.random() * 100;
		}
		long time = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			addArrays_CPU(a1,a2);
			System.out.println("CPU took: " + (System.currentTimeMillis() - time));
			time = System.currentTimeMillis();
		}

		for (int i = 0; i < 10; i++) {
			addArrays_GPU(a1,a2);
			System.out.println("GPU took: " + (System.currentTimeMillis() - time));
			time = System.currentTimeMillis();
		}
		
	}
}
