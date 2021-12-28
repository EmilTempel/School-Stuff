package projection.projectables;

import geometry.Vector;
import projection.PointOfView;

public class Sphere extends Projectable{
	
	double R,alpha;
	
	Vector[] system;
	
	public Sphere(PointOfView PoV, double R) {
		super(PoV);
		this.R = R;
		system = new Vector[3];
		update(PoV.getR());
	}
	
	public void update(Vector r) {
		system[0] = Vector.norm(r);
		system[1] = Vector.norm(new Vector(system[0].x(1),-system[0].x(0),0));
		system[2] = Vector.norm(Vector.cross(system[0],system[1]));
	}

	public Vector project(Vector u) {
		Vector M = PoV.getS();
		Vector r = Vector.sub(M, u);
		Vector cross = Vector.mult(Vector.norm(r), R);
		double lat = -Math.asin(Vector.dot(cross, system[2])/R);
		double lon = Math.atan2(Vector.dot(cross,system[1]),Vector.dot(cross, system[0]));
		
		return new Vector(Math.toDegrees(lon),Math.toDegrees(lat));
	}
}
