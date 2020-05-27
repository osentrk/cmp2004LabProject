import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.LinkedList;

import javax.sound.sampled.Line;
import javax.swing.JComponent;

public class Board extends JComponent {
	
	Teacher tc;
	Color curColor;
	
	private Graphics2D graph;
	private Image img;
	
	public int curX, curY, prevX, prevY; //store mouse position
	
	public int operationCode;
	
	public Board() {
		setDoubleBuffered(false);
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) { // get mouse position(x,y) when mouse pressed
				prevX = e.getX();
				prevY = e.getY();
				System.out.println("Coordinate when mouse clicked X: " + prevX + " Y: " + prevY);
			}
					
		});
		
		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) { // get mouse position(x,y) when mouse pressed
				System.out.println("Mouse released");
				System.out.println(graph);
			}
					
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				curX = e.getX();
				curY = e.getY();
				if(graph != null) {
					graph.drawLine(prevX, prevY, curX, curY);
					tc.hýzýr(prevX,prevY,curX,curY,1);
					repaint();
					prevX = curX; 
					prevY = curY;	
					//System.out.println("Graph is not null");
				}
				else {
					System.out.println("over");
				}
				System.out.println("X: " + curX + " Y: " + curY);
			}
		});
		
		
	}
	
	protected void paintComponent (Graphics grp) {
		if(img == null) {
			img = createImage(getSize().width,getSize().height);
			graph = (Graphics2D) img.getGraphics();
			graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			clearAll();
		}
		grp.drawImage(img, 0, 0, null);
		System.out.println("drawed");
	}
	
	public void clear() {
		this.setThickness(new BasicStroke(10));
		graph.setPaint(Color.white);
		System.out.println("Board.clear() function is called.");
	}
	
	public void clearAll() {
		graph.setPaint(Color.white);
		graph.fillRect(0,0,getSize().width, getSize().height);
		graph.setPaint(curColor);
		repaint();
		System.out.println("Board.clearAll() function is called.");
	}
	
	public void draw() {
		this.setColor(curColor);
		System.out.println("Board.draw() function is called.");
	}
	
	public void rectangle() {
		this.setColor(curColor);
		graph.drawRect(prevX, prevY, 80, 80);
		repaint();
	}
	
	public void circle() {
		this.setColor(curColor);
		graph.drawOval(prevX, prevY, 80, 80);
		repaint();
	}
	
	public void setColor(Color c) {
		graph.setPaint(c);
		curColor = c;
		System.out.println("Graph color set to: " + c);
	}
	
	public void setThickness(BasicStroke b) {
		graph.setStroke(b);
	}
	
	public Stroke getThickness() {
		return graph.getStroke();
	}
}
