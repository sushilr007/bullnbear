import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class DashBoard implements ActionListener {

	Frame frm;
	
	MainWindow mainWindow;
	
	JPanel panel;
	
	JLabel l;
	
	JMenuBar mbar;
	Home hme;
	JMenuItem home,portfolio,mywatchlist,help,logout,trend;
	JScrollPane jsp;
	User user;
	DB dbobj;
	public DashBoard(Frame frm, MainWindow mainWindow,User user, DB dbobj) {
		this.frm = frm;
		this.dbobj = dbobj;
		this.mainWindow = mainWindow;
		
		this.user = user;
		mbar=new JMenuBar();
		
		home=new JMenuItem("Home");
		
		portfolio=new JMenuItem("Portfolio");
		
		mywatchlist=new JMenuItem("My Watchlist");
		
		help=new JMenuItem("Help");
		
		logout=new JMenuItem("Logout");
		
		trend=new JMenuItem("Trend");
		
		mbar.add(home);
		
		mbar.add(portfolio);
		
		mbar.add(help);
		
		mbar.add(logout);
		
		setFont();
		
		frm.frm.setJMenuBar(mbar);
		
		frm.frm.setVisible(true);
		
		
		home.addActionListener(this);
		
		portfolio.addActionListener(this);
		
		help.addActionListener(this);
		
		logout.addActionListener(this);
	}
	public void setFont(){
		home.setFont(new Font("Serif", Font.PLAIN, 24));
		
		portfolio.setFont(new Font("Serif", Font.PLAIN, 24));
		
		help.setFont(new Font("Serif", Font.PLAIN, 24));
		
		logout.setFont(new Font("Serif", Font.PLAIN, 24));
		
	}
	
	public void actionPerformed(ActionEvent ae) {
		
		if(ae.getSource()==home){
			System.out.println("home");
			frm.remove();
			hme = new Home(frm,mainWindow,user);
			frm.addPanel(hme.createPanel());
		}
		
		if(ae.getSource()==portfolio){
			user = dbobj.getInfo();
			System.out.println("portfolio");
			int count=user.getcount();
			String[] abc = user.getstkname();
			System.out.println("count : "+count);
			System.out.println("Name\taddr\tdate\tphone\temail\tpwd\tpcard\tstkname\tnos\tbal");
			for(int i=0;i<count;i++){
				System.out.print(user.getname());
				System.out.print("\t"+user.getaddr());
				System.out.print("\t"+user.getdate());
				System.out.print("\t"+user.getphone());
				System.out.print("\t"+user.getemail());
				System.out.print("\t"+user.getpwd());
				System.out.print("\t"+user.getpcard());
				System.out.print("\t"+ abc[i]);
				System.out.print("\t"+user.getnos(abc[i]));
				System.out.println("\t"+user.getbal());
			}
			Object ob[][]=new Object[count][4];
			for(int i=0;i<count;i++){
				Stock stk = StockFetcher.getStock(abc[i]);
				ob[i][0]=abc[i];
				ob[i][1]=user.getnos(abc[i]);
				ob[i][2]=stk.getPrice();
				ob[i][3]="Sell "+abc[i];
			}
			String colHead[]={"Name","No of Shares","Price","Sell"};
			JTable jt=new JTable(ob,colHead);
			jt.getColumn("Sell").setCellRenderer(new ButtonRenderer());
		    jt.getColumn("Sell").setCellEditor(new ButtonEditor(new JCheckBox(), user));
			jsp=new JScrollPane(jt);
			jt.setPreferredScrollableViewportSize(jt.getPreferredSize());
			jt.setFillsViewportHeight(true);
			jt.getTableHeader().setFont(new Font("Serif", Font.PLAIN, 24));
			jt.setRowHeight(24);
			jt.setFont(new Font("Serif", Font.PLAIN, 18));
			JPanel p1 = new JPanel();
			JPanel p2 = new JPanel();
			JPanel p = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			JLabel label = new JLabel("Name : " + user.getname());
			label.setFont(new Font("Serif", Font.PLAIN, 30));
			gbc.anchor = GridBagConstraints.NORTHWEST;
			p1.add(label,gbc);
			p2.setLayout(new GridLayout());
			p2.setPreferredSize(new Dimension(650, 450));
			p1.setOpaque(false);
			p2.setOpaque(false);
			p.setOpaque(false);
			p2.add(jsp);
			gbc.gridx=0;
			gbc.gridy=0;
			p.add(p1, gbc);
			gbc.gridx=0;
			gbc.gridy=1;
			p.add(p2,gbc);
			frm.remove();
			frm.addPanel(p);
		}
		if(ae.getSource()==help){
			System.out.println("help");
			JPanel p = new JPanel(new GridBagLayout());
			JPanel panel = new JPanel(null);
			JLabel l1 = new JLabel("Stock Market");
			JLabel l2 = new JLabel("<html><body style='width: 800px'>A stock market, equity market or share market is the aggregation of"
					+ " buyers and sellers (a loose network of economic transactions, not a physical "
					+ "facility or discrete entity) of stocks (also called shares), which represent "
					+ "ownership claims on businesses; these may include securities listed on a public "
					+ "stock exchange as well as those only traded privately. Examples of the "
					+ "latter include shares of private companies which are sold to investors "
					+ "through equity crowdfunding platforms. Stock exchanges list shares of common equity"
					+ " as well as other security types, e.g. corporate bonds and convertible bonds</html>");
			JLabel l3 = new JLabel("Size of the market");
			JLabel l4 = new JLabel("<html><body style='width: 800px'>Stocks can be categorised in various ways. One way is by the country where the company is domiciled. "
					+ "For example, Nestlé and Novartis are domiciled in Switzerland, so they may be considered as part of the Swiss "
					+ "stock market, although their stock may also be traded on exchanges in other countries, for "
					+ "example, as American Depository Receipts (ADRs) on U.S. stock markets.");
			JLabel l5 = new JLabel("Stock exchange");
			JLabel l6 = new JLabel("<html><body style='width: 800px'>Stock exchange is a place where, or an organization through which, "
					+ "individuals and organisations can trade stocks. Many large companies have their "
					+ "stock listed on a stock exchange. This makes the stock more liquid and thus "
					+ "more attractive to many investors. It may also act as a guarantor of settlement. "
					+ "Other stocks may be traded 'over the counter' (OTC), that is, through a dealer. So"
					+ "me large companies will have their stock listed on more than one exchange in "
					+ "different countries, so as to attract international investors.");
			JLabel l7 = new JLabel("Trade");
			JLabel l8 = new JLabel("<html><body style='width: 800px'>Trade in stock markets means the transfer"
					+ " for money of a stock or security from a seller to a buyer. This requires these "
					+ "two parties to agree on a price. Equities (stocks or shares) confer an ownership "
					+ "interest in a particular company.Participants in the stock market range from "
					+ "small individual stock investors to larger trader investors, who can be based "
					+ "anywhere in the world, and may include banks, insurance companies, pension funds "
					+ "and hedge funds. Their buy or sell orders may be executed on their behalf by a "
					+ "stock exchange trader.");
			JLabel l9 = new JLabel("Crashes");
			JLabel l10 = new JLabel("<html><body style='width: 800px'>A stock market crash is often defined"
					+ " as a sharp dip in share prices of stocks listed on the stock exchanges. "
					+ "In parallel with various economic factors, a reason for stock market crashes "
					+ "is also due to panic and investing public's loss of confidence. Often, stock "
					+ "market crashes end speculative economic bubbles.");
			JLabel l11 = new JLabel("For more Details visit following websites"); 
			JLabel l12 = new JLabel("www.investopedia.com");
			JLabel l13 = new JLabel("www.bloomberg.com");
			JLabel l14 = new JLabel("www.moneycontrol.com");
			
			
			JLabel l15 = new JLabel("How to use Bull n Bear");
			l15.setFont(new Font("Serif", Font.BOLD, 24));
			JLabel l16 = new JLabel(">After login click on home to see prices of stock.In home screen you can buy a shares and also see historical data.");
			l16.setFont(new Font("Serif", Font.PLAIN, 22));
			JLabel l17 = new JLabel(">Click buy button to buy the shares");
			l17.setFont(new Font("Serif", Font.PLAIN, 22));
			JLabel l18 = new JLabel(">Enter number of shares in that small window.");
			l18.setFont(new Font("Serif", Font.PLAIN, 22));
			JLabel l19 = new JLabel(">In portfolio you can see the shares that you had already bought.");
			l19.setFont(new Font("Serif", Font.PLAIN, 22));
			JLabel l20 = new JLabel(">Click sell button to sell the shares.");
			l20.setFont(new Font("Serif", Font.PLAIN, 22));
			JLabel l21 = new JLabel(">By clicking Forgot Password you can change the password.");  
			l21.setFont(new Font("Serif", Font.PLAIN, 22));
			
			
			l1.setFont(new Font("Serif", Font.BOLD, 24));
			l2.setFont(new Font("Serif", Font.PLAIN, 20));
			l3.setFont(new Font("Serif", Font.BOLD, 24));
			l4.setFont(new Font("Serif", Font.PLAIN, 20));
			l5.setFont(new Font("Serif", Font.BOLD, 24));
			l6.setFont(new Font("Serif", Font.PLAIN, 20));
			l7.setFont(new Font("Serif", Font.BOLD, 24));
			l8.setFont(new Font("Serif", Font.PLAIN, 20));
			l9.setFont(new Font("Serif", Font.BOLD, 24));
			l10.setFont(new Font("Serif", Font.PLAIN, 20));
			l11.setFont(new Font("Serif", Font.PLAIN, 22));
			l12.setFont(new Font("Serif", Font.PLAIN, 20));
			l13.setFont(new Font("Serif", Font.PLAIN, 20));
			l14.setFont(new Font("Serif", Font.PLAIN, 20));
			GridBagConstraints gbc = new GridBagConstraints();
			
			gbc.insets = new Insets(10, 10, 10, 10);
			gbc.gridx=0;
			gbc.gridy=0;
			gbc.anchor=GridBagConstraints.WEST;
			p.add(l1,gbc);
			gbc.gridx=0;
			gbc.gridy=1;
			p.add(l2,gbc);
			
			gbc.gridx=0;
			gbc.gridy=2;
			p.add(l2,gbc);
			
			gbc.gridx=0;
			gbc.gridy=3;
			p.add(l3,gbc);
			
			gbc.gridx=0;
			gbc.gridy=4;
			p.add(l4,gbc);
			
			gbc.gridx=0;
			gbc.gridy=5;
			p.add(l5,gbc);
			
			gbc.gridx=0;
			gbc.gridy=6;
			p.add(l6,gbc);
			
			gbc.gridx=0;
			gbc.gridy=7;
			p.add(l7,gbc);
			
			gbc.gridx=0;
			gbc.gridy=8;
			p.add(l8,gbc);
			
			gbc.gridx=0;
			gbc.gridy=9;
			p.add(l9,gbc);
			
			gbc.gridx=0;
			gbc.gridy=10;
			p.add(l10,gbc);
			
			
			gbc.gridx=0;
			gbc.gridy=11;
			p.add(l11,gbc);
			
			gbc.gridx=0;
			gbc.gridy=12;
			p.add(l12,gbc);
			
			gbc.gridx=0;
			gbc.gridy=13;
			p.add(l13,gbc);
			
			gbc.gridx=0;
			gbc.gridy=14;
			p.add(l14,gbc);
			
			gbc.gridx=0;
			gbc.gridy=15;
			p.add(l15,gbc);
			
			gbc.gridx=0;
			gbc.gridy=16;
			p.add(l16,gbc);
			
			gbc.gridx=0;
			gbc.gridy=17;
			p.add(l17,gbc);
			
			gbc.gridx=0;
			gbc.gridy=18;
			p.add(l18,gbc);
			
			gbc.gridx=0;
			gbc.gridy=19;
			p.add(l19,gbc);
			
			gbc.gridx=0;
			gbc.gridy=120;
			p.add(l20,gbc);
			
			gbc.gridx=0;
			gbc.gridy=21;
			p.add(l21,gbc);
			
			/*
			JScrollPane jsp = new JScrollPane(p);
			panel.setPreferredSize(new Dimension(1650, 950));
			jsp.setBounds(50,30,500,500);
			jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	        gbc.anchor=GridBagConstraints.NORTHWEST;
			panel.add(jsp);
			frm.remove();
			frm.addPanel(panel);
			*/
			JScrollPane scrollPane = new JScrollPane(p);
	        //scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	        scrollPane.setBounds(50, 30, 1600, 900);
	        panel.setPreferredSize(new Dimension(1650, 950));
	        p.setOpaque(false);
	        panel.setOpaque(false);
	        scrollPane.getViewport().setOpaque(false);
	        scrollPane.setOpaque(false);
	        panel.add(scrollPane);
	        frm.remove();
	        frm.addPanel(panel);
		}
		
		if(ae.getSource()==logout){
			System.out.println("logout");
			frm.remove();
			frm.frm.setJMenuBar(null);
			frm.addPanel(mainWindow.panel);
		}
	}
	
}
