package projection.projectables;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import geometry.Vector;
import math.Functions;
import multithreading.Threading;
import projection.PointOfView;

public class Sphere extends Projectable {

	double R, alpha;
	Dimension d, D;
	double FOV;

	Vector[] system;

	public Sphere(PointOfView PoV, double R, double FOV, Dimension d) {
		super(PoV);
		this.R = R;
		system = new Vector[3];
		update(PoV.getR());
		this.d = d;
		this.D = new Dimension((int) (d.getWidth() * 360 / FOV), (int) (d.getWidth() * 360 / FOV / 2));
	}

	public void update(Vector r) {
		system[0] = Vector.norm(r);
		system[1] = Vector.norm(new Vector(system[0].x(1), -system[0].x(0), 0));
		system[2] = Vector.norm(Vector.cross(system[0], system[1]));
	}

	public Vector project(Vector u) {
		for(int i = 0; i < 1000000; i++) {
			i++;
			i--;
		}
			
		
		Vector M = PoV.getS();
		Vector r = Vector.sub(M, u);
		Vector cross = Vector.norm(r);

		double lon = Math.atan2(Vector.dot(cross, system[1]), Vector.dot(cross, system[0]));
		double lat = -Math.asin(Vector.dot(cross, system[2]));

		return new Vector(Math.toDegrees(lon), Math.toDegrees(lat));
	}

	@Override
	public Point transform(Vector p2D) {
		Point p = new Point(
				(int) (Functions.map(p2D.x(0) + 180, 0, 360, 0, D.getWidth()) - (D.getWidth() / 2 - d.getWidth() / 2)),
				(int) (Functions.map(p2D.x(1) + 90, 0, 180, 0, D.getHeight())
						- (D.getHeight() / 2 - d.getHeight() / 2)));
		return p;
	}

	public static void main(String[] args) {
		PointOfView p = new PointOfView(new Vector(0, 0, 0), 360, 1600);
		Sphere s = new Sphere(p, 3, 360, new Dimension(1600, 900));
		int N = 3000;
		Vector[] vecs = new Vector[N];
		Vector[] projs = new Vector[N];
		for (int i = 0; i < N; i++) {
			vecs[i] = new Vector(Math.random() * 100 - 50, Math.random() * 100 - 50, Math.random() * 100 - 50);
		}

		int test_zahl = 100;
		double average = 0;

		for (int i = 0; i < test_zahl; i++) {
			long millis = System.currentTimeMillis();
			for (int j = 0; j < N; j++) {
				projs[j] = s.project(vecs[j]);
				s.transform(projs[j]);
			}
			s.update(new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5));
			long time = System.currentTimeMillis() - millis;
			System.out.print(time+"|");
			average += time;
		}
		average /= (double) test_zahl;
		
		System.out.println("\nFür " + N + " Punkte braucht ein Core durchschnittlich: " + average + " millisekunden");
		average = 0;
		for (int i = 0; i < N; i++) {
			vecs[i] = new Vector(Math.random() * 100 - 50, Math.random() * 100 - 50, Math.random() * 100 - 50);
		}

		Threading<Vector, Vector> t = new Threading<Vector, Vector>();
		for (int i = 0; i < test_zahl; i++) {
			long millis = System.currentTimeMillis();

			t.parallelize(vecs, projs, (in, out, n) -> {
				out[n] = s.project(in[n]);
				s.transform(out[n]);
			});
			s.update(new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5));
			long time = System.currentTimeMillis() - millis;
			average += time;
			System.out.print(time+"|");
		}
		average /= (double) test_zahl;
		System.out.println(
				"\nFür " + N + " Punkte braucht mein Multithreading durchschnittlich: " + average + " millisekunden");

		average = 0;
		for (int i = 0; i < N; i++) {
			vecs[i] = new Vector(Math.random() * 100 - 50, Math.random() * 100 - 50, Math.random() * 100 - 50);
		}
		ExecutorService ES = Executors.newFixedThreadPool(4);
		for (int i = 0; i < test_zahl; i++) {
			long millis = System.currentTimeMillis();
			
			List<Callable<Vector>> tasks = new ArrayList<Callable<Vector>>();
			for (int j = 0; j < N; j++) {
				int J = j;
				Callable<Vector> c = new Callable<Vector>() {
					@Override
					public Vector call() throws Exception {
						projs[J] = s.project(vecs[J]);
						s.transform(projs[J]);
						return projs[J];
					}
				};
				tasks.add(c);
			}
			try {
				ES.invokeAll(tasks);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s.update(new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5));
			long time = System.currentTimeMillis() - millis;
			average += time;
			System.out.print(time+"|");
		}
		average /= (double) test_zahl;
		System.out.println(
				"\nFür " + N + " Punkte braucht javas Multithreading durchschnittlich: " + average + " millisekunden");
	}

}
