package org.yli.web.rbm.db;

import com.google.common.base.Strings;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by yli on 1/23/16.
 */
public class RbDb {

  private static final Logger LOGGER = LogManager.getLogger(RbDb.class);

  private static BasicDataSource dataSource;

  static {
    LOGGER.debug("initialize the data source");

    dataSource = new BasicDataSource();
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");

    String host = System.getProperty("db_host");
    String port = System.getProperty("db_port");

    if (Strings.isNullOrEmpty(host)) {
      host = "localhost";
    }

    if (Strings.isNullOrEmpty(port)) {
      port = "3306";
    }

    dataSource.setUrl(String.format("jdbc:mysql://%s:%s/reviewboard", host, port));

    dataSource.setUsername("reviewboard");
    dataSource.setPassword("reviewboard");

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        LOGGER.debug("Close the datasource");
        try {
          dataSource.close();
        } catch (SQLException e) {
          LOGGER.debug(e.getMessage(), e);
        }
      }
    });
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
  public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

}
