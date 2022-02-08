package gui;

import java.awt.Font;
import java.io.PrintStream;

import javax.swing.JTextPane;

public class Console extends JTextPane{
	
	public Console(int FontSize) {
		setSize(200,100);
		setVisible(true);
		
		PrintStream out = new PrintStream(System.out,true) {
			public void println(String s) {
				setText(getText() + (!getText().equals("") ? "\n" : "") + s);
			}
			
			public void print(String s) {
				setText(getText() + s);
			}
		};
		System.setOut(out);
		
		setEditable(false);
		
		setFont(new Font("Test", 0, FontSize));
	}
	
	
	
}
