import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class User {
	private int adhr;
	private String name;
	private String addr;
	private Date bdate;
	private Long phone;
	private String email;
	private String pwd;
	private String pcard;
	private String[] stkname;
	private Map<String, Integer> nos = new HashMap<String, Integer>();
	private int bal;
	User(int adhr,String name,String addr,Date bdate,Long phone,String email,String pwd,String pcard,String[] stkname,Map<String, Integer> nos,int bal){
		this.adhr = adhr;
		this.name = name;
		this.addr = addr;
		this.bdate = bdate;
		this.phone = phone;
		this.email = email;
		this.pwd = pwd;
		this.pcard = pcard;
		this.stkname = stkname;
		this.nos = nos;
		this.bal = bal;
	}
	public int getadhr(){
		return adhr;
	}
	public String getname(){
		return name;
	}
	public String getaddr(){
		return addr;
	}
	public Date getdate(){
		return bdate;
	}
	public Long getphone(){
		return phone;
	}
	public String getemail(){
		return email;
	}
	public String getpwd(){
		return pwd;
	}
	public String getpcard(){
		return pcard;
	}
	public String[] getstkname(){
		return stkname;
	}
	public int getnos(String stk){
		if(nos.get(stk)==null)
			return 0;
		return nos.get(stk);
	}
	public int getbal(){
		return bal;
	}
	public int getcount(){
		return stkname.length;
	}
}
