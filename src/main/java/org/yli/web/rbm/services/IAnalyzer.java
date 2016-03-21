package org.yli.web.rbm.services;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by yli on 2/20/2016.
 */
public interface IAnalyzer {

  String getTop30ReviewRequestCountGroupByPeople() throws SQLException;

  String getRequestStatisticOfLastSixMonth() throws SQLException;

  String getRequestsGroupByProductSentInLastMonth() throws SQLException;

  String getNewAddedUsersPerMonthInLastYear() throws SQLException;

  String getTop30ReviewerInLastMonth() throws SQLException;

  String getP4Statistic(Date fromDate) throws SQLException;
}
