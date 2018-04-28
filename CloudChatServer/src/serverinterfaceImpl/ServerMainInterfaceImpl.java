/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverinterfaceImpl;

import ServerInterfaces.ServerMainInterface;
import clientInterface.ClientInterface;
import daofriendimpl.FriendDao;
import daoofflinemessageimpl.OfflineMessageDao;
import daorequestimpl.FriendRequestDao;
import daouserimpl.UserLogicImpl;
import dtos.UserDto;
import exceptions.ResultNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import messagepkg.CloudNotification;
import messagepkg.Message;

/**
 * @author AMUN Description ServerMainInterface is Implementation for all
 * methods clients may use as a service
 */
public class ServerMainInterfaceImpl extends UnicastRemoteObject implements ServerMainInterface {

    private UserLogicImpl uli;
    private FriendDao fdao;
    private FriendRequestDao frdao;
    private OfflineMessageDao ofmsgdao;
    private static Map<Integer, ClientInterface> onlineUsersRefsMap;
    private static Map<Integer, UserDto> onlineUsersDtosMap;
    private static Map<Integer, Integer> oneToOneChatMap;
    private static Map<String, Integer> groupChatMap;
    private Integer chatID;
    private Integer groupChatID;

    public ServerMainInterfaceImpl(Map<Integer, ClientInterface> onlineUsersRefsMap, Map<Integer, UserDto> onlineUsersDtosMap, Map<Integer, Integer> oneToOneChatMap, Map<String, Integer> groupChatMap) throws RemoteException {
        this.onlineUsersRefsMap = onlineUsersRefsMap;
        this.onlineUsersDtosMap = onlineUsersDtosMap;
        this.oneToOneChatMap = oneToOneChatMap;
        this.groupChatMap = groupChatMap;
        uli = new UserLogicImpl();
        fdao = new FriendDao();
        ofmsgdao = new OfflineMessageDao();
        frdao = new FriendRequestDao();
        chatID = new Integer(1);
        groupChatID = new Integer(1);
    }

    /**
     * Description Func used to register client obj on the server
     *
     * @param userID user id
     * @param client Remote obj
     * @throws RemoteException if RMI failed
     */
    @Override
    public void register(int userID, ClientInterface client) {

        onlineUsersRefsMap.put(userID, client);
        try {
            ArrayList<Message> offlineMessages = ofmsgdao.retreiveMessages(userID);

            if (offlineMessages != null && offlineMessages.size() > 0) {
                for (Message msg : offlineMessages) {
                    System.out.println("Offlie msg" + msg.getMsg());
                    client.receiveMessage(msg);
                }
                ofmsgdao.delete(userID);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerMainInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Description Func used to send msg
     *
     * @throws RemoteException if RMI failed
     */
    @Override
    public void sendToAll(Message msg) throws RemoteException {
        List<Integer> receivers = Collections.synchronizedList(new ArrayList<Integer>(msg.getUsersIdList()));
        for (int userID : receivers) {
            if (onlineUsersRefsMap.get(userID) != null
                    && (!onlineUsersDtosMap.get(userID).getAppearanceStatus().equals(UserDto.OFFLINE)) || userID == msg.getSenderID()) {
                onlineUsersRefsMap.get(userID).receiveMessage(msg);
            } else  {
                try {
                    UserDto offlineUser = uli.getUserById(userID);
                    String notifiMsg = offlineUser.getFname() + " is Offline he will respond ASAP";
                    CloudNotification notifi = new CloudNotification();
                    if (offlineUser.getProfilePic() != null) {
                        notifi.setNotificationBody(notifiMsg);
                        notifi.setNotificationImage(offlineUser.getProfilePic());
                    }
                    onlineUsersRefsMap.get(msg.getSenderID()).receiveNotification(notifi);
                    ofmsgdao.addOfflineMessage(userID, msg);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (ResultNotFoundException ex) {
                    Logger.getLogger(ServerMainInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    /**
     * Description Func used to logout
     *
     * @param user UserDTO
     * @return true if succeed, false if failed
     * @throws java.sql.SQLException if DB failed
     * @throws RemoteException if RMI failed
     */
    @Override
    public boolean logOut(UserDto user) throws SQLException {
        boolean res = uli.Logout(user);
        user.setAppearanceStatus(UserDto.OFFLINE);
        for (UserDto friend : fdao.retreiveFriends(user.getId())) {
            if (onlineUsersRefsMap.containsKey(friend.getId())) {
                try {
                    String msg = user.getFname() + " went offline";
                    CloudNotification notifi = new CloudNotification();
                    if (user.getProfilePic() != null) {
                        notifi.setNotificationBody(msg);
                        notifi.setNotificationImage(user.getProfilePic());
                    }
                    onlineUsersRefsMap.get(friend.getId()).receiveNotification(notifi);
                    onlineUsersRefsMap.get(friend.getId()).updateFriend(user);
                } catch (RemoteException ex) {
                    Logger.getLogger(ServerMainInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        onlineUsersDtosMap.remove(user.getId(), user);
        onlineUsersRefsMap.remove(user.getId(), user);
        return res;
    }

    /**
     * Description Func used to logout
     *
     * @param idSender
     * @param idReceiver
     */
    @Override
    public Integer getChatID(Integer idSender, Integer idReceiver) {
        int i = 0;
        for (Entry<Integer, Integer> entry : oneToOneChatMap.entrySet()) {
            i++;
            if (entry.getKey().equals(idSender) && entry.getValue().equals(idReceiver)) {
                System.out.println("sender :" + idSender + "Receiver :" + idReceiver + "Found");
                return i;
            }
            if (entry.getKey().equals(idReceiver) && entry.getValue().equals(idSender)) {

                System.out.println("sender :" + idSender + "Receiver :" + idSender + "Found");
                return i;
            }
        }
        oneToOneChatMap.put(idSender, idReceiver);
        return chatID++;
    }

    /**
     * Description Func used to get group chat id
     *
     * @param groupName
     */
    @Override
    public Integer getGroupChatID(String groupName) {
        int i = 0;
        for (Entry<String, Integer> entry : groupChatMap.entrySet()) {
            i++;
            if (entry.getKey().equals(groupName)) {
                return i;
            }
        }
        groupChatMap.put(groupName, groupChatID);
        return groupChatID++;
    }

    /**
     * Description Func used to Update User Info
     *
     * @param user UserDTO
     * @return true if succeed, false if failed
     * @throws java.sql.SQLException if DB failed
     * @throws RemoteException if RMI failed
     */
    @Override
    public boolean updateInfo(UserDto user) throws SQLException, RemoteException {
        try {
            ArrayList<Message> offlineMessages = ofmsgdao.retreiveMessages(user.getId());

            if (offlineMessages != null && offlineMessages.size() > 0 && !user.getConnStatus().equals(UserDto.OFFLINE)) {
                for (Message msg : offlineMessages) {
                    System.out.println("Offlie msg" + msg.getMsg());
                    onlineUsersRefsMap.get(user.getId()).receiveMessage(msg);
                }
                ofmsgdao.delete(user.getId());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        ArrayList<UserDto> friends = fdao.retreiveFriends(user.getId());
        if (friends != null) {
            for (UserDto friend : fdao.retreiveFriends(user.getId())) {
                if (onlineUsersRefsMap.containsKey(friend.getId())) {
                    onlineUsersRefsMap.get(friend.getId()).updateFriend(user);
                }
            }
            onlineUsersDtosMap.replace(user.getId(), user);
        }
        return uli.update(user);
    }

    @Override
    public UserDto searchByMail(String mail) throws SQLException {
        ArrayList<UserDto> allUsers = uli.getallUsers();
        if (allUsers != null) {
            for (UserDto user : allUsers) {
                if (user.getMail().equalsIgnoreCase(mail)) {
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public boolean addFriend(int idMe, int idMyfirend) {
        Boolean res = false;
        try {
            res = fdao.addFriend(idMe, idMyfirend) && frdao.deleteRequest(idMyfirend, idMe);
            if (onlineUsersRefsMap.containsKey(idMe)) {
                onlineUsersRefsMap.get(idMe).receiveFriends(fdao.retreiveFriends(idMe));
            }
            if (onlineUsersRefsMap.containsKey(idMyfirend)) {
                onlineUsersRefsMap.get(idMyfirend).receiveFriends(fdao.retreiveFriends(idMyfirend));
                onlineUsersRefsMap.get(idMyfirend).receiveFriendRequest(frdao.retreiveRequests(idMyfirend));
            }
        } catch (ResultNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean removeFriend(int idMe, int idMyfirend) {
        Boolean res = false;
        try {
            res = fdao.delete(idMe, idMyfirend);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return res;
    }

    /**
     * Description Func used to send friend Request
     *
     * @param idME User id
     * @param idMyfirend receiver id
     * @return true if send false otherwise;
     * @throws RemoteException if RMI failed
     */
    @Override
    public boolean sendFriendRequest(int idMe, int idMyfirend) {
        boolean res = false;
        res = frdao.sendRequest(idMe, idMyfirend);
        if (onlineUsersRefsMap.containsKey(idMyfirend)) {
            try {
                ArrayList<UserDto> temp = new ArrayList<UserDto>();
                temp.add(uli.getUserById(idMe));
                onlineUsersRefsMap.get(idMyfirend).receiveFriendRequest(temp);
            } catch (ResultNotFoundException ex) {
                ex.printStackTrace();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }

        }
        return res;
    }

    /**
     * Description Func used to decline friend Request
     *
     * @param idME User id
     * @param idMyfirend receiver id
     * @return true if send false otherwise;
     * @throws RemoteException if RMI failed
     */
    @Override
    public boolean removeFriendRequest(int idMe, int idMyfirend) {
        boolean res = false;
        try {
            if (onlineUsersRefsMap.containsKey(idMe)) {
                onlineUsersRefsMap.get(idMe).receiveFriendRequest(frdao.retreiveRequests(idMe));
            }
            if (onlineUsersRefsMap.containsKey(idMyfirend)) {
                onlineUsersRefsMap.get(idMyfirend).receiveFriendRequest(frdao.retreiveRequests(idMyfirend));
            }
            res = frdao.deleteRequest(idMyfirend, idMe);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return res;
    }

    /**
     * Description Func used to logout
     *
     * @param id User id
     * @return ArrayList<UserDto> user Friend List
     * @throws java.sql.SQLException if DB failed
     * @throws RemoteException if RMI failed
     */
    @Override
    public ArrayList<UserDto> getFriendList(int id) {
        try {
            return fdao.retreiveFriends(id);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Description Func used to logout
     *
     * @param id User id
     * @return ArrayList<UserDto> user Friend Requests List
     */
    @Override
    public ArrayList<UserDto> getFriendRequestList(int id) {
        try {
            return frdao.retreiveRequests(id);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
