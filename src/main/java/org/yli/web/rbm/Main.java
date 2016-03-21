package org.yli.web.rbm;

import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.yli.web.rbm.memcached.MemcachedUtil;
import org.yli.web.rbm.proxy.CachedAnalyzerProxy;
import org.yli.web.rbm.services.Analyzer;
import org.yli.web.rbm.services.IAnalyzer;
import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by yli on 1/23/16.
 */
public class Main {

    public static void main(String[] args) {
        Spark.port(9090);
        Spark.threadPool(8, 2, 30000);
        Spark.staticFileLocation("/public");

        IAnalyzer proxyTmp = null;
        if ("true".equalsIgnoreCase(System.getProperty("use_memcached"))) {
            proxyTmp = CachedAnalyzerProxy.bind(new Analyzer());
        } else {
            proxyTmp = new Analyzer();
        }

        final IAnalyzer proxy = proxyTmp;

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

        Spark.get("/get_perforce_data", (req, res) -> {
            return proxy.getP4Statistic(DateTime.now().minusMonths(1).toDate());
        });

    }

}
