package finiteautomata;

import java.awt.Graphics2D;
import java.awt.Point;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class FiniteAutomaton {

	State[] states;
	String[][][] matrix;
	int start_state;

	public FiniteAutomaton() {
		states = new State[0];
		matrix = new String[0][0][0];
	}

	public void setStartState(int start_state) {
		states[this.start_state].setStart(false);
		states[start_state].setStart(true);
		this.start_state = start_state >= 0 ? start_state : 0 < states.length ? start_state : states.length;
	}

	public void addState(String name, boolean end) {
		addState(name, end, 0, 0);
	}

	public void addState(String name, boolean end, int x, int y) {
		State state = new State(name, end, x, y);
		states = addToArray(states, state, State.class);

		String[][][] temp = matrix;
		matrix = new String[states.length][states.length][];
		for (int i = 0; i < states.length; i++) {
			for (int j = 0; j < states.length; j++) {
				if (i < states.length - 1 && j < states.length - 1) {
					matrix[i][j] = temp[i][j];
				} else {
					matrix[i][j] = new String[0];
				}
			}
		}
		if(states.length == 1)
			setStartState(0);
	}
	
	public void removeState(int s) {
		int c = 0; 
		State[] temp_states = new State[states.length-1];
		String[][][] temp_matrix = new String[states.length-1][states.length-1][];
		
		for(int i = 0; i < states.length; i++) {
			if(i != s) {
				temp_states[c] = states[i];
				int d = 0;
				for(int j = 0; j < states.length; j++) {
					if(j != s) {
						temp_matrix[c][d] = matrix[i][j];
						d++;
					}
				}
				c++;
			}
		}
		
		states = temp_states;
		matrix = temp_matrix;
	}
	
	public void removeState(State s) {
		int idx = getIndex(s);
		if(idx != -1) {
			removeState(idx);
		}
	}
	
	public int getIndex(State s) {
		for(int i = 0; i < states.length; i++) {
			if(s.equals(states[i])) {
				return i;
			}
		}
		return -1;
	}

	public void addEdge(int s1, int s2, String c) {
		if (!contains(matrix[s1][s2], c)) {
			matrix[s1][s2] = addToArray(matrix[s1][s2], c, String.class);
		}
	}
	
	public void addEdge(State s1, State s2, String c) {
		int idx1 = getIndex(s1), idx2 = getIndex(s2);
		if(idx1 != -1 && idx2 != -1) {
			addEdge(idx1, idx2, c);
		}
	}

	public void removeEdge(int s1, int s2, String c) {
		matrix[s1][s2] = removeFromArray(matrix[s1][s2], c, String.class);
	}
	
	public void removeEdge(State s1, State s2, String c) {
		int idx1 = getIndex(s1), idx2 = getIndex(s2);
		if(idx1 != -1 && idx2 != -1) {
			matrix[idx1][idx2] = removeFromArray(matrix[idx1][idx2], c, String.class);
		}
		
	}

	public boolean matches(int s, String word) {
		if (word.equals("")) {
			return states[s].isEnd();
		} else {
			String c = word.substring(0, 1);
			for (int i = 0; i < matrix[s].length; i++) {
				if (containsMatch(matrix[s][i], c)) {
					return matches(i, word.substring(1));
				}
			}
			return false;
		}
	}

	public static boolean containsMatch(String[] arr, String s) {
		for (int i = 0; i < arr.length; i++) {
			if ((s != null && s.matches(arr[i])) || (arr[i] != null && arr[i].matches(s))) {
				return true;
			}
		}
		return false;
	}

	public String toEBNF() {
		String str = "";
		for (int n = 0; n < states.length; n++) {
			str += states[n].getName() + " = ";
			for (int i = 0; i < states.length; i++) {
				if (matrix[n][i].length > 0) {
					str += matrix[n][i].length > 1 ? "(" : "";
					for (int j = 0; j < matrix[n][i].length; j++) {
						str += "'" + matrix[n][i][j] + "'";
						if (j < matrix[n][i].length - 1) {
							str += "|";
						}
					}
					str += matrix[n][i].length > 1 ? ")" : "";
					if (states[i].isEnd()) {
						str += "[" + states[i].getName() + "]";
					} else {
						str += states[i].getName();
					}
				}
			}
			str += ";\n";
		}
		return str;
	}

	public int[] findSCCs(int start) {
		int[] component = new int[states.length];
		for (int i = 0; i < component.length; i++) {
			component[i] = -1;
		}
		boolean[] besucht = new boolean[states.length];
		int[] pre = new int[states.length];

		findSCCs(start, start, component, besucht, pre);

		System.out.println(Arrays.toString(component));
		System.out.println(Arrays.toString(besucht));
		System.out.println(Arrays.toString(pre));

		return component;
	}

	public void findSCCs(int before, int now, int[] component, boolean[] besucht, int[] pre) {
		besucht[now] = true;
		component[now] = component[before] + 1;
		pre[now] = before;

		for (int i = 0; i < states.length; i++) {
			if (matrix[now][i].length != 0) {
				if (!besucht[i]) {
					findSCCs(now, i, component, besucht, pre);
				} else {
					backtrack(now, pre, component, component[i]);
				}
			}
		}
	}

	public void backtrack(int s, int[] pre, int[] component, int c) {
		component[s] = c;
		if (component[pre[s]] != c) {
			backtrack(pre[s], pre, component, c);
		}
	}
	
	public State StateAt(Point p) {
		for(int i = 0; i < states.length; i++) {
			if(states[i].contains(p)) {
				return states[i];
			}
		}
		return null;
	}

	public void paint(Graphics2D g) {
		for (int i = 0; i < states.length; i++) {
			
			for (int j = 0; j < states.length; j++) {
				if (matrix[i][j].length != 0)
					states[i].connectTo(g, states[j], Arrays.deepToString(matrix[i][j]));
			}
		}
		
		for(int i = 0; i < states.length; i++) {
			states[i].draw(g);
		}
	}

	public static <E> boolean contains(E[] arr, E o) {
		for (int i = 0; i < arr.length; i++) {
			if ((arr[i] == null && o == null) || (arr[i] != null && arr[i].equals(o))) {
				return true;
			}
		}
		return false;
	}

	public static <E> E[] addToArray(E[] arr, E o, Class<E> clazz) {
		E[] temp = arr;
		arr = (E[]) Array.newInstance(clazz, arr.length + 1);
		for (int i = 0; i < arr.length - 1; i++) {
			arr[i] = temp[i];
		}
		arr[arr.length - 1] = o;
		return arr;
	}

	public static <E> E[] removeFromArray(E[] arr, E o, Class<E> clazz) {
		if (contains(arr, o)) {
			ArrayList<E> list = new ArrayList<E>();
			for (int i = 0; i < arr.length; i++) {
				if (!(arr[i] == null && o == null) && !(arr[i] != null && arr[i].equals(o))) {
					list.add(arr[i]);
				}
			}
			arr = (E[]) Array.newInstance(clazz, list.size());
			for (int i = 0; i < arr.length; i++) {
				arr[i] = list.get(i);
			}
		}
		return arr;
	}

	public static void main(String[] args) {
		FiniteAutomaton fa = new FiniteAutomaton();
		fa.addState("start", true);
		fa.addState("z1", false);
		fa.addState("z2", true);
		fa.addState("z2", false);
		fa.addState("z3", false);

		fa.addEdge(0, 1, ".");
		fa.addEdge(1, 3, ".");
		fa.addEdge(3, 4, ".");
		fa.addEdge(4, 0, ".");
		fa.addEdge(3, 2, ".");

		for (int i = 1; i < 100; i++) {
			String str = "";
			for (int j = 0; j < i; j++) {
				str += (char) ('a' + Math.random() * 26);
			}
			System.out.println(str + " -> " + fa.matches(0, str));
		}

		fa.findSCCs(0);
		System.out.println(fa.toEBNF());
	}
}
