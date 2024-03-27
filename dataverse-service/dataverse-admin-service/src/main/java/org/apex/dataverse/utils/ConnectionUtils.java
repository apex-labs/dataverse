package org.apex.dataverse.utils;



import org.apex.dataverse.constrant.DriverClassConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @ClassName ConnectionUtils
 * @Description TODO
 * @Author wwd
 * @Date 2023/2/23 10:16
 **/
public class ConnectionUtils {

    public static Connection connection(String url, String user, String password, Integer dataSourceType) throws SQLException {
        Connection connection = null;
        try {
            Class.forName(DriverClassConstants.getDriverName(dataSourceType));
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return connection;
    }

    public static void close (Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
