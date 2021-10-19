package gui;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

import finiteautomata.FiniteAutomaton;
import finiteautomata.State;
import geometry.Vector;

public class Gui extends JLabel implements MouseListener, MouseMotionListener {

	JFrame frame;
	FiniteAutomaton automaton;

	State dragged, connect;
	Point dragged_point;

	public Gui(int width, int height) {
		frame = new JFrame();
		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.setLayout(null);

		this.setLocation(0, 0);
		this.setSize(width, height);
		this.setVisible(true);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		automaton = new FiniteAutomaton();

	}

	public void setFA(FiniteAutomaton automaton) {
		this.automaton = automaton;
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		automaton.paint(g2);
		if (dragged != null) {
			dragged.draw(g2);
		}
		if(connect != null) {
			
		}
		repaint();
	}

	public static void drawArrow(Graphics2D g, Point p1, Point p2, int stroke) {
		drawArrow(g, p1.x, p1.y, p2.x, p2.y, stroke);
	}

	public static void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2, int stroke) {
		g.setStroke(new BasicStroke(stroke));

		Vector n = Vector.mult(Vector.norm(new Vector(x2 - x1, y2 - y1)), stroke * 1.5);
		Vector v = Vector.add(new Vector(x2, y2), Vector.mult(n, -1.5));
		Vector u = Vector.add(new Vector(x1, y1), n);
		Polygon t = new Polygon();

		addVector(t, new Vector(x2, y2));
		addVector(t, Vector.add(v, n = Vector.cross(n)));
		addVector(t, Vector.add(v, Vector.mult(n, -1)));

		g.drawLine((int) u.x(0), (int) u.x(1), (int) v.x(0), (int) v.x(1));
		g.fillPolygon(t);
	}

	public static Point getPointOnRect(Vector r, int x, int y, int w, int h) {
		r = Vector.mult(Vector.norm(r), max(w / 2, h / 2));
		r = new Vector(min(max((int) r.x(0), -w / 2), w / 2), min(max((int) r.x(1), -h / 2), h / 2));
		return new Point(x + w / 2 + (int) r.x(0), y + h / 2 + (int) r.x(1));
	}

	public static int max(int i, int j) {
		return i > j ? i : j;
	}

	public static int min(int i, int j) {
		return i < j ? i : j;
	}

	public static void addVector(Polygon p, Vector v) {
		p.addPoint((int) v.x(0), (int) v.x(1));
	}
	
	public static Point getPointFrom(Vector v) {
		return new Point((int)v.x(0), (int)v.x(1));
	}

	public void mouseClicked(MouseEvent e) {
		State s = automaton.StateAt(e.getPoint());
		if (s != null) {
			if (connect == null) {
				connect = s;
			} else {
				automaton.addEdge(connect, s, "");
				connect = null;
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) {

	}

	public void mouseExited(MouseEvent arg0) {

	}

	public void mousePressed(MouseEvent e) {
		State s = automaton.StateAt(e.getPoint());
		if (s != null) {
			switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				dragged = s;
				dragged_point = new Point(dragged.getX() - e.getX(), dragged.getY() - e.getY());

				break;
			case MouseEvent.BUTTON2:
				s.setEnd(!s.isEnd());
				break;
			case MouseEvent.BUTTON3:
				automaton.removeState(s);
				break;
			}

		} else {
			switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				automaton.addState("", false, e.getX() - State.width / 2, e.getY() - State.height / 2);
				break;
			case MouseEvent.BUTTON2:

				break;
			case MouseEvent.BUTTON3:

				break;
			}

		}

	}

	public void mouseReleased(MouseEvent arg0) {
		dragged = null;
		dragged_point = null;
	}

	public void mouseDragged(MouseEvent e) {
		if (dragged != null) {
			dragged.setX(e.getX() + dragged_point.x);
			dragged.setY(e.getY() + dragged_point.y);
		}

	}

	public void mouseMoved(MouseEvent e) {

	}

	public static void main(String[] args) {
		Gui g = new Gui(1000, 1000);

		FiniteAutomaton fa = new FiniteAutomaton();
		fa.addState("start", true);
		fa.addState("z1", false, 120, 0);
		fa.addState("z2", true, 300, 200);
		fa.addState("z3", false, 500, 0);

		fa.addEdge(0, 1, ".");
		fa.addEdge(0, 2, ".");
		fa.addEdge(0, 3, ".");
		fa.addEdge(3, 0, ".");

		g.setFA(fa);
	}
}
