package dbconnfactory;

/**
 *
 * @author AMUN
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
//127.0.0.1:6666
    //192.168.137.1:3306
    public static final String URL = "jdbc:mysql://127.0.0.1:6666/";
    public static final String USER = "root";
    public static final String PASS = "1234";

    /**
     * Get a connection to database
     *
     * @return Connection object
     */
    public static Connection getConnection() {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            return DriverManager.getConnection(URL + "chat", USER, PASS);
        } catch (SQLException ex) {
            throw new RuntimeException("Error connecting to the database", ex);
        }
    }

}
