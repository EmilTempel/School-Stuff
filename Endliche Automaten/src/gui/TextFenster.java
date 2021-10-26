package gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextFenster implements WindowListener {

	JFrame frame;
	JTextField tf;
	Thread t;

	public TextFenster() {
		frame = new JFrame();
		frame.setVisible(true);
		frame.setSize(200, 100);

		tf = new JTextField();
		tf.setVisible(true);

		frame.add(tf);
		
		TextFenster l = this;
		t = new Thread() {
			public void run() {
				frame.addWindowListener(l);
			}
		};
		
	}

	public String getText() {
		
		synchronized (t) {
			try {
				System.out.println("waiting...");
				wait();
				System.out.println("finished");
			} catch (Exception e) {

			}
		}
		t.start();
		return tf.getText();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		synchronized (t) {
			notify();
		}
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

}
