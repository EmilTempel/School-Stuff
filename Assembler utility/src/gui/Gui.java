package gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Gui {
	JFrame frame;
	public Gui(){
		frame = new JFrame();
		frame.setSize(500,500);
		frame.setVisible(true);
		
		JPanel pnl = new JPanel();
		pnl.setVisible(true);
		frame.getContentPane().add(pnl,BorderLayout.CENTER);
		
		JTextField code = new JTextField();
		code.setVisible(true);
		pnl.add(code);
		
	}
	
	public static void main(String[] args){
		new Gui();
	}
	
}
