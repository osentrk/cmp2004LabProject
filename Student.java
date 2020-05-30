import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Student {
	
	public static final int PORT = 5353;
	private String serverInfo;
	private Socket studentSocket;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	private static Student st;
	public static void main(String[] args) throws Exception, IOException, ClassNotFoundException {
		System.out.println("Student side");
		st = new Student("127.0.0.1");
		
		st.showBoard();
		st.runStudent();		
	}
	
	public Student(String ipAddress) {
		serverInfo = ipAddress;
	}
	
	JButton sendDataBtn;
	Board brd;
	
	ActionListener aListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == sendDataBtn) {
				try {
					//st.sendCoordinate();
				}catch (Exception x) {
					System.out.println("student.sendCoordinate() in button listener errror!");
				}
			}
		}
	};
	
	public void showBoard() {
		JFrame jf = new JFrame("Student Side");
		Container content = jf.getContentPane();
		
		content.setLayout(new BorderLayout());
		//content.setBackground(Color.white);
		brd = new Board(); 
		content.add(brd, BorderLayout.CENTER);
		
		JPanel control = new JPanel();
		sendDataBtn = new JButton ("Eraser");
		sendDataBtn.addActionListener(aListener);
				
		control.add(sendDataBtn);
		
		
		content.add(control, BorderLayout.NORTH);
		jf.setSize(800,600);
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		jf.setLocation(d.width/2 - jf.getSize().width/2, d.height/2 - jf.getSize().height/2);
		
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
	
	public void connectTeacher() throws Exception{
		System.out.println("Connecting..");
		studentSocket = new Socket(InetAddress.getByName(serverInfo), PORT);
	}
	
	public void createStream() throws Exception{
		outStream = new ObjectOutputStream(studentSocket.getOutputStream());
		inStream = new ObjectInputStream(studentSocket.getInputStream());
	}
	
	public Coordinate recvPacket() throws Exception {
		Coordinate recvPacket;
		recvPacket = new Coordinate();
		
		do {
			try {
				recvPacket = (Coordinate) inStream.readObject();
				if(recvPacket.opCode == 1) {
					System.out.println("Drawing line");
					brd.setColor(recvPacket.color);
					brd.setThickness(recvPacket.stroke);
					brd.graph.drawLine(recvPacket.prevX, recvPacket.prevY, recvPacket.curX, recvPacket.curY);
					brd.repaint();
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
			studentSocket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runStudent() throws Exception{
		try {
			connectTeacher();
			createStream();
			System.out.println(recvPacket());
		}catch (EOFException e) {
			System.out.println("\nClient Terminated Conn\n");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			stopServer();
		}
	}
	
	/*public void sendCoordinate() throws Exception {
		try {
			//Coordinate c = new Coordinate(3,4,17,15,2);
			outStream.writeObject(c);
			outStream.flush();
			System.out.println("C is sent!");
		} catch(Exception e) {
			System.out.println("student.sendCoordinate() error!");
		}
	}*/
		
}
