
public class Coordinate {
	public int prevX;
	public int prevY;
	public int curX;
	public int curY;
	public int opCode;
	
	public Coordinate(int prevX, int prevY, int curX, int curY, int opCode) {
		this.prevX = prevX;
		this.prevY = prevY;
		this.curX = curX;
		this.curY = curY;
		this.opCode = opCode;
	}
}
