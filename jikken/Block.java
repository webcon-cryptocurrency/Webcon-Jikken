package jikken;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface Block extends Remote {
 public int Miner(String IP,int belong) throws RemoteException;
 public boolean TATETA(int ID,String IP)throws RemoteException;
 public void hashed(int Height, int belong, String nonce) throws RemoteException, SQLException;

}
