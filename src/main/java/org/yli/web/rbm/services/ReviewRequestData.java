package org.yli.web.rbm.services;

import java.util.Date;

/**
 * Created by yli on 6/17/2016.
 */
public class ReviewRequestData {

  private int reviewId;

  private String summary;

  private Date timeAdded;

  private String username;

  public ReviewRequestData(int reviewId, String summary, String username, Date timeAdded) {
    this.reviewId = reviewId;
    this.summary = summary;
    this.username = username;
    this.timeAdded = timeAdded;
  }

  public int getReviewId() {
    return reviewId;
  }

  public String getSummary() {
    return summary;
  }

  public Date getTimeAdded() {
    return timeAdded;
  }

  public String getUsername() {
    return username;
  }
}
