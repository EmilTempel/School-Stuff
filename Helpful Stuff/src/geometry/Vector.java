package geometry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Vector implements Iterable<Double>{
	private static final int ANY_DEGREE = -1;
	private static int R = -1;
	private final double[] x;

	public Vector(double... x) {
		int len = R == ANY_DEGREE ? x.length : R;
		this.x = new double[len];
		for (int i = 0; i < len; i++) {
			this.x[i] = i < x.length ? x[i] : 0;
		}
	}
	
	public Vector(List<Double> x) {
		int len = R == ANY_DEGREE ? x.size() : R;
		this.x = new double[len];
		for (int i = 0; i < len; i++) {
			this.x[i] = i < x.size() ? x.get(i) : 0;
		}
	}
	
	public Vector(Stream<Double> x) {
		this(x.toList());
	}
	
	public Vector(int len) {
		len = R == ANY_DEGREE ? len : R;
		this.x = new double[len];
	}

	public Vector(Vector u) {
		this(u.x);
	}

	public double x(int i) {
		return x[i];
	}

	public double x() {
		return x[0];
	}

	public double y() {
		return x[1];
	}

	public double z() {
		return x[2];
	}

	public int deg() {
		return x.length;
	}
	
	public double[] getX() {
		return x;
	}
	 
	public String toString() {
		String str = "(";
		for (int i = 0; i < deg(); i++) {
			str += x[i] + "|";
		}
		return str.substring(0, str.length() - 1) + ")";
	}

	public boolean equals(Object o) {
		if (o instanceof Vector) {
			Vector v = (Vector) o;

			int len = same_deg(this, v);
			boolean same = len == 0 ? false : true;
			for (int i = 0; i < len; i++) {
				if (x(i) != v.x(i)) {
					same = false;
					break;
				}
			}
			return same;
		} else {
			return false;
		}
	}

	@Override
	public Iterator<Double> iterator() {
		return new VectorIterator(this);
	}
	
	public Stream<Double> stream(){
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
	}

	public static double abs(Vector u) {
		double sum = 0;
		for (int i = 0; i < u.deg(); i++) {
			sum += u.x(i) * u.x(i);
		}
		return Math.sqrt(sum);
	}

	public static Vector add(Vector... u) {
		int len = same_deg(u);
		double[] y = new double[len];
		for (int j = 0; j < u.length; j++) {
			for (int i = 0; i < len; i++) {
				y[i] += u[j].x(i);
			}
		}
		return new Vector(y);
	}

	public static Vector sub(Vector... u) {
		for (int i = 1; i < u.length; i++)
			u[i] = mult(u[i], -1);
		return add(u);
	}

	public static Vector mult(Vector u, double s) {
		int len = same_deg(u);
		double[] y = new double[len];
		for (int i = 0; i < len; i++) {
			y[i] = u.x(i) * s;
		}
		return new Vector(y);
	}

	public static Vector norm(Vector u) {
		return mult(u, 1 / abs(u));
	}

	public static double dot(Vector u, Vector v) {
		int len = same_deg(u, v);
		double sum = 0;
		for (int i = 0; i < len; i++) {
			sum += u.x(i) * v.x(i);
		}
		return sum;
	}

	public static Vector cross(Vector... u) {
		int len = same_deg(u) == u.length + 1 ? u.length + 1 : 0;
		double[] v = new double[len];
		for (int i = 0; i < len; i++) {
			double[][] b = new double[len - 1][len - 1];
			int c = 0;
			for (int j = 0; j < len; j++) {
				if (i != j) {
					for (int k = 1; k < len; k++) {
						b[c][k - 1] = u[k - 1].x(j);
					}
					c++;
				}
			}
			Matrix B = new Matrix(b);
			v[i] = (i % 2 == 0 ? 1 : -1) * (Matrix.recursive_Determinant(B));
		}
		return new Vector(v);
	}

	public static Vector orth(Vector... u) {
		int len = same_deg(u) == u.length + 1 ? u.length + 1 : 0;
		double[] x = new double[len];
		for (int i = 0; i < len; i++) {
			x[i] = 1;
		}
		Vector[] v = new Vector[len];
		v[0] = new Vector(x);
		for (int i = 1; i < len; i++) {
			v[i] = u[i - 1];
		}
		for (int i = 1; i < len; i++) {
			v[i] = proj(v[i], v[0]);
		}
		return norm(sub(v));
	}

	public static Vector proj(Vector u, Vector v) {
		return Vector.mult(u, Vector.dot(u, v) / Vector.dot(v, v));
	}

	public static int same_deg(Vector... u) {
		for (int i = 1; i < u.length; i++) {
			if (u[i] == null || u[i - 1].deg() != u[i].deg()) {
				return 0;
			}
		}
		return u[0].deg();
	}

	
	public static void main(String[] args) {
		Vector u = new Vector(0, 0, 3);
		Vector v = new Vector(1, 0, 0);
		Vector c = orth(u, v);
		System.out.println();
		System.out.println(dot(c, u));
		System.out.println(dot(c, v));
		ArrayList<Integer> list = new ArrayList<>();
		list.stream();
	}

	
	
	private class VectorIterator implements Iterator<Double>{
		private Vector v;
		int c;
		
		public VectorIterator(Vector v) {
			this.v = v;
			c = 0;
		}
		
		@Override
		public boolean hasNext() {
			return c < v.deg();
		}

		@Override
		public Double next() {
			return v.x(c++);
		}
	}
}
