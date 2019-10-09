package nl.bartboersma.kvnbcrawler.kvnbcrawler.models;

import lombok.Data;

@Data
public class KnvbSearchClubRequest {

  public Double longitude = 4.4777325999999675;
  public Double latitude = 51.9244201;
  public String cityname;
  public int radius = 25;

}
