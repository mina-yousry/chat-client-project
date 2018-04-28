/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerInterfaces;

import dtos.UserDto;
import exceptions.ResultNotFoundException;
import exceptions.WrongPasswordException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

/**
 *
 * @author Anonymous
 */
public interface LoginInterface extends Remote{
    
    /**
     *
     * @param email
     * @param pass
     * @return user bean if succeeded 
     * @return null if user Already signed IN
     * @throws ResultNotFoundException If mail not found in DB
     * @throws exceptions.WrongPasswordException if wrong password
     * @throws RemoteException
     */
    public UserDto login(String email,String pass)throws ResultNotFoundException,WrongPasswordException,RemoteException;
     /**
     *
     * @param user userDTO 
     * @return user bean if succeeded 
     * @return true if succeded,false if user exist
     * @throws SQLException If DB error
     * @throws 
     */
    public Boolean SignUp(UserDto user)throws SQLException,RemoteException;
    

}
