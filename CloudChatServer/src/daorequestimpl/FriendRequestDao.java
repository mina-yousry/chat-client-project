package daorequestimpl;

import daoRequestinterfaces.RequestDaoInt;
import daouserimpl.UserLogicImpl;
import dbconnfactory.ConnectionFactory;
import dtos.UserDto;
import exceptions.ResultNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AMUN
 */
public class FriendRequestDao implements RequestDaoInt {

    UserLogicImpl uli = new UserLogicImpl();
//    CREATE TABLE FRIEND_REQUEST (
//    sender_id INT(11),
//    receiver_id INT(11),
//    DOS DATE,

    /**
     *
     * @param idMe
     * @param idMyFriend
     * @return boolean true if sent, false if already sent
     *
     */
    @Override
    public boolean sendRequest(int idMe, int idMyFriend) {
        if (idMe != idMyFriend) {
            try (Connection connection = ConnectionFactory.getConnection();
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO FRIEND_REQUEST VALUES (?,?,?)");) {
                try (PreparedStatement check = connection.prepareStatement("SELECT * FROM FRIEND_REQUEST WHERE sender_id=? AND receiver_id=?");) {
                    check.setInt(1, idMe);
                    check.setInt(2, idMyFriend);
                    ResultSet rs = check.executeQuery();
                    if (rs.isBeforeFirst()) {
                        return false;
                    }
                }
                 try (PreparedStatement check = connection.prepareStatement("SELECT * FROM FRIEND_REQUEST WHERE sender_id=? AND receiver_id=?");) {
                    check.setInt(2, idMe);
                    check.setInt(1, idMyFriend);
                    ResultSet rs = check.executeQuery();
                    if (rs.isBeforeFirst()) {
                        return false;
                    }
                }
                ps.setInt(1, idMe);
                ps.setInt(2, idMyFriend);
                ps.setDate(3, new java.sql.Date(new java.util.Date().getTime()));

                int flag = ps.executeUpdate();
                if (flag == 1) {
                    return true;
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;

    }

    /**
     *
     * @param id User id
     * @return ArrayList<UserDto> of ‘UserDTO of Senders or null if no friend
     * requests
     * @throws SQLException
     */
    @Override
    public ArrayList<UserDto> retreiveRequests(int id) throws SQLException {
        ArrayList<UserDto> requestsUsersDtos = new ArrayList<>();
        ArrayList<Integer> idsArrayList = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM FRIEND_REQUEST WHERE receiver_id=? ")) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                while (rs.next()) {
                    idsArrayList.add(rs.getInt("sender_id"));
                }
                for (Integer idSender : idsArrayList) {
                    requestsUsersDtos.add(uli.getUserById(idSender));
                }
                return requestsUsersDtos;

            }

        } catch (ResultNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * @param idSender
     * @param idReciever
     * @return boolean true if deleted,false other wise
     * @throws SQLException
     */
    @Override
    public boolean deleteRequest(int idSender, int idReciever) throws SQLException {
        try (
                Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("DELETE FROM FRIEND_REQUEST WHERE sender_id=? AND receiver_id=?");) {
            ps.setInt(1, idSender);
            ps.setInt(2, idReciever);
            int flag = ps.executeUpdate();

            if (flag == 1) {
                ps.setInt(1, idReciever);
                ps.setInt(2, idSender);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            throw ex;
        }
        return false;
    }

    @Override
    public boolean create(Integer obj) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer retreive(String mail) throws ResultNotFoundException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean update(Integer obj) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean delete(Integer obj) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}


