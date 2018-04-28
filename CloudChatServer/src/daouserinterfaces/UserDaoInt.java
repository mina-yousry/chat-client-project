/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daouserinterfaces;

import daosinterfaces.DataBaseDaoInt;
import dtos.UserDto;
import exceptions.ResultNotFoundException;
import java.sql.SQLException;

/**
 *
 * @author AMUN
 */
public interface UserDaoInt extends DataBaseDaoInt<UserDto> {

    /**
     *
     * @param bean
     * @return boolean
     * @throws SQLException
     */
    @Override
    public abstract boolean create(UserDto bean) throws SQLException;

    /**
     *
     * @param email
     * @return user bean
     * @throws SQLException
     * @throws exceptions.ResultNotFoundException
     */
    @Override
    public abstract UserDto retreive(String email) throws SQLException, ResultNotFoundException;

    /**
     * @param bean
     * @return boolean
     * @throws SQLException
     */
    @Override
    public abstract boolean update(UserDto bean) throws SQLException;

    /**
     *
     * @param bean
     * @return boolean
     * @throws SQLException
     */
    @Override
    public abstract boolean delete(UserDto bean) throws SQLException;

}
