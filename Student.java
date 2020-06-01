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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Student {
	
	public static final int PORT = 5353;
	private String serverInfo;
	private Socket studentSocket;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;	
	
	JButton sendDataBtn;
	Board brd;
	private static Student st;
	
	String studentName;
	int studentNumber;
	
	public static void main(String[] args) throws Exception, IOException, ClassNotFoundException {
		System.out.println("Student side");
		st = new Student("127.0.0.1");
		st.login();
			
	}
	
	public void login() throws Exception, IOException, ClassNotFoundException {
		JTextField f1 = new JTextField();
		JTextField f2 = new JTextField();
		
		Object[] fields = {
				"Student Number" , f1,
				"Name - Surname" , f2
		};
		JOptionPane.showConfirmDialog(null, fields, "Join Class", JOptionPane.DEFAULT_OPTION);
		System.out.println(f1.getText());
		int i = Integer.parseInt(f1.getText());
		studentNumber = i;
		studentName = f2.getText();
		
		st.showBoard();
		st.runStudent();	
	}
	
		
	public Student(String ipAddress) {
		serverInfo = ipAddress;
	}
	
	
	ActionListener aListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == sendDataBtn) {
				try {
					String question = JOptionPane.showInputDialog(null, "Write your question", "You raised your hand", JOptionPane.QUESTION_MESSAGE);
					st.sendPacket(question , 9);
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
		brd = new Board(); 
		content.add(brd, BorderLayout.CENTER);
		
		JPanel control = new JPanel();
						
		sendDataBtn = new JButton ("Raise Hand");
		sendDataBtn.addActionListener(aListener);
		
		control.add(sendDataBtn);
		
		
		content.add(control, BorderLayout.NORTH);
		jf.setSize(1000,800);
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		jf.setLocation(d.width/2 - jf.getSize().width/2, d.height/2 - jf.getSize().height/2);
		
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
	
	public void connectTeacher() throws Exception{
		System.out.println("Connecting..");
		try {
			studentSocket = new Socket(InetAddress.getByName(serverInfo), PORT);
		}catch (Exception e) {
			System.out.println("Student cannot connect to Teacher.");
		}
		
	}
	
	public void createStream() throws Exception{
		outStream = new ObjectOutputStream(studentSocket.getOutputStream());
		inStream = new ObjectInputStream(studentSocket.getInputStream());
		
		st.sendPacket(studentNumber, studentName, 10); //login operation
	}
	
	public Packet recvPacket() throws Exception {
		Packet recvPacket;
		recvPacket = new Packet();
		
		do {
			try {
				recvPacket = (Packet) inStream.readObject();
				if(recvPacket.opCode == 1) { //drawing - erasing mode
					System.out.println("Drawing line");
					brd.setColor(recvPacket.color);
					brd.setThickness(new BasicStroke(recvPacket.stroke));
					brd.graph.drawLine(recvPacket.prevX, recvPacket.prevY, recvPacket.curX, recvPacket.curY);
					brd.repaint();
				} else if(recvPacket.opCode == 2) {
					System.out.println("Erasing line");
					brd.graph.setPaint(Color.white);
					brd.setThickness(new BasicStroke(recvPacket.stroke));
					brd.graph.drawLine(recvPacket.prevX, recvPacket.prevY, recvPacket.curX, recvPacket.curY);
					brd.repaint();
				}
				else if(recvPacket.opCode == 3) {
					brd.setColor(recvPacket.color);
					brd.setThickness(new BasicStroke(recvPacket.stroke));
					brd.graph.drawRect(recvPacket.prevX, recvPacket.prevY, 80, 80);
					brd.repaint();
				}else if(recvPacket.opCode == 4) {
					brd.setColor(recvPacket.color);
					brd.setThickness(new BasicStroke(recvPacket.stroke));
					brd.graph.drawOval(recvPacket.prevX, recvPacket.prevY, 80, 80);
					brd.repaint();
				}else if(recvPacket.opCode == 6) {
					brd.clearAll();
				}else if(recvPacket.opCode == 9) {
					JOptionPane.showMessageDialog(null, "Teacher answered: " + recvPacket.msg);
				}
				else if(recvPacket.opCode == 11) {
					JOptionPane.showMessageDialog(null, "Teacher shouted: " + recvPacket.msg);
				}
			}
			catch (Exception e) {
				System.out.println("recvPacket() error");
				e.printStackTrace();
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
		
	public void sendPacket(String s, int opCode) throws Exception {
		try {
			Packet c = new Packet(s, opCode);
			outStream.writeObject(c);
			outStream.flush();
			System.out.println("C is sent!");
		} catch(Exception e) {
			System.out.println("student.sendPacket() error!");
		}
	}
	
	public void sendPacket(int i, String s, int opCode) throws Exception {
		try {
			Packet c = new Packet(i, s, opCode);
			outStream.writeObject(c);
			outStream.flush();
			System.out.println("C is sent!");
		} catch(Exception e) {
			System.out.println("student.sendPacket() error!");
		}
	}
		
}
