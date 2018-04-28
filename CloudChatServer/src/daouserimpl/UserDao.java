package daouserimpl;

import daouserinterfaces.UserDaoInt;
import dbconnfactory.ConnectionFactory;
import dtos.UserDto;
import exceptions.ResultNotFoundException;
import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author AMUN
 */
public class UserDao implements UserDaoInt {

    /**
     *
     * @return true if succeed, false if Email exist
     * @throws SQLException if failed
     */
    @Override
    public boolean create(UserDto bean) throws SQLException {
        try (
                Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("INSERT INTO USER VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");) {
            try (PreparedStatement st = connection.prepareStatement("SELECT * FROM USER WHERE mail=?");) {
                st.setString(1, bean.getMail());
                ResultSet rs = st.executeQuery();

                if (rs.isBeforeFirst()) {
                    return false;
                }
            }

            try (PreparedStatement st = connection.prepareStatement("SELECT id from USER ORDER BY id DESC LIMIT 1");) {
                ResultSet resSet = st.executeQuery();
                if (!resSet.wasNull() && resSet.first()) {
                    ps.setInt(1, resSet.getInt("id") + 1);
                } else {
                    ps.setInt(1, 1);
                }
            }
            ps.setString(2, bean.getFname());
            ps.setString(3, bean.getLname());
            ps.setString(4, bean.getMail());
            ps.setString(5, bean.getPassword());
            ps.setString(6, bean.getGender());
            ps.setDate(7, (Date) bean.getDob());
            ps.setString(8, bean.getCountry());
            if (bean.getProfilePic() == null) {
                ps.setNull(9, java.sql.Types.BLOB);
            } else {
                ps.setBinaryStream(9, new ByteArrayInputStream(bean.getProfilePic()), bean.getProfilePic().length);
            }
            ps.setString(10, bean.getConnStatus());
            ps.setString(11, bean.getAppearanceStatus());
            ps.setDate(12, (Date) bean.getDor());
            ps.executeUpdate();
            return true;
        }
    }

    /**
     * Used to get user bean from database by Email
     *
     * @param email
     * @return UserDto if succeeded
     * @throws SQLException if failed
     * @throws ResultNotFoundException if User Not found in DB
     */
    @Override
    public UserDto retreive(String email) throws ResultNotFoundException, SQLException {
        try (
                Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM USER WHERE mail=?");) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {
                throw new ResultNotFoundException();
            }
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }

        }
        return null;
    }

    /**
     * @param bean User bean
     * @return true if Succeed or false if failed
     * @throws java.sql.SQLException if failed to UPDATE
     */
    @Override

    public boolean update(UserDto bean) throws SQLException {

        try (
                Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("UPDATE USER SET  fname=?,lname=? , mail=?,"
                        + " password=?, gender=?, dob=? ,country=?,image=?,connecting_status=?, appearance_status=?,"
                        + " dor=? WHERE id=?");) {
            ps.setString(1, bean.getFname());
            ps.setString(2, bean.getLname());
            ps.setString(3, bean.getMail());
            ps.setString(4, bean.getPassword());
            ps.setString(5, bean.getGender());
            ps.setDate(6, (Date) bean.getDob());
            ps.setString(7, bean.getCountry());
            if (bean.getProfilePic() == null||bean.getProfilePic().length<0) {
                ps.setNull(8, java.sql.Types.BLOB);
            } else {
                ps.setBinaryStream(8, new ByteArrayInputStream(bean.getProfilePic()), bean.getProfilePic().length);
            }
            ps.setString(9, bean.getConnStatus());
            ps.setString(10, bean.getAppearanceStatus());
            ps.setDate(11, (Date) bean.getDor());
            ps.setInt(12, bean.getId());
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
     * Function to Delete user
     *
     * @param bean user bean
     * @return true if Succeed , False if NOT EXIST
     * @throws SQLException if DB fail
     */
    @Override
    public boolean delete(UserDto bean) throws SQLException {
        try (
                Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("DELETE FROM USER WHERE id=?");) {
            ps.setInt(1, bean.getId());
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
     * Used to get user bean from database by id
     *
     * @param id
     * @return user bean If Exist
     * @throws SQLException if failed
     * @throws ResultNotFoundException if User Not found in DB
     */
    public UserDto getUserById(int id) throws ResultNotFoundException, SQLException {
        try (
                Connection connection = ConnectionFactory.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM USER WHERE id=?");) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {
                throw new ResultNotFoundException();
            }
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }

        }

        return null;
    }

    /**
     * Used to Map user bean from database result set
     *
     * @param rs Result set from query
     * @return UserDto
     * @throws SQLException if DB Failed
     */
    public UserDto extractUserFromResultSet(ResultSet rs) throws SQLException {

        UserDto user = new UserDto();
        user.setId(rs.getInt("id"));
        user.setFname(rs.getString("fname"));
        user.setLname(rs.getString("lname"));
        user.setMail(rs.getString("mail"));
        user.setDob(rs.getDate("DOB"));
        user.setDor(rs.getDate("DOR"));
        user.setPassword(rs.getString("Password"));
        user.setConnStatus(rs.getString("connecting_status"));
        user.setCountry(rs.getString("country"));
        user.setGender(rs.getString("gender"));
        user.setAppearanceStatus(rs.getString("appearance_status"));
        Blob imgBlob = rs.getBlob("image");
        if (!rs.wasNull()) {
            byte[] img = imgBlob.getBytes(1, (int) rs.getBlob("image").length());
            user.setProfilePic(img);
        } else {
            user.setProfilePic(null);
        }

        return user;
    }

}
