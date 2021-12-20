package graph;

import java.util.ArrayList;

public class Graph<Node, Edge> {

	public ArrayList<Node> nodes;
	ArrayList<ArrayList<Edge>> matrix;

	Edge default_connection;
	boolean directed;

	public Graph(boolean directed) {
		nodes = new ArrayList<Node>();
		matrix = new ArrayList<ArrayList<Edge>>();
	}

	public Graph(boolean directed, Edge default_connection) {
		this(directed);
		this.default_connection = default_connection;
	}

	public Graph(Node[] nodes, Edge[][] matrix) {

	}

	public void addNode(Node n) {
		nodes.add(n);
		matrix.add(new ArrayList<Edge>());
		for (int i = 0; i < size(); i++) {
			matrix.get(size() - 1).add(default_connection);
			matrix.get(i).add(default_connection);
		}
	}

	public void removeNode(int i) {
		nodes.remove(i);
		matrix.remove(i);
		for (int j = 0; j < size(); j++) {
			matrix.get(j).remove(i);
		}
	}

	public void setEdge(Node n1, Node n2, Edge e) {
		int i = getIndexOf(n1), j = getIndexOf(n2);
		if (i != -1 && j != -1) {
			setEdge_index(i, j, e);
		}
	}

	public void setEdge_index(int i, int j, Edge e) {
		matrix.get(i).set(j, e);
		if (!directed)
			matrix.get(j).set(i, e);
	}

	public int getIndexOf(Node n) {
		return nodes.indexOf(n);
	}

	public int size() {
		return nodes.size();
	}

	public Node getNode(int i) {
		return nodes.get(i);
	}

	public Edge getEdge(int i, int j) {
		return matrix.get(i).get(j);
	}

	public void DepthFirstSearch(int i, boolean[] besucht, Checker<Node> c, Executer<Node> e) {
		if (c.check(getNode(i), i)) {
			e.execute(getNode(i), i);
			for (int j = 0; j < size(); j++) {
				if (getEdge(i, j) != null && !besucht[j] && c.check(getNode(j), j)) {
					System.out.println("yes");
					e.execute(getNode(j), j);
					besucht[j] = true;
					DepthFirstSearch(j, besucht, c, e);
				}
			}
		}
	}

	public String toString() {
		String str = "";
		for (int i = 0; i < size(); i++) {
			for (int j = 0; j < size(); j++) {
				str += (matrix.get(i).get(j) != null ? 1 : 0) + " ";
			}
			str += "\n";
		}

		return str;
	}

	public interface Checker<Node> {
		public abstract boolean check(Node n, int i);
	}

	public interface Executer<Node> {
		public abstract void execute(Node n, int i);
	}

//	public static void main(String[]args) {
//		Graph<String, Integer> g = new Graph<String, Integer>(420);
//		g.addNode("1");
//		g.addNode("2");
//		g.addNode("3");
//		g.addNode("4");
//		
//		g.setEdge("1", "3", 69);
//		System.out.println(g.toString());
//	}
}
