/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daouserimpl;

import dbconnfactory.ConnectionFactory;
import dtos.UserDto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import daouserinterfaces.UserLogicInterface;
import exceptions.ResultNotFoundException;
import exceptions.WrongPasswordException;

/**
 *
 * @author AMUN
 */
public class UserLogicImpl extends UserDao implements UserLogicInterface {

    /**
     * Used to get Array list of all user beans from database
     *
     *
     * @return ArrayList<UserBean> or null if None found
     * @throws SQLException if failed
     */
    @Override
    public ArrayList<UserDto> getallUsers() throws SQLException {
        try (
                Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM USER");) {
            ResultSet rs;
            ArrayList<UserDto> users = new ArrayList<>();
            rs = ps.executeQuery();
            while (rs.next()) {

                users.add(extractUserFromResultSet(rs));
            }
            return users;
        }
    }

    /**
     * Used to login with user mail and password
     *
     * @param mail
     * @param pass
     * @return userBean if succeeded or NULL if password wrong
     * @throws ResultNotFoundException if User mail don't exist
     * @throws SQLException if DB error
     */
    @Override
    public UserDto Login(String mail, String pass) throws ResultNotFoundException,WrongPasswordException, SQLException {

        try (
                Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("UPDATE USER SET connecting_status =?  WHERE id =?");) {
            try {
                UserDto user = super.retreive(mail);
                if (pass.equals(user.getPassword())) {
                    user.setConnStatus(UserDto.ONLINE);
                    ps.setString(1, user.getConnStatus());
                    ps.setInt(2, user.getId());
                    int flag = ps.executeUpdate();
                    if (flag == 1) {
                        return user;
                    }
                }
                else
                {
                    throw new WrongPasswordException();
                }
            } catch (ResultNotFoundException ex) {
                throw ex;
            }
            return null;
        }
    }

    /**
     *
     * @param bean User bean
     * @return true if succeeded ,false if failed
     * @throws SQLException if DB error
     */
    @Override
    public boolean Logout(UserDto bean) throws SQLException {
        try (
                Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("UPDATE USER SET connecting_status =?,appearance_status=? WHERE id =?");) {

             bean.setConnStatus(UserDto.OFFLINE);
            ps.setString(1, UserDto.OFFLINE);
            ps.setString(2, UserDto.OFFLINE);
            ps.setInt(3, bean.getId());
            int flag = ps.executeUpdate();
            if (flag == 1) {
                return true;
            } else {
                return false;
            }
        }
    }
}
