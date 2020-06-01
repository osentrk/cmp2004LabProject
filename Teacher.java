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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.JColorChooser;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.text.SimpleDateFormat;

import javax.swing.JComponent;

import java.util.Timer;
import java.util.TimerTask;


public class Teacher extends JComponent {
	
	
	Color curColor;
	
	public Graphics2D graph;
	public Image img;
	public int curX, curY, prevX, prevY; //store mouse position
	public int operationCode;
	private int curStroke;
	
	
	public static final int PORT = 5353;
	private ServerSocket serverSocket;
	private Socket teacherSocket;
	ObjectOutputStream outStream;
	ObjectInputStream inStream;

	JButton deleteBtn, drawBtn, rectangleBtn, circleBtn, colorBtn,clearBtn, startLessonBtn, shoutBtn, attendanceBtn;
	private static Teacher tc;
		
	int counter = 2400000;
	int hour, minute, second;
	
	int numberOfRectangle;
	int numberOfCircle;
	
	String studentName;
	int studentNumber;
	
	public static void main(String[] args) throws Exception, IOException{
		tc = new Teacher();
		tc.showBoard();
		tc.runServer();
	}
	
	public Teacher() {
		setDoubleBuffered(false);
		numberOfRectangle = 0;
		numberOfCircle = 0;
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) { // get mouse position(x,y) when mouse pressed
				prevX = e.getX();
				prevY = e.getY();
				System.out.println("Coordinate when mouse clicked X: " + prevX + " Y: " + prevY);
			}		
		});
		
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				curX = e.getX();
				curY = e.getY();
				if(graph != null) {
					graph.drawLine(prevX, prevY, curX, curY);
					try {
						tc.sendPacket(prevX, prevY, curX, curY, operationCode, tc.getColor(),tc.getThickness());
						System.out.println(operationCode);
					}catch (Exception x) {
						x.printStackTrace();
					}
					repaint();
					prevX = curX; 
					prevY = curY;	
				}
				System.out.println("X: " + curX + " Y: " + curY);
			}
		});
	}
	
		
	public void waitConnection() throws Exception {
		System.out.println("Waiting connection");
		teacherSocket = serverSocket.accept();
		System.out.println("Student joined.");
		startLessonBtn.setEnabled(true);
		JOptionPane.showMessageDialog(null, "Student joined. You can use Start Lesson button to start timer." );
	}
	
	public void createStream() throws Exception{
		outStream = new ObjectOutputStream(teacherSocket.getOutputStream());
		outStream.flush();
		
		inStream = new ObjectInputStream(teacherSocket.getInputStream());
	}
	
	public Packet recvPacket() throws Exception {
		Packet recvPacket;
		recvPacket = new Packet();
		
		do {
			try {
				recvPacket = (Packet) inStream.readObject();
				if(recvPacket.opCode == 9) {
					JOptionPane.showMessageDialog(null, "Student asked: " + recvPacket.msg);
					String answer = JOptionPane.showInputDialog(null, recvPacket.msg, "Write the answer", JOptionPane.INFORMATION_MESSAGE);
					tc.sendPacket(answer, 9);
				}else if(recvPacket.opCode == 10) {
					studentName = recvPacket.name;
					studentNumber = recvPacket.stu;
				}
				
			}
			catch (Exception e) {
				System.out.println("recvPacket() error");
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
					JOptionPane.showMessageDialog(null, "Öðrenci kaçtý");
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
		
	protected void paintComponent (Graphics grp) {
		if(img == null) {
			img = createImage(getSize().width,getSize().height);
			graph = (Graphics2D) img.getGraphics();
			graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			clearAll();
		}
		grp.drawImage(img, 0, 0, null);
	}
	
	public void clear() { //eraser
		this.setThickness(10);
		graph.setPaint(Color.white);
		operationCode = 2;
		System.out.println("Board.clear() function is called.");
	}
	
	public void clearAll() {
		graph.setPaint(Color.white);
		graph.fillRect(0,0,getSize().width, getSize().height);
		graph.setPaint(curColor);
		try {
			tc.sendPacket(prevX, prevY, curX, curY, 6, tc.getColor(),tc.getThickness());
		}catch (Exception x) {
			x.printStackTrace();
		}
		repaint();
		rectangleBtn.setText("Draw a Rectangle");
		circleBtn.setText("Draw a Circle");
		numberOfRectangle = 0;
		numberOfCircle = 0;
		System.out.println("Board.clearAll() function is called.");
	}
	
	public void draw() { 
		this.setColor(curColor);
		operationCode = 1;
		System.out.println("Board.draw() function is called.");
	}
	
	public void rectangle() throws Exception {
		this.setColor(curColor);
		graph.drawRect(prevX, prevY, 80, 80);
		try {
			tc.sendPacket(prevX, prevY, curX, curY, 3, tc.getColor(),tc.getThickness());
		}catch (Exception x) {
			x.printStackTrace();
		}
		numberOfRectangle++;
		rectangleBtn.setText("Draw a Rectangle ("+numberOfRectangle+")");
		repaint();
	}
	
	public void circle() {
		this.setColor(curColor);
		graph.drawOval(prevX, prevY, 80, 80);
		try {
			tc.sendPacket(prevX, prevY, curX, curY, 4, tc.getColor(),tc.getThickness());
		}catch (Exception x) {
			x.printStackTrace();
		}
		numberOfCircle++;
		circleBtn.setText("Draw a Circle ("+numberOfCircle+")");
		repaint();
	}
		
	public void setThickness(int stroke) {
		graph.setStroke(new BasicStroke(stroke));
		curStroke = stroke;
		System.out.println("Line width is set to "+stroke);
	}
	
	public int getThickness() {
		return curStroke;
	}
	
	ActionListener aListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == deleteBtn) {
				tc.clear();
			}else if(e.getSource() == drawBtn) {
				if(tc.getColor() == null) {
					tc.setColor();
				}
				tc.setThickness(1);
				tc.draw();
			}else if(e.getSource() == rectangleBtn) {
				try {
					if(tc.getColor() == null) {
						tc.setColor();
					}
					tc.rectangle();
				}
				catch (Exception xxx) {
					xxx.printStackTrace();
				}
				
			}else if(e.getSource() == circleBtn) {
				if(tc.getColor() == null) {
					tc.setColor();
				}
				tc.circle();
			}else if(e.getSource() == colorBtn) {
				tc.setColor();
			}else if(e.getSource() == clearBtn) {
				tc.clearAll();
				System.out.println(tc.getThickness());
			}else if(e.getSource() == startLessonBtn) {
				Timer t = new Timer();
				startLessonBtn.setForeground(Color.red);
				counter = 300;
				TimerTask tTask = new TimerTask() {
					public void run() {
						
						hour = ((counter / 60) / 60);
						minute = (counter/60) % 60;
						second = counter % 60;
						
						startLessonBtn.setText(hour + ":" + minute + ":" + second);
						counter--;
						if(counter == -1) {
							t.cancel();
							JOptionPane.showMessageDialog(null, "Time is over.");
						}
					}					
				};
				t.scheduleAtFixedRate(tTask, 1000, 1000);
			}else if(e.getSource() == shoutBtn) {
				String shout = JOptionPane.showInputDialog(null, "Send message to all students", "Shout", JOptionPane.INFORMATION_MESSAGE);
				
				try {
					tc.sendPacket(shout, 11);
				}
				catch (Exception ex) {
					System.out.println("Can not shout");
				}
			}else if(e.getSource() == attendanceBtn) {
				String msg = studentName + " (" + studentNumber + ")";
				JOptionPane.showMessageDialog(null, msg);
				saveAttendance();
			}
		}
	};
	
	
	public void showBoard() {	
		JFrame jf = new JFrame("Teacher Side");
		Container content = jf.getContentPane();
		
		content.setLayout(new BorderLayout());
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
		startLessonBtn = new JButton ("Start Lesson");
		startLessonBtn.addActionListener(aListener);
		shoutBtn = new JButton ("Shout");
		shoutBtn.addActionListener(aListener);
		attendanceBtn = new JButton ("Get Attendance");
		attendanceBtn.addActionListener(aListener);
		
		control.add(drawBtn);
		control.add(deleteBtn);
		control.add(rectangleBtn);
		control.add(circleBtn);
		control.add(colorBtn);
		control.add(clearBtn);
		control.add(startLessonBtn);
		control.add(clearBtn);
		control.add(shoutBtn);
		control.add(attendanceBtn);
		control.add(startLessonBtn);
		
		startLessonBtn.setEnabled(false);
		
		content.add(control, BorderLayout.NORTH);
		jf.setSize(1000,800);
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		jf.setLocation(d.width/2 - jf.getSize().width/2, d.height/2 - jf.getSize().height/2);
				
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
	
	public void setColor() {
		curColor = JColorChooser.showDialog(null, "Choose a color", Color.BLACK);
		if(curColor != null) {
			tc.setColor(curColor);
		}else {
			JOptionPane.showMessageDialog(null, "You didn't select any color. Your color is set to black.");
			tc.setColor(Color.black);
		}
	}
	
	public void setColor(Color c) {
		graph.setPaint(c);
		curColor = c;
		System.out.println("Graph color set to: " + c);
	}
	
	public Color getColor() {
		return curColor;
	}
	
	public void sendPacket(int prevX, int prevY, int curX, int curY, int opCode, Color color, int stroke) throws Exception {
		try {
			Packet c = new Packet(prevX, prevY, curX, curY, opCode, color, stroke);
			outStream.writeObject(c);
			outStream.flush();
			System.out.println("C is sent!");
		} catch(Exception e) {
			System.out.println("Teacher.sendPacket() error!"+e);
			e.printStackTrace();
		}
	}
	
	public void sendPacket(String s, int opCode) throws Exception {
		try {
			Packet c = new Packet(s, opCode);
			outStream.writeObject(c);
			outStream.flush();
			System.out.println("C is sent!");
		} catch(Exception e) {
			System.out.println("Teacher.sendPacket() error!"+e);
			e.printStackTrace();
		}
	}
	
	public void saveAttendance() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss");
		System.out.println(formatter.format(date));
		try {
	      File myObj = new File(formatter.format(date) + "_attendance.txt");
	      if (myObj.createNewFile()) {
	        System.out.println("File created: " + myObj.getName());
	      } else {
	        System.out.println("File already exists.");
	      }
	      
	      FileWriter myWriter = new FileWriter(myObj);
	      myWriter.write("Student Name: " + studentName + " Student Number: " + studentNumber);
	      myWriter.close();
	      JOptionPane.showMessageDialog(null, "Attendance saved => attendance.txt");
	      
	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
	}
	
}
