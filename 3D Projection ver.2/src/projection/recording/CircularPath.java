package projection.recording;

import static geometry.Vector.*;
import static java.lang.Math.*;
import geometry.Vector;

public class CircularPath {
	Vector[] positions;

	public CircularPath(Vector s, double R, double h, int n) {
		double r = Math.sqrt(R * R - h * h);
		// R^2 = h^2 + r^2 ==> r = sqrt(R^2 - h^2)
		s = add(s, new Vector(0, 0, h));

		positions = new Vector[n];

		for (int i = 0; i < n; i++) {
			double alpha = i * 2 * Math.PI / n;
			
			Vector p = new Vector(r * cos(alpha), r* sin(alpha), h);
			positions[i] = p;
			System.out.println(toDegrees(alpha));
		}
	}
	
	public static void main(String[] args) {
		new CircularPath(new Vector(0,0,0), 1, 0, 4);
	}
}
