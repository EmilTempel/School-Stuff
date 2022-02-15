package gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class Gui {
	JFrame frame;
	public Gui(){
		frame = new JFrame();
		frame.setSize(500,800);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("ASSEMBLY IDE");
		
		JPanel pnl = new JPanel();
		pnl.setVisible(true);
		pnl.setLayout(new GridLayout(0,2));
		
		CodingArea ca = new CodingArea(1,20);
		
		pnl.add(new JScrollPane(ca, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		
		RegisterArea ra = new RegisterArea(ca);
		right.add(new JScrollPane(ra, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		Console console = new Console(15);
		right.add(new JScrollPane(console, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		
		pnl.add(right);
		
		setFont(right, new Font("TEST",0,20));
		
		frame.getContentPane().add(pnl);
		frame.pack();
	}
	
	public static void setFont(Container c, Font f) {
		c.setFont(f);
		if(c instanceof Container) {
			for(Component child : c.getComponents()) {
				if(child instanceof Container)
				setFont((Container)child,f);
			}
		}
	}
	
	public static void main(String[] args){
		Gui gui = new Gui();
	}
	
}
