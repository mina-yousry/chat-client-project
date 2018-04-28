/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daosinterfaces;

import exceptions.ResultNotFoundException;
import java.sql.SQLException;

/**
 *
 * @author AMUN
 * @param <T>
 */
public interface DataBaseDaoInt <T> {

    public abstract boolean create ( T obj)throws SQLException;
    public abstract T retreive (String mail)throws ResultNotFoundException,SQLException;
    public abstract boolean update ( T obj)throws SQLException;
    public abstract boolean delete ( T obj)throws SQLException;
  
}
