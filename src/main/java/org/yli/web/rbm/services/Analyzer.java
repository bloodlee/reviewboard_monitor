package org.yli.web.rbm.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import org.javatuples.Pair;
import org.yli.web.rbm.db.RbDb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * Created by yli on 1/23/16.
 */
public class Analyzer {

  public static final String UNKNOWN = "unknown";

  private Analyzer() {
    // do nothing
  }

  public static String getCommentCountGroupByPeople() throws SQLException {
    List<Pair<String, Integer>> aList = Lists.newArrayList();

    Connection conn = RbDb.getConnection();

    Statement statement = null;
    ResultSet rs = null;
    try {
      statement = conn.createStatement();

      rs =
          statement.executeQuery("select count(a.id) the_count, b.username, b.first_name, b.last_name " +
              "from reviews_reviewrequest a, auth_user b " +
              "where a.submitter_id = b.id and MONTH(DATE_SUB(curdate(), INTERVAL 60 DAY)) <= MONTH(a.time_added)" +
              " AND  YEAR(DATE_SUB(curdate(), INTERVAL 60 DAY)) <= YEAR(a.time_added) " +
              "group by b.username" +
              "  HAVING count(a.id) > 5 " +
              "ORDER BY the_count desc");


      while (rs.next()) {
        aList.add(new Pair<>(rs.getString(3) + " " + rs.getString(4), rs.getInt(1)));
      }
    } finally {
      if (rs != null) {
        rs.close();
      }

      if (statement != null) {
        statement.close();
      }
    }

    return new GsonBuilder().create().toJson(aList);
  }

  public static String getRequestStatisticOfLastSixMonth() throws SQLException {
    List<Pair<String, Integer>> aList = Lists.newArrayList();

    Connection conn = RbDb.getConnection();

    Statement statement = null;
    ResultSet rs = null;
    try {
      statement = conn.createStatement();

      rs =
          statement.executeQuery("select count(*) the_count, CONCAT(year(time_added), '/', month(time_added)) month " +
              "from reviews_reviewrequest " +
              "where MONTH(date_sub(CURDATE(), INTERVAL 180 day)) <= MONTH(time_added) AND " +
              " YEAR(data_sub(CURDATE(), INTERVAL 180 day)) <= YEAR(time_added) " +
              "GROUP BY month(time_added) " +
              "ORDER BY time_added");

      while (rs.next()) {
        aList.add(new Pair<>(rs.getString(2), rs.getInt(1)));
      }
    } finally {
      if (rs != null) {
        rs.close();
      }

      if (statement != null) {
        statement.close();
      }
    }

    return new GsonBuilder().create().toJson(aList);
  }

  public static String getRequestsGroupByProductSentInLastMonth() throws SQLException {
    Map<String, Integer> aMap = Maps.newLinkedHashMap();

    final List<String> patterns = Lists.newArrayList("PLT", "SMO", "MOD", "TBX", "LMC", "DOM");
    for (String aPattern : patterns) {
      aMap.put(aPattern, 0);
    }
    aMap.put(UNKNOWN, 0);

    Connection conn = RbDb.getConnection();

    Statement statement = null;
    ResultSet rs = null;
    try {
      statement = conn.createStatement();

      rs = statement.executeQuery("SELECT summary FROM reviews_reviewrequest " +
          "WHERE MONTH(DATE_SUB(CURDATE(), INTERVAL 180 DAY)) <= MONTH(time_added) AND " +
          "  YEAR(DATE_SUB(CURDATE(), INTERVAL 180 DAY)) <= YEAR(time_added) " +
          "ORDER BY time_added");

      String summary = null;
      boolean found = false;
      while (rs.next()) {
        summary = rs.getString(1);

        found = false;
        for (int i = 0; i < patterns.size(); ++i) {
          String key = patterns.get(i);
          if (summary.contains(key + "-")) {
            aMap.put(key, aMap.get(key) + 1);
            found = true;
            break;
          }
        }

        if (!found) {
          aMap.put(UNKNOWN, aMap.get(UNKNOWN) + 1);
        }
      }
    } finally {
      if (rs != null) {
        rs.close();
      }

      if (statement != null) {
        statement.close();
      }
    }

    List<Pair<String, Integer>> aList = Lists.newArrayList();

    for (Map.Entry<String, Integer> entry : aMap.entrySet()) {
      aList.add(new Pair<>(entry.getKey(), entry.getValue()));
    }

    return new GsonBuilder().create().toJson(aList);
  }

  public static String getNewAddedUsersPerMonthInLastYear() throws SQLException {
    List<Pair<String, Integer>> aList = Lists.newArrayList();

    Connection conn = RbDb.getConnection();

    Statement statement = null;
    ResultSet rs = null;
    try {
      statement = conn.createStatement();

      rs = statement.executeQuery("SELECT CONCAT(YEAR(date_joined), '-', MONTH(date_joined)) as joined_month, " +
          "         count(*) AS people_count " +
          "    FROM auth_user " +
          "WHERE MONTH(DATE_SUB(CURDATE(), INTERVAL 365 DAY)) <= MONTH(date_joined) AND " +
          "  YEAR(DATE_SUB(CURDATE(), INTERVAL 365 DAY)) <= YEAR(date_joined) " +
          "GROUP BY YEAR(date_joined), MONTH(date_joined) " +
          "ORDER BY YEAR(date_joined), MONTH(date_joined) ");


      while (rs.next()) {
        aList.add(new Pair<>(rs.getString(1), rs.getInt(2)));
      }
    } finally {
      if (rs != null) {
        rs.close();
      }

      if (statement != null) {
        statement.close();
      }
    }

    return new GsonBuilder().create().toJson(aList);
  }
}
