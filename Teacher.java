import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JColorChooser;
import java.io.*;
import java.net.*;


public class Teacher {
	
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	
	JButton deleteBtn, drawBtn, rectangleBtn, circleBtn, colorBtn,clearBtn;
	Board brd;
	Color curColor;
	static ServerSocket serverSocket;
	static Socket clientSocket = null;
	static String clientGelen = null;
	
	static int sayi;
	
	public static void main(String[] args) throws IOException{
		new Teacher().showBoard();
		//new Teacher().listen();
		listen();
		//System.out.println("AAAAAAA" + new Board().prevX);
	}
	
	public static void SocketHandler(Socket cs) throws IOException {
	    oos = new ObjectOutputStream(cs.getOutputStream());
	    ois = new ObjectInputStream(cs.getInputStream());
	}	
	
	public static void sendObject(Object o) throws IOException {
	    oos.writeObject(o);
	    oos.flush();
	}

	public Object receiveObject() throws IOException, ClassNotFoundException {
	    return ois.readObject();
	}
	
	
	ActionListener aListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == deleteBtn) {
				brd.clear();
			}else if(e.getSource() == drawBtn) {
				brd.setThickness(new BasicStroke(1));
				brd.draw();
			}else if(e.getSource() == rectangleBtn) {
				brd.rectangle();
			}else if(e.getSource() == circleBtn) {
				brd.circle();
			}else if(e.getSource() == colorBtn) {
				curColor = JColorChooser.showDialog(null, "Choose a color", Color.BLACK);
				brd.setColor(curColor);
			}else if(e.getSource() == clearBtn) {
				brd.clearAll();
				System.out.println(brd.getThickness());
			}
		}
	};
	
	
	
	public void showBoard() {
				
		JFrame jf = new JFrame("Teacher Side");
		Container content = jf.getContentPane();
		
		content.setLayout(new BorderLayout());
		//content.setBackground(Color.white);
		brd = new Board(); 
		content.add(brd, BorderLayout.CENTER);
		
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
	
	public static void listen() throws UnknownHostException, IOException {
		try {
			serverSocket = new ServerSocket(7755);
		} catch (Exception e) {
			System.out.println("Port Error");
		}
		System.out.println("Ready to start");
		clientSocket = serverSocket.accept();
		SocketHandler(clientSocket);
		
		//* Client'a veri gönderimi için kullandýðýmýz PrintWriter nesnesi oluþturulur *//
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        //* Client'dan gelen verileri tutan BufferedReader nesnesi oluþturulur *//
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        /*while((clientGelen = in.readLine()) != null) {
             System.out.println("Client'dan gelen veri = " + clientGelen);
             sayi = Integer.valueOf(clientGelen);
             out.println(sayi*sayi);
        }*/
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
		
	}
	
	public static void hýzýr(int prevX, int prevY, int curX, int curY, int opCode){
		Coordinate cn = new Coordinate(prevX, prevY, curX, curY, opCode);
		try {
			
		} catch (IOException e) {
			sendObject(cn);
		}
		
		System.out.println("pX: " + prevX + " pY: " + prevY + " cX: " + curX + " cY: " + curY + " opCode" + opCode);
	}
}
