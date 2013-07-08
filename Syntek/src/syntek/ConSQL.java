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
        public static Connection con = null;
    public static Connection getConnection() {
        
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=syntek";
            String user = "sa";
            String pass = "123456";
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(url, user, pass);
        } catch (Exception ex) {
            Logger.getLogger(ConSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }
}
