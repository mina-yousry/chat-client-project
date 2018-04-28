/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servermainlogic;

import clientInterface.ClientInterface;
import dtos.UserDto;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import serverinterfaceImpl.LoginImpl;
import serverinterfaceImpl.ServerMainInterfaceImpl;

/**
 * Server main class
 *
 * @author Anonymous
 */
public class ServerMain {

    private static Map<Integer, UserDto> onlineUsersDtosMap;
    private static Map<Integer, ClientInterface> onlineUsersRefsMap;
    private static Map<Integer, Integer> oneToOneChatMap;
    private static Map<String, Integer> groupChatMap;
    private static LoginImpl loginObj;
    private static ServerMainInterfaceImpl usersObj;
    private static Registry reg;

    public static Map<Integer, UserDto> getOnlineUsersDtosMap() {
        return onlineUsersDtosMap;
    }

    public static Map<Integer, ClientInterface> getOnlineUsersRefsMap() {
        return onlineUsersRefsMap;
    }

    public ServerMain() {
        try {
            onlineUsersDtosMap = Collections.synchronizedMap(new Hashtable<Integer, UserDto>());
            onlineUsersRefsMap = Collections.synchronizedMap(new Hashtable<Integer, ClientInterface>());
            oneToOneChatMap = Collections.synchronizedMap(new LinkedHashMap<Integer, Integer>());
            groupChatMap = Collections.synchronizedMap(new LinkedHashMap<String, Integer>());
            loginObj = new LoginImpl(onlineUsersDtosMap,onlineUsersRefsMap);//create Remote Obj for login
            usersObj = new ServerMainInterfaceImpl(onlineUsersRefsMap, onlineUsersDtosMap, oneToOneChatMap, groupChatMap);//create Remote Obj for all user functions
            reg = LocateRegistry.createRegistry(9999);//Locating registry on port 9999 on the localhost
            reg.rebind("LoginService", loginObj);// bind login Service
            reg.rebind("FunctionService", usersObj);// bind user Service
            System.out.println("LoginObj Binded Successfully...");
            System.out.println("usersObj Binded Successfully...");
        } catch (RemoteException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    public void stop() {
        try {
            reg.unbind("LoginService");
            reg.unbind("FunctionService");
            UnicastRemoteObject.unexportObject(loginObj, true);
            UnicastRemoteObject.unexportObject(usersObj, true);
            System.out.println("Server exiting.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void resume() {

        try {
            loginObj = new LoginImpl(onlineUsersDtosMap,onlineUsersRefsMap);//create Remote Obj for login
            usersObj = new ServerMainInterfaceImpl(onlineUsersRefsMap, onlineUsersDtosMap, oneToOneChatMap, groupChatMap);//create Remote Obj for all user functions
            reg.rebind("LoginService", loginObj);// bind login Service
            reg.rebind("FunctionService", usersObj);// bind user Service
            System.out.println("LoginObj Binded Successfully...");
            System.out.println("usersObj Binded Successfully...");
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }

    }

}
