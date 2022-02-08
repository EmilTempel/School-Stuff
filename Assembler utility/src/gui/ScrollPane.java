package gui;

import java.awt.Component;

import javax.swing.JScrollPane;

public class ScrollPane extends JScrollPane{

	public ScrollPane(Component component) {
		setVisible(true);
		add(component);
	}
}
