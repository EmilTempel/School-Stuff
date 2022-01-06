package projection;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import geometry.Mesh;
import geometry.Vector;
import multithreading.Threading;
import projection.projectables.Projectable;
import projection.projectables.Sphere;

public class PointOfView extends KeyAdapter implements MouseMotionListener{

	Vector s, r;
	Projectable p;
	double alpha, beta;

	double FOV;
	Dimension d;

	Vector light = Vector.norm(new Vector(0, 5, -3));
	
	Robot bot;

	public PointOfView(Vector s, double FOV, Dimension d) {
		this.s = s;
		r = new Vector(1, 0, 0);
		this.p = new Sphere(this, 1,FOV,d);
		this.FOV = FOV;
		this.d = d;
		
		
		try {
			this.bot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PointOfView(Vector s, double FOV, int width) {
		this(s, FOV, new Dimension(width, width *9/16));

	}

	public void setS(Vector s) {
		this.s = s;
	}

	public void SetR(Vector r) {
		this.r = r;
		p.update(r);
		updateAngles();
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
	
	public Dimension getDim() {
		return d;
	}
	
	public double getFOV() {
		return FOV;
	}

	public void updateAngles() {
		beta = Math.acos(r.x(2));
		alpha = Math.acos(r.x(0) / Math.cos(beta));
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
		ArrayList<Vector> verts = m.getVertices();
		boolean[] inSight = new boolean[verts.size()];
		double[] dists = new double[verts.size()];
		Vector[] projected = new Vector[verts.size()];

		ArrayList<Integer> sorted = new ArrayList<Integer>();
		for (int i = 0; i < verts.size(); i++) {
			Vector v = Vector.sub(verts.get(i), s);
			dists[i] = Vector.abs(v);

			double alpha = Math.acos(Vector.dot(Vector.mult(r, -1), Vector.mult(v, 1 / dists[i])));
			if (inSight[i] = alpha < Math.toRadians(FOV / 2)+10) {
				sorted.add(i);
			}
		}

		sorted.sort((v1, v2) -> (int) Math.round((dists[v2] - dists[v1]) * 100000));

		ArrayList<Integer> triangles = new ArrayList<Integer>();
		boolean[] used = new boolean[m.size()];

		for (Integer v : sorted) {
			for (int i = 0; i < m.size(); i++) {
				if (!used[i]) {
					for (int j = 0; j < 3; j++) {
						if (m.getTriangles().get(i * 3 + j).equals(v)) {
							triangles.add(i);
							used[i] = true;
							break;
						}
					}
				}
			}
		}
//		System.out.println(triangles.size() + "  " + m.size() + " | " + sorted.size() + "  " + verts.size());
		
		
		for (int j = 0; j < triangles.size(); j++) {
//			Color c = m.getColors().get(triangles.get(j));
			Color c = Color.LIGHT_GRAY;
			Vector[] tri3D = m.getTriangle(triangles.get(j));
			Vector surface_normal = Vector
					.norm(Vector.cross(Vector.sub(tri3D[0], tri3D[1]), Vector.sub(tri3D[0], tri3D[2])));
			
			
			double light_val = Vector.dot(surface_normal, light) / 2 + 0.5;
			c = new Color((int) (light_val * c.getRed()), (int) (light_val * c.getGreen()),
					(int) (light_val * c.getBlue()));
			
			
			final Vector[] tri2D = new Vector[3];
			for(int i = 0; i < 3; i++) {
				int idx = m.getTriangles().get(triangles.get(j)*3 + i);
				
				if(projected[idx] == null) {
					projected[idx] = p.project(tri3D[i]);
				}
				Vector point = projected[idx];
				
				tri2D[i] = point;
			}
			
			if (isCut(tri2D)) {
				for (int i = 0; i <= 1; i += 2) {
					Vector[] triNeu = new Vector[3];

					for (int n = 0; n < 3; n++) {
						double x = tri2D[n].x(0);
						if (x < 0) {
							x += i == 0 ? 0 : 360;
						} else {
							x -= i == 1 ? 0 : 360;
						}
						triNeu[n] = new Vector(x, tri2D[n].x(1));
					}

					paint(triNeu, c, g);
				}
			} else {
				paint(tri2D, c, g);
			}
		}
	}

	public void paint(Vector[] poly2D, Color c, Graphics g) {
		g.setColor(c);
		Polygon poly = new Polygon();
		for (int i = 0; i < poly2D.length; i++) {
			Point p2D = p.transform(poly2D[i]);
			poly.addPoint(p2D.x, p2D.y);
			
		}

		g.fillPolygon(poly);
//		g.setColor(Color.GREEN);
//		g.drawPolygon(poly);
	}

	public Vector[] calc(Vector[] poly3D) {
		Vector[] poly2D = new Vector[poly3D.length];
		for (int i = 0; i < poly2D.length; i++) {
			Vector point = p.project(poly3D[i]);
			poly2D[i] = new Vector(point.x(1), point.x(0));
		}
		return poly2D;
	}

	public static boolean isCut(Vector[] tri2D) {
		for (int i = 1; i < 3; i++) {
			if (Math.abs(tri2D[0].x(0) - tri2D[i].x(0)) > 180) {
				return true;
			}
		}
		return false;
	}

	public void keyPressed(KeyEvent e) {
		double speed = 0.4;
		Vector vel = new Vector(0, 0, 0);;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W:
			vel = Vector.mult(r, -speed);
			break;
		case KeyEvent.VK_S:
			vel = Vector.mult(r, speed);
			break;
		case KeyEvent.VK_A:
			vel = new Vector(speed * r.x(1), -speed * r.x(0), 0);
			break;
		case KeyEvent.VK_D:
			vel = new Vector(-speed * r.x(1), speed * r.x(0), 0);
			break;
		case KeyEvent.VK_SPACE:
			vel = new Vector(0,0,speed);
			break;
		case KeyEvent.VK_CONTROL:
			vel = new Vector(0,0,-speed);
			break;
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		case KeyEvent.VK_0:
			SetR(new Vector(0,1,0));
		break;
		}
		s = Vector.add(s,vel);
	}
	
	

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		double speed = Math.toRadians(0.05);
		setAlpha(getAlpha() + -speed * (e.getX()-d.getWidth()/2));
		double beta = Math.toDegrees(getBeta() + speed * (e.getY()-d.getHeight()/2));
		setBeta(Math.toRadians(beta > 90 ? 90 : beta < -90 ? -90 : beta));
		bot.mouseMove(e.getXOnScreen()+(int)(d.getWidth()/2-e.getX()), e.getYOnScreen()+(int)(d.getHeight()/2-e.getY()));
		
//		System.out.println(e.getXOnScreen() + "   " + e.getX() + "  " + (e.getXOnScreen()+(int)(D.getWidth()-e.getX())));
	}
}
