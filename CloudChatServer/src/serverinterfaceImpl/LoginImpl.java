package serverinterfaceImpl;

import ServerInterfaces.LoginInterface;
import clientInterface.ClientInterface;
import daofriendimpl.FriendDao;
import daouserimpl.UserLogicImpl;
import dtos.UserDto;
import exceptions.ResultNotFoundException;
import exceptions.WrongPasswordException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import messagepkg.CloudNotification;

/**
 * Class Represents Login Service On the Server
 *
 * @author AMUN
 */
public class LoginImpl extends UnicastRemoteObject implements LoginInterface {

    private UserLogicImpl uli;
    private Map<Integer, UserDto> onlineUsersDtosMap;
    private Map<Integer, ClientInterface> onlineUsersRefsMap;
    private UserDto user = null;
    private FriendDao fdao;

    public LoginImpl(Map<Integer, UserDto> onlineUsers, Map<Integer, ClientInterface> onlineUsersRefsMap) throws RemoteException {
        uli = new UserLogicImpl();
        fdao = new FriendDao();
        this.onlineUsersDtosMap = onlineUsers;
        this.onlineUsersRefsMap = onlineUsersRefsMap;
    }

    @Override
    public UserDto login(String email, String pass) throws ResultNotFoundException, WrongPasswordException {

        try {
            user = uli.Login(email, pass);
        } catch (SQLException ex) {
            throw new ResultNotFoundException();
        } catch (WrongPasswordException ex) {
            throw ex;
        }

        if (user != null) {
            if (onlineUsersDtosMap.containsKey(user.getId())) {
                return null;
            }
            onlineUsersDtosMap.put(user.getId(), user);
            System.out.println("user " + user.getFname() + "Connected Successfully");
            if (!user.getAppearanceStatus().equals(UserDto.OFFLINE)) {

                try {
                    String msg = user.getFname() + " is online";
                    CloudNotification notifi = new CloudNotification();
                    notifi.setNotificationBody(msg);
                    if (user.getProfilePic() != null) {
                        notifi.setNotificationImage(user.getProfilePic());
                    }
                    ArrayList<UserDto> friends=fdao.retreiveFriends(user.getId());
                    if(friends!=null)
                    for (UserDto friend :friends) {
                        if (onlineUsersRefsMap.containsKey(friend.getId())) {
                            try {
                                onlineUsersRefsMap.get(friend.getId()).receiveNotification(notifi);
                                onlineUsersRefsMap.get(friend.getId()).updateFriend(user);
                            } catch (RemoteException ex) {
                                Logger.getLogger(ServerMainInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            }
        }

        return user;
    }

    /**
     *
     * @return true if succeeded
     *
     */
    @Override
    public Boolean SignUp(UserDto user) throws SQLException, RemoteException {

        try {
            if (uli.create(user)) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            throw ex;
        }
    }

}
