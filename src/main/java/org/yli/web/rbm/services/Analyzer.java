package org.yli.web.rbm.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.yli.web.rbm.db.RbDb;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yli on 1/23/16.
 */
public class Analyzer implements IAnalyzer {

  public static final String UNKNOWN = "unknown";

  public Analyzer() {
    // do nothing
  }

  @Override
  public String getTop30ReviewRequestCountGroupByPeople() throws SQLException {
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

      if (conn != null) {
        conn.close();
      }
    }

    return new GsonBuilder().create().toJson(aList);
  }

  @Override
  public String getRequestStatisticOfLastSixMonth() throws SQLException {
    List<Pair<String, Integer>> aList = Lists.newArrayList();

    Connection conn = RbDb.getConnection();

    Statement statement = null;
    ResultSet rs = null;
    try {
      statement = conn.createStatement();

      rs =
          statement.executeQuery("select count(*) the_count, CONCAT(year(time_added), '/', month(time_added)) month " +
              "from reviews_reviewrequest " +
              "where date_sub(CURDATE(), INTERVAL 180 day) <= time_added " +
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

      if (conn != null) {
        conn.close();
      }
    }

    return new GsonBuilder().create().toJson(aList);
  }

  @Override
  public String getRequestsGroupByProductSentInLastMonth() throws SQLException {
    Map<String, Integer> aMap = Maps.newLinkedHashMap();

    final List<String> patterns = Lists.newArrayList(System.getProperty("PRODUCTS").split(","));
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
          "WHERE DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= time_added " +
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

      if (conn != null) {
        conn.close();
      }
    }

    List<Pair<String, Integer>> aList = Lists.newArrayList();

    for (Map.Entry<String, Integer> entry : aMap.entrySet()) {
      aList.add(new Pair<>(entry.getKey(), entry.getValue()));
    }

    return new GsonBuilder().create().toJson(aList);
  }

  @Override
  public String getNewAddedUsersPerMonthInLastYear() throws SQLException {
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

      if (conn != null) {
        conn.close();
      }
    }

    return new GsonBuilder().create().toJson(aList);
  }

  @Override
  public String getTop30ReviewerInLastMonth() throws SQLException {
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

      if (conn != null) {
        conn.close();
      }
    }

    return new GsonBuilder().create().toJson(aList);
  }

  @Override
  public String getP4Statistic(Date fromDate) throws SQLException {
    List<PerforceStatisticData> datas = Lists.newArrayList();

    Connection conn = RbDb.getConnection();

    PreparedStatement statement = null;
    ResultSet rs = null;
    try {
      statement = conn.prepareStatement("" +
          "select a.first_name, a.last_name, a.username, a.ticket_count reviewed_changelists, b.ticket_count all_changelists, a.ticket_count / b.ticket_count ratio from\n" +
          "  (\n" +
          "    select count(a.id) ticket_count, a.username, b.first_name, b.last_name\n" +
          "    from p4_cl a\n" +
          "      left join auth_user b on a.username = b.username\n" +
          "    where date > ?\n" +
          "          and a.pending_cl_id in (select changenum from reviews_reviewrequest)\n" +
          "    group by a.username\n" +
          "    order by ticket_count desc\n" +
          "  ) a,\n" +
          "  (\n" +
          "    select count(a.id) ticket_count, a.username, b.first_name, b.last_name\n" +
          "    from p4_cl a\n" +
          "      left join auth_user b on a.username = b.username\n" +
          "    where date > ?\n" +
          "    group by a.username order by ticket_count desc\n" +
          "  ) b\n" +
          "where a.username = b.username\n" +
          "order by reviewed_changelists desc");

      statement.setDate(1, new java.sql.Date(fromDate.getTime()));
      statement.setDate(2, new java.sql.Date(fromDate.getTime()));

      rs = statement.executeQuery();

      while (rs.next()) {
        String firstName = rs.getString(1);
        String lastName = rs.getString(2);
        String p4Account = rs.getString(3);
        int reviewClCount = rs.getInt(4);
        int allClCount = rs.getInt(5);
        double reviewRatio = rs.getDouble(6) * 100.0;
        datas.add(new PerforceStatisticData(firstName, lastName, p4Account, reviewClCount, allClCount, reviewRatio));
      }

    } finally {
      if (rs != null) {
        rs.close();
      }

      if (statement != null) {
        statement.close();
      }

      if (conn != null) {
        conn.close();
      }
    }

    return new GsonBuilder().create().toJson(datas);
  }

  @Override
  public String getLatestClId() throws SQLException {
    Map<String, Integer> data = Maps.newHashMap();

    Connection conn = RbDb.getConnection();

    Statement statement = null;
    ResultSet rs = null;
    try {
      statement = conn.createStatement();

      rs = statement.executeQuery("SELECT max(id) FROM p4_cl");

      if (rs.next()) {
        data.put("latest_id", rs.getInt(1));
      }

    } finally {
      if (rs != null) {
        rs.close();
      }

      if (statement != null) {
        statement.close();
      }

      if (conn != null) {
        conn.close();
      }
    }

    return new GsonBuilder().create().toJson(data, new TypeToken<Map<String,
            Object>>() {}.getType());
  }

  @Override
  public String getReviewRequest(String p4account, Date startDate) throws SQLException {
    List<ReviewRequestData> datas = Lists.newArrayList();

    Connection conn = RbDb.getConnection();

    PreparedStatement statement = null;
    ResultSet rs = null;

    try {
      statement = conn.prepareStatement(
          "SELECT a.id, a.summary, a.time_added, b.username from reviews_reviewrequest a, auth_user b " +
          "WHERE a.submitter_id = b.id and b.username = ? and a.time_added > ? " +
          "order by a.id desc");

      statement.setString(1, p4account);
      statement.setDate(2, new java.sql.Date(startDate.getTime()));

      rs = statement.executeQuery();

      while (rs.next()) {
        int reviewId = rs.getInt(1);
        String summary = rs.getString(2);
        Date timeAdded = rs.getDate(3);
        String p4Account = rs.getString(4);
        datas.add(new ReviewRequestData(reviewId, summary, p4Account, timeAdded));
      }

    } finally {
      if (rs != null) {
        rs.close();
      }

      if (statement != null) {
        statement.close();
      }

      if (conn != null) {
        conn.close();
      }
    }

    return new GsonBuilder().create().toJson(datas);
  }

  @Override
  public String getChangelist(String p4account, Date startDate) throws SQLException {
    List<PerforceClData> datas = Lists.newArrayList();

    Connection conn = RbDb.getConnection();

    PreparedStatement statement = null;
    ResultSet rs = null;

    try {
      statement = conn.prepareStatement(
          "SELECT id, username, description from p4_cl where username = ? and date > ?" + " order by id desc");

      statement.setString(1, p4account);
      statement.setDate(2, new java.sql.Date(startDate.getTime()));

      rs = statement.executeQuery();

      while (rs.next()) {
        String clId = rs.getString(1);
        String username = rs.getString(2);
        String description = rs.getString(3);
        datas.add(new PerforceClData(clId, username, description));
      }

    } finally {
      if (rs != null) {
        rs.close();
      }

      if (statement != null) {
        statement.close();
      }

      if (conn != null) {
        conn.close();
      }
    }

    return new GsonBuilder().create().toJson(datas);
  }
}
