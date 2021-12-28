package geometry;

import java.awt.Color;
import java.util.ArrayList;

import graph.Graph;

public class Polytope {

	Graph<Vector, Vector> graph;

	public Polytope(Graph<Vector, Vector> graph) {
		this.graph = graph;
	}

	public Polytope(Vector[] points, boolean[][] connections) {
		Vector[][] edges = new Vector[points.length][points.length];
		if (connections.length == points.length && connections[0].length == points.length) {
			for (int i = 0; i < points.length; i++) {
				for (int j = 0; j < points.length; j++) {
					if (connections[i][j]) {
						edges[i][j] = Vector.sub(points[j], points[i]);
					}
				}
			}
		}
		this.graph = new Graph<Vector, Vector>(points, edges);
	}

	public Polytope(Vector s, Vector... v) {
		graph = new Graph<Vector, Vector>(false);
		graph.addNode(s);
		for (int i = 0; i < v.length; i++) {
			int len = graph.size();

			for (int j = 0; j < len; j++) {
				graph.addNode(Vector.add(graph.getNode(j), v[i]));
				graph.setEdge_index(j, j + len, v[i]);
			}

			for (int j = 0; j < len; j++) {
				for (int k = 0; k < len; k++) {
					graph.setEdge_index(j + len, k + len, graph.getEdge(j, k));
				}
			}
		}

	}

	public Polytope(Vector s, double l, double b, double h) {
		this(s, new Vector(l, 0, 0), new Vector(0, b, 0), new Vector(0, 0, h));
	}

	public Mesh calcFaces() {
		Mesh mesh = new Mesh();
		ArrayList<ArrayList<Vector>> faces = new ArrayList<ArrayList<Vector>>();

		int len = graph.size();

		for (int i = 0; i < len; i++) {
			Vector s = graph.getNode(i);
			for (int j = 0; j < len; j++) {
				Vector t = graph.getNode(j);
				Vector v = graph.getEdge(i, j);
				if (v != null) {
					for (int k = 0; k < len; k++) {
						Vector u = graph.getNode(k);
						Vector w = graph.getEdge(j, k);
						if (k != i && w != null && !contains(faces, s, t, u)) {
							Vector c = Vector.cross(v, w);
							ArrayList<Vector> poly = new ArrayList<Vector>();
							poly.add(s);
							poly.add(t);
							poly.add(u);

							boolean[] besucht = new boolean[len];
							besucht[i] = true;
							besucht[j] = true;
							besucht[k] = true;

							graph.DepthFirstSearch(k, besucht, (N, n) -> {
								return Vector.dot(Vector.sub(N, s), c) == 0;
							}, (N, n) -> {
								poly.add(N);
							});
							faces.add(poly);

							mesh.addMesh(triangulate(poly));
						}
					}
				}
			}
		}

		System.out.println(mesh);
		return mesh;
	}

	public boolean contains(ArrayList<ArrayList<Vector>> f, Vector... v) {
		for (ArrayList<Vector> poly : f) {
			int c = v.length;
			for (Vector u : v) {
				if (poly.contains(u))
					c--;
			}
			if (c == 0)
				return true;
		}

		return false;
	}

	public static Mesh triangulate(ArrayList<Vector> poly) {
		Mesh mesh = new Mesh();
		for (int i = 2; i < poly.size(); i++) {
			mesh.addTriangle(new Vector[] { poly.get(0), poly.get(i - 1), poly.get(i) }, new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
		}

		return mesh;
	}
	
	public static ArrayList<Vector> convexify(ArrayList<Vector> p){
		ArrayList<Vector> poly = (ArrayList<Vector>) p.clone();
		ArrayList<Vector> convex = new ArrayList<Vector>();
		
		convex.add(poly.get(0));
		poly.remove(0);
		Vector ref = Vector.norm(Vector.sub(poly.get((int)(Math.random()*poly.size())), convex.get(0)));
		Vector next = poly.get(0);
		function f = (v,u) -> Vector.dot(Vector.norm(Vector.sub(v,convex.get(0))), u);
		double dot = f.calc(next,ref);
		for(int i = 1; i < poly.size(); i++) {
			if(f.calc(poly.get(i),ref) < dot) {
				next = poly.get(i);
				dot = f.calc(next,ref);
			}
		}
		
		convex.add(next);
		poly.remove(next);
		ref = Vector.norm(Vector.sub(poly.get((int)(Math.random()*poly.size())), convex.get(0)));
		
		while(poly.size() > 0) {
			next = poly.get(0);
			dot = f.calc(next,ref);
			for(int i = 1; i < poly.size(); i++) {
				if(f.calc(poly.get(i),ref)< dot) {
					next = poly.get(i);
					dot = f.calc(next,ref);
				}
			}
			convex.add(next);
			poly.remove(next);
		}
		
		return convex;
	}

	public static void main(String[] args) {
		System.out.println(1/Math.acos(1));
	}
	
	public interface function{
		public abstract double calc(Vector u, Vector v);
	}
}
