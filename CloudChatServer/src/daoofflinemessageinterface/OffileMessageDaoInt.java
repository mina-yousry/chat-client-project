/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daoofflinemessageinterface;

import daosinterfaces.DataBaseDaoInt;
import java.sql.SQLException;
import java.util.ArrayList;
import messagepkg.Message;

/**
 *
 * @author AMUN
 */
public interface OffileMessageDaoInt extends DataBaseDaoInt<Byte[]> {

    /**
     *
     * @param idMe
     * @param message
     * @return boolean
     * @throws SQLException
     */
   public boolean addOfflineMessage(int idMe, Message message) throws SQLException;

    /**
     *
     * @param id User id
     * @return ArrayList<UserBean> of Friends IDs
     * @throws exceptions.ResultNotFoundException
     * @throws SQLException
     */

    public abstract ArrayList<Message>retreiveMessages(int id) throws SQLException;

        /**
     * @param idMe
     * @return boolean
     * @throws SQLException
     */
    public abstract boolean delete(int idMe) throws SQLException;

}
