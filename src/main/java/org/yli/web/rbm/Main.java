package org.yli.web.rbm;

import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.yli.web.rbm.perforce.P4ClUtil;
import org.yli.web.rbm.proxy.CachedAnalyzerProxy;
import org.yli.web.rbm.services.Analyzer;
import org.yli.web.rbm.services.IAnalyzer;
import org.yli.web.rbm.services.UpdatePerforceRunnable;
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

        IAnalyzer proxyTmp = null;
        if ("true".equalsIgnoreCase(System.getProperty("use_memcached"))) {
            proxyTmp = CachedAnalyzerProxy.bind(new Analyzer());
        } else {
            proxyTmp = new Analyzer();
        }

        final IAnalyzer proxy = proxyTmp;

        final UpdatePerforceRunnable updateRunnable = new UpdatePerforceRunnable();
        Thread updateThread = new Thread(updateRunnable);

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

        Spark.get("/get_perforce_data/:year/:month/:day", (req, res) -> {
            DateTime now = DateTime.now();
            DateTime startDate = now.year().setCopy(req.params(":year"))
                .monthOfYear().setCopy(req.params(":month"))
                .dayOfMonth().setCopy(req.params(":day"));
            return proxy.getP4Statistic(startDate.toDate());
        });

        Spark.get("/get_max_cl_id", (req, res) -> {
            return proxy.getLatestClId();
        });

        Spark.get("/is_p4_data_updating", (req, res) -> {
            return updateRunnable.isUpdating();
        });

        Spark.get("/p4_updator_status", (req, res) -> {
            return updateRunnable.getUpdatorStatu();
        });

        Spark.get("/start_p4_updating", (req, res) -> {
            // interrupt it to make it wake up.
            updateThread.interrupt();
            return "started";
        });

        Spark.get("/get_review_request/:p4account/:year/:month/:day", (req, res) -> {
            DateTime now = DateTime.now();
            DateTime startDate = now.year().setCopy(req.params(":year"))
                .monthOfYear().setCopy(req.params(":month"))
                .dayOfMonth().setCopy(req.params(":day"));
            return proxy.getReviewRequest(req.params(":p4account"), startDate.toDate());
        });

        Spark.get("/get_changelist/:p4account/:year/:month/:day", (req, res) -> {
            DateTime now = DateTime.now();
            DateTime startDate = now.year().setCopy(req.params(":year"))
                .monthOfYear().setCopy(req.params(":month"))
                .dayOfMonth().setCopy(req.params(":day"));
            return proxy.getChangelist(req.params(":p4account"), startDate.toDate());
        });

        Spark.get("/get_rb_host", (req, res) -> {
            return System.getProperty("RB_HOST");
        });

        Spark.get("/get_p4web_host", (req, res) -> {
            return System.getProperty("P4WEB_HOST");
        });

        updateThread.start();
    }

}
