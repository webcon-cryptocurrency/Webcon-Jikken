package jikken;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {

 /**
  * @param args
  *
  */
	static //host,belong
	int ID=-1;
	static Clientserver server;
 public static void main(String[] args) throws UnknownHostException {
  server = new Clientserver();
  String host ="****************";
  int belong = Integer.parseInt("1");//善性は1、敵性は2、最初はCUIで呼び出す際に変数を与えていたが、自動化するために最初から数字を入れるようにした。
  int port = 50000;

  try {
   Registry registry = LocateRegistry.getRegistry(host, port);
   Block stub = (Block) registry.lookup("Block");
   String MyIP = InetAddress.getLocalHost().getHostAddress();
   ID = stub.Miner(MyIP,belong);
   if(ID<0){
	   System.out.println("error!");
	   System.exit(1);
   }
   System.out.println(ID);
   //与えられたIDからポートを開いて、成功したらTATETAを呼び出す。
   boolean TATTA=server.Stand(ID,belong,host);
   if(TATTA){

   }else{
	 stub.TATETA(ID, MyIP);
	 System.out.println("building Clientserver was fault");
   }

  } catch (Exception e) {
   e.printStackTrace();

  }
 }
}