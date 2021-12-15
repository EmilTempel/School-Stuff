package projection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import geometry.Mesh;
import geometry.Vector;
import projection.projectables.Projectable;
import projection.projectables.Sphere;

public class PointOfView {

	Vector s, r;
	Projectable p;
	double alpha, beta;

	public PointOfView(Vector s) {
		this.s = s;
		r = new Vector(1, 0, 0);
		this.p = new Sphere(this, 5);
	}

	public void setS(Vector s) {
		this.s = s;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
		updateR();
	}

	public double getAlpha() {
		return alpha;
	}

	public void setBeta(double beta) {
		this.beta = beta;
		updateR();
	}

	public double getBeta() {
		return beta;
	}

	public void updateR() {
		r = new Vector(Math.cos(beta) * Math.cos(alpha), Math.cos(beta) * Math.sin(alpha), Math.sin(beta));
		p.update(r);
//		System.out.println(r);
	}

	public Vector getS() {
		return s;
	}

	public Vector getR() {
		return r;
	}

	public void paint(Mesh m, Graphics g) {
		for (int j = 0; j < m.getTriangles().size(); j++) {
			Vector[] v = m.getTriangles().get(j);
			Color c = m.getColors().get(j);
			Polygon calc = calc(v);

			if (isCut(calc)) {
				for (int i = -1; i <= 1; i += 2) {
					Polygon neu = new Polygon();

					for (int n = 0; n < calc.npoints; n++) {
						int x = calc.xpoints[n] + i * 360 - 180;
						if ((i == -1 ? x < -360 : x > 360)) {
							x = calc.xpoints[n] - 180;
						}
						neu.addPoint(x + 180, calc.ypoints[n]);
					}

					paint(neu, c, g);
				}
			} else {
				paint(calc, c, g);
			}
		}
	}

	public void paint(Polygon p, Color c, Graphics g) {
		g.setColor(c);
		g.drawPolygon(p);
	}

	public Polygon calc(Vector[] p) {
		Polygon polygon = new Polygon();
		for (int i = 0; i < p.length; i++) {
			Vector point = this.p.project(p[i]);
			polygon.addPoint((int) Math.round(point.x(1) + 180), (int) Math.round(point.x(0) + 90));
		}
		return polygon;
	}

	public boolean isCut(Polygon p) {
		for (int i = 1; i < p.npoints; i++) {
			if (Math.abs(p.xpoints[0] - p.xpoints[i]) > 180) {
				return true;
			}
		}
		return false;
	}

}
