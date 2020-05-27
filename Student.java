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
	
	public static void main(String[] args) throws IOException {
		System.out.println("stu");
		new Student().showBoard();
		connection();
	}
	
	JButton sendDataBtn;
	Board brd;
	
	ActionListener aListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == sendDataBtn) {
				System.out.println("send data");
				//brd.clear();
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
	
	public static void connection() throws UnknownHostException, IOException {
		Socket socket = null;
		PrintWriter out = null;
        BufferedReader in = null;
        String deger;
        try {
             //* server 'a localhost ve 7755 portu üzerinden baþlantý saðlanýyor *//
             socket = new Socket("localhost", 7755);
        } catch (Exception e) {
             System.out.println("Port Hatasý!");
        }
        
      //* Server'a veri gönderimi için kullandýðýmýz PrintWriter nesnesi oluþturduk *//
        out = new PrintWriter(socket.getOutputStream(), true);

        //* Server'dan gelen verileri tutan BufferedReader nesnesi oluþturulur *//
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("Server'a gönderilecek sayýsý giriniz:");

        //* Gönderilecek sayýnýn giriþi yapýlýyor *//
        BufferedReader data = new BufferedReader(new InputStreamReader(System.in));

        while((deger = data.readLine()) != null) {
             out.println(deger);
             System.out.println("Server'dan gelen sonuç = " + in.readLine());
             System.out.println("Server'a gönderilecek saysý giriniz");
        }
        out.close();
        in.close();
        data.close();
        socket.close();
	}
	

}
