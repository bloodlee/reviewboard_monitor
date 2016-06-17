package org.yli.web.rbm.services;

/**
 * Created by yli on 6/17/2016.
 */
public class PerforceClData {

  private String clId;

  private String username;

  private String description;

  public PerforceClData(String clId, String username, String description) {
    this.clId = clId;
    this.username = username;
    this.description = description;
  }

  public String getClId() {
    return clId;
  }

  public String getUsername() {
    return username;
  }

  public String getDescription() {
    return description;
  }
}
