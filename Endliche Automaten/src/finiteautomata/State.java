package finiteautomata;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import geometry.Vector;
import gui.Gui;

public class State {

	public static final int width = 100, height = 50, d = 3, arc = 15, stroke = 2, arrow_stroke = 4, size = 20;

	String name;
	boolean start, end;
	int x, y;

	public State(String name, boolean start, boolean end) {
		this(name, end, 0, 0);
	}

	public State(String name, boolean end, int x, int y) {
		this.name = name;
		this.end = end;
		this.x = x;
		this.y = y;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public boolean isEnd() {
		return end;
	}

	public String getName() {
		return name;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public void draw(Graphics2D g) {
		g.setStroke(new BasicStroke(stroke));
		g.setColor(Color.WHITE);
		g.fillRoundRect(x, y, width, height, arc, arc);
		g.setColor(Color.BLACK);
		g.drawRoundRect(x, y, width, height, arc, arc);
		if (end)
			g.drawRoundRect(x + d, y + d, width - 2 * d, height - 2 * d, arc, arc);

		g.setFont(new Font("OCR A Extended", Font.CENTER_BASELINE, size));
		g.drawString(name, (x + width / 2) - (int) ((double) name.length() / (double) 3 * size),
				y + height / 2 + (int) (size / 3));

		if (start) {
			Gui.drawArrow(g, x - 50, y + height / 2, x, y + height / 2, arrow_stroke);
		}
	}

	public void connectTo(Graphics2D g, State s, String connection) {
		if (!this.equals(s)) {
			Vector r = Vector.add(s.mid(), Vector.mult(mid(), -1));
			Gui.drawArrow(g, getPointIn(r), s.getPointIn(Vector.mult(r, -1)), arrow_stroke);
		} else {
			g.setStroke(new BasicStroke(arrow_stroke));
			g.drawLine(x + width / 4, y, x + width / 4, y - height / 4);
			g.drawLine(x + width / 4, y - height / 4, x + width * 3 / 4, y - height / 4);
			Gui.drawArrow(g, x + width * 3 / 4, y - height / 4, x + width * 3 / 4, y, arrow_stroke);
		}
	}

	public void connectTo(Graphics2D g, Vector v, String connection) {
		Vector r = Vector.add(v, Vector.mult(mid(), -1));

		Gui.drawArrow(g, getPointIn(r), Gui.getPointFrom(v), arrow_stroke);
	}
	
	public void connectFrom(Graphics2D g, Vector v, String connection) {
		Vector r = Vector.add(v, Vector.mult(mid(), -1));

		Gui.drawArrow(g, Gui.getPointFrom(v), getPointIn(r), arrow_stroke);
	}

	public Point getPointIn(Vector r) {
		return Gui.getPointOnRect(r, x, y, width, height);
	}

	public Vector mid() {
		return new Vector(x + width / 2, y + height / 2);
	}

	public boolean contains(Point p) {
		return new Rectangle(x, y, width, height).contains(p);
	}

	public boolean intersects(Rectangle r) {
		return r.intersects(x, y, width, height);
	}

	public String toString() {
		return "{" + name + ":" + end + "}";
	}
}
