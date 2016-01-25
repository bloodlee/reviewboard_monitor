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
                            "where a.submitter_id = b.id and DATE_SUB(curdate(), INTERVAL 60 DAY) <= a.time_added " +
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
                statement.close();;
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
                            "where date_sub(CURDATE(), INTERVAL 180 day) < time_added " +
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
                statement.close();;
            }
        }

        return new GsonBuilder().create().toJson(aList);
    }
}
