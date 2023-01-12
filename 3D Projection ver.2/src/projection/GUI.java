package projection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import geometry.Mesh;
import geometry.Polytope;
import geometry.Vector;

public class GUI {

	public GUI() {
		int width = 1600;
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(width, width * 9 / 16 + 30);
		frame.setLayout(null);

		double FOV = 90;

		PointOfView PoV = new PointOfView(new Vector(0, 0, 0), FOV, width);
		PoV.setAlpha(Math.toRadians(0));
		PoV.setBeta(Math.toRadians(0));
		PoV.setS(new Vector(4, 0, 0));
		PoV.SetR(new Vector(0, 1, 0));

		double R = 4;
		double r = 1;


//		Mesh m = Mesh.MarchingCubes(new Vector(5,5,5), -10, 20, v -> v.x()*v.x()+v.y()*v.y()-3);
//		Mesh m = Mesh.MarchingCubes(new Vector(5,5,5), -10, 20, v -> Vector.dot(Vector.sub(v,new Vector(-1,0,-3.5)), Vector.sub(v,new Vector(-1,0,0)))-r);
//		Mesh m = Mesh.MarchingCubes(new Vector(5,5,5), -10, 20, v -> 0);
		Mesh m = Mesh.MarchingCubes(new Vector(5,5,5), -10, 20, v -> Math.pow((Vector.dot(v, v)-R*R-r*r),2)+4*R*R*(v.x()*v.x()+v.y()*v.y()));
//		Mesh m =  Mesh.MarchingCubes(new Vector(5,5,5), -10, 20, v -> Math.tan(v.x(0))*Math.exp(v.x(1)) -v.x(2));
//		Mesh m =  Mesh.MarchingCubes(new Vector(5,5,5), -10, 20, v -> (Math.sin(v.x(0))*Math.cos(v.x(1))-v.x(2)));
//		Mesh m =  Mesh.MarchingCubes(new Vector(5,5,5), -10, 20, v -> v.y()*Math.pow(v.x(), 3)-v.x()*Math.pow(v.y(), 3) - v.x(2));
//		Mesh m =  Mesh.MarchingCubes(new Vector(5,5,5), -10, 20, v -> Math.exp(v.x()) * Math.log(v.y()+5) + Math.exp(-v.x()) * Math.log(v.y()+5)  - v.z());
//		Mesh m =  Mesh.MarchingCubes(new Vector(5,5,5), -10, 40, v -> v.x()*v.x() + v.y()*v.y()*v.y()-v.z());
//		Mesh m = new Polytope(new Vector(5, -20, 5), -10, -10, -10).calcFaces();

		JLabel lbl = new JLabel() {
			Vector u = new Vector(0, 0, 1);
			Vector w = new Vector(-1, 0, 0);

			protected void paintComponent(Graphics g) {
//				PoV.setS(Vector.mult(Vector.norm(Vector.add(PoV.getS(), Vector.mult(Vector.norm(Vector.cross(u,w)),0.1))), 20));
//				PoV.SetR(Vector.norm(PoV.getS()));
				PoV.paint(m, g);
				g.setColor(Color.BLACK);
//				System.out.println(PoV.getR());
//				g.drawRect((int) Functions.map(180-FOV/2, 0, 360, 0, width),(int) Functions.map( 90-FOV/2, 0, 360, 0, width), (int) Functions.map(FOV, 0, 360, 0, width), (int) Functions.map(FOV, 0, 360, 0, width));
//				System.out.println(Math.toDegrees(PoV.getAlpha()));
				w = PoV.getS();
//				try {
//					Thread.sleep(1);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				repaint();
			}
		};
		lbl.setSize(width, width * 9 / 16);
		lbl.setVisible(true);
		SwingUtilities.updateComponentTreeUI(frame);
		frame.add(lbl);
		frame.addKeyListener(PoV);
		frame.addMouseMotionListener(PoV);
		frame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
				new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR), new Point(0, 0), "invisibleCursor"));
	}

	public static void main(String[] args) {
		GUI gui = new GUI();

	}
}
