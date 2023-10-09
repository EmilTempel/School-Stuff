package geometry2D;

import geometry.Vector;

public abstract class Shape {

	public abstract Vector getFurthestPoint(Vector direction);

	public abstract Vector intersection(Vector p, Vector direction);
	
	public abstract Shape transform(Vector s);

	public Vector distanceVector(Shape p, Vector direction) {
		Vector f = getFurthestPoint(direction);
		Vector s = intersection(f, direction);
		if (s != null) {
			Vector d = Vector.sub(s, f);
			return d;
		} else {
			return null;
		}
	}
}
