package geometry2D;

import geometry.Vector;
import math.Functions;

public class Polygon extends Shape {

	Vector[] vertices;
	LineSection[] lineSections;

	public Polygon(Vector... vertices) {
		assert vertices.length >= 2;

		this.vertices = vertices;
		this.lineSections = new LineSection[vertices.length];

		for (int i = 0; i < vertices.length - 1; i++) {
			lineSections[i] = new LineSection(vertices[i], vertices[i + 1]);
		}
		lineSections[vertices.length - 1] = new LineSection(vertices[vertices.length - 1], vertices[0]);
	}

	@Override
	public Vector getFurthestPoint(Vector direction) {
		double[] dots = new double[vertices.length];
		for (int i = 0; i < vertices.length; i++) {
			dots[i] = Vector.dot(direction, vertices[i]);
		}
		return vertices[Functions.max_index(dots)];
	}

	@Override
	public Vector intersection(Vector p, Vector direction) {
		Vector s = null;
		for (int i = 0; i < lineSections.length; i++) {
			Vector intersection = lineSections[i].intersection(p, direction);
			if (intersection != null) {
				if (s == null || Vector.abs(Vector.sub(intersection, p)) < Vector.abs(Vector.sub(s, p))) {
					s = intersection;
				}
			}
		}
		return s;
	}

	@Override
	public Shape transform(Vector s) {
		Vector[] transformedVertices = new Vector[vertices.length];
		for(int i = 0; i < vertices.length; i++) {
			transformedVertices[i] = Vector.add(vertices[i], s);
		}
		return new Polygon(transformedVertices);
	}
	
	
}
