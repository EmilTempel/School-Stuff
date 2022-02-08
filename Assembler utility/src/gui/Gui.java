package gui;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

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
		
		right.add(new JScrollPane(new RegisterArea(ca), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		
		right.add(new JScrollPane(new Console(15), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		
		pnl.add(right);
		
		frame.getContentPane().add(pnl);
		frame.pack();
	}
	
	public static void main(String[] args){
		Gui gui = new Gui();
	}
	
}
