package clientInterface;

import dtos.UserDto;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import messagepkg.CloudNotification;
import messagepkg.Message;

/**
 *
 * @author AMUN
 */
public interface ClientInterface extends Remote{
    
    public abstract void receiveMessage (Message msg) throws RemoteException;
    public abstract void receiveFriends (ArrayList<UserDto> myFriendsList)throws RemoteException;
    public abstract void receiveFriendRequest(ArrayList<UserDto> friendRequests)throws RemoteException;
    public abstract void updateFriend(UserDto friend)throws RemoteException;
    public abstract void receiveNotification(CloudNotification notification)throws RemoteException;
}
