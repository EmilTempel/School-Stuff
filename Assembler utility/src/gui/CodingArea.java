package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import assembly.Command;
import assembly.CompilerException;

public class CodingArea extends JPanel implements KeyListener {

	int FontSize;
	Font font;
	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	JPanel text;
	ArrayList<JLabel> numbers;
	ArrayList<JTextField> lines;

	public CodingArea(int size, int FontSize) {
		this.FontSize = FontSize;
		this.font = new Font("Test", 0, FontSize);

		setLayout(new BorderLayout());

		JMenuBar menu = new JMenuBar();

		JMenu edit = new JMenu("Edit");

		JMenuItem save = new JMenuItem("save");
		save.addActionListener(a -> new SavingFrame(this));
		edit.add(save);

		JMenuItem load = new JMenuItem("load");
		load.addActionListener(a -> new LoadingFrame(this));
		edit.add(load);

		JMenuItem copy = new JMenuItem("copy");
		copy.addActionListener(a -> {
			StringSelection sel = new StringSelection(toString());
			clipboard.setContents(sel, sel);
		});
		edit.add(copy);

		menu.add(edit);

		add(menu, BorderLayout.NORTH);

		text = new JPanel();
		text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

		add(text, BorderLayout.CENTER);

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

		JLabel lbl = new JLabel(index + ":");
		lbl.setFont(font);
		lbl.setPreferredSize(new Dimension(getFontMetrics(font).stringWidth("99: "), FontSize));
		lbl.setHorizontalAlignment(SwingConstants.RIGHT);

		numbers.add(index, lbl);

		pnl.add(newLine, BorderLayout.CENTER);
		pnl.add(lbl, BorderLayout.WEST);
		text.add(pnl, index);

		update();

		requestFocus(index);

		SwingUtilities.updateComponentTreeUI(this);
	}

	public void addLine(int index, String text) {
		addLine(index);
		lines.get(index).setText(text);
	}

	public void removeLine(int index) {
		text.remove(index);
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
		for (JTextField f : lines) {
			f.setBackground(Color.WHITE);
		}
	}

	public String toString() {
		String str = "";
		for (JTextField field : lines) {
			str += field.getText() + "\n";
		}
		return str;
	}

	public void fromString(String str) {
		while (lines.size() > 1) {
			removeLine(1);
		}

		String[] split = str.split("\n");

		for (int i = 0; i < split.length; i++) {
			addLine(i, split[i]);
		}

		update();
	}

	public void addFromString(int index, String str) {
		String[] split = str.split("\n");

		lines.get(index).setText(lines.get(index).getText() + split[0]);
		for (int i = 1; i < split.length; i++) {
			addLine(index + i, split[i]);
		}

		update();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int index = lines.indexOf(e.getComponent());

		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			System.out.println(index + 1);
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
		case KeyEvent.VK_S:
			if (e.isControlDown()) {
				new SavingFrame(this);
			}
			break;
		case KeyEvent.VK_V:
			if (e.isControlDown()) {
				try {
					e.consume();
					addFromString(index, (String) clipboard.getData(DataFlavor.stringFlavor));
				} catch (UnsupportedFlavorException | IOException e1) {
					System.out.println("ERROR! Can't insert that.");
				}
			}
			break;
		case KeyEvent.VK_C:
			if (e.isControlDown()) {
				StringSelection sel = new StringSelection(toString());
				clipboard.setContents(sel, sel);
			}
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
