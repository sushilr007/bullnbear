import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Home {

	JPanel panel;
	JScrollPane jsp;
	Frame frm;
	MainWindow mainWindow;
	User user;
	Home(Frame frm, MainWindow mainWindow,User user){
		this.user = user;
		this.frm = frm;
		this.mainWindow = mainWindow;
		panel = new JPanel();
		Object ob[][]=new Object[14][11];
		String[] stkname = {"AAPL","KO","FB","TWTR","HPQ","ABB","ABEV","ACN","AKP","CAT","CSC","RBS","RHT","RACE"};
		Stock stk;
		for(int i=0;i<14;i++){
			stk=StockFetcher.getStock(stkname[i]);
			ob[i][0]=stk.getName();
			ob[i][1]=stk.getPrice();
			ob[i][2]=stk.getOpen();
			ob[i][3]=stk.getVolume();
			ob[i][4]=stk.getEps();
			ob[i][5]=stk.getWeek52low();
			ob[i][6]=stk.getWeek52high();
			ob[i][7]=stk.getDaylow();
			ob[i][8]=stk.getDayhigh();
			ob[i][9]=stkname[i];
			ob[i][10]="Buy "+stkname[i];
		}
		String colHead[]={"Name","Price","Open for Day","Volume","EPS","Year Low","Year High","Day Low","Day High","Historical Data","Buy"};
		JTable jt=new JTable(ob,colHead);
		jt.getColumnModel().getColumn(0).setPreferredWidth(100);
		jt.getColumnModel().getColumn(1).setPreferredWidth(100);
		jt.getColumnModel().getColumn(2).setPreferredWidth(100);
		jt.getColumnModel().getColumn(3).setPreferredWidth(100);
		jt.getColumnModel().getColumn(4).setPreferredWidth(100);
		jt.getColumnModel().getColumn(5).setPreferredWidth(100);
		jt.getColumnModel().getColumn(6).setPreferredWidth(100);
		jt.getColumnModel().getColumn(7).setPreferredWidth(100);
		jt.getColumnModel().getColumn(8).setPreferredWidth(100);
		jt.getColumnModel().getColumn(9).setPreferredWidth(100);

		jt.getColumn("Buy").setCellRenderer(new ButtonRenderer());
	    jt.getColumn("Buy").setCellEditor(new ButtonEditor(new JCheckBox(), user));

	    
	    jt.getColumn("Historical Data").setCellRenderer(new ButtonRenderer());
	    jt.getColumn("Historical Data").setCellEditor(new ButtonEditor(new JCheckBox(), user));
	    
		jsp=new JScrollPane(jt);
		jt.setPreferredScrollableViewportSize(jt.getPreferredSize());
		jt.setFillsViewportHeight(true);
		jt.getTableHeader().setFont(new Font("Serif", Font.PLAIN, 24));
		jt.setRowHeight(24);
		jt.setFont(new Font("Serif", Font.PLAIN, 18));
		
	
	}
	public JPanel createPanel(){
		panel.setLayout(new GridLayout());
		panel.setPreferredSize(new Dimension(1650, 950));
		panel.setOpaque(false);
		panel.add(jsp);
		return panel;
	}
	
}
