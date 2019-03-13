import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.GregorianCalendar;

import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

public class ButtonEditor extends DefaultCellEditor {
	  protected JButton button;
	  private String    label;
	  private boolean   isPushed;
	  User user;
	  public ButtonEditor(JCheckBox jCheckBox,User user) {
	    super(jCheckBox);
	    this.user = user;
	    button = new JButton();
	    button.setOpaque(true);
	    button.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        fireEditingStopped();
	      }
	    });
	  }
	  
	  public Component getTableCellEditorComponent(JTable table, Object value,
	                   boolean isSelected, int row, int column) {
	    if (isSelected) {
	      button.setForeground(table.getSelectionForeground());
	      button.setBackground(table.getSelectionBackground());
	    } else{
	      button.setForeground(table.getForeground());
	      button.setBackground(table.getBackground());
	    }
	    label = (value ==null) ? "" : value.toString();
	    button.setText( label );
	    isPushed = true;
	    return button;
	  }
	  
	  public Object getCellEditorValue() {
	    if (isPushed)  {
	      if(label.regionMatches(0,"Buy",0,3)){
	    		System.out.println("matched");
	    		showPan(label.substring(4));
	    		System.out.println(label.substring(4));
	    		
	      }
	      if(label.regionMatches(0,"Sell",0,3)){
	    		System.out.println("matched");
	    		showPan1(label.substring(5));
	    		System.out.println(label.substring(5));
	    		
	      }
	      //JOptionPane.showMessageDialog(button ,label + ": Ouch!");
	      //System.out.println(label + "pressed");
	      else{
	    	  Historical hist = new Historical(label,new GregorianCalendar(2017,1,12),new GregorianCalendar(),user);
	      }
	    }
	    isPushed = false;
	    return new String( label ) ;
	  }
	    
	  private void showPan1(String stk) {
		
		  JTextField nos = new JTextField(5);
		  System.out.println(stk);
		  Stock stock = StockFetcher.getStock(stk);
		  int price = (int) stock.getPrice();
	      JPanel myPanel = new JPanel();
	      myPanel.add(new JLabel("how many shares"));
	      myPanel.add(nos);
	      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
	      int result = JOptionPane.showConfirmDialog(null, myPanel, 
	               "Sell Stock", JOptionPane.OK_CANCEL_OPTION);
	      if (result == JOptionPane.OK_OPTION) {
	    	 int noOfShare = Integer.parseInt(nos.getText());
	    	 System.out.println("Stock price : "+price);
	    	 System.out.println("User bal : "+user.getbal());
	    	 System.out.println("no of stock request : "+nos.getText());
	    	 
	    	 if(user.getbal()>price*noOfShare & user.getbal()>10000){
	    		 JPanel myPanel1 = new JPanel();
		         myPanel1.add(new JLabel("Total : " + price*noOfShare));
		         int res = JOptionPane.showConfirmDialog(null, myPanel1, 
			               "Sell Stock", JOptionPane.OK_CANCEL_OPTION);
		         if(res == JOptionPane.OK_OPTION){
		        	 int nshare = user.getnos(stk);
		        	 System.out.println("number : "+nshare);
		        	 new DB().updateStock(user.getadhr(),stk,nshare-noOfShare,(user.getbal()+price*noOfShare));
		         }
	    	 }
	    	 else{
	    		 System.out.println("less");
	    	 }/*
	         JPanel myPanel1 = new JPanel();
	         myPanel1.add(new JLabel("Total : " + ))
	    	  nos.getText());*/
	      }
		
	}


	private void showPan(String stk) {
		  JTextField nos = new JTextField(5);
		  Stock stock = StockFetcher.getStock(stk);
		  int price = (int) stock.getPrice();
	      JPanel myPanel = new JPanel();
	      myPanel.add(new JLabel("how many shares"));
	      myPanel.add(nos);
	      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
	      int result = JOptionPane.showConfirmDialog(null, myPanel, 
	               "Buy Stock", JOptionPane.OK_CANCEL_OPTION);
	      if (result == JOptionPane.OK_OPTION) {
	    	 int noOfShare = Integer.parseInt(nos.getText());
	    	 System.out.println("Stock price : "+price);
	    	 System.out.println("User bal : "+user.getbal());
	    	 System.out.println("no of stock request : "+nos.getText());
	    	 
	    	 if(user.getbal()>price*noOfShare & user.getbal()>10000){
	    		 JPanel myPanel1 = new JPanel();
		         myPanel1.add(new JLabel("Total : " + price*noOfShare));
		         int res = JOptionPane.showConfirmDialog(null, myPanel1, 
			               "Please Enter X and Y Values", JOptionPane.OK_CANCEL_OPTION);
		         if(res == JOptionPane.OK_OPTION){
		        	 int nshare = user.getnos(stk);
		        	 System.out.println("number : "+nshare);
		        	 new DB().updateStock(user.getadhr(),stk,nshare+noOfShare,(user.getbal()-price*noOfShare));
		         }
	    	 }
	    	 else{
	    		 System.out.println("less");
	    	 }/*
	         JPanel myPanel1 = new JPanel();
	         myPanel1.add(new JLabel("Total : " + ))
	    	  nos.getText());*/
	      }
		
	}

	public boolean stopCellEditing() {
	    isPushed = false;
	    return super.stopCellEditing();
	  }
	  
	  protected void fireEditingStopped() {
	    super.fireEditingStopped();
	  }
	}