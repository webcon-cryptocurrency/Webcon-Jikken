
package jikken;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class Clientserver implements Miners {

 /**
  * @param args
  */
  //
 static int ID;
 static int Hei;

 public static HashG hashing;
 public static void main(String[] args){
 }

 public boolean Stand(int id,int belong,String host) {
  ID=id;
  hashing = new HashG();
  hashing.belong=belong;
  hashing.host=host;
  Clientserver server = new Clientserver();
  DataB.connect();
  try {
   // start RMI server
	   server.createAndBindRegistry("Miners",server,50000+ID);
	   return true;
  } catch (RemoteException e) {
   e.printStackTrace();
  } catch (AlreadyBoundException e) {
   e.printStackTrace();
  } catch (Exception e) {
   e.printStackTrace();
  }
  return false;
 }



 public void createAndBindRegistry(String refName, Remote r, int port)
   throws RemoteException, AlreadyBoundException {


  // export remote method stub
  Remote stub = UnicastRemoteObject.exportObject(r, 0);


  // get RMI registry on port 1099
  Registry registry = LocateRegistry.createRegistry(port);

  // bind stub to registry
  registry.bind(refName, stub);
 }



@Override

public void Start(int Height) throws RemoteException {
Hei=Height;
hashing.Height=Hei;
hashing.running=true;
hashing.start();
}


@Override
public void Stophash() throws RemoteException {
	 hashing.running=false;


}

@Override
public void Restart(int Height) throws RemoteException {
	Hei=Height;
	hashing.Height=Hei;
	hashing.running=true;
}

@Override
public void exit() throws RemoteException {
	System.exit(0);
}


}

class Sendhash extends Thread{
	int Height;
	int Belong;
	String Nonce;
	String Host;
	public void run(){
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(Host, 50000);
			Block stub = (Block) registry.lookup("Block");
			Thread.sleep(10);
			stub.hashed(Height, Belong, Nonce);
		} catch (RemoteException | NotBoundException | SQLException | InterruptedException e) {
			e.printStackTrace();
		}

}
	public void SetStates(int belong,int height,String host,String nonce){
		Belong=belong;
		Height=height;
		Host=host;
		Nonce=nonce;
	}
}



class HashG extends Thread {
	static String result;
	 int Height;
	 String HAHE;
	 String host;
	 int belong;
	 int ID;
	 boolean running=false;
	boolean mined=false;
	public void run() {
		String nonce="";
		String[] a;
		String hash;
		while(true){
		System.out.println("YEAH!");
		try {
			HAHE=sha256d(""+Height);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		while(running){
			//runningがtrueの時だけ実行、falseなら抜けて終わる。
		try {
			 a =mining(HAHE);
			nonce=a[0];
			hash=a[1];
			if(hash.substring(0,6).equals("000000")){
				mined=true;
			   running=false;
	        }

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		}
		if(mined){
		Sendhash S = new Sendhash();
		S.SetStates(belong, Height, host, nonce);
		S.start();
		mined=false;
		}
		while(!running){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		}
	}
	public String[] mining(String H) throws NoSuchAlgorithmException{

		Random rnd = new Random();
		String source = H;
    	int n,m,l;
    	n= rnd.nextInt();
    	m= rnd.nextInt();
    	l= rnd.nextInt();
        source+=n+m+l;
        String Result=sha256d(source);
        String[] R= new String[2];
        R[0]=source;
        R[1]=Result;
		return R;



	}
    public String sha256d (String S) throws NoSuchAlgorithmException {
    	String source = S;
        byte[] once = MessageDigest.getInstance("SHA-256").digest(source.getBytes());
        byte[] bytes2 = MessageDigest.getInstance("SHA-256").digest(once);
        String result = DatatypeConverter.printHexBinary(bytes2);
        return result;
    }
}





