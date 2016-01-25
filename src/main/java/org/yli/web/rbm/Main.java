package org.yli.web.rbm;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.javatuples.Pair;
import org.javatuples.Tuple;
import org.yli.web.rbm.services.Analyzer;
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

        Spark.get("/dashboard", (req, res) -> {
            return new ModelAndView(Maps.newHashMap(), "hello.ftl");
        }, new FreeMarkerEngine());

        Spark.get("/last_month_people_post", (req, res) -> {
            return Analyzer.getCommentCountGroupByPeople();
        });

        Spark.get("/review_request_per_month_last_half_year", (req, res) -> {
            return Analyzer.getRequestStatisticOfLastSixMonth();
        });
    }

}
