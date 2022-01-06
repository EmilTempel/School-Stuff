package geometry;

public class Matrix {

	int row, col;

	double[][] x;

	public Matrix(Vector... u) {
		row = Vector.same_deg(u);
		col = u.length;

		x = new double[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				x[i][j] = u[j].x(i);
			}
		}
	}

	public Matrix(double[][] x) {
		row = x.length;
		col = x[0].length;

		this.x = new double[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				this.x[i][j] = j < x[i].length ? x[i][j] : 0;
			}
		}
	}

	public double x(int i, int j) {
		return x[i][j];
	}

	public int row() {
		return row;
	}

	public int col() {
		return col;
	}

	public Vector deg() {
		return new Vector(new double[] { row, col });
	}

	public boolean isSquare() {
		if (col == row) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isTriangular() {
		boolean upper = true;
		boolean lower = true;
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < i; j++) {
				if (x[i][j] != 0)
					lower = false;

				if (x[j][i] != 0)
					upper = false;

				if (!upper && !lower) {
					return false;
				}
			}
		}
		return true;
	}

	public static Vector mult(Matrix A, Vector v) {
		assert A.deg().y() == v.deg() : "Die passen gar nicht zusammen, du kleiner Hurensohn";
		double[] x = new double[(int) A.deg().x()];
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < v.deg(); j++) {
				x[i] += A.x(i, j) * v.x(j);
			}
		}
		return new Vector(x);
	}

	public static double recursive_Determinant(Matrix A) {
		if (A.isSquare()) {
			int deg = A.row();
			if (deg == 0) {
				return Double.NaN;
			} else if (deg == 1) {
				return A.x(0, 0);
			} else {
				double erg = 0;
				for (int i = 0; i < deg; i++) {
					double[][] b = new double[deg - 1][deg - 1];
					int c = 0;
					for (int j = 0; j < deg; j++) {
						if (i != j) {
							for (int k = 1; k < deg; k++) {
								b[c][k - 1] = A.x(j, k);
							}
							c++;
						}
					}
					Matrix B = new Matrix(b);
					erg += (i % 2 == 0 ? 1 : -1) * A.x(i, 0) * (recursive_Determinant(B));
				}
				return erg;
			}
		} else {
			return Double.NaN;
		}
	}

	public static void main(String[] args) {
		Vector v = new Vector(2, -1, 3);
		Matrix A = new Matrix(new double[][] { { 2, 3, -2 }, { 4, 2, 1 }, {-2,5,3} });
		System.out.println(Matrix.mult(A, v));
	}
}
