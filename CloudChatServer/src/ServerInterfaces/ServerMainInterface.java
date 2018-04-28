/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerInterfaces;

import clientInterface.ClientInterface;
import dtos.UserDto;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import messagepkg.Message;

/**
 * @author AMUN Description ServerMainInterface is where to put all methods
 * clients may use as a service
 */
public interface ServerMainInterface extends Remote {

    /**
     * Description Func used to register client obj on the server
     *
     * @param userID user id
     * @param client Remote obj
     * @throws RemoteException if RMI failed
     */
    public void register(int userID, ClientInterface client) throws RemoteException;

    /**
     * Description get chat ID to decide create a new tap in tapped panel
     *
     * @return Integer value 1:Integer.MAXVALUE
     * @throws RemoteException if RMI failed
     */
    public Integer getChatID(Integer idSender, Integer idReceiver) throws RemoteException;

    /**
     * Description Func used to send msg
     *
     * @throws RemoteException if RMI failed
     */
    public void sendToAll(Message msg) throws RemoteException;

    /**
     * Description Func used to search for a user to add
     *
     * @param mail searched email
     * @return UserDto of friend if Found, null if not found
     * @throws java.sql.SQLException if DB failed
     * @throws RemoteException if RMI failed
     */
    public UserDto searchByMail(String mail) throws SQLException, RemoteException;

    /**
     * Description Func used to Add Friend
     *
     * @param idMe user id
     * @param idMyfirend friend id
     * @return true if succeed, false if already friend
     * @throws RemoteException if RMI failed
     */
    public boolean addFriend(int idMe, int idMyfirend) throws RemoteException;

    /**
     * Description Func used to remove Friend from user friend list
     *
     * @param idMe user id
     * @param idMyfirend friend id
     * @return true if succeed, false if failed to delete duo to DB error
     * @throws RemoteException if RMI failed
     */
    public boolean removeFriend(int idMe, int idMyfirend) throws RemoteException;

    /**
     * Description Func used to Update User Info
     *
     * @param user UserDTO
     * @return true if succeed, false if failed
     * @throws java.sql.SQLException if DB failed
     * @throws RemoteException if RMI failed
     */
    public boolean updateInfo(UserDto user) throws SQLException, RemoteException;

    /**
     * Description Func used to logout
     *
     * @param user UserDTO
     * @return true if succeed, false if failed
     * @throws java.sql.SQLException if DB failed
     * @throws RemoteException if RMI failed
     */
    public boolean logOut(UserDto user) throws SQLException, RemoteException;

    /**
     * Description Func used to logout
     *
     * @param id User id
     * @return ArrayList<UserDto> user Friend List
     * @throws java.sql.SQLException if DB failed
     * @throws RemoteException if RMI failed
     */
    public ArrayList<UserDto> getFriendList(int id) throws SQLException, RemoteException;

    /**
     * Description Func used to send friend Request
     *
     * @param idME User id
     * @param idMyfirend receiver id
     * @return true if send false otherwise;
     * @throws RemoteException if RMI failed
     */
    public boolean sendFriendRequest(int idMe, int idMyfirend) throws RemoteException;

    /**
     * Description Func used to decline friend Request
     *
     * @param idME User id
     * @param idMyfirend receiver id
     * @return true if send false otherwise;
     * @throws RemoteException if RMI failed
     */
    public boolean removeFriendRequest(int idMe, int idMyfirend) throws RemoteException;

    /**
     * Description Func used to logout
     *
     * @param id User id
     * @return ArrayList<UserDto> user Friend Requests List
     */
    public ArrayList<UserDto> getFriendRequestList(int id) throws RemoteException;

    /**
     * Description Func used to get group chat id
     *
     * @param groupName
     */
    public Integer getGroupChatID(String groupName)throws RemoteException;

}
