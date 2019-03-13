import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Frame {
	
	JFrame frm;
	
	BufferedImage img1,img2;
	
	JLabel background;
	
	JPanel panel;
	
	Frame(){
		
		frm = new JFrame("Bull and Bear");
		
		frm.setTitle("Bull & Bear");
		
		frm.setSize(1920,1080);
		
		frm.setResizable(false);
		
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//frm.setUndecorated(true);
		
		frm.setLocationRelativeTo(null);
		
		try{
	
			img1= ImageIO.read(this.getClass().getResource("cream2.jpg"));
			
			img2= ImageIO.read(this.getClass().getResource("bull-bear.png"));

		}catch(Exception e){

			e.printStackTrace();
		}
		
		//java.net.URL url = getClass().getClassLoader().getResource("bull-bear.png");
        
		ImageIcon i = new ImageIcon(img2);
        
		frm.setIconImage(i.getImage());
		
		background = new JLabel();
		
		ImageIcon bg=new ImageIcon(ScaledImage(img1, 1920, 1080));

		background.setLayout(new GridBagLayout());
		
		background.setIcon(bg);
		
		frm.add(background);
		
	}
	
	private Image ScaledImage(Image img,int w ,int h){

		BufferedImage resizedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = resizedImage.createGraphics();
	
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g2.drawImage(img,0,0,w,h,null); 

		g2.dispose();

		return resizedImage;

	}

	public void addPanel(JPanel panel){
		this.panel = panel;
		background.add(panel);
		panel.setVisible(true);
	}

	public void remove(){
		panel.setVisible(false);
	}
}
