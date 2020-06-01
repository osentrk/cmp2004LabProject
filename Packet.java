import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Color;
import java.io.*;

public class Packet implements Serializable {
	public int prevX;
	public int prevY;
	public int curX;
	public int curY;
	public int opCode;
	public Color color;
	public int stroke;
	public String msg;
	
	public Packet(int prevX, int prevY, int curX, int curY, int opCode, Color color, int stroke) {
		this.prevX = prevX;
		this.prevY = prevY;
		this.curX = curX;
		this.curY = curY;
		this.opCode = opCode;
		this.color = color;
		this.stroke = stroke;
	}
	
	public Packet(int prevX, int prevY, int curX, int curY, int opCode, Color color) {
		this.prevX = prevX;
		this.prevY = prevY;
		this.curX = curX;
		this.curY = curY;
		this.opCode = opCode;
		this.color = color;
	}
	
	public Packet (String message, int opCode) {
		this.msg = message;
		this.opCode = opCode;
	}
	
	public Packet() {
		this.prevX = -1;
		this.prevY = -1;
		this.curX = -1;
		this.curY = -1;
		this.opCode = -1;
		this.color = Color.black;
		this.stroke = 1;
	}
}
