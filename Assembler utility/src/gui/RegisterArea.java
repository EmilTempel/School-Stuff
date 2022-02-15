package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import assembly.Command;
import assembly.CompilerException;
import assembly.EternalException;
import assembly.RegisterMachine;

public class RegisterArea extends JPanel {

	CodingArea ca;

	RegisterMachine rm;
	ArrayList<RegisterMachine> steps;
	int index;

	Register Step;
	Register Counter;
	Register Accumulator;
	ArrayList<Register> registers;

	public RegisterArea(CodingArea ca) {
		this.ca = ca;

		rm = new RegisterMachine();

		setLayout(new BorderLayout());

		JPanel Header = new JPanel();

		JButton compile = new JButton("Compile");
		JButton last = new JButton("Back");
		JButton play = new JButton("Play");
		JTextField millis = new JTextField();
		JButton stop = new JButton("Stop");
		JButton next = new JButton("Forward");
		JButton edit = new JButton("Edit");

		Player p = new Player(this);

		compile.addActionListener(a -> {
			Command[] instructions;
			try {
				instructions = ca.getInstructions();
				steps = RegisterMachine.run(new RegisterMachine(rm), instructions);
			} catch (CompilerException | EternalException e) {
				e.printStackTrace();
				return;
			}

			setIndex(0);
			last.setEnabled(true);
			play.setEnabled(true);
			millis.setEnabled(true);
			stop.setEnabled(true);
			next.setEnabled(true);

			for (Register r : registers) {
				r.setEditable(false);
			}

			ca.setEditable(false);
		});

		last.addActionListener(a -> {
			setIndex(index != 0 ? index - 1 : 0);
			SwingUtilities.updateComponentTreeUI(this);
		});

		play.addActionListener(a -> {
			p.wakeUp();
		});

		millis.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE)) {
					e.consume();
				}
			}
		});
		millis.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				p.setMillis(Long.parseLong(millis.getText()));
			}
		});
		millis.setPreferredSize(new Dimension(50,30));

		stop.addActionListener(a -> {
			p.stopTheCount();
		});

		next.addActionListener(a -> {
			setIndex(index != steps.size() - 1 ? index + 1 : index);
			SwingUtilities.updateComponentTreeUI(this);
		});

		last.setEnabled(false);
		play.setEnabled(false);
		millis.setEnabled(false);
		stop.setEnabled(false);
		next.setEnabled(false);

		edit.addActionListener(a -> {

			last.setEnabled(false);
			play.setEnabled(false);
			millis.setEnabled(false);
			stop.setEnabled(false);
			next.setEnabled(false);

			for (Register r : registers) {
				r.setEditable(true);
				r.setVal(rm.R(registers.indexOf(r)));
			}

			ca.setEditable(true);
			ca.clearBackground();
		});

		Header.add(compile);
		Header.add(edit);

		add(Header, BorderLayout.NORTH);

		JPanel Body = new JPanel(new GridLayout(5, 5));

		Step = new Register("Step", rm);
		Body.add(Step);

		Counter = new Register("BZ", rm);
		Body.add(Counter);

		Accumulator = new Register("A", rm);
		Body.add(Accumulator);

		registers = new ArrayList<Register>();
		for (int i = 0; i < 15; i++) {
			Register reg = new Register(i, rm);
			Body.add(reg);
			registers.add(reg);
		}

		add(Body, BorderLayout.CENTER);

		JPanel Bottom = new JPanel();

		Bottom.add(last);
		Bottom.add(play);
		Bottom.add(millis);
		Bottom.add(stop);
		Bottom.add(next);

		add(Bottom, BorderLayout.SOUTH);
	}

	public void setIndex(int index) {
		this.index = index;
		RegisterMachine now = steps.get(index), last = steps.get(index != 0 ? index - 1 : 0);
		int[] R = now.getR();

		Step.setVal(index + 1);

		Counter.setVal(now.getBZ());

		ca.clearBackground();
		ca.getLine(last.getBZ()).setBackground(Color.YELLOW);

		Accumulator.setVal(now.getA());

		for (int i = 0; i < registers.size(); i++) {
			registers.get(i).setVal(i < R.length ? R[i] : 0);
		}

		SwingUtilities.updateComponentTreeUI(this);
	}
}
