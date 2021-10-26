package gui;

import javax.swing.JOptionPane;

public class TextFenster{


	public static String getText(String message, String def) {
		String s = JOptionPane.showInputDialog(message, def);
		return s != null ? s : "";
	}

}
