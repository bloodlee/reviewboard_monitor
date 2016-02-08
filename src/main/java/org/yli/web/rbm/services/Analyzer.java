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

  public static String getTop30ReviewRequestCountGroupByPeople() throws SQLException {
    List<Pair<String, Integer>> aList = Lists.newArrayList();

    Connection conn = RbDb.getConnection();

    Statement statement = null;
    ResultSet rs = null;
    try {
      statement = conn.createStatement();

      rs =
          statement.executeQuery("select count(a.id) the_count, b.username, b.first_name, b.last_name " +
              "from reviews_reviewrequest a, auth_user b " +
              "where a.submitter_id = b.id and MONTH(DATE_SUB(curdate(), INTERVAL 30 DAY)) <= MONTH(a.time_added)" +
              " AND  YEAR(DATE_SUB(curdate(), INTERVAL 30 DAY)) <= YEAR(a.time_added) " +
              "group by b.username " +
              "ORDER BY the_count desc " +
              "LIMIT 30");


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
              " YEAR(date_sub(CURDATE(), INTERVAL 180 day)) <= YEAR(time_added) " +
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
          "WHERE MONTH(DATE_SUB(CURDATE(), INTERVAL 30 DAY)) <= MONTH(time_added) AND " +
          "  YEAR(DATE_SUB(CURDATE(), INTERVAL 30 DAY)) <= YEAR(time_added) " +
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

  public static String getTop30ReviewerInLastMonth() throws SQLException {
    List<Pair<String, Integer>> aList = Lists.newArrayList();

    Connection conn = RbDb.getConnection();

    Statement statement = null;
    ResultSet rs = null;
    try {
      statement = conn.createStatement();

      rs = statement.executeQuery("select count(*) total_count, CONCAT(u.first_name, ' ', u.last_name) user_name\n" +
          "from reviews_review a, reviews_comment b, reviews_review_comments c, reviews_reviewrequest d, auth_user u\n" +
          "WHERE a.id = c.review_id and b.id = c.comment_id and d.id = a.review_request_id and a.user_id = u.id\n" +
          "  and MONTH(date_sub(CURDATE(), INTERVAL 30 day)) <= MONTH(d.time_added)\n" +
          "  and YEAR(date_sub(CURDATE(), INTERVAL 30 day)) <= YEAR(d.time_added)\n" +
          "GROUP BY user_name\n" +
          "ORDER BY total_count DESC\n");


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
}
