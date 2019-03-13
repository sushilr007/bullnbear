import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MainWindow extends JPanel implements MouseListener{

JLabel head,l1,l2,l3;
	
	JTextField tf1,tf2;
	
	JButton signin,signup;
	
	JPanel panel,p1,p2,p3,p4;
	
	GridBagConstraints gbc;
	
	BufferedImage img1;
	
	Frame frm ;
	DB dbobj;
	MainWindow(){
		frm = new Frame();
		
		head=new JLabel();
		
		l1=new JLabel("Username");
		l2=new JLabel("Password");
		l3=new JLabel("<html><u>Forgot Password?</u></html>");
		
		tf1=new JTextField(20);
		tf2=new JTextField(20);

		try{
			
			img1= ImageIO.read(this.getClass().getResource("bull-bear.png"));

		}catch(Exception e){

			e.printStackTrace();
		}
		
		signin=new JButton("Sign-in");
		signup=new JButton("Sign-up");
		
		gbc=new GridBagConstraints();
		gbc.insets=new Insets(10,10,10,10);
		
		p1=new JPanel(new GridBagLayout());
		p2=new JPanel(new GridBagLayout());
		p3=new JPanel(new GridBagLayout());
		p4=new JPanel(new GridBagLayout());
		panel=new JPanel(new GridBagLayout());
		
		//panel.setBorder(BorderFactory.createLineBorder(Color.black));
		frm.addPanel(createPanel());
		frm.frm.setVisible(true);
		signin.addMouseListener(this);

		signup.addMouseListener(this);

		l3.addMouseListener(this);
	}

	public JPanel createPanel() {
		l1.setFont(new Font("Serif", Font.PLAIN, 28));
		l2.setFont(new Font("Serif", Font.PLAIN, 28));
		l3.setFont(new Font("Serif", Font.PLAIN	, 28));
		signin.setFont(new Font("Serif", Font.PLAIN	, 28));
		signup.setFont(new Font("Serif", Font.PLAIN	, 28));	
		tf1.setFont(new Font("Serif", Font.PLAIN, 20));
		tf2.setFont(new Font("Serif", Font.PLAIN, 20));	
		
		ImageIcon i = new ImageIcon(ScaledImage(img1, 350, 250));
		head.setIcon(i);
		
		gbc.anchor=GridBagConstraints.CENTER;	

		p1.add(head,gbc);

		gbc.gridx=0;
	
		gbc.gridy=1;

		gbc.anchor=GridBagConstraints.EAST;

		p2.add(l1,gbc);

		gbc.gridx=1;

		gbc.gridy=1;

		gbc.anchor=GridBagConstraints.WEST;

		p2.add(tf1,gbc);

		gbc.gridx=0;

		gbc.gridy=2;

		gbc.anchor=GridBagConstraints.EAST;

		p2.add(l2,gbc);

		gbc.gridx=1;

		gbc.gridy=2;

		gbc.anchor=GridBagConstraints.EAST;

		p2.add(tf2,gbc);

		gbc.gridx=0;

		gbc.gridy=0;

		p3.add(l3,gbc);

		gbc.insets=new Insets(5,5,5,5);

		gbc.gridx=1;

		gbc.gridy=0;

		p4.add(signin,gbc);

		gbc.gridx=2;

		gbc.gridy=0;

		p4.add(signup,gbc);

		gbc.gridx=0;

		gbc.gridy=0;

		gbc.anchor=GridBagConstraints.CENTER;

		panel.add(p1,gbc);

		gbc.gridx=0;

		gbc.gridy=1;

		panel.add(p2,gbc);

		gbc.gridx=0;

		gbc.gridy=2;

		gbc.anchor=GridBagConstraints.CENTER;

		panel.add(p3,gbc);

		gbc.gridx=0;

		gbc.gridy=3;

		gbc.anchor=GridBagConstraints.CENTER;

		panel.add(p4,gbc);

		panel.setOpaque(false);
		p1.setOpaque(false);
		p2.setOpaque(false);
		p3.setOpaque(false);
		p4.setOpaque(false);
		return panel;
		
	}
	public void mouseClicked(MouseEvent me){
		if(me.getSource()==signin){
			String usr=tf1.getText();
			String pwd=tf2.getText();
			dbobj=new DB();
			int i = dbobj.validate(usr,pwd);
			if(i==1){
				User user = dbobj.getInfo();
				panel.setVisible(false);
				new DashBoard(frm,this,user,dbobj);
				
			}
		
		}
		
		if(me.getSource()==signup){
		
			System.out.println("up");

			
			frm.panel.setVisible(false);

			new Signup(frm,this);
			
			frm.panel.setVisible(true);

			//su.initialize();
		}

		if(me.getSource()==l3){

			//new ForgotPassword();

			System.out.println("forgot");
			JPanel p = new JPanel(new GridBagLayout());
			JPanel p1 = new JPanel(new GridBagLayout());
			JPanel p2 = new JPanel(new GridBagLayout());
			JPanel p3 = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(10, 10, 10, 10);
			JTextField tf = new JTextField(20);
			tf.setFont(new Font("Serif", Font.PLAIN, 28));
			JButton send = new JButton("Send");
			JButton back = new JButton("Back");
			send.setFont(new Font("Serif", Font.PLAIN, 28));
			back.setFont(new Font("Serif", Font.PLAIN, 28));
			JLabel l1 = new JLabel("Password will be sent to your email");
			JLabel l2 = new JLabel("Enter Email : ");
			JLabel l3 = new JLabel("");
			l1.setFont(new Font("Serif", Font.PLAIN, 28));
			l2.setFont(new Font("Serif", Font.PLAIN, 28));
			l3.setFont(new Font("Serif", Font.PLAIN	, 28));
			p1.add(l1,gbc);
			p2.add(l2,gbc);
			gbc.gridx = 1;
			p2.add(tf,gbc);
			gbc.gridx=0;
			p3.add(send,gbc);
			gbc.gridx=1;
			p3.add(back,gbc);
			gbc.gridy=1;
			p3.add(l3,gbc);
			gbc.gridy=0;
			p.add(p1,gbc);
			gbc.gridy=1;
			p.add(p2,gbc);
			gbc.gridy=2;
			p.add(p3,gbc);
			frm.remove();
			frm.addPanel(p);
			p.setOpaque(false);
			p1.setOpaque(false);
			p2.setOpaque(false);
			p3.setOpaque(false);
			send.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					System.out.println(tf.getText());
					String pwd = DB.getPwd(tf.getText());
					System.out.println(pwd);
					new SendEmail(pwd,tf.getText());
					l3.setText("Mail Sent");
				}
			
			});
			back.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					frm.remove();
					frm.addPanel(panel);
				}
			
			});
		}	

	}
		
    public void mouseEntered(MouseEvent me){
    
		if(me.getSource() == l3){

			l3.setForeground(Color.red);

		}
	
		repaint();

    }

    
	public void mouseExited(MouseEvent me){
    
		if(me.getSource() == l3)

			l3.setForeground(Color.black);

		repaint();

	}
		
    public void mousePressed(MouseEvent mouseevent){
    
	}
    
	public void mouseReleased(MouseEvent mouseevent){
    
	}
	
	private Image ScaledImage(Image img,int w ,int h){

		BufferedImage resizedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = resizedImage.createGraphics();
	
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g2.drawImage(img,0,0,w,h,null); 

		g2.dispose();

		return resizedImage;

	}
	
}
