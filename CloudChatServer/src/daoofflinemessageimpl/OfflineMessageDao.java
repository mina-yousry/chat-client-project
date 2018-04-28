package daoofflinemessageimpl;

import daoofflinemessageinterface.OffileMessageDaoInt;
import dbconnfactory.ConnectionFactory;
import exceptions.ResultNotFoundException;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import messagepkg.Message;

/**
 *
 * @author AMUN
 */
public class OfflineMessageDao implements OffileMessageDaoInt {

    /**
     *
     * @param idReceiver
     * @return true if succeed, false if failed to insert
     * @throws SQLException if failed
     */
    @Override
    public boolean addOfflineMessage(int idReceiver, Message message) throws SQLException {
        boolean res = false;
        try (
                Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("INSERT INTO OFFLINE_MESSAGE VALUES (?,?,?,?,?,?,?,?)");) {

            ps.setInt(1, message.getSenderID());
            ps.setInt(2, idReceiver);
            ps.setInt(3, message.getChatID());
            ps.setString(4, message.getGroupChatName());
            ps.setString(5, message.getSenderName());
            ps.setString(6, message.getMsg());
            if (message == null) {
                ps.setNull(7, java.sql.Types.BLOB);
            } else {
                ps.setBinaryStream(7, new ByteArrayInputStream(message.getSenderPicThumbnail()), message.getSenderPicThumbnail().length);
            }
            ps.setTimestamp(8, new java.sql.Timestamp(new java.util.Date().getTime()));

            res = ps.executeUpdate() > 0;
        }
        return res;
    }

    /**
     * Used to get user bean from database by Email
     *
     * @param idReceiver user id
     * @return ArrayList<Message> offline Messages succeeded or null if none
     * @throws SQLException if failed
     */
    @Override
    public ArrayList<Message> retreiveMessages(int idReceiver) throws SQLException {
        ArrayList<Message> messages = null;
        try (
                Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM OFFLINE_MESSAGE WHERE receiver_id= ?");) {
            ps.setInt(1, idReceiver);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                messages = new ArrayList<>();
                while (rs.next()) {
                    Message msg = new Message();
                    msg.setChatID(rs.getInt("chatID"));
                    msg.setMsg(rs.getString("message"));
                    msg.setGroupChatName(rs.getString("group_name"));
                    msg.setSenderID(rs.getInt("sender_id"));
                    msg.setSenderName(rs.getString("sender_name"));
                    msg.setMsgColor("BLACK");
                    msg.setSenderPicThumbnail(rs.getBlob("image").getBytes(1, (int) rs.getBlob("image").length()));
                    ArrayList<Integer> ids = new ArrayList<>();
                    ids.add(rs.getInt("sender_id"));
                    ids.add(rs.getInt("receiver_id"));
                    msg.setUsersIdList(ids);
                    messages.add(msg);
                }
            }
        }

        return messages;
    }

    /**
     * Used to get user bean from database by Email
     *
     * @param idMe user id
     * @return true if succeeded to delete offline msgs of false if failed
     * @throws SQLException if DB failed
     */
    @Override
    public boolean delete(int idMe) throws SQLException {
        try (
                Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("DELETE FROM OFFLINE_MESSAGE WHERE receiver_id=?");) {
            ps.setInt(1, idMe);
            int flag = ps.executeUpdate();

            if (flag == 1) {
                return true;
            }

        } catch (SQLException ex) {
            throw ex;
        }
        return false;

    }

    @Override
    public boolean create(Byte[] obj) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Byte[] retreive(String mail) throws ResultNotFoundException, SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean update(Byte[] obj) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean delete(Byte[] obj) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
