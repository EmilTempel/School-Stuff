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
		row = x[0].length;
		col = x.length;

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
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < i; j++) {
				if(x[i][j] != 0)
					lower = false;
				
				if(x[j][i] != 0)
					upper = false;
				
				if(!upper && !lower) {
					return false;
				}
			}
		}
		return true;
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
		Matrix A = new Matrix(
				new double[][] { 
					new double[] { 4, 2, 4 },
					new double[] { 0, 2, 8 }, 
					new double[] { 0, 0, 3 } });
		System.out.println(recursive_Determinant(A));
		System.out.println(A.isTriangular());
	}
}
