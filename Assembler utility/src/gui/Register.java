package gui;

import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import assembly.RegisterMachine;

public class Register extends JPanel {
	JLabel name;
	JTextField field;

	public Register(int i, RegisterMachine rm) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(50,80));

		name = new JLabel("R" + i);
		name.setHorizontalTextPosition(SwingConstants.RIGHT);

		field = new JTextField();
		field.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!(Character.isDigit(c)
						|| (field.getCaretPosition() == 0 && c == '-') || c == KeyEvent.VK_BACK_SPACE)) {
					e.consume();
				}
			}
		});
		field.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				String number = field.getText();
				if(number.equals("") || number.equals("-")) {
					number = "0";
					field.setText(number);
				}
				rm.setR(i, Integer.parseInt(number));
			}
		});
		field.setHorizontalAlignment(SwingConstants.CENTER);
		field.setText("" + rm.R(i));

		add(name);
		add(field);
	}
	
	public Register(String name, RegisterMachine rm) {
		this(0,rm);
		this.name.setText(name);
		setEditable(false);
	}
	
	public void setEditable(boolean editable) {
		field.setEditable(editable);
	}
	
	public void setVal(int i) {
		field.setText(String.valueOf(i));
	}
}
