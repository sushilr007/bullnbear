import java.awt.Dimension;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;

public class Historical{

	public static final int DATE = 0;
	public static final int OPEN = 0;
	public static final int HIGH = 0;
	public static final int LOW = 0;
	public static final int CLOSE = 0;
	public static final int VOLUME = 0;
	public static final int ADJCLOSE = 0;
		
	User user;
	public Historical(String symbol, GregorianCalendar start, GregorianCalendar end, User user) {
		this.user=user;
		//http://chart.finance.yahoo.com/table.csv?s=KO&a=2&b=12&c=2017&d=3&e=12&f=2017&g=d&ignore=.csv
		String url = "http://chart.finance.yahoo.com/table.csv?s="+symbol+
					"&a="+start.get(Calendar.MONTH)+
					"&b="+start.get(Calendar.DAY_OF_MONTH)+
					"&c="+start.get(Calendar.YEAR)+
					"&d="+end.get(Calendar.MONTH)+
					"&e="+end.get(Calendar.DAY_OF_MONTH)+
					"&f="+end.get(Calendar.YEAR)+
					"&g=d&ignore=.csv";
		try{
			URL yhoofin = new URL(url);
			URLConnection data = yhoofin.openConnection();
			Scanner input = new Scanner(data.getInputStream());
			if(input.hasNext()){
				input.nextLine();
			}
			int i=0;
			Object ob[][]=new Object[65][7];
			while(input.hasNextLine()){
				String line = input.nextLine();
				System.out.println(line);
				String[] stockinfo = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
					ob[i][0]=stockinfo[0];
					ob[i][1]=stockinfo[1];
					ob[i][2]=stockinfo[2];
					ob[i][3]=stockinfo[3];
					ob[i][4]=stockinfo[4];
					ob[i][5]=stockinfo[5];
					ob[i][6]=stockinfo[6];
					i++;
			}
			String colHead[]={"Date","Open","High","Low","Close","Volume","Adj Close"};
			JTable jt=new JTable(ob,colHead);
			UIManager.put("OptionPane.minimumSize",new Dimension(900,900)); 
			JOptionPane.showMessageDialog(null,new JScrollPane(jt));
		}
		catch(Exception e){
			
		}
		
	}
}
	

