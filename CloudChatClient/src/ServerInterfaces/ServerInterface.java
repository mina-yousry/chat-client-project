/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import messagepkg.Message;

/**
 * @author AMUN
 * Description ServerInterface is where to put all methods clients may use as a service
 */
public interface ServerInterface extends Remote{
    public void sendToAll(Message msg)throws RemoteException;
  //  public UserBean register(int userID,ClientInterface client) throws RemoteException;
   // public boolean unRegister(String user,ClientInterface client) throws RemoteException;

    public boolean isRegistered(String userName) throws RemoteException;
    
}
