package jikken;
import java.net.InetAddress;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

public class Hostserver  implements Block {
//観測者

/**
  * @param args
  */
  //

static DataB host = new DataB();


public static void main(String[] args) {
//データを集めて記録するためのホストを開く
  Hostserver server = new Hostserver();
  try {

   String hostAddress = InetAddress.getLocalHost().getHostAddress();
   System.out.println("Host Address(A)  : " + hostAddress);
   //RMIを受けるサーバーを開く
	   server.createAndBindRegistry("Block",server,50000);
	   DataB.connect();
		DataB.DeleteDB();
		boolean Start=false;
		while(!Start){
		//毎秒ごとにマイナーが規定の人数いるかを確認
		Start=(DataB.HMM()>=11);
		Thread.sleep(10000);
		}
		beginmine();

  } catch (RemoteException e) {
   e.printStackTrace();
  } catch (AlreadyBoundException e) {
   e.printStackTrace();
  } catch (Exception e) {
   e.printStackTrace();
  }
 }




 public void createAndBindRegistry(String refName, Remote r, int port)
   throws RemoteException, AlreadyBoundException {


  // export remote method stub
  Remote stub = UnicastRemoteObject.exportObject(r, 0);


  // get RMI registry on port 1099
  Registry registry = LocateRegistry.createRegistry(port);

  System.out.println("Binding registry...");
  // bind stub to registry
  registry.bind(refName, stub);
 }

 static String[] MinerIPs;//マイナーのIP全部を格納
 String[] GoodIPs;//善性のみ格納
 String[]HostileIPs;//悪性のみ格納


 public static void beginmine(){
//マイナー達に開始を通知
 MinerIPs=DataB.IPs();
 for(int j=0;j<MinerIPs.length;j++){
if(MinerIPs[j].isEmpty()){
}
else{
	Registry registry;
	try {
		registry = LocateRegistry.getRegistry(MinerIPs[j], 50000+j);
		Miners stub = (Miners) registry.lookup("Miners");
		stub.Start(2);
	} catch (RemoteException | NotBoundException e) {
		e.printStackTrace();
	}

}
 }

 }

 @Override
	public int Miner(String IP, int belong) throws RemoteException {
	 //採掘者をDBに登録
	 int ID=DataB.Adminer(IP,belong);
	 System.out.println(ID);
	 return ID;
	}







@Override
public boolean TATETA(int ID,String IP) throws RemoteException {
	if(ID<0){
	DataB.Delminer(-ID);
	}
	return false;
}



@Override
public void hashed(int Height,int belong,String nonce) throws RemoteException, SQLException {
	//採掘者が掘ったらこれを呼び出してブロックをDBに書き込むみ、次に掘るブロックの支指示を与える。
int A=DataB.chain(Height,belong,nonce);
Registry registry;
if(Height>=100){

	 for(int j=0;j<MinerIPs.length;j++){

		 if(MinerIPs[j].isEmpty()){}
		 else{
		 	try {
		 		registry = LocateRegistry.getRegistry(MinerIPs[j], 50000+j);
		 		Miners stub = (Miners) registry.lookup("Miners");
		 		stub.exit();
		 	} catch (RemoteException | NotBoundException e) {
		 	}

		 }
		  }
System.exit(0);
}
else{
System.out.println(belong+"が"+Height+"番目のブロックを掘り出しました");

Nods[] Go=DataB.GoodIPs();
Nods[]Ho=DataB.HostileIPs();

if(A==1){
	//善良な相手だけに通知
int ID;
String IP;

for(int j=0;j<Go.length;j++){
	ID=Go[j].ID;
	IP=Go[j].IP;
		registry = LocateRegistry.getRegistry(IP, 50000+ID);
		Miners stub;
		try {
			stub = (Miners) registry.lookup("Miners");
			stub.Stophash();
			stub.Restart(Height+1);
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
}
}else if(A==4){
	//悪い相手だけに通知
	int ID;
	String IP;

	for(int k=0;k<Ho.length;k++){
		ID=Ho[k].ID;
		IP=Ho[k].IP;
			registry = LocateRegistry.getRegistry(IP, 50000+ID);
			Miners stub;
			try {
				stub = (Miners) registry.lookup("Miners");
				stub.Stophash();
				stub.Restart(Height+1);
			} catch (NotBoundException e) {
				e.printStackTrace();
			}

	}
}else{

	 for(int j=0;j<MinerIPs.length;j++){
		 if(MinerIPs[j].isEmpty()){}
		 else{
		 	try {
		 		registry = LocateRegistry.getRegistry(MinerIPs[j], 50000+j);
		 		Miners stub = (Miners) registry.lookup("Miners");
		 		stub.Stophash();
		 		stub.Restart(Height+1);
		 	} catch (RemoteException | NotBoundException e) {
		 		e.printStackTrace();
		 	}

		 }
		  }

}
}
}
}




