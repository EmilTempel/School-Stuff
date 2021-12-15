package projection;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import geometry.Mesh;
import geometry.Polytope;
import geometry.Vector;

public class GUI {

	public GUI() {

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(360, 210);
		frame.setLayout(null);

		PointOfView PoV = new PointOfView(new Vector(0, 0, 0));
		PoV.setAlpha(Math.toRadians(0));
		PoV.setBeta(Math.toRadians(0));
		PoV.setS(new Vector(-25,0,0));
		
		Polytope p = new Polytope(new Vector(-5,-5,-5),10,10,10);
		Mesh m = p.calcFaces();
		
		JLabel lbl = new JLabel() {
			Vector u = new Vector(0,0,1);
			Vector w = new Vector(-1,0,0);
			protected void paintComponent(Graphics g) {
				PoV.paint(m, g);
				PoV.setS(Vector.add(PoV.getS(), Vector.mult(Vector.cross(u,w),0.005)));
				w = Vector.sub(PoV.getS(),new Vector(0,0,0));
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				repaint();
			}
		};
		lbl.setSize(360, 180);
		lbl.setVisible(true);
		SwingUtilities.updateComponentTreeUI(frame);
		frame.add(lbl);

	}

	public static void main(String[] args) {
		GUI gui = new GUI();
		
	}
}
