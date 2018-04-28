package daouserinterfaces;

import dtos.UserDto;
import exceptions.ResultNotFoundException;
import exceptions.WrongPasswordException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author AMUN
 */
public interface UserLogicInterface extends UserDaoInt {

    /**
     * Used to get Array list of all user beans from database
     *
     *
     * @return ArrayList<UserBean> or null if None found
     * @throws SQLException if failed
     */
    public ArrayList<UserDto> getallUsers() throws SQLException;

    /**
     * Used to login with user mail and password
     *
     * @param mail
     * @param pass
     * @return userBean or NULL if not EXIST
     * @throws ResultNotFoundException if username doesn't exist
     * @throws SQLException
     */
    public UserDto Login(String mail, String pass) throws ResultNotFoundException,WrongPasswordException, SQLException;

    /**
     *
     * @param bean
     * @return true if succeeded ,false if failed
     * @throws SQLException if DB error
     */
    public boolean Logout(UserDto bean) throws SQLException;

}
