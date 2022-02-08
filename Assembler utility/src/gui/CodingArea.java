package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import assembly.Command;
import assembly.CompilerException;

public class CodingArea extends JPanel implements KeyListener {

	int FontSize;
	Font font;
	ArrayList<JLabel> numbers;
	ArrayList<JTextField> lines;

	public CodingArea(int size, int FontSize) {
		this.FontSize = FontSize;
		this.font = new Font("Test", 0, FontSize);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		numbers = new ArrayList<JLabel>();
		lines = new ArrayList<JTextField>();
		for (int i = 0; i < size; i++) {
			addLine(i);
		}

	}

	public void requestFocus(int index) {
		lines.get(index).requestFocusInWindow();
	}

	public JTextField getLine(int index) {
		return lines.get(index);
	}

	public void addLine(int index) {
		JPanel pnl = new JPanel();
		pnl.setVisible(true);
		pnl.setLayout(new BorderLayout());
		pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) (FontSize * 1.5)));

		JTextField newLine = new JTextField();
		newLine.setVisible(true);
		newLine.setFont(font);
		newLine.addKeyListener(this);
		newLine.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				clearBackground();
			}
		});

		lines.add(index, newLine);

		JLabel lbl = new JLabel((lines.size() - 1) + ":");
		lbl.setFont(font);
		lbl.setPreferredSize(new Dimension(getFontMetrics(font).stringWidth("99: "), FontSize));
		lbl.setHorizontalAlignment(SwingConstants.RIGHT);

		numbers.add(index, lbl);

		pnl.add(newLine, BorderLayout.CENTER);
		pnl.add(lbl, BorderLayout.WEST);
		add(pnl, index);

		update();

		requestFocus(index);

		SwingUtilities.updateComponentTreeUI(this);
	}

	public void removeLine(int index) {
		remove(index);
		lines.remove(index);
		numbers.remove(index);
		update();

		requestFocus(index - 1);

		SwingUtilities.updateComponentTreeUI(this);
	}

	public void update() {
		for (int i = 0; i < numbers.size(); i++) {
			numbers.get(i).setText(i + ":");

		}
	}

	public Command[] getInstructions() throws CompilerException {
		Command[] instructions = new Command[lines.size()];
		CompilerException exception = null;
		for (int i = 0; i < instructions.length; i++) {
			String[] split = lines.get(i).getText().split(" ");

			Command.Type t = Command.Type.fromString(split[0]);

			if (t == null || (t != Command.Type.END && split.length == 1)) {
				if (exception == null) {
					exception = new CompilerException(i, lines.get(i));
				} else {
					exception.addException(i, lines.get(i));
				}
				continue;
			}

			instructions[i] = new Command(t, t == Command.Type.END ? 0 : Integer.parseInt(split[1]));
		}

		if (exception != null)
			throw exception;

		return instructions;
	}

	public void setEditable(boolean editable) {
		for (JTextField f : lines) {
			f.setEditable(editable);
		}
	}
	
	public void clearBackground() {
		for(JTextField f : lines) {
			f.setBackground(Color.WHITE);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int index = lines.indexOf(e.getComponent());

		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			addLine(index + 1);
			break;
		case KeyEvent.VK_BACK_SPACE:
			if (lines.get(index).getText().equals("") && lines.size() > 1)
				removeLine(index);
			break;
		case KeyEvent.VK_UP:
			requestFocus(index != 0 ? index - 1 : lines.size() - 1);
			break;
		case KeyEvent.VK_DOWN:
			requestFocus(index != lines.size() - 1 ? index + 1 : 0);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
