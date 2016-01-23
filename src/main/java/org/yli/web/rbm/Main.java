package org.yli.web.rbm;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * Created by yli on 1/23/16.
 */
public class Main {

    public static void main(String[] args) {
        Spark.port(9090);
        Spark.threadPool(8, 2, 30000);
        Spark.staticFileLocation("/public");

        Spark.get("/hello", (req, res) -> {
            Map<String, Object> aMap = Maps.newHashMap();

            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn =
                    DriverManager.getConnection(
                            "jdbc:mariadb://localhost:3306/reviewboard", "reviewboard", "reviewboard");

            Statement statement = conn.createStatement();

            ResultSet rs =
                    statement.executeQuery("select count(a.id) the_count, b.username, b.first_name, b.last_name\n" +
                        "from reviews_reviewrequest a, auth_user b\n" +
                        "where a.submitter_id = b.id and DATE_SUB(curdate(), INTERVAL 60 DAY) <= a.time_added\n" +
                        "group by b.username\n" +
                        "  HAVING count(a.id) > 5\n" +
                        "ORDER BY the_count desc");

            List<String> names = Lists.newArrayList();
            List<Integer> counts = Lists.newArrayList();

            while (rs.next()) {
                names.add(rs.getString(3) + " " + rs.getString(4));
                counts.add(rs.getInt(1));
            }

            aMap.put("names", names);
            aMap.put("counts", counts);


            return new ModelAndView(aMap, "hello.ftl");
        }, new FreeMarkerEngine());
    }

}
