package assembly;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JTextField;

public class CompilerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ArrayList<Integer> lines;
	ArrayList<JTextField> fields;

	public CompilerException(int line, JTextField field) {
		this();
		lines.add(line);
		fields.add(field);

	}

	public CompilerException() {
		this.lines = new ArrayList<Integer>();
		this.fields = new ArrayList<JTextField>();
	}
	
	public void addException(int line, JTextField field) {
		lines.add(line);
		fields.add(field);
	}

	public void printStackTrace() {

		System.out.println("ERROR! At line" + (lines.size() > 1 ? "s" : "") + ": "
				+ lines.toString());

		for (JTextField field : fields) {
			field.setBackground(Color.RED);
		}
	}
}
