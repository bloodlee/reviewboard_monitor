package org.yli.web.rbm.db;

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
            Class.forName("org.mariadb.jdbc.Driver");
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
            dbConn =
                    DriverManager.getConnection(
                            "jdbc:mariadb://localhost:3306/reviewboard", "reviewboard", "reviewboard");
        }
        return dbConn;
    }

}
