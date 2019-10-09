package nl.bartboersma.kvnbcrawler.kvnbcrawler.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nl.bartboersma.kvnbcrawler.kvnbcrawler.models.Club;
import nl.bartboersma.kvnbcrawler.kvnbcrawler.models.KnvbSearchClubRequest;
import nl.bartboersma.kvnbcrawler.kvnbcrawler.utils.JsonToXWwwFormUrlencoded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class KnvbDataService {

  @Value("${knvb.vind-club.url}")
  private String knvbVindClubUrl;

  ObjectMapper mapper = new ObjectMapper();

  private RestTemplate restTemplate;

  @Autowired
  private VoetbalNlDataService voetbalNlDataService;

  public KnvbDataService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<Club> executeSearch(KnvbSearchClubRequest request) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Host", "www.knvb.nl");
    httpHeaders.add("Origin", "http://www.knvb.nl");
    httpHeaders.add("Referer", "https://www.knvb.nl/doe-mee/lid-worden/vind-club");
    httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");

    String requestAsString = JsonToXWwwFormUrlencoded.translateJsonToXWwwFormUrlencoded(request);

    HttpEntity<String> entity = new HttpEntity<>(requestAsString, httpHeaders);

    System.out.println(request);
    ResponseEntity<String> response = restTemplate.exchange(knvbVindClubUrl, HttpMethod.POST, entity, String.class);

    if (response != null && response.getBody() != null) {
      return voetbalNlDataService.executeSearch(extractData(response.getBody()));
    }
    return null;
  }

  private List<Club> extractData(String response) {
    CopyOnWriteArrayList<Club> clubsFromKnvb = new CopyOnWriteArrayList<>();

    String data = response.substring(response.indexOf("data-clubs=") + 12, response.indexOf("}]'") + 2);

    try {
      clubsFromKnvb = mapper.readValue(data, new TypeReference<CopyOnWriteArrayList<Club>>() {});

      log.info("Amount of clubs found: {}", clubsFromKnvb.size());

      for (Club club : clubsFromKnvb) {
        String logo = club.getLogo();

        if (club.getName().contains("DJSCR")) {
          club.setVoetbalNlId(logo.substring(logo.lastIndexOf("/") + 1, logo.indexOf(".jpg")));
        } else if (!club.getLogo().contains("default-logo")) {
          club.setVoetbalNlId(logo.substring(logo.lastIndexOf("/") + 1, logo.indexOf(".png")));
        } else {
          clubsFromKnvb.remove(club);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    log.info("Amount of clubs found with correct voetbalNlId: {}", clubsFromKnvb.size());

    return clubsFromKnvb;
  }
}
