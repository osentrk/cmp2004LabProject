import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JColorChooser;
import java.io.*;
import java.net.*;

import javax.swing.JComponent;


public class Teacher extends JComponent {
	
	
	Color curColor;
	
	public Graphics2D graph;
	public Image img;
	
	public int curX, curY, prevX, prevY; //store mouse position
	
	public int operationCode;
	
	
	/* tahta deneme */
	
	
	
	
	public static final int PORT = 5353;
	private ServerSocket serverSocket;
	private Socket teacherSocket;
	ObjectOutputStream outStream;
	ObjectInputStream inStream;
	
		
	public void waitConnection() throws Exception {
		System.out.println("Waiting connection");
		teacherSocket = serverSocket.accept();
		System.out.println("Student joined.");
	}
	
	public void createStream() throws Exception{
		outStream = new ObjectOutputStream(teacherSocket.getOutputStream());
		outStream.flush();
		
		inStream = new ObjectInputStream(teacherSocket.getInputStream());
	}
	
	public Coordinate recvPacket() throws Exception {
		Coordinate recvPacket;
		recvPacket = new Coordinate();
		
		do {
			try {
				recvPacket = (Coordinate) inStream.readObject();
				System.out.println(recvPacket.opCode);
			}
			catch (Exception e) {
				System.out.println("recvPacket() error"); //TODO: kontrol, sonsuz döngüye giriyor
			}
		}while(recvPacket.opCode != -2);
		
		return recvPacket;
	}
	
	public void stopServer() throws IOException{
		try {
			outStream.close();
			inStream.close();
			teacherSocket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runServer() throws Exception {
		
		try {
			serverSocket = new ServerSocket(PORT);
			
			while(true) {
				try {
					waitConnection();
					createStream();
					System.out.println(recvPacket());
				}
				catch (EOFException e) {
					System.out.println("Server Terminated Conn");
				}
				finally {
					stopServer();
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	JButton deleteBtn, drawBtn, rectangleBtn, circleBtn, colorBtn,clearBtn;
	//Board brd;
	
	private static Teacher tc;
	
	public static void main(String[] args) throws Exception, IOException{
		tc = new Teacher();
		tc.showBoard();
		tc.runServer();
		
		/* tahta deneme */
	}
	
	public Teacher() {
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
					try {
						tc.sendCoordinate(prevX, prevY, curX, curY, 1, tc.getColor(), tc.getThickness());
					}catch (Exception x) {
						x.printStackTrace();
					}
					repaint();
					prevX = curX; 
					prevY = curY;	
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
	BasicStroke strokes;
	public void rectangle() throws Exception {
		strokes = ( BasicStroke ) graph.getStroke();
		System.out.println("kkkkk:" + strokes);
		/*this.setColor(curColor);
		graph.drawRect(prevX, prevY, 80, 80);
		repaint();*/
	}
	
	public void circle() {
		tc.setThickness(strokes);
		//b();
		/*this.setColor(curColor);
		graph.drawOval(prevX, prevY, 80, 80);
		repaint();*/
	}
	
	public void setColor(Color c) {
		graph.setPaint(c);
		curColor = c;
		System.out.println("Graph color set to: " + c);
	}
	
	public void setThickness(BasicStroke b) {
		graph.setStroke(b);
	}
	
	public BasicStroke getThickness() {
		BasicStroke stroke;
		stroke = (BasicStroke) graph.getStroke();
		return stroke;
	}

	ActionListener aListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == deleteBtn) {
				tc.clear();
			}else if(e.getSource() == drawBtn) {
				tc.setThickness(new BasicStroke(1));
				tc.draw();
			}else if(e.getSource() == rectangleBtn) {
				try {
					tc.rectangle();
				}
				catch (Exception xxx) {
					xxx.printStackTrace();
				}
				
			}else if(e.getSource() == circleBtn) {
				tc.circle();
			}else if(e.getSource() == colorBtn) {
				curColor = JColorChooser.showDialog(null, "Choose a color", Color.BLACK);
				tc.setColor(curColor);
			}else if(e.getSource() == clearBtn) {
				tc.clearAll();
				System.out.println(tc.getThickness());
			}
		}
	};
	
	
	
	public void showBoard() {
				
		JFrame jf = new JFrame("Teacher Side");
		Container content = jf.getContentPane();
		
		content.setLayout(new BorderLayout());
		//content.setBackground(Color.white);
		//brd = new Board(); 
		content.add(tc, BorderLayout.CENTER);
		
		JPanel control = new JPanel();
		deleteBtn = new JButton ("Eraser");
		deleteBtn.addActionListener(aListener);
		drawBtn = new JButton ("Draw with Pen");
		drawBtn.addActionListener(aListener);
		rectangleBtn = new JButton ("Draw a Rectangle");
		rectangleBtn.addActionListener(aListener);
		circleBtn = new JButton ("Draw a Circle");
		circleBtn.addActionListener(aListener);
		colorBtn = new JButton ("Choose Color");
		colorBtn.addActionListener(aListener);
		clearBtn = new JButton ("Clear All");
		clearBtn.addActionListener(aListener);
		
		
		control.add(drawBtn);
		control.add(deleteBtn);
		control.add(rectangleBtn);
		control.add(circleBtn);
		control.add(colorBtn);
		control.add(clearBtn);
		
		content.add(control, BorderLayout.NORTH);
		jf.setSize(800,600);
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		jf.setLocation(d.width/2 - jf.getSize().width/2, d.height/2 - jf.getSize().height/2);
		
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
	
	public Color getColor() {
		return curColor;
	}
	
	public void sendCoordinate(int prevX, int prevY, int curX, int curY, int opCode, Color color, BasicStroke strokesssss) throws Exception {
		System.out.println("okito");
		System.out.println(strokesssss);
		//retr
		try {
			Coordinate c = new Coordinate(prevX, prevY, curX, curY, opCode, color, strokesssss);
			outStream.writeObject(c);
			outStream.flush();
			System.out.println("C is sent!");
		} catch(Exception e) {
			System.out.println("Teacher.sendCoordinate() error!"+e);
			//e.printStackTrace();
		}
	}
	
}
