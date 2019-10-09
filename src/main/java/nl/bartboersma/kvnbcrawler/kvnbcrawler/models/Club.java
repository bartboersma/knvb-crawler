package nl.bartboersma.kvnbcrawler.kvnbcrawler.models;

import lombok.Data;

import java.util.List;

@Data
public class Club {

  private int clubId;

  private String voetbalNlId;

  private String name;

  private String logo;

  private String lat;

  private String lon;

  private String voetbalNlName;

  private List<String> teams;
}
