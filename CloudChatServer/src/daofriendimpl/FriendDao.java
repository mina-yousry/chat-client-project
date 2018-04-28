package daofriendimpl;

import daofriendsinterfaces.FriendsDaoInt;
import daouserimpl.UserDao;
import dbconnfactory.ConnectionFactory;
import dtos.UserDto;
import exceptions.ResultNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author AMUN
 */
public class FriendDao implements FriendsDaoInt {

    /**
     *
     * @return true if succeed, false if already friend
     * @throws ResultNotFoundException if DB failed
     */
    @Override
    public boolean addFriend(int idMe, int idMyFriend) throws ResultNotFoundException {
        if (idMe != idMyFriend) {
            try (Connection connection = ConnectionFactory.getConnection();
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO FRIEND VALUES (?,?)");) {
                try (PreparedStatement check = connection.prepareStatement("SELECT * FROM FRIEND WHERE user_id=? AND friend_id=?");) {
                    check.setInt(1, idMe);
                    check.setInt(2, idMyFriend);
                    ResultSet rs = check.executeQuery();
                    if (rs.isBeforeFirst()) {
                        return false;
                    }
                }
                ps.setInt(1, idMe);
                ps.setInt(2, idMyFriend);
                int flag = ps.executeUpdate();
                ps.setInt(1, idMyFriend);
                ps.setInt(2, idMe);
                flag = ps.executeUpdate();
                if (flag == 1) {
                    return true;
                }

            } catch (SQLException ex) {
                throw new ResultNotFoundException();
            }
        }
        return false;
    }

    /**
     *
     * @param id User id
     * @return ArrayList<UserBean> of Friends IDs or null if No Friends
     * @throws SQLException
     */
    @Override
    public ArrayList<UserDto> retreiveFriends(int id) throws SQLException {
        ArrayList<UserDto> friendsList = null;
        try (Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT friend_id FROM FRIEND WHERE user_id=?");) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                return null;
            } else {
                UserDao us = new UserDao();
                friendsList = new ArrayList<>();
                while (rs.next()) {
                    int idSeek = rs.getInt("friend_id");
                    friendsList.add(us.getUserById(idSeek));
                }
            }
        } catch (ResultNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
        return friendsList;
    }

    /**
     * Function to Delete user
     *
     * @param idMe user id
     * @param idMyFriend user id
     * @return true if Succeed , False if NOT EXIST
     * @throws SQLException if DB fail
     */
    @Override
    public boolean delete(int idMe, int idMyFriend) throws SQLException {
        try (
                Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("DELETE FROM FRIEND WHERE user_id=? AND friend_id=?");) {
            ps.setInt(1, idMe);
            ps.setInt(2, idMyFriend);
            int flag = ps.executeUpdate();

            if (flag == 1) {
                return true;
            }
        } catch (SQLException ex) {
            throw ex;
        }
        return false;
    }

    /**
     * Not supported
     *
     * @param id user id
     *
     */
    @Override
    public boolean create(Integer obj) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Not supported
     *
     * @param id user id
     *
     */
    @Override
    public Integer retreive(String mail) throws ResultNotFoundException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Not supported
     *
     * @param id user id
     *
     */
    @Override
    public boolean update(Integer obj) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Function to Delete user from whole Table of friends
     *
     * @param id user id
     * @return true if Succeed , False if NOT EXIST
     * @throws SQLException if DB fail
     */
    @Override
    public boolean delete(Integer id) throws SQLException {
        try (
                Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("DELETE FROM FRIEND WHERE user_id=? OR friend_id=?");) {
            ps.setInt(1, id);
            ps.setInt(2, id);
            int flag = ps.executeUpdate();

            if (flag == 1) {
                return true;
            }
        } catch (SQLException ex) {
            throw ex;
        }
        return false;
    }

}
