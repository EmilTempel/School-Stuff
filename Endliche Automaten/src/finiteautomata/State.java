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

	static final int width = 100, height = 50, d = 3, arc = 15, stroke = 2, size = 20;

	String name;
	boolean end;
	int x, y;

	public State(String name, boolean end) {
		this(name, end, 0, 0);
	}

	public State(String name, boolean end, int x, int y) {
		this.name = name;
		this.end = end;
		this.x = x;
		this.y = y;
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

	}

	public void connectTo(Graphics2D g,State s, String connection) {
		Vector r = Vector.add(s.mid(), Vector.mult(mid(),-1));
		
		Gui.drawArrow(g, getPointIn(r), s.getPointIn(Vector.mult(r, -1)), 5);
	}

	public Point getPointIn(Vector r) {
		return Gui.getPointOnRect(r, x, y, width, height);
	}
	
	public Vector mid() {
		return new Vector(x+width/2, y + height/2);
	}
	
	public boolean contains(Point p) {
		return new Rectangle(x,y,width,height).contains(p);
	}
	
	public boolean intersects(Rectangle r) {
		return r.intersects(x, y, width, height);
	}

	public String toString() {
		return "{" + name + ":" + end + "}";
	}
}
