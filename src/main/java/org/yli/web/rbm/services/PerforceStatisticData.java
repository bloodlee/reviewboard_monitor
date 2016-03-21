package org.yli.web.rbm.services;

/**
 * Created by yli on 3/20/2016.
 */
public class PerforceStatisticData {
  private String firstName;
  private String lastName;
  private String p4Account;
  private int reviewedClCount;
  private int allClCount;
  private double ratio;

  public PerforceStatisticData(String firstName, String lastName, String p4Account, int reviewedClCount,
                               int allClCount, double ratio) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.p4Account = p4Account;
    this.reviewedClCount = reviewedClCount;
    this.allClCount = allClCount;
    this.ratio = ratio;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getP4Account() {
    return p4Account;
  }

  public int getReviewedClCount() {
    return reviewedClCount;
  }

  public int getAllClCount() {
    return allClCount;
  }

  public double getRatio() {
    return ratio;
  }
}
