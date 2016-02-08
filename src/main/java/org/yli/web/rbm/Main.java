package org.yli.web.rbm;

import com.google.common.collect.Maps;
import org.yli.web.rbm.services.Analyzer;
import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

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

        Spark.get("/new_added_user_per_month_in_last_year", (req, res) -> {
            return Analyzer.getNewAddedUsersPerMonthInLastYear();
        });

        Spark.get("/get_requests_groupd_by_product_in_last_30_days", (req, res) -> {
            return Analyzer.getRequestsGroupByProductSentInLastMonth();
        });
    }

}
