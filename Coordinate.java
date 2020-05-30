import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Color;
import java.io.*;

public class Coordinate implements Serializable {
	public int prevX;
	public int prevY;
	public int curX;
	public int curY;
	public int opCode;
	public Color color;
	public BasicStroke stroke;
	
	public Coordinate(int prevX, int prevY, int curX, int curY, int opCode, Color color, BasicStroke stroke) {
		this.prevX = prevX;
		this.prevY = prevY;
		this.curX = curX;
		this.curY = curY;
		this.opCode = opCode;
		this.color = color;
		this.stroke = stroke;
		System.out.println("coorddddddin:" + stroke);
	}
	
	public Coordinate(int prevX, int prevY, int curX, int curY, int opCode, Color color ) {
		this.prevX = prevX;
		this.prevY = prevY;
		this.curX = curX;
		this.curY = curY;
		this.opCode = opCode;
		this.color = color;
		
		

		
	}
	
	public Coordinate() {
		this.prevX = -1;
		this.prevY = -1;
		this.curX = -1;
		this.curY = -1;
		this.opCode = -1;
		this.color = Color.black;
		this.stroke = new BasicStroke(1);
	}
}
