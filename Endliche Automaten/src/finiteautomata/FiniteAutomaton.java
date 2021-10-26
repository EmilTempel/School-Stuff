package finiteautomata;

import java.awt.Graphics2D;
import java.awt.Point;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class FiniteAutomaton {

	State[] states;
	Character[][][] matrix;
	int start_state;

	public FiniteAutomaton() {
		states = new State[0];
		matrix = new Character[0][0][0];
	}

	public void setStartState(int start_state) {
		states[this.start_state].setStart(false);
		states[start_state].setStart(true);
		this.start_state = start_state >= 0 ? start_state : 0 < states.length ? start_state : states.length;
	}
	
	public void setStartState(State start_state) {
		int i = getIndex(start_state);
		if(i != -1) {
			setStartState(i);
		}
	}

	public void addState(String name, boolean end) {
		addState(name, end, 0, 0);
	}

	public void addState(String name, boolean end, int x, int y) {
		State state = new State(name, end, x, y);
		states = addToArray(states, state, State.class);

		Character[][][] temp = matrix;
		matrix = new Character[states.length][states.length][];
		for (int i = 0; i < states.length; i++) {
			for (int j = 0; j < states.length; j++) {
				if (i < states.length - 1 && j < states.length - 1) {
					matrix[i][j] = temp[i][j];
				} else {
					matrix[i][j] = new Character[0];
				}
			}
		}
		if(states.length == 1)
			setStartState(0);
	}
	
	public void removeState(int s) {
		int c = 0; 
		State[] temp_states = new State[states.length-1];
		Character[][][] temp_matrix = new Character[states.length-1][states.length-1][];
		
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

	public void addEdge(int s1, int s2, char c) {
		if (!contains(matrix[s1][s2], c)) {
			matrix[s1][s2] = addToArray(matrix[s1][s2], c, Character.class);
		}
	}
	
	public void addEdge(State s1, State s2, char c) {
		int idx1 = getIndex(s1), idx2 = getIndex(s2);
		if(idx1 != -1 && idx2 != -1) {
			addEdge(idx1, idx2, c);
		}
	}

	public void removeEdge(int s1, int s2, char c) {
		matrix[s1][s2] = removeFromArray(matrix[s1][s2], c, Character.class);
	}
	
	public void removeEdge(State s1, State s2, char c) {
		int idx1 = getIndex(s1), idx2 = getIndex(s2);
		if(idx1 != -1 && idx2 != -1) {
			matrix[idx1][idx2] = removeFromArray(matrix[idx1][idx2], c, Character.class);
		}
		
	}
	
	public String getConnections(int i, int j) {
		return Arrays.deepToString(matrix[i][j]).replace("[", "").replace("]", "");
	}
	
	public String getConnections(State i, State j) {
		int idx1 = getIndex(i), idx2 = getIndex(j);
		if(idx1 != -1 && idx2 != -1) {
			return Arrays.deepToString(matrix[idx1][idx2]).replace("[", "").replace("]", "");
		}
		return "";
	}
	
	public void setConnections(int i, int j, Character[] arr) {
		matrix[i][j] = arr;
	}
	
	public void setConnections(State i, State j, Character[] arr) {
		int idx1 = getIndex(i), idx2 = getIndex(j);
		if(idx1 != -1 && idx2 != -1) {
			matrix[idx1][idx2] = arr;
		}
	}

	public boolean matches(int s, String word) {
		if (word.equals("")) {
			return states[s].isEnd();
		} else {
			char c = word.charAt(0);
			for (int i = 0; i < matrix[s].length; i++) {
				if (containsMatch(matrix[s][i], c)) {
					return matches(i, word.substring(1));
				}
			}
			return false;
		}
	}
	
	public boolean matches(String word) {
		return matches(start_state, word);
	}

	public static boolean containsMatch(Character[] arr, char s) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == s || arr[i] == '$') {
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
					states[i].connectTo(g, states[j]);
			}
		}
		for(int i = 0; i < states.length; i++) {
			for (int j = 0; j < states.length; j++) {
				if (matrix[i][j].length != 0)
					states[i].addConnectionTo(g, states[j], getConnections(i,j));
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

		fa.addEdge(0, 1, 'x');
		fa.addEdge(1, 3, 'x');
		fa.addEdge(3, 4, 'x');
		fa.addEdge(4, 0, 'x');
		fa.addEdge(3, 2, 'x');

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
