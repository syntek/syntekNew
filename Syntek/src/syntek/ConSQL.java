/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syntek;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ABC
 */
public class ConSQL {

    public static String SERVER_NAME = null;
    public static String PORT = null;
    public static String DATABASE_NAME = null;
    public static String USER_NAME = null;
    public static String PASSWORD = null;
    public static Connection CON = null;

    public ConSQL(String server_name, String port, String database_name, String user_name, String password) {
        this.SERVER_NAME = server_name;
        this.PORT = port;
        this.DATABASE_NAME = database_name;
        this.USER_NAME = user_name;
        this.PASSWORD = password;

        try {
            String url = String.format("jdbc:sqlserver://%s:%s;databaseName=%s", SERVER_NAME, PORT, DATABASE_NAME);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            CON = DriverManager.getConnection(url, USER_NAME, PASSWORD);
        } catch (Exception ex) {
            Logger.getLogger(ConSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
