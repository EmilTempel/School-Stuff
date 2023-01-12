package geometry2D;

import geometry.Vector;

public class Rectangle extends Polygon {
	public Rectangle(Vector midPoint, int width, int height) {
		super(Vector.add(midPoint, new Vector(-width, -height)), Vector.add(midPoint, new Vector(width, -height)),
				Vector.add(midPoint, new Vector(width, height)), Vector.add(midPoint, new Vector(width, height)));
	}
}
