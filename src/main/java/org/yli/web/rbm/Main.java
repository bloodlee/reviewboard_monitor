package org.yli.web.rbm;

import com.google.common.collect.Maps;
import org.yli.web.rbm.memcached.MemcachedUtil;
import org.yli.web.rbm.proxy.CachedAnalyzerProxy;
import org.yli.web.rbm.services.Analyzer;
import org.yli.web.rbm.services.IAnalyzer;
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

        IAnalyzer proxy = CachedAnalyzerProxy.bind(new Analyzer());

        Spark.get("/dashboard", (req, res) -> {
            return new ModelAndView(Maps.newHashMap(), "hello.ftl");
        }, new FreeMarkerEngine());

        Spark.get("/last_month_people_post", (req, res) -> {
            return proxy.getTop30ReviewRequestCountGroupByPeople();
        });

        Spark.get("/review_request_per_month_last_half_year", (req, res) -> {
            return proxy.getRequestStatisticOfLastSixMonth();
        });

        Spark.get("/new_added_user_per_month_in_last_year", (req, res) -> {
            return proxy.getNewAddedUsersPerMonthInLastYear();
        });

        Spark.get("/get_requests_groupd_by_product_in_last_30_days", (req, res) -> {
            return proxy.getRequestsGroupByProductSentInLastMonth();
        });

        Spark.get("/get_top_30_reviewer_in_last_30_days", (req, res) -> {
            return proxy.getTop30ReviewerInLastMonth();
        });

    }

}
