package clientInterfaceImpl;

import clientInterface.ClientInterface;
import controllers.MainUIController;
import controllers.SignInController;
import dtos.UserDto;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import messagepkg.CloudNotification;
import messagepkg.Message;

/**
 *
 * @author Mina
 * @author Usama
 *
 * this class implements user business interface
 */
public class ClientImpl extends UnicastRemoteObject implements ClientInterface {

    /**
     * friends ArrayList
     */
    private static ArrayList<UserDto> myFriendsList;
    private static ArrayList<UserDto> myFriendsRequestList;

    private MainUIController mainUIController;

    /**
     * receives a message from server and pass it to the controller
     *
     * @param msg
     * @throws RemoteException
     */
    @Override
    public void receiveMessage(Message msg) {
        System.out.println(msg.getMsg() + "  " + msg.getSenderName());
        if (msg.getGroupChatName() == null) {
            SignInController.getMainController().showMessage(msg);
        } else {
            SignInController.getMainController().showGroupMessage(msg);
        }
    }

    /**
     * receive list of friends to pass it to the mainUI to view it
     *
     * @param myFriendsList
     * @throws RemoteException
     */
    @Override
    public void receiveFriends(ArrayList<UserDto> myFriendsList) {
        ClientImpl.myFriendsList = myFriendsList;
        SignInController.getMainController().showFriends(myFriendsList);
    }

    /**
     * get user friends
     *
     *
     */
    public static ArrayList<UserDto> getMyFriendsList() {
        return myFriendsList;
    }

    /**
     * get user friends
     *
     *
     */
    public static void setMyFriendsList(ArrayList<UserDto> myFriendsList) {
        ClientImpl.myFriendsList = myFriendsList;
    }

    /**
     * called to update friends lest
     *
     * @param friend
     * @throws RemoteException
     */
    @Override
    public void updateFriend(UserDto friend) {
        mainUIController.updateMyFriend(friend);
    }

    /**
     * send notification to user
     *
     * @param notification
     * @throws RemoteException
     */
    @Override
    public void receiveNotification(CloudNotification notification) throws RemoteException {
        SignInController.getMainController().showNotification(notification);
    }

    /**
     * receive sent friend requests
     *
     * @param friendRequests
     * @throws RemoteException
     */
    @Override
    public void receiveFriendRequest(ArrayList<UserDto> friendRequests) throws RemoteException {
        myFriendsRequestList = friendRequests;
        mainUIController.showFriendRequest(friendRequests);
    }

    /**
     * myFriendsRequestList getter
     *
     * @return myFriendsRequestList
     */
    public static ArrayList<UserDto> getMyFriendsRequestList() {
        return myFriendsRequestList;
    }

    /**
     * myFriendsRequestList getter
     *
     * @return myFriendsRequestList
     */
    public static void setMyFriendsRequestList(ArrayList<UserDto> myFriendsRequestList) {
        ClientImpl.myFriendsRequestList = myFriendsRequestList;
    }

    /**
     * mainUIController getter
     *
     * @return myFriendsRequestList
     */
    public MainUIController getMainUIController() {
        return mainUIController;

    }


}
