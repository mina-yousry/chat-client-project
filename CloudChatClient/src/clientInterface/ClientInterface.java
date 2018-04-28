package clientInterface;

import dtos.UserDto;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import messagepkg.CloudNotification;
import messagepkg.Message;

/**
 *
 * @author Mina
 * @author Usama
 * 
 * the interface of client business layer
 */
public interface ClientInterface extends Remote {

    /**
     * receives a message from server and pass it to the controller
     *
     * @param msg
     * @throws RemoteException
     */
    public abstract void receiveMessage(Message msg) throws RemoteException;

    /**
     * receive list of friends to pass it to the mainUI to view it
     *
     * @param myFriendsList
     * @throws RemoteException
     */
    public abstract void receiveFriends(ArrayList<UserDto> myFriendsList) throws RemoteException;

    /**
     * receive sent friend requests
     *
     * @param friendRequests
     * @throws RemoteException
     */
    public abstract void receiveFriendRequest(ArrayList<UserDto> friendRequests) throws RemoteException;

    /**
     * called to update friends lest
     *
     * @param friend
     * @throws RemoteException
     */
    public abstract void updateFriend(UserDto friend) throws RemoteException;

    /**
     * send notification to user
     *
     * @param notification
     * @throws RemoteException
     */
    public abstract void receiveNotification(CloudNotification notification) throws RemoteException;
}
