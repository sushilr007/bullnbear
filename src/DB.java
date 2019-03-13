import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
class DB{
	int r;
	
	static final String DRIVER="org.postgresql.Driver";
	
	static final String URL="jdbc:postgresql://localhost:5432/stockdb";
	
	static final String USER="postgres";
	
	static final String PWD="1234";
	
	Connection con;

	boolean isTransSuccess=false;
	
	PreparedStatement ps1,ps2,ps3,ps4,ps5,ps6;
	
	ResultSet rs;
	
	Statement stmt;
	
	String user;
	DB(){
		
		try{
			
			Class.forName(DRIVER);
			
			con=DriverManager.getConnection(URL,USER,PWD);
			
			System.out.println("Connected");
			
			ps1 = con.prepareStatement("insert into demo values(?,?,?,?,?,?,?,?)");
			
			ps2 = con.prepareStatement("select * from demo where email=?");
			
			ps3 = con.prepareStatement("select * from demo d natural join stock s natural join balance b where email=? and d.adhr=s.adhr and d.adhr=b.adhr");
			
			ps4 = con.prepareStatement("update stock set nos=? where adhr=? and stock_name=?");
			
			ps5 = con.prepareStatement("insert into stock values(?,?,?)");
			
			ps6 = con.prepareStatement("update balance set bal=? where adhr=?");
			 
			stmt=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
	
		}catch(Exception e){
			
			System.out.println(e);
		
		}
	}
	public int createUser(String name,String addr,Date date,String phone,String email,String pwd,String pcard,String adhar){
		
		try{
			ps1.setString(1,name);
					
		ps1.setString(2,addr);
		
		ps1.setDate(3,date);
					
		ps1.setLong(4,Long.parseLong(phone));
					
		ps1.setString(5,email);
				
		ps1.setString(6,pwd);
					
		ps1.setString(7,pcard);
					
		ps1.setInt(8,Integer.parseInt(adhar));
		
		r= ps1.executeUpdate();
		}catch(Exception e){
					
					e.printStackTrace();
					
					JOptionPane.showMessageDialog(null,e);
				
				}
		return r;
	}
	public int validate(String usr,String pwd){
		user = usr;
		try{
			ps2.setString(1,user);
			ResultSet result=ps2.executeQuery();
			if(result.next())
			{
				if(result.getString("email").equals(usr))
					if(result.getString("password").equals(pwd)){
						System.out.println("in");
						return 1;
					}
				else{
					System.out.println("Incorrect Password or user name");
					JOptionPane.showMessageDialog(null,"Incorrect Password");
					return 0;
				}
			}
			else{
				JOptionPane.showMessageDialog(null,"Enter username and password");
				System.out.println("no record");
				return 0;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return 0;
		
	}
	public User getInfo() {
		int adhr = 0;
		String name = "";
		String addr = "";
		Date bdate = null;
		Long phone = (long) 0;
		String email = "";
		String pwd = "";
		String pcard = "";
		int count=0;
		try{
			ps3.setString(1,user);
			ResultSet result=ps3.executeQuery();
			while(result.next())
				count++;
		}catch(Exception e){
			
		}
		
		String[] stkname = new String[count] ;
		//int nos[] = new int[20];
		Map<String, Integer> nos = new HashMap<String, Integer>();
		int bal = 0;
		int i=0;
		System.out.println("user : "+user);
		try{
			//ps3 = con.prepareStatement("select * from demo d natural join stock s natural join balance b where email=? and d.adhr=s.adhr and d.adhr=b.adhr");
			ps3.setString(1,user);
			ResultSet result=ps3.executeQuery();
			while(result.next())
			{
				adhr = result.getInt("adhr");
				name = result.getString("name");
				addr = result.getString("addr");
				bdate = result.getDate("bdate");
				phone = result.getLong("phone");
				email = result.getString("email");
				pwd = result.getString("password");
				pcard = result.getString("pan");
				stkname[i] = result.getString("stock_name");
				nos.put(stkname[i],result.getInt("nos"));
				//nos[] = result.getInt("nos");
				bal = result.getInt("bal");
				i++;
			}
		}
		catch(Exception e){
			//JOptionPane.showMessageDialog(null,"not able to fetch user info");
			System.out.println(e);
		}
		return new User(adhr,name,addr,bdate,phone,email,pwd,pcard,stkname,nos,bal);
	}
	
	public static String getPwd(String email){
		String pwd = "";
		try {
			Class.forName(DRIVER);
			Connection con=DriverManager.getConnection(URL,USER,PWD);
			Statement stmt=con.createStatement();
			PreparedStatement ps = con.prepareStatement("select password from demo where email=?");
			ps.setString(1, email);
			ResultSet rest=ps.executeQuery();
			
			if(rest.next())
			{
				pwd = rest.getString("password");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pwd;
		
	}
	
	public void updateStock(int getadhr, String stk, int nos, int bal) {
		
		//ps4 = con.prepareStatement("update stock s set nos=? where adhr=? and stock_name=?");
		try{
			Statement stmt=con.createStatement();
			String query="select * from stock";
			ResultSet rest=stmt.executeQuery(query);
			boolean found=false;
			con.setAutoCommit(false);
			while(rest.next())
			{
				if(rest.getString("stock_name").equals(stk))
					found=true;
			}
			if(found){
				ps4.setInt(1, nos);
				ps4.setInt(2, getadhr);
				ps4.setString(3, stk);
				int rs=ps4.executeUpdate();
				if(rs>0)
					System.out.println("Modified......!");
				else
					System.out.println("Failed.......!");
			}
			else{
				//ps5 = con.prepareStatement("insert into stock values(?,?,?)");
				ps5.setInt(1,getadhr);
				ps5.setString(2, stk);
				ps5.setInt(3, nos);
				r= ps5.executeUpdate();
			}
			System.out.println(bal);
			//ps6 = con.prepareStatement("updae balance bal=? where adhr=?");
			ps6.setInt(1, bal);
			ps6.setInt(2, getadhr);
			int rs=ps6.executeUpdate();
			if(rs>0)
				System.out.println("Modified balance......!");
			else
				System.out.println("Failed.......!");
			isTransSuccess=true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		
		finally
		{
			try
			{
				if(con!=null)
				{
					if(isTransSuccess==true)
					{
						con.commit();
						System.out.println("Commited");
					}
					else
					{
						con.rollback();
						System.out.println("rollback");
					}
					con.close();
				}
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		
		}
	
	}
}