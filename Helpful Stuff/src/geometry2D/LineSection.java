package geometry2D;

import geometry.Vector;

public class LineSection {

	Vector[] s;
	Vector r;

	public LineSection(Vector s1, Vector s2) {
		s = new Vector[] { s1, s2 };
		this.r = Vector.sub(s2, s1);
	}

	public Vector intersection(Vector p, Vector direction) {
		if (cross2D(direction, r) != 0) {
			double lambda = (cross2D(direction, p) - cross2D(direction, s[0])) / (cross2D(direction, r));

			if (0 <= lambda && lambda <= 1) {
				return Vector.add(s[0], Vector.mult(r, lambda));
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public Vector furthestIn(Vector d) {
		Vector m = Vector.add(s[0], Vector.mult(r, 0.5));

		if (Vector.dot(Vector.sub(s[0], m), d) > Vector.dot(Vector.sub(s[1], m), d)) {
			return s[0];
		} else {
			return s[1];
		}
	}

	@Override
	public String toString() {
		return "[" + s[0] + "->" + s[1] + "]";
	}

	public static LineSection distance(LineSection l, LineSection m) {
		Vector[] closest = new Vector[2];
		double distance = Double.POSITIVE_INFINITY;

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				double d = Vector.abs(Vector.sub(l.s[i], m.s[j]));
				if (d < distance) {
					distance = d;
					closest = new Vector[] { l.s[i], m.s[j] };
				}
			}
		}

		if (cross2D(l.r, m.r) == 0) {
			Vector intersection = m.intersection(closest[0], new Vector(-l.r.y(), l.r.x()));

			if (intersection != null) {
				return new LineSection(closest[0], intersection);
			} else {
				return new LineSection(closest[0], closest[1]);
			}

		} else {
			Vector lm = m.intersection(l.s[0], l.r);
			Vector ml = l.intersection(m.s[0], m.r);

			if (lm == null && ml == null) {
				return null;
			} else if (lm != null) {
				if (Vector.abs(Vector.sub(l.s[0], lm)) < Vector.abs(Vector.sub(l.s[1], lm))) {
					return new LineSection(l.s[0], lm);
				} else {
					return new LineSection(l.s[1], lm);
				}
			} else {
				if (Vector.abs(Vector.sub(m.s[0], ml)) < Vector.abs(Vector.sub(m.s[1], ml))) {
					return new LineSection(m.s[0], ml);
				} else {
					return new LineSection(m.s[1], ml);
				}
			}
		}
	}

	public static double cross2D(Vector a, Vector b) {
		return a.x() * b.y() - a.y() * b.x();
	}

	public static void main(String[] args) {
		LineSection s1 = new LineSection(new Vector(1, 1), new Vector(1, -1));
		LineSection s2 = new LineSection(new Vector(0, 0), new Vector(0, -1));
		long nano = System.nanoTime();
		for (int i = 0; i < 10000; i++)
			distance(s1, s2);
		long now = System.nanoTime();
		System.out.println(now - nano);
	}
}
