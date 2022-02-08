package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import assembly.Command;
import assembly.CompilerException;
import assembly.RegisterMachine;

public class RegisterArea extends JPanel {

	CodingArea ca;
	
	RegisterMachine rm;
	ArrayList<RegisterMachine> steps;
	int index;

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
		JButton next = new JButton("Forward");
		JButton edit = new JButton("Edit");

		compile.addActionListener(a -> {
			Command[] instructions;
			try {
				instructions = ca.getInstructions();
			} catch (CompilerException e) {
				e.printStackTrace();
				return;
			}

			steps = RegisterMachine.run(new RegisterMachine(rm), instructions);
			setIndex(0);
			last.setEnabled(true);
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

		next.addActionListener(a -> {
			setIndex(index != steps.size() - 1 ? index + 1 : 0);
			SwingUtilities.updateComponentTreeUI(this);
		});

		last.setEnabled(false);
		next.setEnabled(false);

		edit.addActionListener(a -> {
			
			last.setEnabled(false);
			next.setEnabled(false);

			for (Register r : registers) {
				r.setEditable(true);
				r.setVal(rm.R(registers.indexOf(r)));
			}
			
			ca.setEditable(true);
			ca.clearBackground();
		});

		Header.add(compile);
		Header.add(last);
		Header.add(next);
		Header.add(edit);

		add(Header, BorderLayout.NORTH);

		JPanel Body = new JPanel(new GridLayout(5, 5));
		

		Counter = new Register("BZ", rm);
		Body.add(Counter);
		
		Accumulator = new Register("A",rm);
		Body.add(Accumulator);
		
		registers = new ArrayList<Register>();
		for (int i = 0; i < 15; i++) {
			Register reg = new Register(i, rm);
			Body.add(reg);
			registers.add(reg);
		}

		add(Body, BorderLayout.CENTER);
	}

	public void setIndex(int index) {
		this.index = index;
		RegisterMachine now = steps.get(index), last = steps.get(index != 0 ? index -1 : 0);
		int[] R = now.getR();
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
