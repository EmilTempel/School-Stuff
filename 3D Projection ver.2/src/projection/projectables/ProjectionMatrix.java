package projection.projectables;

import java.awt.Dimension;
import java.awt.Point;

import geometry.Matrix;
import geometry.Vector;
import projection.PointOfView;

public class ProjectionMatrix extends Projectable {

	Dimension d;
	double a, f, q, Znear, Zfar;

	Matrix proj;

	Vector[] system;

	public ProjectionMatrix(PointOfView PoV, Dimension d, double FOV, double Znear, double Zfar) {
		super(PoV);
		system = new Vector[3];
		this.d = d;
		this.Znear = Znear;
		this.Zfar = Zfar;

		a = d.getWidth() / d.getHeight();
		f = 1 / Math.tan(Math.toRadians(FOV/ 2) );
		q = Zfar / (Zfar - Znear);

		proj = new Matrix(new double[][] { { a * f, 0, 0, 0 }, { 0, f, 0, 0 }, { 0, 0, q, -Znear * q }, { 0, 0, 1, 0} });

	}

	@Override
	public void update(Vector r) {
		system[0] = Vector.norm(r);
		system[1] = Vector.norm(new Vector(system[0].x(1), -system[0].x(0), 0));
		system[2] = Vector.norm(Vector.cross(system[0], system[1]));
	}

	@Override
	public Vector project(Vector p3d) {
		Vector M = PoV.getS();
		Vector r = Vector.sub(M, p3d);
		
		Vector v = new Vector(Vector.dot(r, system[1]), Vector.dot(r, system[2]), Vector.dot(r, system[0]), 1);
		Vector projected = Matrix.mult(proj, v);
//		System.out.println(projected.x() + "  " + projected.y() + "  ||  " + projected.x(3) );
		projected = Vector.mult(projected, 1/projected.x(3));
		return new Vector(projected.x(), projected.y());
	}

	@Override
	public Point transform(Vector p2D) {
		System.out.println(p2D.x() + "   " + p2D.y());
		Point p = new Point((int) (d.getWidth() * ((p2D.x()+1)/2)), (int) (d.getHeight() * ((p2D.y()+1)/2)));
		return p;
	}

}
