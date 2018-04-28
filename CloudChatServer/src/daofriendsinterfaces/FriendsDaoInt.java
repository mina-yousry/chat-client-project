/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daofriendsinterfaces;

import daosinterfaces.DataBaseDaoInt;
import dtos.UserDto;
import exceptions.ResultNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author AMUN
 */
public interface FriendsDaoInt extends DataBaseDaoInt<Integer> {

    /**
     *
     * @param idMe
     * @param idMyFriend
     * @return boolean
     * @throws SQLException
     */
    public abstract boolean addFriend(int idMe, int idMyFriend) throws ResultNotFoundException;

    /**
     *
     * @param id User id
     * @return ArrayList<UserBean> of Friends IDs
     * @throws exceptions.ResultNotFoundException
     * @throws SQLException
     */

    public abstract ArrayList<UserDto> retreiveFriends(int id) throws ResultNotFoundException,SQLException;

        /**
     * @param idMe
     * @param idMyFriend
     * @return boolean
     * @throws SQLException
     */
    public abstract boolean delete(int idMe, int idMyFriend) throws SQLException;

}
