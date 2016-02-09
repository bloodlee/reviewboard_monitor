package org.yli.web.rbm.db;

import com.google.common.base.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by yli on 1/23/16.
 */
public class RbDb {

    private static final Logger LOGGER = LogManager.getLogger(RbDb.class);

    private static Connection dbConn = null;

    static {
        try {
            // Class.forName("org.mariadb.jdbc.Driver");
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.error("Couldn't find the mariadb JDBC driver", e);
        }
    }

    private RbDb() {
        // do nothing
    }

    /**
     * get the database connection.
     *
     * @return the connection.
     * @throws SQLException
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (dbConn == null) {
            String host = System.getProperty("db_host");
            String port = System.getProperty("db_port");

            if (Strings.isNullOrEmpty(host)) {
                host = "localhost";
            }

            if (Strings.isNullOrEmpty(port)) {
                port = "3306";
            }

            dbConn =
                    DriverManager.getConnection(
                            String.format("jdbc:mysql://%s:%d/reviewboard", host, port), "reviewboard", "reviewboard");
        }
        return dbConn;
    }

}
