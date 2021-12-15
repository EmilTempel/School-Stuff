package geometry;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class Mesh {

	ArrayList<Vector[]> triangles;
	ArrayList<Color> colors;

	public Mesh() {
		triangles = new ArrayList<Vector[]>();
		colors = new ArrayList<Color>();
	}

	public Mesh(ArrayList<Vector[]> triangles, ArrayList<Color> colors) {
		this();
		addTriangles(triangles, colors);
	}
	
	public ArrayList<Vector[]> getTriangles(){
		return triangles;
	} 
	
	public ArrayList<Color> getColors(){
		return colors;
	}

	public void addTriangle(Vector[] t, Color c) {
		if (t.length == 3) {
			triangles.add(t);
			colors.add(c);
		}
	}

	public void addTriangles(ArrayList<Vector[]> triangles, ArrayList<Color> colors) {
		for (int i = 0; i < triangles.size(); i++) {
			addTriangle(triangles.get(i), colors.get(i));
		}
	}
	
	public void addMesh(Mesh m) {
		for (int i = 0; i < m.triangles.size(); i++) {
			addTriangle(m.getTriangles().get(i), m.getColors().get(i));
		}
	}
	
	public String toString() {
		String str = "";
		for(Vector[] t : triangles) {
			str += Arrays.deepToString(t) + "\n";
		}
		return str;
	}
	
	public static Mesh merge(Mesh...meshes) {
		Mesh mesh = new Mesh();
		for(Mesh m : meshes) {
			mesh.addTriangles(m.getTriangles(), m.getColors());
		}
		return mesh;
	}
}
