import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.JXDatePicker;

public class Signup implements KeyListener{
	String name="";
	
	Date date;
	
	static int count=0;
		
	JXDatePicker picker;
	
	ImageIcon bg;
	
	JLabel background,head,l1,l2,l3,l4,l5,l6,l7,l8;
	
	JTextField tfname,tfaddr,tfmail,tfpan,tfadhar;
	
	JFormattedTextField tfphone;
	
	JPasswordField pf;
	
	JButton b1,b2;
	
	JPanel p,p1,p2,p3,p4;
	
	GridBagConstraints cbc;

	Frame frm;
	
	MainWindow mainwindow;
	Signup(Frame frm, MainWindow mainWindow){
		this.frm=frm;
		this.mainwindow=mainWindow;
		p=new JPanel(new GridBagLayout());
		p1=new JPanel(new GridBagLayout());
		p2=new JPanel(new GridBagLayout());
		p3=new JPanel(new FlowLayout());
		p4=new JPanel(new GridBagLayout());
		
		head=new JLabel("Create Account");
		l1=new JLabel("NAME");		
		l2=new JLabel("ADDRESS");
		l3=new JLabel("BIRTHDATE");		
		l4=new JLabel("PHONE");
		l5=new JLabel("EMAIL");	
		l6=new JLabel("PASSWORD");		
		l7=new JLabel("PAN-CARD NO.");
		l8=new JLabel("ADHAR NO.");
		
		b1=new JButton("Sign-Up");
		b2=new JButton("Back");
		
		
		tfname=new JTextField(10);
		tfaddr=new JTextField(10);
		
		
		NumberFormat format = NumberFormat.
		getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		formatter.setMaximum(Integer.MAX_VALUE);
		formatter.setAllowsInvalid(false);
		// If you want the value to be committed on each keystroke instead of focus lost
		formatter.setCommitsOnValidEdit(true);
		tfphone = new JFormattedTextField(formatter);
		tfphone.setColumns(10);
		tfmail=new JTextField(10);
		pf=new JPasswordField(10);
		tfpan=new JTextField(10);
		tfadhar=new JTextField(10);

		picker = new JXDatePicker();
		
		tfname.addKeyListener(this);
		
		b1.addActionListener(new ActionListener(){
		
			public void actionPerformed(ActionEvent ae){
					
					String name=tfname.getText();
					String addr=tfaddr.getText();
					String phone=tfphone.getText();
					String email=tfpan.getText();
					String pwd= new String(pf.getPassword());
					String pcard=tfpan.getText();
					String adhar=tfadhar.getText();
				
					//insert into demo values('abc','xyz','1993-05-26',1234,'abc@123',12345,54321,98765);
					
					DB ob= new DB();
				
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				
					String dateString = format.format(picker.getDate());
					
					try{
					
						date = new Date(format.parse(dateString).getTime());
					
					}catch(Exception e){
						e.printStackTrace();
					}
					int r= ob.createUser(name,addr,date,phone,email,pwd,pcard,adhar);
					
					System.out.println(date);
					
					if(r>0){
						
						JOptionPane.showMessageDialog(null,"Successfully Signed up");
						
					}					
			
			}
			
		});
		
		b2.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){

				frm.remove();
				frm.addPanel(mainwindow.panel);
			}
		
		});
		
		frm.addPanel(createPanel());
	}
	
	public JPanel createPanel(){
		head.setFont(new Font("Serif", Font.PLAIN	, 38));
		l1.setFont(new Font("Serif", Font.PLAIN	, 24));
		l2.setFont(new Font("Serif", Font.PLAIN	, 24));
		l3.setFont(new Font("Serif", Font.PLAIN	, 24));
		l4.setFont(new Font("Serif", Font.PLAIN	, 24));
		l5.setFont(new Font("Serif", Font.PLAIN	, 24));
		l6.setFont(new Font("Serif", Font.PLAIN	, 24));
		l7.setFont(new Font("Serif", Font.PLAIN	, 24));
		l8.setFont(new Font("Serif", Font.PLAIN	, 24));
		
		b1.setFont(new Font("Serif", Font.PLAIN	, 24));
		b2.setFont(new Font("Serif", Font.PLAIN	, 24));
		
		tfname.setFont(new Font("Serif", Font.PLAIN	, 24));
		tfaddr.setFont(new Font("Serif", Font.PLAIN	, 24));
		tfmail.setFont(new Font("Serif", Font.PLAIN	, 24));
		tfphone.setFont(new Font("Serif", Font.PLAIN, 24));
		pf.setFont(new Font("Serif", Font.PLAIN	, 24));
		tfpan.setFont(new Font("Serif", Font.PLAIN	, 24));
		tfadhar.setFont(new Font("Serif", Font.PLAIN, 24));
		picker.setFont(new Font("Serif", Font.PLAIN	, 24));
		picker.setDate(Calendar.getInstance().getTime());
        
		picker.setFormats(new SimpleDateFormat("yyyy.MM.dd"));
					
		cbc=new GridBagConstraints();
		
		cbc.insets=new Insets(10,10,10,10);
		
		cbc.anchor=GridBagConstraints.NORTHWEST;
			
		p1.add(l1,cbc);
		
		cbc.gridx=0;
		
		cbc.gridy=1;
		
		cbc.anchor=GridBagConstraints.NORTHWEST;
		
		p1.add(l2,cbc);
		
		cbc.gridx=0;
		
		cbc.gridy=2;
		
		cbc.anchor=GridBagConstraints.NORTHWEST;
		
		p1.add(l3,cbc);
		
		cbc.gridx=0;
		
		cbc.gridy=3;
		
		cbc.anchor=GridBagConstraints.NORTHWEST;
		
		p1.add(l4,cbc);
		
		cbc.gridx=0;
		
		cbc.gridy=4;
		
		cbc.anchor=GridBagConstraints.NORTHWEST;
		
		p1.add(l5,cbc);
		
		cbc.gridx=0;
		
		cbc.gridy=5;
		
		cbc.anchor=GridBagConstraints.NORTHWEST;
		
		p1.add(l6,cbc);
		
		cbc.gridx=0;
		
		cbc.gridy=6;
		
		cbc.anchor=GridBagConstraints.NORTHWEST;
		
		p1.add(l7,cbc);
		
		cbc.gridx=0;
		
		cbc.gridy=7;
		
		cbc.anchor=GridBagConstraints.NORTHWEST;
		
		p1.add(l8,cbc);
		
		cbc.insets=new Insets(8,8,8,8);
		
		cbc.gridx=0;
		
		cbc.gridy=0;
		
		p2.add(tfname,cbc);
		
		cbc.gridx=0;
		
		cbc.gridy=1;
		
		p2.add(tfaddr,cbc);
		
		cbc.gridx=0;
		
		cbc.gridy=2;
		
		p2.add(picker,cbc);
		
		cbc.gridx=0;
		
		cbc.gridy=3;
		
		p2.add(tfphone,cbc);
		
		cbc.gridx=0;
		
		cbc.gridy=4;
		
		p2.add(tfmail,cbc);
		
		cbc.gridx=0;
		
		cbc.gridy=5;
		
		p2.add(pf,cbc);
		
		cbc.gridx=0;
		
		cbc.gridy=6;
		
		p2.add(tfpan,cbc);
		
		cbc.gridx=0;
		
		cbc.gridy=7;
		
		p2.add(tfadhar,cbc);
		
		p3.add(b1);
		
		p3.add(b2);
				
		cbc.gridx=0;
		
		cbc.gridy=0;
		
		p.add(p1,cbc);
		
		cbc.gridx=1;
		
		cbc.gridy=0;
		
		p.add(p2,cbc);
		
		cbc.gridx=0;
		
		cbc.gridy=0;
		
		cbc.anchor=GridBagConstraints.CENTER;
		
		p4.add(head,cbc);
		
		cbc.gridx=0;
		cbc.gridy=1;
		
		p4.add(p,cbc);
		
		cbc.gridx=0;

		cbc.gridy=2;
		
		p4.add(p3,cbc);
		
		p1.setOpaque(false);
		
		p2.setOpaque(false);
		
		p3.setOpaque(false);
		
		p4.setOpaque(false);
		
		p.setOpaque(false);
		
		//p.setBorder(BorderFactory.createLineBorder(Color.black));
		return p4;
	}
	
	public void keyTyped(KeyEvent ke){
		if(ke.getSource()==tfname){
			if((Character.isLetter(ke.getKeyChar()))  || (ke.getKeyChar() == KeyEvent.VK_BACK_SPACE) 
			|| (ke.getKeyChar() == KeyEvent.VK_DELETE) || (ke.getKeyChar() == KeyEvent.VK_SPACE)){
				System.out.println("ok");
			}
			else{
				JOptionPane.showMessageDialog(null,"Enter Character Only");
			}
		}
		if(ke.getSource()==tfmail){
			if((Character.isDigit(ke.getKeyChar()))  || (ke.getKeyChar() == KeyEvent.VK_BACK_SPACE) 
			|| (ke.getKeyChar() == KeyEvent.VK_DELETE) || (ke.getKeyChar() == KeyEvent.VK_SPACE)){
				System.out.println("ok");
			}
			else{
				JOptionPane.showMessageDialog(null,"Enter Character Only");
			}
		}
		/*if((ke.getKeyChar() == KeyEvent.VK_BACK_SPACE) ){
			StringBuilder str= new StringBuilder();
			str.append(name);
			str = str.deleteCharAt(str.length() - 1);
			name=str.toString();
		}
		else if(ke.getKeyChar() == KeyEvent.VK_DELETE){
			System.out.println(tfname.getCaretPosition());
			StringBuilder str= new StringBuilder();
			str.append(name);
			str = str.deleteCharAt(tfname.getCaretPosition());
			name=str.toString();
		}
		else{
			name+=ke.getKeyChar();
		}String regex = "^[a-zA-Z ]+$";
		if (!name.matches(regex)) { 
			JOptionPane.showMessageDialog(null,"Enter Character Only");
		}
		System.out.println(name);
		
		
		for email
		String regex = "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b";
		if (str.matches(regex)) { 
    // ...
}*/
	}
		
	public void keyReleased(KeyEvent ke){
		//code.
	}
	
	public void keyPressed(KeyEvent ke){
		//code.
	}

}
