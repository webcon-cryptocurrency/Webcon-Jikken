package jikken;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataB {
	static Connection conn;
	static Statement stmt;
	static ResultSet rs;
	static int ID;

	public static void connect(){
		try {
			Class.forName("com.mysql.jdbc.Driver");

			conn=  DriverManager.getConnection("hogehoge");//sqlに接続
	        stmt = conn.createStatement();
	        }
			catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}

	}

	public void C(){
		try {
			Class.forName("com.mysql.jdbc.Driver");

			conn=  DriverManager.getConnection("hogehoge");//sqlに接続
	        stmt = conn.createStatement();
	        }
			catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}

	}



	public static void DeleteDB(){
		try {

			stmt.execute("delete from Miner");
			stmt.execute("delete from Block");
			stmt.execute("delete from log");
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			stmt.execute( "insert into Miner values (0,0,'"+hostAddress+"')" );
			stmt.execute( "insert into Block values (1,1,'firstblock')" );
		} catch (SQLException| UnknownHostException e) {

		}

	}
public void closeSQL(){
	try {
		conn.close();
	} catch (SQLException e) {
		e.printStackTrace();
	}
}
public static int Adminer(String IP,int belong) {
		try {
			ID=-2;

			rs = stmt.executeQuery("select max(ID) as maxID from Miner");
			while(rs.next()) {
			ID=rs.getInt("maxID")+1;
			}
			stmt.execute( "insert into Miner values ("+ID+","+belong+",'"+IP+"')" );

		} catch (SQLException  e) {
			e.printStackTrace();
		}
		return ID;
	}

public static int chain(int Height,int belong,String nonce) throws SQLException {
/*
 * 書き込んだあと、(0)善性ブロックによる書き込みによって、すでに起きていた分岐が無効になる場合。
 * (1)善性ブロックが分岐が起きているところに書き込み、分岐が続く場合。もしくは新しく分岐ができる場合。
 * (2)善性ブロックの書き込みで、分岐は起きない場合。
 * (3)悪性ブロックによる書き込みによって、すでに起きていた分岐が無効になる場合。
 * (4)悪性ブロックが分岐が起きているところに書き込み、分岐が続く場合。もしくは新しく分岐ができる場合。
 * (5)悪性ブロックの書き込みで、分岐は起きない場合。
 * の６つに場合を分けたものをreturnし、その後マイナーへ送る指示を変える。
 * ただし、実際は1,4,その他の三通りでよかった。
 * */


	stmt.execute( "insert into Block values ("+Height+","+belong+",'"+nonce+"')" );
	boolean opposite;
	int HosHeight=0;
	int GoodHeight=0;
	if(belong==1){
		GoodHeight=Height;
		rs = stmt.executeQuery("select Height from Block where belong=2");
		opposite=rs.next();
		//悪者ブロックの有無を確認
		if(opposite){
		rs = stmt.executeQuery("select max(Height) as maxID from Block where belong=2");
		while(rs.next()) {
			HosHeight=rs.getInt("maxID");
			}
		if(GoodHeight-HosHeight>4){
			int Hmin;
			int Hmax;
			rs = stmt.executeQuery("select min(Height) as minID,max(Height) as maxID from Block where belong=2");
			if(rs.next()){
				Hmin=rs.getInt("minID");
				Hmax=rs.getInt("maxID");
				stmt.execute( "insert into log values ("+Hmin+","+(Hmax-Hmin+1)+")");
			}
			stmt.execute("delete from Block where belong=2");

			return 0;
		}

		return 1;
		}

		return 2;

	}else{
	HosHeight=Height;
	rs = stmt.executeQuery("select Height from Block where belong=1");
	opposite=rs.next();
	if(opposite){
	rs = stmt.executeQuery("select max(Height) as maxID from Block where belong=1");
	while(rs.next()) {
		GoodHeight=rs.getInt("maxID");
		}
	if(HosHeight-GoodHeight>4){
		int Gmin;
		int Gmax;
		rs = stmt.executeQuery("select min(Height) as minID,max(Height) as maxID from Block where belong=1");
		if(rs.next()){
			Gmin=rs.getInt("minID");
			Gmax=rs.getInt("maxID");
			stmt.execute( "insert into log values ("+Gmin+","+(Gmax-Gmin+1)+")");
		}
		stmt.execute("delete from Block where belong=1");

		return 3;
	}
	//悪者だけに通知
	return 4;
	}
	}

	return 5;
}
public static String[] IPs (){
	int Howmany=0;
	String[] a;
	try {
		rs = stmt.executeQuery("select max(ID) as maxID from Miner");
		while(rs.next()) {
		Howmany=rs.getInt("maxID");
		}
		a=new String[Howmany+1];
		for (int i=0;i<Howmany+1;i++){
		a[i]="";
		}
		rs = stmt.executeQuery("select * from Miner");
		while(rs.next()) {
			a[rs.getInt("ID")]=rs.getString("IP");
			}
			a[0]="";
		return a;
	} catch (SQLException  e) {
		e.printStackTrace();
	}
	return null;
}
public static  Nods[] GoodIPs (){
	try {
		Nods[] Good;
		rs = stmt.executeQuery("select count(*) as maxID from Miner where belong=1");
		if(rs.next()) {
		Good = new Nods[rs.getInt("maxID")];
		}else{
		Good = new Nods[0];
		}
	rs = stmt.executeQuery("select * from Miner where belong=1");
	int i=0;
	while(rs.next()) {
		Good[i]=new Nods();
		Good[i].setnods(rs.getInt("ID"), rs.getString("IP"));
		i++;
	}
	return Good;


	} catch ( SQLException e) {
	}
	return null;
}
public static  Nods[] HostileIPs (){
	try {
		Nods[] Hostile;
		rs = stmt.executeQuery("select count(*) as maxID from Miner where belong=2");
		if(rs.next()) {
		Hostile = new Nods[rs.getInt("maxID")];
		}else{
		Hostile = new Nods[0];
		}
	rs = stmt.executeQuery("select * from Miner where belong=2");
	int i=0;
	while(rs.next()) {
		Hostile[i]=new Nods();
		Hostile[i].setnods(rs.getInt("ID"), rs.getString("IP"));
		i++;
	}
	return Hostile;
	} catch (SQLException e) {
	}
	return null;
}
public static void Delminer(int ID) {
	try {

	stmt.execute("delete from Miner where ID="+ID);

	} catch (SQLException e) {
		e.printStackTrace();
	}
}


public static int HMM(){
	//接続しているマイナーの数を取得
	int a=0;
	try {

		rs = stmt.executeQuery("select max(ID) as maxID from Miner");
		while(rs.next()) {
		a=rs.getInt("maxID");
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return a;
}

public void export(){

}
}
class Nods{
int ID;
String IP;
public void setnods(int Id,String Ip){
	ID=Id;
	IP=Ip;
}
}